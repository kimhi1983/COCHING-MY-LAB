<template>
  <b-card no-body class="p-1 pt-0">

    <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="cm-filter">
        <b-row>

          <b-col cols="12" md="4" class="mb-md-0 mb-2">
            <label>성명</label>
            <b-input-group>
              <b-form-input v-model="list.sc.nameL"
                @keyup.enter="onSearch"
                placeholder="성명을 입력하세요."
              ></b-form-input>
              <b-input-group-append>
                <b-button variant="outline-primary"
                  @click="onSearch"
                >검색</b-button>      
              </b-input-group-append>
            </b-input-group>
          </b-col>
          
          <b-col cols="12" md="4" class="mb-md-0 mb-2">
            <label>연락처</label>
            <b-input-group>
              <b-form-input v-model="list.sc.phoneL"
                @keyup.enter="onSearch"
                placeholder="연락처를 입력하세요."
              ></b-form-input>
              <b-input-group-append>
                <b-button variant="outline-primary"
                  @click="onSearch"
                >검색</b-button>      
              </b-input-group-append>
            </b-input-group>
          </b-col>

          <b-col cols="12" md="4" class="mb-md-0 mb-2">
            <label>My Shop</label>
            <v-select
              v-model="list.sc.myShop"              
              @input="onSearch"
              :options="codes.SHOP_LIST" :reduce="op => op.value"
              :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
              class="w-100"
              placeholder="마이샵을 검색하세요."
            >
              <span slot="no-options">검색된 항목이 없습니다.</span>
            </v-select>
          </b-col>

          <b-col cols="12" md="4" class="mb-md-0 mb-2">
            <label>차량번호</label>
            <b-input-group>
              <b-form-input v-model="list.sc.vehicleNumberL"
                @keyup.enter="onSearch"
                placeholder="차량번호를 입력하세요."
              ></b-form-input>
              <b-input-group-append>
                <b-button variant="outline-primary"
                  @click="onSearch"
                >검색</b-button>      
              </b-input-group-append>
            </b-input-group>
          </b-col>

          <b-col cols="12" md="4" class="mb-md-0 mb-2">
            <label>ID</label>
            <b-input-group>
              <b-form-input v-model="list.sc.userIdL"
                @keyup.enter="onSearch"
                placeholder="ID를 입력하세요."
              ></b-form-input>
              <b-input-group-append>
                <b-button variant="outline-primary"
                  @click="onSearch"
                >검색</b-button>      
              </b-input-group-append>
            </b-input-group>
          </b-col>

          <b-col cols="12" md="4" class="mb-md-0 mt-2 mb-2 text-right">
            <b-button
              variant="outline-secondary"             
              @click="onClickSearchResetBtn"
            >초기화</b-button>            
          </b-col>

        </b-row>
      </b-card-body>
    </b-card>
    <!-- // 검색조건 -->

    <!-- 목록 -->
    <b-card no-body class="mb-0">
      
      <b-table
        ref="listlistTable"
        class="position-relative" responsive striped hover
        :items="list.rawList" :fields="list.fields"        

        selectable
        @row-selected="onRowSelected"
        :select-mode="multiple ? 'multi': 'single'"
        selected-variant="primary"

        @sort-changed="onSortingChanged" no-local-sorting
        :sort-by.sync="list.sc.sortBy"
        :sort-desc.sync="list.sc.sortDesc" 
        tbody-tr-class="table-click-row"
        primary-key="userSeq"        
        show-empty
        empty-text="데이터가 없습니다."        
      >
        <template #cell(selected)="{ rowSelected }">
          <template v-if="rowSelected">
            <span aria-hidden="true">&check;</span>
            <span class="sr-only">Selected</span>
          </template>
          <template v-else>
            <span aria-hidden="true">&nbsp;&nbsp; &nbsp;&nbsp;</span>
            <span class="sr-only">Not selected</span>
          </template>
        </template>

        <template #cell(index)="data">
          {{ data.index | eufRowNumberDescForPageInfo(list.pi) }}
          <!-- {{ data.index + 1 }} -->
        </template>

        <template #cell(userId)="data">
          {{ data.item.userId | eufmtEllipsis(20) }}
        </template>

        <template #cell(phone)="data">
          {{ data.item.phone | eufmtPhoneDash }}
        </template>

         <template #cell(myshopFrSeq)="data">
          {{ data.item.myshopFrSeq | eufGetCodeName(codes.SHOP_LIST)}}
        </template>

        <template #cell(regDate)="data">
          {{ data.item.regDate | eFmtDateTime('.') }}
        </template>

        <template #cell(udtDate)="data">
          {{ data.item.udtDate | eFmtDateTime('.') }}
        </template>

      </b-table>

      <div class="mx-2 mb-2">
        <b-row>
          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-start"
          >
            <v-select
              v-model="list.pi.perPage"
              :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
              :options="showRows"
              :clearable="false"
              class="per-page-selector d-inline-block mx-50"
              @option:selected="loadList(1)"
            />

            <span class="text-muted">
              {{eumPaginationRangeForPageInfo(list.pi)}}
            </span>
          </b-col>
          
          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
            <b-pagination
              v-if="list.pi.totalItem > 0"
              v-model="list.pi.curPage"
              :total-rows="list.pi.totalItem"
              :per-page="list.pi.perPage"
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
  </b-card>  
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive'
import vSelect from 'vue-select';

