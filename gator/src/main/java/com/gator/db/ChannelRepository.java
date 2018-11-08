package com.gator.db;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Владимир on 16.09.2016.
 */
public interface ChannelRepository extends MongoRepository<Channel, String> {
}
