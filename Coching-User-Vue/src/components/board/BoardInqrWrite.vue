
<template>

  <!--real-content-->
  <div class="real-content">
    <div class="container-inner">
      <!--write-view-->
      <div class="write-view">
        <!--input-->
        <validation-observer ref="simpleRules">
          <form class="write-form">
            <div class="inner">
              
              <div class="input-line">
                <div class="input-set">
                  <input type="text" class="input-lg" placeholder="제목을 입력해 주세요"  v-model="boardData.title"/>
                </div>
              </div>

              <div class="flex">
                <div class="input-set input-wrap">

                  <validation-provider :name="'문의유형'" rules="required" #default="{ errors }">
                    <div class="custom-select-wrapper">
                      <div class="custom-select"
                        :class="{ 'opened': isCategoryOpen }"
                        @click="isCategoryOpen = !isCategoryOpen">
                        <span class="custom-select-trigger">
                          {{ (codes.CATE.find(item => item.value === boardData.cateCd) || {}).label || '문의유형' }}
                        </span>
                        <div class="custom-options">
                          <span class="custom-option undefined"
                            :class="{ 'selection': item.value == boardData.cateCd }"
                            :data-value="item.value"
                            v-for="(item, idx) in codes.CATE"
                            :key="idx"
                            @click.stop="onCategoryClicked(item)"
                          >{{ item.label }}</span>
                        </div>
                      </div>
                      <!-- hidden input으로 v-model 연결 -->
                      <input type="hidden" v-model="boardData.cateCd" />
                    </div>
                    <div class="info error" :class="{ 'active': errors.length > 0 }">{{ errors[0] }}</div>
                  </validation-provider>
                  
                </div>
                <div class="input-set">
                  <input type="tel" 
                    @input="boardData.phone = boardData.phone.replace(/[^0-9]/g, '')" 
                    placeholder="전화번호를 입력해주세요" v-model="boardData.phone"
                  >
                </div>
                <div class="input-set input-wrap">
                  <validation-provider :name="'이메일'" rules="required" #default="{ errors }">
                    <input type="email" placeholder="이메일 주소를 입력해주세요" v-model="boardData.email">
                    <div class="info error" :class="{ 'active': errors.length > 0 }">{{ errors[0] }}</div>
                  </validation-provider>
                </div>
              </div>
              <div class="textarea-wrap">
                <textarea placeholder="내용을 입력해 주세요" v-model="boardData.content"></textarea>
              </div>
              <!--upload-->
              <BoardFile 
                :fileData="fileData"
                pageType="submit"
                @update:fileData="fileData = $event"></BoardFile>
            </div>

            <!--button-->
            <div class="btn-area">
              <button type="button" 
                class="btn btn-lg btn-gray" 
                @click="goList()">취소</button>
              <button type="button" 
                class="btn btn-lg btn-primary" 
                @click="onSubmit">
                {{ isEditMode ? '수정하기' : '등록하기' }}
              </button>
            </div>
          </form>
        </validation-observer>
      </div>
      <!-- boardMstId:{{ boardMstId }}<br/>
      boardData:{{ boardData }} -->

      <!--오류-->
			<AlertModal ref="alertModal"></AlertModal>
			<ConfirmModal ref="confirmModal"></ConfirmModal>
    </div>
  </div>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import boardMixin from './boardMixin';

import BoardFile from './BoardFile.vue';


import { getCodes } from '@/api/coching/comm/code';
import { DEF_FILE_INF, DEF_INQR_BOARD_INF, BOARD_MODE } from '@/constants/board';
import { getBoard, addInqrBoard, updateBoard, getBoardMaster } from '@/api/coching/comm/board';

