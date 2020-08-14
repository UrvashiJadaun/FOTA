package com.bms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bms.model.t_batch_details_log;

@Repository
public interface Batch_details_logRepo extends JpaRepository<t_batch_details_log, Long> {

}
