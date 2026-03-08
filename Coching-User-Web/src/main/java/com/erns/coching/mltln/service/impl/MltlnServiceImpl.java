package com.erns.coching.mltln.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.erns.coching.mltln.domain.MltlnSearchDTO;
import com.erns.coching.mltln.domain.MltlnVO;
import com.erns.coching.mltln.mapper.MltlnDAO;
import com.erns.coching.mltln.service.MltlnService;

import lombok.extern.slf4j.Slf4j;

/**
*
* <p>다국어 마스터 Service</p>
*
* @author Kyungmin Lee
*
*/
@Slf4j
@Service
@Transactional
public class MltlnServiceImpl implements MltlnService {

	@Autowired
	private MltlnDAO dao;

	@Value("${system.file.uploadpath}")
	private String uploadPath;

	@Override
	public List<Map<String, Object>> getList(MltlnSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public List<MltlnVO> getListVo(MltlnSearchDTO param) {
		return dao.getListVo(param);
	}

	@Override
	public int getListCount(MltlnSearchDTO param) {
		return dao.getListCount(param);
	}
	
	@Override
	public Map<String, Object> load(MltlnSearchDTO param) {
		return dao.load(param);
	}

	@Override
	public int isHaveCode(MltlnSearchDTO param) {
		return dao.isHaveCode(param);
	}

	@Override
	public boolean exportMltlnJson() {
		log.info("Start Export mltln Json");
		List<MltlnVO> allList;
		{
			MltlnSearchDTO allScanParam = MltlnSearchDTO.builder().build();
			allScanParam.setRowSize(0); //All
			allList = getListVo(allScanParam);
		}

		String[] locales = new String[] {
			"ko", "en"
		};

		Map<String, Map<String, Object>> i18RootMap = new HashMap<>();
		for(String locale : locales) {
			i18RootMap.put(locale, new HashMap<String, Object>());
		}

		for(MltlnVO ivo : allList) {
			putValues(i18RootMap.get(locales[0]), ivo.getCode(), ivo.getCodeNmKo());
			putValues(i18RootMap.get(locales[1]), ivo.getCode(), ivo.getCodeNmEn());
		}

		//Make Json
		ObjectMapper om = new ObjectMapper();

		Path rootPath = Paths.get(uploadPath, "i18n");
		{	//경로 만들기
			File temp = rootPath.toFile();
			if(! temp.exists()){
				temp.mkdirs();
			}
		}

		for(String locale : locales) {
			Map<String, Object> root = i18RootMap.get(locale);
			Path localePath = rootPath.resolve(String.format("%s.json", locale));

			try {
				log.info("Export mltln Json : {}", localePath);
				om.writeValue(localePath.toFile(), root);
			} catch (IOException e) {
				//e.printStackTrace();
				log.error("cannot create {}", localePath);
				log.error("cannot create mltln-"+locale, e);
			}
		}

		log.info("Done Export mltln Json");
		return true;
	}

	@SuppressWarnings("unchecked")
	private void putValues(Map<String, Object> tMap, String code, String value) {
		String[] tokens = code.split("\\.");
		List<String> vector = new ArrayList<>(Arrays.asList(tokens));

		Map<String, Object> curMap = tMap;
		while(!vector.isEmpty()) {
			String cToken = vector.remove(0);

			if(vector.isEmpty()) {
				curMap.put(cToken, value);
				return;
			}

			if(curMap.containsKey(cToken)) {
				Object obj = curMap.get(cToken);
				if(obj instanceof Map) {
					curMap = (Map<String, Object>)curMap.get(cToken);
				}else {
					log.warn("{}/{} is bad position!", code, value);
				}
				continue;
			}

			Map<String, Object> childMap = new HashMap<String, Object>();
			curMap.put(cToken, childMap);
			curMap = childMap;
		}
	}

}
