export const MESSAGE_MODE = {
	LIST: 'list',
	VIEW: 'view',
	WRITE: 'write',
	EDIT: 'edit'
};

export const MESSAGE_OWN_MODE = {
	SENT: 'sent',
	RECIVED: 'recv'
};


export const DEF_DATA = {
	sc : {
		mode: MESSAGE_OWN_MODE.SENT,
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

export const DEF_MESSAGE_INF = {
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