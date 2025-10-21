package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        MQTTPubClient sensor = new MQTTPubClient("sensor-client-id"); //currently hardcoded
        String jwtToken = KeycloakAuth.getTokenSensor("sensor-client-id", "sensor-client-secret"); //currently hardcoded
        sensor.connect(jwtToken);

        while (true) {
            try {
                double temp = sensor.readTemperatureSensor();
                sensor.publish(temp);
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                break; // Exit the loop on exception
            }
        }

    }
}