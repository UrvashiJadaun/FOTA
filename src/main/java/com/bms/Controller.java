package com.bms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bms.Entity.AssetEntity;
import com.bms.model.AssetDataSelected;
import com.bms.model.Batch_Data;
import com.bms.model.CustomResponse;
import com.bms.model.Version;
import com.bms.model.t_batch;
import com.bms.model.t_batch_details;
import com.bms.model.t_batch_details_log;
import com.bms.model.t_commands;
import com.bms.mqtt.publisher.Publisher;
import com.bms.service.AssetsServiceAPI;
import com.bms.service.BatchDetailsLogServiceApi;
import com.bms.service.BatchDetailsServiceApi;
import com.bms.service.BatchServiceApi;
import com.bms.service.CommandsServiceAPI;
import com.bms.service.OrganizationServiceAPI;
import com.google.gson.Gson;
import com.opencsv.CSVReader;



@RestController
@CrossOrigin(origins = "*")
public class Controller {

	@Autowired
	Publisher publish;
	@Autowired
	CommandsServiceAPI commandsServiceAPI;
	@Autowired
	OrganizationServiceAPI organizationServiceAPI;

	@Autowired
	AssetsServiceAPI assetsServiceAPI;

	@Autowired
	BatchServiceApi batchServiceApi;

	@Autowired
	BatchDetailsServiceApi batchDetailsServiceApi;

	@Autowired
	BatchDetailsLogServiceApi batchDetailsLogServiceApi;

	
	public static final int _bms = 1;
	public static final int _tcu = 2;
	public static final int _config = 3;
	public static final int _sysconfg = 4;
	
	public static final int _reset = 1;
	public static final int _show_config = 2;
	public static final int _show_credential= 3;
	public static final int _show_ota = 4;
	public static final int _show_pub_topics = 5;
	public static final int _show_sub_topics = 6;
	public static final int _set_server_name = 7;
	public static final int _primary_port = 8;
	public static final int _fota_server_name = 9;
	public static final int _save_config = 10;
	
	//public static final Map<Integer,String> comman_map=
	public static final Map<Integer,String>  command_map = new HashMap<Integer, String>();
	static {
	command_map.put(_reset, "reset");
	command_map.put(_reset, "show_config");
	command_map.put(_reset, "show_credential");
	command_map.put(_reset, "show_ota");
	command_map.put(_reset, "show_pub_topics");
	command_map.put(_reset, "show_sub_topics");
	command_map.put(_reset, "set_server_name");
	command_map.put(_reset, "primary_port");
	command_map.put(_reset, "fota_server_name");
	command_map.put(_reset, "save_config");
	}

	


	
	
