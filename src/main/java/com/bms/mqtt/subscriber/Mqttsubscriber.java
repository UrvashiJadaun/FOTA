package com.bms.mqtt.subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.sql.Timestamp;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
public class Mqttsubscriber implements MqttCallback{
	
	
	 @Autowired
	private KafkaTemplate<String, String> kafkaTemplateClient;
    
	public static final String CHECK						=	"FWA/exicom/#";
	public static final String imei							=	"357897106624016";
	//public static final String FWA_exicom_client_TCU_imei	= 	"FWA/exicom/client/TCU/"+imei;
	//public static final String FWA_exicom_client_TCU_imei1	= 	"FWA_exicom_client_TCU_"+imei;
	public static final String FWA_exicom	= 	"FWA_exicom";
	
	//public static final String mqtt_topic					=	FWA_exicom_client_TCU_imei;
	protected final String broker = "Stagingbn.bt.exicom.in";
	//protected final String broker="Stagingbt.exicom.in";
	protected final int qos = 2;
	protected Boolean hasSSL = false;  //By default SSL is disabled 
	protected Integer port = 1883;  //Default port 
	protected final String userName = "exicom.mqtt";
	protected final String password = "Secure}123";
	protected final String TCP = "tcp://";
	protected final String SSL = "ssl://";
	
	private String brokerUrl = null;
	final private String colon = ":";
	final private String clientId = UUID.randomUUID().toString(); //"URVASHI_CLIENT";
	private MqttClient mqttClient = null;
	private MqttConnectOptions connectionOptions = null;
	private MemoryPersistence persistence = null;
	
	private static final Logger logger = LoggerFactory.getLogger(Mqttsubscriber.class);
	
	
	public Mqttsubscriber() {
		logger.info("I am MqttSub impl");
		this.config();
	}
	protected void config() {
		logger.info("Inside Config with parameter");
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
			//this.mqttClient.subscribe(mqtt_topic);
			this.mqttClient.subscribe(CHECK);
		} catch (MqttException me) {
			throw new com.bms.exceptions.MqttException("Not Connected");
		}

	}
	@Override
	public void connectionLost(Throwable cause) {
		logger.info("Connection Lost" + cause);
		logger.info("I am MqttSub impl");
		this.config();
		
	}
	@Override
	public void messageArrived(String mqttTopic, MqttMessage mqttMessage) throws Exception {
		String time = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println();
		System.out.println("***********************************************************************");
		System.out.println("Message Arrived at Time: " + time + "  Topic: " + mqttTopic + "  Message: "
				+ new String(mqttMessage.getPayload()));
		System.out.println("***********************************************************************");

		
//		  if(mqttTopic.startsWith(mqtt_topic)) { System.out.println(
//		  "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ-----MATCHED----ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ"
//		  ); System.out.println(
//		  "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ-----MATCHED----ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ"
//		  ); }
//		 
		
		/***
		 * Kakfa Publish For Master Data Topic
		 * 
		 * 
		 */
		int max = 10;
		int min = 1;
		int range = max - min + 1;
		int rand = (int) (Math.random() * range) + min;		
		String finalTopicName = mqttTopic+"--"+rand;
		//String finalTopicName 	= 	FWA_exicom_client_TCU_imei1+"--"+rand;

		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		
		/*
		 * if(mqttTopic.startsWith(CHECK)) { System.out.
		 * println("IF IF IF IF IF IF IF IF IF IF IF IF IF IF IF IF IF IF IF IF IF IF");
		 */
		 
			 ProducerRecord<String, String> recordMasterdb = new ProducerRecord<String, String>(FWA_exicom,
						finalTopicName, new String(mqttMessage.getPayload()));
			 System.out.println("**MQTT_data** : "+mqttMessage.getPayload());
			 System.out.println("**mqttTopic** : "+ mqttTopic);
			// System.out.println("MQTT_key KafkaMasterDataDB mqttTopic "+ mqttTopic);
			 
			 ListenableFuture<SendResult<String, String>> sendRecordMasterdb = kafkaTemplateClient.send(recordMasterdb);
				sendRecordMasterdb.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
					@Override
					public void onSuccess(SendResult<String, String> result) {
						// TODO Auto-generated method stub
						System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYY KAFKA DB YYYYYYYYYYYYYYYYYYYYYY");
						logger.info("sent message= " + result + " with offset= " + result.getRecordMetadata().offset());
						logger.info("sent message partition= " + result.getRecordMetadata().partition());
					}
					@Override
					public void onFailure(Throwable ex) {
						logger.error("unable to send message= ", ex);
					}
				});
				
				/*
				 * } else { System.out.
				 * println("ELSE ELSE ELSE ELSE ELSE ELSE ELESE ELSE ELSE ELSE ELSE ELSE");
				 * System.out.println(
				 * "**********************************************************");
				 * System.out.println("Message does not start with = "+CHECK);
				 * System.out.println("Message                     = "+mqttMessage.getPayload())
				 * ; System.out.println(
				 * "**********************************************************"); }
				 */
				 
		 
		 
		

		
	}
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
