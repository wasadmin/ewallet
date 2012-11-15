package zw.co.esolutions.ewallet.tellerweb;

import java.io.IOException;

import javax.faces.context.FacesContext;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class MenuBean extends PageCodeBase {

	private boolean renderTellerHome;
	private boolean renderDayEndSearchs;
	private boolean renderDayEndManualResolve;
	private boolean renderDepositCash;
	private boolean renderViewAllDeposits;
	private boolean renderViewTellerDailyDeposits;
	private boolean renderViewAllWithdrawals;
	private boolean renderSearchNonHolderWithdrawalTxn;
	private boolean renderWithdrawCash;
	private boolean renderSearchBankTransactions;
	private boolean renderSearchBranchTransactions;
	private boolean renderViewAllTransfers;
	private boolean renderDayEndApproval;
	private boolean renderTellerDayEnd;
	private boolean renderSearchAllBanksTransactions;
	private boolean renderViewTellerDailyWithdrawals;
	private boolean renderSearchTellerTransactions;
	private boolean renderTellerStartOfDay;
	private boolean renderStartOfDayApproval;
	private boolean renderTellerNetPosition;
	private boolean renderDepositAgentCash;
	

	public String tellerHome() {
		this.redirect("teller/tellerHome.jspx");
		return "";
	}

	public String depositCash() {
		this.redirect("teller/depositCash.jspx");
		return "";
	}

	public String viewAllDeposits() {
		this.redirect("teller/viewAllDeposits.jspx");
		return "";
	}

	public String viewTellerDailyWithdrawals() {
		this.redirect("teller/viewTellerDailyWithdrawals.jspx");
		return "";
	}

	public String viewTellerDailyDeposits() {
		this.redirect("teller/viewTellerDailyDeposits.jspx");
		return "";
	}

	public String viewAllWithdrawals() {
		this.redirect("teller/viewAllWithdrawals.jspx");
		return "";
	}
	
	public String tellerNetPosition(){
		this.redirect("teller/tellerNetPosition.jspx");
		return "";
	}

	public String searchNonHolderWithdrawalTxn() {
		this.redirect("teller/searchNonHolderWithdrawalTxn.jspx");
		return "";
	}

	public String withdrawCash() {
		this.redirect("teller/withdrawCash.jspx");
		return "";
	}

	public String searchBankTransactions() {
		this.redirect("teller/searchBankTransactions.jspx");
		return "";
	}

	public String searchBranchTransactions() {
		this.redirect("teller/searchBranchTransactions.jspx");
		return "";
	}

	public String viewAllTransfers() {
		this.redirect("teller/viewAllTransfers.jspx");
		return "";
	}

	public String dayEndApproval() {
		this.redirect("teller/dayEndApproval.jspx");
		return "";
	}

	public String tellerDayEnd() {
		this.redirect("teller/tellerDayEnd.jspx");
		return "";
	}

	public String searchAllBanksTransactions() {
		this.redirect("teller/searchAllBanksTransactions.jspx");
		return "";
	}

	public String searchTellerTransactions() {
		this.redirect("teller/searchTellerTransactions.jspx");
		return "";
	}
	
	public String depositAgentCash() {
		this.redirect("teller/agentCashDeposit.jspx");
		return "";
	}

	/**
	 * @param renderTellerHome
	 *            the renderTellerHome to set
	 */
	public void setRenderTellerHome(boolean renderTellerHome) {
		this.renderTellerHome = renderTellerHome;
	}

	/**
	 * @return the renderTellerHome
	 */
	public boolean isRenderTellerHome() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderTellerHome = ps.canDo(super.getJaasUserName(), "tellerHome");
		} catch (Exception e) {

		}
		return renderTellerHome;
	}

	public boolean isRenderDepositCash() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderDepositCash = ps
					.canDo(super.getJaasUserName(), "depositCash");
		} catch (Exception e) {

		}
		return renderDepositCash;
	}

	public void setRenderDepositCash(boolean renderDepositCash) {
		this.renderDepositCash = renderDepositCash;
	}

	public boolean isRenderViewAllDeposits() {

		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderViewAllDeposits = ps.canDo(super.getJaasUserName(),
					"viewAllDeposits");
		} catch (Exception e) {

		}
		return renderViewAllDeposits;
	}

	public void setRenderViewAllDeposits(boolean renderViewAllDeposits) {
		this.renderViewAllDeposits = renderViewAllDeposits;
	}

	public boolean isRenderViewTellerDailyDeposits() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderViewTellerDailyDeposits = ps.canDo(super.getJaasUserName(),
					"viewTellerDailyDeposits");
		} catch (Exception e) {

		}
		return renderViewTellerDailyDeposits;
	}

	public void setRenderViewTellerDailyDeposits(
			boolean renderViewTellerDailyDeposits) {
		this.renderViewTellerDailyDeposits = renderViewTellerDailyDeposits;
	}

	public boolean isRenderViewAllWithdrawals() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderViewAllWithdrawals = ps.canDo(super.getJaasUserName(),
					"viewAllWithdrawals");
		} catch (Exception e) {

		}
		return renderViewAllWithdrawals;
	}

	public boolean isRenderSearchNonHolderWithdrawalTxn() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderSearchNonHolderWithdrawalTxn = ps.canDo(super
					.getJaasUserName(), "searchNonHolderWithdrawalTxn");
		} catch (Exception e) {

		}
		return renderSearchNonHolderWithdrawalTxn;
	}

	public boolean isRenderWithdrawCash() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderWithdrawCash = ps.canDo(super.getJaasUserName(),
					"withdrawCash");
		} catch (Exception e) {

		}
		return renderWithdrawCash;
	}

	public boolean isRenderSearchBankTransactions() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderSearchBankTransactions = ps.canDo(super.getJaasUserName(),
					"searchBankTransactions");
		} catch (Exception e) {

		}
		return renderSearchBankTransactions;
	}

	public boolean isRenderSearchBranchTransactions() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderSearchBranchTransactions = ps.canDo(super.getJaasUserName(),
					"searchBranchTransactions");
		} catch (Exception e) {

		}
		return renderSearchBranchTransactions;
	}

	public boolean isRenderViewAllTransfers() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderViewAllTransfers = ps.canDo(super.getJaasUserName(),
					"viewAllTransfers");
		} catch (Exception e) {

		}
		return renderViewAllTransfers;
	}

	public boolean isRenderDayEndApproval() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderDayEndApproval = ps.canDo(super.getJaasUserName(),
					"dayEndApproval");
		} catch (Exception e) {

		}
		return renderDayEndApproval;
	}

	public boolean isRenderSearchTellerTransactions() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderSearchTellerTransactions = ps.canDo(super.getJaasUserName(),
					"searchTellerTransactions");
		} catch (Exception e) {

		}
		return renderSearchTellerTransactions;
	}

	public void setRenderSearchTellerTransactions(
			boolean renderSearchTellerTransactions) {
		this.renderSearchTellerTransactions = renderSearchTellerTransactions;
	}

	public boolean isRenderViewTellerDailyWithdrawals() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderViewTellerDailyWithdrawals = ps.canDo(
					super.getJaasUserName(), "viewTellerDailyWithdrawals");
		} catch (Exception e) {

		}
		return renderViewTellerDailyWithdrawals;
	}

	public void setRenderViewTellerDailyWithdrawals(
			boolean renderViewTellerDailyWithdrawals) {
		this.renderViewTellerDailyWithdrawals = renderViewTellerDailyWithdrawals;
	}

	public void setRenderViewAllWithdrawals(boolean renderViewAllWithdrawals) {
		this.renderViewAllWithdrawals = renderViewAllWithdrawals;
	}

	public void setRenderSearchNonHolderWithdrawalTxn(
			boolean renderSearchNonHolderWithdrawalTxn) {
		this.renderSearchNonHolderWithdrawalTxn = renderSearchNonHolderWithdrawalTxn;
	}

	public void setRenderWithdrawCash(boolean renderWithdrawCash) {
		this.renderWithdrawCash = renderWithdrawCash;
	}

	public void setRenderSearchBankTransactions(
			boolean renderSearchBankTransactions) {
		this.renderSearchBankTransactions = renderSearchBankTransactions;
	}

	public void setRenderSearchBranchTransactions(
			boolean renderSearchBranchTransactions) {
		this.renderSearchBranchTransactions = renderSearchBranchTransactions;
	}

	public void setRenderViewAllTransfers(boolean renderViewAllTransfers) {
		this.renderViewAllTransfers = renderViewAllTransfers;
	}

	public void setRenderDayEndApproval(boolean renderDayEndApproval) {
		this.renderDayEndApproval = renderDayEndApproval;
	}

	/**
	 * @param renderTellerDayEnd
	 *            the renderTellerDayEnd to set
	 */
	public void setRenderTellerDayEnd(boolean renderTellerDayEnd) {
		this.renderTellerDayEnd = renderTellerDayEnd;
	}

	/**
	 * @return the renderTellerDayEnd
	 */
	public boolean isRenderTellerDayEnd() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderTellerDayEnd = ps.canDo(super.getJaasUserName(),
					"tellerDayEnd");
		} catch (Exception e) {

		}
		return renderTellerDayEnd;
	}

	/**
	 * @param renderSearchAllBanksTransactions
	 *            the renderSearchAllBanksTransactions to set
	 */
	public void setRenderSearchAllBanksTransactions(
			boolean renderSearchAllBanksTransactions) {
		this.renderSearchAllBanksTransactions = renderSearchAllBanksTransactions;
	}

	/**
	 * @return the renderSearchAllBanksTransactions
	 */
	public boolean isRenderSearchAllBanksTransactions() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderSearchAllBanksTransactions = ps.canDo(
					super.getJaasUserName(), "searchAllBanksTransactions");
		} catch (Exception e) {

		}
		return renderSearchAllBanksTransactions;
	}

	public String changePassword() {
		this.redirect("teller/changeProfilePassword.jspx");
		return "changeProfilePassword";
	}

	private void redirect(String path) {
		try {
			if (path.startsWith("/")) {
				path = path.replaceFirst("/", "");
			}
			FacesContext.getCurrentInstance().getExternalContext().redirect(
					"/TellerWeb/" + path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String tellerStartOfDay() {
		this.redirect("teller/tellerStartOfDay.jspx");
		return "";
	}

	public String approveTellerStartofDay() {
		this.redirect("teller/startOfDayApproval.jspx");
		return "";
	}

	public String dayEndSearches() {
		this.redirect("teller/searchDayEndTxns.jspx");
		return "";
	}

	
	public String dayEndManualResolve() {
		this.redirect("teller/viewdayEndsManualResolve.jspx");
		return "";
	}
	
	
	public boolean isRenderDayEndSearchs() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderSearchAllBanksTransactions = ps.canDo(
					super.getJaasUserName(), "searchDayEndTxns");
		} catch (Exception e) {

		}
		return renderSearchAllBanksTransactions;
	}

	public void setRenderDayEndSearchs(boolean renderDayEndSearchs) {
		this.renderDayEndSearchs = renderDayEndSearchs;
	}

	public boolean isRenderTellerStartOfDay() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderTellerStartOfDay = ps.canDo(super.getJaasUserName(),
					"tellerStartOfDay");
		} catch (Exception e) {

		}
		return renderTellerStartOfDay;
	}

	public void setRenderTellerStartOfDay(boolean renderTellerStartOfDay) {
		this.renderTellerStartOfDay = renderTellerStartOfDay;
	}

	public boolean isRenderStartOfDayApproval() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderStartOfDayApproval = ps.canDo(super.getJaasUserName(),
					"startOfDayApproval");
		} catch (Exception e) {

		}
		return renderStartOfDayApproval;
	}

	public void setRenderStartOfDayApproval(boolean renderStartOfDayApproval) {
		this.renderStartOfDayApproval = renderStartOfDayApproval;
	}

	public boolean isRenderTellerNetPosition() {
		
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderTellerNetPosition = ps.canDo(super.getJaasUserName(),
					"tellerNetPosition");
		} catch (Exception e) {

		}
		
		return renderTellerNetPosition;
	}

	public void setRenderTellerNetPosition(boolean renderTellerNetPosition) {
		this.renderTellerNetPosition = renderTellerNetPosition;
	}

	public boolean isRenderDayEndManualResolve() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderDayEndManualResolve = ps.canDo(super.getJaasUserName(),
					"startOfDayApproval");
		} catch (Exception e) {

		}
		return renderDayEndManualResolve;
	}

	public void setRenderDayEndManualResolve(boolean renderDayEndManualResolve) {
		this.renderDayEndManualResolve = renderDayEndManualResolve;
	}

	public boolean isRenderDepositAgentCash() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderDepositAgentCash = ps.canDo(super.getJaasUserName(),
					"agentCashDeposit");
		} catch (Exception e) {

		}
		return renderDepositAgentCash;
	}

	public void setRenderDepositAgentCash(boolean renderDepositAgentCash) {
		this.renderDepositAgentCash = renderDepositAgentCash;
	}
}
