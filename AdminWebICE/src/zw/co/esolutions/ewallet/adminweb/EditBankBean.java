package zw.co.esolutions.ewallet.adminweb;

import javax.faces.event.ActionEvent;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class EditBankBean extends PageCodeBase {
	
	private String bankId;
	private Bank bank;
	private ContactDetails contactDetails;
		
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	
	public String getBankId() {
		return bankId;
	}
	
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
	public Bank getBank() {
		if (bank == null || bank.getId() == null) {
			if (this.getBankId() != null) {
				try {
					bank = super.getBankService().findBankById(bankId);
				} catch (Exception e) {
					e.printStackTrace();
					super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				}
			} else {
				bank = new Bank();
			}
		}
		
		return bank;
	}

	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	
	public ContactDetails getContactDetails() {
		if (contactDetails == null || contactDetails.getId() == null) {
			if (this.getBankId() != null) {
				try {
					contactDetails = super.getContactDetailsService().findContactDetailsById(getBank().getContactDetailsId());
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			} else {
				contactDetails = new ContactDetails();
			}
		} 
		return contactDetails;
	}
	
	public void editBank(ActionEvent event) {
		bankId = (String) event.getComponent().getAttributes().get("bankId");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>bankId :" + bankId);
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		
		try {
			contactDetails = super.getContactDetailsService().updateContactDetails(getContactDetails());
			bank = super.getBankService().editBank(getBank(), super.getJaasUserName());
			MessageSync.populateAndSync(bank, MessageAction.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.getRequestScope().put("bankId", bank.getId());
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("bankId", this.getBankId());
		return "cancel";
	}
	
}
