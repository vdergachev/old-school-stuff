package com.gator.service;

import com.gator.db.Channel;
import com.gator.db.ChannelRepository;
import com.gator.db.Playlist;
import com.gator.db.PlaylistRepository;
import com.gator.util.PlaylistReader;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Владимир on 16.09.2016.
 */

@Component
public class UpdateChannelsTask {

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    ChannelRepository channelRepository;

    public void updateChannels(){
        System.out.println("Start channel update process!!!");

        List<Playlist> playlists = playlistRepository.findAll();

        if(playlists.isEmpty()){
            playlists = new ArrayList<>();
            playlists.add(new Playlist("http://listiptv.ru/iptv.m3u", ""));
        }

        playlists.stream().parallel().forEach(playlist -> {

            File file = new File(getTempFilename());
            // Download file by URL
            try {
                FileUtils.copyURLToFile(new URL(playlist.getUlr()), file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //Read all positions
            PlaylistReader channels = new PlaylistReader(file);

            //Store it
            channelRepository.save(channels);

            //TODO Streams Cleanup
            if(!file.delete())
                file.deleteOnExit();
        });
    }

    private static String getTempFilename(){
        return System.getProperty("java.io.tmpdir") + UUID.randomUUID().toString() + ".dat";
    }
}
