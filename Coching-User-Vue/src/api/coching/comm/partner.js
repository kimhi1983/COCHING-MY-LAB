import request from '@/utils/request'

const getPtnList = async function(params){
    return request({
        url: '/api/partner/list.api',
        method: 'post',
        data: params
    });
};

const getPtn = async function(params){
    return request({
        url: '/api/partner/get.api',
        method: 'post',
        data: params
    });
};


export {
    getPtnList,
    getPtn,
}