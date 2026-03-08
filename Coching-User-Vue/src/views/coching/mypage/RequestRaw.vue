<template>
  <!--section-->
  <!--section-->
  <section>
        <div class="container h-100">
          <div class="content">
            <div class="common-content">
              <!--content-title-wrap-->
              <NotificationMenuBar></NotificationMenuBar>

              <div class="tabs style-box">
                <div v-if="'003' != eumLoginUser.userType" class="tabs-nav">
                  <ul>
                    <li :class="{'active' : 'get' === tabMenu}">
                      <a @click="onClickTabMenu('get')" href="javascript:;">받은 원료자료요청</a>
                    </li>
                    <li :class="{'active' : 'send' === tabMenu}">
                      <a @click="onClickTabMenu('send')" href="javascript:;">보낸 원료자료요청</a>
                    </li>
                    <li v-if="'002' === eumLoginUser.userType" :class="{'active' : 'all' === tabMenu}">
                      <a @click="onClickTabMenu('all')" href="javascript:;">전체 요청</a>
                    </li>
                  </ul>
                </div>
                <!--컨텐츠-->
                <div :class="{'tabs-content board-tabs-content' : '003' != eumLoginUser.userType}">
                  <div class="tabs-panel active">
                    <div class="board-wrap">
                      <!--total-wrap-->
                      <div class="total-wrap">
                      <div class="total-num">{{ $t('') || '총' }}<span>{{ requestData.pi.totalItem }}</span></div>
                      <div class="right">
                          <!--button-->
                      </div>
                      </div>
                      <!--responsive table-->
                      <div class="board-list">
                        <a v-if="requestData.list.length > 0" 
                          @click="onClickRequestDetail(item)"
                          v-for="(item, idx) of requestData.list" :key="idx" href="javascript:;">
                          <!--content-wrap-->
                          <div class="content-wrap">
                            <div class="title-wrap">
                              <div class="title">{{ item.rawName }}</div>
                            </div>

                            <div class="info">{{ item.title }}</div>
                            <div class="date"><span>{{ item.ptnName }}</span><span>{{ item.requestDate | eFmtDateFormat('YY.MM.DD') }}</span></div>
                          </div>
                          <!--state-badge-->
                          <div :class="getCodeProperty(item.status, 'color')" 
                                    class="state-badge">{{ getCodeProperty(item.status, 'codeName') }}</div>
                        </a>
                      <!-- <table>
                          <colgroup>
                            <col width="100" />
                            <col />
                            <col width="300" />
                            <col width="150" />
                            <col width="200" />
                            <col v-show="'all' === tabMenu" width="150" />
                            <col width="150" />
                          </colgroup>
                          <thead>
                          <tr>
                              <th>번호</th>
                              <th class="text-left">원료명</th>
                              <th class="text-left">타이틀</th>
                              <th>유통사</th>
                              <th>요청날짜</th>
                              <th v-show="'all' === tabMenu">담당자</th>
                              <th>진행상태</th>
                          </tr>
                          </thead>
                          <tbody v-if="requestData.list.length > 0">
                            <tr @click="onClickRequestDetail(item)"
                                v-for="(item, idx) of requestData.list" :key="idx" class="link">
                                <td class="num">
                                  {{ idx | eufRowNumberDescForPageInfo(requestData.pi) }}
                                </td>
                                <td class="text-left m-w-100">
                                <div class="title-wrap flex">
                                    <div class="title">{{ item.rawName }}</div>
                                </div>
                                </td>
                                <td class="text-left">
                                <div class="info">{{ item.title }}</div>
                                </td>
                                <td>{{ item.ptnName }}</td>
                                <td>{{ item.requestDate | eFmtDateFormat('YY.MM.DD') }}</td>
                                <td v-show="'all' === tabMenu">{{ item.managerName }}</td>
                                <td class="m-w-100 m-p-t">
                                <div :class="getCodeProperty(item.status, 'color')" 
                                    class="state-badge">{{ getCodeProperty(item.status, 'codeName') }}</div>
                                </td>
                            </tr>
                          </tbody>
                      </table> -->
                      <div v-if="requestData.list.length == 0" class="empty-wrap">요청한 원료가 없습니다.</div>
                      </div>
                      <!--pagenation-->
                      <Pagenation
                      v-model="requestData.pi.curPage"
                      :totalRows="requestData.pi.totalItem"
                      :perPage="requestData.pi.perPage"
                      @input="onChangePage"
                      ></Pagenation>
                    </div>
                  </div>
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
import { getCodes } from '@/api/coching/comm/code';
import { getRequestList } from '@/api/coching/raw/request';
 
