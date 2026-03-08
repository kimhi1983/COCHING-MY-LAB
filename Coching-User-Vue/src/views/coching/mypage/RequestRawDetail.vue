<template>
  <!--section-->
  <!--section-->
  <section>
        <div class="container h-100">
          <div class="content">
            <div class="common-content">
              <!--content-title-wrap-->
              <NotificationMenuBar></NotificationMenuBar>

                <!--board-view-->
                <div class="board-view">
                  <!--title-wrap-->
                  <div class="title-wrap">
                    <div class="left">
                      <div class="title">{{ requestData.rawName }}</div>
                      <div class="info">
                        <div>{{ '원료 요청' }}</div>
                        <div><span>{{ requestData.ptnName }}</span><span>{{ requestData.requestDate }}</span></div>
                      </div>
                    </div>
                    <div v-if="eumLoginUser.userSeq === requestData.membSeq" class="right">
                      <!--button-->
                      <button v-if="requestData.status === '001' || requestData.status === '002'" 
                        @click="onClickUpdateStatus('004')"
                        type="button" class="btn btn-sm btn-primary-outline m-w-100">이메일로 전달</button>
                    </div>
                  </div>
                  <!--board-view-content-->
                  <div class="board-view-content">
                    <!--발주처-->
                    <div class="order-wrap">
                      <div class="total-wrap">
                        <div class="total-num">발주처</div>
                      </div>
                      <div class="order-info-wrap">
                        <div class="title">{{ requestData.ptnName }}</div>
                        <div class="order-info">
                          <div class="item">
                            <div class="title-wrap">
                              <div class="ic-md ic-user-md"></div>
                              <div class="label">담당자</div>
                            </div>
                            <div class="data">{{ requestData.membName }}</div>
                          </div>
                          <div class="item">
                            <div class="title-wrap">
                              <div class="ic-md ic-mail-md"></div>
                              <div class="label">이메일</div>
                            </div>
                            <div class="data">{{ requestData.email }}</div>
                          </div>
                          <div class="item">
                            <div class="title-wrap">
                              <div class="ic-md ic-tel-md"></div>
                              <div class="label">연락처</div>
                            </div>
                            <div class="data">{{ requestData.phone | eufmtPhoneDash}}</div>
                          </div>
                          <div class="item">
                            <div class="title-wrap">
                              <div class="ic-md ic-pin-md"></div>
                              <div class="label">샘플받을 주소</div>
                            </div>
                            <div class="data">{{ requestData.address }}</div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!--요청원료-->
                    <div class="order-wrap">
                      <div class="total-wrap">
                        <div class="total-num">요청원료<span>1</span></div>
                      </div>
                      <!--card-->
                      <div class="card-wrap send-card-wrap scroller-x">
                        <div class="item">
                          <!--header-->
                          <div class="header">
                            <div class="title">{{ requestData.rawName }}</div>
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
                                    <th>제조사</th>
                                    <td>{{ requestData.prodCompany }}</td>
                                  </tr>
                                  <tr>
                                    <th>유통사</th>
                                    <td>{{ requestData.supplier }}</td>
                                  </tr>
                                </tbody>
                              </table>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!--요청자료-->
                    <div class="order-wrap">
                      <div class="total-wrap">
                        <div class="total-num">요청자료</div>
                      </div>
                      <div class="data-choice">
                        <div v-for="(code, idx) of CODES.CH009" :key="idx" 
                          @click="toggleType(code.code)" 
                          :class="['item', { active: requestData.typelist.some(item => item.code === code.code) }]">
                          {{ code.codeName }}
                        </div>
                      </div>
                    </div>
                    <!--요청사항-->
                    <div class="order-wrap">
                      <div class="total-wrap">
                        <div class="total-num">요청사항</div>
                      </div>
                      <div class="order-request">{{ requestData.reqDetail }}</div>
                    </div>

                    <!--요청 자료 전달 내역-->
                    <ReplyComment
                      :rawRequestSeq="requestData.rawRequestSeq"
                      :requestData="requestData"></ReplyComment>
                  </div>
                </div>
            </div>
          </div>
          <AlertModal ref="alertModal"></AlertModal>
        </div>
      </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import moment from 'moment';
import { isUserLoggedIn } from '@/auth/utils';

import { getRequest, updateStatus, getReply, updateReplyDelYn, setReply } from '@/api/coching/raw/request';
import { getCodes } from '@/api/coching/comm/code';

import Advertise from '@/components/Advertise.vue';
import NotificationMenuBar from './NotificationMenuBar.vue';
import SimpleDatePicker from '@/components/SimpleDatePicker.vue';
import ReplyComment from '@/components/request/ReplyComment.vue';

