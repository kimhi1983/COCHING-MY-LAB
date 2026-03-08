<template>
  <!--성분구분-->
 <div class="section">
    <!--title-->
    <div class="title-wrap">
      <div class="title">성분구분</div>
    </div>
    <!--section-content-->
    <validation-observer tag="div" ref="typeInfoForm" class="section-content" #default="{ invalid }">
      <div v-if="updateInvalidState(invalid)"></div>
      <!--base-type-->
      <div class="base-type">
        <div class="item">
          <label>효능</label>
          <validation-provider #default="{ errors }" tag="div"
            class="input-set" :name="$t('') || '효능 선택'" rules="">
            <multiselect 
              v-model="selectedType001"
              :options="localData.cmTypeData.TYPE001"
              :label="localeLabel"
              :trackBy="'code'"
              :multiple="true"
              :disabled="!isAdmin || (isAdmin && !isManager)"
              :close-on-select="false"
              :placeholder="$t('') || '효능 선택'"
              :select-label="'추가하려면 클릭해 주세요'"
              :selected-label="'선택됨'"
              :deselect-label="'삭제하려면 클릭해 주세요'"
              @input="(val)=>{onSelectType(val, '001')}"
            >
            <template #noResult>
                <span class="custom-no-result">{{$t('') || '일치하는 효능이 없습니다.'}}</span>
              </template>
            </multiselect>
          </validation-provider>
        </div>
        <div class="item">
          <label>기능</label>
          <validation-provider #default="{ errors }" tag="div"
          class="input-set" :name="$t('') || '기능 선택'" rules="">
            <multiselect 
              v-model="selectedType002"
              :options="localData.cmTypeData.TYPE002"
              :label="localeLabel"
              :trackBy="'code'"
              :multiple="true"
              :disabled="!isAdmin || (isAdmin && !isManager)"
              :close-on-select="false"
              :placeholder="$t('') || '기능 선택'"
              :select-label="'추가하려면 클릭해 주세요'"
              :selected-label="'선택됨'"
              :deselect-label="'삭제하려면 클릭해 주세요'"
              @input="(val)=>{onSelectType(val, '002')}"
              >
            <template #noResult>
                <span class="custom-no-result">{{$t('') || '일치하는 기능이 없습니다.'}}</span>
              </template>
            </multiselect>
            </validation-provider>
        </div>
        <div class="item">
          <label>제품</label>
          <validation-provider #default="{ errors }" tag="div"
            class="input-set" :name="$t('') || '제품 선택'" rules="">
            <multiselect 
              v-model="selectedType003"
              :options="localData.cmTypeData.TYPE003"
              :label="localeLabel"
              :trackBy="'code'"
              :multiple="true"
              :disabled="!isAdmin || (isAdmin && !isManager)"
              :close-on-select="false"
              :placeholder="$t('') || '제품 선택'"
              :select-label="'추가하려면 클릭해 주세요'"
              :selected-label="'선택됨'"
              :deselect-label="'삭제하려면 클릭해 주세요'"
              @input="(val)=>{onSelectType(val, '003')}"
              >
            <template #noResult>
                <span class="custom-no-result">{{$t('') || '일치하는 제품이 없습니다.'}}</span>
              </template>
            </multiselect>
          </validation-provider>
        </div>
        <div class="item">
          <label>성상</label>
          <validation-provider #default="{ errors }" tag="div"
            class="input-set" :name="$t('') || '성상 선택'" rules="">
            <multiselect 
              v-model="selectedType004"
              :options="localData.cmTypeData.TYPE004"
              :label="localeLabel"
              :trackBy="'code'"
              :multiple="true"
              :disabled="!isAdmin || (isAdmin && !isManager)"
              :close-on-select="false"
              :placeholder="$t('') || '성상 선택'"
              :select-label="'추가하려면 클릭해 주세요'"
              :selected-label="'선택됨'"
              :deselect-label="'삭제하려면 클릭해 주세요'"
              @input="(val)=>{onSelectType(val, '004')}"
              >
            <template #noResult>
                <span class="custom-no-result">{{$t('') || '일치하는 성상이 없습니다.'}}</span>
              </template>
            </multiselect>
          </validation-provider>
        </div>
        <div class="item">
          <label>원물</label>
          <validation-provider #default="{ errors }" tag="div"
            class="input-set" :name="$t('') || '원물 선택'" rules="">
            <multiselect 
              v-model="selectedType005"
              :options="localData.cmTypeData.TYPE005"
              :label="localeLabel"
              :trackBy="'code'"
              :multiple="true"
              :disabled="!isAdmin || (isAdmin && !isManager)"
              :close-on-select="false"
              :placeholder="$t('') || '원물 선택'"
              :select-label="'추가하려면 클릭해 주세요'"
              :selected-label="'선택됨'"
              :deselect-label="'삭제하려면 클릭해 주세요'"
              @input="(val)=>{onSelectType(val, '005')}"
              >
            <template #noResult>
                <span class="custom-no-result">{{$t('') || '일치하는 원물이 없습니다.'}}</span>
              </template>
            </multiselect>
          </validation-provider>
        </div>
      </div>
    </validation-observer>


    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

import Multiselect from 'vue-multiselect'
import '/node_modules/vue-multiselect/dist/vue-multiselect.min.css';

import { getCmTypeList } from '@/api/coching/comm/raw';
import { getCodes } from '@/api/coching/comm/code';
import { getTypeList } from '@/api/coching/comm/raw';

const DEF_TYPE_INF={
  grpCode: '',
  code: '',
}

