package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;
import java.util.Date;
import java.util.List;



public class BulkBatchRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9218594727900537472L;
	private String batchReference;
	private Date batchDate;
	private String checkStatus;
	private String narrative;
	private String bulkSourceAccount;
	private String sourceAccountName;
	
	
	
	
	
	public BulkBatchRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	private List<BulkBatchItem> batchItems;
	
	
	
	public List<BulkBatchItem> getBatchItems() {
		return batchItems;
	}
	public void setBatchItems(List<BulkBatchItem> batchItems) {
		this.batchItems = batchItems;
	}
	public String getBatchReference() {
		return batchReference;
	}
	public void setBatchReference(String batchReference) {
		this.batchReference = batchReference;
	}
	public Date getBatchDate() {
		return batchDate;
	}
	public void setBatchDate(Date batchDate) {
		this.batchDate = batchDate;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	public String getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}
	public String getBulkSourceAccount() {
		return bulkSourceAccount;
	}
	public void setBulkSourceAccount(String bulkSourceAccount) {
		this.bulkSourceAccount = bulkSourceAccount;
	}
	public String getSourceAccountName() {
		return sourceAccountName;
	}
	public void setSourceAccountName(String sourceAccountName) {
		this.sourceAccountName = sourceAccountName;
	}
	
	
	

}
