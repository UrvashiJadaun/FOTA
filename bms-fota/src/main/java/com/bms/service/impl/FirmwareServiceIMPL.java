package com.bms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bms.model.t_FirmwareGenerator;
import com.bms.repo.FirmwareRepo;
import com.bms.service.FirmwareServiceAPI;

@Service
public class FirmwareServiceIMPL implements FirmwareServiceAPI {

	@Autowired
	FirmwareRepo firmwareRepo;
	
	@Override
	public void createFirmware(t_FirmwareGenerator firmware) {
		firmwareRepo.save(firmware);
	}

	@Override
	public List<t_FirmwareGenerator> displayFirmware() {
		return firmwareRepo.findAll();
	}

}
