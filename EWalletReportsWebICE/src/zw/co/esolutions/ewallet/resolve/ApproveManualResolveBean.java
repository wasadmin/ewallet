package zw.co.esolutions.ewallet.resolve;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;

import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.process.UniversalProcessSearch;

public class ApproveManualResolveBean extends PageCodeBase{

	public ApproveManualResolveBean() {
		// TODO Auto-generated constructor stub
	}
	
	private List<ProcessTransaction> txns;
		
	@SuppressWarnings("unchecked")
	public String viewPostings() {
		this.txns = null;
		String messageId = (String) super.getRequestParam().get("messageId");
		super.getRequestScope().put("messageId", messageId);
		try {
			ProcessTransaction txn = super.getProcessService().getProcessTransactionByMessageId(messageId);
			if (TransactionStatus.AWAITING_COMPLETION_APPROVAL.equals(txn.getStatus())
					|| TransactionStatus.AWAITING_FAILURE_APPROVAL.equals(txn.getStatus())) {

				super.gotoPage("/resolve/approveMarkedTransaction.jspx");

			} else {
			
				super.gotoPage("/resolve/viewPostings.jspx");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	public void setTxns(List<ProcessTransaction> txns) {
		this.txns = txns;
	}

	public List<ProcessTransaction> getTxns() {
		try {
		
			UniversalProcessSearch uni = new UniversalProcessSearch();
			uni.setStatus(TransactionStatus.AWAITING_APPROVAL);
			uni.setTxnType(TransactionType.ADJUSTMENT);
			this.txns = super.getProcessService().getProcessTransactionsByAllAttributes(uni);
					
			//add AWAITING_COMPLETION_APPROVAL
			txns = (txns == null)? new ArrayList<ProcessTransaction>() : txns;
			this.txns.addAll(super.getProcessService().getProcessTransactionsByTransactionStatus(TransactionStatus.AWAITING_COMPLETION_APPROVAL));
			
			//add AWAITING_FAILURE_APPROVAL
			txns = (txns == null)? new ArrayList<ProcessTransaction>() : txns;
			this.txns.addAll(super.getProcessService().getProcessTransactionsByTransactionStatus(TransactionStatus.AWAITING_FAILURE_APPROVAL));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txns;
	}

}
