package com.gator.web;

import com.gator.db.Playlist;
import com.gator.db.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Владимир on 16.09.2016.
 */
@RestController
@RequestMapping("/list")
public class PlaylistController {

    @Autowired
    PlaylistRepository repository;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Playlist> getLists(){
        return repository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public void addList(@RequestBody Playlist newList){
        repository.save(newList);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public void deleteList(@PathVariable String id){
        repository.delete(id);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public String handleException1(HttpMessageNotReadableException ex) {
        return ex.getMessage();
    }
}
