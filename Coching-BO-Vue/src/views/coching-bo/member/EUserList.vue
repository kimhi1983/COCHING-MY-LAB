<template>
  <div id="coching-bo-biz-user-mgr-main">
    
    <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="cm-main-filter">
        <b-row>

          <b-col cols="12" md="3" class="mb-md-0 mb-2">
            <b-form-group
              label="이름"
              label-for="user-list-sc-name">
              <b-input-group id="user-list-sc-name">
                <b-form-input v-model="list.sc.membNameL"
                  @keyup.enter="loadList(1)"
                  placeholder="사용자 이름을 입력하세요."
                ></b-form-input>
                <b-input-group-append>
                  <b-button variant="outline-primary"
                    @click="loadList(1)"
                  >검색</b-button>      
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="3" class="mb-md-0 mb-2">
            <b-form-group
              label="휴대폰번호"
              label-for="user-list-sc-phone">
              <b-input-group id="user-list-sc-phone">
                <b-form-input v-model="list.sc.phoneL"
                  @keyup.enter="loadList(1)"
                  placeholder="사용자 휴대폰번호를 입력하세요."
                ></b-form-input>
                <b-input-group-append>
                  <b-button variant="outline-primary"
                    @click="loadList(1)"
                  >검색</b-button>      
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>
          
          <b-col cols="12" md="3" class="mb-md-0 mb-2">
            <b-form-group
              label="가입일"
              label-for="user-list-sc-id">
              <b-input-group id="payment-list-trdDate">
                <b-form-datepicker v-model="list.sc.joinFromDate"
                  :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                  placeholder="YYYY-MM-DD" locale="ko" 
                  today-button label-today-button="오늘"
                  reset-button label-reset-button="초기화"
                  close-button label-close-button="닫기"
                  @input="onSearch"
                />
                <b-form-datepicker v-model="list.sc.joinToDate"
                  :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                  placeholder="YYYY-MM-DD" locale="ko"
                  today-button label-today-button="오늘"
                  reset-button label-reset-button="초기화"
                  close-button label-close-button="닫기"
                  @input="onSearch"
                />
              </b-input-group>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="3" class="mb-md-0 mb-2">
            <b-form-group
              label="최근접속일자"
              label-for="user-list-sc-id">
              <b-input-group id="payment-list-trdDate">
                <b-form-datepicker v-model="list.sc.rctFromDate"
                  :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                  placeholder="YYYY-MM-DD" locale="ko" 
                  today-button label-today-button="오늘"
                  reset-button label-reset-button="초기화"
                  close-button label-close-button="닫기"
                  @input="onSearch"
                />
                <b-form-datepicker v-model="list.sc.rctToDate"
                  :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                  placeholder="YYYY-MM-DD" locale="ko"
                  today-button label-today-button="오늘"
                  reset-button label-reset-button="초기화"
                  close-button label-close-button="닫기"
                  @input="onSearch"
                />
              </b-input-group>
            </b-form-group>
          </b-col>

          

          <b-col cols="12" md="12" class="mb-md-0 mb-2">
            <div class="erns-filter-div mt-2 text-right">
              <!-- <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-success"
                class="mr-1"
                @click="onClickExcelBtn"
              >EXCEL</b-button> -->

              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-secondary"
                class="mr-1"
                @click="onClickResetFilter"
              > 검색초기화
              </b-button>
              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="primary"
                @click="loadList(1)"
              > 검색
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
        :items="list.rawList"
        :fields="list.fields"
        ref="boardListTable"
        @row-clicked="onClickListRow"
        
        show-empty
        empty-text="검색된 내용이 없습니다."
      >
        <template #cell(index)="data">
          {{ data.index | eufRowNumberForPageInfo(list.pi) }}
        </template>

        <template #cell(membName)="data">
          {{ data.item.membName | eufBlankToDash }}
        </template>
        
        <template #cell(userIdStr)="data">
          {{ data.item.userIdStr | eufUserId }}
        </template>

        <template #cell(phone)="data">
          {{ data.item.phone | eufmtPhoneDash }}
        </template>

        <template #cell(email)="data">
          {{ data.item.email | eufBlankToDash }}
        </template>

        <template #cell(membStatus)="data">
          <span v-if='data.item.useYn =="Y"' class="text-muted">미사용</span>
          <span v-else-if='data.item.membWtdrYn =="Y"' class="text-danger">탈회</span>
          <span v-else-if='data.item.membDormantYn =="Y"' class="text-warning">휴면</span>
          <span v-else class="text-success">사용중</span>
        </template>

        <template #cell(membEmulator)="data">
          <b-button variant="outline-primary" size="sm"
            @click="onClickPcEmulator(data.item)"
          >에뮬레이터</b-button> 
        </template>

        <template #cell(bsnLicNum)="data">
          {{ data.item.bsnLicNum | eufmtBizNumDash }}
        </template>

        <template #cell(bsnNm)="data">
          {{ data.item.bsnNm | eufBlankToDash }}
        </template>

        <template #cell(bsnRepNm)="data">
          {{ data.item.bsnRepNm | eufBlankToDash }}
        </template>

        <template #cell(bsnMainCateNm)="data">
          {{ data.item | fmtBizSctLargeCateNm(code.RAW_BIZ_SCT) }}
        </template>

        <template #cell(bsnMidCateNm)="data">
          {{ data.item | fmtBizSctMiddleCateNm(code.RAW_BIZ_SCT) }}
        </template>

        <template #cell(bsnAddr)="data">
          {{ data.item | fmtAddress }}
        </template>

        <template #cell(scrapLinkStatus)="data">
          {{ data.item | fmtScrapLinkStatus }}
        </template>

        <template #cell(scrapLinkDetail)="data">
          <b-button variant="outline-primary" size="sm"
            @click="onClickLinkDetail(data.item)"
          >상세보기</b-button> 
        </template>

        <template #cell(taxdietId)="data">
          {{ data.item | fmtTaxdietId }}
        </template>

        <template #cell(membWtdrDttm)="data">
          {{ data.item.membWtdrYn == 'Y' ?  data.item.membWtdrDttm : '-'}}
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
    <!-- // 목록 -->

    <!-- 조회 항목 상세설정 -->
    <b-modal v-model="cmfieldsetModal.show"
      centered no-close-on-backdrop
      title="조회 항목 상세설정"      
      size="lg"
      scrollable
    >
      <b-row>
        <b-col v-for="(fsgItem, idx) of cmfieldsetModal.data" :key="'fsg_'+idx"
          cols="3" md="3">
          <b-form-checkbox
            v-model="fsgItem.chAll"
            :name="'fsg_'+idx"
            @change="fsgToggleAll(fsgItem)"
          >{{fsgItem.name}}</b-form-checkbox>
        </b-col> 
      </b-row>      
      <hr/>
      <b-row>
        <b-col v-for="(fsgItem, idx) of cmfieldsetModal.data" :key="'fsg_'+idx"
          cols="3" md="3">
          <b-form-checkbox 
            v-for="(fsItem, idx) of fsgItem.list" :key="'fsg_'+idx+'_'+fsItem.key"
            v-model="fsItem.val"
            @change="fsToggle(fsgItem)"         
            :name="'fsg_'+idx+'_'+fsItem.key"
            class="mb-1"          
          >{{fsItem.label}}</b-form-checkbox>
        </b-col>
      </b-row>

      <template #modal-footer="{ ok, cancel, hide }">
        <b-button
          v-ripple.400="'rgba(113, 102, 240, 0.15)'"
          variant="outline-primary"
          class="mr-1"
          @click="cancel()"
        >취소</b-button> 
        <b-button
          v-ripple.400="'rgba(113, 102, 240, 0.15)'"
          variant="primary"
          @click="onClickCmfieldsetModalOk()"
        >확인</b-button>         
      </template>
    </b-modal>

    <!-- 연동정보 상세 -->
    <b-modal class="cmLinkStatusModal"
      v-model="cmLinkStatusModal.show"
      centered no-close-on-backdrop
      title="연동 정보 상세"      
      size="xl"
      
    >
      <LinkStatusView 
        :membSeq="cmLinkStatusModal.data.membSeq"
      ></LinkStatusView>
    </b-modal>
    <!-- fields:{{ list.fields }} -->
  </div>
