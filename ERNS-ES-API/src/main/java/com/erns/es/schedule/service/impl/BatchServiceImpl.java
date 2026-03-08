package com.erns.es.schedule.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.es.schedule.service.BatchService;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>배치 Service</p>
*
* @author Hunwoo Park
*
*/
@Slf4j
@Service
@Transactional
public class BatchServiceImpl implements BatchService {

	private int maxBatchCount = 100;
	
}
