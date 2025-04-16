package com.genius.model.content;

import com.genius.model.accounts.Artist;
import com.genius.model.enums.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Song {

    private Integer geniusId;

    private String thumbnailUrl;
    private String title;
    private String lyrics;
    private List<Artist> artists;
    private Album album;
    private Genre genre;
    private List<String> tags;
    private int views;
    private Date releaseDate;
    private List<Comment> comments;

    public Song(String title, String lyrics, List<Artist> artists, Genre genre, Date releaseDate,
                Integer geniusId, String thumbnailUrl) {
        this.title = title;
        this.lyrics = lyrics;
        this.artists = new ArrayList<>(artists);
        this.genre = genre;
        this.tags = new ArrayList<>();
        this.views = 0;
        this.releaseDate = releaseDate;
        this.comments = new ArrayList<>();
        this.geniusId = geniusId;
        this.thumbnailUrl = thumbnailUrl;
    }
    public int getViews() { return views; }
    public void setViews(int views) {
        if (views >= 0) {
            this.views = views;
        }
    }
    public void incrementViews() {
        this.views++;
    }
    
    public void addComment(Comment comment) {
        comments.add(comment);
    }
    
    public void addTag(String tag) {
        tags.add(tag);
    }
    
    // Getters and setters
    public String getTitle() { return title; }
    public String getLyrics() { return lyrics; }
    public List<Artist> getArtists() { return Collections.unmodifiableList(artists); }    public Album getAlbum() { return album; }
    public Genre getGenre() { return genre; }
    public List<String> getTags() { return new ArrayList<>(tags); }
    public Integer getGeniusId() { return geniusId; }
    public Date getReleaseDate() { return releaseDate; }
    public List<Comment> getComments() { return new ArrayList<>(comments); }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    public void setGeniusId(Integer geniusId) {
        this.geniusId = geniusId;
    }
    public void setAlbum(Album album) { this.album = album; }
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics != null ? lyrics : "";
    }
    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", artists=" + artists +
                ", views=" + views +
                '}';
    }
}