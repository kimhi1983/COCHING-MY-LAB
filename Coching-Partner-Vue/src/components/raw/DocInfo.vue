<template>
  <!--구비서류-->
  <div class="section">
    <!--title-->
    <div class="title-wrap">
      <div class="title">구비서류</div>
    </div>
    <!--section-content-->
    <div class="section-content">
      <!--document-check-->
      <div class="document-check">
        <div class="check_all">
          <div class="checkbox">
            <input
              id="checkbox-all"
              type="checkbox"
              value="all"
              @change="onCheckChange($event, 'all')"
              :checked="isChecked('all')"
              :disabled="!isAdmin || (isAdmin && !isManager)"
            />
            <label for="checkbox-all" class="checkbox-label all_font">{{ '전체선택' }}</label>
          </div>
        </div>
        <div v-for="(item, index) of localData.rawDocCodeData.list">
          <div class="checkbox">
            <input
              :id="'checkbox-' + index"
              type="checkbox"
              :value="item.etc1"
              @change="onCheckChange($event, item.etc1)"
              :checked="isChecked(item.etc1)"
              :disabled="!isAdmin || (isAdmin && !isManager)"
            />
            <label :for="'checkbox-'+index" class="checkbox-label">{{ item.codeName }}</label>
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
import { getCodes } from '@/api/coching/comm/code';
import { getDocList } from '@/api/coching/comm/raw';

const DEF_CHECOED_DOC_CODE = [
  '001',
  '002',
  '003',
  '004',
  '005',
  '007',
];

export default {
    name: 'coching-rawInfo',
    mixins: [ernsUtils],
    components: {
    },
    props: {
      rawInfo: {
          type : Object,
          defalt: {}
        },
        isAdmin: {
          type : Boolean,
          default: false
        },
        isManager: {
          type : Boolean,
          default: false
        },
    },
    computed: {
        
    },
    watch: {
      '$i18n.locale' : function(){
        const _vm = this;
      },
      rawInfo: function() {
        const _vm = this;
        
        _vm.init();
      },
      'localData.rawDocData.selected': function(){
        const _vm = this;

        _vm.onChangeValue();
      },
    },
    data() {
        return {
          allChecked: false, //"전체 선택" 체크박스 상태
          localData:{
            rawDocData: {
              docList: [],
            },
            rawDocCodeData: {
              list: [],
            },
          },
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
    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);

      try{
        DEF_CHECOED_DOC_CODE.forEach((docCode) => {
          _vm.localData.rawDocData.docList.push({ docCode: docCode });
        });
        _vm.onChangeValue();
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },  

    async init(){
      const _vm = this;

      //수정일때 정보 들고오기
      if(_vm.rawInfo.rawSeq){
        await _vm.loadRawDoc();
        _vm.onChangeValue();
      }
    },
    
    async loadCodes(){
      const _vm = this;

      const codeRes = await getCodes({grpCode:"CH003", etc5:_vm.$i18n.locale, useYn: 'Y'});
      const { resultCode, resultFailMessage, resultData } = codeRes;

      _vm.localData.rawDocCodeData.list = [...resultData.list];
    },

    async loadRawDoc(){
      const _vm = this;

      const res = await getDocList({rawSeq: _vm.rawInfo.rawSeq});
      const { resultCode, resultFailMessage, resultData } = res;

      const list = resultData.list;
      // 서버에서 가져온 list를 docList 저장
      _vm.localData.rawDocData.docList = list.map((item) => ({
        docCode: item.docCode,
      }));

      _vm.syncAllCheckbox();
    },

    // 체크박스 값 변경 처리
    onCheckChange(event, value) {
      const _vm = this;

      if (value === 'all') {
        //"전체 선택" 체크박스가 변경될 때, 모든 개별 체크박스를 변경
        const isChecked = event.target.checked;
        _vm.localData.rawDocData.docList = isChecked
          ? _vm.localData.rawDocCodeData.list.map((item) => ({ docCode: item.etc1 })) // 모든 항목 추가
          : []; // 모든 항목 제거
      } else {
        //개별 체크박스의 변경 처리
        if (event.target.checked) {
          _vm.localData.rawDocData.docList.push({ docCode: value });
        } else {
          _vm.localData.rawDocData.docList = _vm.localData.rawDocData.docList.filter(
            (item) => item.docCode !== value
          );
        }
      }

      //"전체 선택" 체크박스 상태를 다시 업데이트
      _vm.syncAllCheckbox();

      _vm.onChangeValue();
    },

    //"전체 선택" 체크박스 상태 동기화
    syncAllCheckbox() {
      const _vm = this;
      const totalCount = _vm.localData.rawDocCodeData.list.length;
      const checkedCount = _vm.localData.rawDocData.docList.length;

      _vm.allChecked = totalCount > 0 && totalCount === checkedCount; //전체 선택 여부 체크
    },

    // 체크박스가 체크되었는지 확인
    isChecked(value) {
      if (value === 'all') {
        return this.allChecked; //"전체 선택" 체크 여부 반환
      }
      return this.localData.rawDocData.docList.some(
        (item) => item.docCode === value
      );
    },

    async onChangeValue(){
      const _vm = this;

      _vm.$emit("update:data", _vm.localData.rawDocData);

      const preData = _vm.localData.rawDocCodeData.list.map((item) => {
        return {
          rawSeq: _vm.rawInfo.rawSeq,
          docCode: item.etc1,
          yn: _vm.localData.rawDocData.docList.some(doc => doc.docCode === item.etc1) ? "Y" : "N",
          nameKo: item.codeName || "",
          nameEn: item.codeName || "",
        };
      });
      _vm.$emit("update:preData", preData);
    },

    docReady(){
      const _vm = this;
    },

  }  
}
</script>

<style lang="scss" scoped>
.check_all{
  width: calc(100% - 0.625rem);
  border-bottom: 2px solid var(--color--border-ddd);
}
.all_font{
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 20px;
}
</style>

<style lang="scss">

</style>