package org.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

public class MQTTSubClient {

    private MqttClient subscriberClient;

    public void connect(String realm, String clientId, String username, String password) {
        try {
            String token = KeycloakAuth.getToken(realm, clientId, username, password);
            String broker = "tcp://localhost:8080"; // Example broker
            subscriberClient = new MqttClient(broker, MqttClient.generateClientId(), new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(token.toCharArray());
            System.out.println("Connecting to broker: " + broker);
            subscriberClient.connect(connOpts);
            System.out.println("Connected with JWT");
        } catch (IOException | MqttException e) {
            System.err.println("Failed to connect to the MQTT broker: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the MQTT broker", e);
        }
    }

    public void subscribe(String topic) {
        try {
            if (subscriberClient != null && subscriberClient.isConnected()) {
                subscriberClient.subscribe(topic, (receivedTopic, message) -> {
                    System.out.println("Received message: " + new String(message.getPayload()) + " from topic: " + receivedTopic);
                });
                System.out.println("Subscribed to topic: " + topic);
            } else {
                throw new IllegalStateException("Client is not connected.");
            }
        } catch (MqttException e) {
            System.err.println("Failed to subscribe to topic: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to subscribe to topic", e);
        }
    }
}