package com.bms.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bms.Entity.OrganisationEntity;
import com.bms.repo.OrganisationRepo;
import com.bms.service.OrganizationServiceAPI;

@Service
public class OrganizationServiceImpl implements OrganizationServiceAPI {

	@Autowired OrganisationRepo orgRepo;
	@Override
	public String findById(Integer orgId) {
		return orgRepo.findOrganizationNameByOrganizationId(orgId);
	}

}
