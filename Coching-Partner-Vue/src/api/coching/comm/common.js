import request from '@/utils/request'

const getUserBaseUrl = async function(){
    return request({
        url: '/api/common/userBaseUrl.api',
        method: 'post'
    });
};

export {
    getUserBaseUrl,
}