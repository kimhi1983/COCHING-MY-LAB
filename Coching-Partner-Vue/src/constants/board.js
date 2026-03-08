export const BOARD_ID = {
	RAW_SOURCING_KO: 'BD_RAW_001',
	RAW_SOURCING_EN: 'BD_RAW_002',

	NOTICE_KO: 'BD_NT_001',
	NOTICE_EN: 'BD_NT_002',

	FAQ_KO: 'BD_FAQ_001',
	FAQ_EN: 'BD_FAQ_002',
};

export const BOARD_MODE = {
	LIST: 'list',
	VIEW: 'view',
	WRITE: 'write',
	EDIT: 'edit'
};

export const DEF_BOARD = {
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

export const DEF_BOARD_INF = {
  boardMstId : "",
  boardSeq : 0,
  cateCd : "",
  title : "",
  content : "",
  writer : "",
  sortOrd : 1,
  delYn : "N",
  adminDelCd : "",
  adminDelRsn : "",
  views : 0,

  fromDate : "",
  toDate : ""
};

export const DEF_FILE_INF = {
	filelist: [],
	delfilelist: [],
	fileId: ''
};