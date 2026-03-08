<template>
  <div class="cm-notice--wrap">
    <b-card>
      <validation-observer ref="simpleRules">
        <b-form>
          <b-row>
            <b-col md="12">
              <b-form-group label="제목"
                label-for="bi-form-title">
                <validation-provider #default="{ errors }"
                  name="'제목'"
                  rules="required"
                >
                  <b-form-input
                    id="bi-form-title"
                    v-model="detail.title"
                    :state="errors.length > 0 ? false:null"
                    placeholder="제목을 입력하세요."                      
                    autocomplete="off"
                  />                    
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>                
              </b-form-group>
            </b-col>

            <b-col md="12">
              <b-form-group label="내용"
                label-for="bi-form-content">
                <validation-provider #default="{ errors }"
                  name="'내용'"
                  rules="required"
                >
                  <b-form-textarea
                    id="bi-form-content"     
                    placeholder="내용을 입력하세요."
                    v-model="detail.content"
                    rows="10"
                    max-rows="20"
                    :state="errors.length > 0 ? false:null"                    
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <!-- <b-col md="12" v-if="!isRegMode">
              <b-form-group label="삭제 여부"
                label-for="bi-form-delYn"
              >
                <validation-provider #default="{ errors }"
                  name="'삭제 여부'"
                  rules="required"
                >
                  <b-form-radio-group
                    id="bi-form-delYn"
                    v-model="detail.delYn"
                    :options="[{text:'삭제', value:'Y'},{text:'미삭제', value:'N'}]"
                    button-variant="outline-primary"                
                    buttons name="radio-btn-outline"
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col> -->
            
            <b-col md="12"
              v-show="fileData.filelist && fileData.filelist.length"
            >
              <b-form-group label="첨부파일"
                label-for="bi-form-content">
                <BoardFile 
                  pageType="read"
                  :fileData="fileData"           
                  @update:fileData="fileData = $event"></BoardFile>
              </b-form-group>
            </b-col>

            <b-col md="6" v-if="!isRegMode">
              <b-form-group label="작성자"
                label-for="bi-form-writerName">
                <b-form-input
                  readonly
                  id="bi-form-writerName"
                  type="text"              
                  placeholder="작성자 이름을 입력하세요."
                  :value="`${detail.regName || ''}(${detail.regId})`"                 
                  :formatter="eufmtMaxLength50"
                />
              </b-form-group>
            </b-col>

            <!-- <b-col md="6">
              <b-form-group label="게시일 (시작일 ~ 종료일)"
                label-for="bi-form-fromDate">
                <b-input-group>
                  <b-form-datepicker v-model="detail.fromDate"
                    placeholder="YYYY-MM-DD" locale="ko" 
                    :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                  />
                  <b-input-group-text class="date-range">~</b-input-group-text>
                  <b-form-datepicker v-model="detail.toDate" 
                    :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                    placeholder="YYYY-MM-DD" locale="ko"
                  />
                </b-input-group>                  
              </b-form-group>
            </b-col> -->

            <b-col md="6" v-if="!isRegMode">
              <label>등록일 : {{detail.rgtDttm | eFmtDateTime}}</label><br/>
              <label>최종수정일 : {{detail.chngDttm | eFmtDateTime}}</label>
            </b-col>

            <!-- <b-col md="6" v-if="!isRegMode">
              <label>최종수정일 : {{detail.udtDate | eFmtDateTime}}</label>
            </b-col> -->
          </b-row>
        </b-form>
      </validation-observer>

      <!-- 버튼 영역 -->
      <div class="text-center mt-4">
        <b-button v-if="!isRegMode"
          v-ripple.400="'rgba(113, 102, 240, 0.15)'"
          variant="outline-danger"                
          class="mr-1"
          @click.prevent="onClickDelete"
        > 삭제
        </b-button>
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
    <b-card v-show="detail.cmtUseYn == 'Y' && detail.cmtCnt > 0">
      <BoardComment
        :boardSeq="Number(boardSeq)"></BoardComment>
    </b-card>

    <!-- detail:{{detail}}<br/>
    params:{{params}}<br/>
    isRegMode:{{isRegMode}}<br/>
    isCheckedBoardMstIdOverlap:{{isCheckedBoardMstIdOverlap}}<br/> -->

  </div>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import {getCodes} from '@/api/coching-bo/comm/code';
import {getBoardMaster} from '@/api/coching-bo/system/boardMaster';
import {getBoardDetail, setBoardDetail, addBoardDetail, delBoardDetail} from '@/api/coching-bo/comm/board';

import { required } from '@validations';
import Ripple from 'vue-ripple-directive';
import vSelect from 'vue-select';

import BoardFile from '../BoardFile.vue';
import BoardComment from '../BoardComment.vue';
import { BOARD_MODE, DEF_BOARD_INF, DEF_FILE_INF } from '@/constants/board';
import boardMixin from '../boardMixin';



