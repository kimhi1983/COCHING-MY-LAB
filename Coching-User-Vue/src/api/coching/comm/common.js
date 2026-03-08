import request from '@/utils/request'

const getPartnerBaseUrl = async function(){
    return request({
        url: '/api/common/partnerBaseUrl.api',
        method: 'post'
    });
};

const getPdfImageInfo = async function(params){
    return request({
        url: '/api/common/pdfImageInfo.api',
        method: 'post',
        data: params
    });
};

const loadIntroInfo = async function(params){
    return request({
        url: '/api/common/loadIntroInfo.api',
        method: 'post',
        data: params
    });
};

export {
    getPartnerBaseUrl,
    getPdfImageInfo,
    loadIntroInfo
}