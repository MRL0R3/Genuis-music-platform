package com.genius.util;

import com.genius.model.accounts.Account;
import com.genius.model.accounts.Artist;
import com.genius.model.accounts.User;
import com.genius.model.content.Album;
import com.genius.model.content.Comment;
import com.genius.model.content.LyricEdit;
import com.genius.model.content.Song;

import java.util.*;

public class Database {
    private List<Album> albums;
    private List<Comment> comments;
    private List<LyricEdit> lyricEdits;
    private List<Artist> artistsForApproval;
    private  List<Song> songs = new ArrayList<>();
    private  List<Account> accounts = new ArrayList<>();
    // Notifications
    private Map<User, List<String>> userNotifications;
    private Map<Artist, List<String>> artistNotifications;

    public Database() {
        this.accounts = new ArrayList<>();
        this.songs = new ArrayList<>();
        this.albums = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.lyricEdits = new ArrayList<>();
        this.artistsForApproval = new ArrayList<>();
        this.userNotifications = new HashMap<>();
        this.artistNotifications = new HashMap<>();
    }

    // Account methods
    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
        }
    }
    public Account getAccountByUsername(String username) {
        return accounts.stream()
                .filter(a -> a.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }
    // Song methods
    public void addSong(Song song) {
        songs.add(song);
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }
    // Album methods
    public void addAlbum(Album album) {
        albums.add(album);
    }

    public List<Album> getAlbums() {
        return new ArrayList<>(albums);
    }

    // Comment methods
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    // Lyric edit methods
    public void addLyricEdit(LyricEdit edit) {
        lyricEdits.add(edit);
    }

    public void removeLyricEdit(LyricEdit edit) {
        lyricEdits.remove(edit);
    }

    public List<LyricEdit> getLyricEdits() {
        return new ArrayList<>(lyricEdits);
    }

    // Artist approval methods
    public void addArtistForApproval(Artist artist) {
        artistsForApproval.add(artist);
    }

    public void removeArtistForApproval(Artist artist) {
        artistsForApproval.remove(artist);
    }

    public List<Artist> getArtistsForApproval() {
        return new ArrayList<>(artistsForApproval);
    }

    // Notification methods
    public void addUserNotification(User user, String message) {
        userNotifications.computeIfAbsent(user, k -> new ArrayList<>()).add(message);
    }

    public void addArtistNotification(Artist artist, String message) {
        artistNotifications.computeIfAbsent(artist, k -> new ArrayList<>()).add(message);
    }

    public List<String> getUserNotifications(User user) {
        return new ArrayList<>(userNotifications.getOrDefault(user, new ArrayList<>()));
    }

    public List<String> getArtistNotifications(Artist artist) {
        return new ArrayList<>(artistNotifications.getOrDefault(artist, new ArrayList<>()));
    }

    public void clearUserNotifications(User user) {
        userNotifications.remove(user);
    }

    public void clearArtistNotifications(Artist artist) {
        artistNotifications.remove(artist);
    }
}