<template>
  <div>
    <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body>
        <b-row>
          <b-col cols="12" md="4"
            class="mb-md-0 mb-2"
          >
            <label>기간</label>
            <b-input-group>
              <b-form-datepicker 
                placeholder="YYYY.MM.DD" locale="ko" 
                :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                v-model="vMaster.sc.startDate"
                @input="onDateSearch"
              />
              <b-form-datepicker 
                :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                placeholder="YYYY.MM.DD" locale="ko"
                v-model="vMaster.sc.endDate"
                @input="onDateSearch"
              />
            </b-input-group>
          </b-col>

          <b-col cols="12" md="3" offset-md="5" class="mb-md-0 mb-2">
            <div class="erns-filter-div mt-2 text-right">
              <b-button
                variant="outline-secondary"
                class="mr-1"
                @click="onClickSearchResetBtn"
              >초기화</b-button>
              
              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="primary"
                @click="onClickNew"
              >
                등록
              </b-button>
            </div>
          </b-col>
        </b-row>
      </b-card-body>
    </b-card>
    <!-- // 검색조건 -->
    
    <!-- 목록 -->
    <b-card no-body class="mb-0">
      <b-table
        class="position-relative" responsive striped hover
        :items="vMaster.rawList"
        :fields="vMaster.fields"
        ref="holiDayListTable"
        @row-clicked="onClickRow"

        no-local-sorting
        @sort-changed="onSortingChanged"
        :sort-by.sync="vMaster.sc.sortBy"
        :sort-desc.sync="vMaster.sc.sortDesc"
        
        primary-key="holDate"
        show-empty
        empty-text="데이터가 없습니다."
      >
        <template #cell(holDate)="data">
          <div style="min-width:300px;">
            {{data.item.holDate}}
          </div>
        </template>
        
        <template #cell(holDesc)="data">
          <div style="min-width:600px;">
            {{data.item.holDesc}}
          </div>
        </template>

        <template #cell(deleteHol)="data">
          <div style="width:100px;">
            <b-button
              variant="outline-danger"
              @click="onClickDel(data.item.holDate)"
            >삭제</b-button>
          </div>
        </template>
      </b-table>
      
      <div class="mx-2 mb-2">
        <b-row>
          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-start"
          >
            <v-select
              v-model="vMaster.pi.perPage"
              :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
              :options="showRows"
              :clearable="false"
              class="per-page-selector d-inline-block mx-50"
              @option:selected="loadList(1)"
            />
            <span class="text-muted">
              {{eumPaginationRangeForPageInfo(vMaster.pi)}}
            </span>
          </b-col>
          
          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
            <b-pagination
              v-model="vMaster.pi.curPage"
              :total-rows="vMaster.pi.totalItem"
              :per-page="vMaster.pi.perPage"
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
    <!-- // 목록 -->

    <b-modal v-model="vMaster.modal1.show"
      centered no-close-on-backdrop
      ok-title="등록"
      cancel-title="닫기"
      :ok-disabled="!vMaster.modal1.holDesc.trim() || !vMaster.modal1.holDate"

      @ok.prevent="modal1Save"

      size="lg"
    >
      <b-card no-body class="mb-0">
        <b-row class="p-1">
          <b-col cols="12" md="12">
            <b-form-group
              label="날짜"
              label-cols="2"
            >
              <b-form-datepicker
                placeholder="YYYY.MM.DD" locale="ko" 
                :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                v-model="vMaster.modal1.holDate"
                @input="onDateValid"
              />
            </b-form-group>
          </b-col>

          <b-col cols="12" md="12">
            <b-form-group
              label="설명"
              label-cols="2"
            >
              <b-form-input
                v-model="vMaster.modal1.holDesc"
                maxlength="20"
              />
            </b-form-group>
          </b-col>
        </b-row>
      </b-card>
    </b-modal>

    <b-modal v-model="vMaster.modal2.show"
      centered no-close-on-backdrop
      ok-title="수정"
      cancel-title="닫기"
      :ok-disabled="!vMaster.modal2.holDesc.trim() || !vMaster.modal2.holDate"

      @ok.prevent="modal2Save"

      size="lg"
    >
      <b-card no-body class="mb-0">
        <b-row class="p-1">
          <b-col cols="12" md="12">
            <b-form-group
              label="날짜"
              label-cols="2"
            >
              <b-form-datepicker
                placeholder="YYYY.MM.DD" locale="ko" 
                :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                v-model="vMaster.modal2.holDate"
                @input="onDateValid"
              />
            </b-form-group>
          </b-col>

          <b-col cols="12" md="12">
            <b-form-group
              label="설명"
              label-cols="2"
            >
              <b-form-input
                v-model="vMaster.modal2.holDesc"
                maxlength="20"
              />
            </b-form-group>
          </b-col>
        </b-row>
      </b-card>
    </b-modal>
  </div>