</template>
<script>
import vSelect from 'vue-select';
import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive';



const DEF_SEARCH_OPT = {
  sc:{
    useYn         : 'Y',
    membNameL     : '',
    phoneL        : '',
    joinFromDate  : '',
    joinToDate    : '',
    rctFromDate   : '',
    rctToDate     : '',
  },
  
  pi:{
    curPage : 1,
    totalItem : 0,
    perPage : 15       
  }
};

const DEFAULT_MBMB_TABLE_COLUMNS = [
  { key: 'index'      , label: '번호'        , sortable: false , class: 'text-center' },
  { key: 'compSeq'    , label: '원료사일련번호', sortable: false , class: 'text-center' },
  { key: 'email'      , label: '메일주소'    , sortable: false , class: 'text-center' },
  { key: 'membName'   , label: '이름'        , sortable: false , class: 'text-center' },
  { key: 'phone'      , label: '연락처'      , sortable: false , class: 'text-center' },

  // { key: 'membBirth'  , label: '생년월일'    , sortable: false , class: 'text-center' },
];

const DEFAULT_BSN_TABLE_COLUMNS = [
  // { key: 'bsnSeq'         , label: '사업장일련번호'  , sortable: false , class: 'text-center' },
  
];


const DEFAULT_SCRAP_TABLE_COLUMNS = [
  
];

