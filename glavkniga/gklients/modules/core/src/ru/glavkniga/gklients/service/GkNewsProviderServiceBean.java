package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.Metadata;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.crudentity.GKNews;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientDistributionSettings;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static java.lang.Runtime.getRuntime;

@Service(GkNewsProviderService.NAME)
public class GkNewsProviderServiceBean implements GkNewsProviderService {

    private final static int NUMBER_TO_READ = 150;

    @Inject
    Persistence persistence;

    @Inject
    private Metadata metadata;

    @Override
    public List<GkNewsSet> provide(List<GKNews> newNews) {

        if (newNews == null || newNews.isEmpty()) {
            throw new IllegalArgumentException("No news to provide");
        }

        final long start = System.currentTimeMillis();

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        final Map<GKNews, List<Client>> newsForClients = new HashMap<>();
        final List uniqueSettings = new ArrayList<>();
        final Object lock = new Object();

        final Boolean[] invokeResult = new Boolean[]{false};

        try (final Transaction tx = persistence.createTransaction()) {

            final EntityManager em = persistence.getEntityManager();

            final Query countActiveClients = em.createQuery(
                    "SELECT count(s) FROM gklients$ClientDistributionSettings s WHERE " +
                            " ( s.client.badEmail IS NULL OR s.client.badEmail = false) " +
                            " AND s.deleteTs IS NULL",
                    Client.class
            );

            final Query uniqueSettingsQuery = em.createNativeQuery(
                    " SELECT s.is_company, s.is_ip, s.is_civil, s.is_with_workers, s.is_without_workers, s.is_osno, s.is_usn, s.is_envd, s.is_eshn, s.is_psn " +
                            " FROM gklients_client_distribution_settings s" +
                            " LEFT JOIN gklients_client c ON s.client_id = c.id " +
                            " WHERE s.delete_ts IS NULL AND c.delete_ts IS NULL AND (c.bad_email IS NULL OR c.bad_email = false) " +
                            " GROUP BY s.is_company, s.is_ip, s.is_civil, s.is_with_workers, s.is_without_workers, s.is_osno, s.is_usn, s.is_envd, s.is_eshn, s.is_psn"
            );

            final Query activeClients = em.createQuery(
                    "SELECT s.client FROM gklients$ClientDistributionSettings s WHERE " +
                            " ( s.client.badEmail IS NULL OR s.client.badEmail = false) " +
                            " AND s.deleteTs IS NULL ",
                    Client.class
            );

            // Mark it as read only
            countActiveClients.getDelegate().setHint(QueryHints.READ_ONLY, HintValues.TRUE);
            activeClients.getDelegate().setHint(QueryHints.READ_ONLY, HintValues.TRUE);

            final int numberActiveClient = ((Long) countActiveClients.getSingleResult()).intValue();

            if (numberActiveClient == 0) {
                return null;
            }

            // TODO Проверить CursoredStream - требует ли он предыварительного вычитывания размера колллекции ?
            // TODO https://ufasoli.blogspot.ru/2012/11/jpa-eclipselink-optimizing-query.html

            activeClients.getDelegate().setHint("eclipselink.cursor.scrollable", true);
            final ScrollableCursor cursor = (ScrollableCursor) activeClients.getSingleResult();

            // Обязательно нужно указапулу оптимальное число максимально возможных потоков
            final ForkJoinPool pool = new ForkJoinPool(getRuntime().availableProcessors());
            final int cursorReadNumber = (numberActiveClient % NUMBER_TO_READ == 0)
                    ? numberActiveClient / NUMBER_TO_READ
                    : numberActiveClient / NUMBER_TO_READ + 1;
            final CountDownLatch latch = new CountDownLatch(cursorReadNumber + 1);

            int read = 0;

            // Определяем уникальные настройки для всех клиентов
            pool.submit(() -> {
                uniqueSettings.addAll(uniqueSettingsQuery.getResultList());
                latch.countDown();
            });

            while (true) {

                final int numberToRead = (numberActiveClient - read < NUMBER_TO_READ)
                        ? numberActiveClient - read
                        : NUMBER_TO_READ;
                final List<Client> clients = (List<Client>) ((List) cursor.next(numberToRead));
                read += numberToRead;

                // В фоне для кадой порции клиентов и их настроек находим подходящие новости
                pool.submit(() -> {
                    for (final GKNews news : newNews) {
                        final List<Client> matchedClients = new ArrayList<>();
                        for (final Client client : clients) {
                            if (isNewsMatchesClientSettings(news, client.getClientDistributionSettings())) {
                                matchedClients.add(client);
                            }
                        }

                        if (!matchedClients.isEmpty()) {
                            synchronized (lock) {
                                if (newsForClients.containsKey(news)) {
                                    newsForClients.get(news).addAll(matchedClients);
                                } else {
                                    newsForClients.put(news, matchedClients);
                                }
                            }
                        }
                    }
                    latch.countDown();
                });

                if (read == numberActiveClient) {
                    break;
                }
            }

            // Сопостовление новостей и настроек пользователй должно происходить в момент существования транзакции
            // до ее комита, поэтому мы здесь ждем завершения выполнения всех калькуляций
            try {
                invokeResult[0] = latch.await(1, TimeUnit.MINUTES);
            } catch (Exception ignored) {
                invokeResult[0] = false;
            }

        }

        final long stop = System.currentTimeMillis();
        System.out.println("@@@ sql part takes: " + (stop - start) / 1000.0 + " s");
        final long calcStart = System.currentTimeMillis();

        if (!invokeResult[0]) {
            return null;
        }

        final Map<ClientSettings, Set<Client>> clientsGroupedBySettings = new HashMap<>();
        for(final Object setingObj : uniqueSettings){
            final ClientSettings clientSettings = new ClientSettings((Object[])setingObj);
            final Set<Client> clientGroup = new HashSet<>();

            for(final GKNews aNews : newsForClients.keySet()){
                for(final Client aClient : newsForClients.get(aNews)){
                    if(clientSettings.isClientHaveSameSettings(aClient.getClientDistributionSettings())){
                        clientGroup.add(aClient);
                    }
                }
            }
            clientsGroupedBySettings.put(clientSettings, clientGroup);
        }

        final List<GkNewsSet> result = new ArrayList<>();
        for(final ClientSettings clientSettings : clientsGroupedBySettings.keySet()){
            final List<GkNewsSet> tempGroup = new ArrayList<>();
            final Map<Client, List<GKNews>> clientNews = new HashMap<>();

            final Set<GKNews> newsForClient = new HashSet<>();
            // Map(Client -> List<GKNews>)
            for(final Client client : clientsGroupedBySettings.get(clientSettings)) {

                for(final GKNews gkNews : newsForClients.keySet()){
                    if(newsForClients.get(gkNews).contains(client)) {
                        newsForClient.add(gkNews);
                    }
                }

                if(newsForClient.isEmpty()) {
                    continue;
                }

                clientNews.put(client, new ArrayList<>(newsForClient));
            }
            // build result Map<Settings -> GkNewsSet>    new GkNewsSet(Set<GKNews> news, List<Client> clients)
            for(final Client client : clientsGroupedBySettings.get(clientSettings)) {
                final List<GKNews> groupClientNews = clientNews.get(client);
                boolean isFound = false;
                for(final GkNewsSet newsSet : tempGroup){
                    if(newsSet.getNews().equals(groupClientNews)) {
                        newsSet.addClient(client);
                        isFound = true;
                        break;
                    }
                }
                if(!isFound) {
                    tempGroup.add(new GkNewsSet(groupClientNews, client));
                }
            }
            result.addAll(tempGroup);
        }

        final long calcStop = System.currentTimeMillis();
        System.out.println("@@@ calc part takes: " + (calcStop - calcStart) / 1000.0 + " s");

        return result;
    }

