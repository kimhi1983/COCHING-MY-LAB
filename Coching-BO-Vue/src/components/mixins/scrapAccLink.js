import APP_CONFIG from '@/config';

import moment from 'moment';

export default {
  data() {
    return {

    }
  },
  filters: {
    /* 인증방식 */
    fmtAccLinkType(rowItem){
      if(!rowItem){return '-';}

      const ui = rowItem.userId || '';
      const orgCd = rowItem.orgCd || '';
      const joinYn = rowItem.joinYn || 'N';

      switch(orgCd){
        case 'scd':
          if('scd' == orgCd && 'N' == joinYn){
            return "-"; //미연동
          }
          return '아이디';

        case 'mall':
        case 'dlvy':
          return '아이디';

        case 'hometax':
        case 'bank':
        case 'card':
          return ui ? `아이디 (${ui})` : '인증서';
        
        default :
          return '-';
      }
    },
    /* 인증정보 */
    fmtAccLinkInfo(rowItem){
      if(!rowItem){return '-';}

      const ui = rowItem.userId || '';
      const orgCd = rowItem.orgCd || '';
      const joinYn = rowItem.joinYn || 'N';

      switch(orgCd){
        case 'scd':
          if('scd' == orgCd && 'N' == joinYn){
            return "-"; //미연동
          }
          return ui ? ui : '-';

        case 'hometax':
        case 'mall':
        case 'dlvy':
          return ui ? ui : '-';

        case 'bank':
        case 'card':        
        default :
          return '-';
      }      
    },
    /* 연동상태 */
    fmtAccLinkStatusCode(rowItem){
      if(!rowItem){return '-';}

      const orgCd = rowItem.orgCd || '';
      if(!orgCd){
        return "-";
      }

      const joinYn = rowItem.joinYn || 'N';
      const CONST_TODAY_ZERO = moment(moment().format('YYYY-MM-DD 00:00:00'));
      switch(orgCd){
        case 'scd':
          if('N' == joinYn){
            return "N"; //미연동
          }

          if('F' == joinYn){ 
            return "F"; //비밀번호 오류
          }
  
          if('Y' == joinYn && 
             moment(rowItem.rgtDttm).diff(CONST_TODAY_ZERO) >= 0
          ){
            return "P"; //준비중
          }
          break;
        case 'hometax':
          if(moment(rowItem.rgtDttm).diff(CONST_TODAY_ZERO) >= 0){
            return "P"; //준비중
          }
          break;
        case 'mall':
        case 'dlvy':
        case 'bank':
        case 'card':        
        default :
      } 

      const isTodayLog = rowItem.scrapLogIsToday || 'N';    //금일 로그인지
      const scrapLogStatus = rowItem.scrapLogStatus || 'E'; //로그상 스크래핑 상태
      const statusCode = isTodayLog == "Y" ? 
        (scrapLogStatus || rowItem.lastAccStatus || 'E')    //금일로그에 따라 상태 처리
        : (rowItem.lastAccStatus || 'E');                   //최종스크래핑 상태를 기본으로 처리

      return statusCode;
    },

    /* 연동상태명*/
    fmtAccLinkStatus(statusCode){
      switch(statusCode){
        case "R": return "배치대기";
        case "P": return "준비중";
        case "D": return "정상";
        case "N": return "미연동";
        case "F": return "비밀번호 불일치";
        case "E":
        default :
          return "연동오류";
      }
    },

    /* 연동상태 class */
    fmtAccLinkStatusClass(statusCode){
      switch(statusCode){
        case "R": return "text-muted";    //배치대기
        case "P": return "text-warning";  //준비중
        case "D": return "text-success";  //정상
        case "N": return "text-muted";    //미연동

        case "F": //비밀번호오류
        case "E": //오류
        default :
          return "text-danger";
      }
    },  

    /* 연동오류 메시지*/
    fmtAccLinkErrorMsg(statusCode, rowItem){
      if(!rowItem){return '';}

      const scrapErrorPrefix = rowItem.scrapErrorPrefix || '';
      const scrapErrorMsg = rowItem.scrapErrorMsg || '';
      const orgCd = rowItem.orgCd || '';
      
      switch(statusCode){
        case "R": /*배치대기*/
          return "";
        case "P": /*준비중*/
          if('scd' == orgCd){
            return "매출·정산은 내일부터 확인"; //준비중
          }
          if('hometax' == orgCd){
            return "내일부터 확인 가능!"; //준비중
          }
          return "";
        case "D": /*정상*/
          return "";
        case "N": /*미연동*/
          return "";        
        case "F": 
          return "비밀번호가 일치하지 않습니다.";

        case "E":
        default :
          return `${scrapErrorPrefix} ${scrapErrorMsg}`.trim();
      }
    }
  },
  computed: {
    
  },
  methods: {
    
  }
};