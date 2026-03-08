
const IMAGE_FILE_EXTS = [
  'jpg','png','jfif','jpge','gif'
];

export default {
  data() {
    return {

    }
  },
  filters: {
    
  },
  methods: {
    //Dropzone 에 기존 파일 추가
    eumDzAddMockupFilesInDropzone(dzRef, files, size){
      const _vm = this;
      
      dzRef.removeAllFiles(true);
      console.debug(files);

      for(const file of files){
        const url = _vm.eumFileImagePathWithSize(file.fileId, '1', size);

        const fObj = {
          size : file.fileSize,
          name : file.fileName,
          type : file.fileExt,
          ...file,
          dataURL: url
        };
        dzRef.manuallyAddFile(fObj, url);
        if(IMAGE_FILE_EXTS.includes(fObj.type)){
          dzRef.dropzone.emit('thumbnail', fObj, fObj.dataURL);
          dzRef.dropzone.emit('complete', fObj);
        }
      }
    },

    eumDzHandleFileAdded(data) {
      const _vm = this;
      console.debug("eumDzHandleFileAdded");
      console.debug(data);
      data.previewElement.addEventListener("click", function(){
        const pData = data;
        if(pData.fileId){
          _vm.eumFileDownload({
            downUrl:_vm.eumFileDownPath(pData.fileId), 
            fileName : pData.fileName
          });
        }        
      });
    },
    
  }
}