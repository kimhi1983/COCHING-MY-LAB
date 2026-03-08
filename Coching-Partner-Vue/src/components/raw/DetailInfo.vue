<template>
  <!--원료자료-->
  <div class="section">
    <div class="title-wrap">
      <!--title-->
      <div class="title">{{ $t('') || '원료자료'}}</div>
      <div class="check-radio-wrap">
        <div class="check-title">원료 자료 노출 여부</div>
        <div class="chehck-radio-flex">
          <div>
            <div class="radiobox">
              <input v-model="localData.detailData.useYn" id="data-open" type="radio" name="material-data" value="Y"/>
              <label for="data-open" class="radiobox-label">노출</label>
            </div>
          </div>
          <div>
            <div class="radiobox">
              <input v-model="localData.detailData.useYn" id="data-hidden" type="radio" name="material-data" value="N"/>
              <label for="data-hidden" class="radiobox-label">숨김</label>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!--section-content-->
    <validation-observer tag="div" ref="rawDetailForm" class="section-content" #default="{ invalid }">
      <!-- invalid 값을 로컬 데이터에 바인딩 -->
      <div v-if="updateInvalidState(invalid)"></div>
      <!--base-add-->
      <div class="base-add">
        <div class="header">
          <div>{{ $t('') || '타이틀'}}<span class="text-primary">*</span></div>
        </div>
        <validation-provider #default="{ errors }" tag="div"
          class="body":name="$t('') || '타이틀'" rules="required">
          <div class="item">
            <div class="add-wrap">
              <input v-model="localData.detailData.title" type="text" placeholder="타이틀을 입력해주세요" />
            </div>
          </div>
          <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
        </validation-provider>
      </div>
      <div class="base-add">
        <div class="header">
          <div>{{ $t('') || '해쉬태그'}}</div>
        </div>
        <div class="body">
          <div class="item">
            <validation-provider #default="{ errors }" tag="div"
              class="add-wrap" :name="$t('') || '해쉬태그'" rules="max:200|checkHashtag|checkHashtagCnt|checkDupHashtag" style="display: block;">
              <input v-model="localData.detailData.hashtag" type="text" placeholder="#글리세린#수부지" />
              <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
            </validation-provider>
          </div>
        </div>
      </div>
      <!--파일첨부-->
      <div class="date-wrap">
        <div class="title">{{ $t('') || '파일첨부'}}</div>
        <div class="file-register">
          <!--upload-->
          <div @click="handleInputFile" 
            @dragover.prevent="handleDragOver"
            @dragenter.prevent="handleDragEnter"
            @dragleave="handleDragLeave"
            @drop.prevent="handleDrop" class="upload-wrap">
            <div class="upload-area" id="uploadArea">
              <input @change="handleFileChange" type="file" ref="fileInput" id="fileInput" multiple style="display: none" />
              <div id="fileList">
                <div v-for="(item, index) of localData.detailData.filelist" :key="index"
                  class="file-item">
                  <div @click.stop="onClickFileDownload(item)" class="file-wrap">
                    <div class="file-name">
                      <span>{{ item.fileName.split('.')[0] }}</span>
                      <span class="file-extension">{{ '.' + item.fileName.split('.').pop() }}</span>
                    </div>
                    <div class="file-size">({{ eufmtFileSize(item.fileSize) }})</div>
                  </div>
                  <button @click.stop="deleteFile(index)" type="button" class="delete-button"></button>
                </div>
              </div>
              <p v-show="localData.detailData.filelist.length === 0">{{ $t('') || '파일을 마우스로 끌어 오거나 클릭하세요' }}</p>
            </div>
          </div>
          <!--textarea-->
          <!-- <textarea v-model="localData.detailData.rawDesc" :placeholder=" $t('') || '원료 자료에 대한 설명을 작성해주세요'"></textarea> -->
        </div>
      </div>

      <!--직접입력-->
      <validation-provider #default="{ errors }" tag="div"
            class="date-wrap" :name="$t('') || '직접입력'" rules="">
        <div class="title">{{ $t('') || '직접입력'}}</div>
        <quillEditor ref="rawDetailEditor" v-model="localData.detailData.rawDetail" :options="editorOptions" />
      </validation-provider>
    </validation-observer>

    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { extend } from 'vee-validate';
import { quillEditor } from 'vue-quill-editor'
import Quill from 'quill';
import { getDetail, uploadFile } from '@/api/coching/comm/raw';
import { DEF_PREVIEW_DETAIL, DEF_PREVIEW_CONTENTS } from '@/constants/rawPreview';

const Block = Quill.import('blots/block');
const Link  = Quill.import('formats/link'); // Inline 은 더 이상 필요 없으면 제거