</template>

<script>
import Ripple from 'vue-ripple-directive'
import ernsUtils from '@/components/mixins/ernsUtils';
import vSelect from 'vue-select'

import {
  getHolidayList, 
  insertHoliday, 
  updateHoliday, 
  deleteHoliday
} from '@/api/coching-bo/system/holiday';

export default {
  mixins: [ernsUtils],
  directives: {
    Ripple,
  },
  components : {
    vSelect
  },
  props: {},
  filters :{},
  computed: {
    showRows(){
      return this.$store.state.erns.showRows;
    } 
  },
  data(){
    return {
      vMaster :{
        sc:{
          startDate : null,
          endDate : null,

          sortBy : "holDate",
          sortDesc : true,
        },

        pi:{
          curPage : 1,
          totalItem : 0,
          perPage : 15
        },
        
        fields : [
          { key: 'holDate', label: '날짜', sortable: true , class: 'text-center' },
          { key: 'holDesc', label: '설명', sortable: true , class: 'text-center' },
          { key: 'deleteHol', label: '', sortable: false , class: 'text-center' },
        ],

        rawList:[],

        modal1 : {
          show : false,
          holDate : "",
          holDesc : "",
        },

        modal2 : {
          show : false,
          holDate : "",
          holDesc : "",
          holDateOrg : "",
        },
      },
    }
  },
  mounted(){
    const _vm = this;
    _vm.docReady();
    _vm.loadList();
  },
  beforeDestroy(){
    
	},
  methods: {
    async docReady(){
      const _vm = this;
    },

    onSearch(){
      const _vm = this;
      _vm.loadList(1);
    },

    async loadList(pageNo){
      const _vm = this;
      const vMaster = _vm.vMaster, pInfo = _vm.vMaster.pi, searchOp = _vm.vMaster.sc;
      pInfo.curPage = pageNo || 1;

      try{
        _vm.loading(true);

        const params = {
          page : pInfo.curPage,
          rowSize : pInfo.perPage,

          startDate : searchOp.startDate,
          endDate : searchOp.endDate,

          sortColumn : _vm.eumCamel2under(searchOp.sortBy),
          sortType : searchOp.sortDesc ? "DESC" : "ASC",
        }

        const res = await getHolidayList(params);
        const pageInfo = res.pageInfo;

        vMaster.rawList = res.resultData
        vMaster.pi = {
          ...vMaster.pi,
          curPage : pageInfo.currentPage,
          totalItem : pageInfo.totalItem,
          perPage : pageInfo.pageItemSize
        };
        
      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    onClickRow(data, index){
      const _vm = this;

      _vm.vMaster.modal2.holDate = data.holDate;
      _vm.vMaster.modal2.holDesc = data.holDesc;
      _vm.vMaster.modal2.holDateOrg = data.holDate;

      _vm.vMaster.modal2.show = true;
    },

    onClickNew() {
      const _vm = this;

      _vm.vMaster.modal1.holDate = "";
      _vm.vMaster.modal1.holDesc = "";

      _vm.vMaster.modal1.show = true;
    },

    onDateSearch(e){
      const _vm = this;
      _vm.loadList(1);
    },

    //정렬변경 이벤트
    onSortingChanged(ctx){
      const _vm = this; 
      const sc = _vm.vMaster.sc;
      const pInfo = _vm.vMaster.pi;

      sc.sortBy = ctx.sortBy;
      sc.sortDesc = ctx.sortDesc;

      _vm.loadList(pInfo.curPage);
    },
    
    onClickSearchResetBtn(){
      const _vm = this;

      _vm.vMaster.sc.startDate = null;
      _vm.vMaster.sc.endDate = null;
      _vm.vMaster.sc.sortBy = "holDate";
      _vm.vMaster.sc.sortDesc = true;

      _vm.loadList(1);
    },

    async modal1Save(){
      const _vm = this;

      const result = await _vm.$swal({
        title: '공휴일 등록',
        text: "공휴일을 등록하시겠습니까?",
        showCancelButton: true,
        confirmButtonText: '등록',
        customClass: {
          confirmButton: 'btn btn-primary ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });
      
      if(!result.value) {
        return;
      }

      try{
        _vm.loading(true);
        const params = {
          holDate : _vm.vMaster.modal1.holDate,
          holDesc : _vm.vMaster.modal1.holDesc,
        };

        await insertHoliday(params);

        _vm.loading(false);
        await _vm.alertSuccess(`공휴일을 등록했습니다.`);      
        _vm.vMaster.modal1.show = false;
        _vm.loadList(_vm.vMaster.pi.curPage);

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    async modal2Save(){
      const _vm = this;

      const result = await _vm.$swal({
        title: '공휴일 수정',
        text: "공휴일을 수정하시겠습니까?",
        showCancelButton: true,
        confirmButtonText: '수정',
        customClass: {
          confirmButton: 'btn btn-primary ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });
      
      if(!result.value) {
        return;
      }

      try{
        _vm.loading(true);
        const params = {
          holDate : _vm.vMaster.modal2.holDate,
          holDesc : _vm.vMaster.modal2.holDesc,
          holDateOrg : _vm.vMaster.modal2.holDateOrg,
        };

        await updateHoliday(params);

        _vm.loading(false);
        await _vm.alertSuccess(`공휴일을 수정했습니다.`);      
        _vm.vMaster.modal2.show = false;
        _vm.loadList(_vm.vMaster.pi.curPage);

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    async onDateValid(holDate){
      const _vm = this;
      const params = {
        holDate:holDate,
      };
      const res = await getHolidayList(params);

      if(res.resultData.length > 0){
        if(_vm.vMaster.modal1.show){ //등록모달
          await _vm.alertError("이미 공휴일로 지정된 날짜입니다.");
          _vm.vMaster.modal1.holDate = "";
        }else{ //수정모달
          if(holDate != _vm.vMaster.modal2.holDateOrg){
            await _vm.alertError("이미 공휴일로 지정된 날짜입니다.");
            _vm.vMaster.modal2.holDate = _vm.vMaster.modal2.holDateOrg;
          }
        }
      }
    },

    async onClickDel(holDate){
      const _vm = this;

      const result = await _vm.$swal({
        title: '공휴일 삭제',
        text: "날짜 '" + holDate + "' 을 삭제하시겠습니까?",
        showCancelButton: true,
        confirmButtonText: '삭제',
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });
      
      if(!result.value) {
        return;
      }

      try{
        _vm.loading(true);
        const params = {
          holDate : holDate,
        };

        await deleteHoliday(params);

        _vm.loading(false);
        await _vm.alertSuccess(`공휴일을 삭제했습니다.`);      
        _vm.loadList(1);

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    }
  }
}
</script>

<style lang="scss" scoped>

</style>

<style lang="scss">
  .table-responsive .table th[role=columnheader] div{
    white-space : nowrap;
  }  
</style>