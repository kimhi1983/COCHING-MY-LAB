<template>
  <!--쪽지보내기 modal-->
  <div class="modal for-send m-modal-full" ref="request_raw_modal" style="display: none;">
      <div class="layer">
        <div class="layer-content">
          <div class="modal-inner modal-lg">
            <div class="modal-content">
              <div class="modal-header">
                <div @click="onClickClose" class="modal-close"></div>
                <div class="title">{{ $t('') || '원료 자료 및 샘플요청'}}<span class="m-none">{{ $t('') || '쪽지 및 메일 발송'}}</span></div>
              </div>
              <validation-observer tag="div" ref="rawRequestForm" class="modal-content-inner" #default="{ invalid }">
                <!-- invalid 값을 로컬 데이터에 바인딩 -->
                <div v-if="updateInvalidState(invalid)"></div>
                <div class="modal-body">
                  <!--send-->
                  <div class="send-wrap">
                    <!--section-->
                    <div class="send-section">
                      <!--title-->
                      <div class="title">{{ $t('') || '요청원료'}}<span class="total"></span></div>
                      <!--card-->
                      <div class="card-wrap send-card-wrap scroller-x">
                        <div v-for="(item, index) in localData.rawInfo" :key="index" class="item">
                          <!--header-->
                          <div class="header">
                            <div class="title">{{ item.rawName }}</div>
                          </div>
                          <!--body-->
                          <div class="body">
                            <div class="card-table">
                              <table>
                                <colgroup>
                                  <col width="50" />
                                </colgroup>
                                <tbody>
                                  <tr>
                                    <th>타이틀</th>
                                    <td>{{ item.title }}</td>
                                  </tr>
                                  <tr>
                                    <th>공급사</th>
                                    <td>{{ item.supplier }}</td>
                                  </tr>
                                  <tr>
                                    <th>담당자</th>
                                    <td>{{ item.membName }}</td>
                                  </tr>
                                </tbody>
                              </table>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!--section-->
                    <div class="send-section">
                      <!--title-->
                      <div class="title">{{ $t('') || '요청원료'}}<span class="info">{{ $t('') || '중복 선택 가능'}}</span></div>
                      <!--data choice-->
                      <div class="data-choice">
                        <div v-for="(code, idx) of CODES.CH009" :key="idx" 
                          @click="toggleType(code.code)" 
                          :class="['item', { active: localData.requestData.typeList.some(item => item.code === code.code) }]">
                          {{ code.codeName }}
                        </div>
                      </div>
                    </div>
                    <!--section-->
                    <div class="send-section">
                      <validation-observer tag="div" ref="rawRequestForm" class="section-content" #default="{ invalid }">
                        <!--title-->
                        <div class="title">발주처</div>
                        <!--input-->
                        <div class="input-wrap">
                          <div class="input-half">
                            <div class="label-set">
                              <label>회사명</label>
                              <validation-provider #default="{ errors }" tag="div"
                                class="input-set" :name="$t('') || '회사명'" rules="max:50">
                                  <input v-model="localData.requestData.ptnName" 
                                    :disabled="isPartner" type="text" />
                                  <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                              </validation-provider>
                            </div>
                            <div class="label-set">
                              <label>담당자<span class="text-primary">*</span></label>
                              <validation-provider #default="{ errors }" tag="div"
                                class="input-set" :name="$t('') || '담당자'" rules="required">
                                  <input v-model="localData.requestData.membName" 
                                    :disabled="eumLoginUser" type="text" />
                                  <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                              </validation-provider>
                            </div>
                          </div>
                          <div class="input-half">
                            <div class="label-set">
                              <label>연락처<span class="text-primary">*</span></label>
                              <validation-provider #default="{ errors }" tag="div"
                                class="input-set" :name="$t('') || '연락처'" rules="required">
                                  <input v-model="localData.requestData.phone" type="text" />
                                  <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                              </validation-provider>
                            </div>
                            <div class="label-set">
                              <label>이메일<span class="text-primary">*</span></label>
                              <validation-provider #default="{ errors }" tag="div"
                                class="input-set" :name="$t('') || '이메일'" rules="required|email">
                                  <input v-model="localData.requestData.email" type="text" />
                                  <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                              </validation-provider>
                            </div>
                          </div>
                          <div class="label-set">
                            <label>샘플 받을 주소</label>
                            <validation-provider #default="{ errors }" tag="div"
                              class="input-set" :name="$t('') || '주소'" rules="">
                                <input v-model="localData.requestData.address" 
                                  type="text" placeholder="서울시 OO구 OO로"/>
                                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                            </validation-provider>
                          </div>
                          <div class="label-set">
                            <label>요청사항</label>
                            <validation-provider #default="{ errors }" tag="div"
                              class="input-set" :name="$t('') || '요청사항'" rules="">
                                <input v-model="localData.requestData.reqDetail" 
                                  type="text" placeholder="키인으로 입력해주세요"/>
                                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                            </validation-provider>
                          </div>
                        </div>
                      </validation-observer>
                    </div>
                  </div>
                </div>
                <div class="modal-footer">
                  <button @click="onClickClose" type="button" class="btn btn-lg btn-gray bottom-modal-close">취소</button>
                  <button @click="onSubmit()" 
                    :class="{'btn-disabled' : !localData.isRequestValid,'btn-primary' : localData.isRequestValid}"
                    type="button" class="btn btn-lg">전송하기</button>
                </div>
              </validation-observer>
            </div>
          </div>
        </div>
        <AlertModal ref="alertModal"></AlertModal>
      </div>
    </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { getCodes } from '@/api/coching/comm/code';