    private boolean isNewsMatchesClientSettings(final GKNews news, final ClientDistributionSettings settings) {
    /*
    * Есть настройки новостей и настройки подписчика - по 10 флагов и там и тут.
    * Флаги абсолютно идентичны по смыслу и наименованию.
    * Флаги разбиваются на 3 группы:
    * * Налоги - usno osno eshn envd psn
    * * Виды организации company ip civil
    * * наличие работников withWorkers withoutWorkers
    * По умолчанию считается, что и для новости, и для подписчика:
    * * Флаги налогов могут быть выставлены в любой комбинации
    * * Флаги организации и работников комбинируются следующим образом
    * * * Если выставлены только company или только civil, флаги withWorkers withoutWorkers не выставляются (предметная область подразумевает,
    * что у компаний всегда есть работники, а у физлиц работников всегда нет)
    * * * Если выставлен ip - можно выставить withWorkers withoutWorkers или не выставлять их
    *
    * Требования на соответствие новости и настройкам подписчика следующие
    * Если есть совпадения в первой группе и нет противоречий во второй и третьей, новость включается в рассылку.
    * Ещё новости, помеченные для физлиц, включаются в рассылку независимо от всего остального.
    * Детально:
    * * Если в новости выставлен флаг civil - включаем и не проверяем дальше.
    * * Хотя бы один одинаковый налог должен быть выставлен всегда. Иначе не включаем
    * * Если совпала company (с обеих сторон выставлена) - включаем.
    * * Если совпала по ИП - проверяем работников. Чтобы новость попала в рассылку, либо хотя бы один флаг должен совпасть, либо с одной из сторон флаги не выставлены.
    * */

        // *** Below works, try to simplify ***
//        // Validate if taxes matches
//        if ((news.getTaxesAsByte() & settings.getTaxesAsByte()) != 0) {
//            //validate if organizations matches
//            if ((news.getOrgsAsByte() & settings.getOrgsAsByte()) != 0) {
//                //validate if client set IP on and we need to check workers
//                if (settings.getIsCompany() != null && !settings.getIsCompany()) {
//                    //validate if client set only one workers flag and we need to check it
//                    if (settings.getWorkersAsByte() != 0
//                            && settings.getWorkersAsByte() != 3
//                            && news.getWorkersAsByte() != 0
//                            && news.getWorkersAsByte() != 3) {
//                        //validate if workers matches
//                        if ((settings.getWorkersAsByte() & news.getWorkersAsByte()) != 0) {
//                            return true;
//                        } else return false;
//                    } else return true;
//                } else return true;
//            } else return false;
//        } else return false;
//    }

        //optimized by IDE, don't try to understand it!
        return news.getIsCivil() != null && news.getIsCivil()  //  news marked as for civils are going to anyone
                || (news.getTaxesAsByte() & settings.getTaxesAsByte()) != 0
                && (news.getIsCompany() != null
                && settings.getIsCompany() != null
                && settings.getIsCompany()
                && news.getIsCompany() || news.getIsIp() != null
                && settings.getIsIp() != null
                && settings.getIsIp()
                && news.getIsIp()
                && (news.getWorkersAsByte() == 0 || settings.getWorkersAsByte() == 0
                || (news.getWorkersAsByte() & settings.getWorkersAsByte()) != 0));
    }

