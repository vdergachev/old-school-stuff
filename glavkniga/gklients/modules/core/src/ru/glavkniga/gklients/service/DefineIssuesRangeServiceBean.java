/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.MagazineIssue;
import ru.glavkniga.gklients.entity.Schedule;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

/**
 * @author LysovIA
 */
@Service(DefineIssuesRangeService.NAME)
public class DefineIssuesRangeServiceBean implements DefineIssuesRangeService {



    @Inject
    private Persistence persistence;

    @Override
    public List<MagazineIssue>getMagazineIssuesForES(ElverSubscription subscription){
                Date dateFrom = this.getDateFrom(subscription);
        Date dateTo = this.getDateTo(subscription);
        return getMagazineIssuesForDates(dateFrom, dateTo);
    }

    public List<MagazineIssue>getMagazineIssuesInBetween(MagazineIssue issueStart, MagazineIssue issueEnd){
        Date dateFrom = this.getDateFromMI(issueStart);
        Date dateTo = this.getDateFromMI(issueEnd);
        return getMagazineIssuesForDates(dateFrom, dateTo);
    }

    public List<MagazineIssue> getMagazineIssuesForDates(Date dateFrom, Date dateTo) {
        List<MagazineIssue> list;
        Transaction tx = persistence.createTransaction();
        TypedQuery<MagazineIssue> query;
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("Select mi from gklients$MagazineIssue mi \n" +
                    "join  gklients$Schedule s on s.magazineIssue.id = mi.id  \n" +
                    "join  gklients$Magazine m on mi.magazine.id = m.id \n" +
                    "where  ( m.magazineID = 1 or m.magazineID = 2) and  s.siteUpload between ?1 and ?2\n" +
                    "order by s.siteUpload", MagazineIssue.class);
            query.setParameter(1, dateFrom);
            query.setParameter(2, dateTo);
            query.setViewName("issue-with-magazine");
            list = query.getResultList();
            tx.commit();
        }catch (NoResultException e){
            return null;
        }
        finally {
            tx.end();
        }
        return list;
    }

    public Date getDateFromMI(MagazineIssue magazineIssue) {
        Schedule schedule = null;
        TypedQuery<Schedule> query;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            query = em.createQuery("Select s FROM gklients$Schedule s\n" +
                    "where s.magazineIssue.id = ?1", Schedule.class);
            query.setParameter(1, magazineIssue.getId());

            schedule = query.getSingleResult();
            tx.commit();
        } catch (NoResultException e) {
            //
        } finally {
            tx.end();
        }
        if (schedule != null) {
            return schedule.getSiteUpload();
        }
        return null;
    }


    public Date getDateFrom(ElverSubscription subscription) {
        return getDateFromMI(subscription.getIssueStart());
    }

    public Date getDateTo(ElverSubscription subscription) {
        return getDateFromMI(subscription.getIssueEnd());
    }

}