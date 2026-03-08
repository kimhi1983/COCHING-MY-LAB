package com.erns.coching.common.file.service.impl;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.erns.coching.common.file.domain.DocExtractTextProcessDTO;
import com.erns.coching.common.file.domain.DocProcessDTO;
import com.erns.coching.common.file.domain.DocProcessDTO.DocProcessDTOBuilder;
import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.domain.FileVO;
import com.erns.coching.common.file.domain.PageImageDTO;
import com.erns.coching.common.file.domain.PdfConvertProcessDTO;
import com.erns.coching.common.file.domain.PdfInfoSearchDTO;
import com.erns.coching.common.file.domain.PdfInfoVO;
import com.erns.coching.common.file.domain.PdfToImageProcessDTO;
import com.erns.coching.common.file.domain.ResultExtractTextDTO;
import com.erns.coching.common.file.domain.ResultPdfAllProcessDTO;
import com.erns.coching.common.file.domain.ResultPdfToImagesDTO;
import com.erns.coching.common.file.mapper.PdfInfoDAO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.file.service.FileService;
import com.erns.coching.common.file.service.PdfInfoService;
import com.erns.coching.common.util.CryptoAESEbcUtil;
import com.erns.coching.search.service.EsSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * PDF 정보 Service
 * </p>
 */
@Slf4j
@Service
@Transactional
public class PdfInfoServiceImpl implements PdfInfoService {

  @Autowired
  private PdfInfoDAO dao;

  @Autowired
	private FileRepository fileRepo;

	@Autowired
	private FileService fileService;

  @Autowired
  private EsSearchService esSearchService;

  @Value("${aes.key.image}")
  private String aesKey;

  @Override
  public List<Map<String, Object>> getList(PdfInfoSearchDTO param) {
    return dao.getList(param);
  }

  @Override
  public int getListCount(PdfInfoSearchDTO param) {
    return dao.getListCount(param);
  }

  @Override
  public Map<String, Object> load(PdfInfoSearchDTO param) {
    return dao.load(param);
  }

  @Override
  public PdfInfoVO loadVo(String fileId) {
    return dao.loadVo(fileId);
  }

  @Override
  public int insert(PdfInfoVO param) {
    return dao.insert(param);
  }

  @Override
  public int delete(String fileId) {
    return dao.delete(fileId);
  }

  @Override
  public List<PageImageDTO> getPageImageList(PdfInfoVO pdfVO) throws Exception {
    List<PageImageDTO> imgList = new ArrayList<>();
    Path subDir = Paths.get(pdfVO.getPageImgPath());

    for (int i = 1; i <= pdfVO.getPageCount(); i++) {
      Path p = subDir.resolve(String.format("%04d.png", i));

      String path = p.toString();
      String enPath = CryptoAESEbcUtil.encrypt(aesKey, path);
      PageImageDTO dto = PageImageDTO.builder()
          .pageNo(i)
          .path(enPath)
          .build();

      imgList.add(dto);
    }

    return imgList;
  }

  @Override
  public ResultPdfAllProcessDTO pdfAllProcess(String fileId) throws JsonProcessingException {

    // 파일 정보 확인
    FileVO fileVo = fileService.loadVo(fileId);
    if (fileVo == null) {
      return null;
    }

    FileSetDTO fileDTO = new FileSetDTO();
    fileDTO.setFilePath(fileVo.getFilePath());
    fileDTO.setFileNameDest(fileVo.getFileNameDest());

    // 파일 정보 확인
    File docFile = fileRepo.getOrgFile(fileDTO);
    if (docFile == null || !docFile.exists()) {
      return null;
    }

    // 변환처리
    DocProcessDTOBuilder paramBuilder = DocProcessDTO.builder();

    Path filePath = docFile.toPath(); // 원본 파일경로
    Path pdfPath = filePath.getParent().resolve(
      replaceExtension(filePath.getFileName().toString(), "pdf")); // PDF 파일경로

    // 필요시 PDF 로 변환
    if (!fileVo.getFileExt().toLowerCase().equals("pdf")) {
      PdfConvertProcessDTO pdfParam = PdfConvertProcessDTO.builder()
          .srcFilePath(filePath.toString())
          .destFilePath(pdfPath.toString())
          .watermarkText("")
          .build();

      paramBuilder.pdfConvert(pdfParam);
    }

    { // 텍스트 추출
      DocExtractTextProcessDTO textParam = DocExtractTextProcessDTO.builder()
          .srcFilePath(pdfPath.toString())
          .method(0)
          .build();
      paramBuilder.docExtractText(textParam);
    }

    { // 이미지로 변환
      Path pdfToImgPath = pdfPath.getParent().resolve(replaceExtension(pdfPath.getFileName().toString(), ""));
      PdfToImageProcessDTO imageParam = PdfToImageProcessDTO.builder()
          .srcFilePath(pdfPath.toString())
          .destPath(pdfToImgPath.toString())
          .dpi(300)
          .build();

      paramBuilder.pdfToImage(imageParam);
    }

    DocProcessDTO docProcessDTO = paramBuilder.build();
    ResultPdfAllProcessDTO result = esSearchService.docPreProcess(docProcessDTO);
    if(result == null) {
      return result;
    }

    //log.debug("result:{}", result);
    
    //DB 적용
    ResultExtractTextDTO retExtractText = result.getExtractText();
    ResultPdfToImagesDTO retPdfToImages = result.getPdfToImages();

    Path rootPath = Paths.get(fileRepo.getUploadPath());
    Path relativePdfPath = rootPath.relativize(pdfPath);
    long fileSize = pdfPath.toFile().length();
    Path pdfImagePath = Paths.get(retPdfToImages.getRequestParam().getDestPath());
    Path relativePdfImagePath = rootPath.relativize(pdfImagePath);

    PdfInfoVO pdfInfoVO = PdfInfoVO.builder()
        .fileId(fileId)
        .fileName(replaceExtension(fileVo.getFileName(), "pdf"))
        .fileExt("pdf")
        .filePath(relativePdfPath.getParent().toString())
        .fileNameDest(relativePdfPath.getFileName().toString())        
        .fileSize(fileSize)
        .pageCount(retPdfToImages.getTotalPage())
        .pageImgPath(relativePdfImagePath.toString())
        .content(retExtractText.getExtractedText())        
        .build();

    delete(fileId);
    insert(pdfInfoVO);

    return result;
  }

  private String replaceExtension(String path, String newExt) {
      int dotIndex = path.lastIndexOf('.');
      if (dotIndex == -1) {
          // 확장자가 없으면 그냥 덧붙임

          if(StringUtils.hasText(newExt)) {
            return path + "." + newExt;
          }          
          return path;
      }

      if(StringUtils.hasText(newExt)) {
        return path.substring(0, dotIndex + 1) + newExt;
      } 

      return path.substring(0, dotIndex);
  }

  @Override
  public List<String> getBatchConvertList() {
    return dao.getBatchConvertList();
  }
}