    private static class ClientSettings {

        protected Boolean isCompany;
        protected Boolean isIp;
        protected Boolean isCivil;
        protected Boolean isWithWorkers;
        protected Boolean isWithoutWorkers;
        protected Boolean isOsno;
        protected Boolean isUsn;
        protected Boolean isEnvd;
        protected Boolean isEshn;
        protected Boolean isPsn;

        private ClientSettings(final Object[] values) {
            this.isCompany = (Boolean) values[0];
            this.isIp = (Boolean) values[1];
            this.isCivil = (Boolean) values[2];
            this.isWithWorkers = (Boolean) values[3];
            this.isWithoutWorkers = (Boolean) values[4];
            this.isOsno = (Boolean) values[5];
            this.isUsn = (Boolean) values[6];
            this.isEnvd = (Boolean) values[7];
            this.isEshn = (Boolean) values[8];
            this.isPsn = (Boolean) values[9];
        }

        public boolean isClientHaveSameSettings(final ClientDistributionSettings settings) {

            if (isCompany != null ? !isCompany.equals(settings.getIsCompany()) : settings.getIsCompany() != null) {
                return false;
            }

            if (isIp != null ? !isIp.equals(settings.getIsIp()) : settings.getIsIp() != null) {
                return false;
            }

            if (isCivil != null ? !isCivil.equals(settings.getIsCivil()) : settings.getIsCivil() != null) {
                return false;
            }

            if (isWithWorkers != null ? !isWithWorkers.equals(settings.getIsWithWorkers()) :
                    settings.getIsWithWorkers() != null) {
                return false;
            }

            if (isWithoutWorkers != null ? !isWithoutWorkers.equals(settings.getIsWithoutWorkers()) :
                    settings.getIsWithoutWorkers() != null) {
                return false;
            }
            if (isOsno != null ? !isOsno.equals(settings.getIsOsno()) : settings.getIsOsno() != null) {
                return false;
            }
            if (isUsn != null ? !isUsn.equals(settings.getIsUsn()) : settings.getIsUsn() != null) {
                return false;
            }
            if (isEnvd != null ? !isEnvd.equals(settings.getIsEnvd()) : settings.getIsEnvd() != null) {
                return false;
            }

            if (isEshn != null ? !isEshn.equals(settings.getIsEshn()) : settings.getIsEshn() != null) {
                return false;
            }

            return isPsn != null ? isPsn.equals(settings.getIsPsn()) : settings.getIsPsn() == null;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final ClientSettings that = (ClientSettings) o;

            if (isCompany != null ? !isCompany.equals(that.isCompany) : that.isCompany != null) return false;
            if (isIp != null ? !isIp.equals(that.isIp) : that.isIp != null) return false;
            if (isCivil != null ? !isCivil.equals(that.isCivil) : that.isCivil != null) return false;
            if (isWithWorkers != null ? !isWithWorkers.equals(that.isWithWorkers) : that.isWithWorkers != null)
                return false;
            if (isWithoutWorkers != null ? !isWithoutWorkers.equals(that.isWithoutWorkers) :
                    that.isWithoutWorkers != null)
                return false;
            if (isOsno != null ? !isOsno.equals(that.isOsno) : that.isOsno != null) return false;
            if (isUsn != null ? !isUsn.equals(that.isUsn) : that.isUsn != null) return false;
            if (isEnvd != null ? !isEnvd.equals(that.isEnvd) : that.isEnvd != null) return false;
            if (isEshn != null ? !isEshn.equals(that.isEshn) : that.isEshn != null) return false;
            return isPsn != null ? isPsn.equals(that.isPsn) : that.isPsn == null;
        }

        @Override
        public int hashCode() {
            int result = isCompany != null ? isCompany.hashCode() : 0;
            result = 31 * result + (isIp != null ? isIp.hashCode() : 0);
            result = 31 * result + (isCivil != null ? isCivil.hashCode() : 0);
            result = 31 * result + (isWithWorkers != null ? isWithWorkers.hashCode() : 0);
            result = 31 * result + (isWithoutWorkers != null ? isWithoutWorkers.hashCode() : 0);
            result = 31 * result + (isOsno != null ? isOsno.hashCode() : 0);
            result = 31 * result + (isUsn != null ? isUsn.hashCode() : 0);
            result = 31 * result + (isEnvd != null ? isEnvd.hashCode() : 0);
            result = 31 * result + (isEshn != null ? isEshn.hashCode() : 0);
            result = 31 * result + (isPsn != null ? isPsn.hashCode() : 0);
            return result;
        }
    }
}
