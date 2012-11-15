/**
 * 
 */
package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.tariffservices.service.TariffStatus;
import zw.co.esolutions.ewallet.tariffservices.service.TariffTable;

/**
 * @author taurai
 *
 */
public class ApproveTariffBean extends PageCodeBase{

	private List<TariffTable> tariffTableList;
	
	public ApproveTariffBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setTariffTableList(List<TariffTable> tariffTableList) {
		this.tariffTableList = tariffTableList;
	}

	public List<TariffTable> getTariffTableList() {
		if(this.tariffTableList == null || this.tariffTableList.isEmpty()) {
			try {
				this.tariffTableList = super.getTariffService().getTariffTableByTariffStatusAndBankId(TariffStatus.AWAITING_APPROVAL, "");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return tariffTableList;
	}
	
	@SuppressWarnings("unchecked")
	public String viewTariffTable() {
		super.getRequestScope().put("tariffTableId", super.getRequestParam().get("tariffTableId"));
		super.getRequestScope().put("approveTariff", "approveTariff");
		super.gotoPage("/admin/viewCommissionTable.jspx");
		this.setTariffTableList(null);
		return "success";
	}

	private String getBankId() {
		String bankId = null;
		Profile profile = null;
		Bank bank = null;
		try {
			profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			bank = super.getBankService().findBankBranchById(profile.getBranchId()).getBank();
			bankId = bank.getId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bankId;
	}
	
}
