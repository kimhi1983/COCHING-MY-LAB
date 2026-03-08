<template>
  <ModalFull 
    :isActive="localData.isActive" 
    :classOption="localData.classOption"
    @onClickDim="onClickClose()"
    @close="onClickClose()">

    <!-- Header Content -->
    <template v-slot:header>
      <div class="title">{{localData.rawInfo.rawName}}</div>
    </template>

    <!-- Body Content -->
    <template v-slot:body>
      <!--card simple-->
      <div class="card-simple">
        <!--register-wrap-->

        <div class="register-wrap">
          
          <!--item-->
          <div v-for="(dItem, idx) of localData.rawInfo.rawDetailInfo"
            :key="dItem.rawDetailSeq"
            class="item">
            <div class="content">
              <div class="title">{{dItem.title || '-'}}</div>
              <div class="info">
                <div class="button-wrap">
                  <button @click="onClickSendMessage(dItem)"
                    type="button" class="ic-md ic-folded-note"></button>
                  <button @click="onClickRequest(dItem)"
                    type="button" class="ic-md ic-message modal-open send-modal"></button>
                  <div 
                    @click="onClickFavorite(dItem)"
                    class="heart-wrap"
                    :class="{'active' : dItem.isFavorite}">
                    <button type="button" class="ic-md ic-heart"></button>
                  </div>
                </div>
                <div class="date-wrap">
                  <div>{{dItem.membName || '-'}}</div>
                  <div>{{dItem.chngDttm | eFmtDateFormat('YY.MM.DD')}}</div>
                </div>
              </div>
            </div>

            <button @click="onClickRawDetail(dItem)"
              type="button" class="btn btn-xsm btn-gray-outline">상세보기</button>
          </div>

        </div>

        <!--ewg table-->
        <div class="ewg-table">
          <table>
            <colgroup>
              <col width="80" />
            </colgroup>
            <thead>
              <tr>
                <th>EWG</th>
                <th>성분명</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(ingd, idx) of localData.rawInfo.rawElemInfo" 
                :key="idx" class="link"
                @click="onClickIngdList(ingd)">
                <td>
                  <div class="ewg" 
                  :class="getEwgClass(getEwgInfo(`${ingd.ewgScoreMin}-${ingd.ewgScore}`).max)">
                  {{getEwgInfo(`${ingd.ewgScoreMin}-${ingd.ewgScore}`).scoreText}}</div>
                </td>
                <td>{{ingd.repNameKo}}
                  <div v-if="ingd.repNameEn"
                    class="eng">{{ingd.repNameEn}}</div>
                </td>                
              </tr>  
            </tbody>
          </table>
        </div>
        <!--info box-->
        <div class="info-box">
          <div class="info-table">
            <table>
              <colgroup>
                <col width="60" />
              </colgroup>
              <tbody>
                
                <tr>
                  <th>효능</th>
                  <td>{{displayCateNames((localData.rawInfo.rawTypeInfo || []).filter(item=>'001' == item.grpCode), 'nameKo')}}</td>
                </tr>
                <tr>
                  <th>기능</th>
                  <td>{{displayCateNames((localData.rawInfo.rawTypeInfo || []).filter(item=>'002' == item.grpCode), 'nameKo')}}</td>
                </tr>
                <tr>
                  <th>제품</th>
                  <td>{{displayCateNames((localData.rawInfo.rawTypeInfo || []).filter(item=>'003' == item.grpCode), 'nameKo')}}</td>
                </tr>
                <tr>
                  <th>성상</th>
                  <td>{{displayCateNames((localData.rawInfo.rawTypeInfo || []).filter(item=>'004' == item.grpCode), 'nameKo')}}</td>
                </tr>
                <tr>
                  <th>원물</th>
                  <td>{{displayCateNames((localData.rawInfo.rawTypeInfo || []).filter(item=>'005' == item.grpCode), 'nameKo')}}</td>
                </tr>
                <tr>
                  <th>원료사</th>
                  <td v-if="localData.rawInfo.ptnName">
                    <span class="search-text default-color addIcon"
                      @click="goSearchKeywordV2(`${localData.rawInfo.ptnName}`, {hintField : '1014', emo : true}, getRawSearchRouterName())"
                    >{{localData.rawInfo.ptnName}}</span></td>
                  <td v-else>{{localData.rawInfo.ptnName || '-'}}</td>
                </tr>
                <tr v-if="localData.rawInfo.supplier && localData.rawInfo.ptnName != localData.rawInfo.supplier">
                  <th>공급사</th>
                  <td v-if="localData.rawInfo.supplier">
                    <span class="search-text default-color addIcon"
                      @click="goSearchKeywordV2(`${localData.rawInfo.supplier}`, {hintField : '1011', emo : true}, getRawSearchRouterName())"
                    >{{localData.rawInfo.supplier}}</span></td>
                  <td v-else>{{localData.rawInfo.supplier || '-'}}</td>
                </tr> 
                <tr>
                  <th>제조사</th>
                  <td v-if="localData.rawInfo.prodCompany">
                    <span class="search-text default-color addIcon"
                      @click="goSearchKeywordV2(`${localData.rawInfo.prodCompany}`, {hintField : '1012', emo : true}, getRawSearchRouterName())"
                    >{{localData.rawInfo.prodCompany}}</span></td>
                  <td v-else>{{localData.rawInfo.prodCompany || '-'}}</td>
                </tr>
                <tr>
                  <th>제조국</th>
                  <td v-if="localData.rawInfo.prodCountryName">
                    <span class="search-text default-color addIcon"
                      @click="goSearchKeywordV2(`${localData.rawInfo.prodCountryName}`, {hintField : '1013', emo : false}, getRawSearchRouterName())"
                    >{{localData.rawInfo.prodCountryName}}</span></td>
                  <td v-else>{{localData.rawInfo.prodCountryName || '-'}}</td>
                </tr>                             
                <tr>
                  <th>등록일</th>
                  <td>{{localData.rawInfo.rgtDttm | eFmtDateFormat('YY.MM.DD')}}</td>
                </tr>
                
              </tbody>
            </table>
          </div>
        </div>

        <!--오류-->
        <AlertModal ref="alertModal"></AlertModal>
        <ConfirmModal ref="confirmModal"></ConfirmModal>

      </div>
    </template>

  </ModalFull>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import favoriteMixin from '@/components/favorite/favoriteMixin';
