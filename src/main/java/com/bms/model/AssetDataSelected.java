package com.bms.model;

public class AssetDataSelected {
	private Long imeiNo;
	private Integer bmsConfigurationVersion;
	private String tcu;
	private String bms;
	private String bin;
	
	public String getBin() {
		return bin;
	}
	public void setBin(String bin) {
		this.bin = bin;
	}
	public Long getImeiNo() {
		return imeiNo;
	}
	public void setImeiNo(Long imeiNo) {
		this.imeiNo = imeiNo;
	}
	public Integer getBmsConfigurationVersion() {
		return bmsConfigurationVersion;
	}
	public void setBmsConfigurationVersion(Integer bmsConfigurationVersion) {
		this.bmsConfigurationVersion = bmsConfigurationVersion;
	}
	public String getTcu() {
		return tcu;
	}
	public void setTcu(String tcu) {
		this.tcu = tcu;
	}
	public String getBms() {
		return bms;
	}
	public void setBms(String bms) {
		this.bms = bms;
	}
	@Override
	public String toString() {
		return "AssetDataSelected [imeiNo=" + imeiNo + ", bmsConfigurationVersion=" + bmsConfigurationVersion + ", tcu="
				+ tcu + ", bms=" + bms + "]";
	}
	
	
	
}
