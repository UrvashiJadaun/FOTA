package com.bms.service;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bms.Entity.AssetEntity;

@Component
public interface AssetsServiceAPI {
	
	public List<AssetEntity> findAllAssets();

	AssetEntity exists(AssetEntity assetModel);

	public boolean existsByImeiNo(long imei);

	public List<String> get_distinct_version_of_bms();

	public List<String> get_distinct_version_of_tcu();

	public List<String> get_distinct_version_of_config();

	public List<String> get_distinct_version_of_cisconfg();

}
