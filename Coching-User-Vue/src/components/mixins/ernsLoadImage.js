
const IMAGE_LOADING_OPTION = {
    canvas: true
    ,downsamplingRatio: 1
    ,imageSmoothingEnabled: true
    ,maxWidth: 1920
    ,meta: true
    ,orientation: true
    ,pixelRatio: 1
}

export default {
  data() {
    return {
      
    }
  },
  computed : {
    
  },
  methods: {
    //
    async eumLoadImage(imageFile, option){
        const _vm = this;
        const opt = option || IMAGE_LOADING_OPTION;

        //console.debug(imageFile);

        let data = await window.loadImage(imageFile, opt);
        //console.debug(data);

        if (data.imageHead) {
            if (data.exif) {
                // Reset Exif Orientation data:
                window.loadImage.writeExifData(data.imageHead, data, 'Orientation', 1);
            }
        }
        
        const cMimeType = "image/jpeg";
        var dataUrl = data.image.toDataURL(cMimeType);
        var newFile = await _vm.srcToFile(dataUrl, imageFile.name, cMimeType);
        //console.debug(dataUrl);
        return {dataUrl, newFile};
    },

    async srcToFile(src, fileName, mimeType){
        return (fetch(src)
            .then(function(res){return res.arrayBuffer();})
            .then(function(buf){return new File([buf], fileName, {type:mimeType});})
        );
    },

    eumDataURLtoBlob(dataurl, filename){
        var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
        while(n--){
            u8arr[n] = bstr.charCodeAt(n);
        }

        if(filename){
            return new Blob([u8arr], filename, {type:mime});    
        }
        return new Blob([u8arr], {type:mime});
    },
  }

  /*
  window.loadImage(
    file,
    function(img, data){
      gfn_loadImageProc(img, data, function(blob, dataUrl){
        var $html = $("#card-disp-template").html();
        var $data = {};
        $data.imgSrc = dataUrl;
        $data.errorSrc = INSP_REPORT_NO_IMAGE_URL;
        $data.imageAlt = fileName;
        $data.filesIndex = filesIndex;
        filesIndex++;
      
        var renderedRow = Mustache.render($html, $data);
        $this.closest("div.row").find("div.card-section").eq($index+1).find("div.card-img").addClass("btn-add");
        $this.closest("div.card-section").html(renderedRow);
      });
    },
    {
      canvas: true
      ,downsamplingRatio: 1
      ,imageSmoothingEnabled: true
      ,maxWidth: 1920
      ,meta: true
      ,orientation: true
      ,pixelRatio: 1
    }
  )
  */
}
