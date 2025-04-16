package com.genius.model.content;

import com.genius.model.accounts.Artist;
import com.genius.model.accounts.User;
import java.util.Date;

public class LyricEdit {
    private User suggestedBy;
    private Song song;
    private String originalLyrics;
    private String proposedLyrics;
    private String explanation;
    private Date suggestedDate;
    private boolean approved;
    private boolean rejected;
    private Artist reviewedBy;
    private String rejectionReason;

    public LyricEdit(User suggestedBy, Song song, String originalLyrics, 
                    String proposedLyrics, String explanation) {
        this.suggestedBy = suggestedBy;
        this.song = song;
        this.originalLyrics = originalLyrics;
        this.proposedLyrics = proposedLyrics;
        this.explanation = explanation;
        this.suggestedDate = new Date();
        this.approved = false;
        this.rejected = false;
    }

    public void approve(Artist artist) {
        this.approved = true;
        this.reviewedBy = artist;
    }

    public void reject(Artist artist, String reason) {
        this.rejected = true;
        this.reviewedBy = artist;
        this.rejectionReason = reason;
    }

    // Getters
    public User getSuggestedBy() { return suggestedBy; }
    public Song getSong() { return song; }
    public String getOriginalLyrics() { return originalLyrics; }
    public String getProposedLyrics() { return proposedLyrics; }
    public String getExplanation() { return explanation; }
    public Date getSuggestedDate() { return suggestedDate; }
    public boolean isApproved() { return approved; }
    public boolean isRejected() { return rejected; }
    public Artist getReviewedBy() { return reviewedBy; }
    public String getRejectionReason() { return rejectionReason; }
}