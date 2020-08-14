package com.bms.service;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bms.Entity.AssetEntity;

@Component
public interface AssetsServiceAPI {
	
	public List<AssetEntity> findAllAssets();

	boolean exists(AssetEntity assetModel);

	public boolean existsByImeiNo(long imei);

}
