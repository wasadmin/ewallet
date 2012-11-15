package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.alertsservices.service.AlertOption;
import zw.co.esolutions.ewallet.alertsservices.service.AlertOptionStatus;
import zw.co.esolutions.ewallet.alertsservices.service.TransactionType;
import zw.co.esolutions.ewallet.alertsservices.service.TransactionTypeStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;

public class ApproveTransactionTypeBean extends PageCodeBase{

	private List<TransactionType> transactionTypeList;

	public List<TransactionType> getTransactionTypeList() {
		if(transactionTypeList==null){
			try {
				transactionTypeList = super.getAlertsService().getTransactionTypeByStatus(TransactionTypeStatus.AWAITING_APPROVAL);
				if(transactionTypeList==null || transactionTypeList.isEmpty()){
					super.setInformationMessage("There are no Transaction Types Awaiting Approval");
					transactionTypeList= new ArrayList<TransactionType>();
				}
			} catch (Exception e) {
				e.printStackTrace();
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				transactionTypeList=new ArrayList<TransactionType>();
			}
		}
		return transactionTypeList;
	}

	public void setTransactionTypeList(List<TransactionType> transactionTypeList) {
		this.transactionTypeList = transactionTypeList;
	}
	
	public String approve(){
		TransactionType transactionType =null;
		try {
			String transactionTypeId = (String)super.getRequestParam().get("transactionTypeId");
			transactionType = super.getAlertsService().findTransactionType(transactionTypeId);
			transactionType = super.getAlertsService().approveTransactionType(transactionType, super.getJaasUserName());
			
			//Refresh list
			transactionTypeList = null;
			//Add alert options to every account
			List<BankAccount> bankAccountList = super.getBankService().getBankAccount();
			for(BankAccount bankAccount:bankAccountList){
				if(OwnerType.CUSTOMER.equals(bankAccount.getOwnerType())){
					List<MobileProfile> mobileProfiles = super.getCustomerService().getMobileProfileByCustomer(bankAccount.getAccountHolderId());
					MobileProfile primaryProfile = null;
					for(MobileProfile p:mobileProfiles){
						if(p.isPrimary()){
							primaryProfile = p;
							break;
						}
					}
					
					if(primaryProfile!=null){
						AlertOption alertOption = new AlertOption();
						alertOption.setBankAccountId(bankAccount.getId());
						alertOption.setMobileProfileId(primaryProfile.getId());
						alertOption.setTransactionType(transactionType);
						alertOption.setStatus(AlertOptionStatus.DISABLED);
						super.getAlertsService().createAlertOption(alertOption, super.getJaasUserName());
					}
				}				
			}
			super.setInformationMessage("Transaction Type Approved Successfully");
		} catch (Exception e) {
			try {
				transactionType.setStatus(TransactionTypeStatus.AWAITING_APPROVAL);
				super.getAlertsService().editTransactionType(transactionType, getJaasUserName());	
				
			} catch (Exception e2) {
				
			}
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		return "approve";
	}
		
}
