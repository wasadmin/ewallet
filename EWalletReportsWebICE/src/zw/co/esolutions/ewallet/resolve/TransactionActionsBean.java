/**
 * 
 */
package zw.co.esolutions.ewallet.resolve;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.Transaction;
import zw.co.esolutions.ewallet.bankservices.service.TransactionUniversalPojo;
import zw.co.esolutions.ewallet.process.ManualPojo;
import zw.co.esolutions.ewallet.process.ManualReturn;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionState;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.process.UniversalProcessSearch;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;

/**
 * @author taurai
 * 
 */
public class TransactionActionsBean extends PageCodeBase{

	private ProcessTransaction processTxn;
	private List<Transaction> txns;
	private List<TransactionState> txnStates;
	private String messageId;
	private String debitAccount;
	private String creditAccount;
	private double amount;
	private String narrative;
	private Profile profile;
	private String tableHeader;
	private List<SelectItem> applyMenu;
	private String applyItem;
	private List<SelectItem> txnTypeMenu;
	private String txnTypeItem;
	private String tableHeader1;
	private List<Transaction> transactions;
	private List<ProcessTransaction> latestTxns;
	private String header2;

	private boolean renderManualResolve;
	private boolean renderCompleted;
	private boolean renderReversal;
	private boolean renderTeller;
	private boolean renderBack;
	private boolean renderButtonsSet;
	private boolean renderManualResolveMsg;

