package com.genius.model.accounts;

import com.genius.model.enums.Role;
import java.io.Serializable;
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String name;
    private int age;
    private String email;
    private Role role;
    
    public Account(String username, String password, String name, int age, String email, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.email = email;
        this.role = role;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }

    public void setPassword(String password) { this.password = password; }
    
    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return username.equals(account.username);
    }
}