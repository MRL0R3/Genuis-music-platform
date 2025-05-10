package com.genius.model.accounts;

import com.genius.model.content.Album;
import com.genius.model.content.Song;
import com.genius.model.enums.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Artist extends Account {
    private String geniusId;
    private String imageUrl;
    private  List<Song> songs ;
    private List<Album> albums;
    private boolean verified;


    public Artist(String username, String password, String name, int age,
                  String email) {
        super(username, password, name, age, email, Role.ARTIST);
        this.songs = new ArrayList<>();
        this.albums = new ArrayList<>();
        this.verified = false;
    }

    public String getName() {
        return super.getName(); // Inherited from Account
    }
    
    public void addSong(Song song) {
        songs.add(song);
    }
    
    public void addAlbum(Album album) {
        albums.add(album);
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }
    
    public List<Album> getAlbums() {
        return new ArrayList<>(albums);
    }
    
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    public void setGeniusId(String geniusId) {
        this.geniusId = geniusId;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isVerified() {
        return verified;
    }



}