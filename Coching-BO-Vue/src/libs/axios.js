import Vue from 'vue'

// axios
import axios from 'axios'

const axiosIns = axios.create({
  // You can add your headers here
  // ================================
  // baseURL: 'https://some-domain.com/api/',
  // timeout: 1000,
  // headers: {'X-Custom-Header': 'foobar'}

  headers: {
    'Access-Control-Allow-Origin': '*',
    //'Content-Type': 'application/json; charset = utf-8'
  },
  baseURL: process.env.VUE_APP_API_BASE_URL, // url = base url + request url
  withCredentials: true, // send cookies when cross-domain requests
  timeout: 30000 // request timeout
})

Vue.prototype.$http = axiosIns

export default axiosIns
