/**
 * 은행코드 정의
 */
const BANK_CODES = [
  {code: "002", icon:require("@/assets/images/coching/logo-simbol-KDB.svg")     , nameKo: "산업"},
  {code: "003", icon:require("@/assets/images/coching/logo-simbol-IBK.svg")     , nameKo: "기업"},
  {code: "004", icon:require("@/assets/images/coching/logo-simbol-KB.svg")      , nameKo: "국민"},
  {code: "007", icon:require("@/assets/images/coching/logo-simbol-SH.svg")      , nameKo: "수협"},
  {code: "011", icon:require("@/assets/images/coching/logo-simbol-NH.svg")      , nameKo: "농협"},
  {code: "023", icon:require("@/assets/images/coching/logo-simbol-SC.svg")      , nameKo: "SC"},
  {code: "020", icon:require("@/assets/images/coching/logo-simbol-WOORI.svg")   , nameKo: "우리"},
  {code: "027", icon:require("@/assets/images/coching/logo-simbol-CITI.svg")    , nameKo: "한국씨티"},
  {code: "031", icon:require("@/assets/images/coching/logo-simbol-DGB.svg")     , nameKo: "대구"},
  {code: "032", icon:require("@/assets/images/coching/logo-simbol-BNK.svg")     , nameKo: "부산"},
  {code: "035", icon:require("@/assets/images/coching/logo-simbol-SHIHAN.svg")  , nameKo: "제주"},
  {code: "034", icon:require("@/assets/images/coching/logo-simbol-GWANGJU.svg") , nameKo: "광주"},
  {code: "037", icon:require("@/assets/images/coching/logo-simbol-WOORI.svg")   , nameKo: "전북"},
  {code: "039", icon:require("@/assets/images/coching/logo-simbol-BNK.svg")     , nameKo: "경남"},
  {code: "045", icon:require("@/assets/images/coching/logo-simbol-MG.svg")      , nameKo: "새마을금고"},
  {code: "048", icon:require("@/assets/images/coching/logo-simbol-SHINHYUP.svg"), nameKo: "신협"},
  {code: "050", icon:require("@/assets/images/coching/logo-simbol-SB.svg")      , nameKo: "상호저축"},
  {code: "054", icon:require("@/assets/images/coching/logo-simbol-HSBC.svg")    , nameKo: "홍콩(HSBC은행)"},
  {code: "055", icon:require("@/assets/images/coching/logo-simbol-DOICH.svg")   , nameKo: "도이치"},
  {code: "057", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "JP모간"},
  {code: "060", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "뱅크오브아메리카"},
  {code: "061", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "BNP파리바"},
  {code: "062", icon:require("@/assets/images/coching/logo-simbol-CHINA.svg")   , nameKo: "중국공상"},
  {code: "064", icon:require("@/assets/images/coching/logo-simbol-SANLIM.svg")  , nameKo: "산림조합"},
  {code: "067", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "중국건설은행"},
  {code: "071", icon:require("@/assets/images/coching/logo-simbol-POST.svg")    , nameKo: "우체국"},
  {code: "081", icon:require("@/assets/images/coching/logo-simbol-HANA.svg")    , nameKo: "KEB하나"},
  {code: "088", icon:require("@/assets/images/coching/logo-simbol-SHIHAN.svg")  , nameKo: "신한"},
  {code: "089", icon:require("@/assets/images/coching/logo-simbol-KBANK.svg")   , nameKo: "K뱅크"},
  {code: "090", icon:require("@/assets/images/coching/logo-simbol-KAKAO.svg")   , nameKo: "카카오뱅크"},
  {code: "209", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "유안타증권"},
  {code: "218", icon:require("@/assets/images/coching/logo-simbol-KB.svg")      , nameKo: "KB증권"},
  {code: "227", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "KTB투자증권"},
  {code: "238", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "미래에셋대우"},
  {code: "240", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "삼성증권"},
  {code: "243", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "한국투자증권"},
  {code: "247", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "NH투자증권"},
  {code: "261", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "교보증권"},
  {code: "262", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "하이투자증권"},
  {code: "263", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "현대차증권"},
  {code: "264", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "키움증권"},
  {code: "265", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "이베스트투자증권"},
  {code: "266", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "에스케이증권"},
  {code: "267", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "대신증권"},
  {code: "269", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "한화투자증권"},
  {code: "270", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "하나금융투자"},
  {code: "278", icon:require("@/assets/images/coching/logo-simbol-SHIHAN.svg")  , nameKo: "신한금융투자"},
  {code: "279", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "DB금융투자"},
  {code: "280", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "유진투자증권"},
  {code: "287", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "메리츠증권"},
  {code: "290", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "부국증권"},
  {code: "291", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "신영증권"},
  {code: "294", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "한국포스증권"},
  {code: "292", icon:require("@/assets/images/coching/logo-simbol.svg")         , nameKo: "케이프투자증권"},
];
const DEFAULT_BANK = {
   code: "999", icon:require("@/assets/images/coching/logo-simbol.svg")   , nameKo: "ㅇㅇ은행"}

