<template>
  <div>
    <!--쪽지보내기 modal-->
    <div class="modal" ref="image_cropper_modal">
      <div class="layer">
        <div class="layer-content">
          <div class="modal-inner modal-lg">
            <div class="modal-content">
              <div class="modal-body p-t-0">
                <!--img-upload-wrap-->
                <div class="img-upload-edit" style="height: 500px;">
                  <Cropper
                    ref="cropper"
                    :src="localData.file.blobUrl"
                    :aspect-ratio="1"
                    :view-mode="1"
                    :auto-crop-area="0.5"
                    :background="false"
                    :responsive="true"
                    :zoomable="true"
                    :movable="true"
                    :scalable="true"
                    :crop-box-movable="true"
                    :crop-box-resizable="true"
                    style="height: 100%; width: 100%;"
                  />
                </div>
              </div>
              <div class="modal-footer">
                <button @click="cancel" type="button" class="btn btn-md btn-gray bottom-modal-close">취소</button>
                <button @click="saveCroppedImage" type="button" class="btn btn-md btn-primary">저장하기</button>
              </div>
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
import { isUserLoggedIn } from '@/auth/utils';
import { uploadProfileFile } from '@/api/coching/member/member';

import VueCropper from 'vue-cropperjs';

const DEF_FILE ={
  fileId: null,
  fileName: '',
  file: null,
  blobUrl: null, // Blob URL 추가
};

export default {
  name: 'coching-message-send-modal',
  mixins: [ernsUtils],
  components: {
    Cropper: VueCropper,
  },
  props: {    
  },
  computed: {      
  },
  watch: {
    '$i18n.locale' : async function(){
      const _vm = this;
      await _vm.loadCodes();
      _vm.$nextTick(() => {});
    },
    "localData.isShow" : "onChangeShow"
  },
  created() {
    const _vm = this;
    
  },
  data() {
    return {
      isLoggedIn: true,

      localData : {
        isShow : false,
        titleAlignCenter : false,
        alignCenter : true,

        file : { ...DEF_FILE },

        title : this.$t('') || '프로필 사진',
        okButtonText : this.$t('') || '등록하기',
        cancelButtonText : this.$t('') || '취소',

        result : undefined
      } 
    }
  },
  async mounted(){
    const _vm = this;
    _vm.init();
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    init() {
      const _vm = this;

      _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;
    },

    open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = true;

      ld.file = options.file || '';

      ld.title = (options.title || _vm.$t('') || '프로필 사진').trim();
      ld.okButtonText = (options.okButtonText || _vm.$t('') || '등록하기').trim();
      ld.cancelButtonText = (options.cancelButtonText || _vm.$t('') || '취소').trim();

       _vm.$nextTick(() => {
        if (_vm.$refs.cropper && ld.file.blobUrl) {
          _vm.$refs.cropper.replace(ld.file.blobUrl);
        }
      });
      
      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
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
          const file = new File([blob], _vm.localData.file.fileName || 'cropped.png', { type: 'image/png' });

          const formData = new FormData();
          formData.append('ptnSeq', _vm.eumLoginUser.userSeq || 0);
          formData.append(`profile_files_0`, file, file.name);
          formData.append('fileType', "PROFILE");
          formData.append('strDelFileIds', _vm.localData.file.fileId || ''); // 기존 파일 ID 삭제

          const res = await uploadProfileFile(formData);
          const { resultCode, resultFailMessage, resultData } = res;
          const result = resultData.list[0] || {};

           _vm.localData.file = {
            fileId: result.fileId, // 파일 ID 초기화
            fileName: file.name,
            file: file,
            blobUrl: URL.createObjectURL(file) // Blob URL 생성
          };

          _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '프로필 사진진',
              titleHtml : '저장 되었습니다.'
            });

          _vm.ok();
          

        }, 'image/jpeg'); // 또는 'image/png'

      } catch (error) {
        console.error("자른 이미지 저장 중 오류:", error);
        alert("저장 중 오류가 발생했습니다.");
      }
    },

    ok(){
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = false;
      ld.result(true);
    },

    cancel(){
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = false;
      ld.file = { ...DEF_FILE }; // 초기화
      ld.result(false);
    },

    onChangeShow(){
      const _vm = this;

      const newVal =_vm.localData.isShow;
      //console.debug(newVal);
      //console.debug(_vm.$refscoching_alert_modal);

      if(newVal == true){
        $(_vm.$refs["image_cropper_modal"]).fadeIn(300);
        return;
      }

      $(_vm.$refs["image_cropper_modal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },
  }  
}
</script>

<style lang="scss" scoped>
.input-wrap .info.error {
  padding-top: 0.7rem; 
}
</style>

<style lang="scss">
@import '~cropperjs/dist/cropper.css';
</style>