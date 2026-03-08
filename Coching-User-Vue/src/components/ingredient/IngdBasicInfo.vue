<template>
  <div>
    <div class="h1">{{ingdInfo.korean}}</div>
    <div class="top-content">
      <!--left-->
      <div class="left">
        <!--ingre-table-->
        <div class="ingre-table">
          <table>
            <colgroup>
              <col width="80" />
            </colgroup>
            <tbody>
              <!-- <tr>
                <th>INCI</th>
                <td>{{coIngdInfo.inci[0] | eufmtValueDash}}</td>                
              </tr> -->
              <tr>
                <th>한글명</th>
                <td>{{displayArrayVal([coIngdInfo.repName]) | eufmtValueDash}}</td>
              </tr>
              <tr>
                <th>INCI</th>
                <td>{{displayArrayVal([coIngdInfo.repNameEn]) | eufmtValueDash}}</td>
              </tr>
              <tr>
                <th>구조식</th>
                <td>{{coIngdInfo.formula | eufmtValueDash}}</td>
              </tr>
              <tr>
                <th>기능(식)</th>
                <td>{{displayArrayVal(coIngdInfo.purposes) | eufmtValueDash}}</td>
              </tr>
              <tr>
                <th>CAS No</th>
                <td>{{displayArrayVal(coIngdInfo.casNo) | eufmtValueDash}}</td>
              </tr>
              <tr>
                <th>EC No</th>
                <td>{{displayArrayVal(coIngdInfo.ecNo) | eufmtValueDash}}</td>
              </tr>
              <tr>
                <th>기타명칭</th>
                <td>{{displayArrayVal(coIngdInfo.nickName) | eufmtValueDash}}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <!-- coIngdInfo:{{ coIngdInfo }} -->

        <!--button-->
        <div v-if="enabledRawButton || enabledProductButton" class="btn-area">
          <button type="button" class="btn btn-sm btn-primary"
            v-show="enabledRawButton"
            @click="onClickGotoRaw"
            >사용원료 바로가기</button>
          <button type="button" class="btn btn-sm btn-primary"
            v-show="enabledProductButton"
            @click="onClickGotoProduct"
            >사용제품 바로가기</button>
          <button type="button" 
            class="btn btn-sm"
            :class="{
              'btn-disabled' : !coIngdInfo.kcaiId
              , 'btn-primary-outline' : coIngdInfo.kcaiId
            }"
            :disabled="!coIngdInfo.kcaiId"
            @click="onClickGotoKcia"
            >화장품 성분협회 바로가기</button>
        </div>
        
      </div>
      <!--element-box-->
      <!--이미지 없을 경우 영역 삭제-->      

      <div v-if="coIngdInfo.expUrl1" class="element-box aspect-ratio-container urlImg1">
        <!-- <img src="@/assets/images/img-element.png" alt="" /> -->
        <img :src="getProxyFile(coIngdInfo.expUrl1)"
            :alt="coIngdInfo.formula"
            :style="imageStyle"
            @load="onImageLoad"/>        
      </div>
      <div v-else-if="coIngdInfo.expUrl2" class="element-box aspect-ratio-container">
        <!-- <img src="@/assets/images/img-element.png" alt="" /> -->
        <img :src="getProxyFile(coIngdInfo.expUrl2)"
            :alt="coIngdInfo.formula"
            :style="imageStyle"
            @load="onImageLoad"/>
      </div>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { DEF_INGD_DETAIL } from '@/constants/data';

export default {
  name: 'coching-ingredient-basic-info',
  mixins: [ernsUtils],
  components: {
  },
  props: {
    ingdInfo : {
      type : Object,
      default: () => ({}),
      require : true
    },
    coIngdInfo:{
      type : Object,
      default: () => ({
        ...DEF_INGD_DETAIL
      }),
      require : true
    },
    enabledRawButton : {
      type : Boolean,
      default : false
    },
    enabledProductButton : {
      type : Boolean,
      default : false
    }
  },
  computed: {
  },
  watch: {
    '$i18n.locale' : async function(){
      const _vm = this;
      await _vm.loadCodes();
      _vm.$nextTick(() => {});
    },
  },
  data() {
    return {
      imageStyle : {} // 동적으로 설정할 스타일
    }
  },
  async mounted(){
    const _vm = this;

    _vm.docReady();
    _vm.fetchData();    

  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    //사용원료 클릭이벤트
    onClickGotoRaw(){
      const _vm = this;
      _vm.$emit("onClickGotoRaw", _vm.ingdInfo);
    },

    //사용제품 바로가기 클릭이벤트
    onClickGotoProduct(){
      const _vm = this;
      _vm.$emit("onClickGotoProduct", _vm.ingdInfo);
    },

    //대한 화장품 성분협회 바로가기 클릭이벤트
    onClickGotoKcia(){
      const _vm = this;

      const url = `https://kcia.or.kr/cid/search/ingd_view.php?no=${_vm.coIngdInfo.kcaiId}`;
      _vm.eumLocationUrl(url);      
    },

    //이미지 로드
    onImageLoad(event) {
      const img = event.target;      
      const aspectRatio = img.naturalWidth / img.naturalHeight; // 가로세로 비율 계산
      const containerWidth = img.parentElement.clientWidth; // 컨테이너의 너비
      const containerHeight = img.parentElement.clientHeight; // 컨테이너의 높이

      if (aspectRatio > 1) {
        // 랜드스케이프
        this.imageStyle = {
          width: "100%",
          height: "auto",
        };
      } else {
        // 포트레이트
        this.imageStyle = {
          width: "auto",
          height: "100%",
        };
      }
    },

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

    async loadCodes(){
      const _vm = this;
    },

    docReady(){
      const _vm = this;   

    },
  }  
}
</script>

<style lang="scss" scoped>
.aspect-ratio-container {
  overflow: hidden;
  position: relative;

  &.urlImg1{
    background-color: #F5F5F5;
    padding: 1rem;
  }
}

.aspect-ratio-container img {
  display: block;
  object-fit: cover; /* 비율 유지 및 초과 부분 잘림 */
}

.h2 {
  font-weight: 600;
}
</style>

<style lang="scss">

</style>