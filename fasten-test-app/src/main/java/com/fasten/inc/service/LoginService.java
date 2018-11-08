package com.fasten.inc.service;

import com.fasten.inc.db.MongoProvider;
import com.fasten.inc.net.model.Message;
import com.fasten.inc.net.model.Type;
import com.mongodb.DBRef;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.Session;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;

/**
 * Created by Владимир on 21.08.2016.
 */
@Stateless
public class LoginService {

    private MongoCollection users;
    private MongoCollection tokens;

    @EJB
    MongoProvider mongoProvider;

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddEHH:mm:ssX");

    @PostConstruct
    private void initService() {
        MongoDatabase database = mongoProvider.getMongoClient().getDatabase("fasten");
        users = database.getCollection("users");
        tokens = database.getCollection("tokens");
    }

    @Asynchronous
    public void processRequest(Message message, Session client) {

        String email = message.getData().getString("email");
        String password = message.getData().getString("password");

        FindIterable<Document> it = users.find(
                and(
                        eq("email", email),
                        eq("password", password)
                ));

        Document userDocument = it.first();
        if (userDocument != null) {

            //1. Create new token with user_id
            String newToken = UUID.randomUUID().toString();

            ZonedDateTime expDate = ZonedDateTime.now().plusDays(7);
            String newExpDate = expDate.format(formatter);

            ZonedDateTime createdDateTime = ZonedDateTime.now();
            String createdDate = createdDateTime.format(formatter);

            Document newTokenDoc = new Document()
                    .append("api_token", newToken)
                    .append("api_token_expiration_date", newExpDate)
                    .append("created", createdDate)
                    .append("user", new DBRef("users", userDocument.get("_id")));

            tokens.insertOne(newTokenDoc);

            //2. Update user document
            Document userTokenDoc = new Document("token.api_token", newToken)
                    .append("token.api_token_expiration_date", newExpDate);
            users.updateOne(userDocument, new Document("$set", userTokenDoc ));

            //3. Send result to client
            JsonObject tokenJson = Json.createObjectBuilder()
                    .add("api_token", newToken)
                    .add("api_token_expiration_date", newExpDate)
                    .build();
            Message responseData = new Message(Type.CUSTOMER_API_TOKEN, message.getSequenceId(), tokenJson);
            try {
                client.getBasicRemote().sendText(responseData.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            //Send error wrong login or password
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error_description", "Customer not found")
                    .add("error_code", "customer.notFound")
                    .build();
            Message responseData = new Message(Type.CUSTOMER_ERROR, message.getSequenceId(), errorJson);
            try {
                client.getBasicRemote().sendText(responseData.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
