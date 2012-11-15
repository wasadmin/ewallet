package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.limitservices.service.LimitStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;

public class ApproveLimitBean extends PageCodeBase {
	
	private List<Limit> limitList;
	private String msg;
	
	public ApproveLimitBean() {
		super();
		this.viewAll();
	}

	public String viewAll() {
		try {
			limitList = (super.getLimitService().getLimitByStatusAndBankId(LimitStatus.AWAITING_APPROVAL, ""));
			if(this.getLimitList() == null || this.getLimitList().isEmpty()) {
				super.setInformationMessage("No Limitsfound.");
				this.setMsg("No Limits to Approve.");
				return "failure";
			}
			
		} catch (Exception e) {
			super.setErrorMessage("Error Occurred. Contact Adminstrator. ");
			return "failure";
		}
		super.setInformationMessage(this.getLimitList().size()+" Limit(s) found.");
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public String viewLimit() {
		super.getRequestScope().put("limitId", super.getRequestParam().get("limitId"));
		super.getRequestScope().put("approveLimit", "approveLimit");
		super.gotoPage("/admin/viewLimit.jspx");
		this.setLimitList(null);
		this.msg = null;
		return "success";
	}
	
		
	public List<Limit> getLimitList() {
		if(this.limitList == null || this.limitList.isEmpty()) {
			try {
				this.limitList = super.getLimitService().getLimitByStatusAndBankId(LimitStatus.AWAITING_APPROVAL, "");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return limitList;
	}

	public void setLimitList(List<Limit> limitList) {
		this.limitList = limitList;
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

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
