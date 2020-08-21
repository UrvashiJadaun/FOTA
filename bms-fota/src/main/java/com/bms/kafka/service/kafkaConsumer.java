package com.bms.kafka.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.sql.*;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.bms.Entity.AssetEntity;
//import com.bms.model.t_batch;
//import com.bms.model.t_batch_details;
import com.bms.service.AssetsServiceAPI;
import com.bms.service.BatchDetailsServiceApi;
import com.bms.service.BatchServiceApi;

//@Service
public class kafkaConsumer {

	private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

	/*
	 * private static final String Slag_Master_Data = "Save_master_data"; private
	 * static final String Slag_Client_Data = "Save_client_data"; private static
	 * final String Slag_Publish_Data = "publish_client_data"; private static final
	 * String Asset_event = "asset_day_activity"; private static final String
	 * mqtt_data_with_partitions = "mqtt_data_with_partitions";
	 */

	/*
	 * @KafkaListener(topics = Slag_Master_Data, groupId = "master_data",
	 * containerFactory = "kafkaListenerContainerFactory") public void
	 * KafkaMasterDataDB(@Payload String
	 * data, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
	 * 
	 * @Header(KafkaHeaders.RECEIVED_TOPIC) String
	 * topic, @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) throws
	 * ParseException {
	 * 
	 * String[] skey = key.split("--");
	 * System.out.println("data *************** "+data);
	 * System.out.println("key KafkaMasterDataDB "+ key);
	 * //System.out.println("partition KafkaMasterDataDB "+ partition); if
	 * (skey[0].equals("/exicom/bounce/bin/batterydata")) {
	 * 
	 * } }
	 */

	/*
	 * @KafkaListener(topics = mqtt_data_with_partitions, groupId = "master_data",
	 * containerFactory = "kafkaListenerContainerFactory") public void
	 * KafkaMasterDataDB(@Payload String
	 * data, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
	 * 
	 * @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
	 * 
	 * @Header(KafkaHeaders.RECEIVED_TOPIC) String
	 * topic, @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) throws
	 * ParseException {
	 * 
	 * String[] skey = key.split("--"); System.out.println("**data** : "+data);
	 * System.out.println("key KafkaMasterDataDB "+ key);
	 * System.out.println("partition KafkaMasterDataDB "+ partition);
	 * 
	 * // System.exit(0); if (skey[0].equals("/exicom/bounce/bin/batterydata")) {
	 * 
	 * } }
	 */
	public static final String CHECK = "FWA/exicom";
//  public static final String imei							=	"357897106611195";	
//	public static final String imei							=	"357897106624016";
//	public static final String FWA_exicom_client_TCU_imei	= 	"FWA/exicom/client/TCU/"+imei;
	// public static final String FWA_exicom_client_TCU_imei1 =
	// "FWA_exicom_client_TCU_"+imei;
	// public static final String FWA_exicom_client_BMS_imei =
	// "FWA/exicom/client/BMS/"+imei;
	public static final String FWA_exicom = "FWA_exicom";

	
	/*
	 * @KafkaListener(topics = FWA_exicom, groupId = "master_data", containerFactory
	 * = "kafkaListenerContainerFactory") public void KafkaMasterDataDB(
	 * 
	 * @Payload String data, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
	 * 
	 * @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
	 * 
	 * @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
	 * 
	 * @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) throws ParseException {
	 * 
	 * String[] skey = key.split("--");
	 * System.out.println("partition KafkaMasterDataDB "+ partition);
	 * 
	 * if(skey[0].startsWith(CHECK)) { System.out.
	 * println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ IF IF IF IF IF IF IF I IF IF IF");
	 * System.out.println(">>>> data <<<< : "+data);
	 * System.out.println(">>>> key KafkaMasterDataDB <<<<"+ key);
	 * System.out.println(">>>> kafka_topic <<<< : "+topic);
	 * System.out.println(">>>> mqtt_data <<<< : "+skey[0]); System.out.
	 * println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ IF END IF END IF END IF END IF END IF END IF END IF END IF END"
	 * ); } else {
	 * System.out.println("!!!!!!!!!!!!!! ELSE ELSE ELSE ELSE ELSE ELSE"); } // //
	 * System.exit(0); }
	 */
	 

	/*
	  @Autowired 
	  private AssetsServiceAPI assetsServiceAPI;
	  @Autowired
	  private BatchServiceApi batchServiceApi;
	  @Autowired
	  private BatchDetailsServiceApi batchDetailsServiceApi;*/
	  
	  @KafkaListener(topics = FWA_exicom, groupId = "master_data", containerFactory
	  = "kafkaListenerContainerFactory") public void KafkaMasterDataDB(
	  
	  @Payload String data, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
	  
	  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
	  
	  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
	  
	  @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) throws ParseException {
	  
	  
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	//   List<AssetEntity> assetList = assetsServiceAPI.findAllAssets();
	  // Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	   
	//  System.out.println("assetList :: "+assetList); 
	/*  t_batch batch=new t_batch();
	  batch.setBatch_id(1);
	  batch.setUser("USER1");
	  batch.setBatch_org_name("ORG_1");
	  batch.setCount(assetList.size());
	  batch.setDescription("description_1");
	  batch.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
	  batch.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
	  batch.setExecute("new");
	  batch.setStatus("0");
	  batchServiceApi.saveBatchRecords(batch);
	  
	  for (Iterator iterator = assetList.iterator(); iterator.hasNext();) {
		AssetEntity assetEntity 		= 	(AssetEntity) iterator.next();
		t_batch_details batch_details	=	new t_batch_details();
		batch_details.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
		batch_details.setBMS("BSM_1");
		batch_details.setCFG("CFG_1");
		batch_details.setIMEI(assetEntity.getImeiNo());
		batch_details.setMax_time(6);
		batch_details.setSend_command("send_command_1");
		batch_details.setStatus("0");
		batch_details.setTCL_version("tCL_version_1");
		batch_details.setBatch_id(batch.getBatch_id());
		batch_details.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
		batchDetailsServiceApi.saveBatchDetailsRecords(batch_details);
		}
		*/

	  
	  System.out.println("???????????????????????????????????????????????????????????????????????????????????????????");
	  
	  System.exit(0);
	  
	   // System.exit(0); 
	  }
	 

}
