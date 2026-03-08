package com.erns.coching.schedule.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erns.coching.common.file.domain.FileSetDTO;
import com.erns.coching.common.file.domain.FileVO;
import com.erns.coching.common.file.repository.FileRepository;
import com.erns.coching.common.file.service.FileService;
import com.erns.coching.common.file.service.PdfInfoService;
import com.erns.coching.mail.service.MailService;
import com.erns.coching.member.service.MemberService;
import com.erns.coching.mltln.service.MltlnService;
import com.erns.coching.schedule.service.BatchService;
import com.erns.coching.search.service.CochingEsRawService;
import com.erns.coching.search.service.CochingEsTvService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * <p>
 * 배치 Service
 * </p>
 *
 * @author Hunwoo Park
 *
 */
@Slf4j
@Service
@Transactional
public class BatchServiceImpl implements BatchService {

	private int maxBatchCount = 100;

	@Value("${batch.generate-locale-json:false}")
	private boolean isGenerateLocaleJson;

	@Value("${batch.reset-coching-raw-index:false}")
	private boolean resetCochingRawIndex;

	@Value("${batch.reset-coching-tv-index:false}")
	private boolean resetCochingTvIndex;

	@Value("${batch.send-coching-mail:false}")
	private boolean resetCochingMail;

	@Value("${batch.send-mail-max-cnt:100}")
	private int sendMailMaxCnt;

	@Value("${batch.pdf-info-convert:false}")
	private boolean pdfInfoConvert;

	@Value("${batch.remove-preview-files:false}")
	private boolean removePreviewFiles;

	@Value("${batch.create-profile-img:false}")
	private boolean createProfileImg;

	@Value("${batch.create-profile-max-cnt:100}")
	private int createProfileMaxCnt;

	@Autowired
	private MltlnService mltlnService;

	@Autowired
	private CochingEsRawService cochingEsRawService;

	@Autowired
	private CochingEsTvService cochingEsTvService;

	@Autowired
	private MailService mailService;

	@Autowired
	private PdfInfoService pdfInfoService;

	@Autowired
	private FileService fileService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private FileRepository fileRepo;

	@Override
	// @Scheduled(cron = "0 1 2 * * ?") /* 02:01:00 */
	// @Scheduled(cron = "0 * * * * ?") /* 매 0초 - 테스트용*/
	@Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE) // 부팅후 10초 후
	public void batchGenerateLocaleJson() {
		if (!isGenerateLocaleJson) {
			return;
		}

		log.info("Start... batchGenerateLocaleJson");

		mltlnService.exportMltlnJson();

		log.info("Done... batchGenerateLocaleJson");

	}

	// @Scheduled(cron = "0 0 2 * * ?") /* 매일 새벽 2시 */
	// @Scheduled(cron = "0 * * * * ?") /* 매 0초 - 테스트용*/
	// @Scheduled(cron = "0 */5 * * * ?") /* 5분 마다 */
	@Scheduled(cron = "0 */30 * * * ?") /* 30분 마다 */
	public void batchCochingRawIndexReset() {
		if (!resetCochingRawIndex) {
			return;
		}

		log.info("Start... batchCochingRawIndexReset");

		cochingEsRawService.resetCochingRawIndex();

		log.info("Done... batchCochingRawIndexReset");
	}

	// @Scheduled(cron = "0 0 2 * * ?") /* 매일 새벽 2시 */
	// @Scheduled(cron = "0 * * * * ?") /* 매 0초 - 테스트용*/
	// @Scheduled(cron = "0 */5 * * * ?") /* 5분 마다 */
	@Scheduled(cron = "0 */30 * * * ?") /* 30분 마다 */
	public void batchCochingTvIndexReset() {
		if (!resetCochingTvIndex) {
			return;
		}

		log.info("Start... batchCochingTvIndexReset");

		cochingEsTvService.resetCochingTvIndex();

		log.info("Done... batchCochingTvIndexReset");
	}

	@Scheduled(cron = "0 * * * * ?") /* 매 0초 - 테스트용 */
	public void batchCochingMail() {
		if (!resetCochingMail) {
			return;
		}

		log.info("Start... batchCochingMail");

		mailService.sendMail(sendMailMaxCnt);

		log.info("Done... batchCochingMail");
	}

	@Override
	@Scheduled(cron = "0 */5 * * * ?") /* 5분 마다 */
	public void batchPdfInfoConvert() {
		if (!pdfInfoConvert) {
			return;
		}

		log.info("Start... batchPdfInfoConvert");

		try {
			List<String> fileIdList = pdfInfoService.getBatchConvertList();
			for (String fileId : fileIdList) {
				pdfInfoService.pdfAllProcess(fileId);
			}
		} catch (Exception e) {
			log.error("batchPdfInfoConvert Error", e);
		}

		log.info("Done... batchPdfInfoConvert");

	}

	@Override
	// @Scheduled(cron = "0 */5 * * * ?") /* 5분 마다 */
	@Scheduled(cron = "0 1 2 * * ?") /* 02:01:00 */
	public void batchRemovePreviewFiles() {
		if (!removePreviewFiles) {
			return;
		}

		log.info("Start... batchRemovePreviewFiles");

		try {
			List<FileVO> fileList = fileService.getWillRemovePreviewFiles();
			List<FileSetDTO> fileSetList = fileList.stream()
					.map(fileVo -> {
						return FileSetDTO.builder()
								.fileId(fileVo.getFileId())
								.filePath(fileVo.getFilePath())
								.fileNameDest(fileVo.getFileNameDest())
								.build();
					}).collect(Collectors.toList());

			// 물리파일 삭제
			fileRepo.deleteFiles(fileSetList);

			// DB 삭제
			for (FileVO fileVo : fileList) {
				fileService.delete(fileVo.getFileId());
			}
		} catch (Exception e) {
			log.error("batchRemovePreviewFiles Error", e);
		}

		log.info("Done... batchRemovePreviewFiles");

	}

	@Override
	@Scheduled(cron = "0 */5 * * * ?") /* 5분 마다 */
	public void batchCreateProfileImg() {
		if (!createProfileImg) {
			return;
		}

		log.info("Start... batchCreateProfileImg");

		try {
			memberService.createProfileImg(createProfileMaxCnt);
		} catch (Exception e) {
			log.error("batchCreateProfileImg Error", e);
		}

		log.info("Done... batchCreateProfileImg");

	}
}
