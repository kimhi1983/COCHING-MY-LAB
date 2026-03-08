package com.erns.coching.schedule.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.mltln.service.MltlnService;
import com.erns.coching.schedule.service.BatchService;

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

	@Value("${batch.generate-locale-json:false}")
	private boolean isGenerateLocaleJson;

	@Autowired
	private MltlnService mltlnService;


	@Override
//	@Scheduled(cron = "0 1 2 * * ?") /* 02:01:00 */
//	@Scheduled(cron = "0 * * * * ?") /* 매 0초 - 테스트용*/
	@Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE) //부팅후 10초 후
	public void batchGenerateLocaleJson() {
		if (!isGenerateLocaleJson) {
			return;
		}

		log.info("Start... batchGenerateLocaleJson");


		mltlnService.exportMltlnJson();


		log.info("Done... batchGenerateLocaleJson");

	}

}
