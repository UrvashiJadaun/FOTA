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

}
