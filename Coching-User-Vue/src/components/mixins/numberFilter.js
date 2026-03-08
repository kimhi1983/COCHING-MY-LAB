import format from 'format-number';
import { i18n } from '@/utils/i18n';


const fncFmtCurrency = function(value, prefix, suffix){
  const s = suffix || '';
  const cFormat = format({prefix: prefix, suffix: s});
  return cFormat(value, {noSeparator: false});
};

const fncFmtNumber = function(value, round, truncate, prefix, suffix){
  const s = suffix || '';
  const p = prefix || '';
  const op = {
    prefix: p, suffix: s
  };
  if(round != null){
    op.round = round;
  }
  if(truncate != null){
    op.truncate = truncate;
  }  
  const cFormat = format(op);
  return cFormat(value, {noSeparator: false});
};

//한글 금액
const num2han = function(paramValue) {
  let value = "" + (paramValue || '');
  if(!value)
    return '';     

  value = value.replace(/,/g, "");
  value = parseInt((value + '').replace(/[^0-9]/g, ''), 10) + '';  // 숫자/문자/돈 을 숫자만 있는 문자열로 변환
  if(value == '0')
    return '영';
  var number = ['영', '일', '이', '삼', '사', '오', '육', '칠', '팔', '구'];
  var unit = ['', '만', '억', '조'];
  var smallUnit = ['천', '백', '십', ''];
  var result = [];  //변환된 값을 저장할 배열
  var unitCnt = Math.ceil(value.length / 4);  //단위 갯수. 숫자 10000은 일단위와 만단위 2개이다.
  value = value.padStart(unitCnt * 4, '0')  //4자리 값이 되도록 0을 채운다
  var regexp = /[\w\W]{4}/g;  //4자리 단위로 숫자 분리
  var array = value.match(regexp);
  //낮은 자릿수에서 높은 자릿수 순으로 값을 만든다(그래야 자릿수 계산이 편하다)
  for(var i = array.length - 1, unitCnt = 0; i >= 0; i--, unitCnt++) {
    var hanValue = _makeHan(array[i]);  //한글로 변환된 숫자
    if(hanValue == '')  //값이 없을땐 해당 단위의 값이 모두 0이란 뜻. 
      continue;
    result.unshift(hanValue + unit[unitCnt]);  //unshift는 항상 배열의 앞에 넣는다.
  }
  //여기로 들어오는 값은 무조건 네자리이다. 1234 -> 일천이백삼십사
  function _makeHan(text) {
    var str = '';
    for(var i = 0; i < text.length; i++) {
      var value = text[i];
      if(value == '0')  //0은 읽지 않는다
        continue;
      str += number[value] + smallUnit[i];
    }
    return str;
  }
  
  if(result.join('').includes('undefined')){
    return '';
  }
  return '금 ' + result.join('') + '원 정';
}

const num2Eng = function(num) {
  let value = "" + (num || '');
  if(!value)
    return '';     

  value = value.replace(/,/g, "");
  value = parseInt((value + '').replace(/[^0-9]/g, ''), 10) + '';  // 숫자/문자/돈 을 숫자만 있는 문자열로 변환

  const belowTwenty = [
    'zero', 'one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine', 'ten',
    'eleven', 'twelve', 'thirteen', 'fourteen', 'fifteen', 'sixteen', 'seventeen', 'eighteen', 'nineteen'
  ];
  const tens = [
    '', '', 'twenty', 'thirty', 'forty', 'fifty', 'sixty', 'seventy', 'eighty', 'ninety'
  ];
  const units = ['', 'thousand', 'million', 'billion', 'trillion'];

  if (value === 0) return 'zero';

  function convertToWords(n) {
    if (n < 20) return belowTwenty[n];
    if (n < 100) return tens[Math.floor(n / 10)] + (n % 10 ? '-' + belowTwenty[n % 10] : '');
    if (n < 1000) return belowTwenty[Math.floor(n / 100)] + ' hundred' + (n % 100 ? ' ' + convertToWords(n % 100) : '');
    return '';
  }

  let result = [];
  let unitIndex = 0;

  while (value > 0) {
    const chunk = value % 1000;
    if (chunk > 0) {
      result.unshift(convertToWords(chunk) + (units[unitIndex] ? ' ' + units[unitIndex] : ''));
    }
    value = Math.floor(value / 1000);
    unitIndex++;
  }

  return result.join(' ').trim() + ' won';
}

/**
 *  예제
 *  {{12312312 | eFmtWon}}<br/>
    {{12312312.12 | eFmtWon}}<br/>
    {{12312312.12 | eFmtDolar}}<br/>
    {{12312312.12 | eFmtNum}} <br/> 
    {{12312312.167 | eFmtNumT(2)}} <br/> 
    {{12312312.168 | eFmtNumR(2)}} <br/> 
 */
export default {
  data() {
    return {

    }
  },
  filters: {
    //숫자
    eFmtNum(value, round){
      const r = round || 0;
      return fncFmtNumber(value, r, null);
    },

    //소수 반올림
    eFmtNumR(value, round, prefix, suffix){
      const r = round || 0;
      return fncFmtNumber(value, r, null, prefix, suffix);
    },

    //소수 버림
    eFmtNumT(value, truncate, prefix, suffix){
      const t = truncate || 0;
      return fncFmtNumber(value, null, t, prefix, suffix);
    },

    //소수 버리고 무조건 올림
    eFmtNumC(value, prefix, suffix){
      const transValue = Math.ceil(value);      
      return fncFmtNumber(transValue, null, null, prefix, suffix);
    },

    //통화
    eFmtCurrency(value, prefix, suffix){
      return fncFmtCurrency(value, prefix, suffix);
    },

    //통화 짧게
    eFmtShortWon(value, prefix){
      let round = 2;
      let cVal = value;
      let suffixStr = "원";
      if(value > 1000000000000){
        cVal = value / 1000000000000; 
        suffixStr = "조";
      }else if (value > 10000000000){
        cVal = value / 100000000; 
        suffixStr = "억";
        round = 0; //소수제거
      }else if (value > 100000000){
        cVal = value / 100000000; 
        suffixStr = "억";
      }else if (value > 1000000){
        cVal = value / 10000;
        suffixStr = "만";
        round = 0; //소수제거
      }else if (value > 10000){
        cVal = value / 10000; 
        suffixStr = "만";
      }

      return fncFmtNumber(cVal, round, null, prefix, suffixStr);
    },

    //원화
    eFmtWon(value, suffix) {
      return fncFmtCurrency(value, '₩', suffix);
    },

    //달러
    eFmtDolar(value, suffix) {
      return fncFmtCurrency(value, '$', suffix);
    },
    
    //리뷰 별점 평균값 (5단위절사)
    eFmtReview(val) {
      if(!val){
        return 0;
      }

      return Math.round(val.toFixed(1)) * 10;
    },
    
    //리뷰 별점 평균값 (소수점 아래 1자리 출력)
    eFmtReviewPoint(val) {
      if(!val){
        return "0.0";
      }

      return val.toFixed(1);
    },

    //앞자리에 0을 붙인다
    eFmtPadStart(value, sPos) {
      const strPos = sPos || 3;
     return String(value).padStart(strPos, '0');
    },
    
    num2localized(val){
      if (!val) return '';
      if (i18n.locale === 'ko') {
        return num2han(val);
      } else {
        return num2Eng(val);
      }
    },
    
  },
  methods: {
    
    
  }
}