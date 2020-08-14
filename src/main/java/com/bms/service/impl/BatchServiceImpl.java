package com.bms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bms.model.t_batch;
import com.bms.repo.BatchRepo;
import com.bms.service.BatchServiceApi;

@Service
public class BatchServiceImpl implements BatchServiceApi{
	
	@Autowired
	BatchRepo batchRepo;

	@Override
	public void saveBatchRecords(t_batch batch) {
      System.out.println("********** BatchServiceImpl ::"+batch.toString());
     System.out.println(batch.getBatch_id()+"");
  //   System.out.println(batch.getUser());
		batchRepo.save(batch);
	}

	@Override
	public Long getMaxId() {
		
		return batchRepo.getMaxId();
	}

	@Override
	public List<t_batch> findAll() {
		return batchRepo.findAll();
        		
	}


}
