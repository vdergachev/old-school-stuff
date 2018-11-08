package com.fasten.inc.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import javax.annotation.PostConstruct;
import javax.ejb.*;

/**
 * Created by Владимир on 21.08.2016.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class MongoProvider {
    private MongoClient client;

    @PostConstruct
    private void initDB() {
        client = new MongoClient();
    }

    @Lock(LockType.READ)
    public MongoClient getMongoClient(){
        return client;
    }

}
