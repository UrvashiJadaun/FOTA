package com.bms.service.impl;

/*import java.text.SimpleDateFormat;import java.util.ArrayList;import java.util.Date;*/
import java.util.List;
/*import java.util.Optional;

import org.modelmapper.ModelMapper;*/
import org.springframework.beans.factory.annotation.Autowired;
/*import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;*/
import org.springframework.stereotype.Service;

import com.bms.Entity.AssetEntity;
import com.bms.model.t_batch;
import com.bms.model.t_batch_details;
import com.bms.repo.AssetRepo;

import com.bms.service.AssetsServiceAPI;
//import com.bms.services.AssetsServiceAPI;


@Service
public class AssetsServiceImpl implements AssetsServiceAPI {

	/*
	 * @Autowired private ModelMapperUtil modelMapperUtil;
	 */

	@Autowired
	private AssetRepo assetRepo;

	@Override
	public List<AssetEntity> findAllAssets() {
		System.out.println("Inside AssetsServiceImpl");
		return assetRepo.findAll();
	}

	@Override
	public AssetEntity exists(AssetEntity assetModel ) {
		boolean  flag1,flag2,flag;
		System.out.println("Inside exists AssetsServiceImpl");
		AssetEntity entity=assetRepo.findByImeiNoAndOrgId(assetModel.getImeiNo(),assetModel.getOrgId());
		
		if( entity!= null)
		{
			
			System.out.println("inside asset service impl >> entity :: "+entity.getImeiNo());
			 System.out.println("TRUE");
			//return entity;
		}
		else {
			System.out.println("*********88 NULL");
		System.out.println("FALSE");
		}
		return entity;
		/*
		 * flag1 = assetRepo.existsByImeiNo(assetModel.getImeiNo()); flag2 =
		 * assetRepo.existsByOrgId(assetModel.getOrgId());
		 * System.out.println("flag1 IMEI: "+flag1);
		 * System.out.println("flag2 orgID: "+flag2); if(flag1==true && flag2==true)
		 * flag=true; else flag=false; return flag;
		 */
		
	}

	@Override
	public boolean existsByImeiNo(long imei) {
		// TODO Auto-generated method stub
		return assetRepo.existsByImeiNo(imei);
	}

	@Override
	public List<String> get_distinct_version_of_bms() {
		// TODO Auto-generated method stub
		return assetRepo.get_distinct_version_of_bms();
	}

	@Override
	public List<String> get_distinct_version_of_tcu() {
		// TODO Auto-generated method stub
		return assetRepo.get_distinct_version_of_tcu();
	}

	@Override
	public List<String> get_distinct_version_of_config() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> get_distinct_version_of_cisconfg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AssetEntity> getAssetDataByOrgId(Integer orgId) {

		//return assetRepo.getAssetDataByOrgId(orgId);
		return assetRepo.findByOrgId(orgId);
		
	}

	
	
	

	/*
	 * @Override public List<AssetEntity> findAllAssets() {
	 * System.out.println("Inside AssetsServiceImpl"); return assetRepo.findAll(); }
	 */
	
}
