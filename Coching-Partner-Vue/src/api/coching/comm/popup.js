import request from '@/utils/request'

const getPopupList = async function(params){
    return request({
        url: '/api/popup/list.api',
        method: 'post',
        data: params
    });
};

const setPopupClick = async function(params){
    return request({
        url: '/api/popup/click.api',
        method: 'post',
        data: params
    });
};

export {
    getPopupList,
    setPopupClick,
}