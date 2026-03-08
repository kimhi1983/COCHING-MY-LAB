<template>
  <!--card-account-->
  <div class="section">
    <div class="title-wrap">
      <!--title-->
      <div class="title">기본정보</div>
      <div class="check-radio-wrap">
        <div class="check-title">등록 원료 노출 여부</div>
        <div class="chehck-radio-flex">
          <div>
            <div class="radiobox">
              <input v-model="localData.rawInfoItem.useYn" id="register-open" type="radio" name="material-register" value="Y"/>
              <label for="register-open" class="radiobox-label">노출</label>
            </div>
          </div>
          <div>
            <div class="radiobox">
              <input v-model="localData.rawInfoItem.useYn" id="register-hidden" type="radio" name="material-register" value="N"/>
              <label for="register-hidden" class="radiobox-label">숨김</label>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!--section-content-->
    <validation-observer tag="div" ref="rawInfoForm" class="section-content" #default="{ invalid }">
      <!-- invalid 값을 로컬 데이터에 바인딩 -->
      <div v-if="updateInvalidState(invalid)"></div>
      <!--input-wrap-->
      <div class="info-wrap">
        <div class="input-wrap">
          <div class="label-set">
            <label>{{ $t('') || '원료명' }}<span class="text-primary">*</span></label>
            <validation-provider #default="{ errors }" tag="div"
              class="input-set" :name="$t('') || '원료명'" rules="required">
                <input v-model="localData.rawInfoItem.rawName" 
                  :disabled="!isAdmin || (isAdmin && !isManager)" type="text" placeholder="원료명을 입력해 주세요" />
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
            </validation-provider>
          </div>
          <div class="label-set">
            <label>{{ $t('') || '제조사' }}</label>
            <validation-provider #default="{ errors }" tag="div"
              class="input-set" :name="$t('') || '제조사'" rules="">
                <input v-model="localData.rawInfoItem.prodCompany" 
                  :disabled="!isAdmin || (isAdmin && !isManager)" type="text" placeholder="제조사를 입력해 주세요" />
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
            </validation-provider>
          </div>
          <div class="label-set">
            <label>{{ $t('') || '제조국' }}</label>
            <validation-provider #default="{ errors }" tag="div"
              class="input-set" :name="$t('') || '제조국'" rules="">
              <CochingMultiSelect
                :placeholder="$t('') || '제조국'"
                :value="localData.rawInfoItem.prodCountry"
                :options="CODES.CD_014"
                :label="'codeName'"
                :trackBy="'etc1'"
                :disabled="!isAdmin || (isAdmin && !isManager)"
                :isMultiple="false"
                :isSearch="true"
                :minSearchCnt="5"
                :noSearchText="'일치하는 국가가가 없습니다.'"
                @input="onSelectRawCountry"
                >
              </CochingMultiSelect>
              <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
            </validation-provider>
          </div>
          <div class="label-set">
            <label>{{ $t('') || '공급사' }}</label>
            <validation-provider #default="{ errors }" tag="div"
              class="input-set" :name="$t('') || '공급사'" rules="">
                <input v-model="localData.rawInfoItem.supplier" 
                :disabled="!isAdmin || (isAdmin && !isManager)" type="text" placeholder="공급사를 입력해 주세요" />
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
            </validation-provider>
          </div>
          <div class="label-set">
            <label>{{ $t('') || '무게(kg)' }}</label>
            <validation-provider #default="{ errors }" tag="div"
              class="input-set" :name="$t('') || '무게'" rules="decimal">
                <input v-model="localData.rawInfoItem.weight" 
                :disabled="!isAdmin || (isAdmin && !isManager)" type="text" placeholder="무게를 입력해 주세요" />
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
            </validation-provider>
          </div>
          <div class="label-set">
            <label>{{ $t('') || '원료 담당자' }}</label>
            <validation-provider #default="{ errors }" tag="div"
              class="input-set" :name="$t('') || '담당자 선택'" rules="">
              <CochingMultiSelect
                :placeholder="$t('') || '담당자 선택'"
                :value="managerValue"
                :options="memberData.list"
                :label="'membName'"
                :trackBy="'membSeq'"
                :disabled="!isAdmin || (isAdmin && !isManager)"
                :isMultiple="true"
                :isSearch="true"
                :minSearchCnt="5"
                :noSearchText="'일치하는 이름이 없습니다.'"
                @input="onSelectRawMemb" 
                >
              </CochingMultiSelect>
              <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
            </validation-provider>
          </div>
        </div>
        <!--img-wrap-->
        <div class="img-wrap">
          <label>원료 썸네일 등록<span class="text-primary">*</span></label>
          <div @click="onclickThumbnailRegister" class="preview modal-open img-upload-modal">
            <!--이미지 등록 후 이곳에 이미지 넣기-->
            <img v-show="localData.rawInfoItem.thumbnailId != ''"
              :src="eumFileImagePath(localData.rawInfoItem.thumbnailId, '0', errorImg)"
              @error="onImageError"
              alt="원료 썸네일"/>
          </div>
        </div>
        <validation-provider #default="{ errors }"
              :name="$t('') || '썸네일'" rules="required">
              <input v-model="localData.rawInfoItem.thumbnailId" type="hidden" />
        </validation-provider>
      </div>
    </validation-observer>

    <!--img-upload-->
    <div class="modal for-img-upload" ref="thumbnailModal">
      <div class="layer">
        <div class="layer-content">
          <div class="modal-inner modal-xlg">
            <div class="modal-content">
              <div class="modal-header">
                <div @click="closeThumbnailModal" class="modal-close"></div>
                <div class="title">이미지 선택</div>
              </div>
              <form>
                <div class="modal-body">
                  <!--img-upload-wrap-->
                  <div class="img-upload-wrap scroller">
                    <!--img-type-->
                    <div class="img-type">
                      <!--img-upload-->
                      <div @click="handleInputFile" class="img-upload">
                        <input @change="handleFileChange" type="file" ref="fileInput" accept="image/*" class="hidden-file" style="display: none" />
                        <!-- <button type="button" class="btn-delete"></button> -->
                        <div class="preview"></div>
                      </div>
                      <div v-for="(item, index) in thumbData.prtThumbList" :key="'before-' + index"
                        :class="{ active: thumbData.selectedIndex === index }"
                        @click="onselectThumbmail(item, index)" class="item">
                        <img :src="eumFileImagePath(item.fileId, '0', errorImg)"
                            @error="onImageError"
                            :alt="item.fileName"/>
                        <button @click.stop="deleteThumbnail(item.fileId)" type="button" class="btn-delete"></button>
                      </div>
                      <div
                        class="item"
                        v-for="(item, index) in thumbData.exList"
                        :key="'after-' + index"
                        :class="{ active: thumbData.selectedIndex === index + thumbData.prtThumbList.length + 1 }"
                        @click="onselectThumbmail(item, index + thumbData.prtThumbList.length + 1)"
                      >
                        <img
                            :src="eumFileImagePath(item.fileId, '0', errorImg)"
                            @error="onImageError"
                            :alt="item.fileName"/>
                      </div>
                      
                    </div>
                  </div>
                </div>
                <div class="modal-footer">
                  <button @click="closeThumbnailModal" type="button" class="btn btn-md btn-gray bottom-modal-close">취소</button>
                  <button @click="onsaveThumb" type="button" class="btn btn-md btn-primary">등록하기</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- img cropper -->
     <div class="modal for-img-upload" ref="cropperModal">
      <div class="layer">
        <div class="layer-content">
          <div class="modal-inner modal-xlg">
            <div class="modal-content">
              <div class="modal-header">
                <div @click="closeCropperModal" class="modal-close"></div>
                <div class="title">이미지 자르기</div>
              </div>
              <form>
                <div class="modal-body">
                  <!--img-upload-wrap-->
                  <div class="img-upload-wrap">
                    <Cropper
                      ref="cropper"
                      :src="thumbData.cropFile.blobUrl"
                      :aspect-ratio="16/9"
                      :view-mode="1"
                      :auto-crop-area="0.5"
                      :background="false"
                      :responsive="true"
                      :zoomable="true"
                      :movable="true"
                      :scalable="true"
                      :crop-box-movable="true"
                      :crop-box-resizable="true"
                      style="height: 542px; width: 100%;"
                    />
                  </div>
                </div>
                <div class="modal-footer">
                  <button @click="closeCropperModal" type="button" class="btn btn-md btn-gray bottom-modal-close">취소</button>
                  <button @click="saveCroppedImage" type="button" class="btn btn-md btn-primary">저장하기</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>

    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

