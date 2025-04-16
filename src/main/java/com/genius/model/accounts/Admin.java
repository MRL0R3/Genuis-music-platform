package com.genius.model.accounts;

import com.genius.model.enums.Role;
import java.util.Objects;

/**
 * Represents an administrator account in the Genius music platform.
 * Admins have special privileges to manage the platform and moderate content.
 */
public class Admin extends Account {
    private String adminLevel;
    private String department;

    /**
     * Constructs a new Admin with the specified details and default admin level.
     *
     * @param username The unique username
     * @param password The hashed password
     * @param name     The admin's full name
     * @param age      The admin's age
     * @param email    The admin's email address
     */
    public Admin(String username, String password, String name, int age, String email) {
        super(username, password, name, age, email, Role.ADMIN);
        this.adminLevel = "Standard";
        this.department = "Platform Management";
    }

    /**
     * Constructs a new Admin with custom admin level and department.
     *
     * @param username    The unique username
     * @param password    The hashed password
     * @param name        The admin's full name
     * @param age         The admin's age
     * @param email       The admin's email address
     * @param adminLevel  The admin's privilege level
     * @param department  The department the admin belongs to
     */
    public Admin(String username, String password, String name, int age, String email,
                 String adminLevel, String department) {
        super(username, password, name, age, email, Role.ADMIN);
        this.adminLevel = adminLevel;
        this.department = department;
    }

    /**
     * Gets the admin's privilege level.
     *
     * @return The admin level
     */
    public String getAdminLevel() {
        return adminLevel;
    }

    /**
     * Sets the admin's privilege level.
     *
     * @param adminLevel The new admin level
     * @throws IllegalArgumentException if adminLevel is null or empty
     */
    public void setAdminLevel(String adminLevel) {
        if (adminLevel == null || adminLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin level cannot be null or empty");
        }
        this.adminLevel = adminLevel.trim();
    }

    /**
     * Gets the department the admin belongs to.
     *
     * @return The department name
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the admin's department.
     *
     * @param department The new department
     * @throws IllegalArgumentException if department is null or empty
     */
    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be null or empty");
        }
        this.department = department.trim();
    }

    /**
     * Approves an artist account, granting them verified status.
     *
     * @param artist The artist to approve
     * @return true if the artist was successfully approved, false otherwise
     */
    public boolean approveArtist(Artist artist) {
        if (artist == null) {
            return false;
        }
        artist.setVerified(true);
        return true;
    }

    /**
     * Rejects an artist account, preventing them from posting content.
     *
     * @param artist The artist to reject
     * @param reason The reason for rejection
     * @return true if the artist was successfully rejected, false otherwise
     */
    public boolean rejectArtist(Artist artist, String reason) {
        if (artist == null || reason == null || reason.trim().isEmpty()) {
            return false;
        }
        artist.setVerified(false);
        // In a real system, we would store the rejection reason
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Admin admin = (Admin) o;
        return Objects.equals(adminLevel, admin.adminLevel) &&
                Objects.equals(department, admin.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adminLevel, department);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", age=" + getAge() +
                ", email='" + getEmail() + '\'' +
                ", adminLevel='" + adminLevel + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}