/**
 * 카드사 코드 정의 scd
 */
const SCD_CARD_CODES = [
  {code: "001", icon:require("@/assets/images/coching/logo-SINHAN.png")   , nameKo: "신한카드"},
  {code: "002", icon:require("@/assets/images/coching/logo-HYUNDAI.png")  , nameKo: "현대카드"},
  {code: "003", icon:require("@/assets/images/coching/logo-SAMSUNG.png")  , nameKo: "삼성카드"},
  {code: "004", icon:require("@/assets/images/coching/logo-KB.png")       , nameKo: "KB국민카드"},
  {code: "005", icon:require("@/assets/images/coching/logo-LOTTE.png")    , nameKo: "롯데카드"},
  {code: "006", icon:require("@/assets/images/coching/logo-HANA.png")     , nameKo: "하나카드"},
  {code: "008", icon:require("@/assets/images/coching/logo-NONGHYUP.png") , nameKo: "농협카드"},
  {code: "010", icon:require("@/assets/images/coching/logo-BC.png")       , nameKo: "BC카드"},
];
const DEFAULT_CARD = {
  code: "999", icon:require("@/assets/images/coching/logo-simbol.svg")    , nameKo: "ㅇㅇ카드"};

/**
 * 카드사 코드 정의 (지출용)
 */
 const COST_CARD_CODES = [  
  {code: "001",	icon:require("@/assets/images/coching/logo-SINHAN.png")       , nameKo: "신한카드"},
  {code: "002",	icon:require("@/assets/images/coching/logo-HYUNDAI.png")      , nameKo: "현대카드"},
  {code: "003",	icon:require("@/assets/images/coching/logo-SAMSUNG.png")      , nameKo: "삼성카드"},
  {code: "004",	icon:require("@/assets/images/coching/logo-KB.png")           , nameKo: "KB국민카드"},
  {code: "005",	icon:require("@/assets/images/coching/logo-LOTTE.png")        , nameKo: "롯데카드"},
  {code: "006",	icon:require("@/assets/images/coching/logo-HANA.png")         , nameKo: "하나카드"},
  {code: "008",	icon:require("@/assets/images/coching/logo-NONGHYUP.png")     , nameKo: "농협카드"},
  {code: "010",	icon:require("@/assets/images/coching/logo-BC.png")           , nameKo: "BC카드"},
  {code: "007",	icon:require("@/assets/images/coching/logo-WOORI.png")        , nameKo: "우리카드"},  
  {code: "009",	icon:require("@/assets/images/coching/logo-CITI.png")         , nameKo: "씨티카드"},
  {code: "011",	icon:require("@/assets/images/coching/logo-SUHYUP.png")       , nameKo: "수협카드"},
  {code: "012",	icon:require("@/assets/images/coching/logo-KJ.png")           , nameKo: "광주카드"},
  {code: "013",	icon:require("@/assets/images/coching/logo-KJ.png")           , nameKo: "전북카드"},
  {code: "015",	icon:require("@/assets/images/coching/logo-KBANK.png")        , nameKo: "K뱅크카드"},
  {code: "016",	icon:require("@/assets/images/coching/logo-POST.png")         , nameKo: "우체국"},
  {code: "017",	icon:require("@/assets/images/coching/logo-KDB.png")          , nameKo: "산업카드"},
  {code: "018",	icon:require("@/assets/images/coching/logo-MG.png")           , nameKo: "새마을카드"},
  {code: "373",	icon:require("@/assets/images/coching/logo-JEJU.jpg")         , nameKo: "제주카드"},
  {code: "020",	icon:require("@/assets/images/coching/logo-SHINHYUP.png")     , nameKo: "신협카드"},
];
const DEFAULT_COST_CARD = {
  code: "ZZZ", icon:require("@/assets/images/coching/logo-simbol.svg")        , nameKo: "ㅇㅇ카드" };