	@PostMapping("/single_imei-run/{orgId}/{imei}/{command}")
	public ResponseEntity<CustomResponse> single_IMEI_run(@PathVariable(name = "orgId") Integer orgId,
			@PathVariable(name = "imei") long imei,
			@PathVariable(name = "command") int command,
			@RequestParam("request") String json)
	{
				 String tcu,tcuVersion,bms,bmsVersion,cfg,cfgVersion;
					tcuVersion=bmsVersion=cfgVersion=null;
					tcu=bms=cfg=null;
					
					 Gson gson = new Gson();
				      Version ver = gson.fromJson(json, Version.class);
				      tcu=ver.getTcu();
				      bms=ver.getBms();
				      cfg=ver.getCfg();
				      
				     if(ver.getTcu()!=null) {
				    	 tcuVersion=ver.getTcuVersion();
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				     }
				     if(ver.getBms()!=null) {
				    	 bmsVersion=ver.getBmsVersion();
				    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
				     }
				     if(ver.getCfg()!=null) {
				    	 cfgVersion=ver.getCfgVersion();
					      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
				     }
				     if(ver.getBms()==null)
				    	 cfg=null;
		     // String commandName=command_map.get(command);
		     
		    
				/*
				 * System.out.println("test1 :: "+commandEntity1.getCommandtype());
				 * System.out.println("test2 :: "+commandEntity2.getCommandtype());
				 * System.out.println("test2 :: "+commandEntity3.getCommandtype());
				 */
		      String tcu_topic1="";
		      String bms_topic2="";
		      String cfg_topic3="";
		      
		      switch (command) {
					case 1:		
						// RESET command for tcs bms and cfg
						  if(ver.getTcu()!=null) {
						    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
						    	 tcu_topic1=commandEntity1.getReset();
						    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
						     }
						     if(ver.getBms()!=null) {
						    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
						    	  bms_topic2=commandEntity2.getReset();
						    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
						     }
						     if(ver.getCfg()!=null) {
						    	 cfgVersion=ver.getCfgVersion();
							      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
							      cfg_topic3=commandEntity3.getReset();
							      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
						     }
						     
						break;
					case 2:	
						// SHOW_CONFIG command for tcs bms and cfg
						 if(ver.getTcu()!=null) {
							
					    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
					    	 tcu_topic1=commandEntity1.getShowconfig();
					    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
					     }
					     if(ver.getBms()!=null) {
					    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
					    	  bms_topic2=commandEntity2.getShowconfig();
					    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
					     }
					     if(ver.getCfg()!=null) {
					    	 cfgVersion=ver.getCfgVersion();
						      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
						      cfg_topic3=commandEntity3.getShowconfig();
						      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
					     }
						break;
					case 3:		
						// command for tcs bms and cfg
						 if(ver.getTcu()!=null) {
								
					    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
					    	 tcu_topic1=commandEntity1.getShowcredential();
					    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
					     }
					     if(ver.getBms()!=null) {
					    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
					    	  bms_topic2=commandEntity2.getShowcredential();
					    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
					     }
					     if(ver.getCfg()!=null) {
					    	 cfgVersion=ver.getCfgVersion();
						      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
						      cfg_topic3=commandEntity3.getShowcredential();
						      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
					     }
						break;
					case 4:	
						// command for tcs bms and cfg
						 if(ver.getTcu()!=null) {
								
					    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
					    	 tcu_topic1=commandEntity1.getShowota();
					    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
					     }
					     if(ver.getBms()!=null) {
					    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
					    	  bms_topic2=commandEntity2.getShowota();
					    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
					     }
					     if(ver.getCfg()!=null) {
					    	 cfgVersion=ver.getCfgVersion();
						      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
						      cfg_topic3=commandEntity3.getShowota();
						      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
					     }
						break;
					case 5:	
						// command for tcs bms and cfg
						 if(ver.getTcu()!=null) {
								
					    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
					    	 tcu_topic1=commandEntity1.getShowpubtopics();
					    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
					     }
					     if(ver.getBms()!=null) {
					    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
					    	  bms_topic2=commandEntity2.getShowpubtopics();
					    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
					     }
					     if(ver.getCfg()!=null) {
					    	 cfgVersion=ver.getCfgVersion();
						      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
						      cfg_topic3=commandEntity3.getShowpubtopics();
						      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
					     }
						break;
					case 6:	
						// command for tcs bms and cfg
						 if(ver.getTcu()!=null) {
								
					    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
					    	 tcu_topic1=commandEntity1.getShowsubtopics();
					    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
					     }
					     if(ver.getBms()!=null) {
					    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
					    	  bms_topic2=commandEntity2.getShowsubtopics();
					    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
					     }
					     if(ver.getCfg()!=null) {
					    	 cfgVersion=ver.getCfgVersion();
						      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
						      cfg_topic3=commandEntity3.getShowsubtopics();
						      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
					     }
						break;
					case 7:	
						// command for tcs bms and cfg
						 if(ver.getTcu()!=null) {
								
					    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
					    	 tcu_topic1=commandEntity1.getSetservername();
					    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
					     }
					     if(ver.getBms()!=null) {
					    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
					    	  bms_topic2=commandEntity2.getSetservername();
					    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
					     }
					     if(ver.getCfg()!=null) {
					    	 cfgVersion=ver.getCfgVersion();
						      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
						      cfg_topic3=commandEntity3.getSetservername();
						      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
					     }
						break;
					case 8:		
						// command for tcs bms and cfg
						 if(ver.getTcu()!=null) {
								
					    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
					    	 tcu_topic1=commandEntity1.getPrimaryport();
					    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
					     }
					     if(ver.getBms()!=null) {
					    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
					    	  bms_topic2=commandEntity2.getPrimaryport();
					    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
					     }
					     if(ver.getCfg()!=null) {
					    	 cfgVersion=ver.getCfgVersion();
						      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
						      cfg_topic3=commandEntity3.getPrimaryport();
						      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
					     }
						break; 
					case 9:		
						// command for tcs bms and cfg
						 if(ver.getTcu()!=null) {
								
					    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
					    	 tcu_topic1=commandEntity1.getFotaservername();
					    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
					     }
					     if(ver.getBms()!=null) {
					    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
					    	  bms_topic2=commandEntity2.getFotaservername();
					    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
					     }
					     if(ver.getCfg()!=null) {
					    	 cfgVersion=ver.getCfgVersion();
						      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
						      cfg_topic3=commandEntity3.getFotaservername();
						      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
					     }
						break;
					case 10:	
						// command for tcs bms and cfg
						 if(ver.getTcu()!=null) {
								
					    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
					    	 tcu_topic1=commandEntity1.getSaveconfig();
					    	 tcu_topic1=tcu_topic1.replace("<VER>", tcuVersion);
					     }
					     if(ver.getBms()!=null) {
					    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
					    	  bms_topic2=commandEntity2.getSaveconfig();
					    	  bms_topic2=bms_topic2.replace("<VER>", bmsVersion);
					     }
					     if(ver.getCfg()!=null) {
					    	 cfgVersion=ver.getCfgVersion();
						      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
						      cfg_topic3=commandEntity3.getSaveconfig();
						      cfg_topic3=cfg_topic3.replace("<VER>", cfgVersion);
					     }
						break;
		
					default:
						break;
			}
		      
		      //publish commands tcu, bms, cfg
		      boolean t1 = false,t2 = false,t3 = false;
		      String response ="receive",status="done";
		      if(tcu!=null) {
		    	  	t1= publish.publish(tcu_topic1.replace("#", "")+tcuVersion);
		    	  	if(t1!=false)
		    	  	batchDetailsLogServiceApi.update_records(response,status,new Timestamp(Calendar.getInstance().getTime().getTime()),imei,tcu.toUpperCase());
		      }
		      if(bms!=null) {
		    	  if(t2!=false)
		    	  	t2= publish.publish(bms_topic2.replace("#", "")+bmsVersion);
		    	  	batchDetailsLogServiceApi.update_records(response,status,new Timestamp(Calendar.getInstance().getTime().getTime()),imei,bms.toUpperCase());
		      }
		      if(cfg!=null) {
		    	  if(t3=false)
		    	  	t3= publish.publish(cfg_topic3.replace("#", "")+cfgVersion);
		    	  	batchDetailsLogServiceApi.update_records(response,status,new Timestamp(Calendar.getInstance().getTime().getTime()),imei,bms.toUpperCase());
		      }
		      System.out.println("t1 : "+t1);
		      System.out.println("t2 : "+t2);
		      System.out.println("t3 : "+t3);
		    
		return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "success"));
	}

	 @PostMapping("/push_fota_single/{orgId}/{imei}")
	 	public ResponseEntity<CustomResponse> push_fota_single(@PathVariable(name = "orgId") Integer orgId,
			@PathVariable(name = "imei") long imei,
			@RequestParam("request") String json)
	 	{
		 
		 String tcu,tcuVersion,bms,bmsVersion,cfg,cfgVersion;
			tcuVersion=bmsVersion=cfgVersion=null;
			tcu=bms=cfg=null;
			
			 Gson gson = new Gson();
		      Version ver = gson.fromJson(json, Version.class);
		      tcu=ver.getTcu();
		      bms=ver.getBms();
		      cfg=ver.getCfg();
		      
		     if(ver.getTcu()!=null)
		    	 tcuVersion=ver.getTcuVersion();
		     if(ver.getBms()!=null)
		    	 bmsVersion=ver.getBmsVersion();
		     if(ver.getCfg()!=null)
		    	 cfgVersion=ver.getCfgVersion();
		     if(ver.getBms()==null)
		    	 cfg=null;
		      
		     
			String orgName = organizationServiceAPI.findById(orgId);
			AssetEntity asset = new AssetEntity();
			asset.setOrgId(orgId);
			asset.setImeiNo(imei);
			AssetEntity assetEntity = assetsServiceAPI.exists(asset);
			if (assetEntity != null) {
				
				System.out.println("asset Entity not null");
				Batch_Data emei_Batch = new Batch_Data();
				// bd.setSNo(nextRecord[0]);
				emei_Batch.setBatch_Name(orgName);
				emei_Batch.setImei(assetEntity.getImeiNo() + "");
				emei_Batch.setTcu(assetEntity.getTcu());
				emei_Batch.setBms(assetEntity.getBms());
				emei_Batch.setCfg(assetEntity.getBmsConfigurationVersion() + "");
				if(tcu!=null)
				{
					System.out.println("inside tcu");
					t_batch_details_log log=new t_batch_details_log();
					String tcuCommand="$TMCFG|START OTA "+tcuVersion+"/#";
					log.setIMEI(Long.parseLong(emei_Batch.getImei()));
					log.setOrgName(orgName);
					log.setResponse("send");
					log.setStatus("running");
					log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
					log.setBatch_id(0);
					log.setCommand(tcuCommand);
					log.setType("TCU");
					
					batchDetailsLogServiceApi.save_tcu_command(log);
				}
				if(bms!=null)
				{
					System.out.println("inside bms");
					t_batch_details_log log=new t_batch_details_log();
					String bmsCommand="$TMCFG|START BMSOTA "+bmsVersion+"/#";
					log.setIMEI(Long.parseLong(emei_Batch.getImei()));
					log.setOrgName(orgName);
					log.setResponse("send");
					log.setStatus("running");
					log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
					log.setBatch_id(0);
					log.setCommand(bmsCommand);
					log.setType("BMS");
					
					batchDetailsLogServiceApi.save_bms_command(log);
				}
				if(bms!=null && cfg!=null)
				{
					System.out.println("inside cfg");
					t_batch_details_log log=new t_batch_details_log();
					String cfgCommand="$TMCFG|START CFGOTA "+cfgVersion+"/#";
					log.setIMEI(Long.parseLong(emei_Batch.getImei()));
					log.setOrgName(orgName);
					log.setResponse("send");
					log.setStatus("running");
					log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
					log.setBatch_id(0);
					log.setCommand(cfgCommand);
					log.setType("CFG");
					
					batchDetailsLogServiceApi.save_cfg_command(log);
				}
				if (!batchDetailsServiceApi.existsByImeiNo(Long.parseLong(emei_Batch.getImei()))) {
					t_batch_details batch_details = new t_batch_details();
					batch_details.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
					batch_details.setBMS(emei_Batch.getBms() + "");
					batch_details.setCFG(emei_Batch.getCfg() + "");
					batch_details.setIMEI(Long.parseLong(emei_Batch.getImei()));
					batch_details.setMax_time(6);
					batch_details.setSend_command("send_command_1");
					batch_details.setStatus("0");
					batch_details.setTCL_version(emei_Batch.getTcu());
					batch_details.setBatch_id(0);
					batch_details.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));

					batchDetailsServiceApi.saveBatchDetailsRecords(batch_details);
				}
				
				
			}
		return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "success"));
		
	}
