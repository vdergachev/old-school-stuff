package com.revo.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Владимир on 14.12.2015.
 */
@Entity
public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Menu menus;

    public Restaurant() {
    }

    public Restaurant(String name, Menu menu) {
        this.name = name;
        this.menus = menu;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
