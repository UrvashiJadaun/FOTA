package com.bms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bms.model.t_FirmwareGenerator;
import com.bms.model.t_topicGenerator;

@Service
public interface TopicGeneratorServiceAPI {

	void createTopic(t_topicGenerator topicGenerator);

	List<t_topicGenerator> displayFirmware();

}
