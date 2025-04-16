package com.genius.services;

import com.genius.model.accounts.Account;
import com.genius.model.accounts.Artist;
import com.genius.model.content.Song;
import com.genius.model.enums.Genre;
import com.genius.util.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SongService {
    private final Database database;
    private final GeniusAPIService geniusAPI;
    private final Map<Integer, Song> geniusIdToSongMap;


    public SongService(Database database, GeniusAPIService geniusAPI) {
        this.database = database;
        this.geniusAPI = geniusAPI;
        this.geniusIdToSongMap = new HashMap<>();
    }

    private void initializeCache() {
        synchronized (database) {
            database.getSongs().stream()
                    .filter(song -> song.getGeniusId() != null)
                    .forEach(song -> geniusIdToSongMap.put(song.getGeniusId(), song));
        }
    }

    public void importSongsFromGenius(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            System.out.println("Search query cannot be empty");
            return;
        }

        try {
            System.out.println("\nSearching Genius for: " + searchQuery);

            JsonObject response = geniusAPI.search(searchQuery);
            if (response == null || !response.has("response")) {
                System.out.println("Invalid API response");
                return;
            }

            JsonArray hits = response.getAsJsonObject("response").getAsJsonArray("hits");
            if (hits == null || hits.size() == 0) {
                System.out.println("No songs found for your search.");
                return;
            }

            System.out.println("Found " + hits.size() + " results. Importing...");

            for (JsonElement hit : hits) {
                if (hit == null || !hit.isJsonObject()) continue;

                JsonObject songData = hit.getAsJsonObject().getAsJsonObject("result");
                if (songData == null) continue;

                processSongData(songData);
            }

            System.out.println("Import completed!\n");
        } catch (Exception e) {
            System.err.println("Error during import: " + e.getMessage());
        }
    }

    private void processSongData(JsonObject songData) {
        try {

            Integer geniusId = songData.get("id").getAsInt();

            // Skip if already exists
            if (geniusIdToSongMap.containsKey(geniusId)) {
                System.out.println("✓ " + songData.get("title").getAsString() + " already exists");
                return;
            }

            String title = getStringOrEmpty(songData, "title");
            String artistName = getStringOrEmpty(songData.getAsJsonObject("primary_artist"), "name");
            String thumbnailUrl = getStringOrEmpty(songData.getAsJsonObject("song_art_image_url"), "thumbnail");

            Artist artist = findOrCreateArtist(artistName);
            Song song = new Song(
                    title,
                    "Lyrics loading...", // Placeholder
                    List.of(artist),
                    determineGenre(songData),
                    new Date(), // Current date as plawceholder
                    geniusId,
                    thumbnailUrl
            );

            synchronized (database) {
                database.addSong(song);
                artist.getSongs().add(song);
                geniusIdToSongMap.put(geniusId, song);
            }

            System.out.println("✓ Added " + title + " by " + artistName);

            // Fetch lyrics in background
            executorService.submit(() -> fetchAndStoreLyrics(song));
        } catch (Exception e) {
            System.err.println("Error processing song data: " + e.getMessage());
        }
    }
    public Song createSong(String title, String lyrics, Artist artist,
                           Genre genre, Date releaseDate, Integer geniusId,
                           String thumbnailUrl) {
        if (title == null || artist == null || genre == null || releaseDate == null) {
            throw new IllegalArgumentException("Required song parameters cannot be null");
        }

        Song song = new Song(title, lyrics, List.of(artist), genre, releaseDate,
                geniusId, thumbnailUrl);
        database.getSongs().add(song);
        artist.getSongs().add(song);
        return song;
    }
    private String getStringOrEmpty(JsonObject obj, String key) {
        if (obj == null || !obj.has(key)) return "";
        return obj.get(key).getAsString();
    }

    private Genre determineGenre(JsonObject songData) {
        try {
            if (songData.has("tags") && songData.get("tags").isJsonArray()) {
                JsonArray tags = songData.getAsJsonArray("tags");
                for (JsonElement tag : tags) {
                    String tagName = tag.getAsString().toLowerCase();
                    if (tagName.contains("hip-hop")) return Genre.HIP_HOP;
                    if (tagName.contains("rock")) return Genre.ROCK;
                    if (tagName.contains("pop")) return Genre.POP;
                    if (tagName.contains("r&b")) return Genre.RNB;
                }
            }
        } catch (Exception e) {
            System.err.println("Error determining genre: " + e.getMessage());
        }
        return Genre.POP; // Default
    }

    private void fetchAndStoreLyrics(Song song) {
        try {
            System.out.println("Fetching lyrics for: " + song.getTitle());
            String lyrics = geniusAPI.getLyrics(song.getGeniusId());
            synchronized (song) {
                song.setLyrics(lyrics != null ? lyrics : "Lyrics not available");
            }
            System.out.println("✓ Lyrics loaded for " + song.getTitle());
        } catch (Exception e) {
            System.err.println("Failed to get lyrics for " + song.getTitle() + ": " + e.getMessage());
            synchronized (song) {
                song.setLyrics("Could not load lyrics: " + e.getMessage());
            }
        }
    }

    private Artist findOrCreateArtist(String name) {
        if (name == null || name.trim().isEmpty()) {
            name = "Unknown Artist";
        }

        synchronized (database) {
            String finalName = name;
            String finalName1 = name;
            return database.getAccounts().stream()
                    .filter(Account.class::isInstance)
                    .map(Account.class::cast)
                    .filter(a -> a instanceof Artist)
                    .map(a -> (Artist) a)
                    .filter(a -> finalName1.equalsIgnoreCase(a.getName()))
                    .findFirst()
                    .orElseGet(() -> createNewArtist(finalName));
        }
    }

    private Artist createNewArtist(String name) {
        String username = name.toLowerCase().replaceAll("[^a-z0-9]", "_");
        Artist artist = new Artist(
                username,
                "temp_pass_" + System.currentTimeMillis(),
                name,
                30, // Default age
                username + "@genius.com"
        );
        artist.setVerified(true);

        synchronized (database) {
            database.addAccount(artist);
        }
        return artist;
    }

    // Search helper methods
    private boolean matchesQuery(Song song, String query) {
        if (query == null || query.isEmpty()) return true;
        String lowerQuery = query.toLowerCase();
        return (song.getTitle() != null && song.getTitle().toLowerCase().contains(lowerQuery)) ||
                (song.getLyrics() != null && song.getLyrics().toLowerCase().contains(lowerQuery));
    }

    private boolean matchesGenre(Song song, Genre genre) {
        return genre == null || song.getGenre() == genre;
    }

    private boolean matchesArtist(Song song, Artist artist) {
        return artist == null ||
                (song.getArtists() != null && song.getArtists().contains(artist));
    }

    public List<Song> searchSongs(String query) {
        try {
            JsonObject response = geniusAPI.search(query);
            JsonArray hits = response.getAsJsonObject("response").getAsJsonArray("hits");

            List<Song> results = new ArrayList<>();
            for (JsonElement hit : hits) {
                JsonObject result = hit.getAsJsonObject().getAsJsonObject("result");
                if (result != null && "song".equals(result.get("type").getAsString())) {
                    Song song = createSongFromApiResult(result);
                    if (song != null) {
                        results.add(song);
                    }
                }
            }
            return results;
        } catch (Exception e) {
            System.err.println("Error searching songs: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    private Song createSongFromApiResult(JsonObject songData) {
        try {
            int geniusId = songData.get("id").getAsInt();

            // Check if we already have this song
            if (geniusIdToSongMap.containsKey(geniusId)) {
                return geniusIdToSongMap.get(geniusId);
            }

            String title = songData.get("title").getAsString();
            String path = songData.get("path").getAsString();

            // Get primary artist
            JsonObject primaryArtist = songData.getAsJsonObject("primary_artist");
            String artistName = primaryArtist.get("name").getAsString();

            // Create or get artist
            Artist artist = findOrCreateArtist(artistName);

            // Get thumbnail URL if available
            String thumbnailUrl = null;
            if (songData.has("song_art_image_url")) {
                thumbnailUrl = songData.getAsJsonObject("song_art_image_url").get("thumbnail").getAsString();
            }

            // Create song with placeholder lyrics
            Song song = new Song(
                    title,
                    "Loading lyrics...", // Will be loaded separately
                    Collections.singletonList(artist),
                    determineGenre(songData),
                    new Date(), // Placeholder release date
                    geniusId,
                    thumbnailUrl
            );

            // Load lyrics in background
            new Thread(() -> {
                try {
                    String lyrics = geniusAPI.getLyrics(path);
                    song.setLyrics(lyrics);
                } catch (Exception e) {
                    song.setLyrics("Could not load lyrics");
                }
            }).start();

            // Cache the song
            geniusIdToSongMap.put(geniusId, song);
            database.addSong(song);
            artist.addSong(song);

            return song;
        } catch (Exception e) {
            System.err.println("Error creating song from API data: " + e.getMessage());
            return null;
        }
    }


    public void addViewToSong(Song song) {
        if (song != null) {
            synchronized (song) {
                song.setViews(song.getViews() + 1);
            }
        }
    }


    public List<Song> getTopSongs(int limit) {
        synchronized (database) {
            return database.getSongs().stream()
                    .sorted(Comparator.comparingInt(Song::getViews).reversed())
                    .limit(Math.max(limit, 0))
                    .collect(Collectors.toList());
        }
    }
    public GeniusAPIService getGeniusAPI() {
        return this.geniusAPI;
    }

}