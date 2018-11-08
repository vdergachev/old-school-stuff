package ru.glavkniga.gklients.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.crud.Gedit;
import ru.glavkniga.gklients.crud.Gget;
import ru.glavkniga.gklients.crud.GkIntegrationTypeHelper;
import ru.glavkniga.gklients.crud.Operation;
import ru.glavkniga.gklients.crudentity.SiteExchange;
import ru.glavkniga.gklients.crudentity.SiteExchangeEvent;
import ru.glavkniga.gklients.crudentity.SiteExchangeStatus;
import ru.glavkniga.gklients.service.GKExchangeProcessorService;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ru.glavkniga.gklients.crudentity.SiteExchangeStatus.IN_PROGRESS;
import static ru.glavkniga.gklients.crudentity.SiteExchangeStatus.PROCESSED;

@Service(GKExchangeProcessorService.NAME)
public class GKExchangeProcessorServiceBean implements GKExchangeProcessorService {

    private static final int BATCH_SIZE = 100;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    @Inject
    private GkIntegrationTypeHelper typeHelper;

    private Persistence persistence;
    private Metadata metadata;

    @Override
    public boolean process() {

        persistence = persistence == null ? AppBeans.get(Persistence.class) : persistence;
        metadata = metadata == null ? AppBeans.get(Metadata.class) : metadata;

        final Collection<Object> response = getActualExchangesFromSite();
        final List<String> receivedUuids = new ArrayList<>();

        // pre-process changes
        final Map<String, List<SiteExchange>> exchangesByTables = response.stream()
                .limit(BATCH_SIZE)
                .map(obj -> {
                    receivedUuids.add(((SiteExchange) obj).getUuid().toString());
                    return (SiteExchange) obj;
                })
                .collect(Collectors.groupingBy(SiteExchange::getTableName));

        setSiteExchangesStatusInProgress(receivedUuids);

        // process changes
        exchangesByTables.forEach(this::processTableChanges);

        setSiteExchangesStatusProcessed(receivedUuids);

        return true;
    }

    private void processTableChanges(final String tableName, final List<SiteExchange> changes) {

        final Map<UUID, List<SourceChange>> srcChanges = changes.stream()
                .map(SourceChange::create)
                .filter(SourceChange::isValid)
                .sorted(Comparator.comparing(SourceChange::getEvent)
                        .thenComparing(SourceChange::getLastUpdate))
                .collect(Collectors.groupingBy(SourceChange::getUuid));


        final Map<UUID, SourceChange> dstChanges = mergeChanges(srcChanges);

        if (dstChanges == null || dstChanges.isEmpty()) {
            return;
        }

        saveChangesToDB(dstChanges, tableName);
    }

    private void saveChangesToDB(final Map<UUID, SourceChange> changes, final String tableName) {
        changes.entrySet().forEach(e -> saveChangeToDB(e.getKey(), tableName, e.getValue()));
    }

