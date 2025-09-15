package org.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

public class MQTTSubClient {
    private final String clientId = MqttClient.generateClientId();
    private MqttClient subscriberClient;
    private final String topic = "sensors/temperature"; // Example topic

    public void connectAndSubscribe() {
        try {
            String token = KeycloakAuth.getToken("smartocean-testrealm", "hivemq-smartocean-testbroker", "username1", "password1"); //temporary
            String broker = "tcp://localhost:8080"; //temporary
            subscriberClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName("test"); //temporary
            connOpts.setPassword(token.toCharArray()); //temporary
            System.out.println("MQTTSubClient Connecting to broker: " + broker);
            subscriberClient.connect(connOpts);
            System.out.println("MQTTSubClient Connected with JWT");
            subscriberClient.subscribe(topic, (receivedTopic, message) -> {
                System.out.println("Recieved message: " + new String(message.getPayload()) + " from topic: " + receivedTopic);
            });
        } catch (IOException e) {
            System.err.println("An IOException occurred while connecting to the MQTT broker: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the MQTT broker", e);
        } catch (MqttException e) {
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            throw new RuntimeException("Failed to connect to the MQTT broker", e);

        }
    }


}
