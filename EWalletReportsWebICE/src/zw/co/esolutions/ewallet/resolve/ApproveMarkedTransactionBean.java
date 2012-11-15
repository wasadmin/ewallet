package zw.co.esolutions.ewallet.resolve;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class ApproveMarkedTransactionBean extends PageCodeBase {
	private String messageId;
	private ProcessTransaction txn;
	private Profile profile;
	private boolean renderBack;
	private boolean renderTeller;
	private boolean renderApprove;
	private boolean renderDisapprove;
	
	public ApproveMarkedTransactionBean() {
		this.setRenderApprove(true);
		this.setRenderDisapprove(true);
		this.setRenderBack(true);
	}
	private void clear() {
		this.messageId = null;
		this.profile = null;
		this.txn = null;
	}
	public String approveTransaction() {
		
		try {
			System.out.println("1 ------starting approval process");
			ProcessTransaction txn = super.getProcessService().getProcessTransactionByMessageId(this.getMessageId());
			System.out.println("2 ------got txn>>>>>>"+txn);
			if (TransactionStatus.AWAITING_COMPLETION_APPROVAL.equals(txn.getStatus()) 
					|| TransactionStatus.AWAITING_FAILURE_APPROVAL.equals(txn.getStatus())) {
			
				TransactionStatus newStatus = TransactionStatus.COMPLETED;
				if (TransactionStatus.AWAITING_FAILURE_APPROVAL.equals(txn.getStatus())) {
					newStatus = TransactionStatus.FAILED;
				}
				
				txn = super.getProcessService().approveMarkedTransaction(txn.getId(), newStatus, "Transaction Manually Resolved", super.getJaasUserName());
				System.out.println("2 ------got txn>>>>>>"+txn);
				this.setTxn(txn);
				this.getTxn();
			} else {
				super.setErrorMessage("Transaction is in " + txn.getStatus().name() + " status. Cannot be disapproved.");
				return "failure";
			}
		} catch (Exception e) {
			System.out.println("got an unhandled exception >>>>>>>>>>> debugging");
			System.out.println(">>>>>>>>>>>>>>>>>>>>error >>>>>>>>>>>>>"+e.getMessage());
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		super.setInformationMessage("Transaction Status Marked successfully.");
		
		this.setRenderApprove(false);
		this.setRenderDisapprove(true);
		return "success";
	}
	
	public String disapproveTransaction() {
		
		try {
			
			ProcessTransaction txn = super.getProcessService().getProcessTransactionByMessageId(this.getMessageId());
			if (TransactionStatus.AWAITING_COMPLETION_APPROVAL.equals(txn.getStatus()) 
					|| TransactionStatus.AWAITING_FAILURE_APPROVAL.equals(txn.getStatus())) {
			
				txn = super.getProcessService().approveMarkedTransaction(txn.getId(), TransactionStatus.DISAPPROVED, "Transaction Manually Resolved", super.getJaasUserName());
			
				this.setTxn(txn);
				this.getTxn();
			} else {
				super.setErrorMessage("Transaction is in " + txn.getStatus().name() + " status. Cannot be disapproved.");
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		super.setInformationMessage("Transaction Disapproved Successfully.");
		
		this.setRenderApprove(true);
		this.setRenderDisapprove(false);
		return "success";
	}
	
	public String back() {
		this.renderApprove = true;
		this.renderDisapprove = true;
		this.clear();
		super.gotoPage("/resolve/approveManualResolve.jspx");
		return "success";
	}
	
	public String home() {
		this.renderApprove = true;
		this.renderDisapprove = true;
		this.clear();
		super.gotoPage("/reportsweb/reportsHome.jspx");
		return "success";
	}

	public String getMessageId() {
		if(this.messageId == null) {
			messageId = (String)super.getRequestScope().get("messageId");
		}
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public ProcessTransaction getTxn() {
		if(this.txn == null && this.getMessageId() != null) {
			try {
				this.txn = super.getProcessService().getProcessTransactionByMessageId(this.getMessageId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return txn;
	}

	public void setTxn(ProcessTransaction txn) {
		this.txn = txn;
	}
	
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Profile getProfile() {
		if(this.profile == null && this.getTxn() != null) {
			if(this.getTxn().getNonTellerId() != null) {
				this.profile = new ProfileServiceSOAPProxy().findProfileById(this.getTxn().getNonTellerId());
			}
		}
		return profile;
	}

	public void setRenderBack(boolean renderBack) {
		this.renderBack = renderBack;
	}

	public boolean isRenderBack() {
		return renderBack;
	}

	public void setRenderTeller(boolean renderTeller) {
		this.renderTeller = renderTeller;
	}

	public boolean isRenderTeller() {
		return renderTeller;
	}
	public boolean isRenderApprove() {
		return renderApprove;
	}
	public void setRenderApprove(boolean renderApprove) {
		this.renderApprove = renderApprove;
	}
	public boolean isRenderDisapprove() {
		return renderDisapprove;
	}
	public void setRenderDisapprove(boolean renderDisapprove) {
		this.renderDisapprove = renderDisapprove;
	}

	
}
