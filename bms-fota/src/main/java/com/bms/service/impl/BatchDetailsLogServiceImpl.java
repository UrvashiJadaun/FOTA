package com.bms.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bms.model.t_batch_details;
import com.bms.model.t_batch_details_log;
import com.bms.model.t_commands;
import com.bms.repo.Batch_details_logRepo;
import com.bms.service.BatchDetailsLogServiceApi;

@Service
public class BatchDetailsLogServiceImpl implements BatchDetailsLogServiceApi {

	@Autowired
	Batch_details_logRepo logRepo;
	
	@Override
	public List<t_batch_details_log> get_t_batch_details_logByIMEI(long IMEI) {
		// TODO Auto-generated method stub
		 return logRepo.findByIMEI(IMEI,Sort.by(Sort.Direction.DESC, "time"));
	}
	
	@Override
	public List<t_batch_details_log> get_t_batch_details_logBy_Batch_id(long batch_id) {
		// TODO Auto-generated method stub
		 return logRepo.findByBatchid(batch_id,Sort.by(Sort.Direction.DESC, "time"));
	}

@Override
	public void save_tcu_command(t_batch_details_log log) {
		logRepo.save(log);		
	}

@Override
public void save_bms_command(t_batch_details_log log) {
	logRepo.save(log);	
}

@Override
public void save_cfg_command(t_batch_details_log log) {
	logRepo.save(log);		
}

/*
 * @Override public void update_records(long imei, String componentType) {
 * logRepo.update_records(imei,componentType);
 * 
 * }
 */

@Override
public void update_records(String response, String status, Timestamp timestamp,String topic,long imei, String componentType) {
	t_batch_details_log c=logRepo.findByImeiANDComponentType(imei,componentType);
	c.setResponse(response);
	c.setStatus(status);
	c.setTime(timestamp);
	c.setTopic(topic);
	logRepo.save(c);
	System.out.println("t-commands : "+c.getType());
	
	//logRepo.update_records(response,status,timestamp,imei,componentType);
	
}

@Override
public int deleteByBatch_id(Long batch_id) {
	// TODO Auto-generated method stub
	List<t_batch_details_log> list=logRepo.findByBatch_id(batch_id);
	System.out.println(list);
	for (t_batch_details_log t_batch_details_log : list) {
		logRepo.delete(t_batch_details_log);
	}
	if(list.isEmpty()) return 0;
	return 1;
}

@Override
public void update_records(String command, String orgName, Long batch_id, String response, String status,
		Timestamp timestamp, String topic, long imei, String componentType) {
	// TODO Auto-generated method stub
	//t_batch_details_log c=logRepo.findByImeiANDComponentType(imei,componentType);
	t_batch_details_log c=new t_batch_details_log();
	c.setCommand(command);
	c.setOrgName(orgName);
	c.setBatch_id(batch_id);
	c.setResponse(response);
	c.setStatus(status);
	c.setTime(timestamp);
	c.setTopic(topic);
	c.setIMEI(imei);
	c.setType(componentType);
	logRepo.save(c);
	System.out.println("t-commands : "+c.getType());
}

@Override
public int deleteByIMEI(Long imei) {
	// TODO Auto-generated method stub
		
	return logRepo.deleteByIMEI(imei);
}

@Override
public t_batch_details_log getlatestStatus(String type, Long imei) {
	// TODO Auto-generated method stub
	return logRepo.getlatestStatus(type, imei);
}

@Override
public boolean existByIMEI(Long imei) {
	// TODO Auto-generated method stub
	return logRepo.existsByIMEI(imei);
}



}
