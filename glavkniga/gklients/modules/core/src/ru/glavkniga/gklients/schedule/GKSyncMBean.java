/*
 * Copyright (c) 2017 gklients
 */

package ru.glavkniga.gklients.schedule;

import com.haulmont.cuba.core.*;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.crud.*;
import ru.glavkniga.gklients.crudentity.Regkey;
import ru.glavkniga.gklients.crudentity.SituationAccess;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientService;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.Tarif;
import ru.glavkniga.gklients.service.DateTimeService;
import ru.glavkniga.gklients.service.EmailerService;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.*;

/**
 * Created by LysovIA on 28.07.2016.
 */

@Component(GKSync.NAME)
public class GKSyncMBean implements GKSync {

    private Map<String, UUID> emails = new HashMap<>();
    private Map<String, UUID> inactiveRegkeys = new HashMap<>();
    private Map<UUID, Client> clients = new HashMap<>();


    @Inject
    private DateTimeService dts;

    @Inject
    private Persistence persistence;

    private Logger log = LoggerFactory.getLogger(getClass());

    // @Inject
    //  private GKExchangeService exchangeService;


     /*
      TODO: addObject public void setId(String id){this.id = UuidProvider.fromString(id); to each synchronisable entity
      TODO: remove obsolette Enum-based integration
      */


    @Override
    public String sync() {
        String result = "";
        String s = "";

        Gget gRegkeyGet = new Gget(Regkey.class);
        gRegkeyGet.addFilterProperty("date_activation", "NULL", Operation.IS_NOT);
        List<String> activatedWebsiteRegkeys = gRegkeyGet.getFieldList("regkey");

        if (activatedWebsiteRegkeys != null && activatedWebsiteRegkeys.size() > 0) {
            this.getRegkeysFromDB();
            for (String regkey : activatedWebsiteRegkeys) {
                if (this.inactiveRegkeys.containsKey(regkey)) {
                    UUID id = this.inactiveRegkeys.get(regkey);
                    activateSubscription(id);
                }
            }
        }

        ArrayList<UUID> usersToDownload = new ArrayList<>();
        Gget gget = new Gget(Client.class);
        List<UUID> websiteUserIds = gget.getIdList("id");
        this.getUsersFromDB();

        if (websiteUserIds != null && !websiteUserIds.isEmpty()) {

            websiteUserIds.forEach(websiteUserID -> {
                //  log.warn(websiteUserID.toString());
                if (!(this.clients.containsKey(websiteUserID))) {
                    usersToDownload.add(websiteUserID);
                }
            });
        }

        if (usersToDownload.size() > 0) {
            s = "Found " + usersToDownload.size() + " different user(s).";
            result += s + " ";
            this.downloadUsers(usersToDownload);
            s += " Downloaded";
            log.warn(s);
        }


        ArrayList<UUID> usersToUpload = new ArrayList<>(this.clients.keySet());
        if (websiteUserIds != null && !websiteUserIds.isEmpty()) {
            this.getUsersFromDB(); //refresh user context
            usersToUpload.removeAll(websiteUserIds);
        }

        if (!usersToUpload.isEmpty()) {
            s = "Found " + usersToUpload.size() + " different user(s).";
            result += s + " ";
            this.uploadUsers(usersToUpload);
            s += " Uploaded";
            log.warn(s);
        }

        if (result.length() == 0) {
            result = "Data is sync";
        }
        return result;
    }

