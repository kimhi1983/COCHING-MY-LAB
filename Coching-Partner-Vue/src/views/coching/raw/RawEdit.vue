<template>
  <!--section-->
  <section>
    <div class="container">
      <div class="content-wrap">
        <!--title-->
        <div class="h1">{{ $t('') || '원료 등록' }}</div>
        <!--content-->
        <div class="content-inner">
          <!--base-register-->
          <div class="base-register">
            <form>
              <!--기본정보-->
              <RawInfo
                :rawInfo="rawInfo"
                :isAdmin="isAdmin"
                :isManager="isManager"
                :managerSeq="managerSeq"
                @update:valid="rawData.rawInfoValid = $event"
                @update:data="rawData.rawInfo = $event"
                @update:preData="previewData.rawInfo = $event"></RawInfo>

              <!--성분-->
              <ElementInfo
                :rawInfo="rawInfo"
                :isAdmin="isAdmin"
                :managerSeq="managerSeq"
                @update:valid="rawData.elementInfoValid = $event"
                @update:data="rawData.elementInfo = $event"
                @update:preData="previewData.elementInfo = $event"></ElementInfo>

              <!--성분구분-->
              <TypeInfo
                :rawInfo="rawInfo"
                :isAdmin="isAdmin"
                :isManager="isManager"
                @update:valid="rawData.typeInfoValid = $event"
                @update:data="rawData.typeInfo = $event"
                @update:preData="previewData.typeInfo = $event"></TypeInfo>

              <!--구비서류-->
              <DocInfo
                :rawInfo="rawInfo"
                :isAdmin="isAdmin"
                :isManager="isManager"
                @update:data="rawData.docInfo = $event"
                @update:preData="previewData.docInfo = $event"></DocInfo>

              <!--원료자료-->
              <DetailInfo
                :rawInfo="rawInfo"
                :managerSeq="managerSeq"
                @update:valid="rawData.detailInfoValid = $event"
                @update:data="rawData.detailInfo = $event"
                @update:preData="previewData.detailInfo = $event"></DetailInfo>

              <!--button-->
              <div class="btn-area">
                <button @click="onClickMain"
                  type="button" class="btn btn-lg btn-primary">{{ $t('') || '목록'}}</button>
                <button @click="onClickPreview"
                  type="button" class="btn btn-lg btn-green">{{ $t('') || '미리보기'}}</button>
                <button v-if="isAdmin && isManager" @click="onSubmit"
                  type="button" class="btn btn-lg registRaw"
                  :class="{
                      'btn-disabled': !(rawData.rawInfoValid
                                        && rawData.elementInfoValid
                                        && rawData.typeInfoValid
                                        && rawData.docInfoValid
                                        && rawData.detailInfoValid),
                      'btn-primary': (rawData.rawInfoValid
                                        && rawData.elementInfoValid
                                        && rawData.typeInfoValid
                                        && rawData.docInfoValid
                                        && rawData.detailInfoValid)
                }">{{ rawSeq > 0 ? $t('') || '원료 수정' : $t('') || '원료 등록'}}</button>

                <button v-if="!isAdmin || (isAdmin && !isManager)" @click="onSalesSubmit"
                  type="button" class="btn btn-lg registRawDt"
                  :class="{
                      'btn-disabled': !rawData.detailInfoValid,
                      'btn-primary': rawData.detailInfoValid
                }">{{ $t('') || '원료 자료 등록'}}</button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>

    </div>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import RawInfo from '@/components/raw/RawInfo.vue';
import ElementInfo from '@/components/raw/ElementInfo.vue';
import TypeInfo from '@/components/raw/TypeInfo.vue';
import DocInfo from '@/components/raw/DocInfo.vue';
import DetailInfo from '@/components/raw/DetailInfo.vue';

import { getCodes } from '@/api/coching/comm/code';
import { getRaw, setRaw, updateRaw, setDetail, uploadFile } from '@/api/coching/comm/raw';
import { DEF_PREVIEW_DETAIL, DEF_PREVIEW_CONTENTS, DEF_RAW_PREVIEW } from '@/constants/rawPreview';

import CochingSelect from '@/components/CochingSelect.vue';

const DEF_RAW_INF = {
  rawSeq: '',  
  rawName: '',
  rawMemb: '',
  ratDttm: '',
};

