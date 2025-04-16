package com.genius;

import com.genius.model.accounts.Admin;
import com.genius.model.accounts.Artist;
import com.genius.model.accounts.User;
import com.genius.model.content.Album;
import com.genius.model.content.Comment;
import com.genius.model.content.LyricEdit;
import com.genius.model.content.Song;
import com.genius.model.enums.Genre;
import com.genius.services.*;
import com.genius.util.Database;
import com.genius.util.PasswordHasher;
import com.genius.view.CLI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class App {
    private static Database database;
    private static SongService songService;
    private static AlbumService albumService;
    private static AccountService accountService;
    private static AuthenticationService authService;

    public static void main(String[] args) {
        String apiToken = System.getenv("GENIUS_API_TOKEN");
        if (apiToken == null || apiToken.isEmpty()) {
            System.err.println("GENIUS_API_TOKEN environment variable not set!");
            System.exit(1);
        }


        database = new Database();

        GeniusAPIService geniusAPI = new GeniusAPIService(apiToken);
        SongService songService = new SongService(database, geniusAPI);
        AlbumService albumService = new AlbumService(database, songService);
        AccountService accountService = new AccountService(database);
        AuthenticationService authService = new AuthenticationService(database);
        CLI cli = new CLI(authService, database, songService, albumService, accountService, geniusAPI);
        cli.start();

        try {
            initializeDatabaseAndServices(); // Move initialization here
            initializeSeedData();
            cli.start();
        } catch (Exception e) {
            System.err.println("Startup failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // This causes exit value 1
        }


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

    private static void initializeSeedData() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Create accounts
            createAccounts(dateFormat);

            // Create songs
            createSongs(dateFormat);

            // Create albums
            createAlbums(dateFormat);

            // Create comments
            createComments();

            // Create lyric edits
            createLyricEdits();

            // Create follow relationships
            createFollowRelationships();

            // Set view counts
            setViewCounts();

            System.out.println("Seed data initialized successfully!");
        } catch (ParseException e) {
            System.err.println("Error initializing seed data: " + e.getMessage());
        }
    }

    private static void createAccounts(SimpleDateFormat dateFormat) {
        // Admin
        Admin admin = new Admin("admin", PasswordHasher.hash("admin123"),
                "System Admin", 35, "admin@genius.com");
        database.addAccount(admin);

        // Verified Artists
        Artist taylorSwift = createArtist("taylor_swift", "swift123", "Taylor Swift", 33,
                "taylor@example.com", true);
        Artist weeknd = createArtist("the_weeknd", "weeknd123", "The Weeknd", 33,
                "weeknd@example.com", true);
        Artist edSheeran = createArtist("ed_sheeran", "sheeran123", "Ed Sheeran", 32,
                "ed@example.com", true);
        Artist billieEilish = createArtist("billie_eilish", "eilish123", "Billie Eilish", 21,
                "billie@example.com", true);

        // Artist pending approval
        Artist newArtist = createArtist("new_artist", "artist123", "New Artist", 25,
                "new@example.com", false);

        // Regular Users
        User johnDoe = createUser("john_doe", "doe123", "John Doe", 25,
                "john@example.com");
        User janeSmith = createUser("jane_smith", "smith123", "Jane Smith", 28,
                "jane@example.com");
        User musicFan = createUser("music_fan", "fan123", "Music Fan", 22,
                "fan@example.com");
    }

    private static void createSongs(SimpleDateFormat dateFormat) throws ParseException {
        Artist taylorSwift = (Artist) database.getAccountByUsername("taylor_swift");
        Artist weekend = (Artist) database.getAccountByUsername("the_weekend");

        // Taylor Swift songs
        createSong("Blank Space",
                "Nice to meet you, where you been?...",
                taylorSwift,
                Genre.POP,
                dateFormat.parse("2014-11-10"),
                1, // geniusId
                "https://example.com/blank_space.jpg"); // thumbnailUrl

        createSong("Love Story",
                "We were both young when I first saw you...",
                taylorSwift,
                Genre.COUNTRY_POP,
                dateFormat.parse("2008-09-12"),
                2,
                "https://example.com/love_story.jpg");

        // The Weeknd songs
        createSong("Blinding Lights",
                "I've been tryna call...",
                weekend,
                Genre.POP,
                dateFormat.parse("2019-11-29"),
                3,
                "https://upload.wikimedia.org/wikipedia/en/e/e6/The_Weeknd_-_Blinding_Lights.png");
    }

    private static void createAlbums(SimpleDateFormat dateFormat) throws ParseException {
        Artist taylorSwift = (Artist) database.getAccountByUsername("taylor_swift");
        Artist weeknd = (Artist) database.getAccountByUsername("the_weeknd");
        Artist edSheeran = (Artist) database.getAccountByUsername("ed_sheeran");
        Artist billieEilish = (Artist) database.getAccountByUsername("billie_eilish");

        // Taylor Swift's "1989"
        Album album1989 = createAlbum("1989", taylorSwift, dateFormat.parse("2014-10-27"));
        albumService.addSongToAlbum(album1989, (Song) database.getSongs().stream()
                .filter(s -> s.getTitle().equals("Blank Space"))
                .findFirst().orElse(null));

        // The Weeknd's "After Hours"
        Album afterHours = createAlbum("After Hours", weeknd, dateFormat.parse("2020-03-20"));
        albumService.addSongToAlbum(afterHours, (Song) database.getSongs().stream()
                .filter(s -> s.getTitle().equals("Blinding Lights"))
                .findFirst().orElse(null));

        // Ed Sheeran's "÷ (Divide)"
        Album divide = createAlbum("÷ (Divide)", edSheeran, dateFormat.parse("2017-03-03"));
        albumService.addSongToAlbum(divide, (Song) database.getSongs().stream()
                .filter(s -> s.getTitle().equals("Shape of You"))
                .findFirst().orElse(null));
        albumService.addSongToAlbum(divide, (Song) database.getSongs().stream()
                .filter(s -> s.getTitle().equals("Perfect"))
                .findFirst().orElse(null));

        // Billie Eilish's "When We All Fall Asleep, Where Do We Go?"
        Album wwafawdwg = createAlbum("When We All Fall Asleep, Where Do We Go?",
                billieEilish, dateFormat.parse("2019-03-29"));
        albumService.addSongToAlbum(wwafawdwg, (Song) database.getSongs().stream()
                .filter(s -> s.getTitle().equals("bad guy"))
                .findFirst().orElse(null));
    }

    private static void createComments() {
        User johnDoe = (User) database.getAccountByUsername("john_doe");
        User janeSmith = (User) database.getAccountByUsername("jane_smith");
        User musicFan = (User) database.getAccountByUsername("music_fan");

        Song blankSpace = database.getSongs().stream()
                .filter(s -> s.getTitle().equals("Blank Space"))
                .findFirst().orElse(null);
        Song blindingLights = database.getSongs().stream()
                .filter(s -> s.getTitle().equals("Blinding Lights"))
                .findFirst().orElse(null);
        Song shapeOfYou = database.getSongs().stream()
                .filter(s -> s.getTitle().equals("Shape of You"))
                .findFirst().orElse(null);
        Song badGuy = database.getSongs().stream()
                .filter(s -> s.getTitle().equals("bad guy"))
                .findFirst().orElse(null);

        createComment(blankSpace, johnDoe, "This song is amazing! The lyrics are so clever.");
        createComment(blindingLights, janeSmith, "Can't stop listening to this track!");
        createComment(shapeOfYou, musicFan, "The beat is so catchy!");
        createComment(badGuy, johnDoe, "Billie's voice is hauntingly beautiful.");
    }

    private static void createLyricEdits() {
        User musicFan = (User) database.getAccountByUsername("music_fan");
        User janeSmith = (User) database.getAccountByUsername("jane_smith");

        Song blankSpace = database.getSongs().stream()
                .filter(s -> s.getTitle().equals("Blank Space"))
                .findFirst().orElse(null);
        Song blindingLights = database.getSongs().stream()
                .filter(s -> s.getTitle().equals("Blinding Lights"))
                .findFirst().orElse(null);

        createLyricEdit(blankSpace, musicFan,
                "Magic, madness, heaven, sin",
                "Magic, madness, heaven sent",
                "I think it's 'heaven sent' based on live performances");

        createLyricEdit(blindingLights, janeSmith,
                "I've been tryna call",
                "I've been trying to call",
                "Correcting grammar");
    }

    private static void createFollowRelationships() {
        User johnDoe = (User) database.getAccountByUsername("john_doe");
        User janeSmith = (User) database.getAccountByUsername("jane_smith");
        User musicFan = (User) database.getAccountByUsername("music_fan");

        Artist taylorSwift = (Artist) database.getAccountByUsername("taylor_swift");
        Artist weeknd = (Artist) database.getAccountByUsername("the_weeknd");
        Artist billieEilish = (Artist) database.getAccountByUsername("billie_eilish");
        Artist edSheeran = (Artist) database.getAccountByUsername("ed_sheeran");

        accountService.followArtist(johnDoe, taylorSwift);
        accountService.followArtist(johnDoe, weeknd);
        accountService.followArtist(janeSmith, billieEilish);
        accountService.followArtist(musicFan, edSheeran);
        accountService.followArtist(musicFan, taylorSwift);
    }

    private static void setViewCounts() {
        database.getSongs().forEach(song -> {
            switch (song.getTitle()) {
                case "Blank Space" -> song.setViews(1500000);
                case "Love Story" -> song.setViews(2500000);
                case "Blinding Lights" -> song.setViews(3000000);
                case "Starboy" -> song.setViews(1800000);
                case "Shape of You" ->  {
                    song.setViews(3500000);
                }
                case "Perfect" -> song.setViews(2200000);
                case "bad guy" -> song.setViews(2800000);
                case "Ocean Eyes" -> song.setViews(1200000);
            }
        });
    }

    // Helper methods
    private static Artist createArtist(String username, String password, String name,
                                       int age, String email, boolean verified) {
        Artist artist = new Artist(username, PasswordHasher.hash(password), name, age, email);
        artist.setVerified(verified);
        database.addAccount(artist);
        if (!verified) {
            database.getArtistsForApproval().add(artist);
        }
        return artist;
    }

    private static User createUser(String username, String password, String name,
                                   int age, String email) {
        User user = new User(username, PasswordHasher.hash(password), name, age, email);
        database.addAccount(user);
        return user;
    }

    private static Song createSong(String title, String lyrics, Artist artist,
                                   Genre genre, Date releaseDate, Integer geniusId,
                                   String thumbnailUrl) {
        Objects.requireNonNull(artist, "Artist cannot be null");

        Song song = new Song(
                title,
                lyrics,
                Collections.singletonList(artist),
                genre,
                releaseDate,
                geniusId,
                thumbnailUrl
        );

        database.getSongs().add(song);
        artist.getSongs().add(song);
        return song;
    }
    private static Album createAlbum(String title, Artist artist, Date releaseDate) {
        Album album = new Album(title, artist, releaseDate);
        database.getAlbums().add(album);
        artist.getAlbums().add(album);
        return album;
    }

    private static void createComment(Song song, User user, String text) {
        if (song != null && user != null) {
            Comment comment = new Comment(user, text, new Date());
            song.getComments().add(comment);
            database.getComments().add(comment);
        }
    }

    private static void createLyricEdit(Song song, User user, String original,
                                        String proposed, String explanation) {
        if (song != null && user != null) {
            LyricEdit edit = new LyricEdit(user, song, original, proposed, explanation);
            database.getLyricEdits().add(edit);
        }
    }
}