package org.example;

import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParseException {

        Properties props = ConfigLoader.load();

        // Load sensitive data from properties file
        String clientId = props.getProperty("CLIENT_ID");
        String clientSecret = props.getProperty("CLIENT_SECRET");


        MQTTPubClient sensor = new MQTTPubClient(clientId);
        String jwtToken = KeycloakAuth.getTokenSensor(clientId, clientSecret);
        System.out.println("JwtToken: " + jwtToken);
        sensor.connect(jwtToken);
        System.out.println("Sensor with ID: " + clientId + " is now connected");

        String topic1 = "smartocean/Austevoll/Aanderaa/sensor-1/temperature";


        while (true) {
            try {
                double temp = sensor.readTemperatureSensor();
                sensor.publish(temp, topic1);
                Thread.sleep(5000); // Publish every 5 seconds


                if (JWTUtils.willExpireSoon(jwtToken, 60)) {
                    System.out.println("JWT is about to expire, refreshing...");
                    jwtToken = KeycloakAuth.getTokenSensor(clientId, clientSecret);
                    sensor.refreshConnection(jwtToken);
                }

            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                break;
            }
        }

    }
}