export default {
	name: 'coching-inqr-board-write',
	mixins: [ernsUtils, boardMixin],
	components: {
    BoardFile,
  },
	computed: {    
  },
	watch: {
	},
	props: {
    boardMode:{
			type: String,
			default: BOARD_MODE.WRITE
		},
    boardMstId:{
      type : String
    },
		inqrSeq:{
      type : [String, Number],
			default : 0
    },
  },
	data() {
		return {
      codes : {
        CATE : [],        
      },
      boardData : {...DEF_INQR_BOARD_INF},
      fileData : JSON.parse(JSON.stringify(DEF_FILE_INF)),
      params : {},
      isEditMode : false,
      boardMst : {},
      isCategoryOpen: false,
		}
	},
	async mounted() {
		const _vm = this;

    await _vm.docReady();
    await _vm.loadCodes();
    await _vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {   
    onCategoryClicked(item) {
      const _vm = this;
      
      _vm.boardData.cateCd = item.value;
      _vm.isCategoryOpen = false;
    },

    async loadCodes(){
      const _vm = this;
      const retBoardMst = await getBoardMaster({boardMstId : _vm.boardMstId});
      _vm.boardMst = retBoardMst.resultData;
      
      const categoryGroupCd = _vm.boardMst ? _vm.boardMst.cateCd : null;
      if(categoryGroupCd){
        const cateList = await getCodes({grpCode:categoryGroupCd, etc5: _vm.$i18n.locale});
        _vm.codes.CATE = _vm.eumConvertToVueSelectOption(cateList.resultData.list, 'etc1');
      }
    },
    
    /* 게시글 상세 정보 가져오기 */
    async loadDetail() {
			const _vm = this;

			_vm.loading(true);
      try {
        const boardRes = await getBoard({
          boardSeq : _vm.boardSeq,
          boardMstId : _vm.boardMstId,
          delYn : 'N',
        });

        //데이터 바인딩
        _vm.boardData = boardRes.resultData;
        _vm.fileData.filelist = boardRes.resultData.filelist;

        //dataMap.backupList = JSON.parse(JSON.stringify(resData.list));
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

    async onSubmit() {
      const _vm = this;

      // validation
      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid){
        return;
      }

      if(!_vm.eumLoginUser) { //로그인 정보가 없을 때 return
        await _vm.onClickConfirmLogin(); //로그인 확인
        return;
      };

      _vm.loading(true);
      try{
        const param = _vm.setParamData();

        if(_vm.isEditMode){
          //수정모드
          /*
          const editRes = await updateBoard(param);

          _vm.loading(false);
          await _vm.alertSuccess(`게시물을 수정 했습니다.`);

          const data = editRes.resultData;

          _vm.goView(_vm.boardSeq);
          */
        }else{
          //등록 모드
          const writeRes = await addInqrBoard(param);
          _vm.loading(false);

          await _vm.alertSuccess(`게시물이 등록 되었습니다.`);          

          const data = writeRes.resultData;

          //게시글 상세 화면으로 이동
          _vm.goList(1);
        }

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    setParamData(){
      const _vm = this;

      const dataMap = _vm.boardData;

      const formData = new FormData(); //파일포함이라 FormData 객체 생성

      // 키-값 쌍을 FormData에 추가
      formData.append('boardMstId', _vm.boardMstId);
      formData.append('inqrSeq', parseInt(dataMap.inqrSeq, 10));
      formData.append('cateCd', dataMap.cateCd);
      formData.append('title', dataMap.title);
      formData.append('content', dataMap.content);
      formData.append('writer', dataMap.writer);
      formData.append('phone', dataMap.phone);
      formData.append('email', dataMap.email);
      formData.append('sortOrd', dataMap.sortOrd);
      formData.append('orgSeq', 0);
      formData.append('delYn', dataMap.delYn);
      formData.append('adminDelCd', dataMap.adminDelCd);
      formData.append('adminDelRsn', dataMap.adminDelRsn);
      formData.append('views', dataMap.views);
      formData.append('fromDate', dataMap.fromDate);
      formData.append('toDate', dataMap.toDate);
      formData.append('strDelFileIds', _vm.fileData.delfilelist.join(","))

      // 파일 리스트 추가
      _vm.fileData.filelist.forEach((fileItem, index) => {
          if (fileItem.file) {
            formData.append(`board_files_${index}`, fileItem.file, fileItem.fileName);
          }
      });

      return formData; 
    },

    /* 데이터 가져오는 부분(사용하지 필요없어도 반드시 선언) */
		async fetchData() {      
			const _vm = this;

      _vm.loading(true);
      try {
        const curBoardSeq = parseInt((""+_vm.boardSeq) || '0', 10);
        if(_vm.boardMode == BOARD_MODE.EDIT && curBoardSeq) { //수정모드
          _vm.isEditMode = true;
          await _vm.loadDetail();
        }

        _vm.boardData.boardMstId = _vm.boardMstId;

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

		docReady() {
			const _vm = this;
		},
	}
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
