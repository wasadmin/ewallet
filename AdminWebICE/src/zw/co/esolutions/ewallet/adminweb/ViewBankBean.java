package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantStatus;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.RoleAccessRight;

public class ViewBankBean extends PageCodeBase {
	
	private String bankId;
	private Bank bank;
	private ContactDetails contactDetails;
	private List<BankBranch> branchList;
	private List<BankMerchant> bankMerchantList;
	private boolean approver;
	private boolean auditor;
		
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	
	public String getBankId() {
		if (bankId == null) {
			bankId = (String) super.getRequestScope().get("bankId");
		}
		if (bankId == null) {
			bankId = (String) super.getRequestParam().get("bankId");
		}
		return bankId;
	}
	
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
	public Bank getBank() {
		if (bank == null && getBankId() != null) {
			try {
				bank = super.getBankService().findBankById(bankId);
				auditor = this.checkProfileAccessRight("AUDIT");
			} catch (Exception e) {
				e.printStackTrace();
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			}
		}
		
		return bank;
	}
	
	public void setBranchList(List<BankBranch> branchList) {
		this.branchList = branchList;
	}

	public List<BankBranch> getBranchList() {
		if (branchList == null || branchList.isEmpty()) {
			List<BankBranch> branchArray;
			try {
				if (bankId != null) {
					branchArray = super.getBankService().getBankBranchByBank(bankId);
					if (branchArray != null) {
						setBranchList(branchArray);
					} else {
						branchList = new ArrayList<BankBranch>();
					}
				} else {
					branchList = new ArrayList<BankBranch>();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return branchList;
	}

	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	
	public ContactDetails getContactDetails() {
		if (contactDetails == null && this.getBank() != null) {
			try {
				contactDetails = super.getContactDetailsService().findContactDetailsById(getBank().getContactDetailsId());
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		} 
		return contactDetails;
	}
	
	public void setBankMerchantList(List<BankMerchant> bankMerchantList) {
		this.bankMerchantList = bankMerchantList;
	}

	public List<BankMerchant> getBankMerchantList() {
		if (bankMerchantList == null || bankMerchantList.isEmpty()) {
			if (this.getBankId() != null) {
				bankMerchantList = new ArrayList<BankMerchant>();
				try {
					List<Merchant> merchants = super.getMerchantService().getAllMerchants();
					if (merchants != null) {
						for (Merchant merchant: merchants) {
							bankMerchantList.addAll(super.getMerchantService().getBankMerchantByMerchantId(merchant.getId()));
						}
					}
					System.out.println(">>>>>>>>> bankMerchantList > " + bankMerchantList);
				} catch (Exception e) {
					e.printStackTrace();
					super.setErrorMessage("Error retrieving merchant accounts.");
				}
			} else {
				bankMerchantList = new ArrayList<BankMerchant>();
				bankMerchantList.add(new BankMerchant());
			}
		}
		return bankMerchantList;
	}

	public String ok() {
		bank=null;
		bankId=null;
		return "ok";
	}
	
	public String edit() {
		return "edit";
	}
	
	public String viewBranch() {
		return "viewBranch";
	}
	
	public String logs(){
		super.gotoPage("/admin/viewLogs.jspx");
		return "logs";
	}
	
	public String editBankMerchant() {
		super.gotoPage("/admin/editBankMerchant.jspx");
		return "editBankMerchant";
	}
	
	public String approveBankMerchant() {
		
		String bankMerchantId = (String) super.getRequestParam().get("bankMerchantId");
		BankMerchant bankMerchant;
		try {
		
			bankMerchant = super.getMerchantService().findBankMerchantById(bankMerchantId);
			this.approveOrReject(bankMerchant, BankMerchantStatus.ACTIVE);
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bank Merchant " + bankMerchant.getMerchant().getShortName() + " approved successfully.");
		return "approveBankMerchant";
	}
	
	public String approveAllBankMerchants() {
		try {
			List<BankMerchant> bankMerchantList = super.getMerchantService().getBankMerchantByBankId(getBankId());
			if (bankMerchantList != null) {
				for (BankMerchant bankMerchant: bankMerchantList) {
					if (BankMerchantStatus.AWAITING_APPROVAL.equals(bankMerchant.getStatus())) {
						this.approveOrReject(bankMerchant, BankMerchantStatus.ACTIVE);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bank Merchants approved successfully.");
		return "approveAllBankMerchants";
	}
		
	private BankMerchant approveOrReject(BankMerchant bankMerchant, BankMerchantStatus status) throws Exception {
		
		bankMerchant.setStatus(status);
		bankMerchant.setEnabled(true);
		super.getMerchantService().editBankMerchant(bankMerchant, super.getJaasUserName());
//		MessageSync.populateAndSync(bankMerchant, MessageAction.UPDATE);
		
		if (BankMerchantStatus.ACTIVE.equals(status)) {
			Merchant merchant = super.getMerchantService().findMerchantById(bankMerchant.getMerchant().getId());
			merchant.setStatus(MerchantStatus.ACTIVE);
			super.getMerchantService().editMerchant(merchant, super.getJaasUserName());
//			MessageSync.populateAndSync(merchant, MessageAction.UPDATE);
		}
		
		BankAccount merchantAccount = super.getBankService().getBankAccountByAccountHolderAndTypeAndOwnerType(bankMerchant.getId(), BankAccountType.MERCHANT_SUSPENSE, OwnerType.BANK_MERCHANT, null);
		merchantAccount.setStatus(BankAccountStatus.valueOf(status.name()));
		super.getBankService().editBankAccount(merchantAccount, super.getJaasUserName());
//		MessageSync.populateAndSync(merchantAccount, MessageAction.UPDATE);
		
		return bankMerchant;
	}
	
	public String rejectBankMerchant() {
		String bankMerchantId = (String) super.getRequestParam().get("bankMerchantId");
		BankMerchant bankMerchant;
		try {
		
			bankMerchant = super.getMerchantService().findBankMerchantById(bankMerchantId);
			this.approveOrReject(bankMerchant, BankMerchantStatus.DISAPPROVED);
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bank Merchants rejected successfully.");
		return "rejectBankMerchant";
	}

	public void setApprover(boolean approver) {
		this.approver = approver;
	}

	public boolean isApprover() {
		boolean approver = super.getProfileService().canDo(super.getJaasUserName(), "APPROVE");
		return approver;
	}
	
	public boolean isEditable(){
		if(super.getProfileService().canDo(super.getJaasUserName(), "EDITBANK")){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isAuditor() {
		System.out.println("Print boolean value auditor "+this.auditor);
		return auditor;
	}
	
	public void setAuditor(boolean auditor) {
		this.auditor = auditor;
	}
	
	public boolean checkProfileAccessRight(String accessRight) {
		ProfileServiceSOAPProxy proxy= new ProfileServiceSOAPProxy();
		Profile profile = proxy.getProfileByUserName(super.getJaasUserName());
		List<RoleAccessRight> parList = proxy.getRoleAccessRightByRole(profile.getRole().getId());
		for (RoleAccessRight par: parList) {
			//System.out.println(" Action name     "+par.getAccessRight().getActionName());
			if (par.getAccessRight().getActionName().equals(accessRight)) {
				return par.isCanDo();
				
			}
		}
		return false;
	}
	
	public String viewBankMerchant() {
		gotoPage("/admin/viewBankMerchant.jspx");
		return "viewBankMerchant";
	}
	
	public boolean isBankMerchantEditable(){
		if(super.getProfileService().canDo(super.getJaasUserName(), "EDITBANKMERCHANT")){
			return true;
		}else{
			return false;
		}
	}
}
