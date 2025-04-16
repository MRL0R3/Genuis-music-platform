package com.genius.model.enums;

/**
 * Enum representing the different user roles in the system.
 * Each role has specific permissions and access levels.
 */
public enum Role {
    /**
     * Regular user role with basic permissions:
     * - View songs and lyrics
     * - Suggest lyric edits
     * - Comment on songs
     * - Follow artists
     */
    USER("Regular User"),

    /**
     * Artist role with content creation permissions:
     * - All USER permissions
     * - Create and edit own songs
     * - Create albums
     * - Approve/reject lyric edits for own songs
     */
    ARTIST("Content Creator"),

    /**
     * Administrator role with system management permissions:
     * - All ARTIST permissions
     * - Verify new artists
     * - Moderate content
     * - Manage all user accounts
     */
    ADMIN("System Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the user-friendly display name of the role
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a string into a Role enum value (case-insensitive)
     * @param roleString The string to parse
     * @return The corresponding Role, or null if no match
     */
    public static Role fromString(String roleString) {
        if (roleString == null) {
            return null;
        }
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(roleString)) {
                return role;
            }
        }
        return null;
    }
}