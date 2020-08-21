package com.bms.service;

import org.springframework.stereotype.Service;

import com.bms.model.t_commands;

@Service
public interface CommandsServiceAPI {

	String getTopic_by_tcu_and_command_name(String lowerCase);

	String getTopic_by_tcu(String tcu);

	String getTopic_by_bcu(String bms);

	String getTopic_by_cfg(String cfg);

	t_commands findByTcu(String lowerCase);

	t_commands findByBms(String lowerCase);

	t_commands findByCfg(String lowerCase);

}
