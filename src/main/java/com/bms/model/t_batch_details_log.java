package com.bms.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Table(schema = "public", name = "t_batch_details_log")
@Entity
public class t_batch_details_log {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long s_no;
	
	private long batch_id;
	
	private String type;
	
	private long IMEI;
	
	private Timestamp time;
	
	private String orgName;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String status;
	
	private String command;
	
	private String response;
	
	public long getS_no() {
		return s_no;
	}

	public void setS_no(long s_no) {
		this.s_no = s_no;
	}

	public long getBatch_id() {
		return batch_id;
	}
   
	public void setBatch_id(long batch_id) {
		this.batch_id = batch_id;
	}

	public long getIMEI() {
		return IMEI;
	}

	public void setIMEI(long iMEI) {
		IMEI = iMEI;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	/*
	 * @JoinColumn(name="batch_id") private t_batch_details t_batch_details;
	 */
	
	

	/*
	 * public t_batch_details getT_batch_details() { return t_batch_details; }
	 * 
	 * public void setT_batch_details(t_batch_details t_batch_details) {
	 * this.t_batch_details = t_batch_details; }
	 */

	

	
	
	
	
}
