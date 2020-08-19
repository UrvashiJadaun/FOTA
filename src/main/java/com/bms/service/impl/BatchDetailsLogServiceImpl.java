package com.bms.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
public void update_records(String response, String status, Timestamp timestamp, long imei, String componentType) {
	t_batch_details_log c=logRepo.findByImeiANDComponentType(imei,componentType);
	c.setResponse(response);
	c.setStatus(status);
	c.setTime(timestamp);
	logRepo.save(c);
	System.out.println("t-commands : "+c.getType());
	
	//logRepo.update_records(response,status,timestamp,imei,componentType);
	
}

}