    private void saveChangeToDB(UUID uuid, final String tableName, final SourceChange change) {

        final Class<? extends Entity> clazz = typeHelper.getEntityClass(tableName);

        try (final Transaction tx = persistence.createTransaction()) {
            final EntityManager em = persistence.getEntityManager();

            switch (change.getEvent()) {
                // TODO запретить для каждой сущности отправку на сайт !!!
                case CREATED:
                    Entity entityToPersist = em.find(clazz, uuid);
                    if (entityToPersist == null) {
                        entityToPersist = metadata.create(clazz);
                        mergeEntityWithChange(entityToPersist, change, tableName);
                        storeNewEntity(em, entityToPersist);
                    }else{
                        mergeEntityWithChange(entityToPersist, change, tableName);
                        updateExistingEntity(em, entityToPersist);
                    }
                    break;
                case UPDATED:
                    final Entity entityToUpdate = em.find(clazz, uuid);
                    if (entityToUpdate != null) {
                        mergeEntityWithChange(entityToUpdate, change, tableName);
                        updateExistingEntity(em, entityToUpdate);
                    }
                    break;
                case DELETED:
                    final Entity entityToDelete = em.find(clazz, uuid);
                    if (entityToDelete != null) {
                        em.remove(entityToDelete);
                    }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mergeEntityWithChange(final Entity entity, final SourceChange change, final String tableName) {

        for (final Map.Entry<String, String> entry : change.getData().entrySet()) {
            final String property = typeHelper.getProperty(tableName, entry.getKey());
            if (property == null) {
                continue;
            }

            System.out.println("entity = [" + entity.getClass().getSimpleName() + ", column [" + entry.getKey() + "], property = [" + property + "], value [" + entry.getValue() + "]");

            try {
                setEntityAttributeValue(entity, property, entry.getValue());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setEntityAttributeValue(final Entity entity, final String attribute, final String value) throws Exception {
        final Class attributeType = typeHelper.getEntityAttributeType(entity.getClass(), attribute);
        Object valueObj = null;
        if(attributeType.equals(String.class)){
            valueObj = value;
        }else if(attributeType.equals(Long.class)){
            valueObj = Long.parseLong(value);
        }else if(attributeType.equals(Integer.class)){
            valueObj = Integer.parseInt(value);
        }else if(attributeType.equals(Float.class)){
            valueObj = Float.parseFloat(value);
        }else if(attributeType.equals(Double.class)){
            valueObj = Double.parseDouble(value);
        }else if(attributeType.equals(Boolean.class)){
            valueObj = value.trim().equals("1") || value.trim().equalsIgnoreCase("true");
        }else if(attributeType.equals(Date.class)){
            valueObj = DATE_FORMAT.parse(value);
        }else if(Entity.class.isAssignableFrom(attributeType)){
            final EntityManager em = persistence.getEntityManager();
            valueObj = em.find((Class<? extends Entity>) attributeType, UUID.fromString(value));
        }
        entity.setValue(attribute, valueObj);
    }

    private void storeNewEntity(final EntityManager em, final Entity entity) {
        // TODO Т.к у нас попадаються не персистентные сущности нужно распихать изменения
        // TODO в них по нужным обьектам
        em.persist(entity);
    }

    private void updateExistingEntity(final EntityManager em, final Entity entity) {
        // TODO Т.к у нас попадаються не персистентные сущности нужно распихать изменения
        // TODO в них по нужным обьектам
        em.merge(entity);
    }

    private Map<UUID, SourceChange> mergeChanges(final Map<UUID, List<SourceChange>> srcChanges) {
        final Map<UUID, SourceChange> result = new HashMap<>();

        for (final Map.Entry<UUID, List<SourceChange>> entry : srcChanges.entrySet()) {
            final SourceChange mergedChange = mergeList(entry.getValue());
            if (mergedChange == null) {
                continue;
            }
            result.put(entry.getKey(), mergedChange);
        }
        return result;
    }

    private SourceChange mergeList(final List<SourceChange> srcChanges) {
        if (srcChanges.isEmpty()) {
            return null;
        }

        final SourceChange first = srcChanges.get(0);
        final SourceChange last = srcChanges.get(srcChanges.size() - 1);

        if (first == null || last == null) {
            return null;
        }

        boolean startWithCreate = first.event == SiteExchangeEvent.CREATED;
        boolean endsWithDelete = last.event == SiteExchangeEvent.DELETED;

        // если начинаеться с создания и заканчивается удалением нам нечего создавать, пусто
        if (startWithCreate && endsWithDelete) {
            return null;
        }

        // если не начинается с создания, но заканчивается удалением то удаляем
        if (!startWithCreate && endsWithDelete) {
            return last;
        }

        // в оставшихся случаях создается или обновляется, мержим
        if (srcChanges.size() == 1) {
            return first;
        }

        for (int i = 1; i < srcChanges.size(); i++) {
            first.mergeData(srcChanges.get(i));
        }

        return first;
    }

    // Update website table `gk_sys_site_exchange` with given UUIDs
    // list column `status` to IN_PROGRESS value
    protected void setSiteExchangesStatusInProgress(final List<String> receivedUuids) {
        updateExchangesWithStatus(receivedUuids, IN_PROGRESS);
    }

    // Update website table `gk_sys_site_exchange` with  with given UUIDs
    // list column `status` to PROCESSED value
    protected void setSiteExchangesStatusProcessed(List<String> receivedUuids) {
        updateExchangesWithStatus(receivedUuids, PROCESSED);
    }

    // Get all changes with `Gget` create website table `gk_sys_site_exchange`
    // with `status` = NEW or IN_PROGRESS with `last_update` > now() - 1 hour
    protected Collection<Object> getActualExchangesFromSite() {

        final String hourBefore = DATE_FORMAT.format(new Date(System.currentTimeMillis() - 3600 * 1000));

        final Gget getNewExchanges = new Gget(SiteExchange.class);
        getNewExchanges.addFilterField("status", "NEW", Operation.EQUAL);

        final Gget getExchangesInProgress = new Gget(SiteExchange.class);
        getExchangesInProgress.addFilterField("status", "IN_PROGRESS", Operation.EQUAL);
        getExchangesInProgress.addFilterField("last_update", hourBefore, Operation.MORE_EQUAL);

        Collection<Object> newExchanges = getNewExchanges.getObjects().values();
        Collection<Object> inProgressExchanges = getExchangesInProgress.getObjects().values();

        newExchanges.addAll(inProgressExchanges);

        return newExchanges;
    }

    private void updateExchangesWithStatus(final List<String> receivedUuids, final SiteExchangeStatus status) {

        receivedUuids.parallelStream().forEach(uuid -> {

            final SiteExchange siteExchange = new SiteExchange();
            siteExchange.setUuid(UUID.fromString(uuid));
            siteExchange.setStatus(status);

            final Gedit inProgressEdit = new Gedit();
            inProgressEdit.addFilterField("uuid", uuid);
            inProgressEdit.editObject(siteExchange);
        });
    }

    private static class SourceChange {
        private UUID uuid;
        private SiteExchangeEvent event;
        private Map<String, String> data;
        private Date lastUpdate;

        static SourceChange create(final SiteExchange siteExchange) {
            final SourceChange change = new SourceChange();
            change.event = siteExchange.getEvent();
            change.lastUpdate = siteExchange.getLastUpdate();
            change.data = getAsMap(siteExchange.getData());
            if (change.data != null) {
                if (change.data.containsKey("id")) {
                    change.uuid = UUID.fromString(change.data.get("id"));
                } else if (change.data.containsKey("user_id")) {
                    change.uuid = UUID.fromString(change.data.get("user_id"));
                }
            }
            return change;
        }

        boolean isValid() {
            return uuid != null && event != null && data != null && !data.isEmpty() && lastUpdate != null;
        }

        public SiteExchangeEvent getEvent() {
            return event;
        }

        public UUID getUuid() {
            return uuid;
        }

        public void mergeData(SourceChange change) {
            if (change == null || change.getData() == null || change.getData().isEmpty())
                return;
            this.data.putAll(change.getData());
        }

        public Map<String, String> getData() {
            return data;
        }

        public Date getLastUpdate() {
            return lastUpdate;
        }
    }

    private static Map<String, String> getAsMap(final String values) {
        try {
            return OBJECT_MAPPER.readValue(values, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
