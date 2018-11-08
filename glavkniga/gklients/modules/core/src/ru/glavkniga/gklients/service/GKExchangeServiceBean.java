/*
 * Copyright (c) 2015 gklients
 */
package ru.glavkniga.gklients.service;

import com.beust.jcommander.internal.Nullable;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UuidProvider;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import org.springframework.stereotype.Service;
import ru.glavkniga.gklients.crud.AccessGranter;
import ru.glavkniga.gklients.crud.Gadd;
import ru.glavkniga.gklients.crud.Gget;
import ru.glavkniga.gklients.crudentity.ElverAccess;
import ru.glavkniga.gklients.crudentity.Regkey;
import ru.glavkniga.gklients.crudentity.SeminarAccess;
import ru.glavkniga.gklients.crudentity.SituationAccess;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.ClientService;
import ru.glavkniga.gklients.entity.ElverSubscription;
import ru.glavkniga.gklients.entity.Tarif;
import ru.glavkniga.gklients.gconnection.*;
import ru.glavkniga.gklients.interfaces.WebsiteConfig;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LysovIA
 */
@Service(GKExchangeService.NAME)
public class GKExchangeServiceBean implements GKExchangeService {


    @Inject
    private ReportService reportService;
    @Inject
    private Configuration configuration;
    @Inject
    private Persistence persistence;


    public boolean setUser(UUID uuid, Client client) {
        HashMap<String, String> urlParams = new HashMap<>();
        if (uuid != null) {
            urlParams.put("id", uuid.toString());
        }
        HashMap<String, String> postFields = new HashMap<>();
        postFields.put("id", client.getId().toString());
        postFields.put("email", client.getEmail());
        if (client.getPasswordHash() != null)
            postFields.put("pass_hash", client.getPasswordHash());
        postFields.put("access_type", "0");
        Response response = loginAndSendPost(MethodEnum.setUser, urlParams, postFields);
        return response.code == HttpURLConnection.HTTP_OK;
    }

    public boolean setUser(Client client) {
        Gadd clientAdder = new Gadd();
        return clientAdder.addObject(client);
    }


    public boolean setUserServices(Client client) {
        List<ClientService> csList = new ArrayList<>();
        TypedQuery<ClientService> query;
        Transaction tx = persistence.createTransaction();
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
                                Tarif tarif = metadata.create(Tarif.class);
                                Transaction tx2 = persistence.createTransaction();
                                try {
                                    EntityManager em = persistence.getEntityManager();
                                    tarif = em.find(Tarif.class, elverSubscription.getTarif().getId(), "_minimal");
                                    tx2.commit();
                                } finally {
                                    tx2.end();
                                }

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
        return true;
    }

