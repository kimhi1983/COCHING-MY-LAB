const POPUP_INFO = {
	POPUP_0001 : {
		id: 'POPUP_0001',
		name: '사용자 팝업',
	},
	POPUP_0002 : {
		id: 'POPUP_0002',
		name: '파트너 팝업',
	},
};

const POPUP_MODE = {
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
  },
  pi:{
    curPage : 1,
    totalItem : 0,
    perPage : 15,
		//perPage : 2,
  },
};


const DEF_POPUP_INF = {
	popupSeq: 0,
  popupName: "",
  popupType: "", // 롤링, 고정 ...
  fromDate: "",
  toDate: "",
  popupImgUrl: "",
  popupDesc: "",
  topPhrase : "",
  bottomPhrase: "",
  popupUrl: "",
  popupWidth: 0,
  popupHeight: 0,
  text1: "",
  text2: "",
  text3: "",
  popupHit: 0,
  popupOrder: 1,
  useYn: "Y",
  delYn: "N",
  closeOpt: '1',
  file: null
};



const DEF_FILE_INF = {
	filelist: [],
	delfilelist: [],
	fileId: ''
};

export {
	POPUP_INFO,
	POPUP_MODE,

	DEF_POPUP_INF,
	DEF_FILE_INF,
	DEF_SEARCH_OPT,
};