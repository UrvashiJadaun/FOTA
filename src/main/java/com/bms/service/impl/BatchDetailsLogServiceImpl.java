package com.bms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bms.model.t_batch_details_log;
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

}
