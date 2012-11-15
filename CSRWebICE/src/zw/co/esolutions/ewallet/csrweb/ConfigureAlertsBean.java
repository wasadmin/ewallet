package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.alertsservices.service.AlertOption;
import zw.co.esolutions.ewallet.alertsservices.service.AlertOptionStatus;
import zw.co.esolutions.ewallet.alertsservices.service.TransactionType;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.process.ProcessResponse;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;

public class ConfigureAlertsBean extends PageCodeBase{

	private List<AlertOption> alertOptionList;
	private String accountId;
	private String previous;
	
	public void configureAlerts(ActionEvent event){
		accountId = (String)event.getComponent().getAttributes().get("accountId");
		this.previous = (String)event.getComponent().getAttributes().get("previous");
	}

	public List<AlertOption> getAlertOptionList() {
		if(accountId!=null){
			List<AlertOption> alerts = super.getAlertsService().getAlertOptionByBankAccountId(accountId);
			alertOptionList = new ArrayList<AlertOption>();
			
			if(alerts == null || alerts.isEmpty()){
				try{
					List<TransactionType> tts = super.getAlertsService().getAllTransactionTypes();
									
					for(TransactionType tt: tts){
						AlertOption ao = new AlertOption();
						ao.setBankAccountId(accountId);
						ao.setTransactionType(tt);
						ao.setStatus(AlertOptionStatus.DISABLED);
						ao.setMobileProfileId(this.getMobileProfileId(accountId));
						ao = super.getAlertsService().createAlertOption(ao, getJaasUserName());
					}
					alerts = super.getAlertsService().getAlertOptionByBankAccountId(accountId);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			for(AlertOption alert:alerts){
				TransactionType type = super.getAlertsService().getTransactionTypeByAlertOption(alert);
				alert.setTransactionType(type);
				alertOptionList.add(alert);
			}
		}
		return alertOptionList;
	}
	
	private String getMobileProfileId(String accId){
		String mobileProfileId = null;
		BankAccount account = super.getBankService().findBankAccountById(accId);
		Customer customer = super.getCustomerService().findCustomerById(account.getAccountHolderId());
		List<MobileProfile> mobileProfiles = super.getCustomerService().getMobileProfileByCustomer(customer.getId());
		for(MobileProfile mp:mobileProfiles){
			if(mp.isPrimary()){
				mobileProfileId = mp.getId();
				break;
			}
		}
		return mobileProfileId;
	}

	public void setAlertOptionList(List<AlertOption> alertOptionList) {
		this.alertOptionList = alertOptionList;
	}

	public String getAccountId() {
		if(accountId==null){
			accountId = (String)super.getRequestParam().get("accountId");
		}
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	@SuppressWarnings("unchecked")
	public String updateAlertOption(){
		
		ProcessServiceSOAPProxy processService = new ProcessServiceSOAPProxy();
		ProcessResponse processResponse = new ProcessResponse();
		
		try {
			String alertOptionId = (String)super.getRequestParam().get("alertOptionId");
			AlertOption alertOption = super.getAlertsService().findAlertOptionById(alertOptionId);
			if(alertOption.getStatus().equals(AlertOptionStatus.ACTIVE)){
				processResponse = processService.processAlertRegistration(alertOption.getBankAccountId(), AlertOptionStatus.DISABLED.name());
				if (ResponseCode.E000.name().equals(processResponse.getResponseCode())) {
					super.getAlertsService().disableAlertOption(alertOption, super.getJaasUserName());
				}
			}else if(alertOption.getStatus().equals(AlertOptionStatus.DISABLED)){
				processResponse = processService.processAlertRegistration(alertOption.getBankAccountId(), AlertOptionStatus.ACTIVE.name());
				if (ResponseCode.E000.name().equals(processResponse.getResponseCode())) {
					super.getAlertsService().enableAlertOption(alertOption, super.getJaasUserName());
				}
			}
						
			alertOptionList = null;
			
			if (ResponseCode.E000.name().equals(processResponse.getResponseCode())) {
				super.setInformationMessage("Alert Option Updated");
			} else {
				super.setErrorMessage(processResponse.getNarrative());
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		super.getRequestParam().put("accountId", accountId);
		return "update";
	}
	
	public String back(){
		System.out.println(this.getPrevious());
		super.gotoPage(this.getPrevious());
		return "back";
	}
	
}
