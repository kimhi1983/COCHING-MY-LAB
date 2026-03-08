<template>
  <div>
<b-card no-body>
      <b-card-body class="sys-vehicle-filter">
        <b-row>
          <b-col
            cols="12"
            class="mb-md-0 mb-2"
          >
            <div class="erns-filter-div mt-2 text-right">             
              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="primary"
                @click="onClickNew"
              > 신규등록
              </b-button>
            </div>
          </b-col>
        </b-row>
      </b-card-body>
    </b-card>

    <!-- 목록 -->
    <b-card no-body class="mb-0">
      <b-table
        class="position-relative" responsive striped
        :items="bannerData.rawList"
        :fields="bannerData.fields"
        ref="bannerlistTable"
        @row-clicked="onClickRow"

        primary-key="bannerId"
        show-empty
        empty-text="데이터가 없습니다."
      >
        <template #table-colgroup="scope">
          <col
            v-for="field in scope.fields"
            :key="field.key"
            :style="{ width: field.width }"
          >
        </template>

        <template #cell(index)="data">
          {{ data.index | eufRowNumberForPageInfo(bannerData.pi)}}
        </template>

        <template #cell(useYn)="data">
          {{ data.item.useYn | useYnName }}
        </template>

      </b-table>
      <div class="mx-2 mb-2">
        <b-row>
          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-start"
          >
            <v-select
              v-model="bannerData.pi.perPage"
              :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
              :options="showRows"
              :clearable="false"
              class="per-page-selector d-inline-block mx-50"
              @option:selected="loadList(1)"
            />

            <span class="text-muted">
              {{eumPaginationRangeForPageInfo(bannerData.pi)}}
            </span>
          </b-col>

          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
            <b-pagination
              v-if="bannerData.pi.totalItem > 0"
              v-model="bannerData.pi.curPage"
              :total-rows="bannerData.pi.totalItem"
              :per-page="bannerData.pi.perPage"
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
import ernsUtils from '@/components/mixins/ernsUtils';
import {getBannerList} from '@/api/coching-bo/sys/banner';
import Ripple from 'vue-ripple-directive'
import vSelect from 'vue-select';

const DEF_PARAM = {
  bannerMstId: "BANNER_0001",
  delYn: "N"
};

export default {
  name: '',
  mixins: [ernsUtils],  
  components : {
    vSelect
  },
  props: {

  },
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    }
  },  
  directives: {
    Ripple
  },
  watch: {
    
  },
  data(){
    return {
      params: DEF_PARAM,
      bannerData: {
        pi:{
          curPage : 1,
          totalItem : 0,
          perPage : 15
        },
        
        fields : [
          { width: '120px' , key: 'index'        , label: '번호'         , sortable: false , class: 'text-center' },
          { width: '500px' , key: 'bannerName'   , label: '제목'         , sortable: false , class: 'text-left' },
          { width: '*'     , key: 'bannerDesc'   , label: '내용'         , sortable: false , class: 'text-left' },
          { width: '200px' , key: 'useYn'        , label: '사용여부'      , sortable: false , class: 'text-center' },
        ],

        rawList:[],
        dispList:[]
      },

      selected: true,
    }
  },
  mounted(){
    const _vm = this;    
    _vm.loadList(_vm.bannerData.pi.curPage);
  },
  beforeDestroy(){
    const _vm = this;
	},
  //페이지 이동전 처리
  beforeRouteLeave : function(to, from, next){
    const _vm = this;    
    try{
      const data = {
        initParam : {
          pi : _vm.bannerData.pi
        }
      };
      //console.info("beforeRouteLeave");        
      //console.info(data);      
      _vm.eumSetRouteHistoryParam(data);
    }catch(err){
      console.error(err);
    }
  
    return next();
  },
  methods: {
    getInitParam() {
      const _vm = this;      
      const data = this.eumGetRouteHistoryParam();

      if(data && data.initParam){
        _vm.bannerData.pi = {...data.initParam.pi};
      }
    },
    
    onSearch(){
      const _vm = this;      
    },

    async loadList(pageNo){
      const _vm = this;      
      
      const dataList = _vm.bannerData, pInfo = _vm.bannerData.pi; 
      pInfo.curPage = pageNo || 1;
      
      const params = {
        ..._vm.params
      };

      _vm.loading(true);
      const res = await getBannerList(params); 
      const pageInfo = res.pageInfo;

      dataList.rawList = res.resultData
      dataList.pi = {
        ...dataList.pi,
        curPage : pageInfo.currentPage,
        totalItem : pageInfo.totalItem,
        perPage : pageInfo.pageItemSize
      };
      _vm.loading(false);
    },

    onClickNew() {
      const _vm = this;
      _vm.$router.push({ name: 'coching-bo-sys-mainPage-mainAdd'})
    },

    onClickRow(item) {
      const _vm = this;
      _vm.$router.push({ name: 'coching-bo-sys-mainPage-mainEdit', query: {bannerId: item.bannerId}});
    }
  }  
}
</script>

<style lang="scss" scoped>                                                  

</style>