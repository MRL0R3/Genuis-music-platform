package com.genius.view;

import com.genius.model.accounts.*;
import com.genius.model.enums.Genre;
import com.genius.services.*;
import com.genius.util.Database;
import com.genius.model.content.Album;
import com.genius.model.content.Comment;
import com.genius.model.content.LyricEdit;
import com.genius.model.content.Song;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CLI {
    private GeniusAPIService geniusAPI;
    private final Scanner scanner;
    private final AuthenticationService authService;
    private final Database database;
    private Account currentUser;
    private boolean running;
    private SongService songService;
    private AlbumService albumService;
    private AccountService accountService;



    public CLI(AuthenticationService authService, Database database,
               SongService songService, AlbumService albumService,
               AccountService accountService, GeniusAPIService geniusAPI)  {
        this.geniusAPI = geniusAPI;
        this.scanner = new Scanner(System.in);
        this.authService = authService;
        this.database = database;
        this.songService = songService;
        this.albumService = albumService;
        this.accountService = accountService;
        this.geniusAPI = geniusAPI;
    }


    public void start() {
        System.out.println("=== Welcome to Genius Music Platform ===");
        
        while (running) {
            if (currentUser == null) {
                showGuestMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private void showGuestMenu() {
        System.out.println("\n--- Main Menu (Guest) ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Browse Songs");
        System.out.println("4. Browse Artists");
        System.out.println("5. Top Charts");
        System.out.println("6. Search");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");

        int choice = readIntInput();
        
        switch (choice) {
            case 1 -> login();
            case 2 -> register();
            case 3 -> browseSongs();
            case 4 -> browseArtists();
            case 5 -> showTopCharts();
            case 6 -> search();
            case 0 -> running = false;
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void showUserMenu() {
        System.out.println("\n--- Main Menu (" + currentUser.getUsername() + ") ---");
        
        // Common options for all users
        System.out.println("1. Browse Songs");
        System.out.println("2. Browse Artists");
        System.out.println("3. Top Charts");
        System.out.println("4. Search");
        
        // User-specific options
        if (currentUser instanceof User) {
            System.out.println("5. My Followed Artists");
            System.out.println("6. View Notifications");
        } 
        // Artist-specific options
        else if (currentUser instanceof Artist) {
            System.out.println("5. My Artist Profile");
            System.out.println("6. Manage My Songs");
            System.out.println("7. Manage My Albums");
            System.out.println("8. View Lyric Edit Requests");
            System.out.println("9. View Notifications");
        } 
        // Admin-specific options
        else if (currentUser instanceof Admin) {
            System.out.println("5. Artist Approvals");
            System.out.println("6. View All Lyric Edit Requests");
        }
        
        System.out.println("0. Logout");
        System.out.print("Select an option: ");

        int choice = readIntInput();
        
        switch (choice) {
            case 1 -> browseSongs();
            case 2 -> browseArtists();
            case 3 -> showTopCharts();
            case 4 -> search();
            case 5 -> {
                if (currentUser instanceof User) showFollowedArtists();
                else if (currentUser instanceof Artist) showArtistProfile();
                else if (currentUser instanceof Admin) showArtistApprovals();
            }
            case 6 -> {
                if (currentUser instanceof User) showUserNotifications();
                else if (currentUser instanceof Artist) manageSongs();
                else if (currentUser instanceof Admin) showAllLyricEdits();
            }
            case 7 -> {
                if (currentUser instanceof Artist) manageAlbums();
            }
            case 8 -> {
                if (currentUser instanceof Artist) showLyricEditRequests();
            }
            case 9 -> {
                if (currentUser instanceof Artist) showArtistNotifications();
            }
            case 0 -> {
                System.out.println("Logging out...");
                currentUser = null;
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    // ========== Authentication Methods ==========
    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        currentUser = authService.login(username, password);
        
        if (currentUser != null) {
            System.out.println("Login successful! Welcome, " + currentUser.getName());
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }
    }

    private void register() {
        System.out.println("\n--- Register ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Age: ");
        int age = readIntInput();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.println("Select role:");
        System.out.println("1. Regular User");
        System.out.println("2. Artist");
        System.out.print("Choice: ");
        int roleChoice = readIntInput();
        
        String role = roleChoice == 2 ? "ARTIST" : "USER";
        
        Account newAccount = authService.register(username, password, name, age, email, role);
        
        if (newAccount != null) {
            System.out.println("Registration successful!");
            if (role.equals("ARTIST")) {
                System.out.println("Your artist account is pending admin approval.");
            }
        } else {
            System.out.println("Registration failed. Username may already exist.");
        }
    }

    // ========== Song Methods ==========
    private void browseSongs() {
        List<Song> songs = database.getSongs();
        if (songs == null || songs.isEmpty()) {

            System.out.println("No songs available.");
            return;
        }

        System.out.println("\n--- Browse Songs ---");
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            if (song != null) {
                System.out.printf("%d. %s by %s (%d views)%n",
                        i + 1,
                        song.getTitle(),
                        song.getArtists().stream()
                                .filter(Objects::nonNull)
                                .map(Artist::getName)
                                .collect(Collectors.joining(", ")),
                        song.getViews());
            }
        }

        System.out.print("Enter song number to view details (0 to go back): ");
        int choice = readIntInput();

        if (choice > 0 && choice <= songs.size()) {
            Song selectedSong = songs.get(choice - 1);
            if (selectedSong != null) {
                viewSongDetails(selectedSong);
            }
        }
    }

    private void viewSongDetails(Song song) {
        // Increment view count
        song.setViews(song.getViews() + 1);

        System.out.println("\n--- " + song.getTitle() + " ---");

        // Display basic info
        System.out.println("Artist(s): " + song.getArtists().stream()
                .filter(Objects::nonNull)
                .map(Artist::getName)
                .collect(Collectors.joining(", ")));

        System.out.println("Genre: " + (song.getGenre() != null ? song.getGenre().getDisplayName() : "Unknown"));
        System.out.println("Views: " + song.getViews());

        // Display thumbnail if available
        if (song.getThumbnailUrl() != null && !song.getThumbnailUrl().isEmpty()) {
            System.out.println("\n[Thumbnail: " + song.getThumbnailUrl() + "]");
        }

        // Display lyrics
        System.out.println("\nLyrics:\n" +
                (song.getLyrics() != null && !song.getLyrics().isEmpty()
                        ? song.getLyrics()
                        : "No lyrics available"));

        // Show comments
        System.out.println("\n--- Comments (" + song.getComments().size() + ") ---");
        if (song.getComments().isEmpty()) {
            System.out.println("No comments yet.");
        } else {
            song.getComments().sort(Comparator.comparing(Comment::getDate).reversed());
            for (int i = 0; i < song.getComments().size(); i++) {
                Comment comment = song.getComments().get(i);
                System.out.printf("%d. %s (%s): %s%n",
                        i + 1,
                        comment.getUser().getUsername(),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").format(comment.getDate()),
                        comment.getText());
            }
        }

        // Show user options
        if (currentUser != null) {
            showSongOptions(song);
        }
    }

    private void showSongOptions(Song song) {
        System.out.println("\nOptions:");
        if (currentUser instanceof User) {
            System.out.println("1. Suggest lyric edit");
            System.out.println("2. Add comment");
            if (!song.getArtists().isEmpty()) {
                Artist artist = song.getArtists().get(0);
                boolean isFollowing = isFollowing(artist);
                System.out.println("3. " + (isFollowing ? "Unfollow artist" : "Follow artist"));
            }
        }
        System.out.println("0. Go back");
        System.out.print("Select an option: ");

        int choice = readIntInput();
        switch (choice) {
            case 1 -> {
                if (currentUser instanceof User) suggestLyricEdit(song);
            }
            case 2 -> {
                if (currentUser instanceof User) addComment(song);
            }
            case 3 -> {
                if (currentUser instanceof User && !song.getArtists().isEmpty()) {
                    Artist artist = song.getArtists().get(0);
                    if (isFollowing(artist)) {
                        // Corrected unfollow call with both user and artist
                        boolean success = accountService.unfollowArtist((User) currentUser, artist);
                        if (success) {
                            System.out.println("You have unfollowed " + artist.getName());
                        } else {
                            System.out.println("Failed to unfollow artist.");
                        }
                    } else {
                        boolean success = accountService.followArtist((User) currentUser, artist);
                        if (success) {
                            System.out.println("You are now following " + artist.getName());
                        } else {
                            System.out.println("Failed to follow artist.");
                        }
                    }
                }
            }
        }
    }
    private boolean isFollowing(Artist artist) {
        return currentUser instanceof User &&
                ((User) currentUser).getFollowing().contains(artist);
    }

    private void suggestLyricEdit(Song song) {
        System.out.println("\n--- Suggest Lyric Edit ---");
        System.out.println("Current lyrics:\n" + song.getLyrics());
        System.out.print("Enter your proposed lyrics:\n");
        String proposedLyrics = readMultilineInput();
        System.out.print("Explanation for your edit: ");
        String explanation = scanner.nextLine();

        LyricEdit edit = new LyricEdit((User) currentUser, song,
                song.getLyrics(), proposedLyrics, explanation);
        database.getLyricEdits().add(edit);

        System.out.println("Your edit suggestion has been submitted for review.");

    }

    private void addComment(Song song) {
        System.out.println("\n--- Add Comment ---");
        System.out.print("Enter your comment: ");
        String commentText = scanner.nextLine();

        Comment comment = new Comment((User) currentUser, commentText, new Date());
        song.getComments().add(comment);
        database.getComments().add(comment);

        System.out.println("Comment added successfully.");
    }

    // ========== Artist Methods ==========
    private void browseArtists() {
        System.out.println("\n--- Browse Artists ---");
        List<Artist> artists = accountService.searchArtists("");
        
        if (artists.isEmpty()) {
            System.out.println("No artists available.");
            return;
        }
        
        for (int i = 0; i < artists.size(); i++) {
            Artist artist = artists.get(i);
            System.out.println((i + 1) + ". " + artist.getName() + 
                    " (" + artist.getSongs().size() + " songs)");
        }
        
        System.out.print("Enter artist number to view details (0 to go back): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= artists.size()) {
            viewArtistDetails(artists.get(choice - 1));
        }
    }

    private void viewArtistDetails(Artist artist) {
        System.out.println("\n--- " + artist.getName() + " ---");
        System.out.println("Songs: " + artist.getSongs().size());
        System.out.println("Albums: " + artist.getAlbums().size());
        
        // Show top 5 popular songs
        System.out.println("\nPopular Songs:");
        List<Song> popularSongs = artist.getSongs().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getViews(), s1.getViews()))
                .limit(5)
                .collect(Collectors.toList());
        
        for (int i = 0; i < popularSongs.size(); i++) {
            System.out.println((i + 1) + ". " + popularSongs.get(i).getTitle() + 
                    " (" + popularSongs.get(i).getViews() + " views)");
        }
        
        // User options
        if (currentUser != null && currentUser instanceof User) {
            System.out.println("\nOptions:");
            System.out.println("1. Follow artist");
            System.out.println("0. Go back");
            System.out.print("Select an option: ");
            
            int choice = readIntInput();
            if (choice == 1) {
                followArtist(artist);
            }
        }
    }

    private void followArtist(Artist artist) {
        boolean success = accountService.followArtist((User) currentUser, artist);
        if (success) {
            System.out.println("You are now following " + artist.getName());
        } else {
            System.out.println("Failed to follow artist.");
        }
    }

    private void showFollowedArtists() {
        System.out.println("\n--- Followed Artists ---");
        List<Artist> followedArtists = accountService.getFollowedArtists((User) currentUser);
        
        if (followedArtists.isEmpty()) {
            System.out.println("You're not following any artists yet.");
            return;
        }
        
        for (int i = 0; i < followedArtists.size(); i++) {
            Artist artist = followedArtists.get(i);
            System.out.println((i + 1) + ". " + artist.getName());
        }
        
        System.out.print("Enter artist number to view details (0 to go back): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= followedArtists.size()) {
            viewArtistDetails(followedArtists.get(choice - 1));
        }
    }

    // ========== Chart Methods ==========
    private void showTopCharts() {
        System.out.println("\n--- Top Charts ---");
        List<Song> topSongs = songService.getTopSongs(10);
        
        if (topSongs.isEmpty()) {
            System.out.println("No songs available.");
            return;
        }
        
        displaySongs(topSongs);
        System.out.print("Enter song number to view details (0 to go back): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= topSongs.size()) {
            viewSongDetails(topSongs.get(choice - 1));
        }
    }

    // ========== Search Methods ==========
    private void search() {
        System.out.println("\n--- Search ---");
        System.out.print("Enter search query: ");
        String query = scanner.nextLine();

        System.out.println("\nSearch Categories:");
        System.out.println("1. Songs");
        System.out.println("2. Artists");
        System.out.print("Select category to search: ");

        int category = readIntInput();
        switch (category) {
            case 1 -> {
                // Use songService which already has geniusAPI
                List<Song> songs = songService.searchSongs(query);
                if (songs.isEmpty()) {
                    System.out.println("No songs found matching your query.");
                    return;
                }

                System.out.println("\nSongs matching '" + query + "':");
                displaySongs(songs);

                System.out.print("Enter song number to view details (0 to go back): ");
                int songChoice = readIntInput();
                if (songChoice > 0 && songChoice <= songs.size()) {
                    viewSongDetails(songs.get(songChoice - 1));
                }
            }
            case 2 -> {
                try {
                    // Use songService's geniusAPI instance
                    JsonObject response = songService.getGeniusAPI().search(query);
                    JsonArray hits = response.getAsJsonObject("response").getAsJsonArray("hits");

                    List<Artist> artists = new ArrayList<>();
                    for (JsonElement hit : hits) {
                        JsonObject result = hit.getAsJsonObject().getAsJsonObject("result");
                        if (result != null && "artist".equals(result.get("type").getAsString())) {
                            String name = result.get("name").getAsString();
                            String username = name.toLowerCase().replaceAll("[^a-z0-9]", "_");

                            Artist artist = new Artist(
                                    username,
                                    "temp_pass_" + System.currentTimeMillis(),
                                    name,
                                    30,
                                    username + "@genius.com"

                            );
                            artist.setGeniusId(String.valueOf(result.get("id").getAsInt()));

                            if (result.has("image_url")) {
                                artist.setImageUrl(result.get("image_url").getAsString());
                            }

                            artist.setVerified(true);
                            artists.add(artist);
                        }
                    }

                    if (artists.isEmpty()) {
                        System.out.println("No artists found matching your query.");
                        return;
                    }

                    System.out.println("\nArtists matching '" + query + "':");
                    for (int i = 0; i < artists.size(); i++) {
                        System.out.println((i + 1) + ". " + artists.get(i).getName());
                    }

                    System.out.print("Enter artist number to view details (0 to go back): ");
                    int artistChoice = readIntInput();
                    if (artistChoice > 0 && artistChoice <= artists.size()) {
                        viewArtistDetails(artists.get(artistChoice - 1));
                    }
                } catch (Exception e) {
                    System.err.println("Error searching artists: " + e.getMessage());
                }
            }
            default -> System.out.println("Invalid category.");
        }
    }

//    private Artist createArtistFromApiResult(JsonObject artistData) {
//        try {
//            int geniusId = artistData.get("id").getAsInt();
//            String name = artistData.get("name").getAsString();
//
//            // Create a username from the artist name
//            String username = name.toLowerCase().replaceAll("[^a-z0-9]", "_");
//
//            // Create a new artist (note: password is placeholder)
//            Artist artist = new Artist(
//                    username,
//                    "temp_pass_" + System.currentTimeMillis(),
//                    name,
//                    30, // default age
//                    username + "@genius.com",
//                    String.valueOf(geniusId)
//            );
//
//            // Set artist image if available
//            if (artistData.has("image_url")) {
//                artist.setImageUrl(artistData.get("image_url").getAsString());
//            }
//
//            artist.setVerified(true); // Assume API artists are verified
//            return artist;
//        } catch (Exception e) {
//            System.err.println("Error creating artist from API data: " + e.getMessage());
//            return null;
//        }
//    }
    // ========== Artist-Specific Methods ==========
    private void showArtistProfile() {
        Artist artist = (Artist) currentUser;
        System.out.println("\n--- My Artist Profile ---");
        System.out.println("Name: " + artist.getName());
        System.out.println("Songs: " + artist.getSongs().size());
        System.out.println("Albums: " + artist.getAlbums().size());
        
        System.out.println("\nOptions:");
        System.out.println("1. Create New Song");
        System.out.println("2. Create New Album");
        System.out.println("0. Go Back");
        System.out.print("Select an option: ");
        
        int choice = readIntInput();
        switch (choice) {
            case 1 -> createNewSong();
            case 2 -> createNewAlbum();
        }
    }

    private void createNewSong() {
        System.out.println("\n--- Create New Song ---");
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.println("Lyrics (enter 'END' on a new line to finish):");
        String lyrics = readMultilineInput();

        System.out.println("Genre: ");
        for (Genre genre : Genre.values()) {
            System.out.println((genre.ordinal() + 1) + ". " + genre);
        }
        System.out.print("Select genre: ");
        int genreChoice = readIntInput();
        Genre genre = Genre.values()[genreChoice - 1];

        System.out.print("Release date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        Date releaseDate;
        try {
            releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Using current date.");
            releaseDate = new Date();
        }

        System.out.print("Genius ID (or 0 if none): ");
        Integer geniusId = readIntInput();
        if (geniusId == 0) geniusId = null;

        System.out.print("Thumbnail URL (or leave blank): ");
        String thumbnailUrl = scanner.nextLine();
        if (thumbnailUrl.isBlank()) thumbnailUrl = null;

        Song song = songService.createSong(
                title,
                lyrics,
                (Artist) currentUser,
                genre,
                releaseDate,
                geniusId,
                thumbnailUrl
        );

        if (song != null) {
            System.out.println("Song created successfully!");
        } else {
            System.out.println("Failed to create song.");
        }
    }
    private void createNewAlbum() {
        System.out.println("\n--- Create New Album ---");
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Release date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        Date releaseDate;
        try {
            releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Using current date.");
            releaseDate = new Date();
        }
        
        Album album = albumService.createAlbum(
                title, 
                (Artist) currentUser, 
                releaseDate
        );
        
        if (album != null) {
            System.out.println("Album created successfully!");
            System.out.println("Now you can add songs to this album from the Manage Songs menu.");
        } else {
            System.out.println("Failed to create album.");
        }
    }

    private void manageSongs() {
        Artist artist = (Artist) currentUser;
        System.out.println("\n--- Manage My Songs ---");
        List<Song> songs = artist.getSongs();
        
        if (songs.isEmpty()) {
            System.out.println("You haven't created any songs yet.");
            return;
        }
        
        displaySongs(songs);
        System.out.print("Enter song number to manage (0 to go back): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= songs.size()) {
            manageSong(songs.get(choice - 1));
        }
    }

    private void manageSong(Song song) {
        System.out.println("\n--- Managing: " + song.getTitle() + " ---");
        System.out.println("1. Edit Lyrics");
        System.out.println("2. Add to Album");
        System.out.println("3. View Statistics");
        System.out.println("0. Go Back");
        System.out.print("Select an option: ");
        
        int choice = readIntInput();
        switch (choice) {
            case 1 -> editSongLyrics(song);
            case 2 -> addSongToAlbum(song);
            case 3 -> viewSongStatistics(song);
        }
    }

    private void editSongLyrics(Song song) {
        System.out.println("\nCurrent lyrics:\n" + song.getLyrics());
        System.out.println("Enter new lyrics (enter 'END' on a new line to finish):");
        String newLyrics = readMultilineInput();
        
        song.setLyrics(newLyrics);
        System.out.println("Lyrics updated successfully!");
    }

    private void addSongToAlbum(Song song) {
        Artist artist = (Artist) currentUser;
        List<Album> albums = artist.getAlbums();
        
        if (albums.isEmpty()) {
            System.out.println("You don't have any albums yet.");
            return;
        }
        
        System.out.println("\nSelect an album to add this song to:");
        for (int i = 0; i < albums.size(); i++) {
            System.out.println((i + 1) + ". " + albums.get(i).getTitle());
        }
        System.out.print("Select album (0 to cancel): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= albums.size()) {
            boolean success = albumService.addSongToAlbum(
                    albums.get(choice - 1), 
                    song
            );
            if (success) {
                System.out.println("Song added to album successfully!");
            } else {
                System.out.println("Failed to add song to album.");
            }
        }
    }

    private void viewSongStatistics(Song song) {
        System.out.println("\n--- Statistics for " + song.getTitle() + " ---");
        System.out.println("Views: " + song.getViews());
        System.out.println("Comments: " + song.getComments().size());
        
        if (song.getAlbum() != null) {
            System.out.println("Album: " + song.getAlbum().getTitle());
        } else {
            System.out.println("This is a single (not part of an album)");
        }
    }

    private void manageAlbums() {
        Artist artist = (Artist) currentUser;
        System.out.println("\n--- Manage My Albums ---");
        List<Album> albums = artist.getAlbums();
        
        if (albums.isEmpty()) {
            System.out.println("You haven't created any albums yet.");
            return;
        }
        
        for (int i = 0; i < albums.size(); i++) {
            Album album = albums.get(i);
            System.out.println((i + 1) + ". " + album.getTitle() + 
                    " (" + album.getTracklist().size() + " songs)");
        }
        
        System.out.print("Enter album number to manage (0 to go back): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= albums.size()) {
            manageAlbum(albums.get(choice - 1));
        }
    }

    private void manageAlbum(Album album) {
        System.out.println("\n--- Managing Album: " + album.getTitle() + " ---");
        System.out.println("Release Date: " + album.getReleaseDate());
        System.out.println("\nTracklist:");
        List<Song> songs = album.getTracklist();
        
        if (songs.isEmpty()) {
            System.out.println("No songs in this album yet.");
        } else {
            for (int i = 0; i < songs.size(); i++) {
                System.out.println((i + 1) + ". " + songs.get(i).getTitle());
            }
        }
        
        System.out.println("\nOptions:");
        System.out.println("1. Add Song to Album");
        System.out.println("2. Remove Song from Album");
        System.out.println("0. Go Back");
        System.out.print("Select an option: ");
        
        int choice = readIntInput();
        switch (choice) {
            case 1 -> addSongToExistingAlbum(album);
            case 2 -> removeSongFromAlbum(album);
        }
    }

    private void addSongToExistingAlbum(Album album) {
        Artist artist = (Artist) currentUser;
        List<Song> availableSongs = artist.getSongs().stream()
                .filter(song -> song.getAlbum() == null)
                .collect(Collectors.toList());
        
        if (availableSongs.isEmpty()) {
            System.out.println("You don't have any songs that aren't already in an album.");
            return;
        }
        
        System.out.println("\nAvailable Songs:");
        for (int i = 0; i < availableSongs.size(); i++) {
            System.out.println((i + 1) + ". " + availableSongs.get(i).getTitle());
        }
        System.out.print("Select song to add (0 to cancel): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= availableSongs.size()) {
            boolean success = albumService.addSongToAlbum(
                    album, 
                    availableSongs.get(choice - 1)
            );
            if (success) {
                System.out.println("Song added to album successfully!");
            } else {
                System.out.println("Failed to add song to album.");
            }
        }
    }

    private void removeSongFromAlbum(Album album) {
        List<Song> songs = album.getTracklist();
        
        if (songs.isEmpty()) {
            System.out.println("This album doesn't have any songs to remove.");
            return;
        }
        
        System.out.println("\nSelect song to remove:");
        for (int i = 0; i < songs.size(); i++) {
            System.out.println((i + 1) + ". " + songs.get(i).getTitle());
        }
        System.out.print("Select song (0 to cancel): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= songs.size()) {
            songs.remove(choice - 1);
            System.out.println("Song removed from album.");
        }
    }


    private void showLyricEditRequests() {
        if (!(currentUser instanceof Artist)) {
            System.out.println("Only artists can review lyric edits.");
            return;
        }

        Artist artist = (Artist) currentUser;
        List<LyricEdit> edits = database.getLyricEdits().stream()
                .filter(edit -> edit != null && edit.getSong() != null)
                .filter(edit -> edit.getSong().getArtists().contains(artist))
                .collect(Collectors.toList());

        if (edits.isEmpty()) {
            System.out.println("\nNo pending lyric edit requests.");
            return;
        }

        System.out.println("\n--- Pending Lyric Edit Requests ---");
        for (int i = 0; i < edits.size(); i++) {
            LyricEdit edit = edits.get(i);
            System.out.printf("%d. %s (suggested by %s)%n",
                    i + 1,
                    edit.getSong().getTitle(),
                    edit.getSuggestedBy().getUsername());
            System.out.println("Current lyrics:\n" + edit.getOriginalLyrics());
            System.out.println("Proposed lyrics:\n" + edit.getProposedLyrics());
            System.out.println("Explanation: " + edit.getExplanation());
            System.out.println("----------------------------");
        }

        System.out.print("Enter edit number to review (0 to go back): ");
        int choice = readIntInput();

        if (choice > 0 && choice <= edits.size()) {
            reviewLyricEdit(edits.get(choice - 1));
        }
    }

    private void reviewLyricEdit(LyricEdit edit) {
        System.out.println("\n--- Reviewing Lyric Edit ---");
        System.out.println("Song: " + edit.getSong().getTitle());
        System.out.println("Suggested by: " + edit.getSuggestedBy().getUsername());
        System.out.println("\nCurrent lyrics:\n" + edit.getOriginalLyrics());
        System.out.println("\nProposed lyrics:\n" + edit.getProposedLyrics());
        System.out.println("\nExplanation: " + edit.getExplanation());

        System.out.println("\nOptions:");
        System.out.println("1. Approve Edit");
        System.out.println("2. Reject Edit");
        System.out.println("0. Go Back");
        System.out.print("Select an option: ");

        int choice = readIntInput();
        switch (choice)  {
            case 1 -> {
                edit.approve((Artist) currentUser);
                edit.getSong().setLyrics(edit.getProposedLyrics());
                System.out.println("Edit approved successfully!");
            }
            case 2 -> {
                System.out.print("Enter rejection reason: ");
                String reason = scanner.nextLine();
                edit.reject((Artist) currentUser, reason);
                System.out.println("Edit rejected successfully!");
            }
        }
    }
    // ========== Admin Methods ==========
    private void showArtistApprovals() {
        System.out.println("\n--- Artist Approvals ---");
        List<Artist> artists = accountService.getArtistsForApproval();
        
        if (artists.isEmpty()) {
            System.out.println("No artists pending approval.");
            return;
        }
        
        for (int i = 0; i < artists.size(); i++) {
            Artist artist = artists.get(i);
            System.out.println((i + 1) + ". " + artist.getUsername() + 
                    " - " + artist.getName() + " (" + artist.getEmail() + ")");
        }
        
        System.out.print("Enter artist number to approve (0 to go back): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= artists.size()) {
            boolean approved = accountService.verifyArtist((Admin) currentUser, artists.get(choice - 1));
            if (approved) {
                System.out.println("Artist approved successfully!");
            } else {
                System.out.println("Failed to approve artist.");
            }
        }
    }

    private void showAllLyricEdits() {
        System.out.println("\n--- All Lyric Edit Requests ---");

        // Access lyric edits directly from database instead of songService
        List<LyricEdit> edits = database.getLyricEdits();

        if (edits == null || edits.isEmpty()) {
            System.out.println("No pending lyric edit requests.");
            return;
        }

        for (int i = 0; i < edits.size(); i++) {
            LyricEdit edit = edits.get(i);
            // Add null checks for safety
            if (edit != null && edit.getSong() != null && !edit.getSong().getArtists().isEmpty()) {
                System.out.println((i + 1) + ". " + edit.getSong().getTitle() +
                        " by " + edit.getSong().getArtists().get(0).getName() +
                        " (suggested by " +
                        (edit.getSuggestedBy() != null ? edit.getSuggestedBy().getUsername() : "unknown") +
                        ")");
            }
        }

        System.out.print("Enter edit number to review (0 to go back): ");
        int choice = readIntInput();

        if (choice > 0 && choice <= edits.size()) {
            LyricEdit selectedEdit = edits.get(choice - 1);
            if (selectedEdit != null) {
                reviewLyricEditAsAdmin(selectedEdit);
            }
        }
    }
    private void reviewLyricEditAsAdmin(LyricEdit edit) {
        System.out.println("\n--- Admin Review of Lyric Edit ---");
        System.out.println("Song: " + edit.getSong().getTitle());
        System.out.println("Artist: " + edit.getSong().getArtists().get(0).getName());
        System.out.println("Suggested by: " + edit.getSuggestedBy().getUsername());
        System.out.println("\nCurrent lyrics:\n" + edit.getOriginalLyrics());
        System.out.println("\nProposed lyrics:\n" + edit.getProposedLyrics());
        System.out.println("\nExplanation: " + edit.getExplanation());
        
        System.out.println("\nOptions:");
        System.out.println("1. Approve Edit");
        System.out.println("2. Reject Edit");
        System.out.println("0. Go Back");
        System.out.print("Select an option: ");
        
        int choice = readIntInput();
        switch (choice) {
            case 1 -> {
                edit.approve((Artist) currentUser);
                edit.getSong().setLyrics(edit.getProposedLyrics());
                System.out.println("Edit approved successfully!");
            }
            case 2 -> {
                System.out.print("Enter rejection reason: ");
                String reason = scanner.nextLine();
                edit.reject((Artist) currentUser, reason);
                System.out.println("Edit rejected successfully!");
            }
        }
    }

    // ========== Notification Methods ==========
    private void showUserNotifications() {
        System.out.println("\n--- Notifications ---");
        List<String> notifications = accountService.getUserNotifications((User) currentUser);
        
        if (notifications.isEmpty()) {
            System.out.println("No new notifications.");
            return;
        }
        
        for (int i = 0; i < notifications.size(); i++) {
            System.out.println((i + 1) + ". " + notifications.get(i));
        }
    }

    private void showArtistNotifications() {
        System.out.println("\n--- Notifications ---");
        List<String> notifications = accountService.getArtistNotifications((Artist) currentUser);
        
        if (notifications.isEmpty()) {
            System.out.println("No new notifications.");
            return;
        }
        
        for (int i = 0; i < notifications.size(); i++) {
            System.out.println((i + 1) + ". " + notifications.get(i));
        }
    }

    // ========== Helper Methods ==========
    private void displaySongs(List<Song> songs) {
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            System.out.println((i + 1) + ". " + song.getTitle() + 
                    " by " + song.getArtists().stream()
                            .map(Artist::getName)
                            .collect(Collectors.joining(", ")) + 
                    " (" + song.getViews() + " views)");
        }
    }

    private int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private String readMultilineInput() {
        StringBuilder sb = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            sb.append(line).append("\n");
        }
        return sb.toString().trim();
    }

}