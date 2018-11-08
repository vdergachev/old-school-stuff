package com.gator.util;

import com.gator.db.Channel;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Владимир on 16.09.2016.
 */
public class PlaylistReader extends LinkedList<Channel> {

    public PlaylistReader(File file) {

        if(file == null)
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line = reader.readLine();
            if (!line.equals("#EXTM3U\r\n")) {
                while ((line = reader.readLine()) != null) {
                    if (line.contains("EXTINF:")) {
                        String secondLine = reader.readLine();
                        handleLines(line, secondLine);
                    }
                }
            } else {
                //TODO Add another formats
                throw new Exception("Format still not implemented");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleLines(String head, String url) {
        int sep = head.indexOf(",");
        if(sep > 0 && head.length() > sep + 1){
            String description = head.substring(sep + 1);
            add(new Channel(description, url));
        }
    }

//    public static void main(String[] args) {
//        PlaylistReader reader =  new PlaylistReader(null);
//        reader.handleLines("#EXTINF:-1,Хорошее Радио","http://radio02-cn03.akadostream.ru:8113/horosheefm128.mp3");
//        reader.handleLines("#EXTINF:-1,Шансон","http://217.20.164.170:8002/;stream.nsv&amp;type=mp3");
//        reader.handleLines("#EXTINF:-1,Серебряный дождь — 100.1","http://radiosilver.corbina.net:8000/silver128a.mp3");
//        reader.handleLines("#EXTINF:-1,","http://");
//        reader.handleLines("#EXTINF:-1,Z","http://Z");
//    }
}