	/**
	 * 
	 */
	public TransactionActionsBean() {
		try {
			if(this.getMessageId() == null) {
				this.setMessageId((String)super.getRequestScope().get("messageId"));
				this.setRenderButtonsSet(true);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * @param transactions the transactions to set
	 */
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	/**
	 * @return the transactions
	 */
	public List<Transaction> getTransactions() {
			//Get Old Reference Id
			try {
				if(this.getMessageId() != null) {
				TransactionUniversalPojo trans = new TransactionUniversalPojo();
				trans.setOldMessageId(this.getMessageId());
				this.setTransactions(new BankServiceSOAPProxy().getTransactionsByAllAttributes(trans));
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		
		return transactions;
	}

	public String getTableHeader1() {
		if(this.tableHeader1 == null && this.getProcessTxn() != null) {
			try {
				this.tableHeader1 = "Resolved Transactions For Original Transaction : "+this.getProcessTxn().getId();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tableHeader1;
	}

	
	public void setTableHeader1(String tableHeader1) {
		this.tableHeader1 = tableHeader1;
	}

	public List<SelectItem> getTxnTypeMenu() {
		if(this.txnTypeMenu == null) {
			this.txnTypeMenu = new ArrayList<SelectItem>();
			this.txnTypeMenu.add(new SelectItem(TransactionType.ADJUSTMENT.toString(), TransactionType.ADJUSTMENT.toString()));
			/*for(TransactionType tx : this.getApplicableTxnTypes()) {
				if(TransactionType.TARIFF.equals(tx)) {
					this.txnTypeMenu.add(new SelectItem(tx.toString(), "CHARGE"));
				} else {
					this.txnTypeMenu.add(new SelectItem(tx.toString(), tx.toString().replace("_", " ")));
				}
			}*/
			
		}
		return txnTypeMenu;
	}

	public String getTxnTypeItem() {
		if(this.txnTypeItem == null && this.getProcessTxn() != null) {
			try {
				this.txnTypeItem = this.getProcessTxn().getTransactionType().toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return txnTypeItem;
	}

	public void setTxnTypeMenu(List<SelectItem> txnTypeMenu) {
		this.txnTypeMenu = txnTypeMenu;
	}

	public void setTxnTypeItem(String txnTypeItem) {
		this.txnTypeItem = txnTypeItem;
	}

	public List<SelectItem> getApplyMenu() {
		if(this.applyMenu == null) {
			this.applyMenu = new ArrayList<SelectItem>();
			applyMenu.add(new SelectItem("ewal", "E-Wallet"));
			applyMenu.add(new SelectItem("eq3", "Equation"));
		}
		return applyMenu;
	}

	public String getApplyItem() {
		return applyItem;
	}

	public void setApplyMenu(List<SelectItem> applyMenu) {
		this.applyMenu = applyMenu;
	}

	public void setApplyItem(String applyItem) {
		this.applyItem = applyItem;
	}

	/**
	 * @param tableHeader the tableHeader to set
	 */
	public void setTableHeader(String tableHeader) {
		this.tableHeader = tableHeader;
	}

	/**
	 * @return the tableHeader
	 */
	public String getTableHeader() {
		if(this.tableHeader == null && this.getProcessTxn() != null) {
			this.tableHeader = this.getProcessTxn().getTransactionType().toString().replace("_", " ");
		}
		return tableHeader;
		
	}

	/**
	 * @param renderButtonsSet the renderButtonsSet to set
	 */
	public void setRenderButtonsSet(boolean renderButtonsSet) {
		this.renderButtonsSet = renderButtonsSet;
	}

	/**
	 * @return the renderButtonsSet
	 */
	public boolean isRenderButtonsSet() {
		return renderButtonsSet;
	}

	/**
	 * @param renderBack the renderBack to set
	 */
	public void setRenderBack(boolean renderBack) {
		this.renderBack = renderBack;
	}

	/**
	 * @return the renderBack
	 */
	public boolean isRenderBack() {
		this.renderBack = true;
		return renderBack;
	}

	/**
	 * @param renderTeller the renderTeller to set
	 */
	public void setRenderTeller(boolean renderTeller) {
		this.renderTeller = renderTeller;
	}

	/**
	 * @return the renderTeller
	 */
	public boolean isRenderTeller() {
		return renderTeller;
	}

	public boolean isRenderManualResolve() {
		return renderManualResolve;
	}

	public boolean isRenderCompleted() {
		return renderCompleted;
	}

	public boolean isRenderReversal() {
		return renderReversal;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		if(this.profile == null && this.getProcessTxn() != null) {
			if(this.getProcessTxn().getProfileId() != null) {
				try {
					this.profile = new ProfileServiceSOAPProxy().findProfileById(this.getProcessTxn().getProfileId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return profile;
	}

	public void setRenderManualResolve(boolean renderManualResolve) {
		this.renderManualResolve = renderManualResolve;
	}

	public void setRenderCompleted(boolean renderCompleted) {
		this.renderCompleted = renderCompleted;
	}

	public void setRenderReversal(boolean renderReversal) {
		this.renderReversal = renderReversal;
	}

	public ProcessTransaction getProcessTxn() {
		if(this.processTxn == null && this.getMessageId() != null) {
			try {
				this.processTxn = new ProcessServiceSOAPProxy().getProcessTransactionByMessageId(this.getMessageId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return processTxn;
	}

	public List<Transaction> getTxns() {
		try {
			if(this.getMessageId() != null) {
				TransactionUniversalPojo trans = new TransactionUniversalPojo();
				trans.setProcessTxnReference(this.getMessageId());
				this.setTxns(new BankServiceSOAPProxy().getTransactionsByAllAttributes(trans));
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txns;
	}

	public List<TransactionState> getTxnStates() {
		return txnStates;
	}

	public String getMessageId() {
		try {
			if(this.messageId == null) {
				this.messageId = (String)super.getRequestScope().get("messageId");
				this.setRenderButtonsSet(true);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return messageId;
	}

	public String getDebitAccount() {
		return debitAccount;
	}

	public String getCreditAccount() {
		return creditAccount;
	}

	public double getAmount() {
		return amount;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setProcessTxn(ProcessTransaction processTxn) {
		this.processTxn = processTxn;
	}

	public void setTxns(List<Transaction> txns) {
		this.txns = txns;
	}

	public void setTxnStates(List<TransactionState> txnStates) {
		this.txnStates = txnStates;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setDebitAccount(String debitAccount) {
		this.debitAccount = debitAccount;
	}

	public void setCreditAccount(String creditAccount) {
		this.creditAccount = creditAccount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public String reverseTransaction() {
		return "success";
	}

	public String resolveManually() {
		this.setRenderManualResolve(true);
		this.setRenderButtonsSet(false);
		return "success";
	}

	public String completeTransaction() {
		String message = null;
		try {
			ManualPojo manual = new ManualPojo();
			manual.setStatus(TransactionStatus.AWAITING_COMPLETION_APPROVAL);
			manual.setMessageId(this.getMessageId());
			message = super.getProcessService().completeTransaction(manual);
			if(!ResponseCode.E000.toString().equalsIgnoreCase(message)) {
				super.setErrorMessage(message);
				return "failure";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.setInformationMessage("Request completed successfully. Tranction awaiting approval.");
		return "success";
	}
	
	public String failTransaction() {
		String message = null;
		try {
			ManualPojo manual = new ManualPojo();
			manual.setStatus(TransactionStatus.AWAITING_FAILURE_APPROVAL);
			manual.setMessageId(this.getMessageId());
			message = super.getProcessService().completeTransaction(manual);
			if(!ResponseCode.E000.toString().equalsIgnoreCase(message)) {
				super.setErrorMessage(message);
				return "failure";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.setInformationMessage("Request completed successfully. Tranction awaiting approval.");
		return "success";
	}
	
	public String home() {
		try {
			super.gotoPage("/reportsweb/reportsHome.jspx");
			this.cleanUp();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "success";
	}

	public String finishTxn() {
		return "success";
	}
	
	public String back() {
		try {
			super.gotoPage("/resolve/initiateManualResolve.jspx");
			this.cleanUp();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "success";
	}
	
	public String submit() {
		String message = this.checkAttributes();
		if(message != null) {
			this.setRenderButtonsSet(false);
			super.setInformationMessage(message);
			return "failure";
		}
		
		ManualPojo manual = new ManualPojo();
		try {
			manual.setAmount(MoneyUtil.convertToCents(this.getAmount()));
			boolean ewal = false;
			if("ewal".equalsIgnoreCase(this.getApplyItem())) {
				ewal = true;
			}
			try {
				this.setDebitAccount(NumberUtil.formatMobileNumber(this.getDebitAccount()));
			} catch (Exception e) {
				
			}
			BankAccount acc1 = null;
			acc1 = new BankServiceSOAPProxy().getUniqueBankAccountByAccountNumber(this.getDebitAccount());
			
			try {
				this.setCreditAccount(NumberUtil.formatMobileNumber(this.getCreditAccount()));
			} catch (Exception e) {
				// TODO: handle exception
			}
			BankAccount acc2 = new BankServiceSOAPProxy().getUniqueBankAccountByAccountNumber(this.getCreditAccount());
			if(acc1 == null && acc2 == null) {
				super.setErrorMessage("Invalid Account Numbers.");
				return "failure";
			} else if (acc1 == null) {
				super.setErrorMessage("Invalid or Incorrect account number : "+this.getDebitAccount()+".");
				return "failure";
			} else if(acc2 == null) {
				super.setErrorMessage("Invalid or Incorrect account number : "+this.getCreditAccount()+".");
				return "failure";
			} else if(this.getAmount() < 0) {
				super.setErrorMessage("No amounts less than 0 accepted.");
				return "failure";
			}
			manual.setApplyToEWallet(ewal);
			manual.setDestinationAccountNumber(this.getCreditAccount());
			manual.setSourceAccountNumber(this.getDebitAccount());
			manual.setOldMessageId(this.getMessageId());
			manual.setReason(this.getNarrative());
			manual.setUserName(super.getJaasUserName());
			manual.setTransactionType(TransactionType.valueOf(this.getTxnTypeItem()));
			ManualReturn manReturn = new ProcessServiceSOAPProxy().manualResolve(manual);
			message = manReturn.getResponse();
			UniversalProcessSearch uni = new UniversalProcessSearch();
			uni.setOldMessageId(this.getMessageId());
			this.setLatestTxns(super.getProcessService().getProcessTransactionsByAllAttributes(uni));
			this.getLatestTxns();
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		this.setRenderManualResolve(false);
		this.setRenderButtonsSet(true);
		this.setRenderManualResolveMsg(true);
		super.setInformationMessage("Transaction for manual resolve successfully completed.");
		this.setRenderManualResolveMsg(true);
		return "success";
	}
	
	public String cancel() {
		this.setRenderManualResolve(false);
		this.setRenderButtonsSet(true);
		this.setRenderManualResolveMsg(true);
		super.setInformationMessage("Manual Resolve cancelled.");
		this.setRenderManualResolveMsg(true);
		//this.clearManualFields();
		return "success";
	}
	private void cleanUp() {
		this.amount = 0;
		this.creditAccount = null;
		this.debitAccount = null;
		this.messageId = null;
		this.narrative = null;
		this.processTxn = null;
		this.txns = null;
		this.transactions = null;
		this.txnStates = null;
		this.profile = null;
		this.tableHeader = null;
		this.tableHeader1 = null;
		this.latestTxns = null;
		this.header2 = null;
		
	}
	
/*	private void clearManualFields() {
		this.amount = 0;
		this.creditAccount = null;
		this.debitAccount = null;
		this.narrative = null;
		
	}*/
	
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int initialLength = buffer.length();
		if(this.getAmount() == 0) {
			buffer.append("Amount, ");
		} if(this.getCreditAccount() == null || "".equalsIgnoreCase(this.getCreditAccount())) {
			buffer.append("Credit, ");
			
		}if(this.getDebitAccount() == null || "".equalsIgnoreCase(this.getDebitAccount())) {
			buffer.append("Debit, ");
			
		} if(this.getNarrative() == null || "".equalsIgnoreCase(this.getNarrative())) {
			buffer.append("Reason for Posting, ");
			
		} if(this.getApplyItem() == null || "".equalsIgnoreCase(this.getApplyItem())) {
			buffer.append("Apply To, ");
			
		}
		int length = buffer.toString().length();
				
		if(length > initialLength) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private TransactionType[] getApplicableTxnTypes() {
		
		return new TransactionType[] {TransactionType.DEPOSIT, TransactionType.WITHDRAWAL, TransactionType.WITHDRAWAL_NONHOLDER, 
				TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, 
				TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.EWALLET_TO_EWALLET_TRANSFER, TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, 
				TransactionType.BILLPAY, TransactionType.EWALLET_BILLPAY, TransactionType.TOPUP, TransactionType.EWALLET_TOPUP, 
				TransactionType.TARIFF, TransactionType.AGENT_CUSTOMER_DEPOSIT, TransactionType.AGENT_CUSTOMER_WITHDRAWAL, TransactionType.COMMISSION_TRANSFER, 
				TransactionType.DAYEND_OVERPOST, TransactionType.DAYEND_PAYOUTS, TransactionType.DAYEND_RECEIPTS, TransactionType.DAYEND_UNDERPOST};
		
	}

	public void setRenderManualResolveMsg(boolean renderManualResolveMsg) {
		this.renderManualResolveMsg = renderManualResolveMsg;
	}

	public boolean isRenderManualResolveMsg() {
		return renderManualResolveMsg;
	}

	public List<ProcessTransaction> getLatestTxns() {
		if((latestTxns == null || latestTxns.isEmpty()) && this.getMessageId() != null) {
			try {
				UniversalProcessSearch uni = new UniversalProcessSearch();
				uni.setOldMessageId(this.getMessageId());
				latestTxns = super.getProcessService().getProcessTransactionsByAllAttributes(uni);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return latestTxns;
	}

	public void setLatestTxns(List<ProcessTransaction> latestTxns) {
		this.latestTxns = latestTxns;
	}

	public String getHeader2() {
		if(this.header2 == null && this.getProcessTxn() != null) {
			try {
				this.header2 = "Newly Created Transactions For Original Transaction : "+this.getProcessTxn().getId();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return header2;
	}

	public void setHeader2(String header2) {
		this.header2 = header2;
	}

}
