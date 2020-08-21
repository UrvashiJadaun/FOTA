package com.bms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandeller {
	
	@ExceptionHandler(value = MqttException.class)
	public ResponseEntity<Object> exception(MqttException exception)
	{
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}
