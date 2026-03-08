import request from '@/utils/request'

const inquiryList = async function(params){
    return request({
        url: '/api/inquiry/list.api',
        method: 'post',
        data: params
    });
};

const inquiryDetail = async function(params){
    return request({
        url: '/api/inquiry/load.api',
        method: 'post',
        data: params
    });
};

const addInquiry = async function(formData){
    return request({
        url: '/api/inquiry/add.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const editInquiry = async function(formData){
    return request({
        url: '/api/inquiry/edit.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};


export {
    inquiryList,
    inquiryDetail,
    addInquiry,
    editInquiry,
    
}