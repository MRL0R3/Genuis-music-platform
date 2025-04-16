package com.genius.model.content;

import com.genius.model.accounts.Artist;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Album {
    private String title;
    private Artist artist;
    private Date releaseDate;
    private List<Song> tracklist;
    
    public Album(String title, Artist artist, Date releaseDate) {
        this.title = title;
        this.artist = artist;
        this.releaseDate = releaseDate;
        this.tracklist = new ArrayList<>();
    }
    
    public void addSong(Song song) {
        tracklist.add(song);
        song.setAlbum(this);
    }
    
    // Getters
    public String getTitle() { return title; }
    public Artist getArtist() { return artist; }
    public Date getReleaseDate() { return releaseDate; }
    public List<Song> getTracklist() { return new ArrayList<>(tracklist); }
    
    @Override
    public String toString() {
        return "Album{" +
                "title='" + title + '\'' +
                ", artist=" + artist +
                ", releaseDate=" + releaseDate +
                ", tracklistSize=" + tracklist.size() +
                '}';
    }
}