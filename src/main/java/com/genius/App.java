package com.genius;

import com.genius.model.accounts.Admin;
import com.genius.model.accounts.Artist;
import com.genius.model.accounts.User;
import com.genius.services.*;
import com.genius.util.Database;
import com.genius.util.PasswordHasher;
import com.genius.view.CLI;

public class App {

    private static SongService songService;
    private static AlbumService albumService;
    private static AccountService accountService;
    private static AuthenticationService authService;

    public static void main(String[] args) {
        // Get API token from system environment
        String apiToken = System.getenv("GENIUS_API_TOKEN");


        if (apiToken == null || apiToken.isEmpty()) {
            System.err.println("GENIUS_API_TOKEN environment variable not set!");
            System.exit(1);
        }

        GeniusAPIService geniusAPI = new GeniusAPIService(apiToken);;
        if (!geniusAPI.testAPIConnection()) {
            System.err.println("Cannot connect to Genius API. Please check your token and internet connection.");
            System.exit(1);
        }


        try {
            // Initialize services
            Database database = new Database();
            geniusAPI = new GeniusAPIService(apiToken);
            SongService songService = new SongService(database, geniusAPI);
            AlbumService albumService = new AlbumService(database, songService);
            AccountService accountService = new AccountService(database);
            AuthenticationService authService = new AuthenticationService(database);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                database.saveData();
                System.out.println("Data saved successfully on shutdown");
            }));
            // Initialize CLI with all services
            CLI cli = new CLI(authService, database, songService,
                    albumService, accountService, geniusAPI);

            // Initialize seed data
            initializeSeedData(database, accountService);
            // Start the application
            cli.start();


        } catch (Exception e) {
            System.err.println("Startup failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }



    // Modified initializeSeedData to accept required services
    private static void initializeSeedData(Database database, AccountService accountService) {
        try {

            Admin ahmad = new Admin("ahmad", PasswordHasher.hash("123456"),
                    "System Admin", 20, "ahmadlord@genius.com");
            database.addAccount(ahmad);

            // Create admin account
            Admin admin = new Admin("admin", PasswordHasher.hash("admin123"),
                    "System Admin", 35, "admin@genius.com");
            database.addAccount(admin);

            // Create sample verified artists
            createArtist(database, "taylor_swift", "swift123",
                    "Taylor Swift", 33, "taylor@example.com",
                    true, "1421");

            // Create sample users
            createUser(database, "john_doe", "doe123",
                    "John Doe", 25, "john@example.com");

        } catch (Exception e) {
            System.err.println("Error initializing seed data: " + e.getMessage());
        }
    }

    // Helper methods for creating accounts
    private static Artist createArtist(Database database, String username, String password,
                                       String name, int age, String email,
                                       boolean verified, String geniusId) {
        Artist artist = new Artist(username, PasswordHasher.hash(password),
                name, age, email);
        artist.setVerified(verified);
        artist.setGeniusId(geniusId);
        database.addAccount(artist);
        return artist;
    }



    private static User createUser(Database database, String username, String password,
                                   String name, int age, String email) {
        User user = new User(username, PasswordHasher.hash(password),
                name, age, email);
        database.addAccount(user);
        return user;
    }


    private static void initializeDatabaseAndServices() {
        Database database = new Database();
        String apiToken = System.getenv("GENIUS_API_TOKEN");
        if (apiToken == null) {
            System.err.println("GENIUS_API_TOKEN environment variable not set!");
            System.exit(1);
        }

        GeniusAPIService geniusAPI = new GeniusAPIService(apiToken);
        songService = new SongService(database , geniusAPI);
        albumService = new AlbumService(database, songService);
        accountService = new AccountService(database);
        authService = new AuthenticationService(database);
    }



}