package zw.co.esolutions.ewallet.adminweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;


public class CreateBankBean extends PageCodeBase {

	private Bank bank = new Bank();
	private ContactDetails contactDetails = new ContactDetails();
	
	public CreateBankBean() {
		super();
	}
	
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	public Bank getBank() {
		return bank;
	}
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	public ContactDetails getContactDetails() {
		return contactDetails;
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		
		try {
			contactDetails = super.getContactDetailsService().createContactDetails(contactDetails,super.getJaasUserName());
			
			if (contactDetails.getId() != null) {
				bank.setContactDetailsId(contactDetails.getId());
				
				bank = super.getBankService().createBank(bank, super.getJaasUserName());
				MessageSync.populateAndSync(bank, MessageAction.CREATE);
				
				
								
			} 
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.getRequestScope().put("bankId", bank.getId());
		super.setInformationMessage(bank.getName() + " created successfully.");
		return "submit";
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
}
