package zw.co.esolutions.ewallet.adminweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.alertsservices.service.TransactionType;

public class ViewTransactionTypeBean extends PageCodeBase{

	private String transactionTypeId;
	private TransactionType transactionType;
	
	public String getTransactionTypeId() {
		if(transactionTypeId==null){
			transactionTypeId = (String)super.getRequestParam().get("transactionTypeId");
		}
		if(transactionTypeId==null){
			transactionTypeId = (String)super.getRequestScope().get("transactionTypeId");
		}
		return transactionTypeId;
	}
	public void setTransactionTypeId(String transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}
	public TransactionType getTransactionType() {
		if(getTransactionTypeId()!=null){
			transactionType = super.getAlertsService().findTransactionType(transactionTypeId);
		}
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	
	public String edit(){
		transactionType=null;
		transactionTypeId=null;
		super.gotoPage("admin/editTransactionType.jspx");
		return "edit";
	}
	
	public String logs(){
		super.gotoPage("admin/viewLogs.jspx");
		return "logs";
	}
	
}
