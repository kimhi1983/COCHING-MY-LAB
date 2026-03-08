<template>
  <div @click="onClickItem" class="item">
    <div class="title-wrap">
      <div class="num">{{rowNum | eFmtPadStart(2)}}</div>
      <div class="title-zon">
        <div class="title">{{cntrcItem.cntrcName}}</div>
        <div class="badge-wrap badge-flex">
          <div class="badge gray">{{getCompanyTypeCode().label}}</div>
          <div class="badge gray">{{getCurrencyTypeCode().label}}</div>
        </div>
      </div>
    </div>
    <div class="info-wrap">
      <div class="inner">
        <ul>
          <li>{{ $t('menu03.label.label011') || '계약 상대기업' }} : </li>
          <li v-if="cntrcItem.companyType == '001'">{{cntrcItem.salesCompNm}}</li>
          <li v-if="cntrcItem.companyType == '002'">{{cntrcItem.purchaseCompNm}}</li>
        </ul>
        <ul>
          <li>{{ $t('menu03.label.label012') || '판매대금 송금액' }} : </li>
          <li>
            {{ cntrcItem.totPrice | eFmtCurrency('', cntrcItem.currencyType == '001' ? '원' : '$')}} 
            {{ cntrcItem.currencyType == '001' ? '(KRW)' : ''}}</li>
        </ul>
      </div>

      <div class="inner">
        <ul>
          <li>{{ $t('menu03.label.label013') || '구매카드' }} : </li>
          <li>{{cntrcItem.cardName}}</li> <!-- 삼성카드(1234) -->
        </ul>
        <ul>
          <li>{{ $t('menu03.label.label014') || '카드결제액' }} : </li>
          <li>
            {{ cntrcItem.cardPrice | eFmtCurrency('', cntrcItem.currencyType == '001' ? '원' : '$')}} 
            {{ cntrcItem.currencyType == '001' ? '(KRW)' : ''}}
          </li>
        </ul>
        <ul>
          <li>{{ $t('menu03.label.label015') || '수수료' }} : </li>
          <li>
            {{ cntrcItem.vatFee | eFmtCurrency('', '(KRW)')}} 
          </li>
        </ul>
      </div>

      <div class="inner">
        <ul>
          <li>{{ $t('menu03.label.label016') || '계약등록일' }} : </li>
          <li>{{cntrcItem.rgtDttm | eFmtDateFormat('YY-MM-DD')}}</li>
        </ul>
        <ul>
          <li>{{ $t('menu03.label.label017') || '카드결제일' }} : </li>
          <li>{{cntrcItem.authDate | eFmtStrAuthDateToDate | eFmtDateFormat('YY-MM-DD')}}</li>
        </ul>
        <ul>
          <li>{{ $t('menu03.label.label018') || '대금이체일' }} : </li>
          <li>{{cntrcItem.settleDate | eFmtDateFormat('YY-MM-DD')}}</li>
        </ul>
      </div>
    </div>
    <div class="circle-arrow">
      <svg xmlns="http://www.w3.org/2000/svg" width="26" height="13" viewBox="0 0 26 13" fill="none">
        <path fill-rule="evenodd" clip-rule="evenodd"
          d="M20.7071 0.792893C20.3166 0.402369 19.6834 0.402369 19.2929 0.792893C18.9024 1.18342 18.9024 1.81658 19.2929 2.20711L22.5858 5.5H1C0.447715 5.5 0 5.94772 0 6.5C0 7.05228 0.447715 7.5 1 7.5H22.5858L19.2929 10.7929C18.9024 11.1834 18.9024 11.8166 19.2929 12.2071C19.6834 12.5976 20.3166 12.5976 20.7071 12.2071L25.7071 7.20711C26.0976 6.81658 26.0976 6.18342 25.7071 5.79289L20.7071 0.792893Z"
          fill="#222222" />
      </svg>
    </div>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'coching-complete-contract-list-card',
  mixins: [ernsUtils],
  components: {
  },
  filters:{
    
  },
  props: {
    QP006 : { //상태코드
      type : Array,
      defalt: []
    },
    companyTypeCode: { //구매/판매 계약코드
      type : Array,
      defalt: []
    },
    currencyTypeCode: { //원화/외화 코드
      type : Array,
      defalt: []
    },
    rowNum : {
      type : [Number, String],
      default : 0
    },
    cntrcItem : {
      type : Object,
      defalt: {}
    },
  },
  computed: {
      
  },
  watch: {
  },
  data() {
    return {
        
    }
  },
  async mounted(){
    const _vm = this;

    await _vm.loadCodes();
    _vm.docReady();
    _vm.fetchData();    
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    //아이템 선택
    onClickItem() {
			const _vm = this;
			
			_vm.$emit('click', _vm.cntrcItem);
		},

    async loadCodes(){
      const _vm = this;
    },

    //구매/판매 계약코드 리턴
    getCompanyTypeCode(){
      const _vm = this;

      const retCode = _vm.companyTypeCode.find(item=>{
        return item.value == _vm.cntrcItem.companyType;
      });
      if(retCode){
        return retCode;
      }
      
      return {value:'', label:''};
    },

    //상태코드 리턴
    getCurrencyTypeCode(){
      const _vm = this;

      const retCode = _vm.currencyTypeCode.find(item=>{
        return item.value == _vm.cntrcItem.currencyType;
      });
      if(retCode){
        return retCode;
      }
      
      return {value:'', label:''};
    },

    //fetchData
    async fetchData(){
      //Do nothing
    },

    docReady(){
      const _vm = this;      
    },    
  }  
}
</script>

<style lang="scss" scoped>
.finish-result .badge.gray{
  margin-right: 0.25rem;
}
</style>

<style lang="scss">

</style>