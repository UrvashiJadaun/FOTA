package com.bms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Iterator;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bms.Entity.AssetEntity;
import com.bms.Entity.OrganisationEntity;
import com.bms.model.Batch_Data;
import com.bms.model.CustomResponse;
import com.bms.model.t_batch;
import com.bms.model.t_batch_details;
import com.bms.service.AssetsServiceAPI;
import com.bms.service.BatchDetailsServiceApi;
import com.bms.service.BatchServiceApi;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@RestController
@CrossOrigin(origins = "*")
public class Controller {

	
		
	  @Autowired AssetsServiceAPI assetsServiceAPI;
	  
	  @Autowired BatchServiceApi batchServiceApi;
	  
	  @Autowired BatchDetailsServiceApi batchDetailsServiceApi;
	 
	
	  @GetMapping(value = "/getAllAssetAndSaveInBatch")
		public List<AssetEntity> findAllAsset()
		{
		
		  List<AssetEntity> assetList = assetsServiceAPI.findAllAssets();
		//  Timestamp	  currentTimestamp = new  java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		 
		  System.out.println("assetList :: "+assetList); 
			  t_batch batch=new t_batch();
		
			 // batch.setBatch_id(1); 
			  batch.setUsr("USER1");
			  batch.setBatch_org_name("ORG_1"); batch.setCount(assetList.size());
			  batch.setDescription("description_1"); 
			  batch.setStart_date(new
			  Timestamp(Calendar.getInstance().getTime().getTime()));
			  batch.setEnd_date(new
			  Timestamp(Calendar.getInstance().getTime().getTime()));
			  batch.setExecute("new");
			  batch.setStatus("0");
			  batchServiceApi.saveBatchRecords(batch);
			  
			  Long batchid= batchServiceApi.getMaxId() ; 
			  System.out.println("a_batchServiceApi.getMaxId()  :: "+batchid);
			  
			  for (Iterator iterator = assetList.iterator(); iterator.hasNext();) {
			  AssetEntity assetEntity = (AssetEntity) iterator.next();
			  t_batch_details   batch_details = new t_batch_details();
			  batch_details.setStart_date(new
			  Timestamp(Calendar.getInstance().getTime().getTime()));
			  batch_details.setBMS(assetEntity.getBms());
			  batch_details.setCFG("CFG_1");
			  batch_details.setIMEI(assetEntity.getImeiNo());
			  batch_details.setMax_time(6);
			  batch_details.setSend_command("send_command_1");
			  batch_details.setStatus("0"); batch_details.setTCL_version("tCL_version_1");
			  batch_details.setBatch_id(batch.getBatch_id()); batch_details.setEnd_date(new
			  Timestamp(Calendar.getInstance().getTime().getTime()));
			  batchDetailsServiceApi.saveBatchDetailsRecords(batch_details); }
			
		  return assetList;
		  }
	 
	  @GetMapping("/getBatch")
	  public List<t_batch> get_t_batch_data()
	  {
		  List<t_batch> t_batchData = batchServiceApi.findAll();
		  for (t_batch t_batch : t_batchData) {
			System.out.println(t_batch.getBatch_id());
		}
		return t_batchData;
		  
	  }
	  
	  @GetMapping("/getBatchDetails")
	  public List<t_batch_details> get_t_batch_details_data()
	  {
		  List<t_batch_details> t_batch_detailsData = batchDetailsServiceApi.findAll();
		  for (t_batch_details t_batch_detail : t_batch_detailsData) {
			System.out.println(" batch_id : "+t_batch_detail.getBatch_id()+" IMEI : "+t_batch_detail.getIMEI());
		}
		return t_batch_detailsData;
		  
	  }
	  
