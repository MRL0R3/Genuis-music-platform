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
        // Get API token from environment
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

//    private static void initializeSeedData() {
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//            // Create accounts
//            createAccounts(dateFormat);
//
//            // Create songs
//            createSongs(dateFormat);
//
//            // Create albums
//            createAlbums(dateFormat);
//
//            // Create comments
//            createComments();
//
//            // Create lyric edits
//            createLyricEdits();
//
//            // Create follow relationships
//            createFollowRelationships();
//
//            // Set view counts
//            setViewCounts();
//
//            System.out.println("Seed data initialized successfully!");
//        } catch (ParseException e) {
//            System.err.println("Error initializing seed data: " + e.getMessage());
//        }
//    }

//    private static void createAccounts(SimpleDateFormat dateFormat) {
//        // Admin
//        Admin admin = new Admin("admin", PasswordHasher.hash("admin123"),
//                "System Admin", 35, "admin@genius.com");
//        database.addAccount(admin);
//
//        // Verified Artists
//        Artist taylorSwift = createArtist("taylor_swift", "swift123", "Taylor Swift", 33,
//                "taylor@example.com", true);
//        Artist weeknd = createArtist("the_weeknd", "weeknd123", "The Weeknd", 33,
//                "weeknd@example.com", true);
//        Artist edSheeran = createArtist("ed_sheeran", "sheeran123", "Ed Sheeran", 32,
//                "ed@example.com", true);
//        Artist billieEilish = createArtist("billie_eilish", "eilish123", "Billie Eilish", 21,
//                "billie@example.com", true);
//
//        // Artist pending approval
//        Artist newArtist = createArtist("new_artist", "artist123", "New Artist", 25,
//                "new@example.com", false);
//
//        // Regular Users
//        User johnDoe = createUser("john_doe", "doe123", "John Doe", 25,
//                "john@example.com");
//        User janeSmith = createUser("jane_smith", "smith123", "Jane Smith", 28,
//                "jane@example.com");
//        User musicFan = createUser("music_fan", "fan123", "Music Fan", 22,
//                "fan@example.com");
//    }




}