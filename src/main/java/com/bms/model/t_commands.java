package com.bms.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(schema = "public", name = "t_commands")
@Entity
public class t_commands {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String commandtype;
	
	private String reset;
	private String showconfig;
	private String showcredential;
	private String showota;
	private String showpubtopics;
	private String showsubtopics;
	private String setservername;
	private String primaryport;
	private String fotaservername;
	private String saveconfig;
	
	public int getId() { 
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCommandtype() {
		return commandtype;
	}
	public void setCommandtype(String commandtype) {
		this.commandtype = commandtype;
	}
	
	public String getReset() {
		return reset;
	}
	public void setReset(String reset) {
		this.reset = reset;
	}
	public String getShowconfig() {
		return showconfig;
	}
	public void setShowconfig(String showconfig) {
		this.showconfig = showconfig;
	}
	public String getShowcredential() {
		return showcredential;
	}
	public void setShowcredential(String showcredential) {
		this.showcredential = showcredential;
	}
	public String getShowota() {
		return showota;
	}
	public void setShowota(String showota) {
		this.showota = showota;
	}
	public String getShowpubtopics() {
		return showpubtopics;
	}
	public void setShowpubtopics(String showpubtopics) {
		this.showpubtopics = showpubtopics;
	}
	public String getShowsubtopics() {
		return showsubtopics;
	}
	public void setShowsubtopics(String showsubtopics) {
		this.showsubtopics = showsubtopics;
	}
	public String getSetservername() {
		return setservername;
	}
	public void setSetservername(String setservername) {
		this.setservername = setservername;
	}
	public String getPrimaryport() {
		return primaryport;
	}
	public void setPrimaryport(String primaryport) {
		this.primaryport = primaryport;
	}
	public String getFotaservername() {
		return fotaservername;
	}
	public void setFotaservername(String fotaservername) {
		this.fotaservername = fotaservername;
	}
	public String getSaveconfig() {
		return saveconfig;
	}
	public void setSaveconfig(String saveconfig) {
		this.saveconfig = saveconfig;
	}
	
	
}
