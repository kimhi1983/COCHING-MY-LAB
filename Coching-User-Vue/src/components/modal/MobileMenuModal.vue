<template>
  <div>
    <ModalFull :isActive="localData.isActive" 
      :classOption="localData.classOption" 
      @onClickDim="onClickClose()"
      @close="onClickClose()">
            <!-- Header Content -->
            <template v-slot:header>
              <!--로그인 전-->
              <div v-if="!localData.isLoggedIn" class="logo">
                <img src="@/assets/images/logo.svg" alt="COCHING" />
              </div>

              <!--noti 로그인 후 노출-->
              <div v-if="localData.isLoggedIn"
                ref="userNotiContainer"
                class="noti-container toggle-container">
                <div @click="onClickToggleUserNoti"
                  class="ic-wrap toggle-btn">
                  <!--noti-badge 두자리 수 까지만 99+-->
                  <div class="noti-badge">{{ notiData.newCnt }}</div>
                  <div class="ic-lg ic-noti"></div>
                </div>
                <div :class="{'on' : toggle.userNoti}" class="dropdown box toggle-content for-noti">
                  <!--header-->
                  <div class="header">
                    <div class="title">알림</div>
                    <button @click="onClickCloseUserNoti"
                      type="button" class="ic-md ic-close"></button>
                  </div>
                  <!--body-->
                  <div class="body">
                    <!--알림 없음-->
                    <div v-show="notiData.list.length == 0" class="noti-none">알림이 없습니다.</div>

                    <!--item-->
                    <div v-for="(item, index) of notiData.list"
                      @click="onClickNoti(item)"
                      :key="index"
                      :class="item.chkYn === 'N' ? 'item' : 'item off'">
                      <div class="title">{{ getNotiTitle(item) }}</div>
                      <div class="info">
                        <div class="data">{{ item.content }}</div>
                      </div>
                      <div class="date">{{ item.rgtDttm | eFmtDateHMFormat('YYYY.MM.DD') }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </template>

            <!-- Body Content -->
            <template v-slot:body>
              <!--로그인 후-->
              <div v-if="localData.isLoggedIn" class="user-wrap">
                {{ eumLoginUser.userName }}{{ '님' }}<br />
                {{ '환영합니다' }}:&#41;
              </div>

              <div class="board-menu">
                <div @click="onClickBoard('raw')" class="item">
                  <div class="ic-xlg ic-drop"></div>
                  <div class="title">원료소싱</div>
                </div>
                <div @click="onClickBoard('notice')" class="item">
                  <div class="ic-xlg ic-speaker"></div>
                  <div class="title">공지사항</div>
                </div>
                <div @click="onClickClose(); onClickTv();" class="item">
                  <div class="ic-xlg ic-tv"></div>
                  <div class="title">코칭TV</div>
                </div>
                <div @click="onClickMyPage('006')" class="item">
                  <div class="ic-xlg ic-user"></div>
                  <div class="title">개인정보</div>
                </div>
                <!-- <div v-if="localData.isLoggedIn" @click="onClickBoard('faq')" class="item">
                  <div class="ic-xlg ic-faq"></div>
                  <div class="title">FAQ</div>
                </div> -->
              </div>

              <!--button-->
              <div v-if="!localData.isLoggedIn" class="btn-area">
                <!--로그인 전-->
                <button @click="onClickLogin"
                  type="button" class="btn btn-md btn-primary w-100">로그인</button>
                <button @click="onDoJoin"
                  type="button" class="btn btn-md btn-primary-outline w-100">회원가입</button>
              </div>

              <!--로그인 후 메뉴 노출-->
              <div v-if="localData.isLoggedIn" class="my-menu">
                <div v-if="eumLoginUser && 0 < eumLoginUser.ptnSeq" 
                  @click="onClickPartner()" class="item" OnClick="location.href ='javascript:;'">
                  <div class="ic-lg ic-building"></div>
                <div class="title">원료사 홈</div>
                </div>
                <div @click="onClickClose(); onClickTv();" class="item">
                  <div class="ic-lg ic-tv"></div>
                  <div class="title">코칭 TV</div>
                </div>
                <!-- <div @click="onClickClose(); onClickForeignPtn();" class="item">
                  <div class="ic-lg ic-partner"></div>
                  <div class="title">해외 원료사</div>
                </div> -->
                <div @click="onClickClose(); onClickWeeklyNews();" class="item">
                  <div class="ic-lg ic-news"></div>
                  <div class="title">Weekly News</div>
                </div>
                <div @click="onClickMyPage('003')" class="item">
                  <div class="ic-lg ic-list"></div>
                  <div class="title">원료자료요청</div>
                </div>
                <div @click="onClickMyPage('001')" class="item">
                  <div class="ic-lg ic-message"></div>
                  <div class="title">쪽지함</div>
                </div>
                <div @click="onClickMyPage('002')" class="item">
                  <div class="ic-lg ic-heart"></div>
                  <div class="title">찜한 원료</div>
                </div>
                <div @click="onClickMyPage('004')" class="item">
                  <div class="ic-lg ic-eye"></div>
                  <div class="title">최근 본 원료</div>
                </div>
                <div @click="onClickClose(); onClickUserManual();" class="item">
                  <div class="ic-lg ic-book"></div>
                  <div class="title">사용자 매뉴얼</div>
                </div>
                <div  @click="onClickClose(); onClickInquiry();" class="item">
                  <div class="ic-lg ic-chats"></div>
                  <div class="title">1:1 문의하기</div>
                </div>
                <div @click="onClickMyPage('005')" class="item">
                  <div class="ic-lg ic-edit"></div>
                  <div class="title">내가 쓴 글</div>
                </div>
                <div class="item" @click="onClickLogout()">
                  <div class="ic-lg ic-logout"></div>
                <div class="title">로그아웃</div>
                </div>
              </div>
            </template>
            
            <template v-slot:footer>
            </template>
          </ModalFull>
          
		<!--오류-->
		<AlertModal ref="alertModal"></AlertModal>
		<ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import ModalFull from '@/components/modal/ModalFull.vue';
import { getCodes } from '@/api/coching/comm/code';
import { getNotiList, updateChkYn } from '@/api/coching/comm/notification';

export default {
    name: 'coching-modal-full',
    mixins: [ernsUtils],
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
        _vm.$nextTick(() => _vm.$refs.cntrcDetailForm.validate());
      },
      
    },
    data() {
        return {
          CODES: {
           
          },
          localData: {
            isActive : false,
            classOption: "for-menu pc-none",
            result : undefined,
            isLoggedIn: false,
          }, 
          
          toggle: {
            userNoti: false,
          },
          
          notiData :{
            list: [],
            newCnt: 0,
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
    window.removeEventListener("click", this.handleClickNotiOutside);
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

    async open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = true;
      ld.isLoggedIn = options.isLoggedIn;

      if(_vm.localData.isLoggedIn){
          await _vm.loadNoti();
        }

      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },

    onClickClose() {
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = false;
      ld.result(false);
    },

    closeModal() {
      const _vm = this;
      _vm.$emit('close'); // 부모에게 close 이벤트 전달
    },

    async loadCodes() {
        const _vm = this;

        const codeRes = await getCodes({ grpCode: "CH012", etc5: _vm.$i18n.locale });
        const { resultCode, resultFailMessage, resultData } = codeRes;

        _vm.CODES.CH012 = [...resultData.list];
    },

    //로그인
    onClickLogin(){
      const _vm = this;
      _vm.onClickClose();
      _vm.ermPushPage({
        name:'coching-login',
        query: { retRoute: _vm.$route.fullPath }
      });
    },

    //로그아웃
		async onClickLogout(){
			const _vm = this;

			const ret = await _vm.$refs["confirmModal"].open({
				title: _vm.$t('') || '로그아웃 하시겠습니까?',
				content : ''
			});

			if(!ret){
				return;
			}
      _vm.onClickClose();
			_vm.eumLogout(); 
		},

    //회원가입 이동
    onDoJoin(){
      const _vm = this;
      _vm.onClickClose();
      _vm.ermPushPage({name:'coching-user-type'});
    },

    async onClickPartner(){
      const _vm = this;

      _vm.loading(true);
      try {

        // 새 창으로 URL 열기
        const partnerBaseUrlData = _vm.eumPartnerBaseUrlData;
        const baseUrl = partnerBaseUrlData.baseUrl ? partnerBaseUrlData.baseUrl : 'https://partner.cochingprt.co.kr';
        const url = baseUrl +"/common/user/login";

        // 새 창 열기
        const newWindow = window.open('', '_blank'); // 빈 새 창을 열음

        // 새 창이 열렸는지 확인 (팝업 차단 방지)
        if (!newWindow) {
          alert('팝업이 차단되었습니다. 팝업 차단을 해제해주세요.');
          return;
        }

        // 새 창에서 POST 요청을 위한 form 생성
        const form = newWindow.document.createElement('form');
        form.method = 'POST';
        form.action = url;

        // 전달할 데이터
        const params = {
          refreshToken: _vm.eumRefreshToken,
        };

        // form에 데이터 추가
        for (const key in params) {
          if (params.hasOwnProperty(key)) {
            const input = document.createElement('input');
            input.type = 'hidden'; // 숨겨진 input으로 추가
            input.name = key; // 파라미터 이름
            input.value = params[key]; // 파라미터 값
            form.appendChild(input); // form에 추가
          }
        }

        // 새 창 body에 form 추가
        newWindow.document.body.appendChild(form);

        // form 제출
        form.submit();

      } catch (err) {
        console.error(err);
      } finally {
        _vm.loading(false);
      }
    },

    //게시판
    onClickBoard(type){
      const _vm = this;

      _vm.onClickClose();
      
      if(!type) {
        type = 'raw';
      }

      if(type == 'raw') {
        _vm.ermPushPage({name:'coching-rawSourcing-board-list'});
      } else if (type == 'notice') {
        _vm.ermPushPage({name:'coching-notice-board-list'});
      } else if (type == 'faq') {
        _vm.ermPushPage({name:'coching-faq-board-list'});
      }
    },

    //마이페이지
     onClickMyPage(code){
      const _vm = this;
      _vm.onClickClose();

      let router;
      switch(code){
        case "001":          
          router = 'coching-mypage-message';
          break;
        case "002":                    
          router = 'coching-mypage-wish';
          break;
        case "003":                    
          router = 'coching-mypage-requestRaw';
          break;
        case "004":                    
          router = 'coching-mypage-recentView';
          break;
        case "005":                    
          router = 'coching-mypage-myWrite';
          break;
        case "006":                    
          router = 'coching-mypage-myInfo';
          break;
        default:
          router = 'coching-mypage-message';
          break;
      }      
      
      _vm.ermPushPage({name:router});
    },

    async loadNoti(){
      const _vm = this;

      _vm.loading(true);
        try {
            const params = {
              page: 1,
              rowSize: 10
            };
            const res = await getNotiList(params);
            const resData = res.resultData;

            //데이터 바인딩
            const dataMap = _vm.notiData;
            dataMap.list = resData.list;
            dataMap.newCnt = resData.newCnt;
        } catch (err) {
            _vm.defaultApiErrorHandler(err); //기본에러처리
        } finally {
            _vm.loading(false);
        }
    },

    getNotiTitle(item) {
      const _vm = this;
      const matchingCode = _vm.CODES.CH012.find(code => code.etc1 === item.refCode);
      return matchingCode ? matchingCode.codeName : "알림이 도착했습니다."; 
    },

    async onClickNoti(item){
      const _vm = this;
      _vm.onClickClose();

      if(item.chkYn === 'N'){
        await updateChkYn({notiSeq: item.notiSeq, chkYn: 'Y'});
      }
      _vm.onClickCloseUserNoti();
      _vm.eumGoNotiRouter(item);
    },

   //사용자 알림 토글
   onClickToggleUserNoti() {
      const _vm = this;

      _vm.toggle.userNoti = !_vm.toggle.userNoti;
      if (_vm.toggle.userNoti) {
        _vm.$nextTick(() => {          
          window.addEventListener("click", _vm.handleClickNotiOutside);
        });
      } else {
        window.removeEventListener("click", _vm.handleClickNotiOutside);
      }      
    },

    //사용자 알림 닫기
    onClickCloseUserNoti() {
      const _vm = this;

      _vm.toggle.userNoti = false;
      window.removeEventListener("click", _vm.handleClickNotiOutside);
    },

    //사용자 알림창 밖에 영역 클릭 감지
    handleClickNotiOutside(event) {
      const _vm = this;
      if (_vm.$refs.userNotiContainer && _vm.$refs.userNotiContainer.contains(event.target)) {
        return;
      }

      if (_vm.$refs.userNotiLayer && _vm.$refs.userNotiLayer.contains(event.target)) {
        return;
      }

      _vm.toggle.userNoti = false;
      window.removeEventListener("click", _vm.handleClickNotiOutside);
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