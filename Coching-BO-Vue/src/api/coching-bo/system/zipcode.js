import request from '@/utils/request'

const getZipcodeList = async function(params){
    return request({
        url: '/api/zipcode/list.api',
        method: 'post',
        data: params
    });
};

const setZipcode = async function(){
    return request({
        url: '/api/zipcode/renew.api',
        method: 'post'
    });
};


export {
    getZipcodeList,
    setZipcode
}