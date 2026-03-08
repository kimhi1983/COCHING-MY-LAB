package com.erns.coching.rnd.domain;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * <p>AI 연구실 결과 VO
 * t_ai_lab_res</p>
 *
 * @author Hunwoo Park
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class AiLabResVO {

	private long aiLabMstSeq;   // ai_lab_mst_seq (PK)
	private long labMstSeq;     // lab_mst_seq (FK)
	private String aiLabParam;  // ai_lab_param (JSONB)
	private String aiLabRes;    // ai_lab_res (JSONB)
	private long rgtr;          // rgtr
	private Date rgtDttm;       // rgt_dttm

	@lombok.Builder(builderClassName = "AddAiLabResBuilder", builderMethodName = "AddAiLabResBuilder")
	public AiLabResVO(long labMstSeq, String aiLabParam, String aiLabRes, long rgtr) {
	    this.aiLabMstSeq = 0L;
	    this.labMstSeq = labMstSeq;
	    this.aiLabParam = aiLabParam;
	    this.aiLabRes = aiLabRes;
	    this.rgtr = rgtr;
	}

	@lombok.Builder(builderClassName = "SetAiLabResBuilder", builderMethodName = "SetAiLabResBuilder")
	public AiLabResVO(long aiLabMstSeq, long labMstSeq, String aiLabParam, String aiLabRes) {
	    this.aiLabMstSeq = aiLabMstSeq;
	    this.labMstSeq = labMstSeq;
	    this.aiLabParam = aiLabParam;
	    this.aiLabRes = aiLabRes;
	}
}

