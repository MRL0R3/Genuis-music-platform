package com.genius.services;

import com.genius.model.accounts.Admin;
import com.genius.model.accounts.Artist;
import com.genius.model.accounts.User;
import com.genius.model.content.Song;
import com.genius.util.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountService {
    private Database database;

    public AccountService(Database database) {
        this.database = database;
    }

    // Follow an artist (User)
    public boolean followArtist(User user, Artist artist) {
        if (user == null || artist == null || !artist.isVerified()) {
            return false;
        }
        
        user.followArtist(artist);
        database.addUserNotification(user, 
            "You are now following " + artist.getName());
        return true;
    }

    // Unfollow an artist (User)
    public boolean unfollowArtist(User user, Artist artist) {
        if (user == null || artist == null) {
            return false;
        }
        
        // This assumes the User class has an unfollowArtist method
        user.getFollowing().remove(artist);
        return true;
    }

    // Verify artist (Admin)
    public boolean verifyArtist(Admin admin, Artist artist) {
        if (admin == null || artist == null) {
            return false;
        }
        artist.setVerified(true);
        database.removeArtistForApproval(artist);
        database.addAccount(artist); // Re-add to ensure sync

        // Notify the artist
        database.addArtistNotification(artist, 
            "Your account has been verified by admin " + admin.getName());
        return true;
    }

    // Get artists pending approval
    public List<Artist> getArtistsForApproval() {
        return new ArrayList<>(database.getArtistsForApproval());
    }

    // Get user's followed artists
    public List<Artist> getFollowedArtists(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getFollowing();
    }

    // Search accounts


    // Get notifications for a user
    public List<String> getUserNotifications(User user) {
        return database.getUserNotifications(user);
    }

    // Get notifications for an artist
    public List<String> getArtistNotifications(Artist artist) {
        return database.getArtistNotifications(artist);
    }
}