package com.genius.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Service for interacting with the Genius API
 */

public class GeniusAPIService {


    private static final String API_BASE_URL = "https://api.genius.com";
    private final CloseableHttpClient httpClient;
    private final String accessToken;
    private final JsonParser jsonParser;

    public GeniusAPIService(String accessToken) {
        this.accessToken = accessToken;
        this.httpClient = HttpClients.createDefault();
        this.jsonParser = new JsonParser();
    }


    /**
     * Search for songs on Genius
     * @param query Search term
     * @return JsonObject containing search results
     * @throws IOException If API request fails
     */

    public JsonObject search(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = API_BASE_URL + "/search?q=" + encodedQuery;
        return executeGetRequest(url);
    }

    public JsonObject searchArtists(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = API_BASE_URL + "/search?q=" + encodedQuery;
        return executeGetRequest(url);
    }

    /**
     * Get detailed information about a specific song
     * @param songId Genius song ID
     * @return JsonObject containing song details
     * @throws IOException If API request fails
     */

    public JsonObject getSongDetails(int songId) throws IOException {
        String url = API_BASE_URL + "/songs/" + songId;
        return executeGetRequest(url);
    }

    /**
     * Get artist information
     * @param artistId Genius artist ID
     * @return JsonObject containing artist details
     * @throws IOException If API request fails
     */

    public JsonObject getArtistDetails(int artistId) throws IOException {
        String url = API_BASE_URL + "/artists/" + artistId;
        return executeGetRequest(url);
    }
    public JsonObject getArtistSongs(int artistId) throws IOException {
        String url = API_BASE_URL + "/artists/" + artistId + "/songs?sort=popularity&per_page=50";
        return executeGetRequest(url);
    }

    public String getLyrics(String path) throws IOException {
        String lyricsUrl = "https://genius.com" + path;
        Document doc = Jsoup.connect(lyricsUrl).get();

        return doc.select("div[data-lyrics-container=true]").stream()
                .map(e -> e.html()
                        .replaceAll("<[^>]*>", "")
                        .replaceAll("\\[.*?\\]", "")
                        .trim())
                .filter(line -> !line.isEmpty())
                .collect(Collectors.joining("\n\n"));
    }

    private JsonObject executeGetRequest(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + accessToken);
        request.setHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            return jsonParser.parse(jsonResponse).getAsJsonObject();
        }
    }

    /**
     * Close HTTP client resources
     */

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            System.err.println("Error closing HTTP client: " + e.getMessage());
        }
    }
}