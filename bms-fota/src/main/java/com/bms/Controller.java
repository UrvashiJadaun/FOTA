package com.bms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.bms.model.LogStatus;
import com.bms.model.Version;
import com.bms.model.Versionn;
import com.bms.model.t_batch;
import com.bms.model.t_batch_details;
import com.bms.model.t_batch_details_log;
import com.bms.model.t_commands;
import com.bms.mqtt.publisher.Publisher;
import com.bms.repo.Batch_detailsRepo;
import com.bms.repo.Batch_details_logRepo;
import com.bms.service.AssetsServiceAPI;
import com.bms.service.BatchDetailsLogServiceApi;
import com.bms.service.BatchDetailsServiceApi;
import com.bms.service.BatchServiceApi;
import com.bms.service.CommandsServiceAPI;
import com.bms.service.OrganizationServiceAPI;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;



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

	
	
	@GetMapping("/batch_imei_run/{batchid}")
	public ResponseEntity<CustomResponse> batch_IMEI_run(@PathVariable(name = "batchid") Long batchid)
	{
		boolean flag = true;
		try {
		t_batch batch		 =	batchServiceApi.findByBatchid(batchid);
		if(batch==null)
			System.out.println("batch is  null");
		String orgName		 = 	batch.getBatch_org_name();
		List<t_batch_details> imeiList = batchDetailsServiceApi.findAllByBatch_id(batchid);
		String tcuCommand="";
		String bmsCommand="";
		String cfgCommand="";
		batch.setStatus("started");
		batchServiceApi.saveBatchRecords(batch);
		for (t_batch_details t_batch_details : imeiList) {
			
			
				
				if(t_batch_details.getTcuCommand()!=null)
				{
				  tcuCommand			= 	"$TMCFG|START OTA "+t_batch_details.getTcuVersion()+"/#";
					if(publishtopic(t_batch_details.getTcuCommand()))	
					{
						batchDetailsLogServiceApi.update_records(tcuCommand,orgName,batchid,"receive","done",new Timestamp(Calendar.getInstance().getTime().getTime()),t_batch_details.getTcuCommand(),t_batch_details.getIMEI(),"TCU");
						batchDetailsServiceApi.update_records(t_batch_details);
					}
				}
			if(t_batch_details.getBmsCommand()!=null)//BMSOTACFGOTA
			{
			  bmsCommand			= 	"$TMCFG|START BMSOTA "+t_batch_details.getBmsVersion()+"/#";
				if(publishtopic(t_batch_details.getBmsCommand()))	
				{
					batchDetailsLogServiceApi.update_records(bmsCommand,orgName,batchid,"receive","done",new Timestamp(Calendar.getInstance().getTime().getTime()),t_batch_details.getBmsCommand(),t_batch_details.getIMEI(),"BMS");
					batchDetailsServiceApi.update_records(t_batch_details);				
				}
			}
			if(t_batch_details.getCfgCommand()!=null)//BMSOTA
			{
			  cfgCommand			= 	"$TMCFG|START CFGOTA "+t_batch_details.getCfgVersion()+"/#";
				if(publishtopic(t_batch_details.getBmsCommand()))	
				{
					batchDetailsLogServiceApi.update_records(cfgCommand,orgName,batchid,"receive","done",new Timestamp(Calendar.getInstance().getTime().getTime()),t_batch_details.getCfgCommand(),t_batch_details.getIMEI(),"CFG");
					batchDetailsServiceApi.update_records(t_batch_details);
				}
			}
				
			}
		if(flag)
		{
			batch.setStatus("done");
			batch.setExecute("execute");
			batchServiceApi.saveBatchRecords(batch);
		}
			
		}catch (Exception e) {
			// TODO: handle exception
			flag=false;
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new CustomResponse(403, "invalid batchid/something wrong"));
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "success"));
		
	}

	//batch run
	@PostMapping("/bbatch_imei_run/{orgId}")
	public ResponseEntity<CustomResponse> bbatch_IMEI_run(
			@RequestParam("file") MultipartFile file,
			@PathVariable(name = "orgId") Integer orgId,
			@RequestParam("request") String json)
	{
		
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new CustomResponse(417, "file-is-empty"));
		} 
		long imei;
		 String tcu,tcuVersion,bms,bmsVersion,cfg,cfgVersion,tcuCommand,bmsCommand,cfgCommand;
			tcuVersion=bmsVersion=cfgVersion=null;
			tcu=bms=cfg=null;
			tcuCommand=bmsCommand=cfgCommand=null;
			
			/*
			 * Gson gson = new Gson(); Version ver = gson.fromJson(json, Version.class);
			 * tcu=ver.getTcu(); bms=ver.getBms(); cfg=ver.getCfg();
			 */
		      JSONObject jsonObj=new JSONObject(json);
		      tcu=jsonObj.getString("tcu");
		      bms=jsonObj.getString("bms");
		      cfg=jsonObj.getString("cfg");
		      
		      tcuCommand=jsonObj.getString("tcuCommand");
		      bmsCommand=jsonObj.getString("bmsCommand");
		      cfgCommand=jsonObj.getString("cfgCommand");
		      
		      /*
		       "tcuCommand":1,
				"bmsCommand":2,
				"cfgCommand":3
		       */
		     //if(ver.getTcu()!=null) {
		      //if(ver.getTcu()!=null) {
		      if(!tcu.equals("null")) {
		    	// tcuVersion=ver.getTcuVersion();
		    	 tcuVersion=jsonObj.getString("tcuVersion");
		    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
		     }
		     if(!bms.equals("null")) {
		    	// bmsVersion=ver.getBmsVersion();
		    	 bmsVersion=jsonObj.getString("bmsVersion");
		    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
		     }
		     if(!cfg.equals("null")) {
		    	// cfgVersion=ver.getCfgVersion();
		    	 cfgVersion=jsonObj.getString("cfgVersion");
			      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
		     }
		     if(bms.equals("null"))
		    	 cfg="null";
  // String commandName=command_map.get(command);
  
 
		/*
		 * System.out.println("test1 :: "+commandEntity1.getCommandtype());
		 * System.out.println("test2 :: "+commandEntity2.getCommandtype());
		 * System.out.println("test2 :: "+commandEntity3.getCommandtype());
		 */
   String tcu_topic1="";
   String bms_topic2="";
   String cfg_topic3="";
   
   String tcu_command1="";
   String bms_command2="";
   String cfg_command3="";

	String orgName = organizationServiceAPI.findById(orgId);
 
				      
				      
				     try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

							CSVReader csvReader = new CSVReader(reader);
							String[] nextRecord;
							List<Batch_Data> bdataList = new ArrayList<Batch_Data>();
							
							
								t_batch batch = new t_batch();
								batch.setUsr("usr1");
								batch.setBatch_org_name(orgName);
								batch.setCount(bdataList.size());
								batch.setDescription("description_1");
								batch.setStart_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
								batch.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
								batch.setExecute("new");
								batch.setStatus("0");
								batchServiceApi.saveBatchRecords(batch);

								Long batchid = batchServiceApi.getMaxId();
							
							
							
							// we are going to read data line by line
							try {
								while ((nextRecord = csvReader.readNext()) != null) {
									
									imei= Long.parseLong(nextRecord[0]);
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
									
									
									
									  tcu_topic1="";
								      bms_topic2="";
								      cfg_topic3="";
								      
								      tcu_command1="";
								      bms_command2="";
								      cfg_command3="";
								      
								      
								      //1
								      	if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("1")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getReset()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("1")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getReset()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("1")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getReset()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
									     //2
									     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("2")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getShowconfig()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("2")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getShowconfig()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("2")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getShowconfig()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
									     //3
									     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("3")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getShowcredential()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("3")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getShowcredential()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("3")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getShowcredential()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
									     //4
									     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("4")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getShowota()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("4")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getShowota()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("4")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getShowota()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
									     //5
									     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("5")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getShowpubtopics()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("5")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getShowpubtopics()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("5")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getShowpubtopics()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
									     //6
									     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("6")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getShowsubtopics()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("6")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getShowsubtopics()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("6")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getShowsubtopics()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
									     //7
									     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("7")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getSetservername()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("7")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getSetservername()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("7")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getSetservername()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
									     //8
									     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("8")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getPrimaryport()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("8")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getPrimaryport()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("8")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getPrimaryport()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
									     //9
									     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("9")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getFotaservername()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("9")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getFotaservername()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("9")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getFotaservername()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
									     //10
									     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("10")) {
									    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
									    	 tcu_topic1					=	commandEntity1.getSaveconfig()+"TCU/"+imei;
									    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
									    	 tcu_command1				=	tcuString;
									     }
									     if(!bms.equals("null") && bmsCommand.equals("null") && bmsCommand.equals("10")) {
									    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
									    	  bms_topic2				=	commandEntity2.getSaveconfig()+"BMS/"+imei;
									    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
									    	  bms_command2				=	bmsString;
									     }
									     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("10")) {
										      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
										      cfg_topic3				=	commandEntity3.getSaveconfig()+"CFG/"+imei;
										      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
										      cfg_command3				=	cfgString;
									     }
									     
								  
								      //publish commands tcu, bms, cfg
								      boolean t1 = false,t2 = false,t3 = false;
								      String response ="receive",status="done";
								      if(!tcu.equals("null") && !tcuCommand.equals("null")) {
								    	  	//t1= publishtopic(tcu_topic1.replace("#", "")+tcuVersion);
								    	  t1= publishtopic(tcu_topic1);
								    	  	if(t1!=false)
								    	  	batchDetailsLogServiceApi.update_records(tcu_command1,orgName,batchid,response,status,new Timestamp(Calendar.getInstance().getTime().getTime()),tcu_topic1,imei,tcu.toUpperCase());
								      }
								      if(!bms.equals("null") && !bmsCommand.equals("null")) {
								    	  t2= publishtopic(bms_topic2);
								    	  if(t2!=false)
								    	  	batchDetailsLogServiceApi.update_records(bms_command2,orgName,batchid,response,status,new Timestamp(Calendar.getInstance().getTime().getTime()),bms_topic2,imei,bms.toUpperCase());
								      }
								      if(!cfg.equals("null") && !cfgCommand.equals("null")) {
								    	  t3= publishtopic(cfg_topic3);
								    	  if(t3!=false)
								    	  	batchDetailsLogServiceApi.update_records(cfg_topic3,orgName,batchid,response,status,new Timestamp(Calendar.getInstance().getTime().getTime()),cfg_topic3,imei,cfg.toUpperCase());
								      }
								      System.out.println("t1 : "+t1);
								      System.out.println("t2 : "+t2);
								      System.out.println("t3 : "+t3);
							
								     						
								}
								}
								
							} catch (NumberFormatException | CsvValidationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							


							if (!bdataList.isEmpty() && bdataList != null) {
								
								
								for (Batch_Data emei_Batch : bdataList) {
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
							}
							else {
								batchServiceApi.deleteByBatch_id(batchid);
							}
							
							
				     } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				     
				     
		return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "success"));
		
	}


	
	//run single IMEI
	//@PostMapping("/single_imei-run/{orgId}/{imei}/{command}")
	@PostMapping("/single_imei_run/{orgId}/{imei}")
	public ResponseEntity<CustomResponse> single_IMEI_run(@PathVariable(name = "orgId") Integer orgId,
			@PathVariable(name = "imei") long imei,
			@RequestParam("request") String json)
	{
		
		AssetEntity asset = new AssetEntity();
		asset.setOrgId(orgId);
		asset.setImeiNo(imei);
		System.out.println("asset.getOrgId() : " + asset.getOrgId());
		System.out.println("asset.getImeiNo() : " + asset.getImeiNo());

		AssetEntity assetEntity = assetsServiceAPI.exists(asset);
		if (assetEntity != null) {
		
				 String tcu,tcuVersion,bms,bmsVersion,cfg,cfgVersion,tcuCommand,bmsCommand,cfgCommand;
					tcuVersion=bmsVersion=cfgVersion=null;
					tcu=bms=cfg=null;
					tcuCommand=bmsCommand=cfgCommand=null;
					
					/*
					 * Gson gson = new Gson(); Version ver = gson.fromJson(json, Version.class);
					 * tcu=ver.getTcu(); bms=ver.getBms(); cfg=ver.getCfg();
					 */
				      JSONObject jsonObj=new JSONObject(json);
				      tcu=jsonObj.getString("tcu");
				      bms=jsonObj.getString("bms");
				      cfg=jsonObj.getString("cfg");
				      
				      tcuCommand=jsonObj.getString("tcuCommand");
				      bmsCommand=jsonObj.getString("bmsCommand");
				      cfgCommand=jsonObj.getString("cfgCommand");
				      
						/*
						 * String serverName=null; String primaryPort=null; String fotaServerName=null;
						 * serverName = jsonObj.getString("serverName"); primaryPort =
						 * jsonObj.getString("primaryPort"); fotaServerName =
						 * jsonObj.getString("fotaServerName");
						 * System.out.println("serverName 		: "+serverName);
						 * System.out.println("primaryPort 		: "+primaryPort);
						 * System.out.println("fotaServerName 	: "+fotaServerName);
						 */
				      
				      
				      /*
				       "tcuCommand":1,
						"bmsCommand":2,
						"cfgCommand":3
				       */
				      
				      
				     //if(ver.getTcu()!=null) {
				      if(!tcu.equals("null")) {
				    	// tcuVersion=ver.getTcuVersion();
				    	 tcuVersion=jsonObj.getString("tcuVersion");
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				     }
				     if(!bms.equals("null")) {
				    	// bmsVersion=ver.getBmsVersion();
				    	 bmsVersion=jsonObj.getString("bmsVersion");
				    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
				     }
				     if(!cfg.equals("null")) {
				    	// cfgVersion=ver.getCfgVersion();
				    	 cfgVersion=jsonObj.getString("cfgVersion");
					      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
				     }
				     if(bms.equals("null"))
				    	 cfg="null";
		     // String commandName=command_map.get(command);
		     
		    
				/*
				 * System.out.println("test1 :: "+commandEntity1.getCommandtype());
				 * System.out.println("test2 :: "+commandEntity2.getCommandtype());
				 * System.out.println("test2 :: "+commandEntity3.getCommandtype());
				 */
		      String tcu_topic1="";
		      String bms_topic2="";
		      String cfg_topic3="";
		      
		      String tcu_command1="";
		      String bms_command2="";
		      String cfg_command3="";

				String orgName = organizationServiceAPI.findById(orgId);
				
		      
		      //1
		      	if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("1")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getReset()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("1")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getReset()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("1")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getReset()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     //2
			     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("2")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getShowconfig()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("2")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getShowconfig()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("2")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getShowconfig()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     //3
			     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("3")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getShowcredential()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("3")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getShowcredential()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("3")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getShowcredential()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     //4
			     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("4")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getShowota()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("4")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getShowota()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("4")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getShowota()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     //5
			     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("5")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getShowpubtopics()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("5")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getShowpubtopics()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("5")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getShowpubtopics()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     //6
			     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("6")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getShowsubtopics()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("6")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getShowsubtopics()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("6")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getShowsubtopics()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     //7
			     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("7")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getSetservername()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("7")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getSetservername()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("7")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getSetservername()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     //8
			     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("8")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getPrimaryport()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("8")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getPrimaryport()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("8")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getPrimaryport()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     //9
			     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("9")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getFotaservername()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("9")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getFotaservername()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("9")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getFotaservername()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     //10
			     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("10")) {
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			    	 tcu_topic1					=	commandEntity1.getSaveconfig()+"TCU/"+imei;
			    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
			    	 tcu_command1				=	tcuString;
			     }
			     if(!bms.equals("null") && bmsCommand.equals("null") && bmsCommand.equals("10")) {
			    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
			    	  bms_topic2				=	commandEntity2.getSaveconfig()+"BMS/"+imei;
			    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
			    	  bms_command2				=	bmsString;
			     }
			     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("10")) {
				      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
				      cfg_topic3				=	commandEntity3.getSaveconfig()+"CFG/"+imei;
				      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
				      cfg_command3				=	cfgString;
			     }
			     
			     
		      
		      
		      
				
		      
		      //publish commands tcu, bms, cfg
		      boolean t1 = false,t2 = false,t3 = false;
		      String response ="receive",status="done";
		      if(!tcu.equals("null") && !tcuCommand.equals("null")) {
		    	  	//t1= publishtopic(tcu_topic1.replace("#", "")+tcuVersion);
		    	  t1= publishtopic(tcu_topic1);
		    	  	if(t1!=false)
		    	  	batchDetailsLogServiceApi.update_records(tcu_command1,orgName,0l,response,status,new Timestamp(Calendar.getInstance().getTime().getTime()),tcu_topic1,imei,tcu.toUpperCase());
		      }
		      if(!bms.equals("null") && !bmsCommand.equals("null")) {
		    	  t2= publishtopic(bms_topic2);
		    	  if(t2!=false)
		    	  	batchDetailsLogServiceApi.update_records(bms_command2,orgName,0l,response,status,new Timestamp(Calendar.getInstance().getTime().getTime()),bms_topic2,imei,bms.toUpperCase());
		      }
		      if(!cfg.equals("null") && !cfgCommand.equals("null")) {
		    	  t3= publishtopic(cfg_topic3);
		    	  if(t3!=false)
		    	  	batchDetailsLogServiceApi.update_records(cfg_topic3,orgName,0l,response,status,new Timestamp(Calendar.getInstance().getTime().getTime()),cfg_topic3,imei,cfg.toUpperCase());
		      }
		      System.out.println("t1 : "+t1);
		      System.out.println("t2 : "+t2);
		      System.out.println("t3 : "+t3);
		    
		return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "success"));
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new CustomResponse(403, "IMEI does not exist in t_asset"));
	}

	 private boolean publishtopic(String topic) {
		 System.out.println("topic published : "+topic);
		 return true;
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
	//updated upload csv file
  @PostMapping("/upload-csv-file/{orgId}")
	public ResponseEntity<CustomResponse> uploadCSVFile(@RequestParam("file") MultipartFile file, Model model,
			@PathVariable(name = "orgId") Integer orgId,@RequestParam("request") String json) {
   boolean flag=false;
		// validate file
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new CustomResponse(417, "file-is-empty"));

			/*
			 * model.addAttribute("message", "Please select a CSV file to upload.");
			 * model.addAttribute("status", false);
			 */
		} else {

			String tcu,tcuVersion,bms,bmsVersion,cfg,cfgVersion,tcuCommand,bmsCommand,cfgCommand;
			tcuVersion=bmsVersion=cfgVersion=null;
			tcu=bms=cfg=null;
			
			/*
			 * Gson gson = new Gson(); Versionn ver = gson.fromJson(json, Versionn.class);
			 * tcu=ver.getTcu(); bms=ver.getBms(); cfg=ver.getCfg();
			 */
		      
		      JSONObject jsonObj=new JSONObject(json);
		      tcu=jsonObj.getString("tcu");
		      bms=jsonObj.getString("bms");
		      cfg=jsonObj.getString("cfg");
		      
		      tcuCommand=jsonObj.getString("tcuCommand");
		      bmsCommand=jsonObj.getString("bmsCommand");
		      cfgCommand=jsonObj.getString("cfgCommand");
		      
		      tcuVersion=null;
		      bmsVersion=null;
		      cfgVersion=null;
		      System.out.println("TCU :: "+tcu);
		      System.out.println("BMS :: "+bms);
		      System.out.println("CFG :: "+cfg);
		      
		      if(!tcu.equals("null")) {
			    	// tcuVersion=ver.getTcuVersion();
			    	 tcuVersion=jsonObj.getString("tcuVersion");
			    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
			     }
			     if(!bms.equals("null")) {
			    	// bmsVersion=ver.getBmsVersion();
			    	 bmsVersion=jsonObj.getString("bmsVersion");
			    	  t_commands commandEntity2=commandsServiceAPI.findByBms(bms.toLowerCase());
			     }
			     if(!cfg.equals("null")) {
			    	// cfgVersion=ver.getCfgVersion();
			    	 cfgVersion=jsonObj.getString("cfgVersion");
				      t_commands commandEntity3=commandsServiceAPI.findByCfg(cfg.toLowerCase());
			     }
			     if(bms.equals("null"))
			    	 cfg="null";
		      
			     String tcu_topic1 = "";
			      String bms_topic2 = "";
			      String cfg_topic3 = "";
			      
			      String tcu_command1 = "";
			      String bms_command2 = "";
			      String cfg_command3 = "";
			      
			      
			     
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

					t_batch_details batch_details=null;
					
				//	boolean batch_details1=batchDetailsServiceApi.exists(batch_details);
				//	batch_details=BatchDetailsServiceApi.findByImei(Long.parseLong(nextRecord[0]));
					t_batch_details t=batchDetailsServiceApi.findByImei(Long.parseLong(nextRecord[0]));
					System.out.println("9999999999999999999999999 :: "+t);
						
					
					AssetEntity assetEntity = assetsServiceAPI.exists(asset);
					if (assetEntity != null) {
						System.out.println("asset Entity not null");
						if(batchDetailsServiceApi.findByImei(Long.parseLong(nextRecord[0]))==null || "fail".equalsIgnoreCase(batchDetailsServiceApi.findByImei(Long.parseLong(nextRecord[0])).getStatus())) {
							flag=true;
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
				batch.setStatus("not start");
				batchServiceApi.saveBatchRecords(batch);

				Long batchid = batchServiceApi.getMaxId();
				System.out.println("batchServiceApi.getMaxId()  :: " + batchid);

				for (Batch_Data emei_Batch : bdataList) {
					  tcu_topic1 = null;
				       bms_topic2 = null;
				       cfg_topic3 = null;
				      
				       tcu_command1 = null;
				       bms_command2 = null;
				       cfg_command3 = null;
					
					 //1
			      	if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("1")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getReset()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("1")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getReset()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("1")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getReset()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				     //2
				     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("2")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getShowconfig()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("2")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getShowconfig()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("2")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getShowconfig()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				     //3
				     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("3")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getShowcredential()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("3")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getShowcredential()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("3")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getShowcredential()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				     //4
				     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("4")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getShowota()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("4")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getShowota()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("4")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getShowota()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				     //5
				     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("5")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getShowpubtopics()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("5")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getShowpubtopics()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("5")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getShowpubtopics()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				     //6
				     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("6")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getShowsubtopics()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("6")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getShowsubtopics()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("6")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getShowsubtopics()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				     //7
				     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("7")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getSetservername()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("7")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getSetservername()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("7")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getSetservername()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				     //8
				     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("8")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getPrimaryport()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("8")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getPrimaryport()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("8")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getPrimaryport()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				     //9
				     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("9")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getFotaservername()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && !bmsCommand.equals("null") && bmsCommand.equals("9")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getFotaservername()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("9")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getFotaservername()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				     //10
				     if(!tcu.equals("null") && !tcuCommand.equals("null") && tcuCommand.equals("10")) {
				    	 t_commands commandEntity1=commandsServiceAPI.findByTcu(tcu.toLowerCase()); 
				    	 tcu_topic1					=	commandEntity1.getSaveconfig()+"TCU/"+emei_Batch.getImei();
				    	 String tcuString			= 	"$TMCFG|START OTA "+tcuVersion+"/#";
				    	 tcu_command1				=	tcuString;
				     }
				     if(!bms.equals("null") && bmsCommand.equals("null") && bmsCommand.equals("10")) {
				    	  t_commands commandEntity2	=	commandsServiceAPI.findByBms(bms.toLowerCase());
				    	  bms_topic2				=	commandEntity2.getSaveconfig()+"BMS/"+emei_Batch.getImei();
				    	  String bmsString			=	"$TMCFG|START BMSOTA "+bmsVersion+"/#";
				    	  bms_command2				=	bmsString;
				     }
				     if(!cfg.equals("null") && !cfgCommand.equals("null") && cfgCommand.equals("10")) {
					      t_commands commandEntity3	=	commandsServiceAPI.findByCfg(cfg.toLowerCase());
					      cfg_topic3				=	commandEntity3.getSaveconfig()+"CFG/"+emei_Batch.getImei();
					      String cfgString			=	"$TMCFG|START CFGOTA "+cfgVersion+"/#";
					      cfg_command3				=	cfgString;
				     }
				     
				 
					if(!"null".equals(tcu))
					{
						System.out.println("TCU------------------------------------------------------------------------------------------");
						t_batch_details_log log=new t_batch_details_log();
						//String tcuCommand="$TMCFG|START OTA "+tcuVersion+"/#";
						log.setIMEI(Long.parseLong(emei_Batch.getImei()));
						log.setOrgName(orgName);
						log.setResponse("send");
						log.setStatus("running");
						log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
						log.setBatch_id(batchid);
						log.setCommand(tcu_command1);
						log.setType("TCU");
						log.setTopic(tcu_topic1);
						
						batchDetailsLogServiceApi.save_tcu_command(log);
					}
					if(!"null".equals(bms))
					{
						System.out.println("BMS---------------------------------------------------------------------------------------");
						t_batch_details_log log=new t_batch_details_log();
					//	String bmsCommand="$TMCFG|START BMSOTA "+bmsVersion+"/#";
						log.setIMEI(Long.parseLong(emei_Batch.getImei()));
						log.setOrgName(orgName);
						log.setResponse("send");
						log.setStatus("running");
						log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
						log.setBatch_id(batchid);
						log.setCommand(bms_command2);
						log.setType("BMS");
						log.setTopic(bms_topic2);
						batchDetailsLogServiceApi.save_bms_command(log);
					}
					
					if(!"null".equals(bms) && !"null".equals(cfg))
					{
						System.out.println("CFG------------------------------------------------------------------------------------------");
						t_batch_details_log log=new t_batch_details_log();
					//	String cfgCommand="$TMCFG|START CFGOTA "+cfgVersion+"/#";
						log.setIMEI(Long.parseLong(emei_Batch.getImei()));
						log.setOrgName(orgName);
						log.setResponse("send");
						log.setStatus("running");
						log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
						log.setBatch_id(batchid);
						log.setCommand(cfg_command3);
						log.setType("CFG");
						log.setTopic(cfg_topic3);
						
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
						batch_details.setStatus(null);
						batch_details.setTCL_version(emei_Batch.getTcu());
						batch_details.setBatch_id(batchid);
						batch_details.setTcuCommand(tcu_command1);
						batch_details.setBmsCommand(bms_command2);
						batch_details.setCfgCommand(cfg_command3);
						batch_details.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));
						batch_details.setTcuVersion(tcuVersion);
						batch_details.setBmsVersion(bmsVersion);
						batch_details.setCfgVersion(cfgVersion);
						

						batchDetailsServiceApi.saveBatchDetailsRecords(batch_details);
					}

				}
			} else
				System.out.println("0000000000000000000000000000 list is empty");

		}
		if(flag)
		return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "file-upload-status"));
		else
		return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(new CustomResponse(203, "fail-No-new-record-found"));

	}
  
  
  //updated file upload csv
  @PostMapping("/uupload-csv-file/{orgId}")
 	public ResponseEntity<CustomResponse> uuploadCSVFile(@RequestParam("file") MultipartFile file, Model model,
 			@PathVariable(name = "orgId") Integer orgId,@RequestParam("request") String json) {
    boolean flag=false;
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

 					t_batch_details batch_details=null;
 					
 				//	boolean batch_details1=batchDetailsServiceApi.exists(batch_details);
 				//	batch_details=BatchDetailsServiceApi.findByImei(Long.parseLong(nextRecord[0]));
 					t_batch_details t=batchDetailsServiceApi.findByImei(Long.parseLong(nextRecord[0]));
 					System.out.println("9999999999999999999999999 :: "+t);
 						
 					
 					AssetEntity assetEntity = assetsServiceAPI.exists(asset);
 					if (assetEntity != null) {
 						System.out.println("asset Entity not null");
 						if(batchDetailsServiceApi.findByImei(Long.parseLong(nextRecord[0]))==null) {
 							flag=true;
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
 				batch.setStatus("not start");
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
 						batch_details.setStatus(null);
 						batch_details.setTCL_version(emei_Batch.getTcu());
 						batch_details.setBatch_id(batchid);
 						batch_details.setEnd_date(new Timestamp(Calendar.getInstance().getTime().getTime()));

 						batchDetailsServiceApi.saveBatchDetailsRecords(batch_details);
 					}

 				}
 			} else
 				System.out.println("0000000000000000000000000000 list is empty");

 		}
 		if(flag)
 		return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "file-upload-status"));
 		else
 		return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(new CustomResponse(200, "fail-No-new-record-found"));

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

	//order by date by imei
	@GetMapping("/get_t_batch_details_log_By_IMEI_orderByDate/{IMEI}")
	public List<t_batch_details_log> get_t_batch_details_logByIMEI(@PathVariable(name = "IMEI") Long IMEI) {
		return batchDetailsLogServiceApi.get_t_batch_details_logByIMEI(IMEI);

	}
	
	//order by date by batch_id
		@GetMapping("/get_t_batch_details_log_By_Batch_id_orderByDate/{batch_id}")
		public List<t_batch_details_log> get_t_batch_details_logBy_Batch_id(@PathVariable(name = "batch_id") Long batch_id) {
			return batchDetailsLogServiceApi.get_t_batch_details_logBy_Batch_id(batch_id);

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
	
	
	
	@GetMapping("/status/{imei}")
	public LogStatus getStatus(@PathVariable(name = "imei") Long imei)
	{
		t_batch_details_log tcuData=null;
		t_batch_details_log bmsData=null;
		t_batch_details_log cfgData=null;
		 tcuData= batchDetailsLogServiceApi.getlatestStatus("TCU", imei);
		 bmsData= batchDetailsLogServiceApi.getlatestStatus("BMS", imei);
		 cfgData= batchDetailsLogServiceApi.getlatestStatus("CFG", imei);
		
		LogStatus status		= new LogStatus();
		
		if(tcuData!=null)
		{
			status.setTcuStatus(tcuData.getStatus());
		}
		if(bmsData!=null)
		{
			status.setBmStatus(bmsData.getStatus());
		}
		if(cfgData!=null)
		{
			status.setCfgStatus(cfgData.getStatus());
		}
		 return status;
		
	}
	
	
	//delete_batch_
	 @Transactional(rollbackOn = Exception.class)
	  @DeleteMapping("/delete_batch_ByBatchId/{batch_id}")
	  public ResponseEntity<CustomResponse> delete_batch_ByBatchId(
	  
	  @PathVariable(name = "batch_id") Long batch_id) {
		 
		int x=0,y = 0,z=0;
		
		t_batch batch	=	batchServiceApi.findByBatchid(batch_id);
			if("not start".equalsIgnoreCase(batch.getStatus()))
			{
				 x = batchDetailsLogServiceApi.deleteByBatch_id(batch_id);
				  System.out.println("b_x==== : "+x); 
				  y = batchDetailsServiceApi.deleteByBatch_id(batch_id);
				  System.out.println("b_y==== : "+y);
				 
				  			z	 =   batchServiceApi.deleteByBatch_id(batch_id);
				  			System.out.println("b_z==== : "+z);
					 
				  	if(z>0)	 
				  		return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200,"success"));
				  	else return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(203,"record not exist"));
			  
			}
		 return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(204,"can't delete batch"));
	  }
		
	 //delete_single_imei
		  @Transactional(rollbackOn = Exception.class)		  
		  @DeleteMapping("/delete_single_imei/{imei}") 
		  public  ResponseEntity<CustomResponse> delete_single_imei(
		  
		  @PathVariable(name = "imei") Long imei) {
		  
			  t_batch_details batch_details1 = batchDetailsServiceApi.findByImei(imei);
			  if(batch_details1!=null && (batch_details1.getStatus()==null || "fail".equalsIgnoreCase(batch_details1.getStatus())))
			  {
				  int x,y;
				  x = 	batchDetailsLogServiceApi.deleteByIMEI(imei);
			  		System.out.println("x===== "+x);
			  			t_batch_details batch_details=batchDetailsServiceApi.findByImei(imei);
					 if(batch_details==null)
					  { 
						 System.out.println("batch_details NULL");
						 return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(203,"record not exit in batchDetails"));
					  }
					  else {
						  y	=	batchDetailsServiceApi.deleteByIMEI(imei);
						  System.out.println("y===== "+y);
						  batchServiceApi.updateIMEIentry(batch_details.getBatch_id());
					  }
				  return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200,"success"));
			  }
			  boolean b=batchDetailsLogServiceApi.existByIMEI(imei);
			  if(b)
			  {
				  batchDetailsLogServiceApi.deleteByIMEI(imei);
				  return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200,"success"));
			  }
			  return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(204,"can't delete imei"));
			
		  }
		 
	 
	 
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
	
	
	@PostMapping("/jsonjson")
	public String getJson1(@RequestParam("request") String json)
	{
		JSONObject jsonObj=new JSONObject(json);
		System.out.println("json :: "+jsonObj.get("tcuCommand"));
		return jsonObj.get("tcuCommand")+"";
		
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
	@GetMapping("/singleIMEI_test")
	public t_batch_details getSigleIMEI()
	{
		t_batch_details t=batchDetailsServiceApi.findByImei(357897106634148l);
		
		return t;
		
	}
  
}
