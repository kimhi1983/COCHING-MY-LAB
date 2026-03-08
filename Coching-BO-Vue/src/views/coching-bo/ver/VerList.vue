<template>
  <div>
    <!-- 검색조건 -->

    <b-card no-body>
      <b-card-body>
        <b-row>
          <b-col cols="3">
            <b-form-group label="플랫폼" label-for="ver-verData-sc-fpCode">
              <v-select
                id="ver-verData-sc-fpCode"
                v-model="verData.sc.fpCode"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                label="text"
                value="value"
                :options="fps"
                :reduce="op => op.value"
                @input="loadList(1)"
              />
            </b-form-group>
          </b-col>

          <b-col cols="3">
            <b-form-group
              label="업데이트 일시"
              label-for="ver-verData-sc-updateDate"
            >
              <b-input-group id="ver-verData-sc-updateDate">
                <b-form-datepicker
                  v-model="verData.sc.updateFromDate"
                  :date-format-options="{
                    year: 'numeric',
                    month: 'numeric',
                    day: 'numeric',
                  }"
                  placeholder="YYYY-MM-DD"
                  locale="ko"
                  today-button
                  label-today-button="오늘"
                  reset-button
                  label-reset-button="초기화"
                  close-button
                  label-close-button="닫기"
                  @input="onSearch"
                />
                <b-form-datepicker
                  v-model="verData.sc.updateToDate"
                  :date-format-options="{
                    year: 'numeric',
                    month: 'numeric',
                    day: 'numeric',
                  }"
                  placeholder="YYYY-MM-DD"
                  locale="ko"
                  today-button
                  label-today-button="오늘"
                  reset-button
                  label-reset-button="초기화"
                  close-button
                  label-close-button="닫기"
                  @input="onSearch"
                />
              </b-input-group>
            </b-form-group>
          </b-col>


          <b-col cols="3">
            <b-form-group
              label="필수업데이트 여부"
              label-for="ver-verData-sc-reqYn"
            >
              <b-form-radio-group
                id="ver-verData-sc-reqYn"
                v-model="verData.sc.reqYn"
                :options="[{text:'전체', value:''},{text:'필수', value:'Y'}, {text:'선택', value:'N'}]"
                button-variant="outline-primary"
                buttons
                name="radio-btn-outline"
                @input="onSearch"
              />
            </b-form-group>
          </b-col>

          <b-col cols="6" md="6">
            <b-form-group label="적용 내역" label-for="ver-verData-sc-content">
              <b-input-group id="user-verData-sc-content">
                <b-form-input
                  id="ver-verData-sc-content"
                  v-model="verData.sc.updateContent"
                  @keyup.enter="loadList(1)"
                  placeholder="적용 내역을 입력하세요."
                ></b-form-input>
                <b-input-group-append>
                  <b-button variant="outline-primary" @click="loadList(1)"
                    >검색</b-button
                  >
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>


          <b-col cols="12" md="6" >
            <div class="erns-filter-div mt-2 text-right">
              <b-form-group label="">
                <b-button
                  variant="outline-primary"
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  class="mr-1"
                  @click="loadList(1)"
                  >검색</b-button
                >
                <b-button
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="outline-secondary"
                  class="mr-1"
                  @click="onClickResetFilter"
                  >초기화</b-button
                >
                <b-button
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="primary"
                  class="mr-1"
                  @click="onClickVerModifyModal()"
                  >등록</b-button
                >
              </b-form-group>
            </div>
          </b-col>
        </b-row>

      </b-card-body>
    </b-card>
    <!-- // 검색조건 -->

    <!-- 목록 -->
    <b-card no-body class="mb-0">
      <b-table
        responsive
        striped
        hover
        :items="verData.rawList"
        :fields="fields"
        ref="boardListTable"
        primary-key="verSeq"
        show-empty
        empty-text="데이터가 없습니다."
        @row-clicked="onClickVerModifyModal"
      >
        <template #cell(index)="data">
          {{ data.index | eufRowNumberForPageInfo(verData.pi) }}
        </template>

        <template #cell(fpCode)="data">
          {{ data.item.fpCode | eufBlankToDash }}
        </template>

        <template #cell(fpName)="data">
          {{ data.item.fpName }}
        </template>

        <template #cell(reqYn)="data">
          {{ data.item.reqYn | reqYnName }}
        </template>
      </b-table>

      <div class="mx-2 mb-2">
      <!-- /목록 -->
        <b-row>
          <b-col
            cols="12"
            sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-start"
          >
            <v-select
              v-model="verData.pi.perPage"
              :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
              :options="showRows"
              :clearable="false"
              class="per-page-selector d-inline-block mx-50"
              @option:selected="loadList(1)"
            />
            <!-- 페이지당 load할 row수 정하기 -->
            <span class="text-muted">
              {{ eumPaginationRangeForPageInfo(verData.pi) }}
            </span>
          </b-col>

          <!-- 페이지 이동 하는 부분 -->
          <b-col
            cols="12"
            sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
            <b-pagination
              v-if="verData.pi.totalItem > 0"
              v-model="verData.pi.curPage"
              :total-rows="verData.pi.totalItem"
              :per-page="verData.pi.perPage"
              first-number
              last-number
              limit="10"
              class="mb-0 mt-1 mt-sm-0"
              prev-class="prev-item"
              next-class="next-item"
              @change="loadList"
            >
              <template #prev-text>
                <feather-icon icon="ChevronLeftIcon" size="18" />
              </template>
              <template #next-text>
                <feather-icon icon="ChevronRightIcon" size="18" />
              </template>
            </b-pagination>
          </b-col>
        </b-row>
      </div>
    </b-card>

    <!-- 수정모달2-->
    <b-modal
      v-model="verDetail.show"
      centered
      no-close-on-backdrop
      :title="modalTitle"
      size="lg"
      ref="verModal"
    >
      <b-card id="ver-form">
        <b-card no-body class="col-md-12" title="플랫폼">
          <validation-observer ref="simpleRules">

            <b-row>

              <b-col cols="6" class="mt-2">
                <b-form-group label="플랫폼" label-for="ver-fpCode">
                  <validation-provider #default="{ errors }"
                    name="'플랫폼'"
                    rules="required"
                  >
                    <v-select
                      id="ver-fpCode"
                      v-model="verDetail.raw.fpCode"
                      :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                      label="text"
                      value="value"
                      :options="fps"
                      :reduce="label => label.value"
                    />
                    <small class="text-danger">{{ errors[0] }}</small>
                  </validation-provider>
                </b-form-group>
              </b-col>

              <b-col cols="6" class="mt-2">
                <b-form-group label="버전이름" label-for="ver-verCode">
                  <validation-provider #default="{ errors }"
                    name="'버전이름'"
                    rules="required"
                  >
                    <b-form-input
                      id="ver-verCode"
                      placeholder="버전이름을 입력하세요."
                      v-model="verDetail.raw.verName"
                    />
                    <small class="text-danger">{{ errors[0] }}</small>
                  </validation-provider>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col cols="6" class="mt-2">
                <b-form-group label="버전코드" label-for="ver-verCode">
                  <validation-provider #default="{ errors }"
                    name="'버전코드'"
                    rules="required"
                  >
                    <b-form-input
                      id="ver-verCode"
                      placeholder="버전코드를 입력하세요."
                      v-model="verDetail.raw.verCode"
                    />
                    <small class="text-danger">{{ errors[0] }}</small>
                  </validation-provider>
                </b-form-group>
              </b-col>

              <b-col cols="6" class="mt-2">
                <b-form-group label="빌드버전" label-for="ver-buildVer">
                  <validation-provider #default="{ errors }"
                    name="'빌드버전'"
                    rules="required"
                  >
                    <b-form-input
                      id="ver-buildVer"
                      placeholder="빌드버전을 입력하세요."
                      v-model="verDetail.raw.buildVer"
                    />
                    <small class="text-danger">{{ errors[0] }}</small>
                  </validation-provider>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col cols="6" class="mt-2">
                <div class="calendar-container">
                  <b-form-group label="업데이트 일시" label-for="ver-updateDate">
                    <b-input-group id="payment-list-trdDate">
                      <b-form-datepicker
                      id="ver-updateDate"
                        v-model="verDetail.raw.updateDate"
                        :date-format-options="{
                          year: 'numeric',
                          month: 'numeric',
                          day: 'numeric',
                        }"
                        placeholder="YYYY-MM-DD"
                        locale="ko"
                        today-button
                        label-today-button="오늘"
                        reset-button
                        label-reset-button="초기화"
                        close-button
                        label-close-button="닫기"
                      />
                    </b-input-group>
                  </b-form-group>
                </div>
              </b-col>

              <b-col cols="" class="mt-2">
                <b-form-group label="필수업데이트 여부" label-for="ver-reqYn">
                  <b-form-radio-group
                    id="ver-reqYn"
                    v-model="verDetail.raw.reqYn"
                    :options="reqYn"
                    button-variant="outline-primary"
                    buttons
                    name="radio-btn-outline"
                  />
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col cols="12" class="mt-2">
                <b-form-group label="적용 내역" label-for="ver-updateContent">
                  <b-form-textarea
                    rows="5"
                    id="ver-updateContent"
                    placeholder="적용 내역을 입력하세요."
                    v-model="verDetail.raw.updateContent"
                  />
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col md="6" v-if="!isRegMode">
                <label>등록일 : {{ verDetail.raw.rgtDttm | eFmtDateTime }}</label>
                <br/>
                <label>최종수정일 : {{ verDetail.raw.chngDttm | eFmtDateTime }}</label>
              </b-col>
            </b-row>
          </validation-observer>
        </b-card>
      </b-card>

      <template #modal-footer="{ }" >
        <b-button
          v-ripple.400="'rgba(113, 102, 240, 0.15)'"
          variant="outline-primary"
          class="mr-1"
          @click="verDetail.show = false"
          >취소</b-button
        >
        <b-button
          v-ripple.400="'rgba(113, 102, 240, 0.15)'"
          variant="primary"
          @click="onClickSave"
          >저장</b-button
        >
      </template>
    </b-modal>
    <!-- /수정모달2-->
  </div>
