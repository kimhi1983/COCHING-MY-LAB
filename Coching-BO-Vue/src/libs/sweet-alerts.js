import Vue from 'vue'
import VueSweetalert2 from 'vue-sweetalert2'

Vue.use(VueSweetalert2, {
    reverseButtons : true,
    confirmButtonText: "확인",
    denyButtonText: "아니오",
    cancelButtonText: "취소",
})
