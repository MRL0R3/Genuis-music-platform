package com.genius.util;

import com.genius.model.accounts.Account;
import com.genius.model.accounts.Artist;
import com.genius.model.accounts.User;
import com.genius.model.content.Album;
import com.genius.model.content.Comment;
import com.genius.model.content.LyricEdit;
import com.genius.model.content.Song;

import java.io.*;
import java.util.*;


public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "music_platform_data.ser";


    private List<Album> albums;
    private List<Comment> comments;
    private List<LyricEdit> lyricEdits;
    private List<Artist> artistsForApproval;
    private  List<Song> songs = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();




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
        loadData();
    }

    // Account methods
    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
            saveData();
        }


    }

    public Account getAccountByUsername(String username) {
        return accounts.stream()
                .filter(a -> a.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }


    public synchronized void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    private synchronized void loadData() {
        File file = new File(DATA_FILE);

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(DATA_FILE))) {
                Database loaded = (Database) ois.readObject();

                this.accounts = new ArrayList<>();
                this.songs = new ArrayList<>();
                this.albums = new ArrayList<>();
                this.comments = new ArrayList<>();
                this.lyricEdits = new ArrayList<>();
                this.artistsForApproval = new ArrayList<>();
                this.userNotifications = new HashMap<>();
                this.artistNotifications = new HashMap<>();

            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data: " + e.getMessage());
                this.accounts = new ArrayList<>();
                this.songs = new ArrayList<>();
            }
        }
    }


    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }
    // Song methods
    public void addSong(Song song) {
        songs.add(song);
        saveData();

    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);

    }
    // Album methods
    public void addAlbum(Album album) {
        albums.add(album);
        saveData();
    }

    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    public List<LyricEdit> getLyricEdits() {
        return new ArrayList<>(lyricEdits);
    }

    // Artist approval methods
    public void addArtistForApproval(Artist artist) {
        artistsForApproval.add(artist);
        saveData();
    }

    public void removeArtistForApproval(Artist artist) {
        artistsForApproval.remove(artist);
        saveData();
    }

    public List<Artist> getArtistsForApproval() {
        return new ArrayList<>(artistsForApproval);
    }

    // Notification methods
    public void addUserNotification(User user, String message) {
        userNotifications.computeIfAbsent(user, k -> new ArrayList<>()).add(message);
        saveData();
    }

    public void addArtistNotification(Artist artist, String message) {
        artistNotifications.computeIfAbsent(artist, k -> new ArrayList<>()).add(message);
        saveData();
    }

    public List<String> getUserNotifications(User user) {
        return new ArrayList<>(userNotifications.getOrDefault(user, new ArrayList<>()));

    }

    public List<String> getArtistNotifications(Artist artist) {
        return new ArrayList<>(artistNotifications.getOrDefault(artist, new ArrayList<>()));
    }

}