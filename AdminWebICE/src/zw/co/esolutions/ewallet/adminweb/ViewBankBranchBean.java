package zw.co.esolutions.ewallet.adminweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;

public class ViewBankBranchBean extends PageCodeBase {
	
	private String branchId;
	private BankBranch branch;
	private ContactDetails contactDetails;
		
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	
	public String getBranchId() {
		if (branchId == null) {
			branchId = (String) super.getRequestScope().get("branchId");
		}
		if (branchId == null) {
			branchId = (String) super.getRequestParam().get("branchId");
		}
		return branchId;
	}
	
	public void setBranch(BankBranch branch) {
		this.branch = branch;
	}
	
	public BankBranch getBranch() {
		if (branch == null && getBranchId() != null) {
			try {
				branch = super.getBankService().findBankBranchById(branchId);
			} catch (Exception e) {
				e.printStackTrace();
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			}
		}
		
		return branch;
	}
	
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	
	public ContactDetails getContactDetails() {
		if (contactDetails == null && this.getBranch() != null) {
			try {
				contactDetails = super.getContactDetailsService().findContactDetailsById(getBranch().getContactDetailsId());
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		} 
		return contactDetails;
	}
	
	public String ok() {
		
		return "ok";
	}
	
	public String edit() {
		contactDetails=null;
		branch = null;
		branchId=null;
		return "edit";
	}
	
	public String logs(){
		super.gotoPage("/admin/viewLogs.jspx");
		return "logs";
	}
	
	public boolean isEditable(){
		if(super.getProfileService().canDo(super.getJaasUserName(), "EDITBANKBRANCH")){
			return true;
		}else{
			return false;
		}
	}
}
