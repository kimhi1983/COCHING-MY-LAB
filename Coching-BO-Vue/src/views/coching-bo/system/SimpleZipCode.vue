<template>
  <div>
<!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="sys-board-master-filter">
        <b-row>
          <b-col cols="12" md="12" class="mb-md-0 mb-2"
          >
            <div class="erns-filter-div mt-2 text-right">             
              <b-button
                variant="primary"
                @click="onClickReNew"
              > 주소이관
              </b-button>
            </div>
          </b-col>

        </b-row>
      </b-card-body>
    </b-card>
    <!-- 목록 -->
    <b-card no-body class="mb-0">

      <b-table
        class="position-relative" responsive
        :items="vZipcode.rawList"        
        :fields="vZipcode.fields"
        ref="vZipcodelistTable"
        hover       
        show-empty
        empty-text="데이터가 없습니다."        
      >

      </b-table>

      <div class="mx-2 mb-2">
        <b-row>
          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-start"
          >
            <span class="text-muted">
              {{eumPaginationRangeForPageInfo(vZipcode.pi)}}
            </span>
          </b-col>
          
          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
            <b-pagination
              v-model="vZipcode.pi.curPage"
              :total-rows="vZipcode.pi.totalItem"
              :per-page="vZipcode.pi.perPage"
              first-number
              last-number
              limit = "10"
              class="mb-0 mt-1 mt-sm-0"
              prev-class="prev-item"
              next-class="next-item"
              @change="loadList"
            >
              <template #prev-text>
                <feather-icon icon="ChevronLeftIcon" size="18"/>
              </template>
              <template #next-text>
                <feather-icon icon="ChevronRightIcon" size="18"/>
              </template>
            </b-pagination>
          </b-col>

        </b-row>
      </div> 
    </b-card>
  </div>
</template>
<script>
import {getZipcodeList, setZipcode} from '@/api/coching-bo/system/zipcode';
import vSelect from 'vue-select';

import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'coching-BackOffice-System-Main-Zipcode',
  mixins: [ernsUtils],
  components : {
  
  },
  props: {

  },
  data(){
    return {
      vZipcode:{
        pi:{
          curPage : 1,
          totalItem : 0,
          perPage : 20       
        },

        fields : [
          { key: 'zipCode'      , label: '우편번호'     , sortable: false , class: 'text-center' },
          { key: 'siDo'         , label: '시도'         , sortable: false , class: 'text-center' },
          { key: 'siGunGu'      , label: '시군구'         , sortable: false , class: 'text-center' },
        ],

        rawList:[],
      }
    }
  },
  mounted(){
    const _vm = this;    
    _vm.loadList();
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {

    loadList(pageNo) {
      const _vm = this;
      _vm.apiList(_vm.vZipcode.pi.curPage = pageNo || 1);
    },

    async apiList(pageNo) { 
      const _vm = this;
      const vZipcode = _vm.vZipcode, pInfo = _vm.vZipcode.pi;

      const params = {
        page : pInfo.curPage,
        rowSize : pInfo.perPage
      }
    
      const res = await getZipcodeList(params);
      const pageInfo = res.resultData.pageInfo;
      vZipcode.rawList = res.resultData.list
      vZipcode.pi = {
        ...vZipcode.pi,
        curPage : pageInfo.currentPage,
        totalItem : pageInfo.totalItem,
        perPage : pageInfo.pageItemSize        
      }

      console.log(vZipcode)
    },

    onClickReNew(){
      const _vm = this;
      _vm.apiRenew();
    },

    async apiRenew(){
      const _vm = this;
      
      _vm.loading(true);
      try {
        await setZipcode();
        _vm.alertSuccess(`우편번호 갱신이 완료되었습니다.`);
        _vm.loadList(1);
      } catch(err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }

    }
  }  
}
</script>

<style lang="scss" scoped>

</style>