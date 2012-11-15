package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;
import java.util.Date;

public class BulkBatchResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long numberOfSuccessFulTxns;
	private long numberOfFailedTxn;
	private long valueOfSuccessFulTxns;
	private long valueofFailedTxns;
	private Date valueDate;
	private BulkBatchRequest request;
	private String narrative;
	private String status;
	private String checkStatus;
	
	
	

	public BulkBatchResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public BulkBatchRequest getRequest() {
		return request;
	}

	public void setRequest(BulkBatchRequest request) {
		this.request = request;
	}

	public long getNumberOfSuccessFulTxns() {
		return numberOfSuccessFulTxns;
	}

	public void setNumberOfSuccessFulTxns(long numberOfSuccessFulTxns) {
		this.numberOfSuccessFulTxns = numberOfSuccessFulTxns;
	}

	public long getNumberOfFailedTxn() {
		return numberOfFailedTxn;
	}

	public void setNumberOfFailedTxn(long numberOfFailedTxn) {
		this.numberOfFailedTxn = numberOfFailedTxn;
	}

	public long getValueOfSuccessFulTxns() {
		return valueOfSuccessFulTxns;
	}

	public void setValueOfSuccessFulTxns(long valueOfSuccessFulTxns) {
		this.valueOfSuccessFulTxns = valueOfSuccessFulTxns;
	}

	public long getValueofFailedTxns() {
		return valueofFailedTxns;
	}

	public void setValueofFailedTxns(long valueofFailedTxns) {
		this.valueofFailedTxns = valueofFailedTxns;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

}
