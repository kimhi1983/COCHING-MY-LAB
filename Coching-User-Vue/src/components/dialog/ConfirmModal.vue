<template>
  <div class="modal" ref="erns_confirm_modal">
    <div class="layer">
      <div class="layer-content">
        <div class="modal-inner modal-sm">
          <div class="modal-content">

            <template v-if="localData.contentHtml == ''">
              <div class="modal-body only">
                <div class="text-center" v-html="localData.titleHtml"></div>
              </div>
              <div class="modal-footer">
              </div>
            </template>

            <template v-if="localData.contentHtml != ''">
              <div class="modal-header" :class="{'text-center':localData.alignCenter}">
                <div @click="cancel" class="modal-close"></div>
                <div class="title" :class="{'text-center':localData.titleAlignCenter}"
                  >{{localData.title}}</div>
              </div>
              <div class="modal-body" 
                :class="{'text-center':localData.alignCenter}"
                v-html="localData.contentHtml"></div>
            </template>

            <div class="modal-footer">
              <button type="button" @click="cancel"
                class="btn btn-md btn-gray bottom-modal-close">{{localData.cancelButtonText}}</button>
              <button type="button" @click="ok"
                class="btn btn-md btn-primary bottom-modal-close">{{localData.okButtonText}}</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'coching-ConfirmModal',
  mixins: [ernsUtils],
  components : {},
  props: {},
  watch :{
    "localData.isShow" : "onChangeShow"
  },

  data(){
    return {
      localData : {
        isShow : false,
        titleAlignCenter : false,
        alignCenter : true,

        title : this.$t('') || '확인',
        titleHtml : "",
        content : "",
        contentHtml : "",
        okButtonText : this.$t('') || '확인',
        cancelButtonText : this.$t('') || '취소',

        result : undefined
      }
    }
  },

  mounted() {
    const _vm = this;
    _vm.init();
  },

  methods : {
    init() {
      const _vm = this;
    },

    convertToHtml(pPlanText, isTitle){
      const _vm = this;
      
      const pt = (pPlanText || "").trim();
      if("" == pt){
        return "";
      }

      let retVal = "";
      const spLine = pPlanText.split("\n");
      if(isTitle){
        retVal = spLine.join("<br/>\n");
      }else{
        for(const tLine of spLine){
          retVal += `<p>${tLine}</p>\n`;
        }
      }
      

      return retVal
    },

    open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = true;
      ld.titleAlignCenter = options.titleAlignCenter == false ? false : true;
      ld.alignCenter = options.alignCenter == false ? false : true;
      ld.title = (options.title || _vm.$t('') || '확인').trim();
      ld.okButtonText = (options.okButtonText || _vm.$t('') || '확인').trim();
      ld.cancelButtonText = (options.cancelButtonText || _vm.$t('') || '취소').trim();
      ld.content = (options.content || "").trim();
      ld.contentHtml = (options.contentHtml || "").trim();

      if("" == ld.titleHtml){
        ld.titleHtml = _vm.convertToHtml(ld.title, true);
      }
      
      if("" == ld.contentHtml){
        ld.contentHtml = _vm.convertToHtml(ld.content);
      }

      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
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
      ld.result(false);
    },

    onChangeShow(){
      const _vm = this;

      const newVal =_vm.localData.isShow;
      //console.debug(newVal);
      //console.debug(_vm.$refscoching_confirm_modal);

      if(newVal == true){
        $(_vm.$refs["erns_confirm_modal"]).fadeIn(300);
        return;
      }

      $(_vm.$refs["erns_confirm_modal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },
  }
}
</script>
<style lang="scss" scoped>
.modal-header .title{
  padding-right: 0rem;
}     
</style>

<style lang="scss">

</style>
