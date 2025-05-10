package com.genius.model.content;

import com.genius.model.accounts.User;
import java.util.Date;
import java.util.Objects;

public class Comment {
    private final User user;
    private final String text;
    private final Date date;
    private int likes;
    private int dislikes;

    /**
     * Constructs a new Comment with the specified user, text, and current date.
     *
     * @param user The user who made the comment
     * @param text The content of the comment
     * @param date The date when the comment was made
     * @throws IllegalArgumentException if user or text is null, or text is empty
     */
    public Comment(User user, String text, Date date) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be null or empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        this.user = user;
        this.text = text.trim();
        this.date = new Date(date.getTime()); // Defensive copy
        this.likes = 0;
        this.dislikes = 0;
    }

    /**
     * Gets the user who made the comment.
     *
     * @return The commenting user
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the comment text content.
     *
     * @return The comment text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the date when the comment was made.
     *
     * @return The comment date
     */
    public Date getDate() {
        return new Date(date.getTime()); // Defensive copy
    }

    /**
     * Gets the number of likes the comment has received.
     *
     * @return The like count
     */
    public int getLikes() {
        return likes;
    }

    /**
     * Gets the number of dislikes the comment has received.
     *
     * @return The dislike count
     */
    public int getDislikes() {
        return dislikes;
    }

    /**
     * Increments the like count for this comment.
     */
    public void addLike() {
        likes++;
    }

    /**
     * Increments the dislike count for this comment.
     */
    public void addDislike() {
        dislikes++;
    }

    /**
     * Removes a like from this comment (if count > 0).
     */
    public void removeLike() {
        if (likes > 0) {
            likes--;
        }
    }

    /**
     * Removes a dislike from this comment (if count > 0).
     */
    public void removeDislike() {
        if (dislikes > 0) {
            dislikes--;
        }
    }

    /**
     * Calculates the comment's net score (likes - dislikes).
     *
     * @return The net score
     */
    public int getScore() {
        return likes - dislikes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return likes == comment.likes &&
                dislikes == comment.dislikes &&
                user.equals(comment.user) &&
                text.equals(comment.text) &&
                date.equals(comment.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, text, date, likes, dislikes);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "user=" + user.getUsername() +
                ", date=" + date +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", text='" + text + '\'' +
                '}';
    }
}