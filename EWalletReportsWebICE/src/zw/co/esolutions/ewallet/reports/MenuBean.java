package zw.co.esolutions.ewallet.reports;

import java.io.IOException;

import javax.faces.context.FacesContext;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class MenuBean extends PageCodeBase{

	private boolean renderReportsHome;
	private boolean renderTxnLog;
	private boolean renderFailureByReasonReport;
	private boolean renderExceptionsReport;
	private boolean renderCustomerRegStatisticsReport;
	private boolean renderMobileProfileRegStatisticsReport;
	private boolean renderBankAccountRegStatisticsReport;
	private boolean renderBankStatisticsReport;
	private boolean renderCustomersReport;
	private boolean renderDormantCustomersReport;
	private boolean renderTopXCustomersReport;
	private boolean renderCommissionListReport;
	private boolean renderTransactionTotalsReport;
	private boolean renderCashMovementReport;
	private boolean renderCommissionRevenueAnalysisReport;
	private boolean renderViewAccountStatementReport;
	private boolean renderSettlementReport;
	private boolean renderMerchantCustomersReport;
	private boolean renderInitiateManualResolve;
	private boolean renderApproveManualResolve;
	private boolean renderqueryResolution;
	private boolean renderPoolControlAccountStatementReport;
	private boolean renderEWalletBalanceReport;
	private boolean renderViewTransaction;
	private boolean renderAuditTrailReport;
	private boolean renderLoggedOnUsersReport;
	
	public String reportsHome(){
		this.redirect("reportsweb/reportsHome.jspx");
		return "";
	}
	
	public String poolControlAccountStatementReport(){
		this.redirect("reportsweb/poolControlAccountStatementReport.jspx");
		return "";
	}
	
	public String merchantCustomersReport(){
		this.redirect("reportsweb/merchantCustomersReport.jspx");
		return "";
	}
	
	public String commissionRevenueAnalysisReport(){
		this.redirect("reportsweb/commissionRevenueAnalysisReport.jspx");
		return "";
	}
	
	
	public String queryResolution(){
		this.redirect("resolve/queryResolution.jspx");
		return "";
	}
	
	
	public String initiateManualResolve(){
		this.redirect("resolve/initiateManualResolve.jspx");
		return "";
	}
	
	public String approveManualResolve(){
		this.redirect("resolve/approveManualResolve.jspx");
		return "";
	}
	
	public String cashMovementReport(){
		this.redirect("reportsweb/cashMovementReport.jspx");
		return "";
	}
	
	public String txnLogReport(){
		this.redirect("reportsweb/txnLogReport.jspx");
		return "";
	}
	
	public String failureByReasonReport(){
		this.redirect("reportsweb/failureByReasonReport.jspx");
		return "";
	}
	
	public String settlementReport(){
		this.redirect("reportsweb/settlementReport.jspx");
		return "";
	}
	
	public String exceptionsReport(){
		this.redirect("reportsweb/exceptionsReport.jspx");
		return "";
	}
	
	public String customerRegStatisticsReport(){
		this.redirect("reportsweb/customerRegStatisticsReport.jspx");
		return "";
	}
	public String mobileProfileRegStatisticsReport(){
		this.redirect("reportsweb/mobileProfileRegStatisticsReport.jspx");
		return "";
	}
	public String bankAccountRegStatisticsReport(){
		this.redirect("reportsweb/bankAccountRegStatisticsReport.jspx");
		return "";
	}
	
	public String bankStatisticsReport(){
		this.redirect("reportsweb/bankStatisticsReport.jspx");
		return "";
	}
	public String customersReport(){
		this.redirect("reportsweb/customersReport.jspx");
		return "";
	}
	public String dormantCustomersReport(){
		this.redirect("reportsweb/dormantCustomersReport.jspx");
		return "";
	}
	public String topXCustomersReport(){
		this.redirect("reportsweb/topXCustomersReport.jspx");
		return "";
	}
	public String commissionListReport(){
		this.redirect("reportsweb/commissionListReport.jspx");
		return "";
	}
	
	public String transactionTotalsReport(){
		this.redirect("reportsweb/transactionTotalsReport.jspx");
		return "";
	}
	
	public String viewAccountStatementReport(){
		this.redirect("reportsweb/viewAccountStatementReport.jspx");
		return "";
	}
	
	public String viewEWalletBalanceStatementReport(){
		this.redirect("reportsweb/ewalletBalanceReport.jspx");
		return "";
	}
	
	/**
	 * @param renderReportsHome the renderReportsHome to set
	 */
	public void setRenderReportsHome(boolean renderReportsHome) {
		this.renderReportsHome = renderReportsHome;
	}



	/**
	 * @return the renderReportsHome
	 */
	public boolean isRenderReportsHome() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderReportsHome = ps.canDo(super.getJaasUserName(), "reportsHome");
		} catch (Exception e) {
			
		}
		return renderReportsHome;
	}



	public boolean isRenderTxnLog() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderTxnLog = ps.canDo(super.getJaasUserName(), "txnLogReport");
		} catch (Exception e) {
			
		}
		return renderTxnLog;
	}

	public void setRenderTxnLog(boolean renderTxnLog) {
		this.renderTxnLog = renderTxnLog;
	}

	public boolean isRenderFailureByReasonReport() {
		
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderFailureByReasonReport = ps.canDo(super.getJaasUserName(), "failureByReasonReport");
		} catch (Exception e) {
			
		}
		return renderFailureByReasonReport;
	}

	public void setRenderFailureByReasonReport(boolean renderFailureByReasonReport) {
		this.renderFailureByReasonReport = renderFailureByReasonReport;
	}

	public boolean isRenderExceptionsReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderExceptionsReport = ps.canDo(super.getJaasUserName(), "exceptionsReport");
		} catch (Exception e) {
			
		}
		return renderExceptionsReport;
	}

	public void setRenderExceptionsReport(boolean renderExceptionsReport) {
		this.renderExceptionsReport = renderExceptionsReport;
	}

	public boolean isRenderCustomerRegStatisticsReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderCustomerRegStatisticsReport = ps.canDo(super.getJaasUserName(), "customerRegStatisticsReport");
		} catch (Exception e) {
			
		}
		return renderCustomerRegStatisticsReport;
	}

	public boolean isRenderMobileProfileRegStatisticsReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderMobileProfileRegStatisticsReport = ps.canDo(super.getJaasUserName(), "mobileProfileRegStatisticsReport");
		} catch (Exception e) {
			
		}
		return renderMobileProfileRegStatisticsReport;
	}

	public boolean isRenderBankAccountRegStatisticsReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderBankAccountRegStatisticsReport = ps.canDo(super.getJaasUserName(), "bankAccountRegStatisticsReport");
		} catch (Exception e) {
			
		}
		return renderBankAccountRegStatisticsReport;
	}

	public boolean isRenderCustomersReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderCustomersReport = ps.canDo(super.getJaasUserName(), "customersReport");
		} catch (Exception e) {
			
		}
		return renderCustomersReport;
	}

	public boolean isRenderDormantCustomersReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderDormantCustomersReport = ps.canDo(super.getJaasUserName(), "dormantCustomersReport");
		} catch (Exception e) {
			
		}
		return renderDormantCustomersReport;
	}

	public boolean isRenderTopXCustomersReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderTopXCustomersReport = ps.canDo(super.getJaasUserName(), "topXCustomersReport");
		} catch (Exception e) {
			
		}
		return renderTopXCustomersReport;
	}
	
	public boolean isRenderCommissionListReport() {
	//	ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			//renderCommissionListReport = ps.canDo(super.getJaasUserName(), "commissionListReport");
			return false;
		} catch (Exception e) {
			
		}
		return renderCommissionListReport;
	}

	public boolean isRenderTransactionTotalsReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderTransactionTotalsReport = ps.canDo(super.getJaasUserName(), "transactionTotalsReport");
		} catch (Exception e) {
			
		}
		return renderTransactionTotalsReport;
	}

	public void setRenderTransactionTotalsReport(
			boolean renderTransactionTotalsReport) {
		this.renderTransactionTotalsReport = renderTransactionTotalsReport;
	}
	public void setRenderCustomerRegStatisticsReport(
			boolean renderCustomerRegStatisticsReport) {
		this.renderCustomerRegStatisticsReport = renderCustomerRegStatisticsReport;
	}

	public void setRenderMobileProfileRegStatisticsReport(
			boolean renderMobileProfileRegStatisticsReport) {
		this.renderMobileProfileRegStatisticsReport = renderMobileProfileRegStatisticsReport;
	}

	public void setRenderBankAccountRegStatisticsReport(
			boolean renderBankAccountRegStatisticsReport) {
		this.renderBankAccountRegStatisticsReport = renderBankAccountRegStatisticsReport;
	}

	public void setRenderCustomersReport(boolean renderCustomersReport) {
		this.renderCustomersReport = renderCustomersReport;
	}

	public void setRenderDormantCustomersReport(boolean renderDormantCustomersReport) {
		this.renderDormantCustomersReport = renderDormantCustomersReport;
	}

	public void setRenderTopXCustomersReport(boolean renderTopXCustomersReport) {
		this.renderTopXCustomersReport = renderTopXCustomersReport;
	}

	public void setRenderCommissionListReport(boolean renderCommissionListReport) {
		this.renderCommissionListReport = renderCommissionListReport;
	}

	public void setRenderCashMovementReport(boolean renderCashMovementReport) {
		this.renderCashMovementReport = renderCashMovementReport;
	}

	public boolean isRenderCashMovementReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderCashMovementReport = ps.canDo(super.getJaasUserName(), "cashMovementReport");
		} catch (Exception e) {
			
		}
		return renderCashMovementReport;
	}

	public void setRenderCommissionRevenueAnalysisReport(boolean renderCommissionRevenueAnalysisReport) {
		this.renderCommissionRevenueAnalysisReport = renderCommissionRevenueAnalysisReport;
	}

	public boolean isRenderCommissionRevenueAnalysisReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderCommissionRevenueAnalysisReport = ps.canDo(super.getJaasUserName(), "commissionRevenueAnalysisReport");
		} catch (Exception e) {
			
		}
		return renderCommissionRevenueAnalysisReport;
	}

	private void redirect(String path){
		try {
			if(path.startsWith("/")){
				path = path.replaceFirst("/", "");
			}
			FacesContext.getCurrentInstance().getExternalContext().redirect("/EWalletReportsWebICE/"+path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isRenderBankStatisticsReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderBankStatisticsReport = ps.canDo(super.getJaasUserName(), "bankStatisticsReport");
		} catch (Exception e) {
			
		}
		return renderBankStatisticsReport;
	}

	public void setRenderBankStatisticsReport(boolean renderBankStatisticsReport) {
		this.renderBankStatisticsReport = renderBankStatisticsReport;
	}

	/**
	 * @param renderViewAccountStatementReport the renderViewAccountStatementReport to set
	 */
	public void setRenderViewAccountStatementReport(
			boolean renderViewAccountStatementReport) {
		this.renderViewAccountStatementReport = renderViewAccountStatementReport;
	}

	/**
	 * @return the renderViewAccountStatementReport
	 */
	public boolean isRenderViewAccountStatementReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderViewAccountStatementReport = ps.canDo(super.getJaasUserName(), "viewAccountStatementReport");
		} catch (Exception e) {
			
		}
		return renderViewAccountStatementReport;
	}

	/**
	 * @param renderSettlementReport the renderSettlementReport to set
	 */
	public void setRenderSettlementReport(boolean renderSettlementReport) {
		this.renderSettlementReport = renderSettlementReport;
	}

	/**
	 * @return the renderSettlementReport
	 */
	public boolean isRenderSettlementReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderSettlementReport = ps.canDo(super.getJaasUserName(), "settlementReport");
		} catch (Exception e) {
			
		}
		return renderSettlementReport;
	}

	/**
	 * @param renderMerchantCustomersReport the renderMerchantCustomersReport to set
	 */
	public void setRenderMerchantCustomersReport(
			boolean renderMerchantCustomersReport) {
		this.renderMerchantCustomersReport = renderMerchantCustomersReport;
	}

	/**
	 * @return the renderMerchantCustomersReport
	 */
	public boolean isRenderMerchantCustomersReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderMerchantCustomersReport = ps.canDo(super.getJaasUserName(), "merchantCustomersReport");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return renderMerchantCustomersReport;
	}

	/**
	 * @param renderInitiateManualResolve the renderInitiateManualResolve to set
	 */
	public void setRenderInitiateManualResolve(boolean renderInitiateManualResolve) {
		this.renderInitiateManualResolve = renderInitiateManualResolve;
	}

	/**
	 * @return the renderInitiateManualResolve
	 */
	public boolean isRenderInitiateManualResolve() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderInitiateManualResolve = ps.canDo(super.getJaasUserName(), "initiateManualResolve");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return renderInitiateManualResolve;
	}

	public void setRenderApproveManualResolve(boolean renderApproveManualResolve) {
		this.renderApproveManualResolve = renderApproveManualResolve;
	}
	
	public boolean isRenderApproveManualResolve() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderApproveManualResolve = ps.canDo(super.getJaasUserName(), "approveManualResolve");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return renderApproveManualResolve;
	}

	public boolean isRenderqueryResolution() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderqueryResolution = ps.canDo(super.getJaasUserName(), "QUERYRESOLUTION");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return renderqueryResolution;
	}

	public void setRenderqueryResolution(boolean renderqueryResolution) {
		this.renderqueryResolution = renderqueryResolution;
	}

	public void setRenderPoolControlAccountStatementReport(
			boolean renderPoolControlAccountStatementReport) {
		this.renderPoolControlAccountStatementReport = renderPoolControlAccountStatementReport;
	}
	
	public boolean isRenderPoolControlAccountStatementReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderPoolControlAccountStatementReport = ps.canDo(super.getJaasUserName(), "poolControlAccountStatementReport");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return renderPoolControlAccountStatementReport;
	}

	public boolean isRenderEWalletBalanceReport() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderEWalletBalanceReport = ps.canDo(super.getJaasUserName(), "ewalletBalanceReport");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return renderEWalletBalanceReport;
	}

	public void setRenderEWalletBalanceReport(boolean renderEWalletBalanceReport) {
		this.renderEWalletBalanceReport = renderEWalletBalanceReport;
	}
	
	public String reconciliationReport(){
		this.redirect("reportsweb/reconciliationReport.jspx");
		return "";
	}
	
	public boolean isRenderReconciliationReport(){
		return new ProfileServiceSOAPProxy().canDo(getJaasUserName(), "reconciliationReport");
	}
	
	public String ewalletMovementReport(){
		this.redirect("reportsweb/ewalletMovementReport.jspx");
		return "";
	}
	
	public boolean isRenderEwalletMovementReport(){
		return new ProfileServiceSOAPProxy().canDo(getJaasUserName(), "ewalletMovementReport");
	}
	
	public String autoReconcile(){
		this.redirect("reportsweb/autoReconcile.jspx");
		return "";
	}
	
	public boolean isRenderAutoReconcile(){
		return new ProfileServiceSOAPProxy().canDo(getJaasUserName(), "autoReconcile");
		//return true;
	}

	public boolean isRenderPinGenerationReport() {
		return new ProfileServiceSOAPProxy().canDo(getJaasUserName(), "pinGenerationReport");
	}

	public void setRenderPinGenerationReport(boolean renderPinGenerationReport) {
	}
	
	public String pinGenerationReport(){
		this.redirect("reportsweb/pinGenerationReport.jspx");
		return "";
	}
	
	public boolean isRenderViewTransaction() {
		return new ProfileServiceSOAPProxy().canDo(getJaasUserName(), "viewTransaction");
	}

	public void setRenderViewTransaction(boolean renderViewTransaction) {
		this.renderViewTransaction = renderViewTransaction;
	}
	
	public String viewTransaction(){
		this.redirect("reportsweb/viewTransaction.jspx");
		return "";
	}

	public boolean isRenderAuditTrailReport() {
		return new ProfileServiceSOAPProxy().canDo(getJaasUserName(), "auditTrailReport");
	}

	public void setRenderAuditTrailReport(boolean renderAuditTrailReport) {
		this.renderAuditTrailReport = renderAuditTrailReport;
	}
	
	public String auditTrailReport(){
		this.redirect("reportsweb/auditTrailReport.jspx");
		return "";
	}
	
	public boolean isRenderLoggedOnUsersReport() {
		return new ProfileServiceSOAPProxy().canDo(getJaasUserName(), "loggedOnUsersReport");
	}

	public void setRenderLoggedOnUsersReport(boolean renderLoggedOnUsersReport) {
		this.renderLoggedOnUsersReport = renderLoggedOnUsersReport;
	}
	
	public String loggedOnUsersReport(){
		this.redirect("reportsweb/loggedOnUsersReport.jspx");
		return "";
	}
}
