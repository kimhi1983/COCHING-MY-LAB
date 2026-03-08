<template>
  <!--쪽지보내기 modal-->
  <div class="modal" ref="erns_message_read_modal">
    <div class="layer">
      <div class="layer-content">
        <div class="modal-inner modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <div @click="cancel" class="modal-close"></div>
              <div class="title">쪽지 내용
                <span> {{ localData.senderName }}</span>
              </div>
            </div>
            <div class="modal-body">
              <textarea 
                v-model="localData.content"
                readonly
                class="read-only"
                rows="10" placeholder="내용을 입력해 주세요"></textarea>
            </div>
            <div class="modal-footer">
              <button type="button" @click="cancel"
                class="btn btn-md btn-gray bottom-modal-close w-100">{{localData.cancelButtonText}}</button>
              <button 
                v-if="localData.mode == 'recv'"
                type="button" @click="onClickReSendMessage"
                class="btn btn-md btn-primary bottom-modal-close w-100">{{localData.okButtonText}}</button>
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
import { EventBus } from '@/components/eventBus'; 
import { isUserLoggedIn, getUserData} from '@/auth/utils';
import { DEF_MESSAGE_INF, MESSAGE_OWN_MODE} from '@/constants/message';

export default {
  name: 'coching-message-read-modal',
  mixins: [ernsUtils],
  components: {
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
  data() {
    return {
      isLoggedIn: false,

      localData : {
        isShow : false,
        titleAlignCenter : false,
        alignCenter : true,

        mode : MESSAGE_OWN_MODE.RECIVED,

        title : this.$t('') || '쪽지 내용',        
        okButtonText : this.$t('') || '쪽지 보내기',
        cancelButtonText : this.$t('') || '확인',

        ...DEF_MESSAGE_INF,

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

      ld.mode = options.mode;
      ld.messageSeq = options.messageSeq;
      ld.content = options.content || '';
      ld.sender = options.sender;
      ld.senderName = options.senderName;

      ld.title = (options.title || _vm.$t('') || '쪽지 내용').trim();
      ld.cancelButtonText = (options.cancelButtonText || _vm.$t('') || '확인').trim();
      if(MESSAGE_OWN_MODE.RECIVED == ld.mode){        
        ld.okButtonText = (options.okButtonText || _vm.$t('') || '쪽지 보내기').trim();
      }else{
        ld.okButtonText = (options.okButtonText || _vm.$t('') || '확인').trim();
      }
      
      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },

    async onClickReSendMessage(){
      const _vm = this;

      _vm.cancel();
      
      const ld = _vm.localData;
      EventBus.$emit('open-message-modal', {
        membSeq : ld.sender,
        membName : ld.senderName,
      }); 
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
      //console.debug(_vm.$refscoching_alert_modal);

      if(newVal == true){
        $(_vm.$refs["erns_message_read_modal"]).fadeIn(300);
        return;
      }

      $(_vm.$refs["erns_message_read_modal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },
  }  
}
</script>

<style lang="scss" scoped>
.read-only{
  background-color: #f5f5f5;
}
</style>

<style lang="scss">

</style>