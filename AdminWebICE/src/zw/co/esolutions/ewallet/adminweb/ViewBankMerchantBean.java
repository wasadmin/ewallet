package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import javax.faces.event.ActionEvent;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
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

public class ViewBankMerchantBean extends PageCodeBase{

	private MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
	private ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
	private String bankMerchantId;
	private BankMerchant bankMerchant;
	private ContactDetails contactDetails;
	private String back;
	
	public String getBankMerchantId() {
		if(bankMerchantId==null){
			bankMerchantId=(String)super.getRequestScope().get("bankMerchantId");
		}
		if(bankMerchantId==null){
			bankMerchantId=(String)super.getRequestParam().get("bankMerchantId");
		}
		return bankMerchantId;
	}
	public void setBankMerchantId(String bankMerchantId) {
		this.bankMerchantId = bankMerchantId;
	}
	public BankMerchant getBankMerchant() {
		if(bankMerchant==null && getBankMerchantId()!=null){
			try{
				bankMerchant = merchantService.findBankMerchantById(bankMerchantId);
			}catch(Exception e){
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			}
		}
		return bankMerchant;
	}
	public void setBankMerchant(BankMerchant bankMerchant) {
		this.bankMerchant = bankMerchant;
	}
	public ContactDetails getContactDetails() {
		
		if(contactDetails==null && getBankMerchant()!=null){
			try{
				contactDetails = contactDetailsService.findContactDetailsById(getBankMerchant().getMerchant().getContactDetailsId());
				System.out.println("Contact Details >>>>> " + contactDetails);
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
		return contactDetails;
	}
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	
	public String ok(){
//		if (back != null) {
//			gotoPage(back);
//		} else {
		super.gotoPage("/admin/adminHome.jspx");
//		}
		return "ok";
	}
	
	public String edit(){
		super.gotoPage("/admin/editBankMerchant.jspx");
		return "edit";
	}
	
@SuppressWarnings("unchecked")
public String approveBankMerchant() {
		
//		String bankMerchantId = (String) super.getRequestParam().get("bankMerchantId");
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
		super.getRequestParam().put("bankMerchantId", bankMerchantId);
		gotoPage("/admin/viewBankMerchant.jspx");
		return "approveBankMerchant";
	}
	
	public String approveAllBankMerchants() {
		try {
			List<BankMerchant> bankMerchantList = super.getMerchantService().getBankMerchantByStatus(BankMerchantStatus.AWAITING_APPROVAL);
			if (bankMerchantList != null) {
				for (BankMerchant bankMerchant: bankMerchantList) {
			//		if (BankMerchantStatus.AWAITING_APPROVAL.equals(bankMerchant.getStatus())) {
						this.approveOrReject(bankMerchant, BankMerchantStatus.ACTIVE);
			//		}
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
		MessageSync.populateAndSync(bankMerchant, MessageAction.UPDATE);
		Merchant merchant = super.getMerchantService().findMerchantById(bankMerchant.getMerchant().getId());
		if (BankMerchantStatus.ACTIVE.equals(status)) {
			
			merchant.setStatus(MerchantStatus.ACTIVE);
			super.getMerchantService().editMerchant(merchant, super.getJaasUserName());
			
			BankAccount merchantAccount = super.getBankService().getBankAccountByAccountHolderAndTypeAndOwnerType(bankMerchant.getMerchant().getId(), 
					BankAccountType.MERCHANT_SUSPENSE, OwnerType.MERCHANT, null);
			merchantAccount = super.getBankService().approveBankAccount(merchantAccount, super.getJaasUserName());
			super.getBankService().editBankAccount(merchantAccount, super.getJaasUserName());
			
			MessageSync.populateAndSync(merchant, MessageAction.UPDATE);
		}else{
			merchant.setStatus(MerchantStatus.DISAPPROVED);
			super.getMerchantService().editMerchant(merchant, super.getJaasUserName());
			
			BankAccount merchantAccount = super.getBankService().getBankAccountByAccountHolderAndTypeAndOwnerType(bankMerchant.getMerchant().getId(), 
					BankAccountType.MERCHANT_SUSPENSE, OwnerType.MERCHANT, null);
			merchantAccount = super.getBankService().rejectBankAccount(merchantAccount, super.getJaasUserName());
			super.getBankService().editBankAccount(merchantAccount, super.getJaasUserName());
//			MessageSync.populateAndSync(merchantAccount, MessageAction.UPDATE);
		}
		
//		MessageSync.populateAndSync(merchant, MessageAction.UPDATE);
		
		
		return bankMerchant;
	}
	
	public String rejectBankMerchant() {
//		String bankMerchantId = (String) super.getRequestParam().get("bankMerchantId");
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
		gotoPage("/admin/approveBankMerchant.jspx");
		return "rejectBankMerchant";
	}

	
	public void doApproveAction(ActionEvent event) {
		bankMerchantId = (String) event.getComponent().getAttributes().get("bankMerchantId");
	}
	
	public boolean isApprover() {
		boolean approver = super.getProfileService().canDo(super.getJaasUserName(), "APPROVE");
		return approver;
	}
	
	public boolean isEditable(){
		if(super.getProfileService().canDo(super.getJaasUserName(), "EDITBANKMERCHANT")){
			return true;
		}else{
			return false;
		}
	}
	public void setBack(String back) {
		this.back = back;
	}
	public String getBack() {
		if (back == null) {
			back = (String) super.getRequestParam().get("back");
		}
		return back;
	}
}
