package com.bms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bms.model.t_commands;
import com.bms.repo.CommandsRepo;
import com.bms.service.CommandsServiceAPI;

@Service
public class CommandsServiceImpl implements CommandsServiceAPI {

	@Autowired
	CommandsRepo commandsRepo;
	//

	@Override
	public String getTopic_by_tcu_and_command_name(String lowerCase) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTopic_by_tcu(String tcu) {
		return commandsRepo.findCommandTopicByTcu(tcu);
	}

	@Override
	public String getTopic_by_bcu(String bms) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTopic_by_cfg(String cfg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public t_commands findByTcu(String tcu) {
		// TODO Auto-generated method stub
		return commandsRepo.findByCommandtype(tcu);
	}

	@Override
	public t_commands findByBms(String bms) {
		// TODO Auto-generated method stub
		return commandsRepo.findByCommandtype(bms);
	}

	@Override
	public t_commands findByCfg(String cfg) {
		// TODO Auto-generated method stub
		return commandsRepo.findByCommandtype(cfg);
	}
}
