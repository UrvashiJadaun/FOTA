package com.bms.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

//@Table(schema = "public", name = "t_batch_details_log_")
@Entity
public class t_batch_details_log {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	/*
	 * @JoinColumn(name="batch_id") private t_batch_details t_batch_details;
	 */
	private long t_batch_details_id;
	
	private String log;
	
	public long getT_batch_details_id() {
		return t_batch_details_id;
	}

	public void setT_batch_details_id(long t_batch_details_id) {
		this.t_batch_details_id = t_batch_details_id;
	}

	private String send_command;
	
	private String response;
	
	

	/*
	 * public t_batch_details getT_batch_details() { return t_batch_details; }
	 * 
	 * public void setT_batch_details(t_batch_details t_batch_details) {
	 * this.t_batch_details = t_batch_details; }
	 */

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getSend_command() {
		return send_command;
	}

	public void setSend_command(String send_command) {
		this.send_command = send_command;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	
	
}
