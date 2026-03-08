<template>
  <div>
    <pdf
      :src="pdfUrl"
      :page="currentPage"
      @password="handlePassword"
      style="width: 100%; height: 800px;"
    ></pdf>
    <div style="margin-top: 10px;">
      <button @click="prevPage" :disabled="currentPage <= 1">Previous</button>
      <button @click="nextPage" :disabled="currentPage >= totalPages">Next</button>
      <span>Page: {{ currentPage }} / {{ totalPages }}</span>
    </div>
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
    return {
      currentPage: 1,
      totalPages: 0,
    };
  },
  methods: {
    prevPage() {
      if (this.currentPage > 1) this.currentPage--;
    },
    nextPage() {
      if (this.currentPage < this.totalPages) this.currentPage++;
    },
    handlePassword(updatePassword, reason) {
      if (reason === 1) {
        const password = prompt('Enter the password to access this PDF');
        updatePassword(password);
      }
    },
  },
  watch: {
    pdfUrl() {
      this.currentPage = 1; // PDF 변경 시 페이지를 첫 페이지로 초기화
    },
  },
};
</script>

<style scoped>
button {
  margin: 5px;
  padding: 5px 10px;
}
</style>
