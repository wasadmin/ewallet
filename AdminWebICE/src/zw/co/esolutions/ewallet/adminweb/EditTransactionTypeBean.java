package zw.co.esolutions.ewallet.adminweb;

import javax.faces.event.ActionEvent;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.alertsservices.service.TransactionType;

public class EditTransactionTypeBean extends PageCodeBase {

	private String transactionTypeId;
	private TransactionType transactionType;
	public String getTransactionTypeId() {
		return transactionTypeId;
	}
	public void setTransactionTypeId(String transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}
	public TransactionType getTransactionType() {
		if(transactionType==null && transactionTypeId!=null){
			transactionType = super.getAlertsService().findTransactionType(transactionTypeId);
		}
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	
	public void editTransactionType(ActionEvent event){
		transactionTypeId = (String)event.getComponent().getAttributes().get("transactionTypeId");
	}
	
	@SuppressWarnings("unchecked")
	public String submit(){
		try {
			if(transactionType.getTransactionCode().equals("") ){
				super.setErrorMessage("Please enter transaction type code");
				return "failure";
			}
			if(transactionType.getDescription().equals("")){
				super.setErrorMessage("Please enter transaction type description");
				return "failure";
			}
			
			transactionType = super.getAlertsService().editTransactionType(transactionType, super.getJaasUserName());
			super.setInformationMessage("Transaction Type Updated Successfully");
			super.getRequestScope().put("transactionTypeId", transactionType.getId());
			super.gotoPage("admin/viewTransactionType.jspx");
			transactionType = null;
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel(){
		super.getRequestScope().put("transactionTypeId", transactionTypeId);
		super.gotoPage("admin/viewTransactionType.jspx");
		transactionType = null;
		return "cancel";
	}
}
