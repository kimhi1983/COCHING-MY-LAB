import request from '@/utils/request'

const getBoardMaster = async function(params){
    return request({
        url: '/api/board/mst/get.api',
        method: 'post',
        data: params
    });
};

const getBoardList = async function(params){
    return request({
        url: '/api/board/list.api',
        method: 'post',
        data: params
    });
};

const getBoard = async function(params){
    return request({
        url: '/api/board/get.api',
        method: 'post',
        data: params
    });
};

const getBoardCmt = async function(params){
    return request({
        url: '/api/board/get/cmt.api',
        method: 'post',
        data: params
    });
};

const setBoard = async function(formData){
    return request({
        url: '/api/board/set.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const updateBoard = async function(formData){
    return request({
        url: '/api/board/update.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

export {
	getBoardMaster,
    getBoardList,
    getBoard,
    getBoardCmt,
    setBoard,
    updateBoard
}