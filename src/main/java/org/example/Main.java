package org.example;

import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {


        Properties props = ConfigLoader.load();

        // Load sensitive data from properties file
        String clientId = props.getProperty("CLIENT_ID");
        String clientSecret = props.getProperty("CLIENT_SECRET");

        MQTTPubClient sensor = new MQTTPubClient(clientId);
        String jwtToken = KeycloakAuth.getTokenSensor(clientId, clientSecret);
        System.out.println("JwtToken: " + jwtToken);
        sensor.connect(jwtToken);

        while (true) {
            try {
                double temp = sensor.readTemperatureSensor();
                String topic = "sensors/" + sensor.getClientId() + "/temperature";
                sensor.publish(temp, topic);
                Thread.sleep(5000); // Publish every 5 seconds

                if (JWTUtils.willExpireSoon(jwtToken, 60)) {
                    System.out.println("JWT is about to expire, refreshing...");
                    jwtToken = KeycloakAuth.getTokenSensor(clientId, clientSecret);
                    sensor.refreshConnection(jwtToken);

                }

            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                break; // Exit the loop on exception
            }
        }

    }
}