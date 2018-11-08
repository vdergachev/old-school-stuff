/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.gconnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haulmont.cuba.core.global.UuidProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glavkniga.gklients.entity.Client;
import ru.glavkniga.gklients.entity.Test;
import ru.glavkniga.gklients.entity.TestEmail;
import ru.glavkniga.gklients.entity.TestMark;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by LysovIA on 09.12.2015.
 */
public class Parser {
    private Logger log = LoggerFactory.getLogger(getClass());

    private int responseCode;
    private HashMap<String, Object> responseMap;

    public HashMap<String, Object> getResponseMap() {
        return responseMap;
    }

    public int getResponseCode() {
        return responseCode;
    }


    public Parser(Response response, MethodEnum method) {
        this.log = LoggerFactory.getLogger(getClass());
        this.responseMap = new HashMap<>();
        if (response != null) {
            this.responseCode = response.code;

            if (response.code == HttpURLConnection.HTTP_OK) {      //JSON is not coming, error appeared
                if (response.json != null && !response.json.isEmpty()) {
                    doParse(response.json, method);
                }
            } else {
                this.responseMap.put("error", "Response JSON is empty");
            }
        } else
            this.responseMap.put("error", "Response is empty");
    }

    private void doParse(String response, MethodEnum method) {
        try {
            JsonParser parser = new JsonParser();
            Object json = new JSONTokener(response).nextValue();
            if (json instanceof JSONObject) {
                JsonObject mainObject = parser.parse(response).getAsJsonObject();

                Map<String, String> valueMap = doParseObject(mainObject, method);
                this.responseMap.putAll(valueMap);

            } else if (json instanceof JSONArray) {
                JsonArray mainArray = parser.parse(response).getAsJsonArray();
                doParseArray(mainArray, method);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doParseArray(JsonArray mainArray, MethodEnum method) {
        Iterator<JsonElement> itr = mainArray.iterator();

        while (itr.hasNext()) {
            JsonElement item = itr.next();
            Map<String, String> valueMap = new HashMap<>();
            if (item instanceof JsonObject) {
                JsonObject object = item.getAsJsonObject();
                valueMap = doParseObject(object, method);
            } else if (!(item instanceof JsonArray)) {
                valueMap.put(item.toString(), item.toString());
            }
            switch (method) {
                case getTests:
                    Test test = new Test();
                    test.setTestName(valueMap.get("testName"));
                    String testId = valueMap.get("testId");
                    Integer testIdInt = Integer.parseInt(testId);
                    test.setTestId(testIdInt);
                    this.responseMap.put(String.valueOf(test.hashCode()), test);
                    break;
                case getTestEmails:
                    TestEmail testEmail = new TestEmail();

                    String userId = valueMap.get("emailIndex");
                    testEmail.setEmailIndex(userId);

                    String email = valueMap.get("testEmail");
                    testEmail.setEmail(email);

                    String dateSave = valueMap.get("dateSave");
                    DateFormat ddf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Date dDateSave = null;
                    try {
                        dDateSave = ddf.parse(dateSave);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    testEmail.setDateSave(dDateSave);
                    this.responseMap.put(String.valueOf(testEmail.hashCode()), testEmail);
                    break;
                case getTestMarks:
                    TestMark testMark = new TestMark();

                    String mTestId = valueMap.get("testIndex");
                    Integer mTestIdInt = Integer.parseInt(mTestId);
                    testMark.setTestIndex(mTestIdInt);

                    String mId = valueMap.get("markIndex");
                    Integer mIdInt = Integer.parseInt(mId);
                    testMark.setMarkIndex(mIdInt);

                    String mUserId = valueMap.get("emailIndex");
                    testMark.setEmailIndex(mUserId);

                    String datePass = valueMap.get("datePass");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Date dDatePass = null;
                    try {
                        dDatePass = df.parse(datePass);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    testMark.setDatePass(dDatePass);

                    String mark = valueMap.get("mark");
                    Double dMark = Double.parseDouble(mark);
                    testMark.setMark(dMark);

                    this.responseMap.put(String.valueOf(testMark.hashCode()), testMark);
                    break;
                case getUser:
                    log.warn("got user as array");
                    break;
                case getUserData:
                    Client client = new Client();
                    client.setId(UuidProvider.fromString(valueMap.get("id")));
                    client.setEmail(valueMap.get("email"));
                    client.setPassword(valueMap.get("pass"));
                    client.setPasswordHash(valueMap.get("pass_hash"));
                    client.setName(valueMap.get("name"));
                    client.setPhone(valueMap.get("phone"));
                    this.responseMap.put(String.valueOf(client.getId()), client);
                    break;
                case getUserServiceData:
                    this.responseMap.putAll(valueMap);
                    break;
                case getUserServices:
                    this.responseMap.put(String.valueOf(item.hashCode()), trunc(String.valueOf(item)));
                    break;
                case getUsersList:
                    this.responseMap.put(String.valueOf(item.hashCode()), trunc(String.valueOf(item)));
                    break;

            }
        }
    }

    private HashMap<String, String> doParseObject(JsonObject mainObject, MethodEnum method) {
        HashMap<String, String> valueMap = new HashMap<>();
        String value;
        try {
            if (mainObject.has("error")) {
                String error = trunc(mainObject.get("error").toString());
                valueMap.put("error", error);
                return valueMap;
            }
            switch (method) {
                case login:
                    String sessionId = mainObject.get("session_id").toString();
                    valueMap.put("sessionId", trunc(sessionId));
                    break;
                case logout:
                    break;
                case setUsers:
                case setRizData:
                case setSchedule:
                case setRegkeys:
                    value = mainObject.get("num_rows").toString();
                    valueMap.put("numRows", value);
                    break;
                case countUsers:
                    value = mainObject.get("countUsers").toString();
                    valueMap.put("countUsers", trunc(value));
                    break;
                case isRegkey:
                    value = mainObject.get("date_activation").toString();
                    valueMap.put("dateActivation", trunc(value));
                    break;
                case countRegkeys:
                    value = mainObject.get("countRegkeys").toString();
                    valueMap.put("countRegkeys", trunc(value));
                    break;
                case countTests:
                    value = mainObject.get("countTests").toString();
                    valueMap.put("countTests", trunc(value));
                    break;
                case countTestEmails:
                    value = mainObject.get("countTestEmails").toString();
                    valueMap.put("countTestEmails", trunc(value));
                    break;
                case countTestMarks:
                    value = mainObject.get("countTestMarks").toString();
                    valueMap.put("countTestMarks", trunc(value));
                    break;
                case getTests:
                    String testId = mainObject.get("test_id").toString();
                    String testName = mainObject.get("test_name").toString();
                    valueMap.put("testId", trunc(testId));
                    valueMap.put("testName", trunc(testName));
                    break;
                case getTestEmails:
                    String testEmail = mainObject.get("email").toString();
                    String dateSave = mainObject.get("date_save").toString();
                    String userId = mainObject.get("user_id").toString();
                    valueMap.put("testEmail", trunc(testEmail));
                    valueMap.put("emailIndex", trunc(userId));
                    valueMap.put("dateSave", trunc(dateSave));
                    break;
                case getTestMarks:
                    String mTestId = mainObject.get("test_id").toString();
                    String mId = mainObject.get("id").toString();
                    String mUserId = mainObject.get("user_id").toString();
                    String mark = mainObject.get("mark").toString();
                    String datePass = mainObject.get("date_pass").toString();
                    valueMap.put("markIndex", trunc(mId));
                    valueMap.put("testIndex", trunc(mTestId));
                    valueMap.put("emailIndex", trunc(mUserId));
                    valueMap.put("datePass", trunc(datePass));
                    valueMap.put("mark", trunc(mark));
                    break;
                case getUser:
                    log.warn("got user as Object");
                    break;
                case getUserData:
                case getUserServiceData:
                    Set<Map.Entry<String, JsonElement>> entrySet =  mainObject.entrySet();
                    entrySet.forEach(stringJsonElementEntry -> {
                        String key = stringJsonElementEntry.getKey();
                        String val = stringJsonElementEntry.getValue().toString();
                        valueMap.put(trunc(key),trunc(val));
                    });
                    break;
            }
        } catch (Exception e) {
            valueMap.put("error", "Error parsing JSON");
        }
        return valueMap;
    }

    private String trunc(String res) {
        if (res.charAt(0) == '\"') {
            res = res.substring(1, res.length());
        }
        if (res.charAt(res.length() - 1) == '\"') {
            res = res.substring(0, res.length() - 1);
        }

        if (res.equals("null") || res.equals("Null")) {
            res = "";
        }

        return res;
    }
}