    public int setUsers(Report report) {
        int returnValue = 0;
        MethodEnum methodEnum = MethodEnum.setUsers;
        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();

        Parser resPars = this.sendReport(report, websiteURL + methodEnum.name(), methodEnum);
        if (resPars.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Map<String, Object> resMap = resPars.getResponseMap();
            try {
                returnValue = Integer.parseInt(resMap.get("numRows").toString());
            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return returnValue;
    }

    public int setRizData(Report report) {
        int returnValue = 0;
        MethodEnum methodEnum = MethodEnum.setRizData;
        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();
        Parser resPars = this.sendReport(report, websiteURL + methodEnum.name(), methodEnum);
        if (resPars.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Map<String, Object> resMap = resPars.getResponseMap();
            try {
                returnValue = Integer.parseInt(resMap.get("numRows").toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return returnValue;
    }

    public int setRegkeys(Report report) {
        int returnValue = 0;
        MethodEnum methodEnum = MethodEnum.setRegkeys;
        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();
        Parser resPars = this.sendReport(report, websiteURL + methodEnum.name(), methodEnum);
        if (resPars.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Map<String, Object> resMap = resPars.getResponseMap();
            try {
                returnValue = Integer.parseInt(resMap.get("numRows").toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return returnValue;
    }

    public int setSchedule(Report report) {
        int returnValue = 0;
        MethodEnum methodEnum = MethodEnum.setSchedule;
        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();
        Parser resPars = this.sendReport(report, websiteURL + methodEnum.name(), methodEnum);
        if ((resPars != null) && resPars.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Map<String, Object> resMap = resPars.getResponseMap();
            try {
                returnValue = Integer.parseInt(resMap.get("numRows").toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return returnValue;
    }


    public int countUsers() {
        return this.getCount(MethodEnum.countUsers);
    }

    public int countRegkeys() {
        return this.getCount(MethodEnum.countRegkeys);
    }

    public boolean isUser(String email) {
        Map<String, String> params = new HashMap<>();
        params.put("m", email);
        return getBoolean(MethodEnum.isUser, params);
    }


    public Date isRegkey(String regkey) {
        Date dateActivation = null;

        MethodEnum methodEnum = MethodEnum.isRegkey;
        HashMap<String, String> urlParams = new HashMap<>();
        urlParams.put("k", regkey);
        Response response = loginAndSendGet(methodEnum, urlParams);

        Parser resPars = new Parser(response, methodEnum);
        if (resPars.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String stringDate = resPars.getResponseMap().get("dateActivation").toString();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dateActivation = format.parse(stringDate);
            } catch (ParseException e) {
                e.printStackTrace(); //TODO validate parsing is correct
            }
        }
        return dateActivation;
    }

    public UUID getUser(String email) {
        String response_uuid = "";
        Map<String, Object> respMap = this.getObject(MethodEnum.getUser);
        if (respMap != null) {
            for (Object obj : respMap.values()) {
                response_uuid = (String) obj;
            }
        }
        return UuidProvider.fromString(response_uuid);
    }


    public Map<String, Object> getUsersList() {
        return this.getObject(MethodEnum.getUsersList);
    }

    public Map<String, Object> getUserData(UUID id) {
        Gget gget = new Gget(Client.class);
        gget.addFilterProperty("id", String.valueOf(id));
        return gget.getObjects();
    }

    public Map<String, Object> getUserServices(UUID id) {
        Gget gget = new Gget(ClientService.class);
        gget.addFilterProperty("id", String.valueOf(id));
        return gget.getObjects();
    }

    public Map<String, Object> getUserServiceData(UUID id, int service) {
        Class paramClass;
        switch (service) {
            case 1:
                paramClass = ElverAccess.class;
                break;
            case 2:
                paramClass = SeminarAccess.class;
                break;
            case 3:
                paramClass = SituationAccess.class;
                break;
            default:
                paramClass = Entity.class;
                break;
        }
        //@SuppressWarnings({"unchecked"})
        Gget gget = new Gget(paramClass);
        gget.addFilterProperty("id", String.valueOf(id));
        gget.addFilterProperty("service", String.valueOf(service));
        return gget.getObjects();
    }

    public boolean deleteUserData(UUID id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id.toString());
        return getBoolean(MethodEnum.deleteUserData, params);
    }

    /**
     * Data collection from GK TEST
     */


    public int countTest() {
        return this.getCount(MethodEnum.countTests);
    }

    public int countTestMark() {
        return this.getCount(MethodEnum.countTestMarks);
    }

    public int countTestEmail() {
        return this.getCount(MethodEnum.countTestEmails);
    }


    public Map<String, Object> getTest() {
        return this.getObject(MethodEnum.getTests);
    }

    public Map<String, Object> getTestEmail() {
        return this.getObject(MethodEnum.getTestEmails);
    }

    public Map<String, Object> getTestMark() {
        return this.getObject(MethodEnum.getTestMarks);
    }

    /**
     * Private functions
     */


    private Parser sendReport(Report report, String requestUrl, MethodEnum method) {
        Session s = AppBeans.get(Session.NAME);
        Map<String, Object> reportParams = new HashMap<>();
        ReportOutputDocument document = reportService.createReport(report, reportParams);
        Response response;
        if (document != null) {
            Map<String, String> urlParams = new HashMap<>();
            urlParams.put("s", s.getSessionId());
            requestUrl = UrlFormer.addParams(requestUrl, urlParams);
            try {
                SendPost post = new SendPost(requestUrl, "UTF-8");
                String fieldName = null;
                switch (method) {
                    case setUsers:
                        fieldName = "user";
                        break;
                    case setRegkeys:
                        fieldName = "reg_keys";
                        break;
                    case setSchedule:
                        fieldName = "schedule";
                        break;
                    case setRizData:
                        fieldName = "riz";
                        break;
                }
                post.addFilePartInByte(fieldName, document.getContent());
                response = post.finish();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return new Parser(response, method);
        } else {
            return null;
        }
    }

    private int getCount(MethodEnum methodEnum) {
        Map<String, Object> responseMap = this.getObject(methodEnum);
        try {
            String value = responseMap.get(methodEnum.name()).toString();
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private boolean getBoolean(MethodEnum methodEnum, @Nullable Map<String, String> params) {
        int responseCode = this.getResponseCode(methodEnum, params);
        return responseCode == HttpURLConnection.HTTP_OK;
    }

    private int getResponseCode(MethodEnum methodEnum, @Nullable Map<String, String> params) {
        Parser resPars = this.doRequest(methodEnum, params);
        return resPars.getResponseCode();
    }

    private Map<String, Object> getObject(MethodEnum methodEnum) {
        return getObject(methodEnum, null);
    }


    private Map<String, Object> getObject(MethodEnum methodEnum, @Nullable Map<String, String> params) {
        HashMap<String, Object> returnValue = new HashMap<>();
        Parser resPars = this.doRequest(methodEnum, params);
        if (resPars.getResponseCode() == HttpURLConnection.HTTP_OK) {
            returnValue = resPars.getResponseMap();
        }
        return returnValue;
    }

    private Parser doRequest(MethodEnum methodEnum, @Nullable Map<String, String> params) {
        HashMap<String, String> urlParams = new HashMap<>();
        if (params != null && !params.isEmpty()) {
            urlParams.putAll(params);
        }
        Response response = this.loginAndSendGet(methodEnum, urlParams);
        return new Parser(response, methodEnum);
    }

    private Response loginAndSendGet(MethodEnum methodEnum, @Nullable HashMap<String, String> urlParams) {
        Session s = AppBeans.get(Session.NAME);
        if (urlParams == null) {
            urlParams = new HashMap<>();
        }
        urlParams.put("s", s.getSessionId());
        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();
        String requestURL = UrlFormer.addParams(websiteURL + methodEnum.name(), urlParams);
        Response response = SendGet.get(requestURL);
        return response;
    }

    private Response loginAndSendPost(MethodEnum methodEnum, @Nullable HashMap<String, String> urlParams, @Nullable HashMap<String, String> postFields) {
        Session s = AppBeans.get(Session.NAME);
        if (urlParams == null) {
            urlParams = new HashMap<>();
        }
        urlParams.put("s", s.getSessionId());
        String websiteURL = configuration.getConfig(WebsiteConfig.class).getWebsiteURL();
        String requestURL = UrlFormer.addParams(websiteURL + methodEnum.name(), urlParams);
        Response response = new Response();
        try {
            SendPost post = new SendPost(requestURL, "UTF-8");
            if (postFields != null) {
                postFields.forEach(post::addFormField);
            }
            response = post.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


}