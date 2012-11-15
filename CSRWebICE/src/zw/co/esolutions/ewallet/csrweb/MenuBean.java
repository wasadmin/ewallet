package zw.co.esolutions.ewallet.csrweb;

import java.io.IOException;

import javax.faces.context.FacesContext;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class MenuBean extends PageCodeBase{
	private boolean renderRegisterCustomer;
	private boolean renderCustomerSearch;
	private boolean renderApproveCustomer;
	private boolean renderApproveMobileProfile;
	private boolean renderApproveBankAccount;
	private boolean renderApproveCustomerMerchant;
//	private boolean renderSearchClass;
//	private boolean renderSearchAgent;
	

	public String csrHome(){
		this.redirect("csr/csrHome.jspx");
		return "csrHome";
	}
	
	public String registerCustomer(){
		this.redirect("csr/registerCustomer.jspx");
		return "registerCustomer";
	}
	
	public String customerSearch(){
		this.redirect("csr/customerSearch.jspx");
		return "customerSearch";
	}
	
	public String approveCustomer(){
		this.redirect("csr/approveCustomer.jspx");
		return "approveCustomer";
	}
	
	public String approveMobileProfile(){
		this.redirect("csr/approveMobileProfile.jspx");
		return "approveMobileProfile";
	}
		
	public String approveBankAccount(){
		this.redirect("csr/accountApproval.jspx");
		return "accountApproval";
	}
	
	public String changePassword(){
		this.redirect("csr/changeProfilePassword.jspx");
		return "changeProfilePassword";
	}
	
	private void redirect(String path){
		try {
			if(path.startsWith("/")){
				path = path.replaceFirst("/", "");
			}
			FacesContext.getCurrentInstance().getExternalContext().redirect("/CSRWebICE/"+path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String approveCustomerMerchant() {
		this.redirect("csr/approveCustomerMerchant.jspx");
		return "approveCustomerMerchant.jspx";
	}

	public boolean isRenderRegisterCustomer() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderRegisterCustomer = ps.canDo(super.getJaasUserName(), "REGISTERCUSTOMER");
		} catch (Exception e) {
			
		}
		return renderRegisterCustomer;
	}

	public void setRenderRegisterCustomer(boolean renderRegisterCustomer) {
		this.renderRegisterCustomer = renderRegisterCustomer;
	}

	public boolean isRenderCustomerSearch() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderCustomerSearch = ps.canDo(super.getJaasUserName(), "CUSTOMERSEARCH");
		} catch (Exception e) {
			
		}
		
		return renderCustomerSearch;
	}

	public void setRenderCustomerSearch(boolean renderCustomerSearch) {
		this.renderCustomerSearch = renderCustomerSearch;
	}

	public boolean isRenderApproveCustomer() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderApproveCustomer = ps.canDo(super.getJaasUserName(), "APPROVECUSTOMER");
		} catch (Exception e) {
			
		}
	
		return renderApproveCustomer;
	}

	public void setRenderApproveCustomer(boolean renderApproveCustomer) {
		
		this.renderApproveCustomer = renderApproveCustomer;
	}

	public boolean isRenderApproveMobileProfile() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderApproveMobileProfile = ps.canDo(super.getJaasUserName(), "APPROVEMOBILEPROFILE");
		} catch (Exception e) {
			
		}
	
		return renderApproveMobileProfile;
	}

	public void setRenderApproveMobileProfile(boolean renderApproveMobileProfile) {
		this.renderApproveMobileProfile = renderApproveMobileProfile;
	}

	public boolean isRenderApproveBankAccount() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderApproveBankAccount = ps.canDo(super.getJaasUserName(), "ACCOUNTAPPROVAL");
		} catch (Exception e) {
			
		}
		return renderApproveBankAccount;
	}

	public void setRenderApproveBankAccount(boolean renderApproveBankAccount) {
		this.renderApproveBankAccount = renderApproveBankAccount;
	}

	public boolean isRenderApproveCustomerMerchant() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderApproveCustomerMerchant = ps.canDo(super.getJaasUserName(), "APPROVECUSTOMERMERCHANT");
		} catch (Exception e) {
			
		}
		
		return renderApproveCustomerMerchant;
	}

	public void setRenderApproveCustomerMerchant(
			boolean renderApproveCustomerMerchant) {
		this.renderApproveCustomerMerchant = renderApproveCustomerMerchant;
	}
	
	public String registerAgentLink(){
		redirect("csr/createAgent.jspx");
		return "success";
	}
	
	public boolean isRenderRegisterAgent(){
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		return profileService.canDo(getJaasUserName(), "createAgent");
	}
	
	public String searchAgentLink(){
		redirect("csr/searchAgent.jspx");
		return "success";
	}
	
	public boolean isRenderSearchAgent(){
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		return profileService.canDo(getJaasUserName(),"AGENTSEARCH");
	}
	
	public String agentApprovalLink(){
		redirect("csr/agentsAwaitingApproval.jspx");
		return "success";
	}
	
	public boolean isRenderAgentApproval(){
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		return profileService.canDo(getJaasUserName(), "agentsAwaitingApproval");
	}
	
	public String agentClassApprovalLink(){
		redirect("csr/agentClassApproval.jspx");
		return "success";
	}
	
	public boolean isRenderAgentClassApproval(){
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		return profileService.canDo(getJaasUserName(), "agentClassApproval");
	}
	
	public String registerClassLink(){
		redirect("csr/createAgentClass.jspx");
		return "success";
	}
	
	public boolean isRenderRegisterClass(){
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		return profileService.canDo(getJaasUserName(), "createAgentClass");
	}
	
	public String searchClassLink(){
		redirect("csr/searchAgentClasses.jspx");
		return "success";
	}
	
	public boolean isRenderSearchClass(){
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		return profileService.canDo(getJaasUserName(), "searchAgentClasses");
	}
}
