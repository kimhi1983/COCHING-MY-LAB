import request from '@/utils/request'

const getMessageList = async function(params){
    return request({
        url: '/api/message/list.api',
        method: 'post',
        data: params
    });
};

export {
    getMessageList,
}