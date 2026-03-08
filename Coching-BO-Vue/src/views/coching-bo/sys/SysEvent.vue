<template>
  <div>
    <b-card no-body>
      <b-card-body class="sys-event-filter">

        <b-row>
          <b-col cols="12" md="3"
            class="mb-md-0 mb-2"
          >
            <label>상태</label>
            <v-select
              v-model="eventData.sc.eventStatus"
              :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
              class="w-100"
              :options="codes.EVENT_STATUS" :reduce="op => op.value"
              @input="loadList(1)"
            >
            </v-select>
          </b-col>
          <b-col cols="12" md="6"
            class="mb-md-0 mb-2"
          >
            <label>제목을 입력하세요</label>
            <b-input-group>
              <b-form-input
                v-model="eventData.sc.title"
                @keypress.enter="loadList(1)"
              ></b-form-input>
              <b-input-group-append>
                <b-button variant="outline-primary"
                  @click="loadList(1)"
                >검색</b-button>
              </b-input-group-append>
            </b-input-group>
          </b-col>

          <b-col
            cols="12" md="3"
            class="mb-md-0 mb-2">
            <div class="erns-filter-div mt-2 text-right">
              <!-- <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                class="mr-1"
                variant="outline-primary"
                @click="onClickOrder"
              > 순서설정
              </b-button> -->

              <b-button
                class="mr-2"
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="primary"
                @click="onClickNew"
              > 이벤트등록
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
        :items="eventData.rawList"
        :fields="eventData.fields"
        ref="bannerlistTable"
        @row-clicked="onClickRow"

        primary-key="bannerId"
        show-empty
        empty-text="데이터가 없습니다."
      >
        <template #cell(index)="data">
          {{ data.index | eufRowNumberForPageInfo(eventData.pi)}}
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
              v-model="eventData.pi.perPage"
              :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
              :options="showRows"
              :clearable="false"
              class="per-page-selector d-inline-block mx-50"
              @option:selected="loadList(1)"
            />

            <span class="text-muted">
              {{eumPaginationRangeForPageInfo(eventData.pi)}}
            </span>
          </b-col>

          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
            <b-pagination
              v-if="eventData.pi.totalItem > 0"
              v-model="eventData.pi.curPage"
              :total-rows="eventData.pi.totalItem"
              :per-page="eventData.pi.perPage"
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
import {getCodes} from '@/api/coching-bo/comm/code';
import {getList} from '@/api/coching-bo/comm/event';

import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive'
import vSelect from 'vue-select';

const DEF_PARAMS = {
  delYn: 'N',
}

const DEF_SEARCH_OPT = {
  pi:{
    curPage : 1,
    totalItem : 0,
    perPage : 15
  },
  sc: {
    eventStatus: '',
    title: ''
  }
}
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
      params: DEF_PARAMS,

      codes: {
        EVENT_STATUS: []
      },
      eventData: {
        pi:{...DEF_SEARCH_OPT.pi},
        sc: {...DEF_SEARCH_OPT.sc},
        fields : [
          { key: 'index'          , label: '번호'         , sortable: false , class: 'text-center', thStyle : {width: "8%"} },
          { key: 'eventStatusCd'  , label: '상태'         , sortable: false , class: 'text-center', thStyle : {width: "10%"} },
          { key: 'title'          , label: '제목'         , sortable: false , thClass: "text-center", thStyle : {width: "20%"} },
          { key: 'dispYn'         , label: '노출여부'     , sortable: false , class: 'text-center', thStyle : {width: "12%"} },
          { key: 'useYn'          , label: '사용여부'     , sortable: false , class: 'text-center', thStyle : {width: "12%"} },
          { key: 'eventPeriod'    , label: '이벤트기간'   , sortable: false , class: 'text-center', thStyle : {width: "*%"}},
        ],

        rawList:[],
        dispList:[]
      },

      selected: false,
      isLoaded: false,
    }
  },
  mounted(){
    const _vm = this;
    _vm.fetchData();
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
          sc: _vm.eventData.sc,
          pi: _vm.eventData.pi
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
      // const data = this.eumGetRouteHistoryParam();

      // if(data && data.initParam){
      //   _vm.eventData.pi = {...data.initParam.pi};
      // }
      const query = _vm.$route.query, params = _vm.$route.params;

      _vm.params = {
        ...query
      };
    },

    async fetchData() {
      const _vm = this;
      await _vm.loadCodes();
      await _vm.loadList(1);
    },

    async loadList(pageNo){
      const _vm = this;

      const dataList = _vm.eventData, pInfo = _vm.eventData.pi, searchOp = _vm.eventData.sc;
      pInfo.curPage = pageNo || 1;

      const params = _vm.getListSearchParam();
      console.log('!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!pInfo!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!')
      console.log(pInfo);
      console.log('!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!pInfo!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!')

      _vm.loading(true);
      try {
        const res = await getList(params);

        dataList.rawList = res.resultData.list.map(item=>{
          const period = item.fromDate.concat(" ~ ", item.toDate);
          let statusCd = '';
          for(const code of _vm.codes.EVENT_STATUS) {
            if(item.evntStatus == code.value) {
              statusCd = code.label;
            }
          };

          return {
            ...item,
            eventPeriod: period,
            eventStatusCd: statusCd
          }
        });

        const pageInfo = res.resultData.pageInfo;
        dataList.pi = {
          ...dataList.pi,
          curPage : pageInfo.currentPage,
          totalItem : pageInfo.totalItem,
          perPage : pageInfo.pageItemSize
        };
        console.log(pageInfo);
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }


    },

    async loadCodes() {
      const _vm = this;
      const rawList = await getCodes({codeMst: 'EVENT_STATUS', rowSize: -1});
      _vm.codes.EVENT_STATUS = _vm.eumConvertToVueSelectOption(rawList.resultData.list);

    },

    onClickNew() {
      const _vm = this;
      _vm.$router.push({ name: 'coching-bo-sys-event-add'})
    },

    onClickRow(item) {
      const _vm = this;
      _vm.$router.push({ name: 'coching-bo-sys-event-edit', query: {evntSeq: item.evntSeq}});
    },

    onClickOrder() {
      const _vm = this;
    },

    getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.eventData.pi, searchOp = _vm.eventData.sc;

      const params = {
        page: pInfo.curPage,
        rowSize: pInfo.perPage,

        eventStatus: searchOp.eventStatus,
        title: searchOp.title
      };
      return params;
    },
  }
}
</script>

<style lang="scss" scoped>

</style>