const BOARD_INFO = {
	RAW_SOURCING_KO : {
		id: 'BD_RAW_001',
		name: '원료소싱[ko]',
	},
	RAW_SOURCING_EN : {
		id: 'BD_RAW_002',
		name: '원료소싱[en]',
	},

	NOTICE_KO : {
		id: 'BD_NT_001',
		name: '공지사항[ko]',
	},
	NOTICE_EN : {
		id: 'BD_NT_002',
		name: '공지사항[en]',
	},

	FAQ_KO : {
		id: 'BD_FAQ_001',
		name: 'FAQ 관리[ko]',
	},
	FAQ_EN : {
		id: 'BD_FAQ_002',
		name: 'FAQ 관리[en]',
	},

	INQR_KO : {
		id: 'BD_INQR_001',
		name: '1:1문의 관리[ko]',
	},
	INQR_EN : {
		id: 'BD_INQR_002',
		name: '1:1문의 관리[en]',
	},

	WEEKLYNEWS_KO : {
		id: 'BD_WN_001',
		name: 'WeeklyNews[ko]',
	},
	WEEKLYNEWS_EN : {
		id: 'BD_WN_002',
		name: 'WeeklyNews[en]',
	},
};

const BOARD_ID = {
	RAW_SOURCING_KO: BOARD_INFO.RAW_SOURCING_KO.id,
	RAW_SOURCING_EN: BOARD_INFO.RAW_SOURCING_EN.id,

	NOTICE_KO: BOARD_INFO.NOTICE_KO.id,
	NOTICE_EN: BOARD_INFO.NOTICE_EN.id,

	FAQ_KO: BOARD_INFO.FAQ_KO.id,
	FAQ_EN: BOARD_INFO.FAQ_EN.id,

	INQR_KO: BOARD_INFO.INQR_KO.id,
	INQR_EN: BOARD_INFO.INQR_EN.id,

	WEEKLYNEWS_KO: BOARD_INFO.WEEKLYNEWS_KO.id,
	WEEKLYNEWS_EN: BOARD_INFO.WEEKLYNEWS_EN.id,
};

const BOARD_MODE = {
	LIST: 'list',
	VIEW: 'view',
	WRITE: 'write',
	EDIT: 'edit'
};

const DEF_SEARCH_OPT = {
  pi:{
    curPage : 1,
    totalItem : 0,
    perPage : 15
  },
  sc: {
    delYn: 'N',
    titleL: '',
    boardMstId : '',
    category : "",
  }
};

const DEF_BOARD = {
	sc : {
    titleL: '',
    delYn: 'N'
	},
	pi:{
		curPage : 1,
    totalItem : 0,
    perPage : 10
		//perPage : 1 //Test
	},
	summary : []
};

const DEF_BOARD_INF = {
  boardMstId : "",
  boardSeq : 0,
  cateCd : "",
  title : "",
  content : "",
  writer : "",
  orgSeq : 0,
  sortOrd : 1,
  delYn : "N",
  adminDelCd : "",
  adminDelRsn : "",
  views : 0,

  fromDate : "",
  toDate : ""
};

const DEF_INQR_BOARD_INF = {
  boardMstId : "",
  boardSeq : 0,
  cateCd : "",
  title : "",
  content : "",
  writer : "",
  phone : "",
  email : "",
  orgSeq : 0,
  sortOrd : 1,
  delYn : "N",
  adminDelCd : "",
  adminDelRsn : "",
  views : 0,

  fromDate : "",
  toDate : ""
};

const DEF_FILE_INF = {
	filelist: [],
	delfilelist: [],
	fileId: ''
};

export {
	BOARD_INFO,
	BOARD_ID,
	BOARD_MODE,

	DEF_BOARD,
	DEF_BOARD_INF,
	DEF_FILE_INF,
	DEF_SEARCH_OPT,

	DEF_INQR_BOARD_INF,
};