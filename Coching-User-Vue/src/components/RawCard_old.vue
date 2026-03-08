<template>
  <div class="item">
    <div class="inner" @click="onClickCard()">
      <div class="thum">
        <img src="@/assets/images/img-product.png" alt="img" />
      </div>
      <div class="content-wrap">
        <div class="profile">
          <img src="@/assets/images/ic-profile.svg" alt="profile" />
        </div>
        <div class="title-wrap">
          <div class="title">{{rawInfo.rawName}}</div>
          <div class="info">
            <span>{{rawInfo.ptnName || '-'}}</span><!--원료사-->
            <span>{{rawInfo.prodCompany || '-'}}</span><!--제조사-->            
          </div>
          <div class="date">
            <span>조회수 {{(rawInfo.viewSumCnt || '0') | fmtViews }}</span>
          </div>
        </div>
      </div>
      <div class="hash-tag">
        <a v-for="(item, idx) of localData.hashTags" :key="idx"
          href="" @click="onClickHashTag(item)">#{{item}}</a>        
      </div>



    <!--header-->
    <div class="header">
      <div class="title">{{rawInfo.rawName}}</div>
      <!-- <div class="weight">{{rawInfo.weight || '-'}}</div> -->
    </div>
    <!--body-->
    <div class="body">
      <div class="card-table">
        <table>
          <colgroup>
            <col width="50"/>
          </colgroup>
          <tbody>
            <tr>
              <th>제조사</th>
              <td class="text-right">{{rawInfo.prodCompany || '-'}}</td>
            </tr>

            <tr>
              <th>원료사</th>
              <td class="text-right">{{rawInfo.ptnName || '-'}}</td>
            </tr>

            <tr v-if="localData.cate001.length == 0 && localData.cate002.length == 0">
              <th>효능</th>
              <td class="text-right">-</td>
            </tr>
            <tr v-else-if="localData.cate001.length > 0">
              <th>효능</th>
              <td class="text-right">
                {{displayCateNames(localData.cate001, 'nameKo')}}
              </td>
            </tr>
            <tr v-else-if="localData.cate002.length > 0">
              <th>기능</th>
              <td class="text-right">
                {{displayCateNames(localData.cate002, 'nameKo')}}
              </td>
            </tr>
            
            <tr>
              <th>패킹</th>
              <td class="text-right"
                v-if="rawInfo.weight"
              >{{rawInfo.weight}} Kg</td>
              <td class="text-right"
                v-else>-</td>
            </tr>

            <!-- <tr>
              <th>성분</th>
              <td class="text-right">
                <span v-for="(rawElem, idx) of rawInfo.rawElemInfo" :key="idx"
                  v-html="displayIngdNamesHtml(rawElem.repNameKo, idx, rawInfo.rawElemInfo)"/>
                <span v-if="!(rawInfo.rawElemInfo && rawInfo.rawElemInfo.length > 0)">
                  [No Data]
                </span>
              </td>
            </tr> -->

            <tr>
              <th>등록일</th>
              <td class="text-right">{{rawInfo.rgtDttm | eFmtDateFormat('YY.MM.DD')}}</td>
            </tr>

          </tbody>
        </table>
      </div>
    </div>

    <!-- {{ rawInfo }} -->
    <!-- {{ rawInfo.rawTypeInfo.filter(item=>'001' == item.grpCode) }} -->

    <!--footer-->
    <div class="footer">
      <!-- <div class="tag-badge blue">
        <div v-for="(tag, idx) of rawInfo.tags" :key="idx"
          class="item">{{tag.name}}</div>
      </div> -->
      <div class="tag-wrap">
        <span v-for="(item, idx) of localData.hashTags" :key="idx"
          class="hashtag"
          @click="onClickHashTag(item)"
        >#{{item}}</span>
        <!-- #신원료 #노화방지 #신원료 #노화방지 #신원료 #노화방지 #신원료 #노화방지 -->
      </div>
      <!--조회수 일정 숫자(1000단위) 넘어갈 경우 유튜브처럼 예:1.6천 단위 사용 희망-->
      <div class="inquiry">조회수 {{(rawInfo.viewSumCnt || '0') | fmtViews }}</div>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'coching-raw-card',
  mixins: [ernsUtils],
  components: {},
  props: {
    rawInfo : {
      type : Object,
      default: () => ({}),
      //require : true
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
    rawInfo(){
      const _vm = this;
      _vm.adjustHashTags();
    }
  },
  data() {
    return {
      localData:{
        hashTags:[],

        cate001:[],
        cate002:[],
        cate003:[],
        cate004:[],
        cate005:[],
      }
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
    //원료카드 클릭이벤트
    onClickCard(){
      const _vm = this;
      _vm.$emit("onClickCard", _vm.localData.rawInfo);
    },

    //hashtag 클릭
    onClickHashTag(hashtag){
      const _vm = this;
      _vm.$emit("onClickHashTag", `#${hashtag}`);
    },

    //hashtag 정리
    adjustHashTags(){
      const _vm = this;

      const rawHashTags = [];
      const detailList = _vm.rawInfo.rawDetailInfo || [];
      for(const dItem of detailList){
        const strHashTags = dItem.hashtag || '';
        rawHashTags.push(...strHashTags.split('#'));
      }

      //console.debug(rawHashTags);
      _vm.localData.hashTags = 
        _vm.cleanArrayWithReduce(
          rawHashTags.filter(tag=>{
            return !('#' == tag || '' == tag);
          })
        );
    },

    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);
      try{
        _vm.localData.cate001 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'001' == item.grpCode);
        _vm.localData.cate002 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'002' == item.grpCode);
        _vm.localData.cate003 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'003' == item.grpCode);
        _vm.localData.cate004 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'004' == item.grpCode);
        _vm.localData.cate005 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'005' == item.grpCode);
        _vm.adjustHashTags();
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
.tag-wrap{
  .hashtag + .hashtag {
    margin-left: 2px; 
  }
}

.footer {
  .inquiry {
    min-width: 5.2rem;
  }
}
  


</style>

<style lang="scss">

</style>