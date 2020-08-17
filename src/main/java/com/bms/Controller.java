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

import org.json.JSONArray;
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
import org.springframework.web.bind.annotation.RequestBody;
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
import com.bms.model.Version;
import com.bms.model.t_batch;
import com.bms.model.t_batch_details;
import com.bms.model.t_batch_details_log;
import com.bms.repo.AssetRepo;
import com.bms.service.AssetsServiceAPI;
import com.bms.service.BatchDetailsLogServiceApi;
import com.bms.service.BatchDetailsServiceApi;
import com.bms.service.BatchServiceApi;
import com.bms.service.OrganizationServiceAPI;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.sun.javafx.collections.MappingChange.Map;

@RestController
@CrossOrigin(origins = "*")
public class Controller {

	public static final int _bms = 1;
	public static final int _tcu = 2;
	public static final int _config = 3;
	public static final int _sysconfg = 4;

	
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

//	  @RequestMapping(value = "findOrgByToken")
//		public Optional<OrganisationEntity> getOrgDetailByToken(@RequestHeader("Authorization") String accessToken) {
//
//			return organisationservice.findOrgDetailsByToken(accessToken);
//		}
//	  
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
						log.setResponse("Incoming");
						log.setStatus("new");
						log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
						log.setBatch_id(orgId);
						log.setCommand(tcuCommand);
						
						batchDetailsLogServiceApi.save_tcu_command(log);
					}
					if(bms!=null)
					{
						t_batch_details_log log=new t_batch_details_log();
						String bmsCommand="$TMCFG|START BMSOTA "+bmsVersion+"/#";
						log.setIMEI(Long.parseLong(emei_Batch.getImei()));
						log.setOrgName(orgName);
						log.setResponse("Incoming");
						log.setStatus("new");
						log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
						log.setBatch_id(orgId);
						log.setCommand(bmsCommand);
						
						batchDetailsLogServiceApi.save_bms_command(log);
					}
					if(bms!=null && cfg!=null)
					{
						t_batch_details_log log=new t_batch_details_log();
						String cfgCommand="$TMCFG|START CFGOTA "+cfgVersion+"/#";
						log.setIMEI(Long.parseLong(emei_Batch.getImei()));
						log.setOrgName(orgName);
						log.setResponse("Incoming");
						log.setStatus("new");
						log.setTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
						log.setBatch_id(orgId);
						log.setCommand(cfgCommand);
						
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
}
