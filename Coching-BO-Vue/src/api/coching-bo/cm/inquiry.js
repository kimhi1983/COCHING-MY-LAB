import request from '@/utils/request'

const getInquiryList = async function(params){
  return request({
      url: '/api/inquiry/list.api',
      method: 'post',
      data: params
  });
};

const getInquiryDetail = async function(params){
  return request({
      url: '/api/inquiry/load.api',
      method: 'post',
      data: params
  });
};

const replyInquiryDetail = async function(formData){
  return request({
      url: '/api/inquiry/reply.api',
      method: 'post',
      data: formData,
      // headers: {'content-type': 'multipart/form-data'}
  });  
};

// const addInquiryDetail = async function(params){
//   return request({
//       url: '/api/inquiry/new.api',
//       method: 'post',
//       data: params
//      // headers: {'content-type': 'multipart/form-data'}
//   });
// };

export {
  getInquiryList,
  getInquiryDetail,  

  replyInquiryDetail,
}