export default {
    name: 'coching-rawInfo',
    mixins: [ernsUtils],
    components: {
      Multiselect,
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
      selectedType001: {
        get() {
          return this.getSelectedItems('001');
        },
        set(val) {
          this.onSelectType(val, '001');
        },
      },
      selectedType002: {
        get() {
          return this.getSelectedItems('002');
        },
        set(val) {
          this.onSelectType(val, '002');
        },
      },
      selectedType003: {
        get() {
          return this.getSelectedItems('003');
        },
        set(val) {
          this.onSelectType(val, '003');
        },
      },
      selectedType004: {
        get() {
          return this.getSelectedItems('004');
        },
        set(val) {
          this.onSelectType(val, '004');
        },
      },
      selectedType005: {
        get() {
          return this.getSelectedItems('005');
        },
        set(val) {
          this.onSelectType(val, '005');
        },
      },
    },
    watch: {
      '$i18n.locale' : function(){
        const _vm = this;
        if(_vm.$i18n.locale === 'ko') {
          _vm.localeLabel = 'codeNmKo'
        } else if(_vm.$i18n.locale === 'en') {
          _vm.localeLabel = 'codeNmEn'
        }
      },
      rawInfo: {
        handler(newVal) {
          const _vm = this;
          if (newVal && newVal.rawSeq) {
            _vm.init(); // rawSeq가 있는 경우 초기화
          } else {
            _vm.localData.rawTypeData.typeList = []; // 초기화
          }
        },
        immediate: true, // 컴포넌트 마운트 시에도 실행
      },
    },
    data() {
        return {
          localData:{
            rawTypeData:{
              typeList: [],
            },
            cmTypeData:{
              grpList: [],
              list: [],
              TYPE001: [],
              TYPE002: [],
              TYPE003: [],
              TYPE004: [],
              TYPE005: [],
            }
          },
          localeLabel: 'codeNmKo',
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
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    async init(){
      const _vm = this;

      //수정일때 정보 들고오기
      await _vm.loadRawType();
    },

    async loadCodes(){
      const _vm = this;

      const codeRes = await getCodes({grpCode:"CH001", etc5:_vm.$i18n.locale, useYn: 'Y'});
      const { resultCode, resultFailMessage, resultData } = codeRes;

      _vm.localData.cmTypeData.grpList = [...resultData.list];

      await _vm.loadCmType();
    },

    async loadCmType(){
      const _vm = this;

      const res = await getCmTypeList({});
      const { resultCode, resultFailMessage, resultData } = res;

      const list = resultData.list;
      _vm.localData.cmTypeData.list = [...list];

      const grpCodes = _vm.localData.cmTypeData.grpList;

      grpCodes.forEach(code => {
        _vm.localData.cmTypeData[`TYPE${code.etc1}`] = list.filter(item => item.grpCode === code.etc1);
      });
    },

    async loadRawType(){
      const _vm =  this;

      const res = await getTypeList({rawSeq: _vm.rawInfo.rawSeq});
      const { resultCode, resultFailMessage, resultData } = res;

      _vm.localData.rawTypeData.typeList = [...resultData.list];
      _vm.onChangeValue();
    },

    // 선택된 옵션을 반환하는 함수
    getSelectedItems(grpCode) {
      return this.localData.rawTypeData.typeList
        .filter((item) => item.grpCode === grpCode)
        .map((item) => {
            return (
                this.localData.cmTypeData[`TYPE${grpCode}`].find(
                    (opt) => opt.code === item.code
                ) || item
            );
        });
    },

    onSelectType(val, code){
      const _vm = this;

      // 기존 리스트에서 해당 code 값이 아닌 항목만 유지 (즉, code 값에 해당하는 항목을 제거)
      _vm.localData.rawTypeData.typeList = _vm.localData.rawTypeData.typeList.filter(
          (item) => item.grpCode !== code
      );

      // val 값에 있는 데이터를 추가 (덮어쓰기)
      const newItems = val.map(item => ({
          grpCode: code,
          code: item.code
      }));

      _vm.localData.rawTypeData.typeList.push(...newItems);

      _vm.onChangeValue();
    },

    async onChangeValue(){
      const _vm = this;

      _vm.$emit("update:data", _vm.localData.rawTypeData);

      const preData = _vm.localData.rawTypeData.typeList.map((item) => {
          return {
              rawSeq: _vm.rawInfo.rawSeq,
              codes: item.grpCode + ':' + item.code,
              grpCode: item.grpCode,
              code: item.code,
              nameKo:
                  _vm.localData.cmTypeData.list.find(
                      (type) => type.grpCode === item.grpCode && type.code === item.code
                  )?.codeNmKo || '',
              nameEn:
                  _vm.localData.cmTypeData.list.find(
                      (type) => type.grpCode === item.grpCode && type.code === item.code
                  )?.codeNmEn || '',
          };
      });

      _vm.$emit("update:preData", preData);
    },

    //양식의 상태값을 확인
    updateInvalidState(invalid) {
      const _vm = this;
      const isChanged = _vm.localInvalidState != invalid;
      _vm.localInvalidState = invalid;
      if(isChanged){
        //상태값이 변경되면 부모에게 알림
        _vm.$emit("update:valid", !_vm.localInvalidState);
      }
      return false; // v-if에서 항상 false를 반환하여 실제 DOM에 렌더링되지 않게 함
    },

    docReady(){
      const _vm = this;
    },

  }  
}
</script>

<style lang="scss" scoped>
/* 기본 스타일 변경 */
</style>

<style lang="scss">
</style>