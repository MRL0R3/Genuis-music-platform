package com.genius.services;

import com.genius.model.accounts.Artist;
import com.genius.model.content.Album;
import com.genius.model.content.Song;
import com.genius.util.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AlbumService {
    private Database database;
    private SongService songService;

    public AlbumService(Database database, SongService songService) {
        this.database = database;
        this.songService = songService;
    }

    // Create a new album (Artist only)
    public Album createAlbum(String title, Artist artist, Date releaseDate) {
        if (artist == null || !artist.isVerified()) {
            return null;
        }
        
        Album album = new Album(title, artist, releaseDate);
        database.addAlbum(album);
        artist.addAlbum(album);
        return album;
    }

    // Add song to album
    public boolean addSongToAlbum(Album album, Song song) {
        if (album == null || song == null) {
            return false;
        }
        
        // Verify the song's artist matches the album's artist
        if (!song.getArtists().contains(album.getArtist())) {
            return false;
        }
        
        album.addSong(song);
        return true;
    }

    // Get album by title
    public Album getAlbumByTitle(String title) {
        return database.getAlbums().stream()
                .filter(a -> a.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    // Get albums by artist
    public List<Album> getAlbumsByArtist(Artist artist) {
        if (artist == null) {
            return new ArrayList<>();
        }
        return database.getAlbums().stream()
                .filter(a -> a.getArtist().equals(artist))
                .collect(Collectors.toList());
    }

    // Search albums
    public List<Album> searchAlbums(String query, Artist artist) {
        return database.getAlbums().stream()
                .filter(album -> 
                    (query == null || album.getTitle().toLowerCase().contains(query.toLowerCase())))
                .filter(album -> artist == null || album.getArtist().equals(artist))
                .collect(Collectors.toList());
    }

    // Get all songs from an artist's albums
    public List<Song> getAllArtistSongs(Artist artist) {
        if (artist == null) {
            return new ArrayList<>();
        }
        
        List<Song> songs = new ArrayList<>();
        artist.getAlbums().forEach(album -> songs.addAll(album.getTracklist()));
        songs.addAll(artist.getSongs().stream()
                .filter(song -> song.getAlbum() == null)
                .collect(Collectors.toList()));
        
        return songs;
    }
}