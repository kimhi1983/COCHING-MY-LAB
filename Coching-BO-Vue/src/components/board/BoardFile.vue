<template>
  <div class="board-file-list">
    
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
                <!-- <div class="file-size">({{ eufmtFileSize(item.fileSize) }})</div> -->
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
      <div class="file-list">
        <b-button variant="outline-primary" size="sm" @click="onClickAllFileDownload(fileData.filelist)">
          모두저장 <span>({{ fileData.filelist.length }}개)</span>
        </b-button>
        <a v-for="(item, index) of fileData.filelist" :key="index" href="javascript:;">
          <div class="title-wrap">
            <span class="title">
              <span>{{ item.fileName.split('.')[0] }}</span>
              <span class="file-extension">{{ '.' + item.fileName.split('.').pop() }}</span>
            </span>
            <!-- <span class="date"><div class="file-size">({{ eufmtFileSize(item.fileSize) }})</div></span> -->
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
    allowedExtensions: {
      type: Array,
      default: () => [],
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

          // 확장자 체크
          const extension = this.file.name.split('.').pop().toLowerCase();

          if (_vm.allowedExtensions.length > 0 && !_vm.allowedExtensions.includes(extension)) {
            alert(`허용되지 않는 파일 형식입니다: ${this.file.name}`);
            continue; // 다음 파일로 넘어감
          }
      
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
.board-file-list {
  
  .ic-download {
    width: 20px;
    height: 20px;
    background-image: url("~@/assets/images/coching/ic-download-gray-md.svg");
  }

  .file-wrap .file-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
  }

  .file-wrap .file-list a {
    display: flex;
    align-items: center;
    /* border: 1px solid var(--color--border-ddd); */
    padding: 0.625rem 0.875rem;
    border-radius: 0.25rem;
    background-color: #ffffff;
    background-color: #f5f5f5;
  }
  @media screen and (min-width: 1024px) {
    .file-wrap .file-list a:hover {
      /* border-color: var(--color--bk); */
      background-color: #e9e9e9;
    }
  }
  .file-wrap .file-list .title-wrap {
    display: flex;
    align-items: center;
    gap: 0.375rem;
    width: calc(100% - 1.125rem);
    padding-right: 1.5rem;
  }
  .file-wrap .file-list .title-wrap .title {
    font-size: 0.875rem;
    color: #222222;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: 600;
  }
  .file-wrap .file-list .title-wrap .date {
    font-size: 0.75rem;
    color: #999999;
  }

  /*upload-area*/
  .upload-area {
    position: relative;
    width: 100%;
    margin: 0 auto;
    border: 2px dashed #dddddd;
    border-radius: 0.25rem;
    text-align: center;
    min-height: 6.25rem;
    max-height: 12.5rem;
    padding: 1.5rem;
    overflow: auto;
    cursor: pointer;
  }
  .upload-area p {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    color: #999999;
    font-size: 0.9375rem;
    white-space: nowrap;
  }
  .upload-area p:before {
    content: "";
    width: 1.125rem;
    height: 1.125rem;
    display: inline-block;
    background-image: url("~@/assets/images/coching/ic-fileplus-gray-md.svg");
    vertical-align: middle;
    margin: -0.25rem 0.375rem 0 0;
  }
  #fileList {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
  }

  .file-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.625rem 0.875rem;
    border-radius: 0.25rem;
    position: relative;
    border: 1px solid #999999;
    gap: 1rem;
  }

  .file-item .file-wrap {
    display: flex;
    align-items: center;
    overflow: hidden;
  }
  .file-item .file-name {
    font-weight: 600;
    display: flex;
    font-size: 0.875rem;
  }
  .file-item .file-name span:first-child {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    max-width: 12.5rem;
    display: inline-block;
  }
  .file-item .file-size {
    color: #666666;
    font-size: 0.75rem;
    padding-left: 0.5rem;
    white-space: nowrap;
  }
  .delete-button {
    border-radius: 0.25rem;
    width: 1.25rem;
    height: 1.25rem;
    background-color: rgba(0, 0, 0, 0.8);
    background-size: 0.875rem 0.875rem;
    background-image: url("~@/assets/images/coching/ic-delete-wh-xsm.svg");
    background-repeat: no-repeat;
    background-position: center;
  }

  @media screen and (max-width: 1023px) {
    .write-view .send-card-wrap {
      justify-content: flex-start;
    }
  }
  @media (max-width: 767px) {
    .write-form textarea {
      height: 18.75rem;
    }
    .upload-area {
      padding: 1rem;
    }
    #fileList {
      width: 100%;
      display: flex;
      flex-wrap: wrap;
      gap: 0.25rem;
    }
    .file-item {
      padding: 0.5rem 0.75rem;
      gap: 0.75rem;
    }
    .board-view-content .file-wrap .file-list a {
      padding: 0.5rem 0.75rem;
    }
    .file-item .file-name {
      font-size: 0.8125rem;
    }
    .file-item .file-size {
      padding-left: 0.375rem;
    }
  }
}
</style>

<style lang="scss">
</style>