const DEFAULT_MB_ST_TABLE_COLUMNS = [
  { key: 'membEmulator'     , label: '에뮬레이터'      , sortable: false , class: 'text-center'},
  { key: 'joinDttm'         , label: '가입일자'        , sortable: false , class: 'text-center'},
  { key: 'rctAcsDttm'       , label: '최근접속일시'    , sortable: false , class: 'text-center'},
  { key: 'membWtdrDttm'     , label: '탈회일자'        , sortable: false , class: 'text-center'},
];

const DEFAULT_PUSH_TABLE_COLUMNS = [
  { key: 'allimYn'          , label: '알림동의'            , sortable: false , class: 'text-center'},
  // { key: 'mrkRcvYn'         , label: '마케팅수신동의'      , sortable: false , class: 'text-center'},
  // { key: 'salesAllimYn'     , label: '매출알림동의'        , sortable: false , class: 'text-center'},
  // { key: 'linkStatusAllimYn', label: '매출/지출 연결상태알림'  , sortable: false , class: 'text-center'},
  // { key: 'calErrAllimYn'    , label: '정산오류알림동의'    , sortable: false , class: 'text-center'},  
];

const DEFAULT_FIELD_SETTING = [
  { name:"사업자정보"       , val:true, list : [...DEFAULT_BSN_TABLE_COLUMNS]},
  { name:"연동정보"         , val:true, list : [...DEFAULT_SCRAP_TABLE_COLUMNS]},
  { name:"회원정보"         , val:true, list : [...DEFAULT_MB_ST_TABLE_COLUMNS]},
  { name:"마케팅/푸시동의"  , val:true, list : [...DEFAULT_PUSH_TABLE_COLUMNS]},
]