import {
  getCmList
} from '@/api/coching-bo/cm/cm';
import {
  listFr
} from '@/api/coching-bo/fr/franchise';

const DEF_SEARCH_OPT = {
  sc:{
    joinDateFrom : null,
    joinDateTo : null,
    myShop : "",
    nameL : "",
    phoneL : "",
    keyword : "",
    vehicleNumberL : "",
    userIdL : "",
    umType : "UMT_ALL",
  },

  pi:{
    curPage : 1,
    totalItem : 0,
    perPage : 5 
  },
};

export default {
  name: 'coching-FindUserForm',
  mixins: [ernsUtils],  
  components : {
    vSelect
  },
  props: {
    multiple : {
      type : Boolean,
      default : false
    }
  },
  directives: {
    Ripple
  },
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    } 
  },
  watch: {
  },
  data(){
    return {
      codes : {
        SHOP_LIST : [],
        SHOP_LIST_RAW : [],
        VEHICLE_BRAND : [],
        VEHICLE_TYPE : []
      },
      
      list :{
        sc:{...DEF_SEARCH_OPT.sc},
        pi:{...DEF_SEARCH_OPT.pi},        

        fields : [         
          { key: 'userId'       , label: 'ID'           , sortable: false  , class: 'text-left' },
          { key: 'userName'     , label: '이름'         , sortable: false  , class: 'text-center' },
          { key: 'phone'        , label: '연락처'       , sortable: false  , class: 'text-center' },
          { key: 'myshopFrSeq'  , label: '마이샵'       , sortable: false , class: 'text-left' },
          { key: 'vehicleNumber', label: '대표차량번호' , sortable: false , class: 'text-center' },
        ],

        rawList:[],        
        dispList:[]
      },

      selectedItems : [],
    }
  },
  mounted(){
    const _vm = this;    

    if(_vm.multiple){
      _vm.list.fields.splice(0, 0, { key: 'selected'     , label: '선택'         , stickyColumn: false, sortable: false , class: 'text-center', width: "40px" });
    }else{
      _vm.list.fields.splice(0, 0, { key: 'index'        , label: '번호'         , stickyColumn: false, sortable: false , class: 'text-center', width: "40px" });
    }

    _vm.loadCode();
    //_vm.loadList(_vm.list.pi.curPage);     
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    //검색
    onSearch(){
      const _vm = this;      
      _vm.loadList(1);
    },

    //선택
    onRowSelected(items) {
      const _vm = this;

      //console.debug(items);
      if(_vm.multiple){
        _vm.list.rawList.selected = items;
      }else{
        _vm.$emit("onClickUserSelect", items[0]); 
      }
    },

    getSelectUser(){
      const _vm = this;

      return _vm.list.rawList.selected;
    },

    //정렬변경 이벤트
    onSortingChanged(ctx){
      const _vm = this; 
      const sc = _vm.list.sc;
      const pInfo = _vm.list.pi;
      console.info(ctx);

      _vm.loadList(pInfo.curPage);
    },

    //목록로드
    async loadList(pageNo){
      const _vm = this;

      const dataList = _vm.list, pInfo = _vm.list.pi, searchOp = _vm.list.sc;
      pInfo.curPage = pageNo || 1;

      try{
        const params = _vm.getListSearchParam();

        _vm.loading(true);
        const res = await getCmList(params); 
        const pageInfo = res.pageInfo;
        dataList.rawList = res.resultData
        dataList.pi = {
          ...dataList.pi,
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

    //검색 파라미터
    getListSearchParam(){
      const _vm = this;

      const pInfo = _vm.list.pi, searchOp = _vm.list.sc;
      return {
        page : pInfo.curPage,
        rowSize : pInfo.perPage,
        delYn : "N",
        sortColumn : _vm.eumCamel2under(searchOp.sortBy),
        sortType : searchOp.sortDesc ? "DESC" : "ASC",
        
        searchText : searchOp.keyword,
        searchField : searchOp.field,

        userNameL : searchOp.nameL,
        phoneL : searchOp.phoneL,

        myShopFrSeq : searchOp.myShop,
        vehicleNumberL : searchOp.vehicleNumberL,
        userIdL : searchOp.userIdL,
        
        joinDateFrom : searchOp.joinDateFrom,
        joinDateTo : searchOp.joinDateTo,

        noshowYn : searchOp.umType == 'UMT_99' ? "Y" : "N",
        attentionYn : searchOp.umType == 'UMT_01' ? "Y" : "N",
      };
    },

    //검색 초기화
    onClickSearchResetBtn() {
      const _vm = this;

      _vm.list.sc = {...DEF_SEARCH_OPT.sc};
      _vm.list.pi = {...DEF_SEARCH_OPT.pi};
      
      _vm.onSearch();
    },
    
    //코드 로드
    async loadCode(){
      const _vm = this;

      { //가맹점 목록
        const params = {
          rowSize : -1,
        };
        const res = await listFr(params);
        _vm.codes.SHOP_LIST_RAW = res.resultData;
        _vm.codes.SHOP_LIST = res.resultData.map(item=>{
          return {label:item.frName, value:item.frSeq};
        });
      }
    }
  }  
}
</script>

<style lang="scss" scoped>
.userForm {
  max-height: 600px;
}
</style>