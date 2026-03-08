<template>
  <div>
    <b-card no-body>
      <b-card-body>
        <b-row align-content="center">
          <b-col cols="12" md="4">
            <b-card class="mt-3">
              <div v-if="detail.fileId || imageFile">
                <b-img
                  fluid
                  class="mb-2 imgs"
                  :src="imageFile ? imageFile : eumFileImagePath(detail.fileId, 0)"
                  img-alt="대체 이미지"
                  @click="$refs.inputFile.click()"
                >
                </b-img>
                <div>
                  <h6>
                    <a :href="detail.fileId ? eumFileDownPath(detail.fileId) : ''"
                    v-text="detail.fileId ? detail.fileName : imageFileName" 
                    /> <b-badge @click.prevent="onClickDelFile(detail.fileId)">Del</b-badge>
                  </h6>
                </div>
              </div>  
              <div
                v-else 
                class="fileInput" 
                @click="$refs.inputFile.click()"
              >
                +
              </div>
               <input
                  type="file"
                  ref="inputFile"
                  accept=".jpg, .png, .gif"
                  @change="onChangeImages"
                  style="display:none;"
                />
            </b-card>
          </b-col>
          <b-col cols="12" md="8">
            <b-card>
              <b-card-body>
                <b-form-group label="메인화면이름"
                  label-for="sys-main-image-name">
                  <b-form-input
                    id="sys-main-image-name"
                    type="text"              
                    placeholder="메인화면이름을 입력하세요."
                    v-model="detail.bannerName"
                  />
                </b-form-group>
                <b-form-group
                  label="설명"
                  label-for="sys-banner-bannerDesc"
                >
                  <b-form-input
                    id="sys-banner-bannerDesc"
                    placeholder="설명을 입력하세요."
                    v-model="detail.bannerDesc"
                  />
                </b-form-group>
                <b-form-group label="문구1"
                  label-for="sys-main-image-text1">
                  <b-form-input
                    id="sys-main-image-text1"
                    type="text"              
                    placeholder="문구를 입력하세요."
                    v-model="detail.text1"
                  />
                </b-form-group>
                <b-form-group label="문구2"
                  label-for="sys-main-image-text2">
                  <b-form-input
                    id="sys-main-image-text2"
                    type="text"              
                    placeholder="문구를 입력하세요."
                    v-model="detail.text2"
                  />
                </b-form-group>
                <b-form-group label="문구3"
                  label-for="sys-main-image-text3">
                  <b-form-input
                    id="sys-main-image-text3"
                    type="text"              
                    placeholder="문구를 입력하세요."
                    v-model="detail.text3"
                  />
                </b-form-group>
                <b-form-group 
                  label="사용 여부"
                  label-for="sys-banner-useYn"
                >
                  <b-form-radio-group
                    id="sys-banner-useYn"
                    v-model="detail.useYn"
                    :options="[{text:'사용', value:'Y'},{text:'미사용', value:'N'}]"
                    button-variant="outline-primary"                
                    buttons name="radio-btn-outline"
                  />
                </b-form-group>
                
              </b-card-body>
            </b-card>
          </b-col>

          <b-col md="6" v-if="!isRegMode">
            <b-form-group label="작성자"
              label-for="bi-form-writerName">
              <b-form-input
                readonly
                id="bi-form-writerName"
                type="text"              
                placeholder="작성자 이름을 입력하세요."
                v-model="detail.writerName"                  
                :formatter="eufmtMaxLength50"
              />
            </b-form-group>
          </b-col>

          <b-col md="6" v-if="!isRegMode">
            <label>등록일 : {{detail.regDate | eFmtDateTime}}</label><br/>
            <label>최종수정일 : {{detail.udtDate | eFmtDateTime}}</label>
          </b-col>

          <b-col>
            <!-- 버튼 영역 -->
            <div class="text-center mt-4 mb-3">
              <b-button v-if="!isRegMode"
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-danger"                
                class="mr-1"
                @click.prevent="onClickDelete"
              > 삭제
              </b-button>
              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="primary"
                class="mr-1"
                @click.prevent="onClickSave"
              > 저장
              </b-button>
              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-primary"
                class="mr-1"
                @click.prevent="onClickCancel"
              > 취소
              </b-button>
            </div>
            <!-- // 버튼 영역 -->
          </b-col>
        </b-row>
      </b-card-body>
    </b-card>
  </div>
</template>

<script>
import {addBanner, getBannerFile, setBanner, delBanner} from '@/api/coching-bo/sys/banner';

import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive'

const DEFAULT_BANNER = {
  bannerMstId: "BANNER_0001", // 메인화면
  bannerId: 0,
  bannerName: "",
  bannerType: "", // 롤링, 고정 ...
  startDate: "",
  endDate: "",
  bannerImgUrl: "",
  bannerDesc: "",
  topPhrase : "",
  bottomPhrase: "",
  bannerUrl: "",
  bannerWidth: 0,
  bannerHeight: 0,
  text1: "",
  text2: "",
  text3: "",
  bannerHit: 0,
  bannerOrder: 1,
  delYn: "N"
}

