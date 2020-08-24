package com.bms.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bms.model.t_batch_details;

@Component
public interface BatchDetailsServiceApi {
	public void saveBatchDetailsRecords(t_batch_details batch_details);

	public List<t_batch_details> findAll();

	public List<t_batch_details> findAllByBatch_id(Long batch_id);
	
	public Boolean existsByImeiNo(long imei);

	public int deleteByBatch_id(Long batch_id);

	public t_batch_details findByImei(long l);

	public int deleteByIMEI(Long imei);

	public void update_records(t_batch_details t_batch_details);



	//public int deleteByBatch_id(Long batch_id);

}
