package com.genius.services;

import com.genius.model.accounts.Account;
import com.genius.model.accounts.Admin;
import com.genius.model.accounts.Artist;
import com.genius.model.accounts.User;
import com.genius.util.Database;
import com.genius.util.PasswordHasher;

public class AuthenticationService {
    private Database database;
    
    public AuthenticationService(Database database) {
        this.database = database;
    }
    
    public Account register(String username, String password, String name, 
                           int age, String email, String role) {
        if (database.getAccountByUsername(username) != null) {
            return null; // Username already exists
        }
        
        String hashedPassword = PasswordHasher.hash(password);
        Account newAccount;
        
        switch (role.toUpperCase()) {
            case "ADMIN":
                newAccount = new Admin(username, hashedPassword, name, age, email);
                break;
            case "ARTIST":
                newAccount = new Artist(username, hashedPassword, name, age, email);
                // Artist needs admin approval
                database.addArtistForApproval((Artist) newAccount);
                return newAccount;
            default:
                newAccount = new User(username, hashedPassword, name, age, email);
        }
        
        database.addAccount(newAccount);
        return newAccount;
    }
    
    public Account login(String username, String password) {
        Account account = database.getAccountByUsername(username);
        if (account != null && PasswordHasher.verify(password, account.getPassword())) {
            // Additional check for artist verification
            if (account instanceof Artist && !((Artist) account).isVerified()) {
                return null; // Artist not verified yet
            }
            return account;
        }
        return null;
    }
}