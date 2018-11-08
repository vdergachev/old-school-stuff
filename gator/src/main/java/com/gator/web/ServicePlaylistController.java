package com.gator.web;

import org.apache.commons.io.IOUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * Created by Владимир on 16.09.2016.
 */
@RestController
@RequestMapping("/myplaylist")
public class ServicePlaylistController {

    @RequestMapping(value="download", method= RequestMethod.GET)
    public void getDownload(HttpServletResponse response) {

        //TODO Fill filename
        String filename = "myplaylist.m3u";

        try{
            // Get your file stream from wherever.
            InputStream myStream = null;

            // Set the content type and attachment header.
            response.addHeader("Content-disposition", "attachment;filename=" + filename);
            response.setContentType("txt/plain");

            // Copy the stream to the response's output stream.
            IOUtils.copy(myStream, response.getOutputStream());
            response.flushBuffer();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
