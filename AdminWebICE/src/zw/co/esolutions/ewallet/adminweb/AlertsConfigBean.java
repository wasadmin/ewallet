package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;

import zw.co.esolutions.ewallet.alertsservices.service.TransactionType;
import zw.co.esolutions.ewallet.alertsservices.service.TransactionTypeStatus;

public class AlertsConfigBean extends PageCodeBase{
	
	private List<TransactionType> transactionTypeList;

	public List<TransactionType> getTransactionTypeList() {
		try {
			transactionTypeList = super.getAlertsService().getAllTransactionTypes();
			if(transactionTypeList==null || transactionTypeList.isEmpty()){
				super.setInformationMessage("No transaction types where found");
				transactionTypeList=new ArrayList<TransactionType>();
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			transactionTypeList=new ArrayList<TransactionType>();
		}
		return transactionTypeList;
	}

	public void setTransactionTypeList(List<TransactionType> transactionTypeList) {
		this.transactionTypeList = transactionTypeList;
	}
	
	public String updateTransactionType(){
		try{
			String transactionTypeId = (String)super.getRequestParam().get("transactionTypeId");
			TransactionType transactionType = super.getAlertsService().findTransactionType(transactionTypeId);
			if(transactionType.getStatus().equals(TransactionTypeStatus.ENABLED)){
				super.getAlertsService().disableTransactionType(transactionType, super.getJaasUserName());
			}else if(transactionType.getStatus().equals(TransactionTypeStatus.DISABLED)){
				super.getAlertsService().enableTransactionType(transactionType, super.getJaasUserName());
			}
			transactionTypeList = null;
			super.setInformationMessage("Transaction Type Updated");
		}catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		return "update";
	}
	
}
