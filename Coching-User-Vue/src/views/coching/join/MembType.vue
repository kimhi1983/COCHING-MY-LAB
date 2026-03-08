<template>
  <!--section-->
  <!--section-->
  <section class="h-100">
        <div class="inner h-100">
          <div class="container h-100">
            <!--join-->
            <div class="join-wrap">
              <div class="inner">
                <div class="h1 text-center">회원가입</div>
                <div class="join-content">
                  <div class="item" @click="select('USER')">
                    <div class="inner">
                      <div class="img">
                        <img src="@/assets/images/ic-user-type.svg" alt="일반 회원가입" />
                      </div>
                      <div class="title">일반 <span>회원가입</span></div>
                    </div>
                  </div>
                  <div class="item" @click="select('PARTNER')">
                    <div class="inner">
                      <div class="img">
                        <img src="@/assets/images/ic-building-type.svg" alt="원료사 회원가입" />
                      </div>
                      <div class="title">원료사 <span>회원가입</span></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        
      </section>

</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'coching-user-type',
  mixins: [ernsUtils],
  components: {
    
},
  computed: {
    
	},
	watch: {
	},
	props: {},
	data() {
		return {

    }
  },
  async mounted(){
    const _vm = this;

    _vm.docReady(); 
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
   
    select(type){
      const _vm = this;
      _vm.ermPushPage({
        name:'coching-user-join',
        params: { membType: type }
      });
    },
   
    docReady(){
      const _vm = this;      
    }
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>