<template>
  <div class="sys-board-master-wrap">
    <b-card title="게시판 정보">
      <validation-observer ref="simpleRules">
        <b-form>
          <b-row>
            <b-col md="6">
              <b-form-group label="게시판 아이디"
                label-for="sys-bm-form-boardMstId">
                <validation-provider #default="{ errors }"
                  name="'게시판 아이디'"
                  rules="required"
                >
                  <b-input-group>
                    <b-form-input
                      id="sys-bm-form-boardMstId"
                      v-model="detail.boardMstId"
                      :readonly="!isRegMode"
                      :state="errors.length > 0 ? false:null"
                      placeholder="게시판 아이디를 입력하세요."
                      @change="isCheckedBoardMstIdOverlap=false"
                      :formatter="(val)=>{return eufmtToUpperMax(val, 30)}"
                      autocomplete="off"
                    />
                    <b-input-group-append v-if="isRegMode">
                      <b-button variant="outline-primary"
                        :disabled="errors.length > 0"
                        @click.prevent="onClickOverlapBoardMstId"
                      >중복확인</b-button>
                    </b-input-group-append>
                  </b-input-group>
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col md="6">
              <b-form-group label="게시판명"
                label-for="sys-bm-form-boardName">
                <validation-provider #default="{ errors }"
                  name="'게시판명'"
                  rules="required"
                >
                  <b-form-input
                    id="sys-bm-form-boardName"
                    type="text"
                    placeholder="게시판 이름을 입력하세요."
                    v-model="detail.boardName"
                    :state="errors.length > 0 ? false:null"
                    :formatter="eufmtMaxLength50"
                    autocomplete="off"
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col md="6">
              <b-form-group label="게시판 형태"
                label-for="sys-bm-form-boardType"
                >
                <validation-provider #default="{ errors }"
                  name="'게시판 형태'"
                  rules="required"
                >
                  <v-select clearable
                    id="sys-bm-form-boardType"
                    v-model="detail.boardType"
                    :reduce="op => op.value"
                    :options="codes.BOARD_TYPE"
                    :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                    class="w-100"
                    placeholder="게시판 형태를 선택하세요."
                  >
                    <span slot="no-options">검색된 항목이 없습니다.</span>
                  </v-select>
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col md="6">
              <b-form-group label="사용 여부"
                label-for="sys-bm-form-useYn"
              >
                <validation-provider #default="{ errors }"
                  name="'사용 여부'"
                  rules="required"
                >
                  <b-form-radio-group
                    id="sys-bm-form-useYn"
                    v-model="detail.useYn"
                    :options="[{text:'사용', value:'Y'},{text:'미사용', value:'N'}]"
                    button-variant="outline-primary"
                    buttons name="radio-btn-outline"
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col cols="12" md="12">
              <b-form-group
                label="게시판 설명"
                label-for="sys-bm-form-boardDesc">
                <b-form-input
                  id="sys-bm-form-boardDesc"
                  type="text"
                  placeholder="게시판 설명을 입력하세요."
                  v-model="detail.boardDesc"
                  autocomplete="off"
                  :formatter="(val)=>{return eufmtMaxLength(val, 100)}"
                />
              </b-form-group>
            </b-col>
          </b-row>

        </b-form>
      </validation-observer>

      <!-- 버튼 영역 -->
      <div class="text-center mt-4">
        <b-button
          v-ripple.400="'rgba(113, 102, 240, 0.15)'"
          variant="primary"
          class="mr-1"
          @click.prevent="onClickSave"
        > {{isRegMode ? '등록': '저장'}}
        </b-button>
        <b-button
          v-ripple.400="'rgba(113, 102, 240, 0.15)'"
          variant="outline-primary"
          @click="onClickCancel"
        > 취소
        </b-button>
      </div>
      <!-- // 버튼 영역 -->
    </b-card>

    <!-- detail:{{detail}}<br/>
    params:{{params}}<br/>
    isRegMode:{{isRegMode}}<br/>
    isCheckedBoardMstIdOverlap:{{isCheckedBoardMstIdOverlap}}<br/> -->

  </div>
</template>
<script>
import {getCodes} from '@/api/coching-bo/comm/code';
import {getBoardMaster, setBoardMaster, addBoardMaster} from '@/api/coching-bo/system/boardMaster';

import {
  getBoardMaster as checkBoardMasterId
} from '@/api/coching-bo/system/boardMaster';

import { required } from '@validations';

