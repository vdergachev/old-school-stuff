package ru.glavkniga.gklients.mailing;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import ru.glavkniga.gklients.BaseIT;
import ru.glavkniga.gklients.crudentity.GKNews;
import ru.glavkniga.gklients.crudentity.GKNewsImage;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientDistributionSettings;
import ru.glavkniga.gklients.rule.SiteRule;
import ru.glavkniga.gklients.service.GkNewsProviderService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class NewsIT extends BaseIT {

    @ClassRule
    public static SiteRule server = new SiteRule("localhost", 38888);

    private final static boolean[] BOOLS = {true, false};
    private Metadata metadata;

    @Before
    public void setup() {
        metadata = AppBeans.get(Metadata.NAME);

//        cleanUpDB();

//        createDupPosibleClients(1);
//        createDupPosibleClients(10);
//        createDupPosibleClients(20);

//        createAllPosibleClients(30000);
//        createAllPosibleClients(40000);
//        createAllPosibleClients(50000);
//
//        createAllPosibleNews();
    }

    @Test
    public void newsProvided() {

        // given
        final GkNewsProviderService service = AppBeans.get(GkNewsProviderService.class);

        final List<GKNews> newNews = new ArrayList<>();
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        try (final Transaction tx = container.persistence().createTransaction()) {

            final EntityManager em = container.persistence().getEntityManager();

            final Query newsQuery = em.createQuery(
                    "SELECT n FROM gklients$GKNews n WHERE n.newsDate > :date_from AND n.deleteTs IS null",
                    GKNews.class
            );
            newsQuery.setParameter("date_from", calendar.getTime());
            newsQuery.setView(GKNews.class, "gKNews-full-view");
            newsQuery.setMaxResults(20);

            final List<GKNews> news = newsQuery.getResultList();

            if (news == null) {
                return;
            }

            newNews.addAll(news);
        }

        assertThat(newNews.size(), greaterThan(0));

        // when
        final List<GkNewsProviderService.GkNewsSet> result = service.provide(newNews);

        assertThat(result, notNullValue());

    }

    private void createAllPosibleNews() {
        int i = 1;

        for (boolean isCompany : BOOLS) {
            for (boolean isIp : BOOLS)
                for (boolean isCivil : new boolean[]{false})
                    for (boolean isWithWorkers : BOOLS)
                        for (boolean isWithoutWorkers : BOOLS)
                            for (boolean isOsno : BOOLS)
                                for (boolean isUsn : BOOLS)
                                    for (boolean isEnvd : BOOLS)
                                        for (boolean isEshn : BOOLS)
                                            for (boolean isPsn : BOOLS) {

                                                GKNews news = metadata.create(GKNews.class);

                                                news.setSiteId(i);
                                                news.setTitle("title " + i);
                                                news.setUrl("http://gk." + i + ".ru");
                                                news.setLeadText("lead text" + i);
                                                news.setFullText("full text" + i++);
                                                news.setNewsDate(new Date());

                                                news.setIsCompany(isCompany);
                                                news.setIsIp(isIp);
                                                news.setIsCivil(isCivil);
                                                news.setIsWithWorkers(isWithWorkers);
                                                news.setIsWithoutWorkers(isWithoutWorkers);
                                                news.setIsOsno(isOsno);
                                                news.setIsUsn(isUsn);
                                                news.setIsEnvd(isEnvd);
                                                news.setIsEshn(isEshn);
                                                news.setIsPsn(isPsn);

                                                final GKNewsImage image = metadata.create(GKNewsImage.class);
                                                image.setImage("http://gk.image.jpeg/" + i);
                                                image.setGkNews(news);

                                                news.setImages(asList(image));

                                                container.persistence().createTransaction().execute(em -> {
                                                    em.persist(image);
                                                    em.persist(news);
                                                });
                                            }
        }
    }

    private Client createClient(int i) {
        final Client client = metadata.create(Client.class);
        client.setItn("client-itn" + i);
        client.setName("client-name" + i);
        client.setPassword("strong-password-123" + i);
        client.setPhone("+7123456789" + i);
        client.setEmail("some@say.com" + i);
        client.setBadEmail(i % 10 == 0 ? true : null);
        return client;
    }

    private void createDupPosibleClients(int start) {
        int i = start;
        for (boolean isCompany : BOOLS) {

            final Client client = createClient(++i);

            final ClientDistributionSettings settings = metadata.create(ClientDistributionSettings.class);
            settings.setClient(client);
            settings.setIsCompany(isCompany);
            settings.setIsIp(true);
            settings.setIsCivil(true);
            settings.setIsWithWorkers(true);
            settings.setIsWithoutWorkers(true);
            settings.setIsOsno(true);
            settings.setIsUsn(true);
            settings.setIsEnvd(true);
            settings.setIsEshn(true);
            settings.setIsPsn(true);

            client.setClientDistributionSettings(settings);

            container.persistence().createTransaction().execute(em -> {
                em.persist(client);
                em.persist(settings);
            });
        }

    }

    private void createAllPosibleClients(int start) {
        int i = start;
        for (boolean isCompany : BOOLS) {
            for (boolean isIp : BOOLS)
                for (boolean isCivil : BOOLS)
                    for (boolean isWithWorkers : BOOLS)
                        for (boolean isWithoutWorkers : BOOLS)
                            for (boolean isOsno : BOOLS)
                                for (boolean isUsn : BOOLS)
                                    for (boolean isEnvd : BOOLS)
                                        for (boolean isEshn : BOOLS)
                                            for (boolean isPsn : BOOLS) {
                                                final Client client = createClient(++i);

                                                final ClientDistributionSettings settings =
                                                        metadata.create(ClientDistributionSettings.class);
                                                settings.setClient(client);
                                                settings.setIsCompany(isCompany);
                                                settings.setIsIp(isIp);
                                                settings.setIsCivil(isCivil);
                                                settings.setIsWithWorkers(isWithWorkers);
                                                settings.setIsWithoutWorkers(isWithoutWorkers);
                                                settings.setIsOsno(isOsno);
                                                settings.setIsUsn(isUsn);
                                                settings.setIsEnvd(isEnvd);
                                                settings.setIsEshn(isEshn);
                                                settings.setIsPsn(isPsn);

                                                client.setClientDistributionSettings(settings);

                                                container.persistence().createTransaction().execute(em -> {
                                                    em.persist(client);
                                                    em.persist(settings);
                                                });
                                            }
        }
    }

}
