package com.bms.mqtt.publisher;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Publisher implements MqttCallback{

	protected final String broker = "Stagingbn.bt.exicom.in";
	//protected final String broker="Stagingbt.exicom.in";
	protected final int qos = 2;
	protected Boolean hasSSL = false; /* By default SSL is disabled */
	protected Integer port = 1883; /* Default port */
	protected final String userName = "exicom.mqtt";
	protected final String password = "Secure}123";
	protected final String TCP = "tcp://";
	protected final String SSL = "ssl://";
	
	private String brokerUrl = null;
	final private String colon = ":";
	final private String clientId = "demoClient11";
	private MqttClient mqttClient = null;
	private MqttConnectOptions connectionOptions = null;
	private MemoryPersistence persistence = null;
	private static final Logger logger = LoggerFactory.getLogger(Publisher.class);
	
	public Publisher() {
		//this.config();
	}
	public boolean publish(String topic) {
		this.brokerUrl = this.TCP + this.broker + colon + this.port;
		this.persistence = new MemoryPersistence();
		this.connectionOptions = new MqttConnectOptions();
		try {
			this.mqttClient = new MqttClient(brokerUrl, clientId, persistence);
			this.connectionOptions.setCleanSession(true);
			this.connectionOptions.setPassword("Secure}123".toCharArray());
			this.connectionOptions.setUserName("exicom.mqtt");
			this.mqttClient.connect(this.connectionOptions);
			this.mqttClient.setCallback(this);
			MqttMessage message = new MqttMessage("Testing".getBytes());

			//here ed sheeran is a message

			    message.setQos(qos);     //sets qos level 1
			    message.setRetained(true); //sets retained message 
			MqttDeliveryToken x=this.mqttClient.getTopic(topic).publish(message);
			if(x!=null)
				return true;
		} catch (MqttException me) {
			logger.error("ERROR", me);
		}
		return false;

	}
	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		System.out.println("Conection Lost");
		
	}
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("published : "+topic);
	}
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		System.out.println("delevery complete : ");
	}
	
	

	
}
