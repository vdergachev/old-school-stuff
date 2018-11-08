package ru.glavkniga.gklients.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.glavkniga.gklients.integration.dao.ExchangeRepository;
import ru.glavkniga.gklients.integration.entity.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * Created by Vladimir on 17.06.2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class TriggersIT {

    private Session session;

    private final ObjectMapper mapper = new ObjectMapper();

    @Resource
    private EntityManager em;

    @Resource
    private ExchangeRepository exchangeRepository;

    private User user;
    private UserData userData;
    private UserServices userServices;
    private UserServiceOne userService1;
    private UserServiceTwo userService2;
    private UserServiceThree userService3;

    @Before
    public void setUp() throws Exception {

        em = em.getEntityManagerFactory().createEntityManager();
        session = em.unwrap(Session.class);

        cleanDb();
    }

    private void createFakeData() {

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("user@user");
        user.setPasswordHash("user#123");
        user.setAccessType((byte) 1);
        user.setPhoneHash("user#cell#123");
        user.setBlocked(false);

        userData = new UserData();
        userData.setId(user.getId());
        userData.setDate(new Date());
        userData.setName("data name");
        userData.setPass("pass-hash-123");
        userData.setPhone("123-123-123");

        userServices = new UserServices();
        userServices.setUserId(user.getId());
        userServices.setService((byte) 1);

        userService1 = new UserServiceOne();
        userService1.setUserId(user.getId());
        userService1.setYear(2017);
        userService1.setNumber("1234");
        userService1.setDateBegin(new Date());
        userService1.setDateEnd(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));

        userService2 = new UserServiceTwo();
        userService2.setUserId(user.getId());
        userService2.setYear(2018);
        userService2.setNumber("4321");
        userService2.setDateBegin(new Date());
        userService2.setDateEnd(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 48));

        userService3 = new UserServiceThree();
        userService3.setUserId(user.getId());
        userService3.setDateBegin(new Date());
        userService3.setDateEnd(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 72));
    }

    private void updateFakeData() {
        user.setEmail("anotheruser@user");
        user.setPasswordHash("anotheruser#456");
        user.setAccessType((byte) 2);
        user.setPhoneHash("anotheruser#cell#456");
        user.setBlocked(true);

        userData.setName("test name");
        userData.setPass("pass-hash-456");
        userData.setPhone("456-456-456");

        userServices.setService((byte) 2);

        userService1.setYear(2018);
        userService1.setNumber("4321");
        userService1.setDateBegin(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 + 1));
        userService1.setDateEnd(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 + 2));

        userService2.setYear(2019);
        userService2.setNumber("1234");
        userService2.setDateBegin(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 + 1));
        userService2.setDateEnd(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 + 2));

        userService3.setDateBegin(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 + 1));
        userService3.setDateEnd(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 + 2));
    }

    @Test
    public void createRecordWhenDataStored() {
        // given
        createFakeData();

        // when
        saveFakeDataToDB();

        // then
        final List<Exchange> createExchanges = exchangeRepository.findWithEventType("CREATED");
        assertThat(createExchanges, hasSize(6));
        assertThat(hasValidJsonInData(createExchanges), is(true));

        // when
        updateFakeData();
        updateFakeDataInDB();

        // then
        final List<Exchange> updateExchanges = exchangeRepository.findWithEventType("UPDATED");
        assertThat(createExchanges, hasSize(6));
        assertThat(hasValidJsonInData(updateExchanges), is(true));

        // when
        removeFakeDataFromDb();

        // then
        final List<Exchange> removeExchanges = exchangeRepository.findWithEventType("DELETED");
        assertThat(removeExchanges, hasSize(6));
        assertThat(hasValidJsonInData(removeExchanges), is(true));
    }

    private boolean hasValidJsonInData(final List<Exchange> exchanges) {

        for (final Exchange exchange : exchanges) {
            try {
                mapper.readValue(exchange.getData(), HashMap.class);
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    private void saveFakeDataToDB() {
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(user);
        em.persist(userData);
        em.persist(userServices);
        em.persist(userService1);
        em.persist(userService2);
        em.persist(userService3);
        tx.commit();
    }

    private void updateFakeDataInDB() {
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.merge(user);
        em.merge(userData);
        em.merge(userServices);
        em.merge(userService1);
        em.merge(userService2);
        em.merge(userService3);
        tx.commit();
    }

    private void removeFakeDataFromDb() {
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(user);
        em.remove(userData);
        em.remove(userServices);
        em.remove(userService1);
        em.remove(userService2);
        em.remove(userService3);
        tx.commit();
    }

    private void cleanDb() {
        final List<User> users = session.createQuery("Select u from User u").list();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (final User user : users) {
            em.remove(user);
        }
        tx.commit();

        session.createSQLQuery("truncate table gk_sys_site_exchange").executeUpdate();
        session.createSQLQuery("truncate table gk_sys_site_user_data").executeUpdate();
        session.createSQLQuery("truncate table gk_sys_site_user_services").executeUpdate();
        session.createSQLQuery("truncate table gk_sys_site_user_service_1").executeUpdate();
        session.createSQLQuery("truncate table gk_sys_site_user_service_2").executeUpdate();
        session.createSQLQuery("truncate table gk_sys_site_user_service_3").executeUpdate();
    }
}
