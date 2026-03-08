import moment from 'moment';
import 'moment/locale/ko';
import { i18n } from '@/utils/i18n';

const FMT_DOT_yyyyMMdd = 'YYYY.MM.DD';
const FMT_DASH_yyyyMMdd = 'YYYY-MM-DD';

const FMT_DOT_yyyyMMdd_HHmiss = 'YYYY.MM.DD HH:mm:ss';
const FMT_DASH_yyyyMMdd_HHmiss = 'YYYY-MM-DD HH:mm:ss';

const FMT_TIME = "HH:mm:ss";

const FMT_HANGLE_yyyyMMdd = 'YYYY년 M월 D일';
const FMT_ENGLISH_yyyyMMdd = 'MM-DD-YYYY';

const FMT_HANGLE_yyyyMMdd_dddd_HHmiss = 'YYYY년 M월 D일 dddd HH:mm';

const hanDays2 = function(value, daysArray) {
  if (!value) {
    return "";
  }

  const daysArr = daysArray || ["일", "월", "화", "수", "목", "금", "토"];
  const date = moment(value).toDate();
  const day = date.getDay();
  const fmtDate = moment(value).format(FMT_HANGLE_yyyyMMdd);

  return fmtDate + " (" + daysArr[day] + ")";
}

const engDays2 =  function(value, daysArray) {
  if (!value) {
    return "";
  }

  const daysArr = daysArray || ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
  const date = moment(value).toDate();
  const day = date.getDay();
  const fmtDate = moment(value).format(FMT_ENGLISH_yyyyMMdd);

  return fmtDate + " (" + daysArr[day] + ")";
}

// 송금 예정일 => Today + 4영업일 
const hanSettleDate = function() {
  const daysArr = ["일", "월", "화", "수", "목", "금", "토"];
  const currentDate = new Date();
  currentDate.setDate(currentDate.getDate() + 4);
  
  if (currentDate.getDay() == "6") {
    // 토요일
    currentDate.setDate(currentDate.getDate() + 2);
  } else if (currentDate.getDay() == "0") {
    // 일요일
    currentDate.setDate(currentDate.getDate() + 1);
  }
  
  const day = currentDate.getDay();
  const fmtDate = moment(currentDate).format(FMT_HANGLE_yyyyMMdd);
  return fmtDate + " (" + daysArr[day] + ")";
}

const engSettleDate = function() {
  const daysArr = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
  const currentDate = new Date();
  currentDate.setDate(currentDate.getDate() + 4);
  
  if (currentDate.getDay() == 6) {
    // 토요일
    currentDate.setDate(currentDate.getDate() + 2);
  } else if (currentDate.getDay() == 0) {
    // 일요일
    currentDate.setDate(currentDate.getDate() + 1);
  }
  
  const day = currentDate.getDay();
  const fmtDate = moment(currentDate).format(FMT_ENGLISH_yyyyMMdd);
  return fmtDate + " (" + daysArr[day] + ")";
}


/**
 * {{data.udtDate | eFmtDate('.')}}
 * {{data.udtDate | eFmtDate()}}
 * {{data.udtDate | eFmtDate('-')}}
 */
export default {
  data() {
    return {

    }
  },
  filters: {
      //승인일 -> 날짜로 변경
      eFmtStrAuthDateToDate(value) {
        if (value) {
          return moment(value, 'YYMMDDhhmmss');        
        }

        return value;
      },

     //날짜 포멧
     eFmtDate(value, delimiter) {
      const d = delimiter || '-';

      if(!value || value.length < 10){
        return value;
      }

      if (value) {
        let tempValue = value;

        if(tempValue.length >= 10){
          tempValue = tempValue.substring(0,10);
        }

        switch(d){
          case '.':
            return moment(tempValue).format(FMT_DOT_yyyyMMdd);
          case '-':
          default:
            return moment(tempValue).format(FMT_DASH_yyyyMMdd);
        }
      }

      return value;
    },

    //날짜 시간 포멧
    eFmtDateTime(value, delimiter) {
      const d = delimiter || '-';
      if (value) {
        switch(d){
          case '.':
            return moment(value).format(FMT_DOT_yyyyMMdd_HHmiss);
          case '-':
          default:
            return moment(value).format(FMT_DASH_yyyyMMdd_HHmiss);
        }
      }

      return value;
    },

    //한글
    eFmtDateHangle(value, fmt) {
      const sFmt = fmt || FMT_HANGLE_yyyyMMdd;
      if (value) {
        return moment(value).format(sFmt);
      }

      return value;
    },

    //날짜 포멧
    eFmtDateFormat(value, fmt) {
      const sFmt = fmt || FMT_TIME;
      if (value) {
        return moment(value).format(sFmt);        
      }

      return value;
    },

    //시간
    eFmtTimeFormat(value) {
      if(!value) {
        return "-";
      }
      const tv = value || "";
      const t1 = tv.replace(/[^0-9]/g, "").replace(/(^[0-9]{2})([0-9]+)?([0-9]{2})$/,"$1:$2:$3");
      //console.debug(t1);
      return t1.replace("::", ":");
    },

    eFmtDays(value, daysArray){
      if (!value) {
        return value;        
      }

      const daysArr = daysArray || ["일", "월", "화", "수", "목", "금", "토"];
      const date = moment(value).toDate();
      const day = date.getDay();
      return daysArr[day];
    },

    eFmtDays2localized(val){
      if (!val) return '';
      if (i18n.locale === 'ko') {
        return hanDays2(val);
      } else {
        return engDays2(val);
      }
    },

    eFmtSettle2localized(){
      if (i18n.locale === 'ko') {
        return hanSettleDate();
      } else {
        return engSettleDate();
      }
    },

    //오늘 체크 후 날짜 포맷
    eFmtDateHMFormat(value, fmt) {
      const sFmt = fmt || FMT_DOT_yyyyMMdd;
      const targetDate = moment(value); // 입력된 날짜
      const now = moment(); // 현재 시간

      if (targetDate.isSame(now, "day")) {
        // 오늘 날짜인 경우
        const diffMinutes = now.diff(targetDate, "minutes"); // 차이 (분)
        const diffHours = now.diff(targetDate, "hours"); // 차이 (시간)

        if (diffMinutes < 1) {
          return "방금 전";
        } else if (diffMinutes < 60) {
          return `${diffMinutes}분 전`;
        } else {
          return `${diffHours}시간 전`;
        }
      } else {
        // 오늘 이전 날짜인 경우
        return targetDate.format(sFmt);
      }
    },

  },
  methods: {
    //날짜 포멧
    eFmtDate(value, delimiter) {
      const d = delimiter || '-';

      if(!value || value.length < 10){
        return value;
      }

      if (value) {
        let tempValue = value;

        if(tempValue.length >= 10){
          tempValue = tempValue.substring(0,10);
        }

        switch(d){
          case '.':
            return moment(tempValue).format(FMT_DOT_yyyyMMdd);
          case '-':
          default:
            return moment(tempValue).format(FMT_DASH_yyyyMMdd);
        }
      }

      return value;
    },
  }
}