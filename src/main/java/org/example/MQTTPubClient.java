package org.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

public class MQTTPubClient {

    private final String clientId = MqttClient.generateClientId();
    private final MqttClient publisherClient;

    public MQTTPubClient(MqttClient publisherClient) {
        this.publisherClient = publisherClient;
    }

    public void connect(String realm, String clientId, String username, String password) {

        try {
            String token = KeycloakAuth.getToken(realm, clientId, username, password);
            String broker = "tcp://localhost:8080"; //temporary
            MqttClient publisherClient = new MqttClient(broker, MqttClient.generateClientId(), new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(token.toCharArray());
            System.out.println("MQTTPubClient Connecting to broker: " + broker);
            publisherClient.connect(connOpts);
            System.out.println("MQTTPubClient Connected with JWT");

        } catch (MqttException e) {
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("An IOException occurred while connecting to the MQTT broker: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the MQTT broker", e);
        }

    }

    public void publish(String sensordata, String topic) {
        MqttMessage message = new MqttMessage(sensordata.getBytes());
        message.setQos(1);
        try {
            if (publisherClient != null && publisherClient.isConnected()) {
                System.out.println("Publishing message: " + sensordata + " to topic: " + topic);
                publisherClient.publish(topic, message);
            } else {
                throw new IllegalStateException("Client is not connected.");
            }
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }


}