/**
 * 쇼핑몰 코드 정의
 */
const MALL_CODES = [
  {code: "AU", icon:require("@/assets/images/coching/mall-auction.png")   ,nameKo: "옥션"         },
  {code: "GM", icon:require("@/assets/images/coching/mall-gmarket.png")   ,nameKo: "지마켓"       },
  {code: "IP", icon:require("@/assets/images/coching/mall-interpark.png") ,nameKo: "인터파크"     },
  {code: "ES", icon:require("@/assets/images/coching/mall-11street.png")  ,nameKo: "11번가"       },
  {code: "SF", icon:require("@/assets/images/coching/mall-naver.png")     ,nameKo: "스마트스토어" },
  {code: "CP", icon:require("@/assets/images/coching/mall-coupang.png")   ,nameKo: "쿠팡"         },
  {code: "TM", icon:require("@/assets/images/coching/mall-tmon.png")      ,nameKo: "티몬"         },
  {code: "WM", icon:require("@/assets/images/coching/mall-wmp.png")       ,nameKo: "위메프"       },
];
const DEFAULT_MALL = {
  code: "00", icon:require("@/assets/images/coching/logo-simbol.svg")     ,nameKo: "ㅇㅇ쇼핑"     };

/**
 * 배달APP 코드 정의
 */
 const DLAPP_CODES = [
  {code: "BM" , icon:require("@/assets/images/coching/app-baemin.png")      ,nameKo: "배달의민족"   },
  {code: "YO" , icon:require("@/assets/images/coching/app-coupangeats.png") ,nameKo: "쿠팡이츠"     },
  {code: "CPE", icon:require("@/assets/images/coching/app-yogiyo.png")      ,nameKo: "요기요"       },  
];
const DEFAULT_DLAPP = {
  code: "00", icon:require("@/assets/images/coching/logo-simbol.svg")       ,nameKo: "ㅇㅇEats"     };

/**
 * (SCD)매출연결 은행사 코드 정의
 */
