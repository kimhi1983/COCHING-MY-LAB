<template>
  <!--대리점 신청 modal-->
  <div class="modal partner_recruit_modal" ref="partner_recruit_modal">
    <div class="layer">
      <div class="layer-content">
        <div class="modal-inner modal-sm">
          <div class="modal-content">
            <div class="modal-header">
              <div @click="cancel" class="modal-close"></div>
              <div class="title">{{ partnerData.ptnName }}</div></div>
            </div>
            <div class="modal-body">
              <div class="info-wrap">
                <div class="info-item">
                  <div class="info-item-label">담당자</div>
                  <div class="info-item-value">{{ partnerData.membName }}</div>
                </div>
                <div class="info-item">
                  <div class="info-item-label">메일주소</div>
                  <div v-if="partnerData.email" class="info-item-value clickable" @click="copyToClipboard(partnerData.email)">{{ partnerData.email }}</div>
                </div>
                <div class="info-item">
                  <div class="info-item-label">홈페이지</div>
                  <a v-if="partnerData.pageUrl" class="info-item-value clickable" :href="partnerData.pageUrl" target="_blank">{{ partnerData.pageUrl }}</a>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" @click="ok"
                class="btn btn-md btn-primary bottom-modal-close">확인</button>
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
  name: 'coching-partner-recruit-modal',
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
      partnerData: {
        ptnSeq: 0,
        ptnName: '',
        nationName: '',
        pageUrl: '',
        ytUrl: '',
        recruitYn: '',
        ptnIntro: '',
        rgtrSeq: 0,

        membSeq: null,
        membId: '',
        membType: '',
        membName: '',
        nickname: '',
        phone: '',
        email: '',
      },
      localData : {
        isShow : false,
        titleAlignCenter : false,
        alignCenter : true,

        result : undefined,
      }
    }
  },
  async mounted(){
    const _vm = this;
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {

    open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = true;
      
      // 파트너 데이터 설정
      if(options && options.partnerData) {
        _vm.partnerData = {
          ...options.partnerData
        };
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
      //console.debug(_vm.$refs.partner_recruit_modal);

      if(newVal == true){
        $(_vm.$refs["partner_recruit_modal"]).fadeIn(300);
        return;
      }

      $(_vm.$refs["partner_recruit_modal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },

    /* 클립보드에 복사 */
    async copyToClipboard(text) {
      const _vm = this;
      
      try {
        if (!text) {
          await _vm.alertError('복사할 정보가 없습니다.');
          return;
        }

        if (navigator.clipboard && window.isSecureContext) {
          await navigator.clipboard.writeText(text);
        }
        
        await _vm.alertSuccess('복사 완료');
      } catch (err) {
        await _vm.alertError('복사 실패');
      }
    },

  }  
}
</script>
<style lang="scss" scoped>
// modal-sm 크기를 450px로 오버라이드


.partner_recruit_modal {
  z-index: 1000 !important; //alert 모달(99999)보다 아래 위치

  .modal-inner.modal-sm {
    max-width: 450px !important;
    width: 450px !important;
  }

  .info-wrap {
    color: var(--color--gray-666) !important;

    .info-item {
      display: flex;
      gap: 1rem;
      line-height: 1.75rem;
    }
    .info-item-label {
      width: 80px;
      flex-shrink: 0;
      font-weight: 600;
      letter-spacing: 0.3em;
    }
     .info-item-value {
       flex: 1;
       word-break: break-all;
       color: var(--color--gray-666);
       
       &.clickable {
         cursor: pointer;
         transition: all 0.2s ease;
         
         &:hover {
           color: var(--color--primary);
         }
       }
     }
  }
}



</style>

<style lang="scss">
</style>