export default {
  name: 'coching-bo-memb-emulator-main',
  mixins: [ernsUtils],
  components : {
    vSelect, 
  },
  props: {},
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    } 
  },
  directives: {
    Ripple,
  },
  filters : {
    //주소
    fmtAddress(rowItem){
      if(!rowItem){return '-';}

      const zc = rowItem.bsnZipCode || '-';
      const a1 = rowItem.bsnAddress || '';
      const a2 = rowItem.bsnAddrDetail || '';

      if(zc == '-'){
        return `${a1} ${a2}`.trim();
      }
      return `(${zc}) ${a1} ${a2}`.trim();
    },
    //택스다이어트
    fmtTaxdietId(rowItem){
      if(!rowItem){return '-';}

      const tdId = rowItem.taxdietId || '';
      const cNo = rowItem.taxdietClientNo || '';
      const ld = rowItem.taxdietLinkDttm || '';

      if(cNo == '' ||  tdId == ''){
        return '미연동';
      }

      return `${tdId}`.trim();
    },
    //연동현황
    fmtScrapLinkStatus(rowItem){
      if(!rowItem){return '-';}

      const scdCnt = rowItem.scdLinkCnt || 0;
      const cardCnt = rowItem.cardLinkCnt || 0;
      const bankCnt = rowItem.bankLinkCnt || 0;
      const hometaxCnt = rowItem.hometaxLinkCnt || 0;
      const mallCnt = rowItem.mallLinkCnt || 0;
      const dappCnt = rowItem.dappLinkCnt || 0;

      const salesCnt = scdCnt + hometaxCnt + mallCnt + dappCnt;
      const costCnt = cardCnt + hometaxCnt;
      
      return `매출 (${salesCnt}) / 지출 (${costCnt}) / 계좌 (${bankCnt})`;
    },

    //업종 대분류
    fmtBizSctLargeCateNm(rowItem, rawCodeList){
      if(!rowItem || !rawCodeList){return '-';}

      const lCate = rowItem.bsnMainCate || '';
      if(!lCate){
        return '-';
      }

      const codeList = rawCodeList;
      const fCode = codeList.find(item=>item.lrgCode == lCate);
      if(!fCode){
        return '-';
      }

      return fCode.lrgNm;
    },

    //업종 중분류
    fmtBizSctMiddleCateNm(rowItem, rawCodeList){
      if(!rowItem || !rawCodeList){return '-';}

      const lCate = rowItem.bsnMainCate || '';
      if(!lCate){
        return '-';
      }

      const mCate = rowItem.bsnMidCate || '';
      if(!mCate){
        return '-';
      }

      const codeList = rawCodeList;
      const fCode = codeList.find(item=>item.lrgCode == lCate && item.mdlCode == mCate);
      if(!fCode){
        return '-';
      }

      return fCode.mdlNm;

    }

  },
  data(){
    const _vm = this;
    const fieldsetting = [
      ...DEFAULT_FIELD_SETTING
    ];
    const fields = _vm.getTableFieldInfo(fieldsetting);

    const fieldSetModalData = _vm.getfieldsetModalData(fieldsetting);

    return {
      code : {
        USER_LOG_TYPE : [],
        BSN_TYPE: [],
        RAW_BIZ_SCT : [],
        BIZ_SCT : [],
      },

      list :{
        sc:{...DEF_SEARCH_OPT.sc},

        pi:{...DEF_SEARCH_OPT.pi},

        fieldSetting : fieldsetting,

        fields :fields,

        rawList:[],
        dispList:[]
      },

      cmfieldsetModal :{
        show : false,

        data : [
          ...fieldSetModalData
        ]
      },

      cmLinkStatusModal : {
        show : false,

        data : {
          membSeq : 0,
        }
      }
    }
  },
  mounted(){
    const _vm = this;    
    _vm.loadCodes();

    _vm.getInitParam();
    _vm.loadList(_vm.list.pi.curPage);
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
          sc : _vm.list.sc,
          pi : _vm.list.pi
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
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam() {
      const _vm = this;
      //const query = _vm.$route.query, params = _vm.$route.params;
      const data = this.eumGetRouteHistoryParam();
      //console.info("onAfterRouter");console.info(data);
      if(data && data.initParam){
        _vm.list.sc = {...data.initParam.sc};
        _vm.list.pi = {...data.initParam.pi};
      }
    },

    //검색
    onSearch(){
      const _vm = this;  
      _vm.loadList(1);    
    },

    //검색 초기화
    onClickResetFilter(){
      const _vm = this;

      _vm.list.sc = {...DEF_SEARCH_OPT.sc};
      _vm.list.pi = {...DEF_SEARCH_OPT.pi};

      _vm.loadList(1);
    },

    //엑셀다운로드
    onClickExcelBtn(){
      const _vm = this;

      _vm.alertError("준비중 입니다.");
    },  

    //상세이동
    onClickListRow(item, index, event){
      const _vm = this;
      _vm.$router.push({ name: 'coching-bo-cm-user-mgr-detail', 
        query: {membSeq: item.membSeq}
      });
    },

    //연동 상세보기
    onClickLinkDetail(item){
      const _vm = this;

      _vm.cmLinkStatusModal.data.membSeq = item.membSeq;
      _vm.cmLinkStatusModal.show = true;
    },

    //PC 에뮬레이터
    async onClickPcEmulator(item){
      const _vm = this;

      try{
        _vm.loading(true);
        const emRes = await getBizMemberEmulatorPc({membSeq : item.membSeq});
        console.debug(emRes);

        const baseUrl = emRes.resultData.targetUrl;
        const wh = window.open(baseUrl, "emPc");

        const loginUrl = baseUrl + '/common/sns/login/emulator/callback.do';
        const emParams = {
          ...emRes.resultData
        };
        setTimeout(() => {
          //console.debug(emParams);
          _vm.post_to_url(loginUrl, emParams, "emPc", "post");

        }, 1000);
      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    post_to_url(path, params, target, method) {
      method = method || "post";

      target = target || null;
      var form = document.createElement("form");
      form.setAttribute("method", method);
      form.setAttribute("action", path);

      if (target != null)
        form.setAttribute("target", target);

        for (var key in params) {
          var hiddenField = document.createElement("input");
          hiddenField.setAttribute("type", "hidden");
          hiddenField.setAttribute("name", key);
          hiddenField.setAttribute("value", params[key]);
          form.appendChild(hiddenField);
      }

      document.body.appendChild(form);
      form.submit();

      return false;
    },

    //컬럼 설정(in 메인)
    onToggleFieldsInMain(fsgItem, index){
      const _vm = this;

      if(fsgItem.val){
        const refData = [
          { name:"사업자정보"       , list : [...DEFAULT_BSN_TABLE_COLUMNS]},
          { name:"연동정보"         , list : [...DEFAULT_SCRAP_TABLE_COLUMNS]},
          { name:"SNS회원정보"      , list : [...DEFAULT_MB_ST_TABLE_COLUMNS]},
          { name:"마케팅/푸시동의"  , list : [...DEFAULT_PUSH_TABLE_COLUMNS]},
        ];

        fsgItem.list.splice(0, fsgItem.list.length, ...refData[index].list);
      }else{
        fsgItem.list.splice(0, fsgItem.list.length);
      }

      _vm.list.fields.splice(0, _vm.list.fields.length, ..._vm.getTableFieldInfo(_vm.list.fieldSetting));
    },

    //컬럼 상세설정
    onClickSettingfields(){
      const _vm = this;

      const fieldsetModalData = _vm.getfieldsetModalData(_vm.list.fieldSetting);

      _vm.cmfieldsetModal.data = fieldsetModalData;
      _vm.cmfieldsetModal.show = true;
    },

    //컬럼 상세설정 ok
    onClickCmfieldsetModalOk(){
      const _vm = this;

      const userData = _vm.cmfieldsetModal.data;

      const ret = [];
      for(const gRef of userData){
        const addObj = {
          name : gRef.name,
          val : gRef.chAll,
          list : gRef.list.filter(item=>item.val)
        };
        ret.push(addObj);
      }

      _vm.list.fieldSetting = ret;
      _vm.list.fields.splice(0, _vm.list.fields.length, ..._vm.getTableFieldInfo(_vm.list.fieldSetting));
      _vm.cmfieldsetModal.show = false;
    },

    //표시할 컬럼정보 리턴
    getTableFieldInfo(fSetting){
      const _vm = this;

      const fSet = fSetting || _vm.list.fieldSetting;

      const ret = [...DEFAULT_MBMB_TABLE_COLUMNS];
      for(const fsg of fSet){
        //console.debug(fsg);
        ret.push(...fsg.list);
      }

      //console.debug(ret);
      return ret;
    }, 
    
    //조회상세설정 모달 데이터
    getfieldsetModalData(fSetting){
      const _vm = this;

      const fSet = fSetting || _vm.list.fieldSetting;
      const fSetAll = [];

      for(const cg of fSet){
        fSetAll.push(...cg.list);
      }

      const refData = [
        { name:"사업자정보"       , list : [...DEFAULT_BSN_TABLE_COLUMNS]},
        { name:"연동정보"         , list : [...DEFAULT_SCRAP_TABLE_COLUMNS]},
        { name:"회원정보"         , list : [...DEFAULT_MB_ST_TABLE_COLUMNS]},
        { name:"마케팅/푸시동의"  , list : [...DEFAULT_PUSH_TABLE_COLUMNS]},
      ];

      const ret = [];
      for(const gRef of refData){
        let fCnt = 0;
        const addObj = {
          name : gRef.name,
          chAll : false,
          val : false,
          list : gRef.list.map((item)=>{            
            const foundItem = fSetAll.find(cItem=>cItem.key == item.key);
            const selVal = foundItem ? true : false;
            fCnt += selVal ? 1 : 0;
            return {
              ...item,
              key : item.key,
              label : item.label,
              val : selVal,
            } 
          })
        };

        addObj.chAll = fCnt == addObj.list.length;
        addObj.val = fCnt > 0;
        ret.push(addObj);
      }

      return ret;
    },

    fsgToggleAll(fsgItem){
      const _vm = this;

      for(const fsItem of fsgItem.list){
        fsItem.val = fsgItem.chAll;
      }      
    },

    fsToggle(fsgItem){
      const _vm = this;

      let allCheck = true;
      for(const fsItem of fsgItem.list){
        if(!fsItem.val){
          allCheck = false;
          break;
        }
      }
      
      fsgItem.chAll = allCheck;
    },  

    //목록로드
    async loadList(pageNo){
      const _vm = this;

      const dataList = _vm.list, pInfo = _vm.list.pi, searchOp = _vm.list.sc;
      pInfo.curPage = pageNo || 1;

      try{
        const params = _vm.getListSearchParam();

        _vm.loading(true);
        const res = await getBizMemberMgrList(params);
        const pageInfo = res.resultData.pageInfo;
        dataList.rawList = res.resultData.list;
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

    async loadCodes(){
      const _vm = this;

      // const bizSctListRes = await getBizSectorList({rowSize:-1});
      // _vm.code.RAW_BIZ_SCT = bizSctListRes.resultData.list;
      // _vm.code.BIZ_SCT = _vm.code.RAW_BIZ_SCT.map(item=>{
      //   return {label:`${item.lrgNm} - ${item.mdlNm}`, value:`${item.lrgCode}_${item.mdlCode}`};
      // });
      

      // const retBoardMst = await getBoardMaster({boardMstId : _vm.list.sc.boardMstId});
      // _vm.boardMst = retBoardMst.resultData;

      // const categoryGroupCd = _vm.boardMst ? _vm.boardMst.cateCd : null;
      // if(categoryGroupCd){
      //   const cateList = await getCodes({codeMst:categoryGroupCd, rowSize:-1});
      //   _vm.codes.CATE = _vm.eumConvertToVueSelectOption(cateList.resultData.list);
      // }
    },

    getListSearchParam() {
      const _vm = this;

      const pInfo = _vm.list.pi, searchOp = _vm.list.sc;

      const params = {
        page : pInfo.curPage,
        rowSize : pInfo.perPage,

        //회원기본정보
        useYn         : searchOp.useYn,
        membNameL     : searchOp.membNameL,
        phoneL        : searchOp.phoneL,
        joinFromDate  : searchOp.joinFromDate,
        joinToDate    : searchOp.joinToDate,
        rctFromDate   : searchOp.rctFromDate,
        rctToDate     : searchOp.rctToDate,

        bsnLicNumL    : searchOp.bsnLicNumL,
        bsnNmL        : searchOp.bsnNmL,
        repNmL        : searchOp.repNmL,
        bsnCate       : searchOp.bsnCate,
        
      }

      if(searchOp.bsnCate){
        params["bsnMainCate"] = searchOp.bsnCate.split('_')[0];
        params["bsnMidCate"]  = searchOp.bsnCate.split('_')[1];
      }
      
      return params;
    },
  }  
}
</script>

<style lang="scss" scoped>
.cmLinkStatusModal{
  max-height: calc(90vh);
}
</style>