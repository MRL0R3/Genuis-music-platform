package com.genius.model.content;

import com.genius.model.accounts.Artist;
import com.genius.model.enums.Genre;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

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

    private String apiPath;



    public Song(String title, String lyrics, List<Artist> artists, Genre genre,
                Date releaseDate, Integer geniusId, String thumbnailUrl) {

        this.title = validateTitle(title);
        this.lyrics = lyrics != null ? lyrics : "";
        this.artists = new ArrayList<>(artists); // Create mutable copy
        this.genre = genre;
        this.releaseDate = new Date(releaseDate.getTime());
        this.geniusId = geniusId;
        this.thumbnailUrl = thumbnailUrl;
        this.views = 0;
        this.comments = new ArrayList<>();
    }

    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        return title.trim();
    }

    private List<Artist> validateArtists(List<Artist> artists) {
        if (artists == null || artists.isEmpty()) {
            throw new IllegalArgumentException("Artists list cannot be null or empty");
        }
        return Collections.unmodifiableList(new ArrayList<>(artists));
    }

    private Genre validateGenre(Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }
        return genre;
    }

    private Date validateDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Release date cannot be null");
        }
        return new Date(date.getTime()); // Defensive copy
    }

    private String validateThumbnailUrl(String url) {
        if (url == null) {
            return null; // Explicitly allowed to be null
        }

        String trimmed = url.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        // Basic URL format check without throwing exceptions
        if (!trimmed.matches("^(https?|ftp)://.*$")) {
            System.err.println("Warning: Thumbnail URL may not be valid: " + trimmed);
        }

        return trimmed;
    }


    public int getViews() { return views; }
    public void setViews(int views) {
        if (views >= 0) {
            this.views = views;
        }
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


    public void setAlbum(Album album) { this.album = album; }
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics != null ? lyrics : "";
    }
    // In Song.java
    @Override
    public String toString() {
        StringBuilder artistNames = new StringBuilder();
        for (Artist artist : artists) {
            if (artistNames.length() > 0) {
                artistNames.append(", ");
            }
            artistNames.append(artist.getName());
        }

        return String.format("%s - %s (%s) [%d views]",
                title,
                artistNames.toString(),
                genre.toString(),
                views);
    }
}