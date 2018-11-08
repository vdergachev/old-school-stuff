package com.gator.db;

import org.springframework.data.annotation.Id;

/**
 * Created by Владимир on 16.09.2016.
 */
public class Playlist {
    @Id
    private String id;

    private String ulr;
    private String description;

    public Playlist() {
    }

    public Playlist(String ulr, String description) {
        this.ulr = ulr;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getUlr() {
        return ulr;
    }

    public void setUlr(String ulr) {
        this.ulr = ulr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "ulr='" + ulr + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Playlist playlist = (Playlist) o;

        if (id != null ? !id.equals(playlist.id) : playlist.id != null) return false;
        if (ulr != null ? !ulr.equals(playlist.ulr) : playlist.ulr != null) return false;
        return description != null ? description.equals(playlist.description) : playlist.description == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ulr != null ? ulr.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