export default {
	name: 'coching-board-basic-write',
	mixins: [ernsUtils, boardMixin],
	components : {
    vSelect, required, BoardComment, BoardFile
  },
  directives: {
    Ripple
  },
  computed : {
    isRegMode(){
      return this.boardMode == BOARD_MODE.WRITE;
    }
  },
	watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData'
  },
	props: {
    boardMode:{
			type: String,
			default: BOARD_MODE.WRITE
		},
    boardMstId:{
      type: String,
      require: true,
    },
		boardSeq:{
      type : [String, Number],
			default : 0
    },
  },
	data() {
		return {
      codes : {
        CATE : [],        
      },

      detail : {...DEF_BOARD_INF},
      fileData : JSON.parse(JSON.stringify(DEF_FILE_INF)),
      params : {},
      boardMst : {},
    }
	},
	async mounted(){
    const _vm = this;        
    await _vm.fetchData();
  },
  beforeDestroy(){
    const _vm = this;
	},
	methods: {    
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam(){
      const _vm = this;      
      const query = _vm.$route.query, params = _vm.$route.params;
      console.debug(query);
      _vm.params = {
        ...query
      };
    },

    async fetchData(){
      const _vm = this;
      _vm.getInitParam();
      await _vm.loadDetail();
      await _vm.loadCodes();
    },

    //취소
    onClickCancel(){
      const _vm = this;
      _vm.$router.back();
    },

    //삭제
    async onClickDelete(){
      const _vm = this;

      const result = await _vm.$swal({
        title: '확인',
        text: "게시물을 삭제 하시겠습니까?",
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
          boardSeq: _vm.detail.boardSeq
          , delYn : "Y" //삭제 처리
        };

        if(_vm.isRegMode){
          return; //등록모드
        }

        //수정모드
        const res = await delBoardDetail(params);

        _vm.loading(false);
        await _vm.alertSuccess(`게시물을 삭제 했습니다.`);      
        _vm.$router.back();

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
      
    },
    
    // 저장
    async onClickSave() {
      const _vm = this;
      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid){
        return;        
      }

      try{
        _vm.loading(true);
        const params = {
          ..._vm.detail,
        };

        const formParam = _vm.setParamData(params, _vm.fileData);

        if(_vm.isRegMode){

          //등록모드
          const res = await addBoardDetail(formParam);
          _vm.loading(false);

          await _vm.alertSuccess(`게시물이 등록 되었습니다.`);

          const data = res.resultData;
          _vm.goEdit({boardSeq: data.boardSeq});
          return;
        }else{
          //수정모드
          const res = await setBoardDetail(formParam);
          _vm.detail = {
            ...res.resultData
          };

          _vm.alertSuccess(`게시물을 수정 했습니다.`);        
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
        _vm.detail = {
          ...DEF_BOARD_INF,
          boardMstId : _vm.boardMstId
        };
        return;
      }

      try{
        _vm.loading(true);
        const params = {
          boardMstId : _vm.boardMstId,      
          boardSeq : _vm.boardSeq,          
        };

        const res = await getBoardDetail(params);
        console.debug(res);
        _vm.detail = {
          ...res.resultData
        }
        _vm.fileData.filelist = res.resultData.filelist;

        //잘못된 접근
        if(!_vm.detail.boardSeq){
          await _vm.alertError(`게시글을 찾을 수 없습니다.`);
          _vm.onClickCancel(); //뒤로가기 
        }
      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }      
    },

    setParamData(param, fileData){
      const _vm = this;

      const dataMap = param;

      const formData = new FormData(); //파일포함이라 FormData 객체 생성

      // 키-값 쌍을 FormData에 추가
      formData.append('boardMstId', _vm.boardMstId);
      formData.append('boardSeq', parseInt(dataMap.boardSeq, 10));
      formData.append('cateCd', dataMap.cateCd);
      formData.append('title', dataMap.title);
      formData.append('content', dataMap.content);
      formData.append('sortOrd', dataMap.sortOrd);
      formData.append('delYn', dataMap.delYn);
      formData.append('adminDelCd', dataMap.adminDelCd);
      formData.append('adminDelRsn', dataMap.adminDelRsn);
      formData.append('views', dataMap.views);
      formData.append('fromDate', dataMap.fromDate);
      formData.append('toDate', dataMap.toDate);
      formData.append('strDelFileIds', fileData.delfilelist.join(","))
      formData.append('fileType', 'BOARD')

      // 파일 리스트 추가
      fileData.filelist.forEach((fileItem, index) => {
        if (fileItem.file) {
          formData.append(`board_files_${index}`, fileItem.file, fileItem.fileName);
        }
      });

      return formData; 
    },

    async loadCodes(){
      const _vm = this;

      const retBoardMst = await getBoardMaster({boardMstId : _vm.boardMstId});
      _vm.boardMst = retBoardMst.resultData;
      
      const categoryGroupCd = _vm.boardMst ? _vm.boardMst.cateCd : null;
      if(categoryGroupCd){
        const cateList = await getCodes({grpCode:categoryGroupCd, etc5: _vm.getLocale(), rowSize:-1});
        _vm.codes.CATE = _vm.eumConvertToVueSelectOption(cateList.resultData.list, 'etc1');
      }
    },

    getLocale(){
      const _vm = this;
      return _vm.$route.meta.locale;
    }
  }  
}
</script>

<style lang="scss" scoped>
  .test-class{
    padding-top:1.80rem;
  }
</style>
