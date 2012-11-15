package zw.co.esolutions.ewallet.adminweb;

import java.io.IOException;

import javax.faces.context.FacesContext;

import pagecode.PageCodeBase;

public class MenuBean extends PageCodeBase{
	
	public String adminHome(){
		this.redirect("admin/adminHome.jspx");
		return "adminHome";
	}
	
	public boolean isRenderAdminHome(){
		return super.getProfileService().canDo(getJaasUserName(), "adminHome");
	}
	
	public String changeProfilePassword(){
		this.redirect("admin/changeProfilePassword.jspx");
		return "changeProfilePassword";
	}
	
	public boolean isRenderChangeProfilePassword(){
		return super.getProfileService().canDo(getJaasUserName(), "changeProfilePassword");
	}
	
	public String createBank(){
		this.redirect("admin/createBank.jspx");
		return "createBank";
	}
	
	public boolean isRenderCreateBank(){
		return super.getProfileService().canDo(getJaasUserName(), "createBank");
	}
	
	public String downloadMerchantsAndBanks(){
		this.redirect("admin/downloadMerchantsAndBanks.jspx");
		return "downloadMerchantsAndBanks";
	}
	
	public boolean isRenderDownloadMerchantsAndBanks(){
		return super.getProfileService().canDo(getJaasUserName(), "downloadMerchantsAndBanks");
	}
	
	public String searchBank(){
		this.redirect("admin/searchBank.jspx");
		return "searchBank";
	}
	
	public boolean isRenderSearchBank(){
		return super.getProfileService().canDo(getJaasUserName(), "searchBank");
	}
	
	public String searchBankBranch(){
		this.redirect("admin/searchBankBranch.jspx");
		return "searchBank";
	}
	
	public boolean isRenderSearchBankBranch(){
		return super.getProfileService().canDo(getJaasUserName(), "searchBankBranch");
	}
		
	public String searchMerchant(){
		this.redirect("/admin/searchMerchant.jspx");
		return "searchMerchant";
	}
	
	public boolean isRenderSearchMerchant(){
		return super.getProfileService().canDo(getJaasUserName(), "searchMerchant");
	}
	
	public String createBankBranch(){
		this.redirect("admin/createBankBranch.jspx");
		return "createBankBranch";
	}
	
	public boolean isRenderCreateBankBranch(){
		return super.getProfileService().canDo(getJaasUserName(), "createBankBranch");
	}
	
	public String approveBankBranch(){
		this.redirect("admin/approveBankBranch.jspx");
		return "approveBankBranch";
	}
	
	public boolean isRenderApproveBankBranch(){
		return super.getProfileService().canDo(getJaasUserName(), "approveBankBranch");
	}
	
	public String createBankAccount(){
		this.redirect("admin/createBankAccount.jspx");
		return "createBankAccount";
	}
	
	public boolean isRenderCreateBankAccount(){
		return super.getProfileService().canDo(getJaasUserName(), "createBankAccount");
	}
	
	public String searchBankAccount(){
		this.redirect("admin/searchBankAccount.jspx");
		return "searchBankAccount";
	}
	
	public boolean isRenderSearchBankAccount(){
		return super.getProfileService().canDo(getJaasUserName(), "searchBankAccount");
	}
	
	public String approveBankAccount(){
		this.redirect("admin/approveBankAccount.jspx");
		return "approveBankAccount";
	}
	
	public boolean isRenderApproveBankAccount(){
		return super.getProfileService().canDo(getJaasUserName(), "approveBankAccount");
	}
	
	public String createProfile(){
		this.redirect("admin/createProfile.jspx");
		return "createProfile";
	}
	
	public boolean isRenderCreateProfile(){
		return super.getProfileService().canDo(getJaasUserName(), "createProfile");
	}
	
	public String createRole(){
		this.redirect("admin/createRole.jspx");
		return "createRole";
	}
	
	public boolean isRenderCreateRole(){
		return super.getProfileService().canDo(getJaasUserName(), "createRole");
	}
	
	public String searchProfile(){
		this.redirect("admin/searchProfile.jspx");
		return "searchProfile";
	}
	
	public boolean isRenderSearchProfile(){
		return super.getProfileService().canDo(getJaasUserName(), "searchProfile");
	}
	
