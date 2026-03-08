<template>
  <b-modal
    v-model="isVisible"
    :title="modalTitle"
    centered
    no-close-on-backdrop
    size="lg"
    scrollable
    @cancel="onCancel"
  >
    <validation-observer ref="formValidation">
      <b-form>
        <b-row>
          <!-- tv_seq (수정 시에만 표시) -->
          <b-col cols="12" v-if="formData.tvSeq">
            <b-form-group label="코칭 TV SEQ" label-for="tvSeq">
              <b-form-input
                id="tvSeq"
                v-model="formData.tvSeq"
                disabled
              />
            </b-form-group>
          </b-col>

          <!-- 유튜브 URL -->
          <b-col cols="12">
            <b-form-group label="유튜브 URL" label-for="ytUrl">
              <validation-provider
                #default="{ errors }"
                name="유튜브 URL"
                rules="required"
              >
                <b-input-group>
                    <b-form-input
                        id="ytUrl"
                        v-model="formData.ytUrl"
                        placeholder="유튜브 URL을 입력하세요"
                    />
                    <b-input-group-append>
                        <b-button
                        variant="outline-primary"
                        @click="fetchYoutubeInfo(formData)"
                        :disabled="!formData.ytUrl"
                        >
                        정보 가져오기
                        </b-button>
                    </b-input-group-append>
                </b-input-group>
                <small class="text-danger">{{ errors[0] }}</small>
              </validation-provider>
            </b-form-group>
          </b-col>

          <!-- 제목 -->
          <b-col cols="12">
            <b-form-group label="제목" label-for="title">
              <validation-provider
                #default="{ errors }"
                name="제목"
                rules="required"
              >
                <b-form-input
                  id="title"
                  v-model="formData.title"
                  placeholder="제목을 입력하세요"
                />
                <small class="text-danger">{{ errors[0] }}</small>
              </validation-provider>
            </b-form-group>
          </b-col>

          <!-- 해시태그 -->
          <b-col cols="12">
            <b-form-group label="해시태그" label-for="hashtag">
              <b-form-textarea
                id="hashtag"
                v-model="formData.hashtag"
                placeholder="해시태그를 입력하세요"
                rows="3"
              />
            </b-form-group>
          </b-col>

          <!-- 내용 -->
          <b-col cols="12">
            <b-form-group label="내용" label-for="content">
              <b-form-textarea
                id="content"
                v-model="formData.content"
                placeholder="내용을 입력하세요"
                rows="3"
              />
            </b-form-group>
          </b-col>

          <!-- 조회수 -->
          <b-col cols="6">
            <b-form-group label="조회수" label-for="views">
              <b-form-input
                id="views"
                v-model="formData.views"
                placeholder="조회수를 입력하세요"
                disabled
              />
            </b-form-group>
          </b-col>

          <!-- 영상 등록일 -->
          <b-col cols="6">
            <b-form-group label="영상 등록일" label-for="ytDttm">
              <b-form-input
                id="ytDttm"
                :value="formData.ytDttm | eFmtDateTime"
                disabled
              />
            </b-form-group>
          </b-col>

          <!-- 사용여부 -->
          <b-col cols="6">
            <b-form-group 
                label="사용 여부"
                label-for="useYn"
            >
            <b-form-radio-group
                id="useYn"
                v-model="formData.useYn"
                :options="[{text:'사용', value:'Y'},{text:'미사용', value:'N'}]"
                button-variant="outline-primary"                
                buttons name="radio-btn-outline"
            />
            </b-form-group>
          </b-col>

          <!-- 구분 -->
          <b-col cols="6">
            <b-form-group label="구분" label-for="category">
              <validation-provider
                #default="{ errors }"
                name="구분"
                rules="required"
              >
                <b-form-select
                    id="category"
                    v-model="formData.category"
                    :options="[
                        {text:'선택하세요', value:''},
                        ...(codes.CATE || []).map(item => ({
                            text: item.label,
                            value: item.value
                        }))
                    ]"
                    placeholder="구분을 선택하세요"
                    class="custom-select"
                />
                <small class="text-danger">{{ errors[0] }}</small>
              </validation-provider>
            </b-form-group>
          </b-col>

          <b-col cols="6" v-if="formData.thumbnail">
            <b-form-group label="썸네일 미리보기" label-for="thumbnailㄸ군_ㅐㄴㄷ0707
            ">
              <img
                id="thumbnail"
                :src="formData.thumbnail"
                alt="썸네일"
                class="img-fluid rounded border"
              />
            </b-form-group>
          </b-col>

        </b-row>
      </b-form>
    </validation-observer>

    <template #modal-footer="{ ok, cancel }">
      <b-button
        v-ripple.400="'rgba(113, 102, 240, 0.15)'"
        variant="outline-primary"
        class="mr-1"
        @click="cancel()"
      >
        취소
      </b-button>
      <b-button
        v-ripple.400="'rgba(113, 102, 240, 0.15)'"
        variant="primary"
        @click="onSubmit()"
      >
        {{ isEdit ? '수정' : '등록' }}
      </b-button>
    </template>
  </b-modal>