export default {
  name: 'coching-BackOffice-sys-MainImg-insert',
  mixins: [ernsUtils],  
  components : {
    
  },
  props: {

  },
  directives: {
    Ripple
  },
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    },
    isRegMode(){
      const params = this.params;
      if(!params.hasOwnProperty('bannerId')){
        return true;
      }

      const pBannerId = params.bannerId;
      return !(pBannerId && pBannerId > 0);
    }
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData'
  },
  data(){
    return {
      detail: DEFAULT_BANNER,
      file: null,
      fileId: null,
      imageFile : null,
      imageFileName : "",
      delFiles : [],
      selected: false,
      disabled: true,
      params: {},
    }
  },
  mounted(){
    const _vm = this;    
    _vm.fetchData();
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    getInitParam(){
      const _vm = this;      
      const query = _vm.$route.query, params = _vm.$route.params;

      _vm.params = {
        ...query
      };

    },

    fetchData(){
      const _vm = this;
      _vm.getInitParam();
      _vm.loadDetail();
    },

    //데이터 로드
    async loadDetail(){
      const _vm = this; 
      
      //_vm.$refs["inputFile"].reset(); // b-form-file
      _vm.$refs.inputFile.value = null; // input type=file
      _vm.delFiles = [];

      if(_vm.isRegMode){
        //등록모드
        _vm.detail = {...DEFAULT_BANNER};
        return;
      }

      const params = {      
        bannerMstId : "BANNER_0001",
        bannerId : _vm.params.bannerId,
        delYn: "N"
      };

      const res = await getBannerFile(params);

      _vm.detail = {
        ...res.resultData
      }

      //잘못된 접근
      if(!_vm.detail.bannerId){
        await _vm.alertError(`내용을 찾을 수 없습니다.`);
        _vm.$router.replace({ name: 'coching-bo-sys-banner-main'});        
      }
    },

    async onClickSave() {
      const _vm = this;
      //TODO: validation

      try {
        _vm.loading(true);
        const params = {
          ..._vm.detail
        };
        
        let res = null;

        const formData = new FormData();

        for( const key in params) {
          formData.append(key, params[key]);
        }
        
        if(_vm.file) {
          formData.append("ban_files_01", _vm.file[0]);
        }
        // formData.append("ban_files_03", _vm.file[0]);
        
        { //삭제할 파일
          formData.append("delFileIds", _vm.delFiles.join());
        }

        if(_vm.isRegMode) {
          res = await addBanner(formData);

          await _vm.alertSuccess(`등록 되었습니다.`);
        } else {
          res = await setBanner(formData);
          _vm.detail = {
            ...res.resultData
          };
          await _vm.alertSuccess(`수정 했습니다.`); 
          await _vm.loadDetail();
        }
        
      } catch(err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //취소
    onClickCancel(){
      const _vm = this;
      _vm.$router.back();
    },

    onChangeImages(event) {
      const _vm = this;
      if(event.target.files) {
        const reader = new FileReader();
        reader.onload = (e) => {
          _vm.imageFile = e.target.result;
        }
        reader.readAsDataURL(event.target.files[0]);
        _vm.imageFileName = event.target.files[0].name;
        _vm.file = event.target.files;

        _vm.delFiles.push(_vm.detail.fileId);

      }

    },
    
    onClickDelFile(fileId) {
      const _vm = this;

      _vm.delFiles.push(fileId);
      _vm.detail.fileId = null;

      _vm.imageFile = null;

    },

    disableCalendar() {
      const _vm = this;
      _vm.disabled = !_vm.disabled;
    },

    async onClickDelete() {
      const _vm = this; 

      const result = await _vm.$swal({
        title: '확인',
        text: "삭제 하시겠습니까?",
        showCancelButton: true,
        confirmButtonText: '삭제',
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });
      
      if(!result.value) {
        return;
      }

      try{
        _vm.loading(true);
        const params = {
          ..._vm.detail
          , delYn : "Y" //삭제 처리
        };

        let res = null;
        if(_vm.isRegMode){
          return;
        }else{
          //수정모드
          res = await delBanner(params);
          _vm.detail = {
            ...res.resultData
          };

          _vm.loading(false);
          await _vm.alertSuccess(`삭제 했습니다.`);      
        }

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },
  }    
}
</script>

<style lang="scss" scoped>
.fileInput{ 
  background-color: #edecfc;
  border: 0;
  border: 1px dotted #7367f0;
  height: inherit;
  line-height: 400px;
  text-align: center;
  font-size: 50px;
  color: #7367f0;
  padding: 0;
}

.fileInput:hover{
  background-color: #e2e0ff;
  cursor: pointer;
}

.imgs:hover {
  cursor: pointer;
}
</style>