package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class EditBankBranchBean extends PageCodeBase {
	
	
	private List<SelectItem> bankList;
	private String selectedBank;
	private String branchId;
	private BankBranch branch;
	private ContactDetails contactDetails;
		
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
	public String getBranchId() {
		return branchId;
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
		if (branch == null || branch.getId()== null) {
			if (this.getBranchId() != null) {
				try {
					branch = super.getBankService().findBankBranchById(branchId);
				} catch (Exception e) {
					e.printStackTrace();
					super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				}
			} else {
				branch = new BankBranch();
			}
		}
		
		return branch;
	}
	
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	
	public ContactDetails getContactDetails() {
		if (contactDetails == null) {
			if (this.getBranchId() != null && getBranch().getContactDetailsId()!=null) {
				try {
					contactDetails = super.getContactDetailsService().findContactDetailsById(getBranch().getContactDetailsId());
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			} 
		}
		
		if(contactDetails==null) {
			contactDetails = new ContactDetails();
		}
		return contactDetails;
	}
	
	public void editBranch(ActionEvent event) {
		branchId = (String) event.getComponent().getAttributes().get("branchId");
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		
		try {
			if(contactDetails.getId()==null){
				contactDetails = super.getContactDetailsService().createContactDetails(getContactDetails(), super.getJaasUserName());
			}else{
				contactDetails = super.getContactDetailsService().editContactDetails(getContactDetails(),super.getJaasUserName());
			}
			if (contactDetails.getId() != null) {
				branch.setContactDetailsId(contactDetails.getId());
				//Bank bank = super.getBankService().findBankById(selectedBank);
				//branch.setBank(bank);
				branch = super.getBankService().editBankBranch(branch, super.getJaasUserName());
				MessageSync.populateAndSync(branch, MessageAction.UPDATE);
			}
							
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.getRequestScope().put("branchId", branch.getId());
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("branchId", this.getBranchId());
		return "cancel";
	}
}