import Pagenation from '@/components/Pagenation.vue';
import NotificationMenuBar from './NotificationMenuBar.vue';

const DEF_REQUESTT = {
	sc : {
		searchField : '',
    searchText : '',
    compSeq: null,
    useYn: 'Y'
	},
	pi:{
		curPage : 1,
    totalItem : 0,
    perPage : 10
		//perPage : 1 //Test
	},
	summary : []
};

const DEF_REQUEST_INF = {
    rawSeq: null,
    rawRequestSeq: null,
    rawName: '',
    title: '',
    rgtDttm: '',
};

const DEF_COLOR_MAP = {
  "001": "orange",
  "002": "green",
  "003": "blue",
  "004": "gray"
};

export default {
	name: 'coching-mypage-requestRaw',
	mixins: [ernsUtils],
	components: {
    NotificationMenuBar,
    Pagenation,
},
	computed: {    
  },
	watch: {
    '$i18n.locale' : async function(){
      const _vm = this;
      await _vm.loadCodes();
      _vm.$nextTick(() => {});
    },
	},
	props: {
    
  },
	data() {
		return {
      tabMenu: "send",
      CODES: {
        CH010: [],
        },
      requestData :{
          list: [],
          pi: {...DEF_REQUESTT.pi},
          sc: {...DEF_REQUESTT.sc},
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

        if('003' != _vm.eumLoginUser.userType){
          _vm.tabMenu = 'get';
        }

        await _vm.loadList(1);

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    async loadCodes() {
        const _vm = this;

        const codeRes = await getCodes({ grpCode: "CH010", etc5: _vm.$i18n.locale });
        const { resultCode, resultFailMessage, resultData } = codeRes;

        _vm.CODES.CH010 = resultData.list.map(item => ({
            ...item,
            color: DEF_COLOR_MAP[item.code] || "orange"
        }));
    },

    getCodeProperty(status, property) {
        const matchingCode = this.CODES.CH010.find(code => code.etc1 === status);
        return matchingCode ? matchingCode[property] : property === 'color' ? "orange" : "요청중";
    },

    onClickTabMenu(menu){
      const _vm = this;

      _vm.tabMenu = menu;

      _vm.loadList(1);
    },

    async loadList(pageNo) {
        const _vm = this;
        const dataMap = _vm.requestData, pInfo = _vm.requestData.pi, searchOp = _vm.requestData.sc;
        pInfo.curPage = pageNo || 1;

        _vm.loading(true);
        try {
            const params = _vm.getListSearchParam();
            const res = await getRequestList(params);
            const resData = res.resultData;

            const pageInfo = resData.pageInfo;
            dataMap.pi = {
                ...dataMap.pi,
                curPage: pageInfo.currentPage,
                totalItem: pageInfo.totalItem,
                perPage: pageInfo.pageItemSize
            }

            //데이터 바인딩
            dataMap.list = resData.list;
        } catch (err) {
            _vm.defaultApiErrorHandler(err); //기본에러처리
        } finally {
            _vm.loading(false);
        }
    },

      /* 검색 조건 가져오기 */
    getListSearchParam() {
        const _vm = this;
        const pInfo = _vm.requestData.pi, searchOp = _vm.requestData.sc;

        const retParam = {

            page: pInfo.curPage,
            rowSize: pInfo.perPage
        };

        if('send' === _vm.tabMenu){
          retParam.membSeq = _vm.eumLoginUser.userSeq;
        }else if('all' === _vm.tabMenu){
          retParam.ptnSeq = _vm.partnerInfo.ptnSeq;
        }else{
          retParam.managerSeq = _vm.eumLoginUser.userSeq;
        }

        return retParam;
    },

      /* 페이지 변경 */
    onChangePage(pageNo) {
        const _vm = this;

        _vm.loadList(pageNo);
    },

    onClickRequestDetail(item){
      const _vm = this;

      _vm.ermPushPage({name:'coching-mypage-requestRaw-detail',  query:{
        rawRequestSeq: item.rawRequestSeq
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
#coching-mypage-requestRaw {

}
</style>
