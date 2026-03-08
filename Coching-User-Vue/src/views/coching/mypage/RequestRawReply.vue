<template>
  <!--section-->
  <!--section-->
  <section>
        <div class="inner">
          <div class="container">
            <div class="content-wrap">
              <!--content-title-wrap-->
              <MypageMenuBar></MypageMenuBar>

              <div class="section-content content-right board-content">
                <!--real-content-->
                <div class="real-content">
                  <div class="container-inner">
                    <!--board-view-->
                    <div class="board-view">
                      <!--title-wrap-->
                      <div class="title-wrap">
                        <div class="left">
                          <div class="title">{{ requestData.rawName }}</div>
                          <div class="info">
                            <div></div>
                            <div><span>{{ requestData.ptnName }}</span><span>{{ requestData.requestDate }}</span></div>
                          </div>
                        </div>
                        <button @click="onClickReply" type="button" class="btn btn-sm btn-primary">답장하기</button>
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
                                <div class="data">{{ requestData.phone }}</div>
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
                      </div>

                      <div v-if="requestData.rawReplySeq && requestData.rawReplySeq > 0" class="board-view-content">
                        <div class="order-wrap">
                          <div class="total-wrap">
                            <div class="total-num">답변</div>
                          </div>
                          <div class="order-request">
                            {{ requestData.contents }}
                          </div>
                        </div>
                        <!--file-wrap-->
                        <!--첨부파일 없을 경우 file-wrap 다 날려주세요.-->
                        <div class="file-wrap">
                          <div class="total-wrap">
                            <div class="total-num">첨부파일<span>{{ requestData.filelist.length }}</span></div>
                            <a href="javascript:;" class="btn-save">모두저장</a>
                          </div>
                          <div class="file-list">
                            <a v-for="(file, idx) of requestData.filelist" href="javascript:;"
                              :key="idx"
                            >
                              <div class="title-wrap">
                                <span class="title">{{ file.fileName }}</span>
                                <span class="date">{{ eufmtFileSize(file.fileSize) }}</span>
                              </div>
                              <span class="ic-md ic-download"></span>
                            </a>
                          </div>
                        </div>
                      </div>

                    </div>
                  </div>
                </div>

                <!--banner-wrap-->
                <!--배너는 2가지 타입 type-text / type-img-->
                <!-- <Advertise></Advertise> -->

              </div>
            </div>
          </div>
          <AlertModal ref="alertModal"></AlertModal>
        </div>
      </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import { required, email, bizNumOnlyNumber, min, max, password } from '@validations';

import { getRequest } from '@/api/coching/raw/request';
import { getCodes } from '@/api/coching/comm/code';

import Advertise from '@/components/Advertise.vue';
import MypageMenuBar from './MypageMenuBar.vue';

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
};

export default {
	name: 'coching-mypage-requestRaw-detail',
	mixins: [ernsUtils],
	components: {
    Advertise,
    MypageMenuBar,
  },
	computed: {    
  },
	watch: {
	},
	props: {
    rawRequestSeq: {
      type: Number,
      default: 0,
    },
  },
	data() {
		return {
      CODES:{
        CH009:[],
      },
      requestData :{...DEF_REQUEST_INF},
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

				await _vm.loadRequest();

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    async loadCodes(){
      const _vm = this;
     
      const codeRes = await getCodes({grpCode:"CH009", etc5:_vm.$i18n.locale});
        const { resultCode, resultFailMessage, resultData } = codeRes;

        _vm.CODES.CH009 = [...resultData.list];
    },

    async loadRequest() {
			const _vm = this;
			_vm.loading(true);
      try {
          const res = await getRequest({rawRequestSeq: _vm.rawRequestSeq });
          const resData = res.resultData;
          
          //데이터 바인딩
          _vm.requestData = {...resData};
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

    onClickReply(){
      const _vm = this;

      _vm.ermPushPage({name:'coching-mypage-requestRaw-reply',  query:{
        rawRequestSeq: _vm.rawRequestSeq
      }});
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