const DEF_REQUEST_INF ={
  rawRequestSeq: null,
  rawSeq: null,
  rawDetailSeq: null,
  rawName: '',
  prodCompany: '',
  supplier: '',
  membName: '',
  phone: '',
  email: '',
  address: '',
  reqDetail: '',
  typelist: [],
  replylist: [],
};

const DEF_REPLY_INFO ={
  rawReplySeq: null,
  rawRequestSeq: null,
  dispatchCode: '',
  dispatchDate: '',
  datePickerOption : {
    dateFormat: "yy-mm-dd",
    minDate : moment().add(0, 'days').toDate()
  },
  contents: '',
  filelist: [],
  delfilelist: [],
  rawName: '',
  ptnName: '',
  membSeq: null,
};

const DEF_FILE ={
  fileId: null,
  fileName: '',
  file: null,
};

export default {
	name: 'coching-mypage-requestRaw-detail',
	mixins: [ernsUtils],
	components: {
    Advertise,
    NotificationMenuBar,
    SimpleDatePicker,
    ReplyComment,
  },
	computed: {    
  },
	watch: {
    "$route.query.rawRequestSeq"(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.reloadPage();
      }
    }
	},
	props: {
    rawRequestSeq: {
      type: Number,
      default: 0,
    },
  },
	data() {
		return {
      isLoggedIn : false,
      CODES:{
        CH009:[],
      },
      requestData :{...DEF_REQUEST_INF},
      localData: {
        isDragging: false,
        replyData:{...DEF_REPLY_INFO},
      },
		}
	},
	async mounted() {
		const _vm = this;

    await _vm.loadCodes();
		_vm.docReady();
		_vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {

		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {

        _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;

				await _vm.loadRequest();

        //상세 클릭 시 자료 확인으로 업데이트
        if(_vm.eumLoginUser.userSeq === _vm.requestData.membSeq && _vm.requestData.status === '001'){

          const params = {
            rawRequestSeq: _vm.rawRequestSeq,
            status: '002',
            rawName: _vm.requestData.rawName,
            ptnName: _vm.partnerInfo.ptnName,
            rgtr: _vm.requestData.requestRgtr
          };
          await updateStatus(params);
        }

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    reloadPage() {
      this.$forceUpdate(); // 강제로 Vue 컴포넌트 업데이트
      this.fetchData(); // API 호출 등 데이터 다시 로드
    },

    async loadCodes(){
      const _vm = this;
     
      const codeRes = await getCodes({grpCode:"CH009", etc5:_vm.$i18n.locale});
      const { resultCode, resultFailMessage, resultData } = codeRes;

      _vm.CODES.CH009 = [...resultData.list];

      const codeRes011 = await getCodes({grpCode:"CH011", etc5:_vm.$i18n.locale});
      _vm.CODES.CH011 = [...codeRes011.resultData.list];
    },

    async loadRequest() {
			const _vm = this;
			_vm.loading(true);
      try {
          const params = _vm.setRequestParams();
          const res = await getRequest(params);
          const resData = res.resultData;
          
          //데이터 바인딩
          if(resData.membSeq == _vm.eumLoginUser.userSeq || resData.requestRgtr == _vm.eumLoginUser.userSeq
            || (_vm.eumLoginUser.userType == '002' && resData.ptnSeq == _vm.partnerInfo.ptnSeq)
            || (_vm.eumLoginUser.userType == '002' && resData.managerPtnSeq == _vm.partnerInfo.ptnSeq)
          ){
            _vm.requestData = {...resData};
          } else{
            _vm.ermPushPage({ name: 'coching-mypage-requestRaw' });
          }
          
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

    setRequestParams(){
      const _vm = this;

      let param = {
        rawRequestSeq: _vm.rawRequestSeq
      };

      return param;
    },

    //메일회신으로 상태 업데이트
    async onClickUpdateStatus(status){
      const _vm = this;

      _vm.loading(true);
      try {
        const params = {
            rawRequestSeq: _vm.rawRequestSeq,
            status: status,
            rawName: _vm.requestData.rawName,
            ptnName: _vm.partnerInfo.ptnName,
            rgtr: _vm.requestData.requestRgtr
          };
          const res = await updateStatus(params);
          
          _vm.loading(false);
          const ret = await _vm.$refs["alertModal"].open({
            title: _vm.$t('mypage.alert.modify001') || '요청 상태 변경',
            titleHtml : _vm.$tt('test001') || '상태가 변경되었습니다.'
          });
          
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },
    

		docReady() {
			const _vm = this;

		},
	}
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
#coching-mypage-requestRaw-detail {

}
</style>
