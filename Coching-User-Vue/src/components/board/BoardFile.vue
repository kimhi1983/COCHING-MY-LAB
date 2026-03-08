<template>
  <div>
    <!-- 제출 및 수정 -->
    <div v-if='pageType == "submit"' class="upload-wrap">
      <div @click="handleInputFile" 
        @dragover.prevent="handleDragOver"
        @dragenter.prevent="handleDragEnter"
        @dragleave="handleDragLeave"
        @drop.prevent="handleDrop" class="upload-wrap">
        <div class="upload-area" id="uploadArea">
          <input @change="handleFileChange" type="file" ref="fileInput" id="fileInput" multiple style="display: none" />
          <div id="fileList">
            <div v-for="(item, index) of fileData.filelist" :key="index"
              class="file-item">
              <div class="file-wrap">
                <div class="file-name">
                  <span>{{ item.fileName.split('.')[0] }}</span>
                  <span class="file-extension">{{ '.' + item.fileName.split('.').pop() }}</span>
                </div>
                <div class="file-size">({{ eufmtFileSize(item.fileSize) }})</div>
              </div>
              <button @click.stop="deleteFile(index)" type="button" class="delete-button"></button>
            </div>
          </div>
          <p v-show="fileData.filelist.length === 0">{{ $t('') || '파일을 마우스로 끌어 오거나 클릭하세요' }}</p>
        </div>
      </div>
    </div>

    <!-- 읽기 및 다운 -->
    <div v-if='pageType == "read"' class="file-wrap">
      <div class="total-wrap">
        <div class="total-num">
          첨부파일
          <span>{{ fileData.filelist.length }}</span>
        </div>
        <a href="javascript:;" class="btn-save" @click="onClickAllFileDownload(fileData.filelist)">모두저장</a>
      </div>

      <div class="file-list">
        <a v-for="(item, index) of fileData.filelist" :key="index" href="javascript:;">
          <div class="title-wrap">
            <span class="title">
              <span>{{ item.fileName.split('.')[0] }}</span>
              <span class="file-extension">{{ '.' + item.fileName.split('.').pop() }}</span>
            </span>
            <span class="date"><div class="file-size">({{ eufmtFileSize(item.fileSize) }})</div></span>
          </div>
          <span class="ic-md ic-download" @click="onClickFileDownload(item)"></span>
        </a>
      </div>
    </div>

  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

const DEF_FILE ={
  fileId: null,
  fileName: '',
  file: null,
};

export default {
  name: 'BoardFile',
  mixins: [ernsUtils],
  computed: {
    
  },
  watch: {
    '$route': 'fetchData' //라우트가 변경되면 메소드를 다시 호출됩니다.
    /*locale 변경감지*/
    ,'$i18n.locale' : function(){this.isLoadTerms=false;} //다시로드하도록 처리
  },
  props:{
    fileData: {
      type: Object,
      required: true,
    },
    pageType: {
      type: String,
      required: true,
      default: 'read'
    },
  },
  async mounted(){
    const _vm = this;

    _vm.fetchData();    
  },
  data(){
    return {
    }
  },
  methods : {

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

    async loadList(){
      const _vm = this;

      _vm.loading(true);
      try {

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

    },

    /* 첨부파일 등록 및 삭제 */
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

      if (files.length > 0) {
        for(let i = 0; i < files.length; i++) {
          this.file = files[i];
          const idx = _vm.fileData.filelist.length;
          _vm.addFile();

          _vm.fileData.filelist[idx].file = this.file;  
          _vm.fileData.filelist[idx].fileName = this.file.name;  
          _vm.fileData.filelist[idx].fileSize = this.file.size;

          _vm.onChangeValue();
        }
      }
    },
    async deleteFile(idx){
      const _vm = this;
      const file = _vm.fileData.filelist[idx];

      if(file.fileId){
        if (!_vm.fileData.delfilelist.includes(file.fileId)) {
          _vm.fileData.delfilelist.push(file.fileId);
        }
      }
      _vm.fileData.filelist.splice(idx, 1);
    },
    addFile(){
      const _vm = this;

      _vm.fileData.filelist.push({...DEF_FILE});

    },

    async onChangeValue(){
      const _vm = this;

      _vm.$emit("update:fileData", _vm.fileData);
    },

    /* 첨부파일 다운로드 */
    // 저장
    onClickFileDownload(file) {
      const _vm = this;

      const downloadPath = _vm.eumFileDownPath(file.fileId);
      document.location.href = downloadPath;
    },

    // 모두저장
    onClickAllFileDownload(filelist) {
      const _vm = this;
      
      filelist.reduce((promise, file) => {
        return promise.then(() => this.downloadFile(file));
      }, Promise.resolve()); // 초기 Promise 설정
    },

    downloadFile(file) {
      return new Promise((resolve) => {
        const downloadPath = this.eumFileDownPath(file.fileId);

        const link = document.createElement('a');
        link.href = downloadPath;
        link.download = file.fileName || '';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        // 다운로드 실행 후 약간의 대기 시간 (필수는 아님)
        setTimeout(resolve, 100); // 브라우저의 다운로드 처리가 끝난 후 resolve 호출
      });
    }
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
