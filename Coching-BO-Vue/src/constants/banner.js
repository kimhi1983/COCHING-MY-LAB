import { title } from "@/@core/utils/filter";

const BANNER_INFO = {
	BANNER_0001 : {
		id: 'BANNER_0001',
		name: '메인화면 상단',
	},
	BANNER_0002 : {
		id: 'BANNER_0002',
		name: '광고 배너',
	},
};

const BANNER_MODE = {
	LIST: 'list',
	VIEW: 'view',
	WRITE: 'write',
	EDIT: 'edit'
};

const DEF_SEARCH_OPT = {
	sc: {
    useYn: "Y",
		delYn: "N",
		notUsedYn: 'N',
	dispYn: 'Y',
  },
  pi:{
    curPage : 1,
    totalItem : 0,
    perPage : 15,
		//perPage : 2,
  },
};


const DEF_BANNER_INF = {
	bannerSeq: 0,
  bannerName: "",
  bannerType: "", // 롤링, 고정 ...
  fromDate: "",
  toDate: "",
  bannerImgUrl: "",
  bannerDesc: "",
  topPhrase : "",
  bottomPhrase: "",
  bannerUrl: "",
  bannerWidth: 0,
  bannerHeight: 0,
  text1: "",
  text2: "",
  text3: "",
  bannerHit: 0,
  bannerOrder: 1,
  useYn: "Y",
  delYn: "N",
  file: null,

	// clicks : 0,

  // bannerMstCd : "",
  // bannerSeq : 0,
  
  // bannerName : "",
	// bannerWidth : 0,
	// bannerHeight : 0,
	// sortOrd : 9999,
  // bannerDesc : "",
	

	// useYn : "Y",
  // delYn : "N",
  
  // fromDate : "",
  // toDate : "",

	// rgtr: 0,
	// rgtDttm : "",
	// chnr : 0,
	// chngDttm : "",
};

const DEF_BANNER_AD_INF = {
  bannerSeq: 0,
  bannerName: "",
  bannerType: "", // 롤링, 고정 ...
  title: "",
  content: "",
  fromDate: "",
  toDate: "",
  adStatus: "",
  dispYn: "Y",
  useYn: "Y",
  delYn: "N",
  refSeq: "",
  refCode: "",
  refUrl: "",
  remarks: "",
  clicks: 0,
  file: null
};



const DEF_FILE_INF = {
	filelist: [],
	delfilelist: [],
	fileId: ''
};

export {
	BANNER_INFO,
	BANNER_MODE,

	DEF_BANNER_INF,
	DEF_BANNER_AD_INF,
	DEF_FILE_INF,
	DEF_SEARCH_OPT,
};