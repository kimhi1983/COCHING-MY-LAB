import request from '@/utils/request'

const getUserLogList = async function(params){
    return request({
        url: '/api/log/list.api',
        method: 'post',
        data: params
    });
};

export {
    getUserLogList,
}