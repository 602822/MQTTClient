package org.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTPubClient {

    private MqttClient publisherClient;
    private final String clientId;

    public MQTTPubClient(String clientId) {
        this.clientId = clientId;
    }

    public void connect(String jwtToken) {

        try {

            publisherClient = new MqttClient(Config.BROKER_URL, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(clientId);
            connOpts.setPassword(jwtToken.toCharArray());
            System.out.println("MQTTPubClient Connecting to broker: " + Config.BROKER_URL);
            publisherClient.connect(connOpts);
            System.out.println("MQTTPubClient Connected with JWT");

        } catch (MqttException e) {
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }

    }

    public void publish(double temperature) {
        MqttMessage message = new MqttMessage(String.valueOf(temperature).getBytes());
        message.setQos(1);
        try {
            if (publisherClient != null && publisherClient.isConnected()) {
                String topic = "sensors/" + clientId + "/temperature";
                System.out.println("Publishing temperature: " + temperature + " to topic: " + topic);
                publisherClient.publish(topic, message);
            } else {
                throw new IllegalStateException("Client is not connected.");
            }
        } catch (MqttException e) {
            throw new RuntimeException("Failed to publish the message due to an MQTT exception: " + e);
        }
    }

    public void disconnect() throws MqttException {
        if(publisherClient != null && publisherClient.isConnected()) {
            publisherClient.disconnect();
            System.out.println("MQTTPubClient Disconnected");
        }
    }

    //Reading simulated temperature sensor data for now
    public double readTemperatureSensor() {
        return 20.0 + Math.random() * 10.0; // Random temperature between 20 and 30
    }


}
