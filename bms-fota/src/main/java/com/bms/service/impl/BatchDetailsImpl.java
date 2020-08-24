package com.bms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bms.model.t_batch_details;
import com.bms.repo.BatchRepo;
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
	/*
	 * @Override public int deleteByBatch_id(Long batch_id) { // TODO Auto-generated
	 * method stub return batch_detailsRepo.deleteByBatch_id(batch_id); }
	 */
	@Override
	public int deleteByBatch_id(Long batch_id) {
		// TODO Auto-generated method stub
	//	batch_detailsRepo.deleteByBatch_id(batch_id);
		List<t_batch_details> list=batch_detailsRepo.findByBatch_id(batch_id);
		System.out.println(list);
		for (t_batch_details t_batch_details : list) {
			
			batch_detailsRepo.delete(t_batch_details);
		}
		//batch_detailsRepo.deleteRecords(batch_id);
		if(list.isEmpty()) return 0;
		return 1;
	}
	@Override
	public t_batch_details findByImei(long imei) {
		// TODO Auto-generated method stub
		return batch_detailsRepo.findByIMEI(imei);
	}
	@Override
	public int deleteByIMEI(Long imei) {
		// TODO Auto-generated method stub
		return batch_detailsRepo.deleteByIMEI(imei);
	}
	@Override
	public void update_records(t_batch_details t_batch_details) {
		// TODO Auto-generated method stub
		t_batch_details.setStatus("done");
		batch_detailsRepo.save(t_batch_details);
		
	}


}
