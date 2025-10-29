package org.example;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

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

        SignedJWT signedJWT = SignedJWT.parse(jwtToken);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        String location = claims.getStringClaim("location");
        String provider = claims.getStringClaim("provider");

        System.out.println("Sensor:" + clientId + " Location:" + location + " Provider:" + provider);

        //smartocean/{location}/{provider}/{clientId}
        String topic = String.format("smartocean/%s/%s/%s/temperature", location, provider, clientId);


        while (true) {
            try {
                double temp = sensor.readTemperatureSensor();
                sensor.publish(temp, topic);
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