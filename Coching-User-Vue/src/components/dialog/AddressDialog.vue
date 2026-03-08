<template>
  <div class="layer">
    <div class="layer-content">
      <div class="modal-inner modal-sm">
        <div class="modal-close" @click="onModalClose"></div>
        <form v-on:submit.prevent>
          <div class="modal-header b-b-0">
            <div class="header-title">주소찾기</div>
          </div>
          <div class="modal-content add-content">
            <div class="service-store-search-area">
              <div class="search-box-wrap">
                <input type="text" placeholder="예) 판교역로 235, 분당 주공, 삼평동 681" v-model="vMaster.sc.keyword" @keypress.enter="onSearch">
                <button type="button" class="search-box" @click="onSearch"></button>
              </div>
            </div>

            <div v-if="rawList.length == 0" class="add-tip">
              <div class="add-tip-title">tip</div>
              <div class="add-tip-text">도로명이나 지역명을 이용해서 검색해 보세요. 건물번호, 번지를 함께 입력하시면 더욱 정확한 결과가 검색됩니다.</div>
              </div>
            
            <!--검색결과 후 노출-->
            <div v-if="rawList.length > 0">
              <div class="add-tip">
                <div class="add-tip-text">아래 주소를 클릭해 주세요.</div>
              </div>
              <div class="add-result">
                <div class="add-list"
                  v-for="(item, index) in rawList"
                  :key="index"

                  @click="clickRowItem(item)"
                >
                  <div class="add-list-num"> <span class="text-red">{{item.zipNo}}</span></div>
                  <div class="add-list-title add-list-road">
                    <div class="add-badge bg-primary">도로명</div>
                    <div class="add-list-text">{{item.roadAddrPart1}} {{item.roadAddrPart2}}</div>
                  </div>
                  <div class="add-list-title add-list-street">
                    <div class="add-badge bg-primary-outline">지번</div>
                    <div class="add-list-text">{{item.jibunAddr}}</div>
                  </div>
                </div>
              </div>              
            </div>            
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
// import {getAddressList} from '@/api/coching/comm/code';

import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'coching-AddressLayer',
  mixins: [ernsUtils],
  components : {
    
  },

  data(){
    return {
      vMaster : {
        sc:{
          keyword : null,
        },

        pi:{
          curPage : 1,
          totalItem : 0,
          perPage : 100
        },
      },

      rawList : [],
      rowItem : {},
    }
  },

  mounted() {
    const _vm = this;
    _vm.fetchData();
  },

  methods : {
    async fetchData() {
      const _vm = this;
    },

    async onSearch() {
      const _vm = this;
      await _vm.loadList(1);
    },

    onModalClose() {
      const _vm = this;
      _vm.$emit("onModalClose");
    },


    async loadList(pageNo) {
      /**
       * 도로명주소 검색 API CALL
       */

      const _vm = this;
      const searchOp = _vm.vMaster.sc;
      const pInfo = _vm.vMaster.pi;
      pInfo.curPage = pageNo || 1;

      try {
        _vm.loading(true);

        const params = {
          keyword : searchOp.keyword,
          currentPage : pInfo.curPage,
          countPerPage : pInfo.perPage
        };

        const res = await getAddressList(params);        
        const restResults = res.objReturnData.results;
        const resCommon = restResults.common;

        _vm.rawList = [];

        if(resCommon.errorCode != 0){ // 도로명주소 API 검색 에러 (validation 등)
          _vm.$emit("returnError", resCommon.errorMessage);
        }else{ // 성공
          _vm.rawList = restResults.juso;
          pInfo.totalItem = resCommon.totalCount;
        }

      }catch(err) {
        _vm.$emit("returnError", err);
      }finally{
        _vm.loading(false);
      }
    },
    
    clickRowItem(item){
      const _vm = this;

      _vm.rowItem = item;
      _vm.$emit("clickRowItem");
    },

    getRowItem() {
      const _vm = this;
      return _vm.rowItem;
    },
  }
}
</script>