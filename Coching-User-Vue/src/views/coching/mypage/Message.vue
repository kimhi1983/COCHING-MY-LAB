<template>
  <!--section-->
  <section>
    <div class="container h-100">
      <div class="content">
        <div class="common-content">
          <NotificationMenuBar></NotificationMenuBar>

          <div class="tabs style-box">
            <div class="tabs-nav">
              <ul>
                <li 
                  :class="{ active: tabType === 'recv' }"
                  @click="tabType = 'recv'"
                ><a href="javascript:;">받은 쪽지함</a></li>
                <li 
                  :class="{ active: tabType === 'sent' }"
                  @click="tabType = 'sent'"
                ><a href="javascript:;">보낸 쪽지함</a></li>
              </ul>
            </div>

            <div class="tabs-content board-tabs-content">
              <Message 
                v-show="tabType == 'recv'"                      
                v-bind="$props"
                ownMode="recv"
              ></Message>

              <Message
                v-show="tabType == 'sent'"
                v-bind="$props"
                ownMode="sent"
              ></Message>
            </div>
                  

          </div>
        </div>
      </div>
    </div>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import Advertise from '@/components/Advertise.vue';
import NotificationMenuBar from './NotificationMenuBar.vue';

import { MESSAGE_OWN_MODE } from '@/constants/message';
import Message from '@/components/message/Message.vue';

export default {
	name: 'coching-mypage-message',
	mixins: [ernsUtils],
	components: {
    Message,

    Advertise,
    NotificationMenuBar,    
  },
	computed: {    
  },
	watch: {
	},
	props: {
    
  },
	data() {
		return {
      tabType: MESSAGE_OWN_MODE.RECIVED,
		}
	},
	async mounted() {
		const _vm = this;
    
    _vm.docReady();
    _vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

		docReady() {
			const _vm = this;

		},
	}
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
#coching-mypage-message {

}
</style>
