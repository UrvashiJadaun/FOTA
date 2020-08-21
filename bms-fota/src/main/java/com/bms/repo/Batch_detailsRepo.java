package com.bms.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bms.model.t_batch_details;

@Repository
public interface Batch_detailsRepo extends JpaRepository<t_batch_details, Long>{

	//List<t_batch_details> findByBatch_id(Long batch_id);
	
	 @Query("FROM t_batch_details t WHERE t.batch_id = :batch_id")
	 	List<t_batch_details> findByBatch_id(@Param("batch_id") Long batch_id);  
	 	
	 	public Boolean existsByIMEI(long imei);
	 	//public int deleteT_batch_detailsByBatch_id(Long batch_id);

		//t_batch_details findByImei(long l);

	 	 @Query("FROM t_batch_details t WHERE t.IMEI = :IMEI")
		t_batch_details findByIMEI(@Param("IMEI") long IMEI);

		int deleteByIMEI(Long imei);

	 	  
	 	
		
		/*
		 * @Query(value="DELETE FROM t_batch_details t WHERE t.batch_id=:batch_id"
		 * ,nativeQuery =true) void deleteRecords(@Param("batch_id") Long batch_id);
		 */
		 

		//int deleteByBatch_id(Long batch_id);


}