</template>

<script>
import ernsUtils from "@/components/mixins/ernsUtils";
import {
  BModal,
  BForm,
  BFormGroup,
  BFormInput,
  BFormSelect,
  BFormTextarea,
  BButton,
  BRow,
  BCol,
} from 'bootstrap-vue';
import vSelect from 'vue-select';
import { ValidationProvider, ValidationObserver } from 'vee-validate';
import Ripple from 'vue-ripple-directive';

import { getCodes } from '@/api/coching-bo/comm/code';
import { getYoutubeApiInfo } from "@/api/coching-bo/cochingtv/cochingtv";

const DEF_FORM_DATA = {
  tvSeq: null,
  ytUrl: '',
  sortOrd: null,
  title: '',
  hashtag: '',
  views: '',
  ytDttm: '',
  useYn: 'Y',
  delYn: 'N',
  thumbnail: '',
  content: '',
  category: '',
}

export default {
  name: 'CochingtvFormModal',
  mixins: [ernsUtils],
  components: {
    BModal,
    BForm,
    BFormGroup,
    BFormInput,
    BFormSelect,
    BFormTextarea,
    BButton,
    BRow,
    BCol,
    ValidationProvider,
    ValidationObserver,
    vSelect,
  },

  directives: {
    Ripple,
  },

  props: {
    value: {
      type: Boolean,
      default: false,
    },
    editData: {
      type: Object,
      default: null,
    },
  },

  data() {
    return {
      codes : {
        CATE : [],
      },
      formData: { ...DEF_FORM_DATA },
    }
  },

  async mounted() {
    const _vm = this;
    await _vm.fetchData();
  },

  computed: {
    isVisible: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      }
    },
    isEdit() {
      return !!this.editData
    },
    modalTitle() {
      return this.isEdit ? '코칭TV 수정' : '코칭TV 등록'
    },
  },

  watch: {
    value(newVal) {
      if (newVal) {
        this.initForm()
      }
    },
  },

  methods: {
    async fetchData(){
      const _vm = this;
      await _vm.loadCodes();
    },

    async loadCodes(){
      const _vm = this;

      const cateList = await getCodes({grpCode:'CH016', etc5: 'ko', rowSize:-1});
      _vm.codes.CATE = _vm.eumConvertToVueSelectOption(cateList.resultData.list, 'etc1');
    },

    async fetchYoutubeInfo(param) {
        const _vm = this;
        if (!param || !param.ytUrl) return;
        _vm.loading(true);
        try {
          const youtubeInfo = await _vm.setYoutubeApiInfo(param);

          if(youtubeInfo) {
            param.title = youtubeInfo.title || '';
            param.views = youtubeInfo.viewCount || '';
            param.ytDttm = youtubeInfo.publishedAt || '';
            param.thumbnail = youtubeInfo.thumbnails || '';
          } else {
            param.title = '';
            param.views = '';
            param.ytDttm = '';
            param.thumbnail = '';
            _vm.alertError('유튜브 정보를 가져오는데 실패했습니다.');
          }

        } catch (err) {
          _vm.alertError(err);
        } finally {
          _vm.loading(false);
        }
    },

    async setYoutubeApiInfo(item){
      try {
        if (!item.ytUrl) {
          console.warn('ytUrl이 없습니다:', item);
          return;
        }

        const params = {
          ytUrl : item.ytUrl,
        };
        const youtubeInfoRes = await getYoutubeApiInfo(params);
        const youtubeInfo = youtubeInfoRes.resultData;

        return youtubeInfo;

      } catch (err) {
        console.warn('YouTube API 호출 오류:', err);
        return null;
      }
    },

    initForm() {
      if (this.isEdit) {
        this.formData = { ...this.editData }
      } else {
        this.formData = { ...DEF_FORM_DATA }
      }
    },

    async onSubmit() {

      const isValid = await this.$refs.formValidation.validate()
      
      if (!isValid) {
        return false;
      }

      this.$emit('submit', this.formData)
    },

    onCancel() {
      this.$emit('cancel')
      this.isVisible = false
    },
  },
}
</script> 
