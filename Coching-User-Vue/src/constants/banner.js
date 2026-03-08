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

const DEF_SEARCH_OPT = {
	sc: {
		useYn: "Y",
		delYn: "N",
		dispYn: "Y",
		//notUsedYn: 'N',
	},
	pi:{
		curPage : 1,
		totalItem : 0,
		perPage : 0, //0이면 전체 목록 가져옴
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
};

const DEF_FILE_INF = {
	filelist: [],
	delfilelist: [],
	fileId: ''
};


export {
	BANNER_INFO,

	DEF_BANNER_INF,
	DEF_FILE_INF,
	DEF_SEARCH_OPT,
};