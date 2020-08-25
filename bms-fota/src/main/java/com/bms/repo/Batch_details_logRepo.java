package com.bms.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bms.model.t_batch_details;
import com.bms.model.t_batch_details_log;
import com.bms.model.t_commands;

@Repository
public interface Batch_details_logRepo extends JpaRepository<t_batch_details_log, Long> {

	//List<t_batch_details_log> findByIMEIOrderByTimeAsc();
	List<t_batch_details_log> findByIMEI(long IMEI,Sort sort);
	List<t_batch_details_log> findByBatchid(long batch_id, Sort sort); 
	
	//select * from t_batch_details_log where type=? and imei=? order by time desc limit 1
	@Query(value = "select * from t_batch_details_log t where t.type=:type and t.imei=:imei order by time desc limit 1",nativeQuery = true)
	t_batch_details_log getlatestStatus(@Param("type") String type,@Param("imei") long l);
	
	//@Modifying(clearAutomatically = true)
	 @Query(value = "UPDATE t_batch_details_log SET  response= ?, status=? , time=? WHERE imei=? and type=?",nativeQuery = true) 
	 void update_records(@Param("response") String response, @Param("status") String status, Timestamp timestamp, @Param("imei") long imei, @Param("componentType") String componentType);


	 @Query(value="select * from t_batch_details_log where imei=? and type=?",nativeQuery = true)
	t_batch_details_log findByImeiANDComponentType(long imei, String type);


	 @Query("FROM t_batch_details_log t WHERE t.batchid = :batchid")
	 	List<t_batch_details_log> findByBatch_id(@Param("batchid") Long batchid);


	int deleteByIMEI(Long imei);
	boolean existsByIMEI(Long imei);
	
	
	 @Query(value="select * from t_batch_details_log t where t.imei= :imei and t.batchid= :batchid order by time desc",nativeQuery = true)
	List<t_batch_details_log> getAllByIMEIandBatchid(@Param("imei") long imei, @Param("batchid") Long batchid);


	
	  
	  //@Query(value = "UPDATE t_batch_details_log SET  response= ?, status=? , time=? WHERE imei=? and type=?",nativeQuery = true) 
	 // void update_records(String response, String status, Timestamp timestamp, long imei, String componentType);
	  
		/*
		 * @Query("update Customer c set c.name = :name WHERE c.id = :customerId") void
		 * setCustomerName(@Param("customerId") Long id, @Param("name") String name);
		 */
	  
	  
	  //@Query(value = "UPDATE t_batch_details_log SET  response='receive', status='done' WHERE imei=?1 and type=?2",nativeQuery = true) 
	  
	  //void update_records(long imei, String componentType);
	//	
	
//	  @Query(value="UPDATE public.t_batch_details_log\r\n" +
//	  "					SET  response='receive', status='done', \"time\"=now()\r\n"
//	  + "					WHERE imei=? and type=?",nativeQuery = true) void
//	  update_records(long imei, String componentType);
//	 
	/*
	 * @Modifying(clearAutomatically = true)
	 * 
	 * @Query("update t_batch_details_log x set x.response =:'xyz'  and x.status='zzzz' where x.imei=? and x.type=?"
	 * ) void update_records(long imei, String componentType);
	 */
}
