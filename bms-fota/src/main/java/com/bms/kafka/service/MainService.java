package com.bms.kafka.service;

//import java.sql.Timestamp
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bms.Entity.AssetEntity;
//import com.bms.model.t_batch;
//import com.bms.model.t_batch_details;
import com.bms.service.AssetsServiceAPI;
import com.bms.service.BatchDetailsServiceApi;
import com.bms.service.BatchServiceApi;


public class MainService {
	/*
	 * @Autowired private static AssetsServiceAPI assetsServiceAPI;
	 * 
	 * @Autowired private static BatchServiceApi batchServiceApi;
	 * 
	 * @Autowired private static BatchDetailsServiceApi batchDetailsServiceApi;
	 * 
	 * 
	 * public MainService() { System.out.println(
	 * "#####################################################################################################333"
	 * );
	 * 
	 * System.out.println(
	 * "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
	 * ); List<AssetEntity> assetList = assetsServiceAPI.findAllAssets(); Timestamp
	 * currentTimestamp = new
	 * java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	 * 
	 * System.out.println("assetList :: "+assetList); t_batch batch=new t_batch();
	 * batch.setBatch_id(1); batch.setUser("USER1");
	 * batch.setBatch_org_name("ORG_1"); batch.setCount(assetList.size());
	 * batch.setDescription("description_1"); batch.setStart_date(new
	 * Timestamp(Calendar.getInstance().getTime().getTime())); batch.setEnd_date(new
	 * Timestamp(Calendar.getInstance().getTime().getTime()));
	 * batch.setExecute("new"); batch.setStatus("0");
	 * batchServiceApi.saveBatchRecords(batch);
	 * 
	 * for (Iterator iterator = assetList.iterator(); iterator.hasNext();) {
	 * AssetEntity assetEntity = (AssetEntity) iterator.next(); t_batch_details
	 * batch_details = new t_batch_details(); batch_details.setStart_date(new
	 * Timestamp(Calendar.getInstance().getTime().getTime()));
	 * batch_details.setBMS("BSM_1"); batch_details.setCFG("CFG_1");
	 * batch_details.setIMEI(assetEntity.getImeiNo()); batch_details.setMax_time(6);
	 * batch_details.setSend_command("send_command_1");
	 * batch_details.setStatus("0"); batch_details.setTCL_version("tCL_version_1");
	 * batch_details.setBatch_id(batch.getBatch_id()); batch_details.setEnd_date(new
	 * Timestamp(Calendar.getInstance().getTime().getTime()));
	 * batchDetailsServiceApi.saveBatchDetailsRecords(batch_details); }
	 * 
	 * System.out.println(
	 * "???????????????????????????????????????????????????????????????????????????????????????????"
	 * );
	 * 
	 * // System.exit(0);
	 * 
	 * }
	 */


}
