package com.erns.coching.view.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.erns.coching.common.file.domain.ProxyFileVO;
import com.erns.coching.common.file.service.ProxyFileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/common/img")
public class ImageProxyController {

  private static final List<String> ALLOWED_DOMAINS = Arrays.asList(
    "coching.co.kr",
    "erns.co.kr",
    "localhost:8480",
    "localhost:8280",
    "localhost:8380",
    "localhost:9727",
    "localhost:9728",
    "localhost:9527"
  );

  private static final Map<String, String> mimeToExtMap = new HashMap<>();
  static {
      mimeToExtMap.put("image/png", "png");
      mimeToExtMap.put("image/gif", "gif");
      mimeToExtMap.put("image/webp", "webp");
      mimeToExtMap.put("image/jpeg", "jpg");
      mimeToExtMap.put("image/jpg", "jpg");
  }

  @Value("${system.file.proxy-root}")
  protected String proxyFileRootPath;

  @Value("${system.file.proxy-cache-seconds:86400}")
  private long proxyCacheSeconds;

  private final ProxyFileService proxyFileService;
  private Path IMAGE_CACHE_ROOT_DIR;

  @Autowired
  public ImageProxyController(ProxyFileService proxyFileService) {
    this.proxyFileService = proxyFileService;
  }

  @PostConstruct
  public void init() {
    log.info("ImageProxyController is started");
    
    IMAGE_CACHE_ROOT_DIR = Paths.get(proxyFileRootPath);
    log.info("ImageProxyController initialized. proxyFileRootPath = {}", proxyFileRootPath);
  }

  @GetMapping("/pc")
  public ResponseEntity<byte[]> getImage(@RequestParam("url") String imageUrl, HttpServletRequest request) {

    { // Referer 체크

      String referer = Optional.ofNullable(request.getHeader("Referer")).orElse("").toLowerCase();
      String host = "";

      try {
          if (referer.contains("://")) {
              URL refererUrl = new URL(referer);
              host = refererUrl.getHost();
              int port = refererUrl.getPort();
              if (port != -1) {
                  host += ":" + port;
              }
          }
      } catch (Exception e) {
          log.warn("Referer 파싱 실패: {}", referer);
      }

      boolean isAllowNoneHost = false; //로컬 테스트시 
      boolean allowed = ALLOWED_DOMAINS.stream().anyMatch(host::endsWith);

      if (!isAllowNoneHost && host.isEmpty()) {
        log.warn("Referer 없음 — 허용할지 여부 결정 필요");
        return ResponseEntity.status(404)
          .body("File not found.".getBytes(StandardCharsets.UTF_8));
      }

      if (!allowed) {
          log.warn("허용되지 않은 Referer 요청: {}", referer);
          return ResponseEntity.status(404)
              .body("File not found.".getBytes(StandardCharsets.UTF_8));
      }
    }

    try {
      // URL Hash 생성 (확장자는 뒤에서 결정)
      String hashedName = DigestUtils.sha256Hex(imageUrl);
      String subDirName = hashedName.substring(0, 2);
      Path subDir = IMAGE_CACHE_ROOT_DIR.resolve(subDirName);
      Files.createDirectories(subDir);

      // 1. 캐시 파일 찾기
      Optional<Path> existingFile = findCachedFile(subDir, hashedName);
      Path localImagePath;

      if (existingFile.isPresent()) {
        localImagePath = existingFile.get();
      } else {
        // 2. 외부 이미지 다운로드 및 MIME 감지
        URL url = new URL(imageUrl);
        URLConnection conn = url.openConnection();
        String mimeType = conn.getContentType(); // ex) image/png
        String extension = mimeToExtension(mimeType);

        localImagePath = subDir.resolve(hashedName + "." + extension);

        try (InputStream in = conn.getInputStream()) {
          Files.copy(in, localImagePath, StandardCopyOption.REPLACE_EXISTING);
          
          // 3.DB에 기록
          String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1); // logo.png
          ProxyFileVO addFileVO = ProxyFileVO.builder()
              .fileId(hashedName)
              .refCode(url.getHost())
              .orgUrl(imageUrl)

              .fileName(fileName)
              .fileExt(extension)
              .fileNameDest(hashedName + "." + extension)
              .filePath(subDir.toString())
              .fileSize(Files.size(localImagePath))
              .build();
          
          proxyFileService.insert(addFileVO);

        } catch (IOException e) {
          return ResponseEntity
              .status(502)
              .body(("외부 이미지 로드 실패: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
      }

      // 3. 로컬 파일 MIME 재확인 (보조용)
      String finalMime = Files.probeContentType(localImagePath);
      if (finalMime == null)
        finalMime = "application/octet-stream";

      byte[] imageBytes = Files.readAllBytes(localImagePath);

      HttpHeaders headers = new HttpHeaders();
      headers.setCacheControl("public, max-age=" + proxyCacheSeconds);
      headers.setExpires(System.currentTimeMillis() + (proxyCacheSeconds * 1000L));

      return ResponseEntity.ok()
          .headers(headers)
          .contentType(MediaType.parseMediaType(finalMime))
          .body(imageBytes);

    } catch (Exception e) {
      return ResponseEntity
          .status(500)
          .body(("서버 오류: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
    }
  }

  private String mimeToExtension(String mimeType) {
    return mimeToExtMap.getOrDefault(mimeType, "jpg");
  }

  // 캐시 파일 존재 여부 확인
  // [ROOT 기준]/[hash 앞 2자리]/실제파일
  private Optional<Path> findCachedFile(Path subDir, String hashedName) throws IOException {
    if (!Files.exists(subDir)) return Optional.empty();

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(subDir, hashedName + ".*")) {
        for (Path path : stream) {
            return Optional.of(path);
        }
    }
    return Optional.empty();
}
}
