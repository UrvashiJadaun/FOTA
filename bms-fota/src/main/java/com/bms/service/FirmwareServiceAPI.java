package com.bms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bms.model.t_FirmwareGenerator;

@Service
public interface FirmwareServiceAPI {

	void createFirmware(t_FirmwareGenerator firmware);

	List<t_FirmwareGenerator> displayFirmware();

}
