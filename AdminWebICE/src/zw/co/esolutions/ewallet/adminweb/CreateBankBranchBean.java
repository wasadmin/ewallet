package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankBranchStatus;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;
import zw.co.esolutions.ewallet.util.GenerateKey;

public class CreateBankBranchBean extends PageCodeBase {
	
	private String bankId;
	private List<SelectItem> bankList;
	private String selectedBank;
	private BankBranch branch = new BankBranch();
	private ContactDetails contactDetails = new ContactDetails();
	
	
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBankId() {
		if (bankId == null) {
			bankId = (String) super.getRequestParam().get("bankId");
		}
		if (bankId != null) {
			setSelectedBank(bankId);
		}
		return bankId;
	}
	public void setBankList(List<SelectItem> bankList) {
		this.bankList = bankList;
	}
	public List<SelectItem> getBankList() {
		if (bankList == null) {
			bankList = new ArrayList<SelectItem>();
			try {
				List<Bank> bankArray = super.getBankService().getBank();
				if (bankArray != null) {
					for (Bank bank: bankArray) {
						 bankList.add(new SelectItem(bank.getId(), bank.getName()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bankList;
	}
	public void setSelectedBank(String selectedBank) {
		this.selectedBank = selectedBank;
	}
	public String getSelectedBank() {
		return selectedBank;
	}
	public void setBranch(BankBranch branch) {
		this.branch = branch;
	}
	public BankBranch getBranch() {
		return branch;
	}
	
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	public ContactDetails getContactDetails() {
		contactDetails.setCountry("ZIMBABWE");
		return contactDetails;
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		
		try {
			if (selectedBank != null && !selectedBank.equals("")) {
				
				BankBranch b = super.getBankService().getBankBranchByCode(branch.getCode());
				
				if(!(b == null || b.getId()==null)){
					super.setErrorMessage("Bank branch already exists.");
					return "failure";
				}
				
				contactDetails.setId(GenerateKey.generateEntityId());
				branch.setContactDetailsId(contactDetails.getId());
				Bank bank = super.getBankService().findBankById(selectedBank);
				branch.setBank(bank);
				branch.setStatus(BankBranchStatus.AWAITING_APPROVAL);
				branch = super.getBankService().createBankBranch(branch, super.getJaasUserName());
				contactDetails.setOwnerType("BANK BRANCH");
				contactDetails.setOwnerId(branch.getId());
				contactDetails = super.getContactDetailsService().createContactDetails(contactDetails,super.getJaasUserName());
				MessageSync.populateAndSync(branch, MessageAction.CREATE);
				super.setInformationMessage(branch.getName()+" created successfully.");
				
			} else {
				super.setErrorMessage("Please select the bank.");
				return "failure";
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.getRequestScope().put("branchId", branch.getId());
		return "submit";
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
}
