package com.bms.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bms.model.t_batch;

@Component
public interface BatchServiceApi {

	public void saveBatchRecords(t_batch batch);

	public  Long getMaxId();

	public List<t_batch> findAll();
}
