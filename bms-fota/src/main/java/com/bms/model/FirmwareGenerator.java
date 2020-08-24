package com.bms.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(schema = "public", name = "FirmwareGenerator")
@Entity
public class FirmwareGenerator {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int sNo;
	
	private String clientName;
	
	private String firmwareType;
	
	private String firmwareVersion;
	
	private String firmwareNomenClature;

	public int getsNo() {
		return sNo;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getFirmwareType() {
		return firmwareType;
	}

	public void setFirmwareType(String firmwareType) {
		this.firmwareType = firmwareType;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getFirmwareNomenClature() {
		return firmwareNomenClature;
	}

	public void setFirmwareNomenClature(String firmwareNomenClature) {
		this.firmwareNomenClature = firmwareNomenClature;
	}
	

}