	public String viewAllRoles(){
		this.redirect("admin/viewAllRoles.jspx");
		return "viewAllRoles";
	}
	
	public boolean isRenderViewAllRoles(){
		return super.getProfileService().canDo(getJaasUserName(), "viewAllRoles");
	}
	
	public String createAccessRight(){
		this.redirect("admin/createAccessRight.jspx");
		return "createAccessRight";
	}
	
	public boolean isRenderCreateAccessRight(){
		return super.getProfileService().canDo(getJaasUserName(), "createAccessRight");
	}
	
	public String searchAccessRight(){
		this.redirect("admin/searchAccessRight.jspx");
		return "createAccessRight";
	}
	
	public boolean isRenderSearchAccessRight(){
		return super.getProfileService().canDo(getJaasUserName(), "searchAccessRight");
	}
	
	public String approveProfile(){
		this.redirect("admin/approveProfile.jspx");
		 return "approveProfile";
	}
	
	public boolean isRenderApproveProfile(){
		return super.getProfileService().canDo(getJaasUserName(), "approveProfile");
	}
	
	public String approveRoles(){
		this.redirect("admin/approveRoles.jspx");
		return "approveRoles";
	}
	
	public boolean isRenderApproveRoles(){
		return super.getProfileService().canDo(getJaasUserName(), "approveRoles");
	}
	
	public String createCommissionTable(){
		this.redirect("admin/createCommissionTable.jspx");
		return "createCommissionTable";
	}
	
	public boolean isRenderCreateCommissionTable(){
		return super.getProfileService().canDo(getJaasUserName(), "createCommissionTable");
	}
	
	public String approveCommission(){
		this.redirect("admin/approveCommission.jspx");
		return "approveCommission";
	}
	
	public boolean isRenderApproveCommission(){
		return super.getProfileService().canDo(getJaasUserName(), "approveCommission");
	}
	
	public String viewAllCommissionTables(){
		this.redirect("admin/viewAllCommissionTables.jspx");
		return "viewAllCommissionTables";
	}
	
	public boolean isRenderViewAllCommissionTables(){
		return super.getProfileService().canDo(getJaasUserName(), "viewAllCommissionTables");
	}
	
	public String createLimit(){
		this.redirect("admin/createLimit.jspx");
		return "createLimit";
	}
	
	public boolean isRenderCreateLimit(){
		return super.getProfileService().canDo(getJaasUserName(), "createLimit");
	}
	
	public String viewAllLimits(){
		this.redirect("admin/viewAllLimits.jspx");
		return "viewAllLimits";
	}
	
	public boolean isRenderViewAllLimits(){
		return super.getProfileService().canDo(getJaasUserName(), "viewAllLimits");
	}
	
	public String approveLimit(){
		this.redirect("admin/approveLimit.jspx");
		return "approveLimit";
	}
	
	public boolean isRenderApproveLimit(){
		return super.getProfileService().canDo(getJaasUserName(), "approveLimit");
	}
	
	public String createReferralConfig(){
		this.redirect("admin/createReferralConfig.jspx");
		return "createReferralConfig";
	}
	
	public boolean isRenderCreateReferralConfig(){
		return super.getProfileService().canDo(getJaasUserName(), "createReferralConfig");
	}
	
	public String searchReferralConfig(){
		this.redirect("admin/searchReferralConfig.jspx");
		return "searchReferralConfig";
	}
	
	public boolean isRenderSearchReferralConfig(){
		return super.getProfileService().canDo(getJaasUserName(), "searchReferralConfig");
	}
	
	public String activityConfig(){
		this.redirect("admin/activityConfig.jspx");
		return "activityConfig";
	}
	
	public boolean isRenderActivityConfig(){
		return super.getProfileService().canDo(getJaasUserName(), "activityConfig");
	}
	
	public String searchLogs(){
		this.redirect("admin/searchLogs.jspx");
		return "searchLogs";
	}
	
	public boolean isRenderSearchLogs(){
		return super.getProfileService().canDo(getJaasUserName(), "searchLogs");
	}
	
	public String createTransactionType(){
		this.redirect("admin/createTransactionType.jspx");
		return "createTransactionType";
	}
	
	public boolean isRenderCreateTransactionType(){
		return super.getProfileService().canDo(getJaasUserName(), "createTransactionType");
	}
	
