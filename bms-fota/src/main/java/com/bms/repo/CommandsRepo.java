package com.bms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bms.model.t_commands;

public interface CommandsRepo extends JpaRepository<t_commands, Integer> {

	@Query(value="select fotaservername from t_commands where commandtype=?",nativeQuery = true)
	String findCommandTopicByTcu(String tcu);

	t_commands findByCommandtype(String tcu);

}
