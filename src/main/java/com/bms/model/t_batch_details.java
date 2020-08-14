package com.bms.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Table(schema = "public", name = "t_batch_details")
@Entity
public class t_batch_details {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long sNo;
	
	private String TCL_version;
	
	private String BMS;
	
	private String CFG;
	
	private String status;
	
	private String send_command;
	
	private Timestamp start_date;
	
	private Timestamp end_date;
	
	private Integer max_time;
	
	private long IMEI;
	
//	@JoinColumn(name = "batch_id")
//	private t_batch t_batch;
	private long batch_id;
	
	public long getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(long batch_id) {
		this.batch_id = batch_id;
	}

	
	

	/*
	 * public t_batch getT_batch() { return t_batch; }
	 * 
	 * public void setT_batch(t_batch t_batch) { this.t_batch = t_batch; }
	 */



	public String getTCL_version() {
		return TCL_version;
	}

	public long getsNo() {
		return sNo;
	}

	public void setsNo(long sNo) {
		this.sNo = sNo;
	}

	public void setTCL_version(String tCL_version) {
		TCL_version = tCL_version;
	}

	public String getBMS() {
		return BMS;
	}

	public void setBMS(String bMS) {
		BMS = bMS;
	}

	public String getCFG() {
		return CFG;
	}

	public void setCFG(String cFG) {
		CFG = cFG;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSend_command() {
		return send_command;
	}

	public void setSend_command(String send_command) {
		this.send_command = send_command;
	}

	public Timestamp getStart_date() {
		return start_date;
	}

	public void setStart_date(Timestamp start_date) {
		this.start_date = start_date;
	}

	public Timestamp getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Timestamp end_date) {
		this.end_date = end_date;
	}

	public Integer getMax_time() {
		return max_time;
	}

	public void setMax_time(Integer max_time) {
		this.max_time = max_time;
	}

	public long getIMEI() {
		return IMEI;
	}

	public void setIMEI(long iMEI) {
		IMEI = iMEI;
	}
	
	
	
	
}
