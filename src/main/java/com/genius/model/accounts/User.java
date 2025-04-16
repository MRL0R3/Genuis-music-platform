package com.genius.model.accounts;

import com.genius.model.enums.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a regular user account in the Genius music platform.
 * Regular users can view content, suggest edits, follow artists, and comment on songs.
 */
public class User extends Account {
    private List<Artist> following;
    private List<String> notifications;

    /**
     * Constructs a new User with the specified details.
     *
     * @param username The unique username
     * @param password The hashed password
     * @param name     The user's full name
     * @param age      The user's age
     * @param email    The user's email address
     */
    public User(String username, String password, String name, int age, String email) {
        super(username, password, name, age, email, Role.USER);
        this.following = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    /**
     * Follows a new artist.
     *
     * @param artist The artist to follow
     * @return true if the artist was successfully followed, false otherwise
     */
    public boolean followArtist(Artist artist) {
        if (artist == null || following.contains(artist)) {
            return false;
        }
        following.add(artist);
        return true;
    }

    /**
     * Unfollows an artist.
     *
     * @param artist The artist to unfollow
     * @return true if the artist was successfully unfollowed, false otherwise
     */
    public boolean unfollowArtist(Artist artist) {
        return following.remove(artist);
    }

    /**
     * Checks if the user is following a specific artist.
     *
     * @param artist The artist to check
     * @return true if the user is following the artist, false otherwise
     */
    public boolean isFollowing(Artist artist) {
        return following.contains(artist);
    }

    /**
     * Adds a notification to the user's notification list.
     *
     * @param notification The notification message
     */
    public void addNotification(String notification) {
        if (notification != null && !notification.isBlank()) {
            notifications.add(notification);
        }
    }

    /**
     * Clears all notifications for the user.
     */
    public void clearNotifications() {
        notifications.clear();
    }

    /**
     * Gets an unmodifiable list of artists the user is following.
     *
     * @return List of followed artists
     */
    public List<Artist> getFollowing() {
        return List.copyOf(following);
    }

    /**
     * Gets an unmodifiable list of user notifications.
     *
     * @return List of notifications
     */
    public List<String> getNotifications() {
        return List.copyOf(notifications);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(following, user.following) && 
               Objects.equals(notifications, user.notifications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), following, notifications);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", age=" + getAge() +
                ", email='" + getEmail() + '\'' +
                ", following=" + following.size() +
                ", notifications=" + notifications.size() +
                '}';
    }
}