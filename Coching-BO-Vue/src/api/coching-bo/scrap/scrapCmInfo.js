import request from '@/utils/request'

const scrapLinkStatus = async function(params){
  return request({
      url: '/api/scrap/cmInfo/linkStatus.api',
      method: 'post',
      data: params
  });
};

const scrapLinkedAccList = async function(params){
  return request({
      url: '/api/scrap/cmInfo/linkedAccList.api',
      method: 'post',
      data: params
  });
};

export {
  scrapLinkStatus,
  scrapLinkedAccList,
}