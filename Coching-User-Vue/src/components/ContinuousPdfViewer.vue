<template>
  <div>
    <pdf
      v-for="page in totalPages"
      :key="page"
      :src="src"
      :page="page"
      style="width: 100%; margin-bottom: 20px;"
      @password="handlePassword"
    ></pdf>
    <div v-if="loadError" style="color: #ccc;">PDF 파일을 불러오는 데 실패했습니다.</div>
  </div>
</template>

<script>
import pdf from 'vue-pdf';
import * as pdfjsLib from 'pdfjs-dist/build/pdf';
import pdfjsWorker from 'pdfjs-dist/build/pdf.worker.entry';

pdfjsLib.GlobalWorkerOptions.workerSrc = pdfjsWorker;


export default {
  components: {
    pdf,
  },
  props: {
    pdfUrl: {
      type: String,
      required: true,
    },
  },
  data() {
    const loadingTask = pdf.createLoadingTask(this.pdfUrl);

    return {
      src: loadingTask,
      totalPages: undefined,
      loadError: false,
    };
  },
  methods: {
    async fetchTotalPages() {
      const _vm = this;

      if(!_vm.pdfUrl){
        this.loadError = true;
        _vm.$emit("onFetchedPages", _vm.totalPages);
        return;
      }

      try {
        _vm.src.promise.then(pdf => {
          _vm.totalPages = pdf.numPages;
          _vm.$emit("onFetchedPages", _vm.totalPages);
        }).catch(error => {
          _vm.loadError = true;

          if (error?.name === 'MissingPDFException') {
            console.warn('PDF 파일을 찾을 수 없습니다.');
          } else {
            console.error('PDF 로딩 실패:', error);
          }

          _vm.$emit("onFetchedPages", undefined);
        });

      
      } catch (error) {
        this.loadError = true;
        console.error('Error fetching PDF pages:', error);
        _vm.$emit("onFetchedPages", _vm.totalPages);
      } 
    },
    handlePassword(updatePassword, reason) {
      if (reason === 1) {
        const password = prompt('Enter the password to access this PDF');
        updatePassword(password);
      }
    },
  },
  async mounted() {
    await this.fetchTotalPages();
  },
};
</script>

<style scoped>
.pdf-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>
