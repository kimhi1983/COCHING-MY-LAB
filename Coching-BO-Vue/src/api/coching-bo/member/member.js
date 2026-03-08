import request from '@/utils/request'


const getList = async function (params) {
    return request({
        url: '/api/member/list.api',
        method: 'post',
        data: params
    });
};

const getMember = async function (params) {
    return request({
        url: '/api/member/get.api',
        method: 'post',
        data: params
    });
};

const setMember = async function (params) {
    return request({
        url: '/api/member/set.api',
        method: 'post',
        data: params
    });
};

const updateMember = async function (params) {
    return request({
        url: '/api/member/updateMember.api',
        method: 'post',
        data: params
    });
};

const chkDuplicate = async function (params) {
    return request({
        url: '/api/member/chkDuplicate.api',
        method: 'post',
        data: params
    });
};

const setUseYn = async function(params){
    return request({
        url: '/api/member/update/useYn.api',
        method: 'post',
        data: params
    });
};

const initPasswd = async function (params) {
    return request({
        url: '/api/member/initPasswd.api',
        method: 'post',
        data: params
    });
};

const getCmMgrEmulatorUser = async function(params){
    return request({
        url: '/api/member/emulator/user.api',
        method: 'post',
        data: params
    });
};

const getCmMgrEmulatorPartner = async function(params){
    return request({
        url: '/api/member/emulator/ptn.api',
        method: 'post',
        data: params
    });
};
const setMemberState = async function(params) {
    return request({
        url: '/api/member/setState.api',
        method: 'post',
        data: params
    });
}

export {
    getList,
    getMember,
    setMember,
    updateMember,
    chkDuplicate,
    initPasswd,
    setUseYn,
    getCmMgrEmulatorUser,
    getCmMgrEmulatorPartner,
    setMemberState,
}