import { required } from '@validations';
import { extend } from 'vee-validate';

import CochingSelect from '@/components/CochingSelect.vue';
import CochingMultiSelect from '@/components/CochingSearchSelect.vue';

import { getMembList } from '@/api/coching/member/member';
import { getManagerList, getExThumbList, getPrtThumbList, uploadThumbFile, updateThumbDelYn } from '@/api/coching/comm/raw';
import { getCodes } from '@/api/coching/comm/code';

import VueCropper from 'vue-cropperjs';
import ERROR_IMG from '@/assets/images/raw_thumb_noImg.jpg';

const DEF_FILE ={
  fileId: null,
  fileName: '',
  file: null,
  blobUrl: null, // Blob URL 추가
};

const DEF_RAW_INF = {
  rawSeq: null,
  ptnSeq: null,
  rawName: '',
  prodCompany: '',
  prodCountry: '',
  supplier: '',
  useYn: 'N',
  delYn: 'N',
  managerList: [],
  thumbnailId: '',
  thumbnailFile: {...DEF_FILE},
};

const DEF_RAW_ELM_INF = {
  id: 0,
  rawElmId: null,
};

export default {
    name: 'coching-rawInfo',
    mixins: [ernsUtils],
    components: {
      CochingSelect,
      CochingMultiSelect,
      Cropper:VueCropper,
    },
    props: {
        rawInfo: {
          type : Object,
          defalt: {}
        },
        isAdmin: {
          type : Boolean,
          default: false
        },
        isManager: {
          type : Boolean,
          default: false
        },
        managerSeq: {
          type: Number,
          default: 0,
        },
    },
    computed: {
        managerValue() {
          return this.localData.rawInfoItem.managerList
          .map((item) => item.membSeq);
        },
        errorImg(){
          return ERROR_IMG;
        },
    },
    watch: {
      '$i18n.locale' : function(){
        const _vm = this;
      },
      rawInfo: {
        handler() {
          if(this.rawInfo.rawSeq > 0){
            this.localData.rawInfoItem = { ...DEF_RAW_INF, ...this.rawInfo };
            this.init(); // 필요한 경우만 호출
          }else{
            this.localData.rawInfoItem.supplier = this.partnerInfo.ptnName;
          }
        },
        immediate: true, // 컴포넌트가 마운트될 때도 트리거
        deep: true, // 깊은 감지 활성화
      },
      'localData.rawInfoItem' : {
        handler(newVal, oldVal){
          this.onChangeValue();
        },
        deep: true
      },       
    },
    data() {
      return {
        localInvalidState: false, //양식폼의 validate 상태
        status: '',
        CODES: {
          CD_014 : [],
        },
        localData:{
          rawInfoItem: {...DEF_RAW_INF},
        },
        memberData: {
            list:[],
        },
        thumbData: {
          exList: [],
          prtThumbList: [],
          cropFile: {...DEF_FILE},
          selectedIndex: null,
        }
      }
    },
  async mounted(){
    const _vm = this;

    function checkDecimal() {
			extend('decimal', {
        validate: value => {
          return /^\d+(\.\d{1,2})?$/.test(value) || _vm.$t('') || '소수점 두번째까지 가능합니다.';
        },
			});
		}
    checkDecimal();

    await _vm.loadCodes();
    _vm.docReady();
    _vm.fetchData();    
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    //이미지 에러시 대체 이미지 설정
    onImageError(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImg; // 대체 이미지로 설정
      targetObj.style.width = '100%'; // 원하는 너비 설정
      targetObj.style.height = '100%'; // 원하는 높이 설정
    },

    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);

      try{
          _vm.localData.rawInfoItem.ptnSeq = _vm.partnerInfo.ptnSeq;

          //원료 담당자 리스트
          const membListRes = await getMembList({ptnSeq: _vm.partnerInfo.ptnSeq, membType: '004', useYn: 'Y'});
          const resData = membListRes.resultData;
          _vm.memberData.list = [...resData.list];

          // 썸네일 예시 데이터 가져오기
          const exThumbRes = await getExThumbList({ptnSeq: _vm.partnerInfo.ptnSeq});
          const resExThumblData = exThumbRes.resultData;
          _vm.thumbData.exList = [...resExThumblData.list];

          //파트너사 썸네일 데이터 가져오기
          await _vm.loadPrtThumbList();

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    async init(){
      const _vm = this;
      _vm.loading(true);

      try{
          //담당자 리스트 
          const managerListRes = await getManagerList({rawSeq: _vm.rawInfo.rawSeq});
          const managerList = managerListRes.resultData.list || [];
          _vm.localData.rawInfoItem.managerList = [...managerList];

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

    },

    async loadCodes(){
      const _vm = this;

      const codeRes = await getCodes({grpCode:"CH014", etc5:_vm.$i18n.locale, useYn: 'Y'});
      const { resultCode, resultFailMessage, resultData } = codeRes;

      _vm.CODES.CD_014 = [...resultData.list];
    },

    async loadPrtThumbList(){
      const _vm = this;

      try{
        //파트너사 썸네일 데이터 가져오기
        const prtThumbRes = await getPrtThumbList({ptnSeq: _vm.partnerInfo.ptnSeq});
        const resPrtThumbData = prtThumbRes.resultData;
        _vm.thumbData.prtThumbList = [...resPrtThumbData.list];

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }
    },

    //제조국 선택
    onSelectRawCountry(val){
      const _vm = this;

      _vm.localData.rawInfoItem.prodCountry = val;

      _vm.onChangeValue();
    },

    //담당자 선택
    onSelectRawMemb(val){
      const _vm = this;

      // val이 배열인지 확인
      if (Array.isArray(val)) {
        // 배열인 경우 각 값에 대해 {membSeq: value} 형식으로 변환
        _vm.localData.rawInfoItem.managerList = val.map((item) => ({ membSeq: item }));
      } else {
        // 단일 값인 경우 배열로 변환 후 {membSeq: value} 형식으로 변환
        _vm.localData.rawInfoItem.managerList = [{ membSeq: val }];
      }

      _vm.onChangeValue();
    },

    //썸네일 등록 모달 열기
    onclickThumbnailRegister() {
      const _vm = this;

      if(!_vm.isAdmin || ( _vm.isAdmin && !_vm.isManager)) {
        return;
      }

      $(_vm.$refs["thumbnailModal"]).fadeIn(300, function() {
        // 모달창 스크롤 적용
        const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);

        if (!isMobile) {
          // 데스크톱에서만 nicescroll 적용
          $(".scroller").niceScroll();
          
          // 리사이즈 시 스크롤 재조정
          $(window).on("resize", function () {
            $(".scroller").getNiceScroll().resize();
          });

          // 스크롤 이벤트에서 다른 스크롤 방지
          $(".scroller").on("scroll", function (e) {
            e.stopPropagation(); // 다른 스크롤 이벤트가 영향을 미치지 않도록 방지
          });
        } else {
          // 모바일에서는 기본 스크롤 사용
          $(".scroller").css("overflow", "auto");
        }

        // 뒷 페이지 스크롤 막기
        $("body").css("overflow", "hidden");
      });
    },

    //썸네일 모달 닫기
    closeThumbnailModal() {
      const _vm = this;

      $(_vm.$refs["thumbnailModal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },

    //썸네일 선택
    onselectThumbmail(file, index) {
      const _vm = this;
      _vm.thumbData.selectedIndex = index;

      _vm.localData.rawInfoItem.thumbnailFile = {
        fileId: file.fileId,
        fileName: file.fileName,
      };
    },

    onsaveThumb() {
      const _vm = this;

      if(_vm.thumbData.selectedIndex === null) {
        _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '썸네일 선택',
          titleHtml : '썸네일을 선택해주세요.'
        });
        return;
      }

      _vm.localData.rawInfoItem.thumbnailId = _vm.localData.rawInfoItem.thumbnailFile.fileId || '';
      _vm.localData.rawInfoItem.thumbnailFile = {...DEF_FILE}; // 썸네일 파일 초기화
      _vm.closeThumbnailModal();
    },

    handleInputFile(){
      this.$refs.fileInput.click();
    },
    //File click
    handleFileChange(event) {
      this.handleFileUpload(event.target.files);
      event.target.value = ''; // 파일 입력 필드 초기화
    },

    async handleFileUpload(files) {
      const _vm = this;
      const allowedExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'];

      if (files.length > 0) {
        for(let i = 0; i < files.length; i++) {
          const file = files[i];
          const extension = file.name.split('.').pop().toLowerCase();

          if (!allowedExtensions.includes(extension)) {
            alert(`허용되지 않는 파일 형식입니다: ${file.name}`);
            continue; // 다음 파일로 넘어감
          }

          const MAX_SIZE_MB = 50 * 1024 * 1024;
          if (file.size > MAX_SIZE_MB) {
            alert("파일 크기는 50MB 이하만 업로드할 수 있습니다.");
            return;
          }

          const blobUrl = URL.createObjectURL(file);
          _vm.thumbData.cropFile = {
            fileId: null,
            fileName: file.name,
            file: file,
            blobUrl: blobUrl, // Cropper의 src로 사용
          };
          
          _vm.onclickCopperModal();
         
        }
      }
    },

    async uploadThumbnail(file) {
      const _vm = this;

      try {
        if (!file) {
          throw new Error("파일이 선택되지 않았습니다.");
        }

        const formData = new FormData();
        formData.append('ptnSeq', _vm.partnerInfo.ptnSeq || 0);
        formData.append(`raw_files_0`, file, file.name);
        formData.append('fileType', "PRT_THUMB");

        //썸네일 등록
        const res = await uploadThumbFile(formData);
        await _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '썸네일 등록',
              titleHtml : '등록 되었습니다.'
            });

        await _vm.loadPrtThumbList();

      } catch (error) {
        console.error("썸네일 업로드 중 오류 발생:", error);
      }

    },

    //썸네일 삭제
    deleteThumbnail(fileId) {
      const _vm = this;

      _vm.$refs["confirmModal"].open({
        title: _vm.$t('') || '썸네일 삭제',
        content: '선택한 썸네일을 삭제하시겠습니까?'
      }).then(async (result) => {
        if (result) {
          try {
            await updateThumbDelYn({ thumbnailId: fileId, delYn: 'Y' });
            _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '썸네일 삭제',
              titleHtml : '삭제 되었습니다.'
            });
            await _vm.loadPrtThumbList();
          } catch (error) {
            console.error("썸네일 삭제 중 오류 발생:", error);
          }
        }
      });
    },

    //썸네일 cropper 모달 열기
    onclickCopperModal() {
      const _vm = this;

      _vm.$nextTick(() => {
        $(_vm.$refs["cropperModal"]).fadeIn(300);

        // cropper 강제 재초기화
        const cropper = _vm.$refs.cropper;
        if (cropper && cropper.replace) {
          cropper.replace(_vm.thumbData.cropFile.blobUrl);
        }
      });
    },

    async saveCroppedImage() {
      const _vm = this;

      try {
        const cropper = _vm.$refs.cropper;

        // Cropper에서 잘라낸 Blob 가져오기
        cropper.getCroppedCanvas().toBlob(async (blob) => {
          if (!blob) {
            alert("이미지 자르기에 실패했습니다.");
            return;
          }

          // Blob → File 로 변환 (파일명 유지)
          const file = new File([blob], _vm.localData.rawInfoItem.thumbnailFile.fileName || 'cropped.png', { type: 'image/png' });

          // uploadThumbnail 호출
          await _vm.uploadThumbnail(file);

          //cropper 모달 닫기
          _vm.closeCropperModal();

        }, 'image/jpeg'); // 또는 'image/png'
      } catch (error) {
        console.error("자른 이미지 저장 중 오류:", error);
        alert("저장 중 오류가 발생했습니다.");
      }
    },

    //썸네일 cropper 모달 닫기
    closeCropperModal() {
      const _vm = this;

      $(_vm.$refs["cropperModal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },

    async onChangeValue(){
      const _vm = this;

      _vm.$emit("update:data", _vm.localData.rawInfoItem);

      //미리보기 데이터 업데이트
      _vm.localData.rawInfoItem.managerName = _vm.memberData.list.find(
                                                (item) => item.membSeq === _vm.managerSeq > 0 ? _vm.managerSeq : _vm.eumLoginUser.userSeq
                                              )?.membName || '';
      _vm.localData.rawInfoItem.prodCountryName = _vm.CODES.CD_014.find(
                                                (item) => item.etc1 === _vm.localData.rawInfoItem.prodCountry
                                              )?.codeName || '';
      _vm.$emit("update:preData", _vm.localData.rawInfoItem);
    },

    //양식의 상태값을 확인
    updateInvalidState(invalid) {
      const _vm = this;
      const isChanged = _vm.localInvalidState != invalid;
      _vm.localInvalidState = invalid;
      if(isChanged){
        //상태값이 변경되면 부모에게 알림
        _vm.$emit("update:valid", !_vm.localInvalidState);
      }
      return false; // v-if에서 항상 false를 반환하여 실제 DOM에 렌더링되지 않게 함
    },

    docReady(){
      const _vm = this;
    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
@import '~cropperjs/dist/cropper.css';
</style>