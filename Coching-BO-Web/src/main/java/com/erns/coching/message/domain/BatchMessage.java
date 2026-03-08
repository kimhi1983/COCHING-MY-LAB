package com.erns.coching.message.domain;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 *
 * <p>배치 Message Super Class</p>
 *
 * @author Hunwoo Park
 *
 */
@ToString()
public class BatchMessage {

	protected int batchSeq;			//BATCH_SEQ

	@Setter
	protected String result="R";  	//RESULT - R:전송대기, S:성공, F:실패

	//@JsonRawValue
	@Setter
	protected String resultData;	//RESULT_DATA

	protected int errorCount = 0;	//ERR_CNT
	protected String sendDate;		//SEND_DATE (예약전송일)
	protected String sentDate;		//SENT_DATE

	protected Date rgtDate;			//RGT_DTTM
	protected Date chngDate;		//CHNG_DTTM

	@JsonRawValue
	public String getResultData() {
		return resultData;
	}
}
