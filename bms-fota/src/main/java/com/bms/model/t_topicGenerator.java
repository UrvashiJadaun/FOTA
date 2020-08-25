package com.bms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(schema = "public", name = "t_topicgenerator")
@Entity
public class t_topicGenerator {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long sno;
	
	private String clientname;
	
	private String componenttype;
	
	private String messagingtype;
	
	private String imei;
	
	private String topicname;
	
	private String description;
	
	private String topicnomenclature;

	
	
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

	public String getComponenttype() {
		return componenttype;
	}

	public void setComponenttype(String componenttype) {
		this.componenttype = componenttype;
	}

	public String getMessagingtype() {
		return messagingtype;
	}

	public void setMessagingtype(String messagingtype) {
		this.messagingtype = messagingtype;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getTopicname() {
		return topicname;
	}

	public void setTopicname(String topicname) {
		this.topicname = topicname;
	}

	public String getTopicnomenclature() {
		return topicnomenclature;
	}

	public void setTopicnomenclature(String topicnomenclature) {
		this.topicnomenclature = topicnomenclature;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
