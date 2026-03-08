package com.erns.coching.common.type;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

/**
 *
 * <p>스크래핑 은행(일반)</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ScrapBankCd {

  SBC_002 ("002",   "bank",     1010,   "Y",    "산업"),
  SBC_003 ("003",   "bank",     1020,   "Y",    "기업"),
  SBC_004 ("004",   "bank",     1030,   "Y",    "국민"),
  SBC_007 ("007",   "bank",     1040,   "Y",    "수협"),
  SBC_011 ("011",   "bank",     1050,   "Y",    "농협"),
  SBC_023 ("023",   "bank",     1060,   "Y",    "SC"),
  SBC_020 ("020",   "bank",     1070,   "Y",    "우리"),
  SBC_027 ("027",   "bank",     1080,   "Y",    "한국씨티"),
  SBC_031 ("031",   "bank",     1090,   "Y",    "대구"),
  SBC_032 ("032",   "bank",     1100,   "Y",    "부산"),
  SBC_035 ("035",   "bank",     1110,   "Y",    "제주"),
  SBC_034 ("034",   "bank",     1120,   "Y",    "광주"),
  SBC_037 ("037",   "bank",     1130,   "Y",    "전북"),
  SBC_039 ("039",   "bank",     1140,   "Y",    "경남"),
  SBC_045 ("045",   "bank",     1150,   "Y",    "새마을금고"),
  SBC_048 ("048",   "bank",     1160,   "Y",    "신협"),
  SBC_050 ("050",   "bank",     1170,   "Y",    "상호저축"),
  SBC_054 ("054",   "bank",     1180,   "Y",    "홍콩(HSBC은행)"),
  SBC_055 ("055",   "bank",     1190,   "Y",    "도이치"),
  SBC_057 ("057",   "bank",     1200,   "Y",    "JP모간"),
  SBC_060 ("060",   "bank",     1210,   "Y",    "뱅크오브아메리카"),
  SBC_061 ("061",   "bank",     1220,   "Y",    "BNP파리바"),
  SBC_062 ("062",   "bank",     1230,   "Y",    "중국공상"),
  SBC_064 ("064",   "bank",     1240,   "Y",    "산림조합"),
  SBC_067 ("067",   "bank",     1250,   "Y",    "중국건설은행"),
  SBC_071 ("071",   "bank",     1260,   "Y",    "우체국"),
  SBC_081 ("081",   "bank",     1270,   "Y",    "KEB하나"),
  SBC_088 ("088",   "bank",     1280,   "Y",    "신한"),
  SBC_089 ("089",   "bank",     1290,   "Y",    "K뱅크"),
  SBC_090 ("090",   "bank",     1300,   "Y",    "카카오뱅크"),
  SBC_209 ("209",   "bank",     1310,   "Y",    "유안타증권"),
  SBC_218 ("218",   "bank",     1320,   "Y",    "KB증권"),
  SBC_227 ("227",   "bank",     1330,   "Y",    "KTB투자증권"),
  SBC_238 ("238",   "bank",     1340,   "Y",    "미래에셋대우"),
  SBC_240 ("240",   "bank",     1350,   "Y",    "삼성증권"),
  SBC_243 ("243",   "bank",     1360,   "Y",    "한국투자증권"),
  SBC_247 ("247",   "bank",     1370,   "Y",    "NH투자증권"),
  SBC_261 ("261",   "bank",     1380,   "Y",    "교보증권"),
  SBC_262 ("262",   "bank",     1390,   "Y",    "하이투자증권"),
  SBC_263 ("263",   "bank",     1400,   "Y",    "현대차증권"),
  SBC_264 ("264",   "bank",     1410,   "Y",    "키움증권"),
  SBC_265 ("265",   "bank",     1420,   "Y",    "이베스트투자증권"),
  SBC_266 ("266",   "bank",     1430,   "Y",    "에스케이증권"),
  SBC_267 ("267",   "bank",     1440,   "Y",    "대신증권"),
  SBC_269 ("269",   "bank",     1450,   "Y",    "한화투자증권"),
  SBC_270 ("270",   "bank",     1460,   "Y",    "하나금융투자"),
  SBC_278 ("278",   "bank",     1470,   "Y",    "신한금융투자"),
  SBC_279 ("279",   "bank",     1480,   "Y",    "DB금융투자"),
  SBC_280 ("280",   "bank",     1490,   "Y",    "유진투자증권"),
  SBC_287 ("287",   "bank",     1500,   "Y",    "메리츠증권"),
  SBC_290 ("290",   "bank",     1510,   "Y",    "부국증권"),
  SBC_291 ("291",   "bank",     1520,   "Y",    "신영증권"),
  SBC_294 ("294",   "bank",     1530,   "Y",    "한국포스증권"),
  SBC_292 ("292",   "bank",     1540,   "Y",    "케이프투자증권")
  ;

	private String orgDivCd; // 코드
	private String orgCd; // 서비스코드

	private int order; // 정렬순서
	private String useYn; // 사용여부
	private String nameKo; // 은행명

	private ScrapBankCd(String orgDivCd, String orgCd, int order, String useYn, String nameKo) {
		this.orgDivCd = orgDivCd;
		this.orgCd = orgCd;

		this.order = order;
		this.useYn = useYn;
		this.nameKo = nameKo;
	}

	public static String[] getCodeArray() {
		ScrapBankCd[] arr = ScrapBankCd.values();
		String[] ret = new String[arr.length];

		for (int i = 0; i < arr.length; i++) {
			ret[i] = arr[i].getOrgDivCd();
		}

		return ret;
	}

	public static String valuesJson() {
		String ret = "[]";

		try {
			ObjectMapper mapper = new ObjectMapper();
			ret = mapper.writeValueAsString(ScrapBankCd.values());
		} catch (IOException e) {
			// ignore
		}

		return ret;
	}

	public static ScrapBankCd findByOrgDivCd(String orgDivCd) {
		for (ScrapBankCd ccd : ScrapBankCd.values()) {
			if (ccd.orgDivCd.equals(orgDivCd)) {
				return ccd;
			}
		}

		return null;
	}
}