	  @GetMapping("/getBatchDetailsByBatchId/{batch_id}")
	  public List<t_batch_details> get_t_batch_details_data_ByBatchId(@PathVariable(name = "batch_id") Long batch_id)
	  {
		  List<t_batch_details> t_batch_detailsData = batchDetailsServiceApi.findAllByBatch_id(batch_id);
		  for (t_batch_details t_batch_detail : t_batch_detailsData) {
			System.out.println(" batch_id : "+t_batch_detail.getBatch_id()+" IMEI : "+t_batch_detail.getIMEI());
		}
		return t_batch_detailsData;
		  
	  }
	  
	  
	  
//	  @RequestMapping(value = "findOrgByToken")
//		public Optional<OrganisationEntity> getOrgDetailByToken(@RequestHeader("Authorization") String accessToken) {
//
//			return organisationservice.findOrgDetailsByToken(accessToken);
//		}
//	  
	  
		
	  @PostMapping("/upload-csv-file/{orgId}")
	    public ResponseEntity<CustomResponse>  uploadCSVFile(@RequestParam("file") MultipartFile file,Model model,@PathVariable(name = "orgId") Integer orgId) {

	        // validate file
	        if (file.isEmpty()) {
	            model.addAttribute("message", "Please select a CSV file to upload.");
	            model.addAttribute("status", false);
	        } else {	   
	        	
	        	System.out.println("test1---------------");
	        	  List<Batch_Data> bdataList=new ArrayList<Batch_Data>();	        	
	            // parse CSV file to create a list of `User` objects
	            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
	            	
					  CSVReader csvReader = new CSVReader(reader); 
					  String[] nextRecord;
					
					  // we are going to read data line by line
						  while ((nextRecord = csvReader.readNext()) != null) { 
						
							  Batch_Data bd=new Batch_Data();
							  bd.setSNo(nextRecord[0]);
							  bd.setBatch_Name(nextRecord[1]);
							  bd.setImei(nextRecord[2]);
							  bd.setTcu(nextRecord[3]);
							  bd.setBms(nextRecord[4]);
							  bd.setCfg(nextRecord[5]);
							  
							  AssetEntity asset= new AssetEntity();
							  asset.setOrgId(orgId);
							  asset.setImeiNo(Long.parseLong(bd.getImei()));
							 System.out.println("asset.getOrgId() : "+asset.getOrgId());
							 System.out.println("asset.getImeiNo() : "+asset.getImeiNo());
							  if( assetsServiceAPI.exists(asset)) {
								  System.out.println("#########################################333TRUE TRUE TRUE TUE TRUE");
							  bdataList.add(bd);
							  }
							
								
						  }
					  }catch (Exception ex) {
			                model.addAttribute("message", "An error occurred while processing the CSV file.");
			                model.addAttribute("status", false);
			            }
	           if(!bdataList.isEmpty() && bdataList!=null)
	           {
	        	   System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111 NOT EMPTY NOT NULL NOT EMPTY NOT NULL");
	        	   t_batch batch=new t_batch();
		            batch.setUsr("usr1");
					  batch.setBatch_org_name(bdataList.get(0).getBatch_Name());
					  batch.setCount(bdataList.size());
					  batch.setDescription("description_1"); 
					  batch.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime())); 
						 batch.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
						 batch.setExecute("new"); 
						 batch.setStatus("0");
						 batchServiceApi.saveBatchRecords(batch);
						 
					 
					 Long batchid= batchServiceApi.getMaxId() ; 
					 System.out.println("batchServiceApi.getMaxId()  :: "+batchid);
					 
