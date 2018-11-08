/*
 * Copyright (c) 2015 ru.glavkniga.gklients.service
 */
package ru.glavkniga.gklients.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.global.LoadContext;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.entity.*;
import ru.glavkniga.gklients.enumeration.SysEmail;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LysovIA
 */
@Service(EmailerService.NAME)
public class EmailerServiceBean implements EmailerService {

    @Inject
    private Persistence persistence;

    @Inject
    private EmailService emailService;

    @Inject
    private DefineIssuesRangeService rangeService;

    public void activateRegkey(UUID esId) {
        ElverSubscription elverSubscription = loadEsById(esId);
        HashMap<String, String> replacements = new HashMap<>();
        String email = elverSubscription.getClient().getEmail();

        replacements.put("LOGIN", email);
        replacements.put("PASSWORD", elverSubscription.getClient().getPassword());
        replacements.put("SCHEDULE", makeTable(elverSubscription));

        Date dateFrom = rangeService.getDateFrom(elverSubscription);
        Date dateTo = rangeService.getDateTo(elverSubscription);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTo);
        int lastDayInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar calLastDay = Calendar.getInstance();
        calLastDay.setTime(dateTo);
        calLastDay.set(Calendar.DAY_OF_MONTH, lastDayInMonth);
        dateTo = calLastDay.getTime();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        replacements.put("DATE_START", df.format(dateFrom));
        replacements.put("DATE_END", df.format(dateTo));
        String mailName = elverSubscription.getRiz().getMailName();
        replacements.put("RIZ_NAME", !(mailName == null) ? mailName : "");
        String mailEmail = elverSubscription.getRiz().getMailEmail();
        replacements.put("RIZ_EMAIL", !(mailEmail == null) ? mailEmail : "");
        String mailPhone = elverSubscription.getRiz().getMailPhone();
        replacements.put("RIZ_PHONE", !(mailPhone == null) ? mailPhone : "");
        String mailPerson = elverSubscription.getRiz().getMailPerson();
        replacements.put("RIZ_PERSON", !(mailPerson == null) ? mailPerson : "");
        String startNumber = elverSubscription.getIssueStart().getCode();
        replacements.put("NUM_START", !(startNumber == null) ? startNumber : "");
        String endNumber = elverSubscription.getIssueEnd().getCode();
        replacements.put("NUM_END", !(endNumber == null) ? endNumber : "");
        if (isReSubscription(elverSubscription))
            replacements.put("SUBSCRIBE_TYPE", " продление ");
        else
            replacements.put("SUBSCRIBE_TYPE", " оформление ");
        this.sendEmailAsync(email, replacements, SysEmail.REGKEY_ACTIVATION);
        //this.sendEmailToRiz(elverSubscription, SysEmail.RIZ_NOTIFICATION);  //TODO: implement RIZ sending methods
    }

    public void notifyRiz(UUID esId) {
        ElverSubscription elverSubscription = loadEsById(esId);
        String rizEmail = elverSubscription.getRiz().getElverEmail();
        String regkey = elverSubscription.getRegkey();
        String clientEmail = elverSubscription.getClient().getEmail();

        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("EMAIL", !(clientEmail == null) ? clientEmail : "");
        replacements.put("REGKEY", !(regkey == null) ? regkey : "");
        this.sendEmailAsync(rizEmail, replacements, SysEmail.RIZ_NOTIFICATION);
    }


    public void changePassword(Client client) {
        String email = client.getEmail();
        String password = client.getPassword();
        String name = client.getName();

        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("NAME", !(name == null) ? name : "");
        replacements.put("PASSWORD", !(password == null) ? password : "");
        this.sendEmailAsync(email, replacements, SysEmail.PASSWORD_CHANGE);
    }


    public void changeEmail(Client client) {
        String email = client.getEmail();
        String password = client.getPassword();
        String name = client.getName();

        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("NAME", !(name == null) ? name : "");
        replacements.put("PASSWORD", !(password == null) ? password : "");
        replacements.put("EMAIL", !(email == null) ? email : "");
        this.sendEmailAsync(email, replacements, SysEmail.EMAIL_CHANGE);
    }

    public void remindPassword(Client client) {
        String email = client.getEmail();
        String password = client.getPassword();
        String name = client.getName();
        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("NAME", !(name == null) ? name : "");
        replacements.put("PASSWORD", !(password == null) ? password : "");
        this.sendEmailAsync(email, replacements, SysEmail.PASSWORD_REMINDER);
    }

    public void notifyNewIssue(Client client, MagazineIssue magazineIssue) {
        String email = client.getEmail();
        String password = client.getPassword();
        String name = client.getName();
        String code = magazineIssue.getCode();
        String number = magazineIssue.getNumber();
        String year = magazineIssue.getYear();
        String magazineAbb = code.substring(0, 2);

        HashMap<String, String> replacements = new HashMap<>();

        replacements.put("ABB", !(magazineAbb == null) ? magazineAbb : "");
        replacements.put("NUMBER", !(number == null) ? number : "");
        replacements.put("YEAR", !(year == null) ? year : "");
        replacements.put("NAME", !(name == null) ? name : "");
        replacements.put("CODE", !(code == null) ? code : "");
        replacements.put("PASSWORD", !(password == null) ? password : "");
        this.sendEmailAsync(email, replacements, SysEmail.NEW_ISSUE);
    }

    public EmailTemplate loadTemplate(SysEmail sysEmail) {
        List<EmailTemplate> list;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager entityManager = persistence.getEntityManager();
            TypedQuery<EmailTemplate> query = entityManager.createQuery(
                    "select o from gklients$EmailTemplate o where o.emailType = ?1", EmailTemplate.class);
            query.setParameter(1, sysEmail.getId());
            list = query.getResultList();
            tx.commit();
        } finally {
            tx.end();
        }
        return list.iterator().next();
    }

    public String processTemplate(String template, Map<String, String> replacements) {
        Pattern pattern = Pattern.compile("\\[(.+?)\\]");
        Matcher matcher = pattern.matcher(template);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = replacements.get(matcher.group(1));
            builder.append(template.substring(i, matcher.start()));
            if (replacement == null)
                builder.append(matcher.group(0));
            else
                builder.append(replacement);
            i = matcher.end();
        }
        builder.append(template.substring(i, template.length()));
        return builder.toString();
    }

    private void sendEmailAsync(String email, HashMap<String, String> replacements, SysEmail sysEmail) {
        EmailTemplate emailTemplate = this.loadTemplate(sysEmail);
        if (emailTemplate == null) {
            //TODO throw exception "Template not found" here
        }
        String caption = processTemplate(emailTemplate.getSubject(), replacements);
        String body = processTemplate(emailTemplate.getBody(), replacements);
        EmailInfo emailInfo = new EmailInfo(email, caption, body);
        emailService.sendEmailAsync(emailInfo);
    }


    private boolean isReSubscription(ElverSubscription es) {
        Transaction tx = persistence.createTransaction();
        int numRows = 0;
        try {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<ElverSubscription> emQuery = em.createQuery(
                    "SELECT s FROM gklients$ElverSubscription s \n" +
                            "WHERE s.client.id = ?1",//TODO add AND construction to ignore soft deletion
                    ElverSubscription.class);
            emQuery.setParameter(1, es.getClient().getId());
            List<ElverSubscription> es_result = emQuery.getResultList();
            numRows = es_result.size();
            tx.commit();
        } finally {
            tx.end();
        }
        return numRows > 1;
    }

    private String makeTable(ElverSubscription subscription) {
        String htmlTable = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" bordercolor=\"#000000\" style=\"width:100%; font-family:Arial, Helvetica, sans-serif; \">";
        List<MagazineIssue> issueList = rangeService.getMagazineIssuesForES(subscription);



        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        for (MagazineIssue issue : issueList) {
            String magazineAbb = issue.getMagazine().getMagazineAbb();
            Integer magazineID = issue.getMagazine().getMagazineID();
            if (magazineID == 1)
                htmlTable += ("<tr style=\"color:#4F57A6;\">\n");
            else if (magazineAbb.equals("ЭС")) {
                htmlTable += ("<tr bgcolor=\"#bdfdff\">\n");
            }
            htmlTable += "<td><span  style=\"margin-left:8px;\">\n";
            htmlTable += magazineAbb + ", ";
            htmlTable += issue.getYear() + ", №";
            htmlTable += issue.getNumber() + " ";
            Schedule schedule = loadScheduleByMagazineIssueId(issue.getId());
            if (magazineAbb.equals("ЭС")) {
                htmlTable += "«" + schedule.getComments() + "»";
            }
            htmlTable += "</span></td>\n<td><span align=\"center\">\n";
            htmlTable += df.format(schedule.getSiteUpload());
            htmlTable += "</span></td>\n</tr>";
        }
        htmlTable += ("</table>");
        return htmlTable;
    }

    @Inject
    private DataManager dataManager;

    private ElverSubscription loadEsById(UUID esId) {
        LoadContext loadContext = LoadContext.create(ElverSubscription.class)
                .setId(esId).setView("elverSubscription-full");
        return (ElverSubscription) dataManager.load(loadContext);
    }


    private Schedule loadScheduleByMagazineIssueId(UUID magazineIssueId) { //TODO refactor to avoid load schedule for each MagIssue
        LoadContext.Query query = new LoadContext.Query("Select s from gklients$Schedule s where s.magazineIssue.id = :id");
        query.setParameter("id", magazineIssueId);
        LoadContext loadContext = LoadContext.create(Schedule.class)
                .setQuery(query)
                .setView("schedule-view");
        return (Schedule) dataManager.load(loadContext);
    }

}