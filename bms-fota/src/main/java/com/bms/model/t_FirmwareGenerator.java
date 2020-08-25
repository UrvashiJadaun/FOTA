package com.bms.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(schema = "public", name = "t_Firmwaregenerator")
@Entity
public class t_FirmwareGenerator {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long sno;
	
	private String clientname;
	
	private String firmwaretype;
	
	private String firmwareversion;
	
	private String firmwarenomenclature;

	public long getSno() {
		return sno;
	}

	public void setSno(long sno) {
		this.sno = sno;
	}

	public String getClientname() {
		return clientname;
	}

	public void setClientname(String clientname) {
		this.clientname = clientname;
	}

	public String getFirmwaretype() {
		return firmwaretype;
	}

	public void setFirmwaretype(String firmwaretype) {
		this.firmwaretype = firmwaretype;
	}

	public String getFirmwareversion() {
		return firmwareversion;
	}

	public void setFirmwareversion(String firmwareversion) {
		this.firmwareversion = firmwareversion;
	}

	public String getFirmwarenomenclature() {
		return firmwarenomenclature;
	}

	public void setFirmwarenomenclature(String firmwarenomenclature) {
		this.firmwarenomenclature = firmwarenomenclature;
	}

	

}
