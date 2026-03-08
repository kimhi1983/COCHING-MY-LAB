import request from '@/utils/request'

const getPtnInfo = async function(params){
    return request({
        url: '/api/partner/ptnInfo.api',
        method: 'post',
        data: params
    });
};


export {
    getPtnInfo,
}