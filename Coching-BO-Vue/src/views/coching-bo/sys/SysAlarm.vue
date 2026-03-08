<template>
  <b-card>
    <b-tabs class="sys-alarm-Tab"      
      fill pills 
    >
      <b-tab
        title="알림톡" lazy>		
        <SysAlarmContents
         :alramData="allimTocData"></SysAlarmContents>
		<small class="text-danger">* 실제 알림 내용 변경을 위해서는 카카오톡 심사 등을 거쳐야합니다.</small>
		
      </b-tab>
      <b-tab title="메일링" lazy>
        <SysAlarmContents
         :alramData="mailData"></SysAlarmContents>
      </b-tab>
      <b-tab
        title="앱푸쉬" lazy>
        <SysAlarmContents
         :alramData="appPushData"></SysAlarmContents>
      </b-tab>
    </b-tabs>

  </b-card>  
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import SysAlarmContents from './SysAlarmContents.vue';

const TH_DATA_ALLIMTOK = ['프로세스', '세부프로세스', '프로세스화면', '형식', '발신자', '수신자', '내용'];
const TD_DATA_ALLIMTOK = [
	{
		"process": "회원가입",
		"processDetail": "신규 회원 가입시",
		"processView": "회원가입",
		"type": "알림톡",
		"sender": "본사",
		"receiver": "고객",
		"content": "회원 가입이 완료되었습니다. \r\n\r\n안녕하세요. 소낙스 카케어 입니다. \r\nSONAX 카케어와 함께 최상의 차량 컨디션을 유지하세요."
	},
	{
		"process": "결제완료",
		"processDetail": "예약 결제 완료시",
		"processView": "서비스예약 >> 방문예약완료",
		"type": "알림톡",
		"sender": "가맹점",
		"receiver": "고객",
		"content": "{고객명} 님, \r\n예약일시 : {예약일시}\r\n서비스점 : {가맹점}\r\n예약서비스 : {사용쿠폰} \r\n                  {예약서비스}\r\n \r\n방문예약의 결제가 완료되었습니다. \r\n실제 시공하실 서비스는 예약 당일, 방문 후 전문 상담사와 상담하신 후 선택하세요.\r\n예약취소 및 예약일시 변경은 방문 예약 시간 3시간 이전 {예약일시-3시간} 까지만 가능합니다."
	},
	{
		"process": "예약일정 알림",
		"processDetail": "예약 전날 18시",
		"processView": "예약내역",
		"type": "알림톡",
		"sender": "본사",
		"receiver": "고객",
		"content": "{고객명} 님, \r\n방문예약일이 하루 남았습니다. \r\n\r\n예약일시 : {예약일시} \r\n서비스점 : {가맹점}\r\n예약서비스 : {사용쿠폰} \r\n                  {예약서비스}\r\n\r\n예약취소 및 예약일시 변경은 방문 예약 시간 3시간 이전 {예약일시-3시간} 까지만 가능합니다."
	},
	{
		"process": "예약일정 알림",
		"processDetail": "예약 당일 3시간 전",
		"processView": "예약내역",
		"type": "알림톡",
		"sender": "본사",
		"receiver": "고객",
		"content": "{고객명} 님, \r\n방문예약 시간이 3시간 남았습니다.\r\n\r\n예약일시 : {예약일시} \r\n서비스점 : {가맹점}\r\n예약서비스 : {사용쿠폰} \r\n                  {예약서비스}"
	},
	{
		"process": "예약일정 알림",
		"processDetail": "예약 당일 1시간 전",
		"processView": "예약내역",
		"type": "알림톡",
		"sender": "본사",
		"receiver": "고객",
		"content": "{고객명} 님, \r\n방문예약 시간이 1시간 남았습니다.\r\n\r\n예약일시 : {예약일시} \r\n서비스점 : {가맹점}\r\n예약서비스 : {사용쿠폰} \r\n                  {예약서비스}"
	},
	{
		"process": "예약일정 알림",
		"processDetail": "예약 시간도래",
		"processView": "예약내역",
		"type": "알림톡",
		"sender": "본사",
		"receiver": "고객",
		"content": "{고객명} 님, \r\n방문예약 시간이 되었습니다.\r\n\r\n예약일시 : {예약일시} \r\n서비스점 : {가맹점}\r\n예약서비스 : {사용쿠폰} \r\n                  {예약서비스} \r\n\r\n방문 하지 않은 경우, 예약 시 사용하신 쿠폰이나 예약금은 소멸됩니다."
	},
	{
		"process": "시공",
		"processDetail": "시공중 (입고)",
		"processView": "예약내역",
		"type": "알림톡",
		"sender": "가맹점",
		"receiver": "고객",
		"content": "{고객명} 님, \r\n차종 : {브랜드} {차종} \r\n차량번호 : {차량번호}\r\n차량이 입고되었습니다. \r\n\r\n예약번호 : {예약번호}"
	},
	{
		"process": "시공",
		"processDetail": "시공중 (완료)",
		"processView": "예약내역",
		"type": "알림톡",
		"sender": "가맹점",
		"receiver": "고객",
		"content": "{고객명} 님, \r\n차종 : {브랜드} {차종} \r\n차량번호 : {차량번호}\r\n시공이 완료되었습니다.\r\n \r\n예약번호 : {예약번호}"
	},
	{
		"process": "시공",
		"processDetail": "시공중 (출고)",
		"processView": "리뷰작성",
		"type": "알림톡",
		"sender": "가맹점",
		"receiver": "고객",
		"content": "{고객명} 님, \r\n차종 : {브랜드} {차종} \r\n차량번호 : {차량번호}\r\n서비스내역 : {서비스명}\r\n\r\n차량이 출고 되었습니다. \r\n시공하신 {가맹점} 에 별점을 남겨주세요."
	},
	{
		"process": "예약 변경, 취소",
		"processDetail": "출고 이후 서비스 변경시",
		"processView": "[가맹점] 예약변경 > 서비스변경",
		"type": "알림톡",
		"sender": "가맹점",
		"receiver": "고객",
		"content": "{고객명} 님, \r\n시공하셨던 {가맹점} 에서 진행하셨던 서비스를 변경하였습니다. \r\n해당 내용에 대한 설명을 듣지 못하셨다면 {가맹점} 으로 문의해주세요. \r\n전화번호 : {가맹점 전화번호}"
	},
	{
		"process": "예약 변경, 취소",
		"processDetail": "예약일시 변경",
		"processView": "[가맹점] 예약변경 > 예약일시 변경",
		"type": "알림톡",
		"sender": "가맹점",
		"receiver": "고객",
		"content": "안녕하세요 소낙스 카케어 입니다. \r\n예약일시가 변경되었습니다.\r\n변경 전 : {예약일시-변경전}\r\n변경 후 : {예약일시-변경후}\r\n해당 내용에 대한 설명을 듣지 못하셨다면 예약하신 {가맹점} 에 문의해주세요."
	},
	{
		"process": "예약 변경, 취소",
		"processDetail": "예약 취소 시",
		"processView": "[가맹점] 예약변경 > 예약취소",
		"type": "알림톡",
		"sender": "가맹점",
		"receiver": "고객",
		"content": "안녕하세요 소낙스 카케어 입니다. \r\n예약일시 : {예약일시}\r\n예약서비스 : {예약서비스}\r\n주문일시 : {주문일시}\r\n\r\n해당 예약이 취소되었습니다.\r\n감사합니다."
	},
	{
		"process": "문의",
		"processDetail": "관리자가 문의 답변시",
		"processView": "마이페이지 > 내문의내역",
		"type": "알림톡",
		"sender": "본사",
		"receiver": "고객",
		"content": "{고객명} 님,\r\n{문의등록일시} 에 작성한 문의에 대해 답변이 등록되었습니다. "
	},
	{
		"process": "쿠폰",
		"processDetail": "고객에게 쿠폰 발급시",
		"processView": "[관리자] 제휴쿠폰발송 \r\n[제휴사] 제휴쿠폰발송",
		"type": "알림톡",
		"sender": "본사",
		"receiver": "고객",
		"content": "안녕하세요, 소낙스 카케어 입니다. \r\n{제휴이벤트} 쿠폰이 도착했습니다. \r\n\r\n쿠폰번호 : {쿠폰시리얼} \r\n유효기간 : {쿠폰유효기간}\r\n\r\n소낙스체크인의 마이페이지 > 쿠폰관리 메뉴에서 등록이 가능합니다.\r\n문의전화 : 080-770-8080"
	},
	{
		"process": "쿠폰",
		"processDetail": "쿠폰 유효기간만료 1주일 전",
		"processView": "마이페이지 > 쿠폰관리",
		"type": "알림톡",
		"sender": "본사",
		"receiver": "고객",
		"content": "안녕하세요, 소낙스 카케어 입니다. \r\n보유하신 쿠폰의 유효기간 만료 1주일 전입니다.\r\n\r\n쿠폰명 : {쿠폰명} \r\n유효기간 : {쿠폰유효기간}\r\n\r\n소낙스체크인의 고객센터 > 문의하기 메뉴에서 문의 가능합니다."
	}
];

