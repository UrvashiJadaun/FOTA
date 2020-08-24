package com.bms.model;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(schema = "public", name = "topicGenerator")
@Entity
public class TopicGenerator {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int sNo;
	
	private String clientName;
	
	private String componentType;
	
	private String messagingType;
	
	private String IMEI;
	
	private String topicName;
	
	private String description;
	
	private String topicNomenClature;

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

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getMessagingType() {
		return messagingType;
	}

	public void setMessagingType(String messagingType) {
		this.messagingType = messagingType;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTopicNomenClature() {
		return topicNomenClature;
	}

	public void setTopicNomenClature(String topicNomenClature) {
		this.topicNomenClature = topicNomenClature;
	}
	
	
	
}