import { EventBus } from '@/components/eventBus';

import ModalFull from '@/components/modal/ModalFull.vue';

import { REF_CODES } from '@/constants/favorite';

export default {
  name: 'coching-rawInfo-modal',
  mixins: [ernsUtils, favoriteMixin],
  components: {
    ModalFull,
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
  },
  data() {
    return {        
      localData: {
        isActive : false,
        classOption: "modal-md for-card",
        result : undefined,

        rawInfo : {
          rawTypeInfo : [],
          rawDetailInfo : []
        },
        hashTags:[],
      },         
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
    async open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = true;
      ld.rawInfo = options.rawInfo;
      ld.rawInfo.rawDetailInfo = ld.rawInfo.rawDetailInfo.map(item=>{
        return {
          ...item,
          isFavorite: false
        }
      });      
      _vm.adjustHashTags();
      await _vm.settingMyFavorite();

      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },

    getRawSearchRouterName(){
      const _vm = this;

      //현재가 원료검색중이면 원료, 아니면 메인검색으로
      return _vm.$route.name == 'coching-search-raws' ? 'coching-search-raws' : 'coching-search-main';
    },  

    //쪽지보내기
    onClickSendMessage(detailItem){
      const _vm = this;

      //_vm.onClickClose();
      EventBus.$emit('open-message-modal', {
        membSeq : detailItem.membSeq,
        membName : detailItem.membName,
      });      
    },

    //원료요청
    onClickRequest(detailItem){
      const _vm = this;

      //_vm.onClickClose();
      _vm.$emit('onClickRequest', _vm.localData.rawInfo, detailItem);
    },

    //즐겨찾기
    async onClickFavorite(detailItem){
      const _vm = this;

      const ret = await _vm.setToggleMyFavorite({
        refCode : REF_CODES.RAW,
        refSeq : detailItem.rawDetailSeq
      });

      if(ret == null){
        return;
      }
      
      for(const dItem of _vm.localData.rawInfo.rawDetailInfo){
        if(dItem.rawDetailSeq == detailItem.rawDetailSeq){
          dItem.isFavorite = ret.resultData == 1;
          break;
        }
      }
    },

    //상세보기 클릭
    onClickRawDetail(detailItem){
      const _vm = this;
      
      _vm.ermPushPage({
        name:'coching-raw-detail', 
        query : {
          rawSeq : _vm.localData.rawInfo.rawSeq,
          rawDetailSeq : detailItem.rawDetailSeq
        }
      });
    },

    //전성분 상세보기 클릭
    onClickIngdList(ingdItem){
      const _vm = this;
      
      _vm.ermPushPage({
        name:'coching-ingredients', 
        query : {
          rawSeq : _vm.localData.rawInfo.rawSeq,
          ingdId : ingdItem.id
        }
      });
    },

    //hashtag 정리
    adjustHashTags(){
      const _vm = this;

      const ld = _vm.localData;
      const rawHashTags = [];
      const detailList = ld.rawInfo.rawDetailInfo || [];
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

    //즐겨찾기 설정
    async settingMyFavorite(){
      const _vm = this;

      const ld = _vm.localData;
      const refSeqs = (ld.rawInfo.rawDetailInfo || []).map(item=>item.rawDetailSeq);
      const ret = await _vm.getMyFavorites(REF_CODES.RAW, refSeqs);

      if(ret == null){
        return;
      }

      const detailList = ld.rawInfo.rawDetailInfo || [];
      for(const dItem of detailList){
        const favItem = ret.resultData.list.find(item=>item.refSeq == dItem.rawDetailSeq);
        if(favItem){
          dItem.isFavorite = true;
        }
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

    onClickClose() {
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = false;
      ld.result(false);
    },

    docReady(){
      const _vm = this;   

    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>