const SCD_BANK_CODES = [
	{code: "004"  ,nameKo: "KB국민은행"	          , icon:require("@/assets/images/coching/logo-simbol-KB.svg")},
	{code: "088"  ,nameKo: "신한은행"		          , icon:require("@/assets/images/coching/logo-simbol-SHIHAN.svg")},
	{code: "020"  ,nameKo: "우리은행"		          , icon:require("@/assets/images/coching/logo-simbol-WOORI.svg")},
	{code: "011"  ,nameKo: "NH농협은행"		        , icon:require("@/assets/images/coching/logo-simbol-NH.svg")},
	{code: "011"  ,nameKo: "지역농협"			        , icon:require("@/assets/images/coching/logo-simbol-NH.svg")},
	{code: "081"  ,nameKo: "하나은행"			        , icon:require("@/assets/images/coching/logo-simbol-HANA.svg")},
	{code: "003"  ,nameKo: "IBK기업은행"	        , icon:require("@/assets/images/coching/logo-simbol-IBK.svg")},
	{code: "023"  ,nameKo: "SC제일은행"		        , icon:require("@/assets/images/coching/logo-simbol-SC.svg")},
	{code: "032"  ,nameKo: "부산은행"			        , icon:require("@/assets/images/coching/logo-simbol-BNK.svg")},
	{code: "031"  ,nameKo: "DGB대구은행"	        , icon:require("@/assets/images/coching/logo-simbol-DGB.svg")},
	{code: "071"  ,nameKo: "우체국"		  	        , icon:require("@/assets/images/coching/logo-simbol-POST.svg")},
	{code: "090"  ,nameKo: "카카오뱅크"		        , icon:require("@/assets/images/coching/logo-simbol-KAKAO.svg")},
	{code: "045"  ,nameKo: "새마을금고"		        , icon:require("@/assets/images/coching/logo-simbol-MG.svg")},
	{code: "039"  ,nameKo: "경남은행"			        , icon:require("@/assets/images/coching/logo-simbol-BNK.svg")},
	{code: "034"  ,nameKo: "광주은행"			        , icon:require("@/assets/images/coching/logo-simbol-GWANGJU.svg")},
	{code: "027"  ,nameKo: "한국씨티은행"	        , icon:require("@/assets/images/coching/logo-simbol-CITI.svg")},
	{code: "002"  ,nameKo: "KDB산업은행"	        , icon:require("@/assets/images/coching/logo-simbol-KDB.svg")},
	{code: "089"  ,nameKo: "케이뱅크"			        , icon:require("@/assets/images/coching/logo-simbol-KBANK.svg")},
	{code: "048"  ,nameKo: "신용협동조합(신협)"		, icon:require("@/assets/images/coching/logo-simbol-SHINHYUP.svg")},
	{code: "037"  ,nameKo: "전북은행"			        , icon:require("@/assets/images/coching/logo-simbol-WOORI.svg")},
	{code: "007"  ,nameKo: "수협은행"			        , icon:require("@/assets/images/coching/logo-simbol-SH.svg")},
	{code: "050"  ,nameKo: "상호저축은행"	        , icon:require("@/assets/images/coching/logo-simbol-SB.svg")},
	{code: "035"  ,nameKo: "제주은행"			        , icon:require("@/assets/images/coching/logo-simbol-SHIHAN.svg")}, //신한은행과 로고 같다함
	{code: "064"  ,nameKo: "산림조합중앙회"	      , icon:require("@/assets/images/coching/logo-simbol-SANLIM.svg")},
	{code: "055"  ,nameKo: "도이치은행"		        , icon:require("@/assets/images/coching/logo-simbol-DOICH.svg")},
	{code: "062"  ,nameKo: "중국공상은행"	        , icon:require("@/assets/images/coching/logo-simbol-CHINA.svg")},
	{code: "054"  ,nameKo: "HSBC(홍상은행)"	      , icon:require("@/assets/images/coching/logo-simbol-HSBC.svg")}
];      

