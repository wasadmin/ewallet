package zw.co.esolutions.ewallet.adminweb;

import javax.faces.event.ActionEvent;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;

public class EditMerchantBean extends PageCodeBase{
	private MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
	private ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
	private Merchant merchant;
	private ContactDetails contactDetails;
	private String merchantId;
	
	public EditMerchantBean(){
		super();
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	
	public Merchant getMerchant() {
		if(merchant==null && getMerchantId()!=null){
			try{
				merchant = merchantService.findMerchantById(merchantId);
				System.out.println("Merchant >>"+merchant.getName());
			}catch(Exception e){
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			}
		}
		
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}
	
	public ContactDetails getContactDetails() {
		if(contactDetails==null && getMerchant()!=null){
			try{
				contactDetails = contactDetailsService.findContactDetailsById(getMerchant().getContactDetailsId());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		System.out.println("Contact>>"+contactDetails.getContactName());
		return contactDetails;
	}

	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}

	public void editMerchant(ActionEvent event){
		merchantId = (String) event.getComponent().getAttributes().get("merchantId");		
	}
	
	@SuppressWarnings("unchecked")
	public String submit(){
		try{
			contactDetails = contactDetailsService.editContactDetails(contactDetails,super.getJaasUserName());
			merchant=merchantService.editMerchant(merchant, super.getJaasUserName());
		}catch(Exception e){
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.getRequestScope().put("merchantId",merchant.getId());
		super.setInformationMessage(merchant.getName()+" update successfully.");
		return "success";
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
	
}
