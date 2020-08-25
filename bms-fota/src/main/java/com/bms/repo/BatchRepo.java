package com.bms.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bms.model.t_batch;

@Repository
public interface BatchRepo extends JpaRepository<t_batch,Long> {
	
	@Query("SELECT coalesce(max(ch.batch_id), 0) FROM t_batch ch")
	Long getMaxId();

	List<t_batch> findByBatchOrgName(String orgName);

	//int deleteByBatch_id(Long batch_id);
}