</template>

<script>
import ernsUtils from "@/components/mixins/ernsUtils";
import {
  getVerList,
  updateVersion,
  insertVersion,
} from "@/api/coching-bo/comm/ver";

import Ripple from "vue-ripple-directive";
import vSelect from "vue-select";
import { required } from '@validations';

const DEF_PARAM = {
  useYn: "Y",
  delYn: "N",
  notUsedYn: "N",
};

const DEF_FIELD_SETTING = [
  { key: "index",         label: "번호",          sortable: false, class: "text-center", thStyle: { width: "5%" } },
  // {  key: 'fpCode'       , label: '플랫폼 코드'     , sortable: false , class: 'text-center' },
  { key: "updateDate",    label: "업데이트 일시", sortable: false, class: "text-center", thStyle: { width: "10%"} },
  { key: "fpName",        label: "플랫폼",        sortable: false, class: "text-center", thStyle: { width: "10%" } },
  { key: "buildVer",      label: "빌드 버전",     sortable: false, class: "text-center", thStyle: { width: "7%" } },
  { key: "verCode",       label: "버전 코드",     sortable: false, class: "text-center", thStyle: { width: "7%" } },
  { key: "verName",       label: "버전이름",      sortable: false, class: "text-center", thStyle: { width: "7%" } },
  { key: "reqYn",         label: "필수업데이트 ", sortable: false, class: "text-center", thStyle: { width: "7%" } },
  // {  key: 'verSeq'       , label: '버전seq'         , sortable: false , class: 'text-center'},
  { key: "updateContent", label: "적용 내역",     sortable: false, thClass: "text-center", thStyle: { width: "*" }  },
];

  const DEF_FP = [
    { text: "전체", value:''},
    { text: "Android",  value:'1'},
    { text: "IOS",      value:'2'},
  ];