//@RequestBody String json
//	@PostMapping("/upload-csv-file/{orgId}/{componentType}/{componentVersion}")
  @PostMapping("/upload-csv-file/{orgId}")
	public ResponseEntity<CustomResponse> uploadCSVFile(@RequestParam("file") MultipartFile file, Model model,
			@PathVariable(name = "orgId") Integer orgId,@RequestParam("request") String json) {

		// validate file
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new CustomResponse(417, "file-is-empty"));

			/*
			 * model.addAttribute("message", "Please select a CSV file to upload.");
			 * model.addAttribute("status", false);
			 */
		} else {

			String tcu,tcuVersion,bms,bmsVersion,cfg,cfgVersion;
			tcuVersion=bmsVersion=cfgVersion=null;
			tcu=bms=cfg=null;
			
			 Gson gson = new Gson();
		      Version ver = gson.fromJson(json, Version.class);
		      tcu=ver.getTcu();
		      bms=ver.getBms();
		      cfg=ver.getCfg();
		      
		     if(ver.getTcu()!=null)
		    	 tcuVersion=ver.getTcuVersion();
		     if(ver.getBms()!=null)
		    	 bmsVersion=ver.getBmsVersion();
		     if(ver.getCfg()!=null)
		    	 cfgVersion=ver.getCfgVersion();
		     if(ver.getBms()==null)
		    	 cfg=null;
		      
		     
			String orgName = organizationServiceAPI.findById(orgId);
			List<Batch_Data> bdataList = new ArrayList<Batch_Data>();
			// parse CSV file to create a list of `User` objects
			try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

				CSVReader csvReader = new CSVReader(reader);
				String[] nextRecord;
				// we are going to read data line by line
				while ((nextRecord = csvReader.readNext()) != null) {
					AssetEntity asset = new AssetEntity();
					asset.setOrgId(orgId);
					asset.setImeiNo(Long.parseLong(nextRecord[0]));
					System.out.println("asset.getOrgId() : " + asset.getOrgId());
					System.out.println("asset.getImeiNo() : " + asset.getImeiNo());

					AssetEntity assetEntity = assetsServiceAPI.exists(asset);
					if (assetEntity != null) {
						
						System.out.println("asset Entity not null");
						Batch_Data bd = new Batch_Data();
						// bd.setSNo(nextRecord[0]);
						bd.setBatch_Name(orgName);
						bd.setImei(assetEntity.getImeiNo() + "");
						bd.setTcu(assetEntity.getTcu());
						bd.setBms(assetEntity.getBms());
						bd.setCfg(assetEntity.getBmsConfigurationVersion() + "");
						 bdataList.add(bd);
					}
				}

			} catch (Exception ex) {
				System.out.println("inside catch block");
				ex.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new CustomResponse(500, "An error occurred while processing the CSV file"));
				// model.addAttribute("message", "An error occurred while processing the CSV
				// file.");
				// model.addAttribute("status", false);
			}
			if (!bdataList.isEmpty() && bdataList != null) {
				
				t_batch batch = new t_batch();
				batch.setUsr("usr1");
				batch.setBatch_org_name(bdataList.get(0).getBatch_Name());
				batch.setCount(bdataList.size());
				batch.setDescription("description_1");
				batch.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
				batch.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
				batch.setExecute("new");
				batch.setStatus("0");
				batchServiceApi.saveBatchRecords(batch);

				Long batchid = batchServiceApi.getMaxId();
				System.out.println("batchServiceApi.getMaxId()  :: " + batchid);

				for (Batch_Data emei_Batch : bdataList) {

					if(tcu!=null)
					{
						t_batch_details_log log=new t_batch_details_log();
						String tcuCommand="$TMCFG|START OTA "+tcuVersion+"/#";
						log.setIMEI(Long.parseLong(emei_Batch.getImei()));
						log.setOrgName(orgName);
						log.setResponse("send");
						log.setStatus("running");
						log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
						log.setBatch_id(batchid);
						log.setCommand(tcuCommand);
						log.setType("TCU");
						
						batchDetailsLogServiceApi.save_tcu_command(log);
					}
					if(bms!=null)
					{
						t_batch_details_log log=new t_batch_details_log();
						String bmsCommand="$TMCFG|START BMSOTA "+bmsVersion+"/#";
						log.setIMEI(Long.parseLong(emei_Batch.getImei()));
						log.setOrgName(orgName);
						log.setResponse("send");
						log.setStatus("running");
						log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
						log.setBatch_id(batchid);
						log.setCommand(bmsCommand);
						log.setType("BMS");
						batchDetailsLogServiceApi.save_bms_command(log);
					}
					if(bms!=null && cfg!=null)
					{
						t_batch_details_log log=new t_batch_details_log();
						String cfgCommand="$TMCFG|START CFGOTA "+cfgVersion+"/#";
						log.setIMEI(Long.parseLong(emei_Batch.getImei()));
						log.setOrgName(orgName);
						log.setResponse("send");
						log.setStatus("running");
						log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
						log.setBatch_id(batchid);
						log.setCommand(cfgCommand);
						log.setType("CFG");
						
						batchDetailsLogServiceApi.save_cfg_command(log);
					}
					
					if (!batchDetailsServiceApi.existsByImeiNo(Long.parseLong(emei_Batch.getImei()))) {
						t_batch_details batch_details = new t_batch_details();
						batch_details.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
						batch_details.setBMS(emei_Batch.getBms() + "");
						batch_details.setCFG(emei_Batch.getCfg() + "");
						batch_details.setIMEI(Long.parseLong(emei_Batch.getImei()));
						batch_details.setMax_time(6);
						batch_details.setSend_command("send_command_1");
						batch_details.setStatus("0");
						batch_details.setTCL_version(emei_Batch.getTcu());
						batch_details.setBatch_id(batchid);
						batch_details.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));

						batchDetailsServiceApi.saveBatchDetailsRecords(batch_details);
					}

				}
			} else
				System.out.println("0000000000000000000000000000 list is empty");

		}
		return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "file-upload-status"));

	}

	/*
	 * 
	 * @PostMapping("/upload-csv-file") public String
	 * uploadCSVFile(@RequestParam("file") MultipartFile file, Model
	 * model,@RequestHeader("Authorization") String accessToken) {
	 * 
	 * System.out.println(
	 * "000000000000000000000000000000000000000000000000000000000000"); // validate
	 * file if (file.isEmpty()) { model.addAttribute("message",
	 * "Please select a CSV file to upload."); model.addAttribute("status", false);
	 * } else {
	 * 
	 * Optional<OrganisationEntity> Optorg =
	 * organisationservice.findOrgDetailsByToken(accessToken); OrganisationEntity
	 * org = Optorg.get(); String orgName = org.getOrgName(); int orgId =
	 * org.getId();
	 * 
	 * 
	 * List<Batch_Data> bdataList=new ArrayList<Batch_Data>(); // parse CSV file to
	 * create a list of `User` objects try (Reader reader = new BufferedReader(new
	 * InputStreamReader(file.getInputStream()))) {
	 * 
	 * CSVReader csvReader = new CSVReader(reader); String[] nextRecord;
	 * 
	 * // we are going to read data line by line while ((nextRecord =
	 * csvReader.readNext()) != null) {
	 * 
	 * Batch_Data bd=new Batch_Data(); bd.setSNo(nextRecord[0]);
	 * bd.setBatch_Name(nextRecord[1]); bd.setImei(nextRecord[2]);
	 * bd.setTcu(nextRecord[3]); bd.setBms(nextRecord[4]); bd.setCfg(nextRecord[5]);
	 * 
	 * // if(orgName.equals(bd.getBatch_Name())) {
	 * 
	 * AssetEntity asset= new AssetEntity(); asset.setOrgId(orgId);
	 * asset.setOrgId(3); asset.setImeiNo(Long.parseLong(bd.getImei()));
	 * 
	 * if( assetsServiceAPI.exists(asset)) { System.out.
	 * println("#########################################333TRUE TRUE TRUE TUE TRUE"
	 * ); bdataList.add(bd); } // }
	 * 
	 * } }catch (Exception ex) { model.addAttribute("message",
	 * "An error occurred while processing the CSV file.");
	 * model.addAttribute("status", false); }
	 * System.out.println("************* bdataList : : "+bdataList);
	 * if(!bdataList.isEmpty() && bdataList!=null) { System.out.
	 * println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111 NOT EMPTY NOT NULL NOT EMPTY NOT NULL"
	 * ); t_batch batch=new t_batch(); // batch.setBatch_id(1); //
	 * batch.setUser("USER1"); batch.setUsr("usr1");
	 * batch.setBatch_org_name(bdataList.get(0).getBatch_Name());
	 * batch.setCount(bdataList.size()); batch.setDescription("description_1");
	 * batch.setStart_date(new
	 * Timestamp(Calendar.getInstance().getTime().getTime())); batch.setEnd_date(new
	 * Timestamp(Calendar.getInstance().getTime().getTime()));
	 * batch.setExecute("new"); batch.setStatus("0");
	 * batchServiceApi.saveBatchRecords(batch);
	 * 
	 * 
	 * Long batchid= batchServiceApi.getMaxId() ;
	 * System.out.println("batchServiceApi.getMaxId()  :: "+batchid);
	 * 
	 * for (Batch_Data emei_Batch : bdataList) {
	 * 
	 * System.out.println("******************** : : "+emei_Batch.getBms());
	 * t_batch_details batch_details = new t_batch_details();
	 * batch_details.setStart_date(new
	 * Timestamp(Calendar.getInstance().getTime().getTime()));
	 * batch_details.setBMS(emei_Batch.getBms()+"");
	 * batch_details.setCFG(emei_Batch.getCfg()+"");
	 * batch_details.setIMEI(Long.parseLong(emei_Batch.getImei()));
	 * batch_details.setMax_time(6);
	 * batch_details.setSend_command("send_command_1");
	 * batch_details.setStatus("0");
	 * batch_details.setTCL_version(emei_Batch.getTcu());
	 * batch_details.setBatch_id(batchid); batch_details.setEnd_date(new
	 * Timestamp(Calendar.getInstance().getTime().getTime()));
	 * batchDetailsServiceApi.saveBatchDetailsRecords(batch_details); } }
	 * 
	 * 
	 * } return "file-upload-status"; }
	 * 
	 */
  
	
	@RequestMapping("/get_asset_data/{OrgId}")
	public List<AssetDataSelected> getAssetDataByOrgId(@PathVariable(name = "OrgId") Integer OrgId)
	{
		List<AssetEntity> list1			=	assetsServiceAPI.getAssetDataByOrgId(OrgId);
		List<AssetDataSelected> list2	=	new ArrayList<AssetDataSelected>();
		for (AssetEntity assetEntity : list1) {
			AssetDataSelected obj		=	new AssetDataSelected();
			obj.setBms(assetEntity.getBms());
			obj.setBmsConfigurationVersion(assetEntity.getBmsConfigurationVersion());
			obj.setTcu(assetEntity.getTcu());
			obj.setImeiNo(assetEntity.getImeiNo());
			obj.setBin(assetEntity.getBin());
			list2.add(obj);
		} 
		
		
		return list2;
	}

	@RequestMapping("/get_distinct_version/{componentType}")
	public List<String> get_distinct_version_of_componentType(@PathVariable(name = "componentType") int componentType) {
		if (componentType == _bms)
			return assetsServiceAPI.get_distinct_version_of_bms();
		if (componentType == _tcu)
			return assetsServiceAPI.get_distinct_version_of_tcu();
		if (componentType == _config)
			return assetsServiceAPI.get_distinct_version_of_config();
		if (componentType == _sysconfg)
			return assetsServiceAPI.get_distinct_version_of_cisconfg();
		return null;
	}

	//order by date
	@RequestMapping("/get_t_batch_details_logByIMEI/{IMEI}")
	public List<t_batch_details_log> get_t_batch_details_logByIMEI(@PathVariable(name = "IMEI") Long IMEI) {
		return batchDetailsLogServiceApi.get_t_batch_details_logByIMEI(IMEI);

	}

	@GetMapping("/getBatch")
	public List<t_batch> get_t_batch_data() {
		List<t_batch> t_batchData = batchServiceApi.findAll();
		for (t_batch t_batch : t_batchData) {
			System.out.println(t_batch.getBatch_id());
		}
		return t_batchData;

	}

	@GetMapping("/getBatchDetails")
	public List<t_batch_details> get_t_batch_details_data() {
		List<t_batch_details> t_batch_detailsData = batchDetailsServiceApi.findAll();
		for (t_batch_details t_batch_detail : t_batch_detailsData) {
			System.out.println(" batch_id : " + t_batch_detail.getBatch_id() + " IMEI : " + t_batch_detail.getIMEI());
		}
		return t_batch_detailsData;

	}

	@GetMapping("/getBatchDetailsByBatchId/{batch_id}")
	public List<t_batch_details> get_t_batch_details_data_ByBatchId(@PathVariable(name = "batch_id") Long batch_id) {
		List<t_batch_details> t_batch_detailsData = batchDetailsServiceApi.findAllByBatch_id(batch_id);
		for (t_batch_details t_batch_detail : t_batch_detailsData) {
			System.out.println(" batch_id : " + t_batch_detail.getBatch_id() + " IMEI : " + t_batch_detail.getIMEI());
		}
		return t_batch_detailsData;

	}
	
	/*
	 * @DeleteMapping("/delete_batch_ByBatchId/{batch_id}") public
	 * ResponseEntity<CustomResponse> delete_batch_ByBatchId(@PathVariable(name =
	 * "batch_id") Long batch_id) { int x=
	 * batchDetailsServiceApi.deleteByBatch_id(batch_id);
	 * System.out.println("x : "+x); int
	 * y=batchServiceApi.deleteByBatch_id(batch_id); System.out.println("y : "+y);
	 * return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200,
	 * "success"));
	 * 
	 * }
	 */
