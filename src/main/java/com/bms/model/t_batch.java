package com.bms.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(schema = "public", name = "t_batchh")
@Entity
public class t_batch {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long batch_id;
	
	private String usr;
	//private String user;
	
	private String batch_org_name;
	
	public String getUsr() {
		return usr;
	}

	public void setUsr(String usr) {
		this.usr = usr;
	}

	private long count;
	
	private Timestamp start_date;
	
	private Timestamp end_date;
	
	//private String user;
	
	private String description;
	
	private String status;
	
	private String execute;

	/*
	 * public String getUser() { return user; }
	 * 
	 * public void setUser(String user) { this.user = user; }
	 */
	
	public long getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(long batch_id) {
		this.batch_id = batch_id;
	}

	public String getBatch_org_name() {
		return batch_org_name;
	}

	public void setBatch_org_name(String batch_org_name) {
		this.batch_org_name = batch_org_name;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
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

	/*
	 * public String getUser() { return user; }
	 * 
	 * public void setUser(String user) { this.user = user; }
	 */

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExecute() {
		return execute;
	}

	public void setExecute(String execute) {
		this.execute = execute;
	}
	
	
}
