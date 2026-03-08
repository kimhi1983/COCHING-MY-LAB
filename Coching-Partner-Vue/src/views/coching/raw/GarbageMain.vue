<template>
  <!--section-->
  <section>
    <div class="container">
      <div class="content-wrap">
        <!--title-->
        <div class="h1">휴지통</div>
        <!--content-->
        <div class="content-inner">
          <!--total-wrap-->
          <div class="total-wrap">
            <div class="total-num">{{ $t('') || '총' }}<span>{{ rawData.pi.totalItem }}</span></div>
            <div class="right">
              <!--select-->
              <CochingSelect v-model="rawData.sc.searchField"
                name="sources"
                :placeholder="$t('') || '전체'"
                :options="CODES.SC_001"
                :label="'label'"
                :trackBy="'etc1'"
                >
              </CochingSelect>

              <!--search-->
              <div class="input-set">
                <div class="input-ic-set">
                  <input v-model="rawData.sc.searchText" 
                    @keyup.enter="onClickSearch"
                    type="text" placeholder="검색" />
                  <button @click="onClickSearch" 
                    type="button" class="input-ic ic-md ic-search-md"></button>
                </div>
              </div>
            </div>
          </div>

          <!--table-->
          <table>
            <colgroup>
              <col width="100" />
              <col />
              <col width="150" />
              <col width="150" />
            </colgroup>
            <thead>
              <tr>
                <th>번호</th>
                <th class="text-left">원료명</th>
                <th>삭제일자</th>
                <th>복구</th>
              </tr>
            </thead>

            <tr v-for="(item, idx) of rawData.list"
              :key="item.rawReq"
              class="hover">
              <td>{{item.rn}}</td>
              <td class="text-left">{{item.rawName}}</td>
              <td>{{item.chngDttm}}</td>
              <td><button @click="onClickRollback(item)" 
                  type="button" class="ic-lg ic-deselect"></button></td>
            </tr>

          </table>
          <div v-if="rawData.list.length == 0" class="result-none">검색결과가 없습니다.</div>

          <!--pagenation-->
          <Pagenation
            v-model="rawData.pi.curPage"
            :totalRows="rawData.pi.totalItem"
            :perPage="rawData.pi.perPage"
            @input="onChangePage"></Pagenation>
        </div>
      </div>

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>

    </div>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import { required, email, bizNumOnlyNumber, min, max, password } from '@validations';

import { getGarbageList, updateRawDelYn } from '@/api/coching/comm/raw';

import Pagenation from '@/components/Pagenation.vue';
import CochingSelect from '@/components/CochingSelect.vue';

const DEF_RAW = {
	sc : {
		searchField : '',
    searchText : '',
    ptnSeq: null,
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

const DEF_RAW_INF = {
  rawSeq: '',  
  ptnSeq: '',
  rawDetailSeq: '',
  rawName: '',
  rawMemb: '',
  ratDttm: '',
};

export default {
	name: 'coching-garbage-main',
	mixins: [ernsUtils],
	components: {
    Pagenation,
    CochingSelect,
},
	computed: {    
  },
	watch: {
	},
	props: {
    
  },
	data() {
		return {
      CODES: {
            SC_001 : [],
          },
      rawInfo : {...DEF_RAW_INF},
      rawData : {
        list: [],
        pi: {...DEF_RAW.pi},
        sc: {...DEF_RAW.sc},
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

    //검색
		onClickSearch() {
      const _vm = this;

      _vm.loadList(1);
    },

    async loadList(pageNo) {
			const _vm = this;
      const dataMap = _vm.rawData, pInfo = _vm.rawData.pi, searchOp = _vm.rawData.sc;
      pInfo.curPage = pageNo || 1;

			_vm.loading(true);
      try {
          const params = _vm.getListSearchParam();
          const rawListRes = await getGarbageList(params);
          const resData = rawListRes.resultData;
          
          const pageInfo = resData.pageInfo;
          dataMap.pi = {
            ...dataMap.pi,
            curPage : pageInfo.currentPage,
            totalItem : pageInfo.totalItem,
            perPage : pageInfo.pageItemSize
          }

          //데이터 바인딩
          dataMap.list = resData.list;
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},
    

    async loadCodes(){
      const _vm = this;

      _vm.CODES.SC_001 = [
        {value:"", label : _vm.$t('') || '전체'},
        {value:"001", label : _vm.$t('') || '원료명'},
      ];

    },

    //페이지 변경
		onChangePage(pageNo) {
      const _vm = this;

      _vm.loadList(pageNo);
    },

    //검색조건
		getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.rawData.pi, searchOp = _vm.rawData.sc;

			const retParam = {
        ptnSeq: _vm.partnerInfo.ptnSeq,
				searchField : searchOp.searchField,
				searchText : $.trim("" + searchOp.searchText),
        delYn: 'Y',

        page : pInfo.curPage,
        rowSize : pInfo.perPage                
      };

			return retParam;
    },

    //원료 삭제
    async onClickRollback(item){
      const _vm = this;

      const rawListRes = await updateRawDelYn({rawSeq: item.rawSeq, delYn: "N"});
      const ret = await _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '원료 복구',
              titleHtml : _vm.$t('') || '복구되었습니다.'
            });

      if(ret){
        _vm.loadList(1);
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
#coching-mypage-main {

}
</style>