//	  @RequestMapping(value = "findOrgByToken")
//		public Optional<OrganisationEntity> getOrgDetailByToken(@RequestHeader("Authorization") String accessToken) {
//
//			return organisationservice.findOrgDetailsByToken(accessToken);
//		}
//	  
  
	@GetMapping(value = "/getAllAssetAndSaveInBatch")
	public List<AssetEntity> findAllAsset() {

		List<AssetEntity> assetList = assetsServiceAPI.findAllAssets();
		// Timestamp currentTimestamp = new
		// java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

		System.out.println("assetList :: " + assetList);
		t_batch batch = new t_batch();

		// batch.setBatch_id(1);
		batch.setUsr("USER1");
		batch.setBatch_org_name("ORG_1");
		batch.setCount(assetList.size());
		batch.setDescription("description_1");
		batch.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
		batch.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
		batch.setExecute("new");
		batch.setStatus("0");
		batchServiceApi.saveBatchRecords(batch);

		Long batchid = batchServiceApi.getMaxId();
		System.out.println("a_batchServiceApi.getMaxId()  :: " + batchid);

		for (Iterator iterator = assetList.iterator(); iterator.hasNext();) {
			AssetEntity assetEntity = (AssetEntity) iterator.next();
			t_batch_details batch_details = new t_batch_details();
			batch_details.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
			batch_details.setBMS(assetEntity.getBms());
			batch_details.setCFG("CFG_1");
			batch_details.setIMEI(assetEntity.getImeiNo());
			batch_details.setMax_time(6);
			batch_details.setSend_command("send_command_1");
			batch_details.setStatus("0");
			batch_details.setTCL_version("tCL_version_1");
			batch_details.setBatch_id(batch.getBatch_id());
			batch_details.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
			batchDetailsServiceApi.saveBatchDetailsRecords(batch_details);
		}

		return assetList;
	}
	
	
	@PostMapping("/getJson")
	public String getJson(@RequestBody String json) {
		
		  Gson gson = new Gson();
		  JSONObject jsonObj=new JSONObject(json); 		 
		  JSONArray jsonArray=jsonObj.getJSONArray("Version1"); 
		  for (int i=0;i<jsonArray.length();i++)
		  { 
			  JSONObject obj=jsonArray.getJSONObject(i);
			  Version emp = gson.fromJson(json, Version.class);
			  System.out.println("emp : "+obj);
		      System.out.println(emp.getTcu());
		      System.out.println(emp.getTcuVersion());
		      System.out.println(emp.getBms());
		      System.out.println(emp.getBmsVersion());
		      System.out.println(emp.getCfg());
		      System.out.println(emp.getCfgVersion());
		  }
		
			/*
			 * JSONObject jsonObj=new JSONObject(json); JSONArray
			 * jsonArray=jsonObj.getJSONArray("dataArray"); for (int
			 * i=0;i<jsonArray.length();i++) { JSONObject obj=jsonArray.getJSONObject(i);
			 * System.out.println(obj.get("d1")); System.out.println(obj.get("d2")); }
			 * return jsonObj.toString();
			 */
		 
		  return "ok";
	}

	@PostMapping("/get")
	public void get(@RequestBody String json) {
		
		System.out.println(json);
		 Gson gson = new Gson();
	      Version emp = gson.fromJson(json, Version.class);
	      System.out.println(emp.getTcu());
	      System.out.println(emp.getTcuVersion());
	      System.out.println(emp.getBms());
	      System.out.println(emp.getBmsVersion());
	      System.out.println(emp.getCfg());
	      System.out.println(emp.getCfgVersion());
		/*
		 * //System.out.println("_________-------- vir : "+vir); JSONArray jsonArray1=
		 * new JSONArray(jsonArray); List<Version> contacts = new ArrayList<>(); for
		 * (int i = 0; i < jsonArray.length(); i++) { JSONObject rec = (JSONObject)
		 * jsonArray.get(i); Version c = new Version();
		 * c.setComponent(rec.optString("component"));
		 * c.setVersion(rec.optString("version")); c.setId(rec.optString("Id"));
		 * c.setName(rec.optString("Name")); //
		 * c.setAtributes(rec.optJSONObject("attributes")) contacts.add(c); }
		 * System.out.println("contacts : "+contacts);
		 */

		/*
		 * for (Version version : vir) {
		 * System.out.println("Component : "+version.getComponent());
		 * System.out.println("Vesion : "+version.getVersion()); }
		 */

	}
  
}