/**
 * 계좌연동 은행코드 정의
 */
 const LINK_BANK_CODES = [
  {code: "004", icon:require("@/assets/images/coching/logo-KB.png")       ,symbol:require("@/assets/images/coching/logo-simbol-KB.svg")      , nameKo: "국민"},
  {code: "088", icon:require("@/assets/images/coching/logo-SINHAN.png")   ,symbol:require("@/assets/images/coching/logo-simbol-SHIHAN.svg")  , nameKo: "신한"},
  {code: "011", icon:require("@/assets/images/coching/logo-NONGHYUP.png") ,symbol:require("@/assets/images/coching/logo-simbol-NH.svg")      , nameKo: "농협"},
  {code: "023", icon:require("@/assets/images/coching/logo-SC.png")       ,symbol:require("@/assets/images/coching/logo-simbol-SC.svg")      , nameKo: "SC"},
  {code: "020", icon:require("@/assets/images/coching/logo-WOORI.png")    ,symbol:require("@/assets/images/coching/logo-simbol-WOORI.svg")   , nameKo: "우리"},
  {code: "081", icon:require("@/assets/images/coching/logo-HANA.png")     ,symbol:require("@/assets/images/coching/logo-simbol-HANA.svg")    , nameKo: "KEB하나"},
  {code: "003", icon:require("@/assets/images/coching/logo-IBK.png")      ,symbol:require("@/assets/images/coching/logo-simbol-IBK.svg")     , nameKo: "기업"},
  {code: "045", icon:require("@/assets/images/coching/logo-MG.png")       ,symbol:require("@/assets/images/coching/logo-simbol-MG.svg")      , nameKo: "새마을금고"},
  {code: "027", icon:require("@/assets/images/coching/logo-CITI.png")     ,symbol:require("@/assets/images/coching/logo-simbol-CITI.svg")    , nameKo: "한국씨티"},
  {code: "002", icon:require("@/assets/images/coching/logo-KDB.png")      ,symbol:require("@/assets/images/coching/logo-simbol-KDB.svg")     , nameKo: "산업"},
  {code: "007", icon:require("@/assets/images/coching/logo-SUHYUP.png")   ,symbol:require("@/assets/images/coching/logo-simbol-SH.svg")      , nameKo: "수협"},
  {code: "039", icon:require("@/assets/images/coching/logo-BNK.png")      ,symbol:require("@/assets/images/coching/logo-simbol-BNK.svg")     , nameKo: "경남"},
  {code: "035", icon:require("@/assets/images/coching/logo-JEJU.jpg")     ,symbol:require("@/assets/images/coching/logo-simbol-SHIHAN.svg")  , nameKo: "제주"},
  {code: "032", icon:require("@/assets/images/coching/logo-BNK.png")      ,symbol:require("@/assets/images/coching/logo-simbol-BNK.svg")     , nameKo: "부산"},
  {code: "034", icon:require("@/assets/images/coching/logo-KJ.png")       ,symbol:require("@/assets/images/coching/logo-simbol-GWANGJU.svg") , nameKo: "광주"},
  {code: "048", icon:require("@/assets/images/coching/logo-SHINHYUP.png") ,symbol:require("@/assets/images/coching/logo-simbol-SHINHYUP.svg"), nameKo: "신협"},
  {code: "037", icon:require("@/assets/images/coching/logo-WOORI.png")    ,symbol:require("@/assets/images/coching/logo-simbol-WOORI.svg")   , nameKo: "전북"},
  {code: "031", icon:require("@/assets/images/coching/logo-DGB.png" )     ,symbol:require("@/assets/images/coching/logo-simbol-DGB.svg")     , nameKo: "대구"},
  {code: "071", icon:require("@/assets/images/coching/logo-POST.png")     ,symbol:require("@/assets/images/coching/logo-simbol-POST.svg")    , nameKo: "우체국"},
  {code: "089", icon:require("@/assets/images/coching/logo-KBANK.png")    ,symbol:require("@/assets/images/coching/logo-simbol-KBANK.svg")   , nameKo: "K뱅크"},
  {code: "090", icon:require("@/assets/images/coching/logo-KAKAO.png")    ,symbol:require("@/assets/images/coching/logo-simbol-KAKAO.svg")   , nameKo: "카카오뱅크"},
];


const setDarkModeInHtml = function(isDarkMode){
  if(isDarkMode){
    document.documentElement.classList.add('dark-mode');
  }else{
    document.documentElement.classList.remove('dark-mode');
  }
};

const storageDarkMode = 'erns.coching.pc.darkMode';

const getDarkModeInLocalStorage = function(){
	const isDarkMode = JSON.parse(localStorage.getItem(storageDarkMode) || "false");
	return isDarkMode;
};

export default {    
  storagePushTokenKey : 'erns.coching.pc.pushInfoData',
  storageBizLicInfoListKey : 'erns.coching.pc.bizLicInfoData',
  storageIsVisitedInfoKeyName: 'erns.coching.pc.firstVisitData',
  storageScdJoinInfo: 'erns.coching.pc.scdJoinInfo',

  storageDarkMode : storageDarkMode,

  DEFAULT_NAVINFO :{
    curPath : "",
    meta : {
      pageTitle : "",
      header : {
        show:true,
        isMain:false,
        showStore : true,
        showBackButton : true,
        emptyTitle:false,
        showStatTotalLayer : false,
      },
      bottomNav : {
        show:true,
      }
    }
  },

  BANK_CODES,
  DEFAULT_BANK,

  SCD_CARD_CODES,
  DEFAULT_CARD,

  COST_CARD_CODES,
  DEFAULT_COST_CARD,
  
  MALL_CODES,
  DEFAULT_MALL,

  DLAPP_CODES,
  DEFAULT_DLAPP,

  SCD_BANK_CODES,

  LINK_BANK_CODES,

  setDarkModeInHtml,
  getDarkModeInLocalStorage,
};

