package zw.co.esolutions.ewallet.adminweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.alertsservices.service.TransactionType;
import zw.co.esolutions.ewallet.alertsservices.service.TransactionTypeStatus;

public class CreateTransactionTypeBean extends PageCodeBase{

	private TransactionType transactionType = new TransactionType();

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
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
			transactionType.setStatus(TransactionTypeStatus.AWAITING_APPROVAL);
			transactionType = super.getAlertsService().createTransactionType(transactionType, super.getJaasUserName());
			super.setInformationMessage("Transaction Type Created Successfully");
			super.getRequestScope().put("transactionTypeId", transactionType.getId());
			super.gotoPage("admin/viewTransactionType.jspx");
			transactionType = new TransactionType();
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		return "submit";
	}
	
	public String cancel(){
		super.gotoPage("admin/adminHome.jspx");
		transactionType = new TransactionType();
		return "cancel";
	}
}
