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


    public JsonObject searchArtists(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        // Specifically search for artists
        String url = API_BASE_URL + "/search?q=" + encodedQuery + "&type=artist&per_page=10";
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


    public JsonObject getChartSongs() throws IOException {
        // Use the correct chart endpoint
        String url = API_BASE_URL + "/charts/songs?per_page=10";
        return executeGetRequest(url);
    }
    public JsonObject search(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = API_BASE_URL + "/search?q=" + encodedQuery + "&per_page=10";
        HttpGet request = new HttpGet(url);

        return executeGetRequest(url);

    }

    private JsonObject executeGetRequest(String url) throws IOException {
        System.out.println("Making request to: " + url);  // Debug log
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + accessToken);
        request.setHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            System.out.println("Raw API response: " + jsonResponse);  // Debug log

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                throw new IOException("Empty response from API");
            }

            return jsonParser.parse(jsonResponse).getAsJsonObject();
        }
    }

    public boolean testAPIConnection() {
        try {
            String testUrl = API_BASE_URL + "/search?q=test";
            HttpGet request = new HttpGet(testUrl);
            request.setHeader("Authorization", "Bearer " + accessToken);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 401) {
                    System.err.println("Error: Invalid API token");
                    return false;
                } else if (statusCode != 200) {
                    System.err.println("API returned status: " + statusCode);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("API connection test failed: " + e.getMessage());
            return false;
        }
    }

}