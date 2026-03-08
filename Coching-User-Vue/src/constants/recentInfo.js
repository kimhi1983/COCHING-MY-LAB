export const REF_CODES = {
	RAW: 'raw',
	PROD: 'prod',	
};

export const RECENT_INFO_MODE = {
	LIST: 'list',
};

export const DEF_DATA = {
	sc : {
		refCode: REF_CODES.RAW,
	},
	pi:{
		curPage : 1,
		totalItem : 0,
		perPage : 10
		//perPage : 1 //Test
	},
	summary : []
};

export const DEF_RECENT_INFO_INF = {
	messageSeq: 0,
	blockYn: "N",
	content: "",
	sender: 0,
	senderName: "",
	receiver: 0,
	receiverName: "",
	refCode: "",
	refSeq: 0,
	rgtDttm: null,
	state: "0"
};