// 공통 Block Blot 생성 함수
function makeBlockBlot(blotName, className) {
  class CustomBlock extends Block {}
  CustomBlock.blotName  = blotName;
  CustomBlock.tagName   = 'div';
  CustomBlock.className = className;
  return CustomBlock;
}

// ★ div 기반 블록들
const RawNoteBlock             = makeBlockBlot('raw-note',              'raw-note');
const RawCompanyBlock          = makeBlockBlot('raw-company',           'raw-company');
// raw-comp-info 를 더 이상 안 쓰면 이 줄은 생략 가능
const RawCompInfoBlock         = makeBlockBlot('raw-comp-info',         'raw-comp-info');
const RawCompAboutBlock        = makeBlockBlot('raw-comp-about',        'raw-comp-about');
const RawCompMainProductsBlock = makeBlockBlot('raw-comp-main-products','raw-comp-main-products');

// ★ 여기 중요: name / tel / email 도 이제 div 블록
const RawCompNameBlock         = makeBlockBlot('raw-comp-name',         'raw-comp-name');
const RawCompTelBlock          = makeBlockBlot('raw-comp-tel',          'raw-comp-tel');
const RawCompEmailBlock        = makeBlockBlot('raw-comp-email',        'raw-comp-email');

// a.raw-comp-home-url → Link 포맷
class RawCompHomeUrlLink extends Link {}
RawCompHomeUrlLink.blotName  = 'raw-comp-home-url';
RawCompHomeUrlLink.tagName   = 'a';
RawCompHomeUrlLink.className = 'raw-comp-home-url';

// Quill 에 등록
Quill.register(
  {
    'formats/raw-note':               RawNoteBlock,
    'formats/raw-company':            RawCompanyBlock,
    'formats/raw-comp-info':          RawCompInfoBlock,          // 안 쓰면 이 라인 제거
    'formats/raw-comp-about':         RawCompAboutBlock,
    'formats/raw-comp-main-products': RawCompMainProductsBlock,
    'formats/raw-comp-name':          RawCompNameBlock,
    'formats/raw-comp-tel':           RawCompTelBlock,
    'formats/raw-comp-email':         RawCompEmailBlock,
    'formats/raw-comp-home-url':      RawCompHomeUrlLink,
  },
  true
);

const DEF_FILE ={
  fileId: null,
  fileName: '',
  file: null,
};

const DEF_DETAIL_INF = {
  rawDetailSeq: null,
  rawSeq: null,
  membSeq: null,
  title: '',
  hashtag: '',
  filelist: [],
  delfilelist: [],
  fileId: '',
  rawDesc: '',
  rawDetail: '', // 에디터 내용
  detailFileList: [],
  htmlFileList: [],
  useYn: 'N',
  delYn: 'N',
}

