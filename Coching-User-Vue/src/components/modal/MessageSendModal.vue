<template>
  <div>
    <!--쪽지보내기 modal-->
    <div class="modal" ref="erns_message_send_modal">
      <div class="layer">
        <div class="layer-content">
          <div class="modal-inner modal-lg">
            <div class="modal-content">
              <div class="modal-header">
                <div @click="cancel" class="modal-close"></div>
                <div class="title">쪽지 보내기
                  <span> {{ localData.membName }}</span></div>
              </div>
              <validation-observer ref="sendMessageForm" #default="{invalid}">
                <div class="modal-body">
                  <validation-provider #default="{ errors }" tag="div"
                    class="input-wrap" :name="$t('') || '쪽지 내용'" rules="required">
                    <textarea 
                      v-model="localData.messageContent"
                      rows="10" placeholder="내용을 입력해 주세요"></textarea>
                    <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] }}</div>
                  </validation-provider>
                </div>
                <div class="modal-footer">
                  <button type="button" 
                    @click="cancel"
                    class="btn btn-md btn-gray bottom-modal-close w-100">{{localData.cancelButtonText}}</button>
                  <button type="button" 
                    @click="onClickSendMessage"
                    :disabled="invalid"
                    :class="{
                      'btn-disabled': invalid,
                      'btn-primary': !invalid
                    }"
                    class="btn btn-md bottom-modal-close w-100">{{localData.okButtonText}}</button>
                </div>
              </validation-observer>            
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
import { sendUserMessage, } from '@/api/coching/member/message';
import { required } from '@validations';

export default {
  name: 'coching-message-send-modal',
  mixins: [ernsUtils],
  components: {
    required
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
    EventBus.$on('open-message-modal', async(data)=>{
      if(!isUserLoggedIn()){
        await _vm.onClickConfirmLogin();
        return;
      }

      _vm.open({
        membSeq : data.membSeq,
        membName : data.membName,
      });
    });
  },
  data() {
    return {
      isLoggedIn: false,

      localData : {
        isShow : false,
        titleAlignCenter : false,
        alignCenter : true,

        membName: "수신자",
        membSeq: 0,
        messageContent : "",

        title : this.$t('') || '쪽지 보내기',        
        okButtonText : this.$t('') || '보내기',
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
      ld.membName = options.membName;
      ld.membSeq = options.membSeq;
      ld.messageContent = options.messageContent || '';

      ld.title = (options.title || _vm.$t('') || '쪽지 보내기').trim();
      ld.okButtonText = (options.okButtonText || _vm.$t('') || '보내기').trim();
      ld.cancelButtonText = (options.cancelButtonText || _vm.$t('') || '취소').trim();
      
      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },

    async onClickSendMessage(){
      const _vm = this;

      const isValid = await _vm.$refs.sendMessageForm.validate();
      if(!isValid){
        return;
      }

      _vm.loading(true);
      try{
        const ld = _vm.localData;
        const params = {
          sender : _vm.eumLoginUser.userSeq,
          receiver : ld.membSeq,
          content : ld.messageContent
        };
        
        const res = await sendUserMessage(params);
        _vm.alertSuccess('쪽지를 전송 했습니다.');
        EventBus.$emit('sent-message', {});      

        _vm.ok();

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
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
      ld.result(false);
    },

    onChangeShow(){
      const _vm = this;

      const newVal =_vm.localData.isShow;
      //console.debug(newVal);
      //console.debug(_vm.$refscoching_alert_modal);

      if(newVal == true){
        $(_vm.$refs["erns_message_send_modal"]).fadeIn(300);
        return;
      }

      $(_vm.$refs["erns_message_send_modal"]).fadeOut(300);
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

</style>