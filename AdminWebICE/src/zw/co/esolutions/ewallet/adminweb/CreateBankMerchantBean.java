package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantStatus;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;
import zw.co.esolutions.ewallet.util.GenerateKey;

public class CreateBankMerchantBean extends PageCodeBase{
	
	private MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
	private ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
	private BankMerchant bankMerchant = new BankMerchant();
	private ContactDetails contactDetails = new ContactDetails();
	private Merchant merchant = new Merchant();
	private List<SelectItem> accountHolderList;
	private List<SelectItem> branchList;
	private String selectedAccountHolder;
	private String selectedBranch;
	
	public CreateBankMerchantBean(){
		super();
	}

	public ContactDetails getContactDetails() {
		return contactDetails;
	}



	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}



	public BankMerchant getBankMerchant() {
		return bankMerchant;
	}

	public void setBankMerchant(BankMerchant bankMerchant) {
		this.bankMerchant = bankMerchant;
	}
	
	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Merchant getMerchant() {
		return merchant;
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
		if (selectedAccountHolder == null) {
			this.getBankMerchant();
		}
		return selectedAccountHolder;
	}
	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	public String getSelectedBranch() {
		return selectedBranch;
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
	public String submit(){
		try{
			
			Merchant c = super.getMerchantService().getMerchantByShortName(merchant.getShortName().toUpperCase());
			if (c != null && c.getId() != null) {
				super.setErrorMessage("Merchant with code " + c.getShortName() + " already registered.");
				return "failure";
			}
			merchant.setId(GenerateKey.generateEntityId());
			contactDetails.setId(GenerateKey.generateEntityId());
			contactDetails.setOwnerType("MERCHANT");
			contactDetails.setOwnerId(merchant.getId());
			
			merchant.setContactDetailsId(contactDetails.getId());
			merchant.setStatus(MerchantStatus.AWAITING_APPROVAL);
						
			merchant = merchantService.createMerchant(merchant, getJaasUserName());
//			MessageSync.populateAndSync(merchant, MessageAction.CREATE);
			
			String branchId = super.getProfileService().getProfileByUserName(super.getJaasUserName()).getBranchId();
			
			bankMerchant.setMerchant(merchant);
			bankMerchant.setStatus(BankMerchantStatus.AWAITING_APPROVAL);
			
			bankMerchant.setBankId(super.getBankService().findBankBranchById(branchId).getBank().getId());
			
			bankMerchant = merchantService.createBankMerchant(bankMerchant, super.getJaasUserName());
//			MessageSync.populateAndSync(bankMerchant, MessageAction.CREATE);
			
			BankAccount merchantAccount = new BankAccount();
			merchantAccount.setAccountClass(BankAccountClass.NONE);
			merchantAccount.setAccountHolderId(merchant.getId());
			merchantAccount.setAccountName(merchant.getName());
			merchantAccount.setBankAccountLastBranch(branchId);
			merchantAccount.setAccountNumber(bankMerchant.getMerchantSuspenseAccount());
			merchantAccount.setApprovable(true);
			merchantAccount.setBranch(super.getBankService().findBankBranchById(branchId));
			merchantAccount.setLevel(BankAccountLevel.CORPORATE);
			merchantAccount.setOwnerType(OwnerType.MERCHANT);
			merchantAccount.setPrimaryAccount(true);
			merchantAccount.setRegistrationBranchId(branchId);
			merchantAccount.setStatus(BankAccountStatus.ACTIVE);
			merchantAccount.setType(BankAccountType.MERCHANT_SUSPENSE);
			merchantAccount.setBankReferenceId(selectedAccountHolder);
				
			super.getBankService().createBankAccount(merchantAccount, super.getJaasUserName());
//			MessageSync.populateAndSync(merchantAccount, MessageAction.CREATE);
			
			try {
				contactDetails = contactDetailsService.createContactDetails(contactDetails,super.getJaasUserName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
								
		}catch(Exception e){
			e.printStackTrace();
			//RollBack
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.getRequestScope().put("bankMerchantId",bankMerchant.getId());
		super.setInformationMessage(bankMerchant.getMerchant().getName()+" created successfully.");
		super.gotoPage("/admin/viewBankMerchant.jspx");

		return "success";
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
	
}