					 for (Batch_Data emei_Batch : bdataList) {
						
						 if(!batchDetailsServiceApi.existsByImeiNo(Long.parseLong(emei_Batch.getImei())))
						  {
						  t_batch_details batch_details = new t_batch_details();
						  batch_details.setStart_date(new
						  Timestamp(Calendar.getInstance().getTime().getTime()));
						  batch_details.setBMS(emei_Batch.getBms()+"");
						  batch_details.setCFG(emei_Batch.getCfg()+"");
						  batch_details.setIMEI(Long.parseLong(emei_Batch.getImei()));
						  batch_details.setMax_time(6);
						  batch_details.setSend_command("send_command_1");
						  batch_details.setStatus("0");
						  batch_details.setTCL_version(emei_Batch.getTcu());
						  batch_details.setBatch_id(batchid); batch_details.setEnd_date(new
						  Timestamp(Calendar.getInstance().getTime().getTime()));
						  
						  batchDetailsServiceApi.saveBatchDetailsRecords(batch_details);
						  }
						
					}  
	           }

	        
	    }
	        return ResponseEntity.status(HttpStatus.OK)
	                .body(new CustomResponse(200, "file-upload-status"));
	        	

	  }
	 
	  /*
	   
	    @PostMapping("/upload-csv-file")
	    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model,@RequestHeader("Authorization") String accessToken) {

		  System.out.println("000000000000000000000000000000000000000000000000000000000000");
	        // validate file
	        if (file.isEmpty()) {
	            model.addAttribute("message", "Please select a CSV file to upload.");
	            model.addAttribute("status", false);
	        } else {	   
	        	
	        	Optional<OrganisationEntity> Optorg	=	organisationservice.findOrgDetailsByToken(accessToken);
	        	OrganisationEntity org				=	Optorg.get();
	        	String orgName						=	org.getOrgName();  
	        	int orgId							=	org.getId();
	        	
	        	
	        	  List<Batch_Data> bdataList=new ArrayList<Batch_Data>();	        	
	            // parse CSV file to create a list of `User` objects
	            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
	            	
					  CSVReader csvReader = new CSVReader(reader); 
					  String[] nextRecord;
					
					  // we are going to read data line by line
						  while ((nextRecord = csvReader.readNext()) != null) { 
						
							  Batch_Data bd=new Batch_Data();
							  bd.setSNo(nextRecord[0]);
							  bd.setBatch_Name(nextRecord[1]);
							  bd.setImei(nextRecord[2]);
							  bd.setTcu(nextRecord[3]);
							  bd.setBms(nextRecord[4]);
							  bd.setCfg(nextRecord[5]);
							  
							 // if(orgName.equals(bd.getBatch_Name())) {
								  
							  AssetEntity asset= new AssetEntity();
							  asset.setOrgId(orgId);
							  asset.setOrgId(3);
							  asset.setImeiNo(Long.parseLong(bd.getImei()));
							 
							  if( assetsServiceAPI.exists(asset)) {
								  System.out.println("#########################################333TRUE TRUE TRUE TUE TRUE");
							  bdataList.add(bd);
							  }
							//  }
								
						  }
					  }catch (Exception ex) {
			                model.addAttribute("message", "An error occurred while processing the CSV file.");
			                model.addAttribute("status", false);
			            }
	            System.out.println("************* bdataList : : "+bdataList);
	           if(!bdataList.isEmpty() && bdataList!=null)
	           {
	        	   System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111 NOT EMPTY NOT NULL NOT EMPTY NOT NULL");
	        	   t_batch batch=new t_batch();
		             // batch.setBatch_id(1); 
		             // batch.setUser("USER1");
		            batch.setUsr("usr1");
					  batch.setBatch_org_name(bdataList.get(0).getBatch_Name());
					  batch.setCount(bdataList.size());
					  batch.setDescription("description_1"); 
					  batch.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime())); 
						 batch.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
						 batch.setExecute("new"); 
						 batch.setStatus("0");
						 batchServiceApi.saveBatchRecords(batch);
						 
					 
					 Long batchid= batchServiceApi.getMaxId() ; 
					 System.out.println("batchServiceApi.getMaxId()  :: "+batchid);
					 
					 for (Batch_Data emei_Batch : bdataList) {
						  
						 System.out.println("******************** : : "+emei_Batch.getBms());
						  t_batch_details batch_details = new t_batch_details();
						  batch_details.setStart_date(new
						  Timestamp(Calendar.getInstance().getTime().getTime()));
						  batch_details.setBMS(emei_Batch.getBms()+"");
						  batch_details.setCFG(emei_Batch.getCfg()+"");
						  batch_details.setIMEI(Long.parseLong(emei_Batch.getImei()));
						  batch_details.setMax_time(6);
						  batch_details.setSend_command("send_command_1");
						  batch_details.setStatus("0");
						  batch_details.setTCL_version(emei_Batch.getTcu());
						  batch_details.setBatch_id(batchid); batch_details.setEnd_date(new
						  Timestamp(Calendar.getInstance().getTime().getTime()));
						  batchDetailsServiceApi.saveBatchDetailsRecords(batch_details);
							        }  
	           }

	        
	    }
	        return "file-upload-status";
	  }

	   */
}
