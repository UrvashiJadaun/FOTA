package com.bms.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bms.model.t_FirmwareGenerator;

@Repository
public interface FirmwareRepo extends JpaRepository<t_FirmwareGenerator, Long>{

}