const TH_DATA_MAIL = ['프로세스', '세부프로세스', '프로세스화면', '형식', '발신자', '수신자', '내용'];
const TD_DATA_MAIL = [
	{
		"process": "고객문의",
		"processDetail": "고객이 문의 등록시",
		"processView": "[고객용] 고객센터 > 문의하기",
		"type": "메일",
		"sender": "고객",
		"receiver": "본사",
		"content": "고객센터 문의 등록 알림 \r\n\r\n문의 유형: {문의 유형}\r\n상세카테고리 : {상세카테고리}\r\n문의 제목 : {문의 제목}\r\n문의가 등록되었습니다. 자세한 사항은 아래 링크를 통해 확인해주세요."
	},
	{	"process": "고객문의",
		"processDetail": "고객이 댓글 등록시",
		"processView": "[고객용] 마이페이지 > 내문의내역",
		"type": "메일",
		"sender": "고객",
		"receiver": "본사",
		"content": "고객센터 문의 등록 알림 \r\n\r\n문의 유형: {문의 유형}\r\n상세카테고리 : {상세카테고리}\r\n문의 제목 : {문의 제목}\r\n{등록일시} 에 등록된 문의에 고객 댓글이 등록되었습니다.\r\n자세한 사항은 아래 링크를 통해 확인해주세요."
	},
	{
		"process": "상세정보 수정",
		"processDetail": "가맹점이 상세정보 수정시",
		"processView": "[가맹점] 서비스점 정보",
		"type": "메일",
		"sender": "가맹점",
		"receiver": "본사",
		"content": "가맹점 상세정보 수정 알림 \r\n\r\n{가맹점} 의 상세정보가 수정되었습니다. \r\n등록된 내용 : \r\n{상세소개 내용}"
	},
	{
		"process": "가맹점 일반문의",
		"processDetail": "가맹점이 일반문의 등록시",
		"processView": "[가맹점] 문의내역 > 본사 1:1 문의",
		"type": "메일",
		"sender": "가맹점",
		"receiver": "본사",
		"content": "가맹점 일반 문의 등록 알림\r\n\r\n{가맹점} 에서 문의가 등록되었습니다. \r\n제목 : {가맹점 일반문의 문의제목}\r\n문의일시 : {가맹점 일반문의 문의일시}"
	},
	{
		"process": "가맹점 일반문의",
		"processDetail": "가맹점이 댓글 등록시",
		"processView": "[가맹점] 문의내역 > 본사 1:1 문의",
		"type": "메일",
		"sender": "가맹점",
		"receiver": "본사",
		"content": "가맹점 일반 문의 댓글 등록 알림 \r\n\r\n제목 : {가맹점 일반문의 문의제목}\r\n문의일시 : {가맹점 일반문의 문의일시}\r\n댓글 : {가맹점 일반문의 본사의 댓글} \r\n\r\n가맹점 일반 문의 내역에 답글이 달렸습니다.\r\n회신이 필요한 경우 댓글을 입력해주세요."
	},
	{
		"process": "포인트 사용 신청",
		"processDetail": "가맹점이 포인트 사용 신청시",
		"processView": "[가맹점] 마이페이지 > 가맹점포인트",
		"type": "메일",
		"sender": "가맹점",
		"receiver": "본사",
		"content": "가맹점 포인트사용신청 알림 \r\n\r\n가맹점명: {가맹점}\r\n요청사유 : {포인트 사용 요청사유}\r\n요청일시 : {포인트 사용 요청일시}\r\n사용 요청 포인트 : {요청 포인트} \r\n\r\n포인트 사용이 요청되었습니다.\r\n자세한 사항은 아래 링크를 통해 확인해주세요."
	},
	{
		"process": "영업시간 변경 신청 \r\n(임시,영구)",
		"processDetail": "가맹점이 영업시간 변경신청시",
		"processView": "[가맹점] 문의내역 > 영업시간관리",
		"type": "메일",
		"sender": "가맹점",
		"receiver": "본사",
		"content": "가맹점 영업시간 변경신청 알림 \r\n\r\n{가맹점} 에서 영업시간 변경 신청을 하였습니다. \r\n자세한 사항은 아래 링크를 통해 확인해주세요."
	},
	{
		"process": "클레임",
		"processDetail": "가맹점이 클레임 댓글 등록시",
		"processView": "[가맹점] 문의내역 > 클레임내역",
		"type": "메일",
		"sender": "가맹점",
		"receiver": "본사",
		"content": "클레임 코멘트 등록 알림 \r\n\r\n가맹점에서 클레임에 코멘트를 작성하였습니다. \r\n자세한 사항은 아래 링크를 통해 확인해주세요."
  }
];

