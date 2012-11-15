package zw.co.esolutions.ewallet.resolve;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ManualPojo;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionCharge;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.transaction.pojo.TxnPojo;

public class ViewPostingsBean extends PageCodeBase{

	private String messageId;
	private ProcessTransaction txn;
	private Profile profile;
	private boolean renderEWallet;
	private boolean renderBack;
	private boolean renderTeller;
	private String creditInfor;
	private String debitInfor;
	private boolean renderApprove;
	private boolean renderDisapprove;
	private List<TxnPojo> txnPojolist;
	
	public ViewPostingsBean() {
		this.setRenderApprove(true);
		this.setRenderDisapprove(true);
		this.setRenderBack(true);
	}
	private void clear() {
		this.messageId = null;
		this.profile = null;
		this.txn = null;
		this.creditInfor = null;
		this.debitInfor = null;
		this.txnPojolist = null;
	}
	public String approveTransaction() {
		ManualPojo manual = new ManualPojo();
		String message = null;
		try {
			manual.setUserName(super.getJaasUserName());
			manual.setMessageId(this.getMessageId());
			manual.setOldMessageId(this.getTxn().getOldMessageId());
			manual.setStatus(TransactionStatus.COMPLETED);
			message = super.getProcessService().confirmManualResolve(manual);
			this.setTxn(super.getProcessService().getProcessTransactionByMessageId(manual.getMessageId()));
			this.getTxn();
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		if(ResponseCode.E000.toString().equalsIgnoreCase(message)) {
			super.setInformationMessage("Transaction Completed Successfully.");
		} else if ("Success".equals(message)) {
			super.setInformationMessage("Transaction Completed Successfully.");
		} else {
			super.setErrorMessage(message);
			return "failure";
		}
		this.setRenderApprove(false);
		this.setRenderDisapprove(false);
		return "success";
	}
	
	public String disapproveTransaction() {
		ManualPojo manual = new ManualPojo();
		String message = null;
		try {
			
			ProcessTransaction txn = super.getProcessService().getProcessTransactionByMessageId(manual.getMessageId());
			if (TransactionStatus.AWAITING_APPROVAL.equals(txn.getStatus())) {
				manual.setUserName(super.getJaasUserName());
				manual.setMessageId(this.getMessageId());
				manual.setOldMessageId(this.getTxn().getOldMessageId());
				manual.setStatus(TransactionStatus.DISAPPROVED);
				message = super.getProcessService().confirmManualResolve(manual);
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
		if(ResponseCode.E000.toString().equalsIgnoreCase(message)) {
			super.setInformationMessage("Transaction Disapproved Successfully.");
		} else {
			super.setErrorMessage(message);
			return "failure";
		}
		this.setRenderApprove(false);
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

	public boolean isRenderEWallet() {
		return renderEWallet;
	}

	public void setRenderEWallet(boolean renderEWallet) {
		this.renderEWallet = renderEWallet;
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

	public String getCreditInfor() {
		if(this.creditInfor == null && this.getTxn() != null) {
			BankAccount acc = null;
			try {
				acc = new BankServiceSOAPProxy().getUniqueBankAccountByAccountNumber(this.getTxn().getDestinationAccountNumber());
			} catch (Exception e) {
				// TODO: handle exception
			}
			if(acc != null) {
				this.creditInfor = "Credit "+acc.getType().toString()+" : "+acc.getAccountNumber()+". ";
			}
		}
		return creditInfor;
	}

	public void setCreditInfor(String creditInfor) {
		this.creditInfor = creditInfor;
	}

	public String getDebitInfor() {
		if(this.debitInfor == null && this.getTxn() != null) {
			BankAccount acc = null;
			try {
				acc = new BankServiceSOAPProxy().getUniqueBankAccountByAccountNumber(this.getTxn().getSourceAccountNumber());
			} catch (Exception e) {
				// TODO: handle exception
			}
			if(acc != null) {
				this.debitInfor = "Debit "+acc.getType().toString()+" : "+acc.getAccountNumber()+". ";
			}
		}
		return debitInfor;
	}

	public void setDebitInfor(String debitInfor) {
		this.debitInfor = debitInfor;
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
	public void setTxnPojolist(List<TxnPojo> txnPojolist) {
		this.txnPojolist = txnPojolist;
	}
	public List<TxnPojo> getTxnPojolist() {
		if(this.txnPojolist == null && this.getTxn() != null) {
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			ProcessServiceSOAPProxy processService = new ProcessServiceSOAPProxy();
			
			this.txnPojolist = new ArrayList<TxnPojo>();
			TxnPojo txnPojo = null;
			BankAccount bankAccount = null;
			
			try {
				//Set parameters for the pojo using the main transaction 1st. getTxn()
				String narrative = "";
				if(this.getTxn().getSourceAccountNumber()!=null && !("".equals(this.getTxn().getSourceAccountNumber()))){
					txnPojo = new TxnPojo();
					bankAccount = bankService.getUniqueBankAccountByAccountNumber(this.getTxn().getSourceAccountNumber());
					
					narrative = "Debit ";
					if (bankAccount != null) {
						narrative += bankAccount.getType().toString();
					}
					
					txnPojo.setPosting(narrative);
					txnPojo.setAccountNumber(this.getTxn().getSourceAccountNumber());
					txnPojo.setAmount(-this.getTxn().getAmount());
					
					this.txnPojolist.add(txnPojo);
				}
				System.out.println("&&&&&&&&                             1");
								
				if(this.getTxn().getSourceEQ3AccountNumber()!=null && !("".equals(this.getTxn().getSourceEQ3AccountNumber()))){
					txnPojo = new TxnPojo();
					bankAccount = bankService.getUniqueBankAccountByAccountNumber(this.getTxn().getSourceEQ3AccountNumber());
					
					narrative = "Debit ";
					if (bankAccount != null) {
						narrative += bankAccount.getType().toString();
					}
					
					txnPojo.setPosting(narrative);
					txnPojo.setAccountNumber(this.getTxn().getSourceEQ3AccountNumber());
					txnPojo.setAmount(-this.getTxn().getAmount());
					
					this.txnPojolist.add(txnPojo);
				}
				
				System.out.println("&&&&&&&&                             2");
				
				if(this.getTxn().getDestinationAccountNumber()!=null && !("".equals(this.getTxn().getDestinationAccountNumber()))){
					txnPojo = new TxnPojo();
					bankAccount = bankService.getUniqueBankAccountByAccountNumber(this.getTxn().getDestinationAccountNumber());
					
					narrative = "Credit ";
					if (bankAccount != null) {
						narrative += bankAccount.getType().toString();
					}
					
					txnPojo.setPosting(narrative);
					txnPojo.setAccountNumber(this.getTxn().getDestinationAccountNumber());
					txnPojo.setAmount(this.getTxn().getAmount());
					
					this.txnPojolist.add(txnPojo);
				}
				
				System.out.println("&&&&&&&&                             3");
				
				if(this.getTxn().getDestinationEQ3AccountNumber()!=null && !("".equals(this.getTxn().getDestinationEQ3AccountNumber()))){
					txnPojo = new TxnPojo();
					bankAccount = bankService.getUniqueBankAccountByAccountNumber(this.getTxn().getDestinationEQ3AccountNumber());
					
					narrative = "Credit ";
					if (bankAccount != null) {
						narrative += bankAccount.getType().toString();
					}
					
					txnPojo.setPosting(narrative);
					txnPojo.setAccountNumber(this.getTxn().getDestinationEQ3AccountNumber());
					txnPojo.setAmount(this.getTxn().getAmount());
					
					this.txnPojolist.add(txnPojo);
				}
				
				System.out.println("&&&&&&&&                             4");
				
				List<TransactionCharge> txnCharges = processService.getTransactionChargeByProcessTransactionId(this.getTxn().getId());
				
				for(TransactionCharge charge: txnCharges){
					//EWallet From
					if(charge.getFromEwalletAccount()!=null && !("".equals(charge.getFromEwalletAccount()))){
						txnPojo = new TxnPojo();
						bankAccount = bankService.getUniqueBankAccountByAccountNumber(charge.getFromEwalletAccount());
						
						narrative = "Debit ";
						if (bankAccount != null) {
							narrative += bankAccount.getType().toString() + " [Charge]";
						}
						
						txnPojo.setPosting(narrative);
						txnPojo.setAccountNumber(charge.getFromEwalletAccount());
						txnPojo.setAmount(-charge.getTariffAmount());
						
						this.txnPojolist.add(txnPojo);
					}
				
					System.out.println("&&&&&&&&                             5");
					
					//EQ From
					if(charge.getFromEQ3Account()!=null && !("".equals(charge.getFromEQ3Account()))){
						txnPojo = new TxnPojo();
						bankAccount = bankService.getUniqueBankAccountByAccountNumber(charge.getFromEQ3Account());
						
						narrative = "Debit ";
						if (bankAccount != null) {
							narrative += bankAccount.getType().toString() + " [Charge]";
						}
						
						txnPojo.setPosting(narrative);
						txnPojo.setAccountNumber(charge.getFromEQ3Account());
						txnPojo.setAmount(-charge.getTariffAmount());
						
						this.txnPojolist.add(txnPojo);
					}
					
					//EWallet To
					if(charge.getToEwalletAccount()!=null && !("".equals(charge.getToEwalletAccount()))){
						txnPojo = new TxnPojo();
						bankAccount = bankService.getUniqueBankAccountByAccountNumber(charge.getToEwalletAccount());
						
						narrative = "Credit ";
						if (bankAccount != null) {
							narrative += bankAccount.getType().toString() + " [Charge]";
						}
						
						txnPojo.setPosting(narrative);
						txnPojo.setAccountNumber(charge.getToEwalletAccount());
						txnPojo.setAmount(charge.getTariffAmount());
						
						this.txnPojolist.add(txnPojo);
					}
					
					System.out.println("&&&&&&&&                             6");
					
					//EQ To
					if(charge.getToEQ3Account()!=null && !("".equals(charge.getToEQ3Account()))){
						txnPojo = new TxnPojo();
						bankAccount = bankService.getUniqueBankAccountByAccountNumber(charge.getToEQ3Account());
						
						narrative = "Credit ";
						if (bankAccount != null) {
							narrative += bankAccount.getType().toString() + " [Charge]";
						}
						
						txnPojo.setPosting(narrative);
						txnPojo.setAccountNumber(charge.getToEQ3Account());
						txnPojo.setAmount(charge.getTariffAmount());
						
						this.txnPojolist.add(txnPojo);
					}
				}
				
				/*TxnPojo debitTxn = new TxnPojo();
				TxnPojo creditTxn = new TxnPojo();
				debitTxn.setPosting(this.getDebitInfor());
				debitTxn.setAmount(-this.getTxn().getAmount());
				debitTxn.setAccountNumber(this.getTxn().getSourceAccountNumber());
				this.txnPojolist.add(debitTxn);
				creditTxn.setPosting(this.getCreditInfor());
				creditTxn.setAmount(this.getTxn().getAmount());
				creditTxn.setAccountNumber(this.getTxn().getDestinationAccountNumber());
				this.txnPojolist.add(creditTxn);*/
				
				//Set params for pojo using Transaction charge. Find charge
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return txnPojolist;
	}
	

}
