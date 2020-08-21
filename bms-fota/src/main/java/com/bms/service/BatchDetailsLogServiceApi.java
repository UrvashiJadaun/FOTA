package com.bms.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bms.model.t_batch_details_log;

@Service
public interface BatchDetailsLogServiceApi {

	List<t_batch_details_log> get_t_batch_details_logByIMEI(long IMEI);

	List<t_batch_details_log> get_t_batch_details_logBy_Batch_id(long batch_id);

	void save_tcu_command(t_batch_details_log log);

	void save_bms_command(t_batch_details_log log);

	void save_cfg_command(t_batch_details_log log);

	void update_records(String response, String status, Timestamp timestamp, String topic, long imei,
			String componentType);

	int deleteByBatch_id(Long batch_id);

	void update_records(String command, String orgName, Long batchid, String response,
			String status, Timestamp timestamp, String topic, long imei, String componentType);

	int deleteByIMEI(Long imei);

	t_batch_details_log getlatestStatus(String type, Long imei);

	boolean existByIMEI(Long imei);



	//void update_records(String response, String status, Timestamp timestamp, long imei, String tcu, String topic);

}