const DEF_SEARCH_OPT = {
  sc: {
    verSeq: "",
    fpCode: "",
    reqYn: [],
    updateFromDate: "",
    updateToDate: "",
    updateDate: "",

    rgtr: "",
    rgtDttm: "",
    chnr: "",
    chngDttm: "",
  },

  pi: {
    curPage: 1,
    totalItem: 0,
    perPage: 15,
  },
};
const DEF_REQ = [
  // { text: "전체",     value: '' },
  { text: "필수",     value: 'Y' },
  { text: "선택",   value: 'N' },
];


const DEF_NULL_VALUE = {
  verSeq: "",
  buildVer: "",
  fpCode: "",
  fpName: "",
  verCode: "",
  verName: "",
  chngDttm: "",
  reqYn: "",
  updateDate: "",
  updateContent: "",
  rgtr: "",
  rgtDttm: "",
  chnr: "",
  rgtDttm: "",
};

export default {
  name: "VerList",
  mixins: [ernsUtils],
  components: {
    vSelect,
    required
  },
  directives: {
    Ripple,
  },
  props: {},
  computed: {
    showRows() {
      return this.$store.state.erns.showRows;
    },
    isRegMode() {
      const params = this.verDetail.raw;
      if (!params.hasOwnProperty("verSeq")) {
        return true;
      }
      const verSeq = params.verSeq;
      return !(verSeq && verSeq > 0);
    },
  },
  filters: {
    reqYnName(val) {
      return val == "Y" ? "필수" : val == "N" ? "-" : "-";
    },
  },

  data() {
    const _vm = this;

    return {
      modalTitle: '버전 업데이트',
      params: { ...DEF_PARAM },
      selected :'',
      fps: [...DEF_FP],
      fields: [...DEF_FIELD_SETTING],
      reqYn: {...DEF_REQ},

      verData: {
        sc: { ...DEF_SEARCH_OPT.sc },
        pi: { ...DEF_SEARCH_OPT.pi },
        rawList: [],
        dispList: [],
        ref: [...DEF_REQ],
      },
      //version data modal에 출력
      verDetail: {
        show: false,
        raw: [],
      },
    };
  },
  mounted() {
    const _vm = this;
    _vm.getInitParam();
    _vm.loadList(_vm.verData.pi.curPage);
  },
  beforeDestroy() {
    const _vm = this;
  },
  //페이지 이동전 처리
  beforeRouteLeave: function (to, from, next) {
    const _vm = this;
    try {
      const data = {
        initParam: {
          sc: _vm.verData.sc,
          pi: _vm.verData.pi,
        },
      };
      _vm.eumSetRouteHistoryParam(data);
    } catch (err) {
      console.error(err);
    }
    return next();
  },
  methods: {
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam() {
      const _vm = this;
      const query = _vm.$route.query, params = _vm.$route.params;

      _vm.params = {
        ...query
      };
    },

    //검색
    onSearch() {
      const _vm = this;
      _vm.loadList(1);
    },

    //검색 초기화
    onClickResetFilter() {
      const _vm = this;
      _vm.verData.sc = { ...DEF_SEARCH_OPT.sc };
      _vm.verData.pi = { ...DEF_SEARCH_OPT.pi };
      _vm.loadList(1);
    },

    //목록 로드
    async loadList(pageNo) {
      const _vm = this;

      const dataList = _vm.verData, pInfo = _vm.verData.pi, searchOp = _vm.verData.sc;

      pInfo.curPage = pageNo || 1;

      _vm.loading(true);
      try {

        const params = _vm.getListSearchParam();
        //데이터 로드
        const res = await getVerList(params);
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
    //SearchParam 입력
    getListSearchParam() {
      const _vm = this;

      const pInfo = _vm.verData.pi, searchOp = _vm.verData.sc;

      const params = {
        page: pInfo.curPage,
        rowSize: pInfo.perPage,
        fpCode: searchOp.fpCode,
        updateFromDate: searchOp.updateFromDate,
        updateToDate: searchOp.updateToDate,
        updateContent: searchOp.updateContent,
      };

      if (searchOp.reqYn.length <= 0) {
        params.reqYn = "";
      } else {
        params.reqYn = searchOp.reqYn[0];
      }
      return params;
    },

    // 등록 + 수정모달
    onClickVerModifyModal(item) {
      const _vm = this;
      const modal = _vm.$refs.verModal.$el;

      try {
        _vm.loading(true);

        _vm.verDetail.raw = item == null ? JSON.parse(JSON.stringify({...DEF_NULL_VALUE})) : JSON.parse(JSON.stringify(item));
        item == null ? _vm.updateModalTitle('버전 등록') : _vm.updateModalTitle('버전 수정');
        _vm.verDetail.show = true;
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },
    updateModalTitle(newTitle) {
      this.modalTitle = newTitle;
    },

    //수정,등록 api 호출
    async onClickSave() {
      const _vm = this;

      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid) {
        return;
      }

      const isRegMode =_vm.verDetail.raw.verSeq == null || _vm.verDetail.raw.verSeq == "";

      try {
        _vm.loading(true);

        { //플렛폼 네임
          _vm.verDetail.raw.fpName = _vm.verDetail.raw.fpCode == "1" ? "Android" : 
            (_vm.verDetail.raw.fpCode == "2" ? "IOS" : "기타")
        }

        console.debug(_vm.verDetail);

        if(isRegMode){
          await insertVersion(_vm.verDetail.raw);
        }else{
          await updateVersion(_vm.verDetail.raw);
        }

        _vm.loadList(1);
        await _vm.alertSuccess(`${isRegMode ? '등록' : '수정'} 되었습니다`);
        _vm.verDetail.show = false;
      } catch(err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

  },
};
</script>

<style lang="scss" scoped>
#ver-updateContent {
  resize: none;
}
</style>
