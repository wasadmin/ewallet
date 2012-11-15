package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class EditBankMerchantBean extends PageCodeBase {
	private String bankMerchantId;
	private BankMerchant bankMerchant;
	private List<SelectItem> accountHolderList;
	private List<SelectItem> branchList;
	private String selectedAccountHolder;
	private String selectedBranch;
	private ContactDetails contactDetails;
	
	public String getBankMerchantId() {
		if (bankMerchantId == null) {
			bankMerchantId = (String) super.getRequestParam().get("bankMerchantId");
		}
		return bankMerchantId;
	}
	public void setBankMerchantId(String bankMerchantId) {
		this.bankMerchantId = bankMerchantId;
	}
	public BankMerchant getBankMerchant() {
		if (bankMerchant == null || bankMerchant.getId() == null) {
			if (this.getBankMerchantId() != null) {
				try {
					bankMerchant = super.getMerchantService().findBankMerchantById(bankMerchantId);
					Bank bank = super.getBankService().findBankById(bankMerchant.getBankId());
					selectedAccountHolder = bank.getId();
//					System.out.println("Merchant Account Holder Value is "+selectedAccountHolder);
//					if (bankAccount != null) {
//						if (bankAccount.getBankReferenceId() != null) {
//							
//						}
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
					
			} else {
				bankMerchant = new BankMerchant();
				Merchant m = new Merchant();
				bankMerchant.setMerchant(m);
			}
		}
		return bankMerchant;
	}
	public void setBankMerchant(BankMerchant bankMerchant) {
		this.bankMerchant = bankMerchant;
	}
	
	public List<SelectItem> getAccountHolderList() {
		if (accountHolderList == null || accountHolderList.get(0) == null) {
			accountHolderList = new ArrayList<SelectItem>();
			accountHolderList.add(new SelectItem("none", "--Select--"));
			
			List<Bank> banks = super.getBankService().getBank();
			if (banks != null) {
				for (Bank bank: banks) {
					accountHolderList.add(new SelectItem(bank.getId(), bank.getName()));
				}
			}
		}
		return accountHolderList;
	}
	
	public void setAccountHolderList(List<SelectItem> accountHolderList) {
		this.accountHolderList = accountHolderList;
	}
	
	public List<SelectItem> getBranchList() {
		if (branchList == null || branchList.get(0) == null) {
			branchList = new ArrayList<SelectItem>();
			branchList.add(new SelectItem("none", "--Select--"));
			List<BankBranch> branches = super.getBankService().getBankBranch();
			if (branches != null) {
				for (BankBranch branch: branches) {
					branchList.add(new SelectItem(branch.getId(), branch.getName()));
				}
			}
		}
		return branchList;
	}
	public void setBranchList(List<SelectItem> branchList) {
		this.branchList = branchList;
	}
	public void setSelectedAccountHolder(String selectedAccountHolder) {
		this.selectedAccountHolder = selectedAccountHolder;
	}
	public String getSelectedAccountHolder() {
		return selectedAccountHolder;
	}
	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	public String getSelectedBranch() {
		return selectedBranch;
	}
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	public ContactDetails getContactDetails() {
		if (contactDetails == null || contactDetails.getId() == null) {
			if (bankMerchant != null) { 
				contactDetails = super.getContactDetailsService().findContactDetailsById(bankMerchant.getMerchant().getContactDetailsId());
			} else {
				contactDetails = new ContactDetails();
			}
		}
		return contactDetails;
	}
	public void processBankValueChange(ValueChangeEvent event) {
		String bankId = (String) event.getNewValue();
		branchList = new ArrayList<SelectItem>();
		branchList.add(new SelectItem("none", "--Select--"));
		
		List<BankBranch> branches = super.getBankService().getBankBranchByBank(bankId);
		if (branches != null) {
			for (BankBranch branch: branches) {
				branchList.add(new SelectItem(branch.getId(), branch.getBank().getName() + ": " + branch.getName()));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		if (this.getBankMerchant() != null) {
			if (bankMerchant.getMerchantSuspenseAccount() != null 
					&& !bankMerchant.getMerchantSuspenseAccount().trim().equals("")) {
				
				if ("none".equals(selectedAccountHolder)) {
					super.setErrorMessage("Please select the Account Holder ");
					return "failure";
				}
				
				
				if (BankMerchantStatus.DRAFT.equals(bankMerchant.getStatus())) {
					bankMerchant.setStatus(BankMerchantStatus.AWAITING_APPROVAL);
				}
				
				try {
					bankMerchant.setStatus(BankMerchantStatus.AWAITING_APPROVAL);
					Merchant merchant = super.getMerchantService().editMerchant(bankMerchant.getMerchant(), getJaasUserName());
//					MessageSync.populateAndSync(merchant, MessageAction.UPDATE);			
		//          Setting bank Account data 
					
					BankAccount merchantAccount = super.getBankService().getBankAccountsByAccountHolderIdAndOwnerTypeAndBankAccountType(
							merchant.getId(),OwnerType.MERCHANT,BankAccountType.MERCHANT_SUSPENSE);
					merchantAccount.setAccountName(merchant.getName());
//					merchantAccount.setStatus(BankAccountStatus.AWAITING_APPROVAL);
					merchantAccount.setAccountNumber(bankMerchant.getMerchantSuspenseAccount());
					merchantAccount.setBankReferenceId(selectedAccountHolder);
					
					merchantAccount = super.getBankService().editBankAccount(merchantAccount, getJaasUserName());
					
					bankMerchant = super.getMerchantService().editBankMerchant(bankMerchant, getJaasUserName());
		//			MessageSync.populateAndSync(bankMerchant, MessageAction.UPDATE);
					
					contactDetails = super.getContactDetailsService().editContactDetails(contactDetails, getJaasUserName());
										
					if (bankMerchant == null) {
						super.setErrorMessage("An error occurred. Edit action aborted.");
						return "failure";
					}
					
				
				} catch (Exception e) {
					e.printStackTrace();
					super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
					return "failure";
				}
			} else {
				super.setErrorMessage("WARNING: You left the account number blank.");
				super.getRequestScope().put("bankId", bankMerchant.getBankId());
				super.gotoPage("/admin/viewBank.jspx");
				return "success";
			}
		} else {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bank merchant details edited successfully.");
		super.getRequestScope().put("bankId", bankMerchant.getBankId());
		super.gotoPage("/admin/viewBank.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("bankId", bankMerchant.getBankId());
		super.gotoPage("/admin/viewBank.jspx");
		return "cancel";
	}
	
	public void editMerchant(ActionEvent event) {
		bankMerchantId = (String) event.getComponent().getAttributes().get("bankMerchantId");
	}
	
}