    private void downloadUsers(List<UUID> usersToDownload) {
        int i = 0;
        for (UUID userId : usersToDownload) {
            Gget clientGetter = new Gget(Client.class);
            clientGetter.addFilterProperty("id", String.valueOf(userId));
            Map<String, Object> userData = clientGetter.getObjects();

            Gget serviceGetter = new Gget(ClientService.class);
            serviceGetter.addFilterProperty("client", String.valueOf(userId));
            Map<String, Object> userServices = serviceGetter.getObjects();

            Client client = (Client) userData.get(userId.toString());
            if (this.emails.containsKey(client.getEmail())) { //если у нас такой email уже есть в базе, хотя его айдишник не найден
                // надо на сайте заменить клиенту айдишник на тот, что в базе.
                log.warn("User with email " + client.getEmail() + " already exist with different UUID");
                //exchangeService.getUserServices(userId);
                UUID uuid = this.emails.get(client.getEmail());
                client = this.clients.get(uuid);
                Gedit clientEditor = new Gedit();
                clientEditor.addFilterField("id", String.valueOf(userId));
                clientEditor.editObject(client);
                //А в базе - разобраться с его сервисами.
                //TODO Тут надо дописать вызовы всего что связано с сервисами.
            } else {

                //TODO здесь надо очистить персональные данные клиента
                HashMap<String, String> filterForDeletion = new HashMap<String, String>();
                filterForDeletion.put("id", String.valueOf(client.getId()));
                Gdelete.deleteRecord("gk_sys_site_user_data", filterForDeletion);
                EntityWorker.persist(client);
            }
            for (Object service : userServices.values()) {
                ClientService cs = (ClientService) service;
                cs.setClient(client);
                //cs.setActivationDate(timeSource.currentTimestamp());
                if (cs.getService().equals("3")) {
                    try {
                        Gget userServiceGetter = new Gget(SituationAccess.class);
                        userServiceGetter.addFilterProperty("client", String.valueOf(userId));
                        Map<String, Object> userServicesData = userServiceGetter.getObjects();
                        SituationAccess situationAccess = (SituationAccess) userServicesData.entrySet().iterator().next().getValue();
                        cs.setActivationDate(situationAccess.getBeginDate());
                        // TODO PUT SOMEWHERE situationAccess.getEndDate();
                    } catch (Exception e) {
                        log.warn("Something wrong with ClientService");
                        e.printStackTrace();
                    }
                    EntityWorker.persist(cs);
                }

            }
        }
    }


    private void uploadUsers(List<UUID> usersToUpload) {
        AccessGranter.toggleSync(false);
        for (UUID userId : usersToUpload) {
            Client client = this.clients.get(userId);
            //prevent from uploading personal data to website
            client.setName(null);
            client.setPassword(null);
            client.setPhone(null);
            //addObject new user
            //exchangeService.setUser(client);
            Gadd clientAdder = new Gadd();
            clientAdder.addObject(client);

            //addObject all services of the user
            List<ClientService> csList = new ArrayList<>();
            TypedQuery<ClientService> query;
            csList = getClientServices(client, csList);

            if (csList != null && csList.size() > 0) {
                csList.forEach((clientService) -> {
                    AccessGranter.addClientService(client, clientService.getService(), clientService.getActivationDate());
                    switch (clientService.getService()) {
                        case "1":
                            List<ElverSubscription> esList = AccessGranter.getElverSubscriptions(client);
                            if (esList != null && esList.size() > 0) {
                                esList.forEach((elverSubscription) -> {
                                    AccessGranter.grantAll(client, elverSubscription);

                                    Metadata metadata = AppBeans.get(Metadata.NAME);
                                    Tarif tarif = (Tarif) EntityWorker.getEntity(Tarif.class, elverSubscription.getTarif().getId(), "_minimal");

                                    Regkey regkey = metadata.create(Regkey.class);
                                    regkey.setEmail(client.getEmail());
                                    regkey.setRegkey(elverSubscription.getRegkey());
                                    if (tarif != null) {
                                        regkey.setTarif(String.valueOf(tarif.getTarifNumber()));
                                    }
                                    if (elverSubscription.getActivation_date() != null)
                                        regkey.setDate_activation(elverSubscription.getActivation_date());
                                    Gadd gadd = new Gadd();
                                    gadd.setDuplicateIgnoreMode(false);
                                    gadd.addObject(regkey);
                                });
                            }
                            break;
                        case "2":
                            // if we add access to Seminars separately
                            break;
                        case "3":
                            // If we force to register users on website then write here
                            break;
                    }
                });
            }
        }
        AccessGranter.toggleSync(true);
    }

