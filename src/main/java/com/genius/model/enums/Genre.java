package com.genius.model.enums;

/**
 * Enum representing musical genres for songs.
 * Includes both broad categories and more specific sub-genres.
 */
public enum Genre {
    POP("Pop"),
    ROCK("Rock"),
    HIP_HOP("Hip Hop"),
    RNB("R&B"),
    COUNTRY("Country"),
    JAZZ("Jazz"),
    BLUES("Blues"),
    CLASSICAL("Classical"),
    ELECTRONIC("Electronic"),
    DANCE("Dance"),
    INDIE("Indie"),
    ALTERNATIVE("Alternative"),
    METAL("Metal"),
    PUNK("Punk"),
    FOLK("Folk"),
    SOUL("Soul"),
    FUNK("Funk"),
    REGGAE("Reggae"),
    LATIN("Latin"),
    K_POP("K-Pop"),
    COUNTRY_POP("Country Pop"),
    POP_ROCK("Pop Rock"),
    SYNTHPOP("Synthpop"),
    INDIE_POP("Indie Pop"),
    INDIE_ROCK("Indie Rock"),
    ALTERNATIVE_ROCK("Alternative Rock"),
    RAP("Rap"),
    TRAP("Trap"),
    DRILL("Drill"),
    LO_FI("Lo-Fi"),
    HOUSE("House"),
    TECHNO("Techno"),
    TRANCE("Trance"),
    DUBSTEP("Dubstep"),
    DRUM_AND_BASS("Drum and Bass"),
    GOSPEL("Gospel"),
    CHRISTIAN("Christian"),
    NEW_AGE("New Age"),
    WORLD("World"),
    SOUNDTRACK("Soundtrack"),
    AMBIENT("Ambient"),
    EXPERIMENTAL("Experimental"),
    OTHER("Other");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the user-friendly display name of the genre
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a string into a Genre enum value (case-insensitive, ignores special chars)
     * @param genreString The string to parse
     * @return The corresponding Genre, or null if no match
     */
    public static Genre fromString(String genreString) {
        if (genreString == null) {
            return null;
        }

        // Normalize the input string
        String normalizedInput = genreString.trim()
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "_");

        for (Genre genre : Genre.values()) {
            if (genre.name().equals(normalizedInput)) {
                return genre;
            }
            // Also check display names
            if (genre.getDisplayName().equalsIgnoreCase(genreString)) {
                return genre;
            }
        }
        return null;
    }

    /**
     * Gets all genre display names as an array
     * @return Array of all genre display names
     */
    public static String[] getAllDisplayNames() {
        Genre[] genres = values();
        String[] displayNames = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            displayNames[i] = genres[i].getDisplayName();
        }
        return displayNames;
    }
}