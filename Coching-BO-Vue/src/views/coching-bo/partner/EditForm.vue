<template>
  <div class="sys-mid-wrap">
    
    <b-card no-body>
      <validation-observer ref="partnerInfoForm">
        <!-- 입력 영역 -->
        <b-card-group class="mt-1">
          <b-card title="원료사정보" class="col-md-12">
              <b-row>

                <b-col md="6" ref="ptnName" class="ptnName_under">
                  <b-form-group label-for="ptnName">
                    <template v-slot:label>
                        <span>회사명</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'회사명'"
                      rules="required"
                    >
                      <b-form-input
                        id="ptnName"
                        v-model="localData.ptnInfo.ptnName"
                        :state="errors.length > 0 ? false:null"
                        placeholder="원료사명을 입력하세요."                      
                        autoptnlete="off"
                      />                    
                    </validation-provider>               
                  </b-form-group>
                </b-col>

                <b-col md="6">
                  <b-form-group label-for="ptnLic"
                    v-if="localData.ptnInfo.nation == '001'"
                  >
                    <template v-slot:label>
                        <span>사업자번호</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'사업자 번호'"
                      rules="required|min:10|max:10"
                    >
                      <b-form-input
                        ref="ptnLicInput"
                        id="ptnLic"
                        v-model="localData.ptnInfo.ptnLic"
                        :state="errors.length > 0 ? false:null"
                        placeholder="‘-’없이 입력하세요."                      
                        autoptnlete="off"
                      />                    
                      <small class="text-danger">{{ errors[0] }}</small>
                    </validation-provider>                
                  </b-form-group>
                </b-col>

                <b-col md="6" style="margin-top:10px">
                  <b-form-group label-for="pageUrl">
                    <template v-slot:label>
                        <span>홈페이지 URL</span>
                        <span class="text-pink">*</span>
                      </template>
                    <validation-provider #default="{ errors }"
                    name="'홈페이지 URL'"
                      rules=""
                      >
                      <b-form-input
                        id="pageUrl"
                        v-model="localData.ptnInfo.pageUrl"
                        :state="errors.length > 0 ? false:null"
                        placeholder="http://www.coching.co.kr"                      
                        autoptnlete="off"
                      />                    
                      <small class="text-danger">{{ errors[0] }}</small>
                    </validation-provider>                
                  </b-form-group>
                </b-col>
                <b-col md="3" style="margin-top:10px">
                  <b-form-group label-for="nation">
                    <template v-slot:label>
                        <span>원료사 구분</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'원료사 구분'"
                      rules="required"
                    >
                      <v-select
                        id="nation"
                        v-model="localData.ptnInfo.nation"
                        :options="codes.NATION"
                        :reduce="option => option.value"
                        placeholder="원료사 구분을 선택하세요"
                        class="w-100"
                        :state="errors.length > 0 ? false:null"
                      >
                        <span slot="no-options">검색된 항목이 없습니다.</span>
                      </v-select>
                      <small class="text-danger">{{ errors[0] }}</small>
                    </validation-provider>                
                  </b-form-group>
                </b-col>
                <b-col md="3" style="margin-top:10px">
                  <b-form-group label-for="recruitYn"
                    v-if="localData.ptnInfo.nation == '002'"
                  >
                    <template v-slot:label>
                        <span>대리점 모집</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'대리점 모집'"
                      rules=""
                    >
                      <b-form-radio-group
                        id="recruit-yn"
                        v-model="localData.ptnInfo.recruitYn"
                        :options="[
                          { text: '모집중', value: 'N' },
                          { text: '모집완료', value: 'Y' },
                        ]"
                        button-variant="outline-primary"
                        buttons
                        name="radio-btn-recruit"
                      />
                      <small class="text-danger">{{ errors[0] }}</small>
                    </validation-provider>                
                  </b-form-group>
                </b-col>
               
              </b-row>
          </b-card>

        </b-card-group>
        <!-- // 입력 영역 -->

        <!-- 버튼 영역 -->
        <b-card-body>
          <div class="text-center mt-1">
            <b-button
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="primary"
              class="mr-1"
              @click.prevent="onClickUpdate"
            > 수정
            </b-button>
            <b-button
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="outline-primary"
              @click="onClickCancel"
            > 취소
            </b-button>
          </div>
        </b-card-body>
        <!-- // 버튼 영역 -->

        <b-card-group class="mt-4">
          <b-card class="mb-2">
            <b-table
              class="position-relative" 
              responsive 
              striped 
              hover
              :items="localData.memberList.rawList"
              :fields="localData.memberList.fields"
              ref="memberListTable"
              no-local-sorting
              primary-key="membSeq"
              show-empty
              empty-text="검색된 내용이 없습니다."
            >

              <template #cell(index)="data">
                {{ data.index + 1 }}
              </template>

              <template #cell(useYn)="data">
                <b-form-radio-group
                  id="ua-form-use-yn"
                  v-model="data.item.useYn"
                  :options="[
                    { text: '사용', value: 'Y' },
                    { text: '미사용', value: 'N' },
                  ]"
                  button-variant="outline-primary"
                  buttons
                  name="radio-btn-outline"
                  @change="onChangeUseYn(data.item)"
                />
              </template>
              
              <template #cell(rgtDttm)="data">
                {{ data.item.rgtDttm  | eFmtDateTime('-')}}
              </template>
            </b-table>
            <div class="mx-2 mb-2">
              <b-row>
                <b-col cols="12" sm="6"
                  class="d-flex align-items-center justify-content-center justify-content-sm-start"
                >
                  <v-select
                    v-model="localData.memberList.pi.perPage"
                    :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                    :options="showRows"
                    :clearable="false"
                    class="per-page-selector d-inline-block mx-50"
                    @option:selected="loadMembList(1)"
                  />

                  <span class="text-muted">
                    {{eumPaginationRangeForPageInfo(localData.memberList.pi)}}
                  </span>
                </b-col>

                <b-col cols="12" sm="6"
                  class="d-flex align-items-center justify-content-center justify-content-sm-end"
                >
                  <b-pagination
                    v-if="localData.memberList.pi.totalItem > 0"
                    v-model="localData.memberList.pi.curPage"
                    :total-rows="localData.memberList.pi.totalItem"
                    :per-page="localData.memberList.pi.perPage"
                    first-number
                    last-number
                    limit = "10"
                    class="mb-0 mt-1 mt-sm-0"
                    prev-class="prev-item"
                    next-class="next-item"
                    @change="loadMembList"
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
        </b-card-group>

      </validation-observer>
    </b-card>
    

    <!-- ptnInfo:{{localData.ptnInfo}}<br/> -->
    <!-- membInfo:{{localData.memberInfo}}<br/>  -->

  </div>
