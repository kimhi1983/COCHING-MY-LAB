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

const setBoardCmt = async function(params){
    return request({
        url: '/api/board/set/cmt.api',
        method: 'post',
        data: params
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

const updateBoardCmt = async function(params){
    return request({
        url: '/api/board/update/cmt.api',
        method: 'post',
        data: params
    });
};

const deleteBoard = async function(params){
    return request({
        url: '/api/board/delete.api',
        method: 'post',
        data: params
    });
};

const deleteBoardCmt = async function(params){
    return request({
        url: '/api/board/delete/cmt.api',
        method: 'post',
        data: params
    });
};

const getMyBoardList = async function(params){
    return request({
        url: '/api/board/my/list.api',
        method: 'post',
        data: params
    });
};

const getInprBoardList = async function(params){
    return request({
        url: '/api/board/inqr/list.api',
        method: 'post',
        data: params
    });
};

const addInqrBoard = async function(formData){
    return request({
        url: '/api/board/inqr/new.api',
        method: 'post',
        data: formData,
        headers: {'content-type': 'multipart/form-data'}
    });
};

const getInqrBoard = async function(params){
    return request({
        url: '/api/board/inqr/get.api',
        method: 'post',
        data: params
    });
};

const getInqrBoardReply = async function(params){
    return request({
        url: '/api/board/inqr/reply.api',
        method: 'post',
        data: params
    });
};

export {
	getBoardMaster,
    getBoardList,
    getBoard,
    getBoardCmt,
    setBoard,
    setBoardCmt,
    updateBoard,
    updateBoardCmt,
    deleteBoard,
    deleteBoardCmt,

    getMyBoardList,

    getInprBoardList,
    addInqrBoard,
    getInqrBoard,
    getInqrBoardReply,
}