import datetimeFilter from './datetimeFilter';
import numberFilter from './numberFilter';
import pagination from './pagination';
import formatter from './formatter';
import loading from './loading';
import routerHistory from './routerHistory';
import dropzone from './dropzone';
import { isNumber } from '@vueuse/shared';

import { getUserData } from '@/auth/utils';

const base = process.env.VUE_APP_API_BASE_URL;

export default {
  mixins: [routerHistory, loading, datetimeFilter, numberFilter, pagination, formatter, dropzone],
  data() {
    return {

    }
  },
  computed: {    
    loginUserData(){
      return getUserData();
    }    
  },
  /*
  beforeRouteLeave : function(to, from, next){
    //페이지 이동전 검색 param 저장용
    try{
      const routerName = from.name;
      if(from.matched.length > 0){
        const coms = from.matched[0].components.default;
        const methods = coms.methods;
        if(methods && methods.hasOwnProperty('onBeforeRouter')){
          const fncOnBeforeRouter = methods['onBeforeRouter'];
          fncOnBeforeRouter(routerName);
        }
      }
    }catch(err){
      console.error(err);
    }
  
    return next();
  },
  */
  filters :{
    //사용여부 필터
    useYnName(val){
      return val =="Y" ? "사용" : val =="N" ? "미사용" : "-";
    },

    //공통코드에서 코드에 해당하는 코드명 출력
    eufGetCodeName(val, codeList, pTraceKey){
      let ret = "-";

      if(!codeList || codeList.length < 1 || !val ){return ret;}

      let traceKey = pTraceKey || 'value';
      //console.debug(traceKey);
      const cd = codeList.find((item)=>{
        return item[traceKey] == val;
      });

      if(cd){
        return cd.label;
      }

      return "-";
    },    

    //데이터가 blank 일때 '-' 로 처리
    eufBlankToDash(val){
      if(!val){
        return "-";
      }

      return val;
    },

 
  },
  methods: {
    //공통코드 목록 => [{label:'text', value:'data'}] 로 변경
    eumConvertToVueSelectOption(rawCommonCodes, pTraceKey){
      //console.log(rawCommonCodes);
      let traceKey = pTraceKey || 'etc1';
      return rawCommonCodes.map(item=>{
        return {label:item.codeName, value:item[traceKey]};
      });
    },

    //Vue-Select 등에서 선택된 값의 value 값을 String 으로 리턴
    eumGetVSelectValue(selectModel, propName) {
      const pName = propName || 'value';
      if (!selectModel) { return null; }

      if (typeof selectModel === 'object') {
        if (selectModel.hasOwnProperty(pName)) {
          return selectModel[pName];
        }
      }
      return null;
    },

    //공통코드 List에서 코드를 찾아 Object리턴
    eumGetCodeObject(codeVal, codes){
      if(codeVal && codes && codes.length > 0){
        return codes.find(item=>{
          return item.value == codeVal;
        });
      }

      return null;
    },

    //오류 alert
    async alertError(errorMsg, title){
      const _vm = this;

      let eMsg = '알 수 없는 오류가 발생하였습니다.';
      if(errorMsg instanceof Error){
        const eRes = errorMsg.response;
        if(eRes && eRes.data && eRes.data.returnMessage){
          eMsg = eRes.data.returnMessage;
        }else{
          eMsg = errorMsg.message;
        } 
      }else{
        eMsg = errorMsg || eMsg;
      }

      return await _vm.$swal({
        icon: 'error',
        title : title,
        text : eMsg,         
        customClass: {
          confirmButton: 'btn btn-primary',
        },
        buttonsStyling: false,
      });
    },

    //성공 alert (자동 꺼짐)
    async alertSuccess(message, title, timer){
      const _vm = this;
      return await _vm.$swal({
        icon: 'success',
        title: title,
        text: message, 
        showConfirmButton: false,
        timer: timer || 1500,
        customClass: {
          confirmButton: 'btn btn-primary',
        },
        buttonsStyling: false,
      });
    },

    eumCamel2under(str){
      if(!str){
        return str;
      }

      return str.replace(/([A-Z])/g, function(arg){
        return "_"+arg.toLowerCase();
      }).toUpperCase();
    },

    eumUnder2camel(str){
      if(!str){
        return str;
      }
      
      return str.toLowerCase().replace(/(\_[a-z])/g, function(arg){
        return arg.toUpperCase().replace('_','');
      });
    },

    eumFileDownPath(fileId){
      const base = process.env.VUE_APP_API_BASE_URL;
      const link = `${base}/file/download.do?fId=${fileId}`;
      return link;
    },

    //첨부파일 이미지 로드
    eumFileImagePath(fileId, pThumb){
      const thumb = pThumb || "1";
      const link = `${base}/file/image.do?thumb=${thumb}&fId=${fileId}`;
      return link;
    },

    //첨부파일 이미지 로드
    eumFileImagePathWithSize(fileId, pThumb, size){
      const thumb = pThumb || "1";
      let link = `${base}/file/image.do?thumb=${thumb}&fId=${fileId}`;
      if(thumb == "1" && size){
        link += '&size='+size;
      }
      return link;
    },

    async eumFileImageThumb(fileId, size){
      const _vm = this;

      try{
        const downUrl = _vm.eumFileImagePathWithSize(fileId, '1', size);
        const res = await _vm.$http.get(downUrl, { 
          responseType: 'blob'
        });

        const lUrl = window.URL.createObjectURL(new Blob([res.data]));
        return lUrl;       
        
      }catch(err){
        console.error(err);        
      }
    },

    /**
     * 첨부파일 다운로드
     * @param {*} {param0} {downUrl:첨부파일 다운로드 URL, fileName:저장할 파일명}
     */
    async eumFileDownload({ downUrl, fileName }){
      const _vm = this;

      try{
        const res = await _vm.$http.get(downUrl, { 
          responseType: 'blob'
        });

        const lUrl = window.URL.createObjectURL(new Blob([res.data]));
        //console.debug(res);
        const link = document.createElement('a');
        const contentDisposition = res.headers['content-disposition']; // 파일 이름
        let tFileName = 'unknown';
        if (contentDisposition) {
          const [ fileNameMatch ] = contentDisposition.split(';').filter(str => str.includes('filename'));
          if (fileNameMatch){
            [ , tFileName ] = fileNameMatch.split('=');
            tFileName = tFileName.replace(/"/gi, '');
          }
        }else{
          //console.debug(fileName);
          tFileName = fileName || 'unknown';
        }
        
        link.href = lUrl;
        link.setAttribute('download', `${tFileName}`);
        link.style.cssText = 'display:none';
        document.body.appendChild(link);
        link.click();
        link.remove();

      }catch(err){
        console.error(err);
        const { config, response } = err;
        //console.debug(config);
        //console.debug(response);
        if(response && response.status === 404){
          _vm.alertError('파일을 찾을 수 없습니다.');
        }else{
          _vm.alertError('알 수 없는 오류가 발생했습니다.');
        }
        
      }
    },

    getDefaultYearOptions(isNow){
      let yearOptions = [];
      const d = new Date();

      const startYear = isNow ? d.getFullYear() : d.getFullYear()+1;

      for(let ii=startYear; ii >=d.getFullYear()-3; ii--){
        yearOptions.push({label:ii+"년", value:ii});
      }

      // for(let ii=d.getFullYear()-3; ii<=d.getFullYear()+1; ii++){
      //   yearOptions.push({label:ii+"년", value:ii});
      // }

      return yearOptions;
    },

    getDefaultMonthOptions(){
      let monthOptions = [];

      for(let ii=1; ii<=12; ii++){
        monthOptions.push({label:ii+"월", value:ii});
      }

      return monthOptions;
    },

    eumReplaceBr(text){
      if(!text){
        return "";
      }

      return text.replace(/(?:\r\n|\r|\n)/g, '<br />');
    },

    eumServiceListJoin(serviceList, joinChar){
      if(!serviceList || serviceList.length == 0){
        return "-"        
      }

      var strArr = [];
      for(let key in serviceList){
        strArr.push(serviceList[key].serviceName);
      }

      if(joinChar){
        return strArr.join(joinChar);
      }

      return strArr.join("<br/>");
    },


    /**
     * 첨부파일 다운로드
     */
    eumFileDownloadResponse(res, fileName){
      const _vm = this;

      try{
        const lUrl = window.URL.createObjectURL(new Blob([res.data]));
        //console.debug(res);
        const link = document.createElement('a');
        const contentDisposition = res.headers['content-disposition']; // 파일 이름
        let tFileName = 'excelData.xls' || fileName;
        if (contentDisposition) {
          const [ fileNameMatch ] = contentDisposition.split(';').filter(str => str.includes('filename'));
          if (fileNameMatch){
            [ , tFileName ] = fileNameMatch.split('=');
            tFileName = tFileName.replace(/"/gi, '');
          }

          if(fileName){
            tFileName = fileName;
          }
        }else{
          //console.debug(fileName);
          tFileName = fileName || 'excelData.xls';
        }
        
        link.href = lUrl;
        link.setAttribute('download', `${tFileName}`);
        link.style.cssText = 'display:none';
        document.body.appendChild(link);
        link.click();
        link.remove();

      }catch(err){
        console.error(err);
        const { config, response } = err;
        //console.debug(config);
        //console.debug(response);
        if(response && response.status === 404){
          _vm.alertError('파일을 찾을 수 없습니다.');
        }else{
          _vm.alertError('알 수 없는 오류가 발생했습니다.');
        }
        
      }
    },

    eumLocationUrl(refUrl) {
      const _vm = this;

      if(!refUrl) {
        return refUrl;
      }
      
      // 외부
      if(refUrl.indexOf("http:") >= 0 || refUrl.indexOf("https:") >= 0){
        window.open(refUrl, '_blank');
        return;
      }

      // 내부
      const routerData = _vm.getUrlRouterData(refUrl);
      _vm.ermPushPage(routerData);
    },

    getUrlRouterData(search) {
      const hashes = search.slice(search.indexOf('?') + 1).split('&')
      const params = {}
      hashes.map(hash => {
          const [key, val] = hash.split('=')
          params[key] = decodeURIComponent(val)
      })
      const path = search.split('?')[0];
      
      const routerData = {
        path: path,
        query: params
      };

      return routerData;
    },
    
    eumGetBaseUrl() {
      const link = `${base}`;
      return link;
    },

    getSelectDayOptions() {
      const dayList = [];
      
      for(let ii = 1; ii < 32; ii++) {
        const dayObj = {};
        dayObj.label = ii + "일";
        dayObj.value = ii;
        dayList.push(dayObj);
      }
      
      return dayList;
    },

    getSelectDayOfWeekOptions() {
      const dayOfWeekList = [];
      
      const dayOfWeek = ["일", "월", "화", "수", "목", "금", "토"];
      
      let ii = 0;
      for(const day of dayOfWeek) {
        ii++;
        const dayObj = {};
        dayObj.label = day;
        dayObj.value = ii;
        dayOfWeekList.push(dayObj);
      }
      
      return dayOfWeekList;
    },

    eumConvertStringArrayToString(c_arr){
      if(c_arr instanceof Array){
        return c_arr.join(", ");
      }

      return c_arr;
    },
  }
}