</template>
<script>
import vSelect from 'vue-select';
import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive'

import { getPartner, getMembList, updatePartner } from '@/api/coching-bo/partner/partner'; 
import { setUseYn } from '@/api/coching-bo/member/member';
import { getCodes } from '@/api/coching-bo/comm/code';

import { extend } from 'vee-validate';
import { required, passwordCochingJoin, noWhiteSpace, min, max, telOnlyNumber, email } from '@validations';

const DEF_PARTNER_INF= {
  ptnSeq: '',
  ptnName: '',
  ptnLic: '',
  pageUrl: '',
  delYn: 'N',
  
  nation: '',
  recruitYn: 'N',
};

const DEF_MEMB_INF = {
    membSeq: null,
    email: '',
    membName: '',
    pswd: '',
    pswdConfirm: '',
    phone: '',
    department: '',
    auth: '',
};

const DEFAULT_MEMB_TABLE_COLUMNS = [
  { key: 'index'      , label: '번호'           , sortable: false , class: 'text-center' },
  { key: 'membId'      , label: '아이디'    , sortable: false , class: 'text-center'},
  { key: 'membName'   , label: '이름'           , sortable: false , class: 'text-center' },
  { key: 'useYn'       , label: '사용여부'      , sortable: false , class: 'text-center' },
  { key: 'rgtDttm'    , label: '가입일'         , sortable: false , class: 'text-center' },
];