import { required, email } from '@validations';
import { getMyInfo } from '@/api/coching/member/member';
import { setRequest } from '@/api/coching/raw/request';
import { getSearchRaw } from '@/api/coching/search/search';

const DEF_REQUEST_INFO ={
  rawDetailSeq: null,
  rawName: '',
  ptnSeq: null,
  ptnName: '',
  membName: '',
  phone: '',
  email: '',
  messageType: 'ALL',
  address: '',
  reqDetail: '',
  rawList: '',
  typeList: [],
};

const DEF_RAW_INFO = {
  rawSeq: null,
  rawDetailSeq: null,
  membSeq: null,
  rawName: '',
  title: '',
  supplier: '',
  membName: '',
};

export default {
    name: 'coching-modal-full',
    mixins: [ernsUtils],
    components: {
          },
    props: {
    },
    computed: {
        
    },
    watch: {
      '$i18n.locale' : async function(){
        const _vm = this;
        await _vm.loadCodes();
        _vm.$nextTick(() => _vm.$refs.cntrcDetailForm.validate());
      },
      "localData.isShow" : "onChangeShow"
    },
    data() {
        return {
          localInvalidState: false, //양식폼의 validate 상태
          status: '',
          CODES:{
            CH009:[],
          },
          localData : {
            isShow : false,
            result : undefined,

            isRequestValid: false,

            rawInfo : [],
            requestData:{...DEF_REQUEST_INFO}
          },
          isPartner: false,
        }
    },
  async mounted(){
    const _vm = this;

    await _vm.loadCodes();
    _vm.docReady();
    _vm.fetchData();    

  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    async open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = true;
      ld.rawInfo = [];

      if (Array.isArray(options.rawInfo)) {
        const promises = options.rawInfo.map(option => _vm.loadRawInfo(option));

        try {
          // 모든 통신이 완료될 때까지 기다림
          const results = await Promise.all(promises);

          // 결과를 rawInfo에 추가
          ld.rawInfo = results;

          console.log(ld.rawInfo); // 최종 결과 출력
        } catch (error) {
          console.error('통신 중 오류 발생:', error);
        }
      }else{
        if (typeof options.rawInfo === "object" && options.rawInfo !== null) {
          ld.rawInfo.push(await _vm.loadRawInfo(options.rawInfo));
        }
      }
      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },
   //데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {

        _vm.localData.requestData = {...DEF_REQUEST_INFO};
        _vm.init();

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},
    
    async init(){
      const _vm = this;

      let dataMap = _vm.localData.requestData;

      const myInfoRes = await getMyInfo({});
      const { resultCode, resultFailMessage, resultData } = myInfoRes;
      dataMap.membName = resultData.membName;
      dataMap.phone= resultData.phone;
      dataMap.email = resultData.email;
      
      if(_vm.partnerInfo){
        _vm.isPartner = true;
        dataMap.ptnSeq = _vm.partnerInfo.ptnSeq;
        dataMap.ptnName = _vm.partnerInfo.ptnName;
      }
      
    },
    //원료정보 로드
    async loadRawInfo(value){
      const _vm = this;

      _vm.loading(true);
      try{
        const params = {
          id : (""+(value.rawSeq || "")).trim()
        };

        const rawRes = await getSearchRaw(params);
        const {list, maxScore, total, version} = rawRes.resultData;
        if((total || 0) < 1){
          await _vm.$refs["alertModal"].open({
            title: _vm.$tt('test001') || '원료정보를 조회 할 수 없습니다.'
          });
          return;
        }

        const dataList = _vm.convertSearchResultByVersion(version, list);
        const rawInfo = dataList[0];
        const rawDetail = rawInfo.rawDetailInfo.find(item => item.rawDetailSeq == value.rawDetailSeq) || {};

        const filteredData = Object.keys(DEF_RAW_INFO).reduce((result, key) => {
          result[key] = rawDetail[key] !== undefined ? rawDetail[key] : rawInfo[key] !== undefined ? rawInfo[key] : DEF_REQUEST_INFO[key];
          return result;
        }, {});

        return filteredData;

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }
    },

    async loadCodes(){
      const _vm = this;
     
      const codeRes = await getCodes({grpCode:"CH009", etc5:_vm.$i18n.locale});
        const { resultCode, resultFailMessage, resultData } = codeRes;

        _vm.CODES.CH009 = [...resultData.list];
    },

    onChangeShow(){
      const _vm = this;

      const newVal =_vm.localData.isShow;
      console.log(newVal);
      //console.debug(newVal);
      //console.debug(_vm.$refscoching_confirm_modal);

      if(newVal == true){
        $(_vm.$refs["request_raw_modal"]).fadeIn(300);
        return;
      }

      $(_vm.$refs["request_raw_modal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },

     //양식의 상태값을 확인
     updateInvalidState(invalid) {
      const _vm = this;
      const hasSelectedType = _vm.localData.requestData.typeList.length > 0;
      //
      const isFormInvalid = invalid || !hasSelectedType;

      const isChanged = _vm.localInvalidState !== isFormInvalid;
      _vm.localInvalidState = isFormInvalid;

      if(isChanged){
        //상태값이 변경되면 부모에게 알림
        _vm.localData.isRequestValid = !_vm.localInvalidState;
      }
      return false; // v-if에서 항상 false를 반환하여 실제 DOM에 렌더링되지 않게 함
    },

    async onSubmit(){
      const _vm = this;

      if(_vm.localData.isRequestValid){
        _vm.loading(true);

        try{
          const param = _vm.setParamData();        

          let res = await setRequest(param);
          const { resultCode, resultFailMessage, resultData } = res;

          _vm.loading(false);

          const ret = await _vm.$refs["alertModal"].open({
            title: _vm.$t('') || '원료 요청',
            titleHtml : _vm.$t('') || '요청되었습니다.'
          });

          if(ret){
            const ld = _vm.localData;
            ld.isShow = false;
            ld.result(false);
          }

        } catch(err) {
          _vm.defaultApiErrorHandler(err); //기본에러처리
        } finally {
          _vm.loading(false);
        }
      }
    },

    setParamData(){
      const _vm = this;
    
      _vm.localData.requestData.rawList = [..._vm.localData.rawInfo];

      const param = {
              ..._vm.localData.requestData,
            };

      return param; 
    },

    onClickClose() {
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = false;
      ld.result(false);
    },

    toggleType(code) {
      const _vm = this;
      // typeList에서 code 값이 일치하는 항목을 찾음
      const index = _vm.localData.requestData.typeList.findIndex(
        (item) => item.code === code
      );

      if (index === -1) {
        // code 값이 typeList에 없으면 추가
        _vm.localData.requestData.typeList.push({ rawRequestSeq: null, code });
      } else {
        // code 값이 이미 존재하면 제거
        _vm.localData.requestData.typeList.splice(index, 1);
      }
    },

    docReady(){
      const _vm = this;

      $(".scroller-x").scrollbar();

      // 마우스 휠 이벤트로 가로 스크롤 동작 추가
      $(".scroller-x").on("mousewheel DOMMouseScroll", function (e) {
        var delta = e.originalEvent.wheelDelta || -e.originalEvent.detail;
        var scrollAmount = 30; // 스크롤 이동 거리 설정

        if (delta > 0) {
          // 마우스 휠을 위로 스크롤할 때 (왼쪽으로 이동)
          $(this).scrollLeft($(this).scrollLeft() - scrollAmount);
        } else {
          // 마우스 휠을 아래로 스크롤할 때 (오른쪽으로 이동)
          $(this).scrollLeft($(this).scrollLeft() + scrollAmount);
        }

        // 기본 동작을 막아 세로 스크롤이 작동하지 않게 함
        e.preventDefault();
      });
    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>