	public String approveTransactionType(){
		this.redirect("admin/approveTransactionType.jspx");
		return "approveTransactionType";
	}
	
	public boolean isRenderApproveTransactionType(){
		return super.getProfileService().canDo(getJaasUserName(), "approveTransactionType");
	}
	
	public String globalAlertsConfig(){
		this.redirect("admin/globalAlertsConfig.jspx");
		return "globalAlertsConfig";
	}
	
	public boolean isRenderGlobalAlertsConfig(){
		return super.getProfileService().canDo(getJaasUserName(), "globalAlertsConfig");
	}
	
	private void redirect(String path){
		try {
			if(path.startsWith("/")){
				path = path.replaceFirst("/", "");
			}
			FacesContext.getCurrentInstance().getExternalContext().redirect("/AdminWebICE/"+path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String createBankMerchant() {
		this.redirect("admin/createBankMerchant.jspx");
		return "createBankMerchant";
	}
	
	public boolean isRenderCreateBankMerchant() {
		return super.getProfileService().canDo(getJaasUserName(), "createBankMerchant");
	}
	
	public String searchBankMerchant() {
		this.redirect("admin/searchBankMerchant.jspx");
		return "searchBankMerchant";
	}
	
	public boolean isRenderSearchBankMerchant() {
		return super.getProfileService().canDo(getJaasUserName(), "searchBankMerchant");
	}
	
	public String approveBankMerchant() {
		this.redirect("admin/approveBankMerchant.jspx");
		return "approveBankMerchant";
	}
	
	public boolean isRenderApproveBankMerchant() {
		return super.getProfileService().canDo(getJaasUserName(), "approveBankMerchant");
	}
	
	public boolean isRenderScheduleAccountBalanceRun() {
		return super.getProfileService().canDo(getJaasUserName(), "scheduleAccountBalanceRun");
	}
	
	public String scheduleAccountBalanceRun(){
		this.redirect("admin/scheduleAccountBalanceRun.jspx");
		return "scheduleAccountBalanceRun";
	}
	
	public String scheduleCollectionRun(){
		this.redirect("admin/scheduleCollectionRun.jspx");
		return "scheduleCollectionRun";
	}
	
	public boolean isRenderScheduleCollectionRun() {
		return super.getProfileService().canDo(getJaasUserName(), "scheduleCollectionRun");
	}
	
	public String scheduleTxnReversalRun(){
		this.redirect("admin/scheduleTransactionReversal.jspx");
		return "scheduleTransactionReversal";
	}
	
	public boolean isRenderTxnReversalRun() {
		return super.getProfileService().canDo(getJaasUserName(), "scheduleTransactionReversal");
	}
	
	public String scheduleCommissionSweep(){
		this.redirect("admin/scheduleCommissionSweep.jspx");
		return "scheduleCommissionSweep";
	}
	
	public boolean isRenderCommissionSweep() {
		return super.getProfileService().canDo(getJaasUserName(), "scheduleCommissionSweep");
	}
	
	public String searchLoggedOnUsers(){
		this.redirect("admin/searchLoggedOnUsers.jspx");
		return "searchLoggedOnUsers";
	}
	
	public boolean isRenderSearchLoggedOnUsers() {
		return super.getProfileService().canDo(getJaasUserName(), "searchLoggedOnUsers");
	}
	
	public String createBulletin(){
		this.redirect("admin/createBulletin.jspx");
		return "createBulletin";
	}
	
	public boolean isRenderCreateBulletin() {
		return super.getProfileService().canDo(getJaasUserName(), "createBulletin");
	}
	
	public String searchBulletin(){
		this.redirect("admin/searchBulletin.jspx");
		return "searchBulletin";
	}
	
	public boolean isRenderSearchBulletin() {
		return super.getProfileService().canDo(getJaasUserName(), "searchBulletin");
	}
	
	public String bulletinAwaitingApproval(){
		this.redirect("admin/bulletinAwaitingApproval.jspx");
		return "bulletinAwaitingApproval";
	}
	
	public boolean isRenderBulletinAwaitingApproval() {
		return super.getProfileService().canDo(getJaasUserName(), "bulletinAwaitingApproval");
	}
}