    private List<ClientService> getClientServices(Client client, List<ClientService> csList) {
        TypedQuery<ClientService> query;Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("" +
                            "SELECT t FROM gklients$ClientService t " +
                            "WHERE t.deleteTs is null " +
                            "AND t.client.id=?1"
                    , ClientService.class);
            query.setView(ClientService.class, "_minimal");
            query.setParameter(1, client);
            query.setCacheable(false);
            csList = query.getResultList();
            tx.commit();
        } catch (NoResultException e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
        return csList;
    }

    private void getUsersFromDB() {
        this.clients.clear();
        this.emails.clear();
        TypedQuery<Client> query;
        List<Client> localClients = new LinkedList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("" +
                    "SELECT t FROM gklients$Client t " +
                    "WHERE t.deleteTs is null", Client.class);
            query.setView(Client.class, "_minimal");
            query.setCacheable(false);
            localClients = query.getResultList();
            tx.commit();
        } catch (NoResultException e) {
            log.warn("No Clients in database");
        } finally {
            tx.end();
        }
        for (Client client : localClients) {
            this.clients.put(client.getId(), client);
            this.emails.put(client.getEmail(), client.getId());
        }
    }

    private void getRegkeysFromDB() {
        this.inactiveRegkeys.clear();
        TypedQuery<ElverSubscription> query;
        List<ElverSubscription> subscriptions = new LinkedList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("" +
                            "SELECT es FROM gklients$ElverSubscription es " +
                            "WHERE es.deleteTs is null " +
                            "AND es.activation_date IS null"
                    , ElverSubscription.class);
            query.setView(ElverSubscription.class, "_minimal");
            query.setCacheable(false);
            subscriptions = query.getResultList();
            tx.commit();
        } catch (NoResultException e) {
            log.warn("No ElverSubscriptions in database");
        } finally {
            tx.end();
        }
        for (ElverSubscription subscription : subscriptions) {
            this.inactiveRegkeys.put(subscription.getRegkey(), subscription.getId());
        }
    }


    private void activateSubscription(UUID subscriptionId) {
        Persistence persistence = AppBeans.get(Persistence.class);
        EmailerService emailerService = AppBeans.get(EmailerService.class);

        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery("UPDATE gklients_elver_subscription " +
                    "SET " +
                    "activation_date = #date, " +
                    "is_pass_sent_to_customer = #yes1, " +
                    "is_reg_key_used = #yes2 " +
                    "WHERE id = #id");
            query.setParameter("date", dts.getCurrentTimestamp());
            query.setParameter("yes1", true);
            query.setParameter("yes2", true);
            query.setParameter("id", subscriptionId);
            query.executeUpdate();
            tx.commit();
        } catch (NoResultException e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
        emailerService.activateRegkey(subscriptionId);
    }

    /*
      int countTest = this.countTest();
      int countTestWebsite = exchangeService.countTest();
      if (countTestWebsite != countTest) {
          s = "Test count different. " + countTest + " in database, " + countTestWebsite + " on website";
          result += s + " ";
          log.warn(s);
      }

      int countRegkey = this.countRegkey();
      int countRegkeyWebsite = exchangeService.countRegkeys();
      if (countRegkeyWebsite != countRegkey) {
          s += "Regkey count different. " + countRegkey + " in database, " + countRegkeyWebsite + " on website";
          result += s + " ";
          log.warn(s);
      }
   */
    /*
    private int countRegkey() {
        TypedQuery<ElverSubscription> query;
        List<ElverSubscription> test = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("" +
                            "SELECT es FROM gklients$ElverSubscription es " +
                            "WHERE es.deleteTs is null",
                    ElverSubscription.class);
            query.setView(ElverSubscription.class, "_minimal");
            test = query.getResultList();
            tx.commit();
        } catch (NoResultException e) {
            return 0;
        } finally {
            tx.end();
        }
        return test.size();
    }


    private int countTest() {
        TypedQuery<Test> query;
        List<Test> test = new LinkedList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("" +
                    "SELECT t FROM gklients$Test t " +
                    "WHERE t.deleteTs is null", Test.class);
            query.setView(Test.class, "_minimal");
            test = query.getResultList();
            tx.commit();
        } catch (NoResultException e) {
            return 0;
        } finally {
            tx.end();
        }
        return test.size();
    }*/


}
