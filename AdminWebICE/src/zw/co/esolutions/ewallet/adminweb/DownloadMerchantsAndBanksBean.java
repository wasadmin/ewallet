/**
 * 
 */
package zw.co.esolutions.ewallet.adminweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.msg.DownloadCommandSync;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;

/**
 * @author wasadmin
 *
 */
public class DownloadMerchantsAndBanksBean extends PageCodeBase{

	private String bankCode;
	public DownloadMerchantsAndBanksBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("unchecked")
	public String downloadBanks() {
		try {
			DownloadCommandSync.synch(EWalletConstants.DOWNLOAD_BANKS, this.getBankCode());
		} catch (Exception e) {
			super.setErrorMessage(ResponseCode.E777.getDescription());
			return "failure";
		}
		super.setInformationMessage("Download of Banks Started Successfully.");
		super.getRequestScope().put("entity", "Banks");
		super.gotoPage("/admin/confirmDownload.jspx");
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public String downloadMerchants() {
		try {
			DownloadCommandSync.synch(EWalletConstants.DOWNLOAD_MERCHANTS, this.getBankCode());
		} catch (Exception e) {
			super.setErrorMessage(ResponseCode.E777.getDescription());
			return "failure";
		}
		super.setInformationMessage("Download of Merchants Started Successfully.");
		super.getRequestScope().put("entity", "Merchants");
		super.gotoPage("/admin/confirmDownload.jspx");
		return "success";
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankCode() {
		if(this.bankCode == null && super.getJaasUserName() != null) {
			try {
				bankCode = super.getBankService().findBankBranchById(super.getProfileService().getProfileByUserName(super.getJaasUserName()).getBranchId()).getBank().getCode();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bankCode;
	}

}
