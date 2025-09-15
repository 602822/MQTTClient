package org.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

public class MQTTPubClient {

    private final String clientId = MqttClient.generateClientId();
    private MqttClient publisherClient;
    private final String topic = "sensors/temperature"; // Example topic

    public MQTTPubClient(MqttClient publisherClient) {
        this.publisherClient = publisherClient;
    }

    public void publish(String sensordata) {
        MqttMessage message = new MqttMessage(sensordata.getBytes());
        message.setQos(1); // 0 or 1
        try {
            publisherClient.publish(topic, message);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }



    }

    public void connect() {

        try {
            String token = KeycloakAuth.getToken("smartocean-testrealm","hivemq-smartocean-testbroker","username1","password1"); //temporary
            String broker = "tcp://localhost:8080"; //temporary
            MqttClient publisherClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName("test"); //temporary
            connOpts.setPassword(token.toCharArray()); //temporary
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

}
