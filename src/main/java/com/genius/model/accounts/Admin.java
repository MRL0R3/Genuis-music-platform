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