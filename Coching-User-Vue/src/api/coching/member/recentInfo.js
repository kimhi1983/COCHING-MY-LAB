import request from '@/utils/request'

const getUserRecentInfoList = async function(params){
    return request({
        url: '/api/log/user/recent/list.api',
        method: 'post',
        data: params
    });
};

const removeUserRecentInfo = async function(params){
    return request({
        url: '/api/log/user/recent/del.api',
        method: 'post',
        data: params
    });
};


export {
    getUserRecentInfoList,
    removeUserRecentInfo,
}