export default {
	name: 'coching-manage-main',
	mixins: [ernsUtils],
	components: {
    RawInfo,
    TypeInfo,
    DocInfo,
    DetailInfo,
    CochingSelect,
    ElementInfo,
},
	computed: {    
  },
	watch: {
	},
	props: {
    rawSeq: {
      type: Number,
      default: 0,
    },
    managerSeq: {
      type: Number,
      default: 0,
    },
  },
	data() {
		return {
      isAdmin: false,
      isManager: true,
      CODES: {
        SC_001 : [],
      },
			rawInfo : {...DEF_RAW_INF},
      rawData : {
        rawInfoValid : false,
				rawInfo : null,

        elementInfoValid : true,
        elementInfo : null,
        
        typeInfoValid : false,
				typeInfo : null,

        docInfoValid : true,
				docInfo : null,

				detailInfoValid : false,
				detailInfo : null,
      },

      previewData : {
				rawInfo : null,

        elementInfo : null,
        
				typeInfo : null,

				docInfo : null,

				detailInfo : {
          detail : {...DEF_PREVIEW_DETAIL},
          contents : {...DEF_PREVIEW_CONTENTS},
        },
      },
		}
	},
	async mounted() {
		const _vm = this;

    _vm.init();
    await _vm.loadCodes();
		_vm.docReady();
		_vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {

    //멤버 타입별 수정 권한 처리
    init(){
      const _vm = this;
      const userType = _vm.eumLoginUser.userType;

      if("002" === userType){
        _vm.isAdmin = true;
      }else{
        false;
      }
    },

		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {

        if(0 < _vm.managerSeq && _vm.eumLoginUser.userSeq != _vm.managerSeq){
          if(_vm.eumLoginUser.userType == "002"){
            _vm.isManager = false;
          }else{
            _vm.$router.push({name:'coching-raw-main'});
            return;
          }
        }

        if(_vm.rawSeq){
          await _vm.loadRaw();
        }

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    async loadCodes(){
      const _vm = this;

      _vm.CODES.SC_001 = [
        {value:"", label : _vm.$t('') || '전체'},
        {value:"001", label : _vm.$t('') || '원료명'},
        {value:"002", label : _vm.$t('') || '담당자'}
      ];

    },

    async loadRaw(){
      const _vm = this;

      const rawListRes = await getRaw({rawSeq: _vm.rawSeq, ptnSeq: _vm.partnerInfo.ptnSeq});
      const { resultCode, resultFailMessage, resultData } = rawListRes;
      if(!rawListRes.resultData){
        _vm.$router.push({name:'coching-raw-main'});
      }
      _vm.rawInfo = {...rawListRes.resultData};
    },

    async onSubmit(){
      const _vm = this;

      if(!(_vm.rawData.rawInfoValid
        && _vm.rawData.elementInfoValid
        && _vm.rawData.typeInfoValid
        && _vm.rawData.docInfoValid
        && _vm.rawData.detailInfoValid)){
          await _vm.$refs["alertModal"].open({
              title: 0 == _vm.rawSeq ? _vm.$t('') || '원료 등록' : _vm.$t('') || '원료 수정',
              titleHtml : '파란 별표시[*]의 항목을 입력해야 등록이 가능합니다.'
            });
            return;
      }

      if(_vm.rawData.rawInfoValid
          && _vm.rawData.elementInfoValid
          && _vm.rawData.typeInfoValid
          && _vm.rawData.docInfoValid
          && _vm.rawData.detailInfoValid){

          _vm.loading(true);

          try{
            const param = _vm.setParamData();        

            let res;
            if(0 == _vm.rawSeq){
              //등록
              res = await setRaw(param);
              const { resultCode, resultFailMessage, resultData } = res;
              const detailParam = _vm.setDetailParam(resultData.rawSeq || null);
              if(null != detailParam){
                //원료 자료 insert
                await setDetail(detailParam);
              }

            }else{
              //수정
              res = await updateRaw(param);
              const detailParam = _vm.setDetailParam(_vm.rawSeq);
              if(null != detailParam){
                //원료 자료 insert
                await setDetail(detailParam);
              }
            }
              
            _vm.loading(false);

            const ret = await _vm.$refs["alertModal"].open({
              title: 0 == _vm.rawSeq ? _vm.$t('') || '원료 등록' : _vm.$t('') || '원료 수정',
              titleHtml : 0 == _vm.rawSeq ? _vm.$t('') || '등록되었습니다.' : _vm.$t('') || '저장되었습니다.'
            });

            if(ret){
              this.$router.push({name:'coching-raw-main'});
            }

          } catch(err) {
            _vm.defaultApiErrorHandler(err); //기본에러처리
          } finally {
            _vm.loading(false);
          }
      }

    },

    async onSalesSubmit(){
      const _vm = this;

      if(!_vm.rawData.detailInfoValid){
        await _vm.$refs["alertModal"].open({
            title: '원료 자료 등록',
            titleHtml : '파란 별표시[*]의 항목을 입력해야 등록이 가능합니다.'
          });
          return;
      }

      if(_vm.rawData.detailInfoValid){
        try{
          const detailParam = _vm.setDetailParam(_vm.rawSeq);

          if(null != detailParam){
            //원료 자료 insert
            _vm.loading(true);
            await setDetail(detailParam);
            _vm.loading(false);

            const ret = await _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '원료 자료',
              titleHtml : _vm.$t('') || '저장되었습니다.'
            });

            if(ret){
              this.$router.push({name:'coching-raw-main'});
            }
          }

        } catch(err) {
          _vm.defaultApiErrorHandler(err); //기본에러처리
        } finally {
          _vm.loading(false);
        }
      }

    },

    setParamData(){
      const _vm = this;

      const exists = _vm.rawData.rawInfo.managerList.some(
        (item) => item.membSeq === _vm.eumLoginUser.userSeq
      );
      // 존재하지 않으면 추가
      if (!exists) {
        _vm.rawData.rawInfo.managerList.push({
          membSeq: _vm.eumLoginUser.userSeq
        });
      }
    
      const param = {
              ..._vm.rawData.rawInfo,
              ..._vm.rawData.elementInfo,
              ..._vm.rawData.typeInfo,
              ..._vm.rawData.docInfo,
            };

      return param; 
    },

    setDetailParam(rawSeq, isPreview) {
      const _vm = this;

      const dataMap = isPreview ? _vm.previewData.detailInfo.contents.detail : _vm.rawData.detailInfo;
      if(dataMap){
        const hasValue = Object.values(dataMap).some(
          (value) => {
            // 배열은 길이가 0보다 크고, 문자열은 비어있지 않은지 확인
            return (Array.isArray(value) && value.length > 0) || 
                  (typeof value === "string" && value.trim() !== "");
          }
        );
        if(hasValue){
          const formData = new FormData();

          // 키-값 쌍을 FormData에 추가
          formData.append('rawDetailSeq', dataMap.rawDetailSeq || 0);
          formData.append('rawSeq', rawSeq);
          formData.append('membSeq', _vm.managerSeq == 0 ? _vm.eumLoginUser.userSeq : _vm.managerSeq);
          if(!isPreview){
            formData.append('title', dataMap.title);
            formData.append('hashtag', dataMap.hashtag);
            formData.append('rawDesc', dataMap.rawDesc);
            formData.append('rawDetail', dataMap.rawDetail);
            formData.append('useYn', dataMap.useYn);
            formData.append('strDelFileIds', dataMap.delfilelist.join(","));

            const htmlString = dataMap.rawDetail;
            //정규 표현식: `fid=` 다음의 숫자 또는 알파벳 값을 추출
            const regex = /&amp;fId=([^"&]+)/g; 
            //matchAll() 사용하여 모든 `fId` 값 찾기
            const matches = [...htmlString.matchAll(regex)];
            //배열로 변환 (값만 추출)
            const saveFileList = matches.map(match => match[1]);
            // 실제 저장될 파일 detailFileList에 추가
            const detailFileList = [];
            dataMap.htmlFileList.forEach(item => {
              if (saveFileList.includes(item.fileId) && !detailFileList.some(file => file.fileId == item.fileId)) {
                detailFileList.push(item);
              }
            });
            formData.append('strDetailFiles', JSON.stringify(detailFileList));

            formData.append('fileType', "RAW");
          }else{
            formData.append('fileType', "PREVIEW");
          }

          // 파일 리스트 추가
          dataMap.filelist.forEach((fileItem, index) => {
              if (fileItem.file) {
                  formData.append(`raw_files_${index}`, fileItem.file, fileItem.fileName);
              }
          });

          return formData;
        }      
      }
      return null;
    },

    async onClickPreview() {
      const _vm = this;

      await _vm.setPreviewFile();

      _vm.loading(true);
      try {
        const userBaseUrlData = _vm.eumUserBaseUrlData;
        const baseUrl = userBaseUrlData ? userBaseUrlData.baseUrl : 'https://www.coching.co.kr';
        const url = baseUrl + "/common/raw/preview";

        // 새 창 열기
        const newWindow = window.open('', '_blank');
        if (!newWindow) {
          alert('팝업이 차단되었습니다. 팝업 차단을 해제해주세요.');
          return;
        }

        // FormData 생성
        const preData = _vm.setPreviewData(); 
        const formData = new FormData();
        formData.append("data", JSON.stringify(preData)); // FormData 사용

        // 새 창에서 form 생성
        const form = newWindow.document.createElement('form');
        form.method = 'POST';
        form.action = url;
        form.enctype = "multipart/form-data"; // FormData 전송 방식

        // FormData 데이터를 input으로 변환하여 추가
        for (let [key, value] of formData.entries()) {
          const input = document.createElement('input');
          input.type = 'hidden';
          input.name = key;
          input.value = value;
          form.appendChild(input);
        }

        // form 제출
        newWindow.document.body.appendChild(form);
        form.submit();
      } catch (err) {
        console.error(err);
      } finally {
        _vm.loading(false);
      }
    },

    //미리보기 파일 업로드
    async setPreviewFile(){
      const _vm = this;

      const params = _vm.setDetailParam(0, true);
      const dataMap = _vm.previewData.detailInfo.contents;

      if (dataMap.detail.filelist && dataMap.detail.filelist.length > 0) {
        const res = await uploadFile(params);
        const { resultCode, resultFailMessage, resultData } = res;
        
        if (resultData.list && resultData.list.length > 0) {
          // 기존 filelist와 업로드된 resultData.list를 매칭하여 교체
          dataMap.detail.filelist = dataMap.detail.filelist.map(file => {
            // resultData.list에서 같은 fileName과 fileSize를 가진 객체 찾기
            const matchedFile = resultData.list.find(newFile => 
              newFile.fileName === file.fileName && newFile.fileSize === file.fileSize
            );
            // 매칭된 파일이 있으면 resultData.list의 데이터를 사용, 없으면 기존 객체 유지
            return matchedFile ? matchedFile : file;
          });
        }

        dataMap.files = dataMap.detail.filelist;
      }
    },
    setPreviewData(){
      const _vm = this;

      let params = {...DEF_RAW_PREVIEW};

      //기본정보
      params = Object.keys(params).reduce((acc, key) => {
        if (_vm.previewData.rawInfo.hasOwnProperty(key)) {
          acc[key] = _vm.rawData.rawInfo[key]; // 해당 KEY의 값만 저장
        }
        return acc;
      }, {});

      //성분
      params.rawElemInfo = [...(_vm.previewData.elementInfo ?? [])];
      params.rawElemInfoCount = params.rawElemInfo.length || 0;

      //성분구분
      params.rawTypeInfo = [...(_vm.previewData.typeInfo ?? [])];

      //구비서류
      params.docinfo = [...(_vm.previewData.docInfo ?? [])];

      //원료자료
      _vm.previewData.detailInfo.detail.membSeq = _vm.managerSeq; //담당자
      _vm.previewData.detailInfo.detail.membName = _vm.previewData.rawInfo.managerName;
      params.rawDetailInfo = [{ ...( _vm.previewData.detailInfo.detail ?? {} ) }];
      params.detailContents = {..._vm.previewData.detailInfo.contents};
      params.detailContents.detail.membSeq = _vm.managerSeq;

      console.log(params);

      return params;
    },

    onClickMain(){
      const _vm = this;

      _vm.$router.push({
        name: "coching-raw-main",
        query: {
          ..._vm.$route.query, // 🔹 기존 검색 조건 유지
        },
      });
    },

		docReady() {
			const _vm = this;

		},
	}
}
</script>

<style lang="scss" scoped>
.registRaw, .registRawDt{
  pointer-events: auto;
}
</style>

<style lang="scss">
#coching-mypage-main {

}
</style>
