package com.erns.coching.common.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nhncorp.lucy.security.xss.XssFilter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class XssFilterService {

	private static final String WEB_EDITER_RULE_FILE = "/xss/lucy-xss-htmlEditor.xml";

	@PostConstruct
	public void startup()
	{
		log.info("XssFilterService is started");
	}


	/**
	 * list<map> 형태의 특정 컬럼의 xss를 제거
	 * @param list
	 * @param columns
	 */
	@SuppressWarnings("rawtypes")
	public void cleanXssForWebEditor(List<?> list, String[] columns) {
		if(null == list || list.size() <= 0) {
			return;
		}

		for(Object row : list) {

			cleanXssForWebEditor((Map)row, columns);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void cleanXssForWebEditor(Map map, String[] columns) {
		if(null == map) {
			return;
		}

		XssFilter xssFilter = XssFilter.getInstance(WEB_EDITER_RULE_FILE);

		Map rowMap = (Map)map;
		for(String key : columns) {
			Object value = rowMap.get(key);
			//String setKey = isCosmaxDataMapper ? decamelize(key) : key;
			String setKey = decamelize(key);

			//log.debug("key:{}|setKey:{}", key, setKey);

			if(value == null || !(value instanceof String)) {
				continue;
			}

			String strVal = (String)value;
			if(StringUtils.hasText(strVal)) {
				String unxssValue = xssFilter.doFilter(strVal);
				//log.debug("orgValue:{}|unxssValue:{}", strVal, unxssValue);
				rowMap.put(setKey, unxssValue);
			}
		}
	}

	public static String decamelize(String value) {
		return decamelize(value, "_");
	}

	public static String decamelize(String value, String splitPattern) {
        if (value == null) {
            return null;
        }

        StringBuilder deCamelized = new StringBuilder(value.length() + 10);
        for (int i = 0; i < value.length(); i++) {
            if (Character.isUpperCase(value.charAt(i)) && deCamelized.length() > 0) {
                deCamelized.append(splitPattern);
            }
            deCamelized.append(Character.toUpperCase(value.charAt(i)));
        }
        return deCamelized.toString();
    }
}
