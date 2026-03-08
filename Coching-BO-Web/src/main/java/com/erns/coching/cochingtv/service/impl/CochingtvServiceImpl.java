package com.erns.coching.cochingtv.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.erns.coching.cochingtv.domain.CochingtvSearchDTO;
import com.erns.coching.cochingtv.domain.CochingtvVO;
import com.erns.coching.cochingtv.mapper.CochigtvDAO;
import com.erns.coching.cochingtv.service.CochingtvService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

/**
 *
 * <p>코칭TV Service</p>
 *
 * @author Kyungmin Lee
 *
 */
@Service
@Transactional
public class CochingtvServiceImpl implements CochingtvService {

	@Autowired
	private CochigtvDAO dao;
	
	@Getter
	@Value("${youtube.api.key}")
	private String youtubeKey;

	@Override
	public List<Map<String, Object>> getList(CochingtvSearchDTO param) {
		return dao.getList(param);
	}

	@Override
	public int getListCount(CochingtvSearchDTO param) {
		return dao.getListCount(param);
	}

	@Override
	public Map<String, Object> load(CochingtvSearchDTO param) {
		return dao.load(param);
	}
	
	@Override
	public Map<String, Object> insert(CochingtvVO cochingtvVO)
			throws IllegalStateException, IOException {
		if(dao.insert(cochingtvVO) <= 0) {
			return null;
		}

		long tvSeq = cochingtvVO.getTvSeq();

		//순번 정렬
		dao.updateOrderWhenInsert();

		CochingtvSearchDTO cochingtvScDTO = new CochingtvSearchDTO();
		cochingtvScDTO.setTvSeq(tvSeq);

		Map<String, Object> retMap = dao.load(cochingtvScDTO);
		return retMap;
	}
	
	@Override
	public int updateState(Collection<CochingtvVO> list) {
		int retCnt = 0;

		for(CochingtvVO param : list){
			retCnt += dao.updateState(param);
		}

		return retCnt;
	}
	
	@Override
	public int updateDelYn(Collection<CochingtvVO> list) {
		int retCnt = 0;

		for(CochingtvVO param : list){
			retCnt += dao.updateDelYn(param);
		}

		return retCnt;
	}
	
	@Override
	public int updateOrder(Collection<CochingtvVO> list) {
		int retCnt = 0;

		for(CochingtvVO param : list){
			retCnt += dao.updateOrder(param);
		}

		return retCnt;
	}
	
	/* Youtube API 호출 */
	@Override
	public Map<String, Object> getYoutubeInfo(CochingtvSearchDTO param) {
		String ytUrl = param.getYtUrl();
		Map<String, Object> ytInfo = new HashMap<>();
		if (ytUrl != null && !ytUrl.trim().isEmpty()) {
			try {
				ytInfo = getYoutubeVideoInfo(extractVideoIdFromUrl(ytUrl));
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return ytInfo;
	}
	
	/**
	 * 유튜브 URL에서 영상 ID를 추출하는 메서드
	 * 지원하는 URL 형식:
	 * - https://youtu.be/Lq29T6FaUOs
	 * - https://youtu.be/Lq29T6FaUOs?si=1CiHTPdRsG80ki0m
	 * - https://www.youtube.com/watch?v=Lq29T6FaUOs
	 * - https://www.youtube.com/watch?v=Lq29T6FaUOs&feature=youtu.be
	 * - https://m.youtube.com/watch?v=Lq29T6FaUOs
	 * - https://youtube.com/watch?v=Lq29T6FaUOs
	 * @param youtubeUrl 유튜브 URL
	 * @return 영상 ID (추출 실패 시 null)
	 */
	public String extractVideoIdFromUrl(String youtubeUrl) {
		if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
			return null;
		}
		
		// 정규식 패턴들
		// 1. youtu.be 형식: https://youtu.be/VIDEO_ID
		Pattern youtuBePattern = Pattern.compile("(?:youtu\\.be/|youtube\\.com/embed/)([a-zA-Z0-9_-]{11})");
		
		// 2. youtube.com/watch 형식: https://www.youtube.com/watch?v=VIDEO_ID
		Pattern youtubeWatchPattern = Pattern.compile("(?:youtube\\.com/watch\\?v=|youtube\\.com/embed/|youtube\\.com/v/)([a-zA-Z0-9_-]{11})");
		
		// 3. youtube.com/shorts 형식: https://www.youtube.com/shorts/VIDEO_ID
		Pattern youtubeShortsPattern = Pattern.compile("youtube\\.com/shorts/([a-zA-Z0-9_-]{11})");
		
		Matcher matcher;
		
		// youtu.be 형식 체크
		matcher = youtuBePattern.matcher(youtubeUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		
		// youtube.com/watch 형식 체크
		matcher = youtubeWatchPattern.matcher(youtubeUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		
		// youtube.com/shorts 형식 체크
		matcher = youtubeShortsPattern.matcher(youtubeUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		
		// 영상 ID만 입력된 경우 (11자리 영숫자와 하이픈, 언더스코어)
		Pattern videoIdOnlyPattern = Pattern.compile("^([a-zA-Z0-9_-]{11})$");
		matcher = videoIdOnlyPattern.matcher(youtubeUrl.trim());
		if (matcher.matches()) {
			return matcher.group(1);
		}
		
		return null;
	}
	
	/**
	 * 유튜브 API를 호출하여 영상 정보를 가져오는 메서드
	 * @param videoId 유튜브 영상 ID
	 * @return 영상 정보가 담긴 Map<String, String>
	 * @throws Exception API 호출 실패 시
	 */
	public Map<String, Object> getYoutubeVideoInfo(String videoId) throws Exception {
		String url = "https://www.googleapis.com/youtube/v3/videos"
				+ "?id=" + videoId
				+ "&key=" + youtubeKey
				+ "&part=snippet,contentDetails,statistics,status";
		
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(url, String.class);
		
		// Jackson 파싱
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response);
		
		// 영상 정보가 있는지 확인
		if (root.path("items").size() > 0) {
			JsonNode video = root.path("items").get(0);
			JsonNode snippet = video.path("snippet");
			JsonNode statistics = video.path("statistics");
			JsonNode contentDetails = video.path("contentDetails");
			JsonNode status = video.path("status");
			
			Map<String, Object> videoInfo = new HashMap<>();
			
			// 기본 정보
			videoInfo.put("videoId", videoId);
			
			// Snippet 정보
			videoInfo.put("title", snippet.path("title").asText());
			videoInfo.put("description", snippet.path("description").asText());
			videoInfo.put("channelTitle", snippet.path("channelTitle").asText());
			videoInfo.put("publishedAt", snippet.path("publishedAt").asText());
			videoInfo.put("thumbnails", snippet.path("thumbnails").path("default").path("url").asText());
			
			// 통계 정보
			videoInfo.put("viewCount", statistics.path("viewCount").asText());
			videoInfo.put("likeCount", statistics.path("likeCount").asText());
			videoInfo.put("dislikeCount", statistics.path("dislikeCount").asText());
			videoInfo.put("commentCount", statistics.path("commentCount").asText());
			
			// 콘텐츠 정보
			videoInfo.put("duration", contentDetails.path("duration").asText());
			
			// 상태 정보
			videoInfo.put("privacyStatus", status.path("privacyStatus").asText());
			videoInfo.put("uploadStatus", status.path("uploadStatus").asText());
			
			return videoInfo;
		} else {
			return null;
		}
	}

}