export default {
  name: 'coching-bo-partner-editForm',
  mixins: [ernsUtils],
  components : {
    vSelect,
  },
  directives: {
    Ripple
  },
  computed : {

    showRows(){
      var _vm = this;
      return _vm.$store.state.erns.showRows;
    },
    isptnListVisible() {
      var _vm = this;
      return _vm.ptnFilterList.length > 0 && _vm.localData.ptnInfo.ptnSeq === '' && _vm.isFocus;
    },
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',

    // 원료사 구분 변경 시 다른 필드 초기화
    'localData.ptnInfo.nation'(newValue, oldValue) {
      const _vm = this;
      
      if (newValue === '002') {
        // 해외 원료사로 변경 시 사업자번호 초기화
        _vm.localData.ptnInfo.ptnLic = '';
      } else if (newValue === '001') {
        // 국내 원료사로 변경 시 대리점 모집여부를 'N'으로 초기화
        _vm.localData.ptnInfo.recruitYn = 'N';
      }
    },

  },
  data(){
      return {
        isFocus: false,
        
        localData: {
          memberList: {
            sc: {
              ptnSeq: "",
              sortBy: "membName",
              sortDesc: true,
            },
            pi: {
              // 페이지네이션
              curPage: 1,
              totalItem: 0,
              perPage: 5,
            },

            fields: [...DEFAULT_MEMB_TABLE_COLUMNS],
            rawList: [],
          },
          ptnInfo: {...DEF_PARTNER_INF},
        },
        codes: {
          GROUP_CODE: [],
          NATION: [],
        },

        checkptnMsg : '',
        ptnList: [],
        ptnFilterList: [],

        emailList:[],
      }
  },
  async mounted(){
    const _vm = this;

    await _vm.loadCodes();
    await _vm.fetchData();
  
  },
  beforeDestroy(){
    const _vm = this;
  },
  methods: {

    async fetchData(){
      const _vm = this;

      _vm.loading(true);   

      try{
        //원료사 상세
        const res = await getPartner({ptnSeq: _vm.$route.query.ptnSeq});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.localData.ptnInfo = {...DEF_PARTNER_INF, ...resultData};

        //멤버 리스트
        _vm.loadMembList(1);
        
      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }

    },

    //취소
    onClickCancel(){
      const _vm = this;
      _vm.$router.back();
    },

    async loadCodes(){
      const _vm = this;
      
      const nationList = await getCodes({grpCode:'CH007', etc5: 'KO', rowSize:-1});
      _vm.codes.NATION = _vm.eumConvertToVueSelectOption(nationList.resultData.list);
    },

    //가입계정 목록 로드
    async loadMembList(pageNo) {
      const _vm = this;
      const dataList = _vm.localData.memberList,
            pInfo = _vm.localData.memberList.pi,
            searchOp = _vm.localData.memberList.sc;
      pInfo.curPage = pageNo || 1;

      const params = _vm.getListSearchParam(_vm.localData.memberList);
      params.ptnSeq = _vm.$route.query.ptnSeq;

      try {
        _vm.loading(true);

        const res = await getMembList(params);

        const pageInfo = res.resultData.pageInfo;
        dataList.rawList = res.resultData.list;
        dataList.pi = {
          ...dataList.pi,
          curPage: pageInfo.currentPage,
          totalItem: pageInfo.totalItem,
          perPage: pageInfo.pageItemSize,
        };
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    getListSearchParam(LIST) {
      const _vm = this;

      const pInfo = LIST.pi;
      const searchOp = LIST.sc;

      return {
        page : pInfo.curPage,
        rowSize : pInfo.perPage,

        useYn : "", //관리자니까 모두 가져오기
        allSrch : searchOp.allSrch,
        ptnSeq : searchOp.ptnSeq,
      };
    },

    setptn(val){
      var _vm = this;
      _vm.localData.ptnInfo.ptnSeq = val;
      
      const selptn = _vm.ptnList.find(elem=>elem.ptnSeq == _vm.localData.ptnInfo.ptnSeq);
      _vm.localData.ptnInfo = {..._vm.localData.ptnInfo, ...selptn};
      _vm.isFocus = false;
      
    },

    setParamData(){
      const _vm = this;
      const param = {
              ..._vm.localData.ptnInfo
            };

      return param; 
    },

    async onClickUpdate() {
      const _vm = this;

      const partner = _vm.localData.ptnInfo;

      const isValid = await _vm.$refs.partnerInfoForm.validate();
      if(!isValid){
        return;
      }

      // ptnList에서 선택 시 해당 ptnName의 ptnLic이 맞는지
      if (partner.ptnName && partner.ptnLic) {
        for (const ptn of _vm.ptnList) {
          if (ptn.ptnName === partner.ptnName && ptn.ptnLic !== partner.ptnLic) {
            await _vm.alertError(`해당 원료사의 사업자번호가 아닙니다.`);
            return;
          }
        }
      }

      try {
      const result = await _vm.$swal({
          title: '원료사 정보',
          text: '수정 하시겠습니까?',
          showCancelButton: true,
          confirmButtonText: '수정',
          customClass: {
            confirmButton: 'btn btn-danger ml-1',
            cancelButton: 'btn btn-outline-primary',
          },
          buttonsStyling: false,
        });

        if(!result.value) {
          return;
        }

        _vm.loading(true);
        const param = _vm.setParamData();        

        const res = await updatePartner(param);
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(resultCode == '0000'){
         await _vm.alertSuccess('수정되었습니다.','원료사 정보');

          _vm.$router.push({ name: 'coching-bo-partner-main'});
          return;
        }

      } catch(err) {
        console.error(err);
      } finally {
        _vm.loading(false);
      }
    },
    
    //계정 사용여부 변경
    async onChangeUseYn(item){
      const _vm = this;

      const result = await _vm.$swal({
          title: '사용 여부',
          text: '변경 하시겠습니까?',
          showCancelButton: true,
          confirmButtonText: '변경',
          customClass: {
            confirmButton: 'btn btn-danger ml-1',
            cancelButton: 'btn btn-outline-primary',
          },
          buttonsStyling: false,
        });

        if(!result.value) {
          if(item.useYn === 'Y'){
            item.useYn = 'N';
          }else{
            item.useYn = 'Y';
          }
          return;
        }

        _vm.loading(true);
        try {
            const param = _vm.setUseYnParam(item);
            const res = await setUseYn(param);
            const { resultCode, resultFailMessage, resultData } = res;
            _vm.loading(false);

            await _vm.alertSuccess('변경되었습니다.','사용 여부');
            return;

        } catch(err) {
          console.error(err);
        } finally {
          _vm.loading(false);
        }
    },

    setUseYnParam(item){
      const _vm = this;
      
      return {
        membSeq: item.membSeq,
        useYn: item.useYn
      };
    },
  }
}
</script>

<style lang="scss" scoped>
.ptnName_under {
  position: relative;

  .ptnName_error {
    position: absolute;
    top: 0;
    left: 0;
    color: blue;
  }

  .ptnName_list {
    position: absolute;
    top: calc(100% + 20px);
    left: 0;
    width: 100%;
    min-height: 100px;
    max-height: 100px;
    overflow-y: scroll;
    z-index: 1; 
    border: 1px solid #ccc; 
    background-color: #fff;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); 
  }

  .auto-box-content {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      padding: 10px;
      border-bottom: 1px solid #eee;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f5f5;
        cursor: pointer;
      }

      a {
        color: #333;
        text-decoration: none;
        font-weight: bold;

        &:hover {
          color: #e44d26;
        }
      }
    }
  }
}

.fileInput{
  background-color: #edecfc;
  border: 0;
  border: 1px dotted #7367f0;
  height: inherit;
  line-height: 200px;
  text-align: center;
  font-size: 50px;
  color: #7367f0;
  padding: 0;
}

.fileInput:hover{
  background-color: #e2e0ff;
  cursor: pointer;
}

.imgs:hover {
  cursor: pointer;
}
</style>

<style lang="scss">
</style>