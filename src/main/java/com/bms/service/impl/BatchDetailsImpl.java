package com.bms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bms.model.t_batch_details;
import com.bms.repo.Batch_detailsRepo;
import com.bms.service.BatchDetailsServiceApi;

@Service
public class BatchDetailsImpl implements BatchDetailsServiceApi{

	@Autowired
	Batch_detailsRepo batch_detailsRepo;
	@Override
	public void saveBatchDetailsRecords(t_batch_details batch_details) {

		batch_detailsRepo.save(batch_details);
	}
	@Override
	public List<t_batch_details> findAll() {
		
		return batch_detailsRepo.findAll();
	}
	
	@Override
	public List<t_batch_details> findAllByBatch_id(Long batch_id) {

		return batch_detailsRepo.findByBatch_id(batch_id);
	}
	@Override
	public Boolean existsByImeiNo(long imei) {
		// TODO Auto-generated method stub
		return batch_detailsRepo.existsByIMEI(imei);
	}

}
