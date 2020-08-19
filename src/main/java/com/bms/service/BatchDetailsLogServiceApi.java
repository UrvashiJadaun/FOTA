package com.bms.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bms.model.t_batch_details_log;

@Service
public interface BatchDetailsLogServiceApi {

	List<t_batch_details_log> get_t_batch_details_logByIMEI(long IMEI);

	void save_tcu_command(t_batch_details_log log);

	void save_bms_command(t_batch_details_log log);

	void save_cfg_command(t_batch_details_log log);

	void update_records(String response, String status, Timestamp timestamp, long imei, String tcu);

}