export default {
    name: 'coching-rawInfo',
    mixins: [ernsUtils],
    components: {
      quillEditor,
    },
    props: {
        rawInfo: {
          type : Object,
          defalt: {}
        },
        managerSeq: {
          type: Number,
          default: 0,
        },
    },
    computed: {
        
    },
    watch: {
      '$i18n.locale' : function(){
        const _vm = this;
      },
      rawInfo: function() {
        const _vm = this;
        
        _vm.init();
      },
      'localData.detailData': {
        handler(newVal) {
          this.onChangeValue();
        },
        deep: true, // 객체 내부 속성 변경 감지
      },
    },
    data() {
        return { 
          localInvalidState: false, //양식폼의 validate 상태
          status: '',
          CODES: {
            USE_001 : [],
          },
          localData:{
            detailData: {...DEF_DETAIL_INF},
            htmlImgList: [],
          },
          editorOptions: {
            placeholder: "내용을 입력하세요...",
            theme: "snow", // "snow" 또는 "bubble"
            modules: {
              toolbar: {
                container: [
                  ["bold", "italic", "underline", "strike"], // 텍스트 스타일
                  [{ 'header': [1, 2, 3, 4, 5, 6, false] }], // 헤더 크기
                  [{ list: "ordered" }, { list: "bullet" }], // 리스트
                  [{ indent: "-1" }, { indent: "+1" }], // 들여쓰기
                  [{ color: [] }, { background: [] }], // 색상
                  [{ font: [] }], // 폰트
                  [{ align: [] }], // 정렬
                  ["link", "image", "video"], // 링크, 이미지, 비디오
                  ["clean"], // 포맷 제거
                ],
                handlers: {
                  // 이미지 핸들러 추가
                  image: async () => {
                    const input = document.createElement("input");
                    input.setAttribute("type", "file");
                    input.setAttribute("accept", "image/png, image/jpeg"); // PNG, JPEG만 허용
                    input.click();

                    input.onchange = async () => {
                      const file = input.files[0];

                      if (!file) return;

                      // 파일 크기 제한 (10MB = 10 * 1024 * 1024 바이트)
                      const MAX_SIZE_MB = 10 * 1024 * 1024;
                      if (file.size > MAX_SIZE_MB) {
                        alert("파일 크기는 10MB 이하만 업로드할 수 있습니다.");
                        return;
                      }

                      // 잘못된 MIME 타입이면 업로드 차단
                      if (!["image/png", "image/jpeg"].includes(file.type)) {
                        alert("PNG 또는 JPEG 형식의 이미지만 업로드 가능합니다.");
                        return;
                      }

                      // 업로드 로직 (예: 서버로 전송 후 URL 반환)
                      try {
                        const imageUrl = await this.uploadImage(file);
                        const quill = this.$refs.rawDetailEditor.quill;
                        const range = quill.getSelection();
                        quill.insertEmbed(range.index, "image", imageUrl);
                      } catch (error) {
                        console.error("이미지 업로드 실패:", error);
                      }
                    };
                  },
                },
              },
            },
          },
          isDragging: false,
        }
    },
  async mounted(){
    const _vm = this;

    function checkHashtag() {
      extend('checkHashtag', {
        validate: value => {
          const hashtagRegex = /^#([\p{L}\p{N}\p{S}\p{P}]+)(#[\p{L}\p{N}\p{S}\p{P}]+)*$/u;
          return hashtagRegex.test(value) || _vm.$t('') || '해쉬태그는 #으로 시작하며, 공백 없이 입력해야 합니다.';
        },
		  });
	  } 
    // 메시지 업데이트 호출
    checkHashtag();

    function checkDupHashtag() {
      extend('checkDupHashtag', {
        validate: value => {
          const doubleHashRegex = /##+/;
          return !doubleHashRegex.test(value) || _vm.$t('') || '해쉬태그는 #을 연속으로 입력할 수 없습니다.';
        },
		  });
	  } 
    // 메시지 업데이트 호출
    checkDupHashtag();

    function checkHashtagCnt() {
      extend('checkHashtagCnt', {
        validate: value => {
           // `#`으로 시작하는 단어들을 배열로 변환
          const hashtags = value.match(/#[가-힣a-zA-Z0-9]+/g) || [];

          if (hashtags.length > 20) {
            return _vm.$t('') || '해쉬태그는 최대 20개까지 입력할 수 있습니다.';
          }

          return true;
        },
		  });
	  } 
    // 메시지 업데이트 호출
    checkHashtagCnt();

    this.$nextTick(() => {
      const quill = this.$refs.editor?.quill; // Quill 인스턴스
      if (quill) {
        quill.setContents([]); // 빈 Delta로 초기화
      }
    });

    await _vm.loadCodes();
    _vm.docReady();
    _vm.fetchData();    
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);

      try{
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    }, 

    async init(){
      const _vm = this;

      //수정일때 정보 들고오기
      if(_vm.rawInfo.rawSeq){
        await _vm.loadDetail();
      }
    },

    async loadCodes(){
      const _vm = this;

      _vm.CODES.USE_001 = [
        {value:"Y", label : _vm.$t('') || '노출'},
        {value:"N", label : _vm.$t('') || '숨김'}
      ];
    },

    async loadDetail(){
      const _vm = this;

      const res = await getDetail({rawSeq: _vm.rawInfo.rawSeq, membSeq: _vm.managerSeq});
      const { resultCode, resultFailMessage, resultData } = res;

      _vm.localData.detailData = {...DEF_DETAIL_INF, ...resultData};
    },

    handleInputFile(){
      this.$refs.fileInput.click();
    },
    //File click
    handleFileChange(event) {
      this.handleFileUpload(event.target.files);
      event.target.value = ''; // 파일 입력 필드 초기화
    },
     //file drag
     handleDrop(event) {
      event.preventDefault();
      this.handleFileUpload(event.dataTransfer.files);
      event.target.value = ''; // 파일 입력 필드 초기화
    },
    handleDragOver(event) {
      this.isDragging = true;
      event.dataTransfer.dropEffect = 'copy'; // 드롭 효과를 'copy'로 설정
    },
    handleDragEnter(event) {
      this.isDragging = true;
    },
    handleDragLeave() {
      this.isDragging = false;
    },
    async handleFileUpload(files) {
      const _vm = this;
      const allowedExtensions = ['pdf', 'ppt', 'pptx', 'doc', 'docx', 'jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'];

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

          this.file = file;
      
          const idx = _vm.localData.detailData.filelist.length;
          _vm.addFile();

          _vm.localData.detailData.filelist[idx].file = this.file;  
          _vm.localData.detailData.filelist[idx].fileName = this.file.name;  
          _vm.localData.detailData.filelist[idx].fileSize = this.file.size;

          _vm.onChangeValue();
        }
      }
    },
    //파일 첨부 다운로드
    onClickFileDownload(file){
      const _vm = this;

      const downloadPath = _vm.eumFileDownPath(file.fileId);
      document.location.href = downloadPath;
    },
    async deleteFile(idx){
      const _vm = this;
      const file = _vm.localData.detailData.filelist[idx];

      if(file.fileId){
        if (!_vm.localData.detailData.delfilelist.includes(file.fileId)) {
          _vm.localData.detailData.delfilelist.push(file.fileId);
        }
      }
      _vm.localData.detailData.filelist.splice(idx, 1);
    },
    addFile(){
      const _vm = this;

      _vm.localData.detailData.filelist.push({...DEF_FILE});
    },

    async uploadImage(file) {
      const _vm = this;

      const formData = new FormData();
      formData.append('rawDetailSeq', _vm.localData.detailData.rawDetailSeq || 0);
      formData.append('rawSeq', _vm.rawInfo.rawSeq || 0);
      formData.append('membSeq', _vm.managerSeq);
      formData.append(`raw_files_0`, file, file.name);
      formData.append('fileType', "PREVIEW");

      const res = await uploadFile(formData);
      const { resultCode, resultFailMessage, resultData } = res;
      const fileData = resultData.list[0]
      const url = _vm.eumFileImagePath(fileData.fileId, '0');
      _vm.localData.detailData.htmlFileList.push(fileData);

      return url; // 업로드된 이미지 URL 반환
    },
    
    async onChangeValue(){
      const _vm = this;

      _vm.$emit("update:data", _vm.localData.detailData);
      const preData = {
        detail: {..._vm.localData.detailData},
        contents: {
          detail: {..._vm.localData.detailData},
          files: [..._vm.localData.detailData.filelist],
        }
      }
      _vm.$emit("update:preData", preData);
    },

    //양식의 상태값을 확인
    updateInvalidState(invalid) {
      const _vm = this;
      const isChanged = this.localInvalidState != invalid;
      this.localInvalidState = invalid;
      if(isChanged || !invalid){
        //상태값이 변경되면 부모에게 알림
        _vm.$emit("update:valid", !this.localInvalidState);
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
/*.body*/
.body .info {
  font-size: 0.875rem;
  color: var(--color--gray-666);
  padding-top: 0.5rem;
  font-weight: 600;
}
.body .info:nth-child(n + 3) {
  padding-top: 0.25rem;
}
.body .info.error,
.body .info.success {
  display: flex;
  display: none;
  padding-left: 1.4rem;
  text-indent: -0.375rem;
}

.body .info.error {
  color: #f43333;
}

.body .info.success {
  color: #00b24d;
}
.body .info.success.disabled {
  color: #aaaaaa;
}
.body .info.error:before,
.body .info.success:before {
  content: "";
  display: inline-block;
  width: 1.125rem;
  height: 1.125rem;
  background-size: cover;
  background-repeat: no-repeat;
  margin-left: -1.125rem;
  margin-right: 0.375rem;
  margin-top: -0.15rem;
  vertical-align: middle;
}

.body .info.error:before {
  background-image: url("~@/assets/images/ic-error-red-md.svg");
}
.body .info.success:before {
  background-image: url("~@/assets/images/ic-success-green-md.svg");
}
.body .info.success.disabled:before {
  background-image: url("~@/assets/images/ic-success-gray-md.svg");
}
</style>

<style lang="scss">
@import "~quill/dist/quill.core.css";  // core css
@import "~quill/dist/quill.snow.css";  // Snow 테마
@import "~quill/dist/quill.bubble.css"; // Bubble 테마 (선택 사항)

blockquote:before, blockquote:after, q:before, q:after {
  content: '';
}
blockquote:before, blockquote:after, q:before, q:after {
  content: '';
}

.ql-editor .raw-company{
  max-width: 200px;
}

.ql-editor .raw-comp-name,
.ql-editor .raw-comp-tel,
.ql-editor .raw-comp-email {
  display: inline-block;   /* div 를 한줄에 정렬 */
}

/* 2개 이상일 때만 앞에 | 붙이기 */
.ql-editor .raw-comp-name + .raw-comp-tel::before,
.ql-editor .raw-comp-name + .raw-comp-email::before,
.ql-editor .raw-comp-tel + .raw-comp-email::before {
  content: "|";
  margin: 0 6px;
  color: #ccc;
}

.ql-editor .raw-comp-about{
  color: var(--color--gray-666);
}
</style>