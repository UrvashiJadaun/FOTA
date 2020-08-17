package com.bms.service;

import org.springframework.stereotype.Service;

import com.bms.Entity.OrganisationEntity;

@Service
public interface OrganizationServiceAPI {

	public String findById(Integer orgId);

}
