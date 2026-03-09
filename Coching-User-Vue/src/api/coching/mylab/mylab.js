import request from '@/utils/request';

const getLabMasterList = function(params) {
  return request({
    url: '/api/rnd/labMaster/list.api',
    method: 'post',
    data: params,
  });
};

const getLabMasterDetail = function(params) {
  return request({
    url: '/api/rnd/labMaster/get.api',
    method: 'post',
    data: params,
  });
};

const addLabMaster = function(params) {
  return request({
    url: '/api/rnd/labMaster/add.api',
    method: 'post',
    data: params,
  });
};

const setLabMaster = function(params) {
  return request({
    url: '/api/rnd/labMaster/set.api',
    method: 'post',
    data: params,
  });
};

const deleteLabMaster = function(params) {
  return request({
    url: '/api/rnd/labMaster/del.api',
    method: 'post',
    data: params,
  });
};

const getAiPrscStream = function(params, onChunk, onDone, onError) {
  var controller = new AbortController();
  fetch('/api/ai/v1/formulate/stream', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(params),
    signal: controller.signal,
  })
    .then(function(res) {
      if (!res.ok) throw new Error('HTTP ' + res.status);
      var reader = res.body.getReader();
      var decoder = new TextDecoder();
      var buffer = '';
      var newline = String.fromCharCode(10);
      function pump() {
        return reader.read().then(function(result) {
          if (result.done) { if (onDone) onDone(); return; }
          buffer += decoder.decode(result.value, { stream: true });
          var lines = buffer.split(newline);
          buffer = lines.pop();
          lines.forEach(function(line) {
            if (line.indexOf('data: ') !== 0) return;
            var raw = line.slice(6).trim();
            if (raw === '[DONE]') { if (onDone) onDone(); return; }
            try { if (onChunk) onChunk(JSON.parse(raw)); } catch (e) {}
          });
          return pump();
        });
      }
      pump();
    })
    .catch(function(err) {
      if (err.name !== 'AbortError' && onError) onError(err);
    });
  return controller;
};

export {
  getLabMasterList,
  getLabMasterDetail,
  addLabMaster,
  setLabMaster,
  deleteLabMaster,
  getAiPrscStream,
};
