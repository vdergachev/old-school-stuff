package com.gator.db;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Владимир on 16.09.2016.
 */
public interface PlaylistRepository extends MongoRepository<Playlist, String> {

}
