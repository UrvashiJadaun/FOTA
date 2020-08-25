package com.bms.service.impl;

import java.util.List;
import java.util.Optional;

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

	@Override
	public int deleteByBatch_id(Long batch_id) {
		// TODO Auto-generated method stub
		boolean b = batchRepo.existsById(batch_id);
		if(b) {
		batchRepo.deleteById(batch_id);
		return 1;
		}else
			return 0;
	}

	@Override
	public void updateIMEIentry(long batch_id) {
		// TODO Auto-generated method stub
		
		boolean b=batchRepo.existsById(batch_id);
			if(b)
			{
				Optional<t_batch> optional=batchRepo.findById(batch_id);
				t_batch t=optional.get();
					if(optional.get()!=null) 
					{
						long count=t.getCount();
						count--;
						t.setCount(count);
						batchRepo.save(t);
						System.out.println("batch count updated");
					}
			}
			else
			{
				System.out.println("No batch found to update count updated");
			}
		}

	@Override
	public  t_batch findByBatchid(Long batch_id) {
		// TODO Auto-generated method stub
		Optional<t_batch>	batch	=	 batchRepo.findById(batch_id);
		return batch.get();
	}

	@Override
	public List<t_batch> findByBatch_org_name(String orgName) {
		// TODO Auto-generated method stub
		return batchRepo.findByBatchOrgName(orgName);
	}
		
			/*
	 * @Override public int deleteByBatch_id(Long batch_id) { // TODO Auto-generated
	 * method stub return batchRepo.deleteByBatch_id(batch_id);
	 * 
	 * }
	 */


}