const TH_DATA_PUSH = ['수신자', '프로세스(아이콘)', '세부프로세스', '프로세스화면', '형식', '내용'];
const TD_DATA_PUSH = [
	{
		"receiver": "고객",
		"process": "회원가입",
		"processDetail": "신규 회원 가입시",
		"processView": "회원가입",
		"type": "앱 푸쉬",
		"content": "회원 가입이 완료되었습니다. \r\n\r\n안녕하세요. 소낙스 카케어 입니다. \r\n소낙스 카케어와 함께 최상의 차량 컨디션을 유지하세요."
	},
	{
		"receiver": "고객",
		"process": "예약",
		"processDetail": "예약 전날 18시",
		"processView": "예약내역",
		"type": "앱 푸쉬",
		"content": "예약 알림 \r\n\r\n방문예약일이 하루 남았습니다.\r\n{예약일시 -3시간} 부터는 예약취소 및 예약일시 변경이 불가능합니다."
	},
	{
		"receiver": "고객",
		"process": "예약",
		"processDetail": "예약 시간도래",
		"processView": "예약내역",
		"type": "앱 푸쉬",
		"content": "예약 알림 \r\n\r\n방문예약 시간이 되었습니다.\r\n방문 하지 않은 경우, 예약 시 사용하신 쿠폰이나 예약금은 소멸됩니다."
	},
	{
		"receiver": "고객",
		"process": "예약",
		"processDetail": "시공중 (출고)",
		"processView": "리뷰작성",
		"type": "앱 푸쉬",
		"content": "시공상태 알림 \r\n\r\n차량이 출고 되었습니다. \r\n시공하신 {가맹점} 에 별점을 남겨주세요."
	},
	{
		"receiver": "고객",
		"process": "예약",
		"processDetail": "출고 이후 서비스 변경시",
		"processView": "[가맹점] 예약변경 > 서비스변경",
		"type": "앱 푸쉬",
		"content": "시공한 서비스가 변경되었습니다.\r\n\r\n시공하셨던 {가맹점} 에서 진행하셨던 서비스를 변경하였습니다. \r\n해당 내용에 대한 설명을 듣지 못하셨다면 {가맹점} 으로 문의해주세요. \r\n전화번호 : {가맹점 전화번호}"
	},
	{
			"receiver": "고객",
			"process": "예약",
			"processDetail": "예약일시 변경",
			"processView": "[가맹점] 예약변경 > 예약일시 변경",
			"type": "앱 푸쉬",
			"content": "예약일시가 변경되었습니다. \r\n\r\n변경 전 : {예약일시-변경전}\r\n변경 후 : {예약일시-변경후}\r\n해당 내용에 대한 설명을 듣지 못하셨다면 예약하신 {가맹점} 에 문의해주세요."
	},
	{
		"receiver": "고객",
		"process": "예약",
		"processDetail": "예약 취소 시",
		"processView": "[가맹점] 예약변경 > 예약취소",
		"type": "앱 푸쉬",
		"content": "예약이 취소되었습니다. \r\n\r\n예약일시 : {예약일시}\r\n예약서비스 : {예약서비스}\r\n해당 예약이 취소되었습니다."
	},
	{
		"receiver": "고객",
		"process": "리뷰",
		"processDetail": "관리자가 문의 답변시",
		"processView": "마이페이지 > 내문의내역",
		"type": "앱 푸쉬",
		"content": "문의 답변 등록 알림 \r\n\r\n{문의등록일시} 에 작성한 문의에 대해 답변이 등록되었습니다. "
	},
	{
		"receiver": "고객",
		"process": "쿠폰",
		"processDetail": "고객에게 쿠폰 발급시",
		"processView": "[관리자] 제휴쿠폰발송 \r\n[제휴사] 제휴쿠폰발송",
		"type": "앱 푸쉬",
		"content": "쿠폰이 도착하였습니다.\r\n\r\n제휴이벤트 : {제휴이벤트} 쿠폰이 도착했습니다. \r\n쿠폰번호 : {쿠폰시리얼} \r\n유효기간 : {쿠폰유효기간}\r\n마이페이지 > 쿠폰관리 메뉴에서 등록이 가능합니다."
	},
	{
		"receiver": "고객",
		"process": "쿠폰",
		"processDetail": "쿠폰 유효기간만료 1주일 전",
		"processView": "마이페이지 > 쿠폰관리",
		"type": "앱 푸쉬",
		"content": "쿠폰 유효기간 만료 1주일 전입니다.\r\n\r\n쿠폰명 : {쿠폰명} \r\n유효기간 : {쿠폰유효기간}\r\n\r\n고객센터 > 문의하기 메뉴에서 문의 가능합니다."
	},
	{
		"receiver": "가맹점",
		"process": "문의",
		"processDetail": "본사에서 댓글 등록시",
		"processView": "[본사] 문의 > 가맹점 일반문의",
		"type": "푸쉬",
		"content": "본사 1:1문의 댓글 등록 알림 \r\n\r\n제목 : {가맹점 일반문의 문의제목}\r\n문의일시 : {가맹점 일반문의 문의일시}\r\n댓글 : {가맹점 일반문의 본사의 댓글} \r\n\r\n본사 1:1문의 내역에 답글이 달렸습니다.\r\n회신이 필요한 경우 댓글을 입력해주세요."
	},
	{
		"receiver": "가맹점",
		"process": "문의",
		"processDetail": "문의 완료처리 버튼 클릭시",
		"processView": "[본사] 문의 > 가맹점 일반문의",
		"type": "푸쉬",
		"content": "본사 1:1문의 완료 알림 \r\n\r\n문의하셨던 본사 1:1문의 건이 완료처리 되었습니다.\r\n제목 : {가맹점 일반문의 문의제목}\r\n문의일시 : {가맹점 일반문의 문의일시}"
	},
	{
		"receiver": "가맹점",
		"process": "문의",
		"processDetail": "포인트사용신청 - 적용시",
		"processView": "[본사] 문의 > 포인트 사용신청",
		"type": "푸쉬",
		"content": "포인트사용신청 적용 알림 \r\n\r\n요청사유 : {포인트 사용 요청사유}\r\n요청일시 : {포인트 사용 요청일시}\r\n사용 요청 포인트 : {요청 포인트} \r\n실제 사용 포인트 : {사용 포인트}\r\n요청하신 포인트가 적용되었습니다. \r\n사용 요청하신 포인트와 실제 차감된 포인트는 다를 수 있습니다."
	},
	{
		"receiver": "가맹점",
		"process": "문의",
		"processDetail": "포인트사용신청 - 반려시",
		"processView": "[본사] 문의 > 포인트 사용신청",
		"type": "푸쉬",
		"content": "포인트사용신청 반려 알림 \r\n\r\n요청사유 : {포인트 사용 요청사유}\r\n요청일시 : {포인트 사용 요청일시}\r\n사용 요청 포인트 : {요청 포인트} \r\n실제 사용 포인트 : 0\r\n요청하신 포인트 신청이 반려되었습니다. \r\n본사 관리자에게 문의해주세요."
	},
	{
		"receiver": "가맹점",
		"process": "문의",
		"processDetail": "본사 관리자가 포인트 적립시",
		"processView": "[본사] 가맹점관리 > 가맹점 포인트관리",
		"type": "푸쉬",
		"content": "포인트 적립 알림\r\n\r\n내용 : {적립 content}\r\n적립일시 : {포인트 적립일시}\r\n적립 포인트 : {적립 포인트}\r\n본사에서 포인트를 적립하였습니다. "
	},
	{
		"receiver": "가맹점",
		"process": "문의",
		"processDetail": "본사 관리자가 포인트 차감시",
		"processView": "[본사] 가맹점관리 > 가맹점 포인트관리",
		"type": "푸쉬",
		"content": "포인트 차감 알림\r\n\r\n내용 : {차감 content}\r\n차감일시 : {포인트 차감일시}\r\n차감 포인트 : {차감 포인트}\r\n본사에서 포인트를 차감하였습니다. "
	},
	{
		"receiver": "가맹점",
		"process": "문의",
		"processDetail": "영업시간 변경 신청 - 적용시",
		"processView": "[본사] 문의 > 영업시간 변경신청",
		"type": "푸쉬",
		"content": "영업시간 변경신청 적용 알림 \r\n\r\n본사에 신청한 영업시간 변경 신청이 적용되었습니다. "
	},
	{
		"receiver": "가맹점",
		"process": "문의",
		"processDetail": "영업시간 변경 신청 - 반려시",
		"processView": "[본사] 문의 > 영업시간 변경신청",
		"type": "푸쉬",
		"content": "영업시간 변경신청 반려 알림 \r\n\r\n본사에 신청한 영업시간 변경 신청이 반려되었습니다. "
	},
	{
		"receiver": "가맹점",
		"process": "클레임",
		"processDetail": "클레임 등록시",
		"processView": "[본사] 문의 > 클레임 확인요청",
		"type": "푸쉬",
		"content": "클레임 접수 알림 \r\n\r\n클레임이 발생하였습니다. \r\n제목 : {클레임제목}\r\n접수되었습니다."
	},
	{
		"receiver": "가맹점",
		"process": "클레임",
		"processDetail": "본사가 클레임 댓글 등록시",
		"processView": "[본사] 문의 > 클레임 확인요청",
		"type": "푸쉬",
		"content": "클레임 코멘트 등록 알림 \r\n\r\n본사에서 클레임에 코멘트를 작성하였습니다. "
	},
	{
		"receiver": "가맹점",
		"process": "클레임",
		"processDetail": "클레임 완료처리시",
		"processView": "[본사] 문의 > 클레임 확인요청",
		"type": "푸쉬",
		"content": "클레임 처리완료 알림 \r\n\r\n{클레임 등록일시} 에 발생한 클레임이 처리완료되었습니다."
	},
	{
		"receiver": "가맹점",
		"process": "우수가맹점 선정",
		"processDetail": "우수 가맹점 선정시",
		"processView": "[본사] 가맹점관리 > 우수가맹점관리",
		"type": "푸쉬",
		"content": "우수가맹점 선정 알림\r\n\r\n축하합니다. 우수 가맹점으로 선정되셨습니다.\r\n다음 우수가맹점 선정일까지 고객용 앱에서 가맹점 이름 옆에 우수가맹점 마크가 함께합니다.\r\n격려차원의 가맹점 포인트를 부여 받을 수 있습니다."
	}

];
export default {
  name: '',
  mixins: [ernsUtils],  
  components : {
    SysAlarmContents
  },
  props: {

  },
  watch: {
    
  },
  data(){
    return {
      allimTocData: {
        thData: [...TH_DATA_ALLIMTOK],
        contents: [...TD_DATA_ALLIMTOK]
      },
      mailData: {
        thData: [...TH_DATA_MAIL],
        contents: [...TD_DATA_MAIL]
      },
      appPushData: {
        thData: [...TH_DATA_PUSH],
        contents: [...TD_DATA_PUSH]
      }
    }
  },
  mounted(){
    const _vm = this;    
    
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    onSearch(){
      const _vm = this;      
    }
  }  
}
</script>

<style lang="scss" scoped>

</style>