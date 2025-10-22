package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        MQTTPubClient sensor = new MQTTPubClient("sensor-client-id"); //currently hardcoded
        TokenResponse tokenResponse = KeycloakAuth.getTokenSensor("sensor-client-id", "sensor-client-secret"); //currently hardcoded
        String jwtToken = tokenResponse.accessToken();
        sensor.connect(jwtToken);

        while (true) {
            try {
                double temp = sensor.readTemperatureSensor();
                sensor.publish(temp);

                if (JWTUtils.willExpireSoon(jwtToken, 60)) {
                    System.out.println("JWT is about to expire, refreshing...");
                    tokenResponse = KeycloakAuth.refreshToken(tokenResponse.refreshToken(), "sensor-client-id", "sensor-client-secret"); //currently hardcoded
                    if (tokenResponse != null) {
                        jwtToken = tokenResponse.accessToken();
                        sensor.refreshConnection(jwtToken);
                    }
                }

                Thread.sleep(5000); // Publish every 5 seconds
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                break; // Exit the loop on exception
            }
        }

    }
}