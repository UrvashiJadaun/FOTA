package com.bms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bms.model.t_FirmwareGenerator;
import com.bms.model.t_topicGenerator;
import com.bms.repo.TopicGeneratorRepo;
import com.bms.service.TopicGeneratorServiceAPI;

@Service
public class TopicGeneratorServiceIMPL implements TopicGeneratorServiceAPI{

	@Autowired
	TopicGeneratorRepo topicGeneratorRepo;
	
	@Override
	public void createTopic(t_topicGenerator topicGenerator) {
		topicGeneratorRepo.save(topicGenerator);
	}

	@Override
	public List<t_topicGenerator> displayFirmware() {
		// TODO Auto-generated method stub
		return topicGeneratorRepo.findAll();
	}

}
