package org.example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class KeycloakAuth {
    public static String getToken(String realm, String clientId, String username, String password) throws IOException {
        String urlString = "http://localhost:8080/realms/" + realm + "/protocol/openid-connect/token"; // change to use HTTPS
        URL url = URI.create(urlString).toURL();
        String data = "grant_type=password"
                + "&client_id=" + clientId
                + "&username=" + username
                + "&password=" + password;

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString());
        return json.getString("access_token");
    }
}
