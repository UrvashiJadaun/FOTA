package com.bms.model;

import com.opencsv.bean.CsvBindByName;

public class Batch_Data {
	
    private String SNo;
    private String Batch_Name;
    private String Imei;
    private String Tcu;
    private String Bms;
    private String Cfg;
	public String getSNo() {
		return SNo;
	}
	public void setSNo(String sNo) {
		SNo = sNo;
	}
	public String getBatch_Name() {
		return Batch_Name;
	}
	public void setBatch_Name(String batch_Name) {
		Batch_Name = batch_Name;
	}
	public String getImei() {
		return Imei;
	}
	public void setImei(String imei) {
		Imei = imei;
	}
	public String getTcu() {
		return Tcu;
	}
	public void setTcu(String tcu) {
		Tcu = tcu;
	}
	public String getBms() {
		return Bms;
	}
	public void setBms(String bms) {
		Bms = bms;
	}
	public String getCfg() {
		return Cfg;
	}
	public void setCfg(String cfg) {
		Cfg = cfg;
	}

}
