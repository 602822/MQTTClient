package org.example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KeycloakAuth {
    public static String getTokenSensor(String clientId, String clientSecret) throws IOException, InterruptedException {

        String urlString = Config.BASE_URL + "/realms/" + Config.KEYCLOAK_REALM + "/protocol/openid-connect/token"; // change to use HTTPS, can't use localhost in production

        //client credential flow used for sensors
        String data = "grant_type=client_credentials"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret;


        return requestToken(urlString, data);
    }


    private static String requestToken(String urlString, String data) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200) {
            throw new IOException("Failed to authenticate: " + response.statusCode() + " " + response.body());
        }
        JSONObject json = new JSONObject(response.body());
        return json.getString("access_token");
    }


}
