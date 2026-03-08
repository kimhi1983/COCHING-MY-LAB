<template>
 <!--section-->
 <section>
    <div class="container h-100">
      <div class="content">
        <div class="common-content">
          <!--content-title-wrap-->
          <NotificationMenuBar></NotificationMenuBar>
          
          <div class="board-wrap">
            <!--total-wrap-->
            <div class="total-wrap">
              <div class="total-num">총<span>{{ notiData.pi.totalItem }}</span></div>
            </div>
            <!--board-list-->
            <div class="board-list">
              <a href="javascript:;"
                v-for="(item, index) of notiData.list"
                :key="index"
                @click="onClickNoti(item)" >
                <!--content-wrap-->
                <div class="content-wrap">
                  <div class="title-wrap">
                    <div class="title">{{getNotiTitle(item)}}</div>
                  </div>
                  <div class="info">{{ item.content }}</div>
                  <div class="date">
                    <span>{{ item.rgtDttm | eFmtDateHMFormat('YYYY.MM.DD')}}</span>
                    <span class="chk-yn" v-if="item.chkYn === 'N'" style="color: var(--color--primary)">읽지않음</span>
                    <span class="chk-yn" v-else>읽음</span>
                  </div>
                </div>
              </a>
              
            </div>
            <!--pagenation-->
            <Pagenation
              v-model="notiData.pi.curPage"
              :totalRows="notiData.pi.totalItem"
              :perPage="notiData.pi.perPage"
              @input="onChangePage"
            ></Pagenation>
          </div>

        </div>
      </div>
    </div>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import { required, email, bizNumOnlyNumber, min, max, password } from '@validations';

import { getCodes } from '@/api/coching/comm/code';
import { getNotiList, updateChkYn } from '@/api/coching/comm/notification';
import NotificationMenuBar from './NotificationMenuBar.vue';
import Pagenation from '@/components/Pagenation.vue';


const DEF_NOTI = {
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

const DEF_NOTI_INF = {
    membSeq: null,
    email: '',
    membName: '',
    pswd: '',
    pswdConfirm: '',
    phone: '',
    department: '',
    auth: '',
};

const MENU_CATEGORY_MYPAGE = "MYPAGE";
const MENU_CODE = "002"

export default {
	name: 'coching-mypage-wish',
	mixins: [ernsUtils],
	components: {
    NotificationMenuBar,
    Pagenation,
},
	computed: {    
  },
	watch: {
	},
	props: {
    
  },
	data() {
		return {
      menuType: MENU_CATEGORY_MYPAGE,
      code: MENU_CODE,
      CODES: {
        CH012: [],
      },
      notiData :{
        list: [],
        pi: {...DEF_NOTI.pi},
        sc: {...DEF_NOTI.sc},
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

        await _vm.loadList(1);

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    async loadCodes() {
        const _vm = this;

        const codeRes = await getCodes({ grpCode: "CH012", etc5: _vm.$i18n.locale });
        const { resultCode, resultFailMessage, resultData } = codeRes;

        _vm.CODES.CH012 = [...resultData.list];
    },

    getNotiTitle(item) {
      const _vm = this;
      const matchingCode = _vm.CODES.CH012.find(code => code.etc1 === item.refCode);
      return matchingCode ? matchingCode.codeName : "알림이 도착했습니다."; 
    },

    async loadList(pageNo) {
        const _vm = this;
        const dataMap = _vm.notiData, pInfo = _vm.notiData.pi, searchOp = _vm.notiData.sc;
        pInfo.curPage = pageNo || 1;

        _vm.loading(true);
        try {
            const params = _vm.getListSearchParam();
            const res = await getNotiList(params);
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
        const pInfo = _vm.notiData.pi, searchOp = _vm.notiData.sc;

        const retParam = {

            page: pInfo.curPage,
            rowSize: pInfo.perPage
        };

        return retParam;
    },

    async onClickNoti(item){
      const _vm = this;

      if(item.chkYn === 'N'){
        await updateChkYn({notiSeq: item.notiSeq, chkYn: 'Y'});
      }
      _vm.eumGoNotiRouter(item);
    },

    /* 페이지 변경 */
      onChangePage(pageNo) {
      const _vm = this;

      _vm.loadList(pageNo);
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
#coching-mypage-wish {

}
</style>
