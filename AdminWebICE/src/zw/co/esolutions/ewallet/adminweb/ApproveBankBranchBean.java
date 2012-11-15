package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankBranchStatus;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class ApproveBankBranchBean extends PageCodeBase{

	private List<BankBranch> branchList;
	
	public List<BankBranch> getBranchList() {
		if(branchList==null){
			try{
				branchList = super.getBankService().getBankBranchByStatus(BankBranchStatus.AWAITING_APPROVAL);
			}catch(Exception e){e.printStackTrace();}
		}
		return branchList;
	}
	public void setBranchList(List<BankBranch> branchList) {
		this.branchList = branchList;
	}
	
	@SuppressWarnings("unchecked")
	public String approve(){
		BankBranch bankBranch = null;
		try{
			String bankBranchId = (String)super.getRequestParam().get("bankBranchId");
			bankBranch = super.getBankService().findBankBranchById(bankBranchId);
			bankBranch = super.getBankService().approveBankBranch(bankBranch, super.getJaasUserName());
			super.setInformationMessage(bankBranch.getName()+" has been approved.");
			super.getRequestScope().put("branchId", bankBranch.getId());
			super.gotoPage("admin/viewBankBranch.jspx");
			MessageSync.populateAndSync(bankBranch, MessageAction.UPDATE);
		}catch(Exception e){
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return "approved";
	}
	
	@SuppressWarnings("unchecked")
	public String reject(){
		BankBranch bankBranch = null;
		try{
			String bankBranchId = (String)super.getRequestParam().get("bankBranchId");
			bankBranch = super.getBankService().findBankBranchById(bankBranchId);
			bankBranch = super.getBankService().rejectBankBranch(bankBranch, super.getJaasUserName());
			super.setInformationMessage(bankBranch.getName()+" has been rejected.");
			super.getRequestScope().put("branchId", bankBranch.getId());
			super.gotoPage("admin/viewBankBranch.jspx");
			MessageSync.populateAndSync(bankBranch, MessageAction.UPDATE);
		}catch(Exception e){
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return "rejected";
	}
	
}