import Ripple from 'vue-ripple-directive'
import ernsUtils from '@/components/mixins/ernsUtils';

import vSelect from 'vue-select';

const DEFAULT_BOARD_MASTER = {
  boardMstId : "",
  boardName : "",
  boardType : "",
  boardDesc : "",
  useYn : "Y",
  options : {},
};

export default {
  name: 'coching-BackOffice-System-BoardMasterForm',
  mixins: [ernsUtils],
  components : {
    vSelect
  },
  directives: {
    Ripple
  },
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    },
    isRegMode(){
      const params = this.params;
      if(!params.hasOwnProperty('boardMstId')){
        return true;
      }

      const pBoardMstId = params.boardMstId;
      return !(pBoardMstId && pBoardMstId.length > 0);
    }
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData'
  },
  data(){
    return {
      codes : {
        BOARD_TYPE : [],
      },
      detail : DEFAULT_BOARD_MASTER,
      params : {},
      isCheckedBoardMstIdOverlap : false
    }
  },
  mounted(){
    const _vm = this;
    _vm.loadCodes();
    _vm.fetchData();
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam(){
      const _vm = this;
      const query = _vm.$route.query, params = _vm.$route.params;
      //console.debug(params);
      _vm.params = {
        ...query
      };
    },

    fetchData(){
      const _vm = this;
      _vm.getInitParam();
      _vm.loadDetail();
    },

    //취소
    onClickCancel(){
      const _vm = this;
      _vm.$router.back();
    },

    //게시판 마스터 아이디 중복확인
    async onClickOverlapBoardMstId(){
      const _vm = this;

      try{
        _vm.loading(true);

        _vm.isCheckedBoardMstIdOverlap = false;

        const params = {
          boardMstId : _vm.detail.boardMstId
        };
        const res = await checkBoardMasterId(params);

        if(!res.resultData){
          _vm.isCheckedBoardMstIdOverlap = true;
          _vm.alertSuccess(`사용 가능한 게시판 아이디 입니다.`);
        }else{
          _vm.isCheckedBoardMstIdOverlap = false;
          _vm.alertError(`사용 불가능한 게시판 아이디 입니다.`);
        }
      }catch(err){
        _vm.alertError();
      }finally{
        _vm.loading(false);
      }
    },

    //게시판 마스터 저장
    async onClickSave() {
      const _vm = this;
      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid){
        return;
      }

      if(_vm.isRegMode && !_vm.isCheckedBoardMstIdOverlap){
        _vm.alertError("게시판 아이디 중복 확인을 진행해 주십시오.");
        return;
      }

      try{
        _vm.loading(true);
        const params = {
          ..._vm.detail
        };

        let res = null;
        if(_vm.isRegMode){
          //TODO : 키 중복확인

          //등록모드
          res = await addBoardMaster(params);
          _vm.loading(false);

          await _vm.alertSuccess(`'${params.boardName}' 게시판이 추가 되었습니다.`);

          const data = res.resultData;
          _vm.$router.replace({ name: 'coching-bo-system-boardMasterEdit', query: {boardMstId: data.boardMstId}});
          return;
        }else{
          //수정모드
          res = await setBoardMaster(params);
          _vm.detail = {
            ...res.resultData
          };

          _vm.alertSuccess(`'${params.boardName}' 게시판을 수정 했습니다.`);
        }

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    //데이터 로드
    async loadDetail(){
      const _vm = this;

      if(_vm.isRegMode){
        //등록모드
        _vm.detail = {...DEFAULT_BOARD_MASTER};
        _vm.isCheckedBoardMstIdOverlap = false;
        return;
      }

      const params = {
        boardMstId : _vm.params.boardMstId
      };

      const res = await getBoardMaster(params);
      console.debug(res);
      _vm.detail = {
        ...res.resultData
      }

      //잘못된 접근
      if(!_vm.detail.boardMstId){
        await _vm.alertError(`게시판을 찾을 수 없습니다.`);
        _vm.$router.replace({ name: 'coching-bo-system-boardMaster'});
      }
    },

    //공통코드
    async loadCodes(){
      const _vm = this;

      //게시판 타입
      const rawBtList = await getCodes({grpCode:'QP012', etc5:'ko', rowSize:-1});
      _vm.codes.BOARD_TYPE = _vm.eumConvertToVueSelectOption(rawBtList.resultData.list);
    },

  }
}
</script>

<style lang="scss" scoped>
  .test-class{
    padding-top:1.80rem;
  }
</style>