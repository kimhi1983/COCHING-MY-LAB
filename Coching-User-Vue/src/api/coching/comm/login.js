import request from '@/utils/request'

//id 찾기
const getUserId = async function(params){
  return request({
      url: '/api/login/getUserId.api',
      method: 'post',
      data: params
  });
};

//비밀번호 변경 요청 이메일 발송
const reqResetPasswd = async function(params){
  return request({
      url: '/api/login/reqResetPasswd.api',
      method: 'post',
      data: params
  });
};

//비밀번호 리셋, 반드시 메일을 통해서 세션에 값이 할당되어 있어야 작동함
const resetPasswd = async function(params){
    return request({
        url: '/api/login/resetPasswd.api',
        method: 'post',
        data: params
    });
};

export {
  getUserId,
  reqResetPasswd,
  resetPasswd,
}