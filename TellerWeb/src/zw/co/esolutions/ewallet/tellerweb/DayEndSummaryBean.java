package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.DayEnd;
import zw.co.esolutions.ewallet.process.DayEndStatus;
import zw.co.esolutions.ewallet.process.DayEndSummary;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorServiceSOAPProxy;

public class DayEndSummaryBean extends PageCodeBase {
	private String dayEndId;
	private DayEnd dayEnd;
	private String tellerId;
	private long netBalance;
	private long tellerFloatTotal;
	private long netImbalance;
	private long transactions;
	private long totalPayouts;
	private long totalReceipts;
	private List<ProcessTransaction> withDrawalList;
	private List<ProcessTransaction>nonHolderWithdrawalList;
	private List<ProcessTransaction> depositList;
	private List<ProcessTransaction> agentDepositList;
	private List<ProcessTransaction> tellerFloats;
	private boolean approver;
	private boolean showButtons;
	private List<DayEndSummary> summaryList;
	private DayEndSummary despsitSummary;
	private DayEndSummary agentDespsitSummary;
	private DayEndSummary withDrawalSummary;
	private DayEndSummary nonHolderSummary;
	private DayEndSummary tellerFloatSummary;
	private List<ProcessTransaction> dayEndTrxns;
	private String imbalanceCaption;
	
	
private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(DayEndSummaryBean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + DayEndSummaryBean.class);
		}
	}
	
	
	
	public boolean isApprover() {
		return approver;
	}




	public void setApprover(boolean approver) {
		this.approver = approver;
	}




	public void cleanBean(){
		dayEndId=null;
		dayEnd=null;
		tellerId=null;
		netBalance=0;
		transactions=0;
		totalPayouts=0;
		totalReceipts=0;
		withDrawalList=null;
		nonHolderWithdrawalList=null;
		depositList=null;
		summaryList=null;
		despsitSummary=null;
		withDrawalSummary=null;
		nonHolderSummary=null;
	}

	
	
	
	public List<ProcessTransaction> getTellerFloats() {
		return tellerFloats;
	}

	
	
	
	
	public DayEndSummary getTellerFloatSummary() {
		return tellerFloatSummary;
	}




	public void setTellerFloatSummary(DayEndSummary tellerFloatSummary) {
		this.tellerFloatSummary = tellerFloatSummary;
	}




	public List<ProcessTransaction> getTellerFloats(DayEnd dayEnd) {
		//ProcessServiceSOAPProxy proxy= new ProcessServiceSOAPProxy();
		/*List<ProcessTransaction> bankRequestfloats=new ArrayList<ProcessTransaction>();
		List<ProcessTransaction> manualResolvefloats=new ArrayList<ProcessTransaction>();
		bankRequestfloats=proxy.getProcessTransactionByTellerIdAndDateRangeAndStatus(dayEnd.getDayEndDate(), dayEnd.getDayEndDate(), dayEnd.getTellerId(), TransactionStatus.MANUAL_RESOLVE);
		manualResolvefloats=proxy.getProcessTransactionByTellerIdAndDateRangeAndStatus(dayEnd.getDayEndDate(), dayEnd.getDayEndDate(), dayEnd.getTellerId(), TransactionStatus.COMPLETED);
		this.tellerFloats.addAll(bankRequestfloats);
		this.tellerFloats.addAll(manualResolvefloats);
		
		*/
		/*try {
			this.tellerFloats=proxy.getStartDayTxnsByTellerAndDateRangeAndStatus(dayEnd.getTellerId(), dayEnd.getDayEndDate(), dayEnd.getDayEndDate());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		return tellerFloats;
	}


	public void setTellerFloats(List<ProcessTransaction> tellerFloats) {
		this.tellerFloats = tellerFloats;
	}




	public List<ProcessTransaction> getDayEndTrxns() {
		if(getDayEndId()!=null){
			ProcessServiceSOAPProxy proxy= new ProcessServiceSOAPProxy();
			dayEndTrxns=proxy.getProcessTransactionsByDayEndId(getDayEnd());
		}
		return dayEndTrxns;
	}




	public void setDayEndTrxns(List<ProcessTransaction> dayEndTrxns) {
		this.dayEndTrxns = dayEndTrxns;
	}




	public DayEndSummaryBean() {
		super();
		LOG.debug("End day Summary Bean");
		getDayEndId();
		populateDayEnd();
	List<DayEndSummary> list =populateSummaries();
		setNetBalance(list,this.dayEnd);
		calculateNumberofTxn(list);
		//this.tellerFloats=getTellerFloats(this.dayEnd);
		//this.tellerFloatTotal=getFloatTotal(tellerFloats);
		this.tellerFloatTotal=getSum(list, TransactionType.START_OF_DAY_FLOAT_IN);
	LOG.debug("  teller float total Is----------------------------"+tellerFloatTotal);
		LOG.debug("  teller  total payouts___________________-------------     "+this.totalPayouts);
		LOG.debug("  teller  total payouts___________________-------------     "+this.totalReceipts);
		LOG.debug("teller net balance by bonde-------------------------------------"+this.netBalance);
		long result=tellerFloatTotal+totalReceipts-totalPayouts;
		LOG.debug("my net balance by induction--------------------------------------"+result);
		
	}

	

	private long getFloatTotal(List<ProcessTransaction> tellerFloats2) {
		long count=0;
		for(ProcessTransaction txn : tellerFloats2){
			count=count+txn.getAmount();
		}
		return count;
	}




	private void calculateNumberofTxn(List<DayEndSummary> summaries) {
		long count=0;
		for(DayEndSummary summary : summaries){
			count=count+summary.getNumberOfTxn();
		}
		transactions=count;
	}



	private void setNetBalance(List<DayEndSummary> summaries, DayEnd tellerDayEnd) {
	
		long withDrawalSum=0;
		long depositSum=0;
		long nonHolderSum=0;
		long tellerFloat=0;
		long agentDepositSum=0;
		if(summaries!=null){
			LOG.debug("  cummaries>>>>>>>>>>>>>>>>>>>>>>>>"+summaries.size());
		}
		withDrawalSum=getSum(summaries,TransactionType.WITHDRAWAL);
		depositSum=getSum(summaries, TransactionType.DEPOSIT);
		agentDepositSum=getSum(summaries, TransactionType.AGENT_CASH_DEPOSIT);
		nonHolderSum=getSum(summaries, TransactionType.WITHDRAWAL_NONHOLDER);
		tellerFloat=getSum(summaries,TransactionType.START_OF_DAY_FLOAT_IN);
		this.tellerFloatTotal=getSum(summaries,TransactionType.START_OF_DAY_FLOAT_IN);
		DayEnd dayEnd= getDayEnd();
		ProcessServiceSOAPProxy proxy=new ProcessServiceSOAPProxy();
		//this.netBalance=depositSum-withDrawalSum-nonHolderSum;
		netBalance=tellerFloat + depositSum +agentDepositSum - withDrawalSum - nonHolderSum;
		
		this.totalPayouts=withDrawalSum+nonHolderSum;
		this.totalReceipts=depositSum;
		this.tellerFloatTotal=tellerFloat;
		this.netImbalance=dayEnd.getCashTendered()-netBalance;
		
		
		if(dayEnd.getCashTendered()>netBalance){
			imbalanceCaption="Surplus";
		}else if (dayEnd.getCashTendered()<netBalance){
			imbalanceCaption="Shortage";
		}else if(dayEnd.getCashTendered()==netBalance){
			imbalanceCaption="Balanced";
		}
		
	}



	




	private long getSum(List<DayEndSummary> summaries,
			TransactionType withdrawal) {
		if(summaries!=null){
		for(DayEndSummary summary: summaries){
			if(withdrawal.equals(summary.getTransactionType())){
				LOG.debug(summary.getTransactionType().name()+"Summmmmm   --------------------"+summary.getValueOfTxns());
				return summary.getValueOfTxns();
			}
			}
		}
		return 0;
	}



	public long getNetBalance() {
		return netBalance;
	}



	public void setNetBalance(long netBalance) {
		this.netBalance = netBalance;
	}



	public long getTransactions() {
		return transactions;
	}



	public void setTransactions(long transactions) {
		this.transactions = transactions;
	}



	private List<DayEndSummary> populateSummaries() {
		if(dayEndId!=null && dayEnd !=null){
			ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
			try {
				LOG.debug(" dayend    "+dayEnd.getBranchId());
				summaryList=proxy.getDayEndSummaryByDayEndId(dayEndId, getJaasUserName());
				//summaryList=dayEnd.getDayendsummaryList();
				withDrawalSummary=getSummaryByTransactionType(summaryList,TransactionType.WITHDRAWAL);
				agentDespsitSummary=getSummaryByTransactionType(summaryList,TransactionType.AGENT_CASH_DEPOSIT);
				despsitSummary=getSummaryByTransactionType(summaryList,TransactionType.DEPOSIT);
				nonHolderSummary=getSummaryByTransactionType(summaryList,TransactionType.WITHDRAWAL_NONHOLDER);
				tellerFloatSummary=getSummaryByTransactionType(summaryList, TransactionType.START_OF_DAY_FLOAT_IN);
				tellerFloats=getTransactions(tellerFloatSummary, tellerId);
				withDrawalList=getTransactions(withDrawalSummary,tellerId);
				depositList=getTransactions(despsitSummary,tellerId);
				nonHolderWithdrawalList=getTransactions(nonHolderSummary,tellerId);
				//tellerFloats=getTellerFloats();
				return summaryList;
			} catch (Exception e) {
			LOG.debug("Exception");
				e.printStackTrace();
			}
		}
		return summaryList;
	}

	private List<ProcessTransaction> getTransactions(DayEndSummary summary,String tellerId){
		ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
		List<ProcessTransaction> trxnList= new ArrayList<ProcessTransaction>();
		if(summary!=null){
			
			LOG.debug("  fnfn   "+summary.getProcesstransactionList().size());
			trxnList=proxy.getProcessTransactionsByTellerIdAndDayEndSummary(tellerId, summary.getId());
			return  trxnList;
		}else
		return new ArrayList<ProcessTransaction>();
	}

	private DayEndSummary getSummaryByTransactionType(
			List<DayEndSummary> summaryList2, TransactionType txnType) {
		for(DayEndSummary summary : summaryList2){
			if(summary.getTransactionType().equals(txnType)){
				return summary;
			}
		}
		return null;
	}

	private DayEnd populateDayEnd() {
		LOG.debug("Populating day End     "+this.dayEndId +"     hdjd  " +getDayEndId());
		if(getDayEndId()!=null && dayEnd==null){
			ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
			try {
				this.dayEnd= proxy.findDayEndById(getDayEndId(), getJaasUserName());
				approver = this.checkProfileAccessRight("DAYENDAPPROVAL");
				showButtons=renderApprovalButtons(dayEnd);
				this.tellerId=dayEnd.getTellerId();
				LOG.debug("Day End value  "+dayEnd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return this.dayEnd;
	}

	private boolean renderApprovalButtons(DayEnd dayEnd2) {
		if(DayEndStatus.AWAITING_APPROVAL.equals(dayEnd2.getStatus())){
			return true;
		}
		return false;
	}




	private boolean checkProfileAccessRight(String accessRightName) {
		ProfileServiceSOAPProxy proxy = new ProfileServiceSOAPProxy();
		
		return proxy.canDo(getJaasUserName(), accessRightName);
	}




	public DayEndSummary getDespsitSummary() {
		return despsitSummary;
	}

	public void setDespsitSummary(DayEndSummary despsitSummary) {
		this.despsitSummary = despsitSummary;
	}

	public DayEndSummary getWithDrawalSummary() {
		return withDrawalSummary;
	}

	public void setWithDrawalSummary(DayEndSummary withDrawalSummary) {
		this.withDrawalSummary = withDrawalSummary;
	}

	public DayEndSummary getNonHolderSummary() {
		return nonHolderSummary;
	}

	public void setNonHolderSummary(DayEndSummary nonHolderSummary) {
		this.nonHolderSummary = nonHolderSummary;
	}

	public String getDayEndId() {
		if (dayEndId == null) {
			dayEndId = (String) super.getRequestParam().get("dayEndId");
		}else if(getDayEndIdFromRequest()!=null){
			dayEndId=getDayEndIdFromRequest();
		}
		return dayEndId;
	}

	
	private String getDayEndIdFromRequest(){
		String id=(String) super.getRequestScope().get("dayEndId");
		return id;
	}
	public void setDayEndId(String dayEndId) {
		this.dayEndId = dayEndId;
	}

	public DayEnd getDayEnd() {
		if(getDayEndId()!=null && dayEnd==null){
			ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
			try {
				this.dayEnd= proxy.findDayEndById(getDayEndId(), getJaasUserName());
				this.tellerId=dayEnd.getTellerId();
				this.approver=this.checkProfileAccessRight("DAYENDAPPROVAL");
				LOG.debug("Day End value  "+dayEnd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dayEnd;
	}

	public void setDayEnd(DayEnd dayEnd) {
		this.dayEnd = dayEnd;
	}

	public List<ProcessTransaction> getWithDrawalList() {
		return withDrawalList;
	}

	public void setWithDrawalList(List<ProcessTransaction> withDrawalList) {
		this.withDrawalList = withDrawalList;
	}

	public List<ProcessTransaction> getNonHolderWithdrawalList() {
		return nonHolderWithdrawalList;
	}

	public void setNonHolderWithdrawalList(
			List<ProcessTransaction> nonHolderWithdrawalList) {
		this.nonHolderWithdrawalList = nonHolderWithdrawalList;
	}

	public List<ProcessTransaction> getDepositList() {
		return depositList;
	}

	public void setDepositList(List<ProcessTransaction> depositList) {
		this.depositList = depositList;
	}

	public List<DayEndSummary> getSummaryList() {
		
		return summaryList;
	}

	public void setSummaryList(List<DayEndSummary> summaryList) {
		this.summaryList = summaryList;
	}
	
	
	
	public String approve(){
		LOG.debug("starting row back tests equation exception");
		ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
		ReferenceGeneratorServiceSOAPProxy referenceProxy=new ReferenceGeneratorServiceSOAPProxy();
		//Mark dayend as approved
		dayEnd.setStatus(DayEndStatus.APPROVED);
		LOG.debug("DayEnd marked as approved");
		//ProcessTransaction receiptsTxn=null;
		//ProcessTransaction payOutTxn=null;
		
		ProcessTransaction imbalancePostTxn=null;
		//ProcessTransaction floatPostTxn=null;
		ProcessTransaction cashTenderedTxn=null;
		// create ProcessTransactions
		
		
	//Done creation
		
		List<DayEndSummary> summaries;
		try {
			String year=Formats.yearFormatTwoDigit.format(new Date());
			String sequenceName="Day Ends";
			String prefix="A";	
			
			
			summaries = proxy.getDayEndSummariesByDayEnd(getDayEnd().getDayEndId());
			long tellerFlaots=getSum(summaries, TransactionType.START_OF_DAY_FLOAT_IN);
			long withDrawalSum=getSumValue(TransactionType.WITHDRAWAL,summaries);
			long nonHolderWithDrawalSum=getSumValue(TransactionType.WITHDRAWAL_NONHOLDER,summaries);
			long totalPayOuts1=withDrawalSum+nonHolderWithDrawalSum;
			long depositSum=getSumValue(TransactionType.DEPOSIT,summaries);
			
			long cashTendered=dayEnd.getCashTendered();
			long imbalance=0;
			long balance=tellerFlaots+depositSum-totalPayOuts1;
			LOG.debug("Nw getting references");
			//String payoutRef=referenceProxy.generateUUID(sequenceName, prefix, year, 0, 999999999);
			//String depositRef=referenceProxy.generateUUID(sequenceName, prefix, year, 0, 999999999);
			//String floatRef=referenceProxy.generateUUID(sequenceName, prefix, year, 0, 999999999);
			String cashTenderedRef=referenceProxy.generateUUID(sequenceName, prefix, year, 0, 999999999);
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>balance:::::::::::::::"+balance);
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>deposit sum::::::::::::::"+depositSum);
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>teller float::::::::::::::"+tellerFlaots);
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>payouts sum:::::::::::::::::"+totalPayOuts1);
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>cashtendered :::::::::::::::::"+cashTendered);
			//depositSum=500;
			//totalPayOuts1=200;
			//cashTendered=200;
			//balance=100;
			LOG.debug(" receipts    "+(depositSum>0));
			LOG.debug(" payouts    "+(totalPayOuts1>0));
			LOG.debug(" is it an over post overpost    "+(cashTendered>balance));
			LOG.debug(" is it an underpost   "+(cashTendered<balance));
			
			//LOG.debug("   dayend deposit ref     "+depositRef);
			//LOG.debug("   dayend payout ref     "+payoutRef);
			//LOG.debug(" cash tendered      "+cashTendered);
			LOG.debug("balance      "+balance);
			//LOG.debug("imbalance value");
			LOG.debug(" day end id    --------------"+dayEnd.getDayEndId());
		/*	
			if(tellerFlaots>0){
				floatPostTxn=new ProcessTransaction();
				floatPostTxn.setAmount(tellerFlaots);
				floatPostTxn.setId(floatRef);
				floatPostTxn.setDayEndId(dayEnd.getDayEndId());
				floatPostTxn.setMessageId(floatRef);
				floatPostTxn.setTransactionType(TransactionType.DAYEND_FLOATS);
				floatPostTxn.setBranchId(dayEnd.getBranchId());
				floatPostTxn=proxy.createProcessTransaction(floatPostTxn, getJaasUserName());
				LOG.debug("floatTxn    result   "+floatPostTxn+"----------floattxn id is  "+floatPostTxn.getId());
				LOG.debug("floatTxn    result   "+floatPostTxn+"----------recipts id is  "+floatPostTxn.getAmount());
				LOG.debug("floatTxn    result   "+floatPostTxn+"----------message id is  "+floatPostTxn.getMessageId());
				LOG.debug("floatTxn    result   "+floatPostTxn+"----------trxn type is  "+floatPostTxn.getTransferType());
				LOG.debug("floatTxn    result   "+floatPostTxn+"----------dayend id is  "+floatPostTxn.getDayEndId());
				LOG.debug("floatTxn    result   "+floatPostTxn+"----------branch id is  "+floatPostTxn.getBranchId());
				
				
			
			}
			if(depositSum>0){
			//
				receiptsTxn=new ProcessTransaction();
				receiptsTxn.setAmount(depositSum);
				receiptsTxn.setId(depositRef);
				receiptsTxn.setDayEndId(dayEnd.getDayEndId());
				receiptsTxn.setMessageId(depositRef);
				receiptsTxn.setTransactionType(TransactionType.DAYEND_RECEIPTS);
				receiptsTxn.setBranchId(dayEnd.getBranchId());
				
				
				 * persist Receipts trxn
				 
				receiptsTxn=proxy.createProcessTransaction(receiptsTxn, getJaasUserName());
				LOG.debug("ReceiptsTxn    result   "+receiptsTxn+"----------recipts id is  "+receiptsTxn.getId());
				
			}
			
			if(totalPayOuts1>0){
				payOutTxn=new ProcessTransaction();
				payOutTxn.setAmount(totalPayOuts1);
				payOutTxn.setId(payoutRef);
				payOutTxn.setDayEndId(dayEnd.getDayEndId());
				payOutTxn.setMessageId(payoutRef);
				payOutTxn.setTransactionType(TransactionType.DAYEND_PAYOUTS);
				payOutTxn.setBranchId(dayEnd.getBranchId());
				
				
				 * persist Receipts trxn
				 
				payOutTxn=proxy.createProcessTransaction(payOutTxn, getJaasUserName());
				LOG.debug("Payout result ______________--------id for payout txn "+payOutTxn.getId());
			}*/
			
			if(cashTendered>0){
				cashTenderedTxn = new ProcessTransaction();
				cashTenderedTxn.setAmount(cashTendered);
				cashTenderedTxn.setMessageId(cashTenderedRef);
				cashTenderedTxn.setDayEndId(dayEnd.getDayEndId());
				cashTenderedTxn.setId(cashTenderedRef);
				cashTenderedTxn.setBranchId(dayEnd.getBranchId());
				cashTenderedTxn.setTransactionType(TransactionType.DAYEND_CASH_TENDERED);
				cashTenderedTxn=proxy.createProcessTransaction(cashTenderedTxn, getJaasUserName());
				LOG.debug("cashTenderedTxn    result   "+cashTenderedTxn+"----------cashTenderedTxn id is  "+cashTenderedTxn.getId());
				
				
			}
			if(cashTendered>balance){
				log("over post occurred");
				imbalance=cashTendered-balance;
				LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>surplus :::::::::::::::::"+imbalance);
				
				String overpostRef=referenceProxy.generateUUID(sequenceName, prefix, year, 0, 999999999);
				imbalancePostTxn=new ProcessTransaction();
				imbalancePostTxn.setAmount(imbalance);
				imbalancePostTxn.setId(overpostRef);
				imbalancePostTxn.setDayEndId(dayEnd.getDayEndId());
				imbalancePostTxn.setMessageId(overpostRef);
				imbalancePostTxn.setTransactionType(TransactionType.DAYEND_OVERPOST);
				imbalancePostTxn.setBranchId(dayEnd.getBranchId());
				
				
				//imbalancePostTxn=proxy.createProcessTransaction(imbalancePostTxn, getJaasUserName());
			}else if(balance>cashTendered){
				imbalance=balance-cashTendered;
				LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>shoratge :::::::::::::::::"+imbalance);
				
				String underPostRef=referenceProxy.generateUUID(sequenceName, prefix, year, 0, 999999999);
				imbalancePostTxn=new ProcessTransaction();
				imbalancePostTxn.setAmount(imbalance);
				imbalancePostTxn.setId(underPostRef);
				imbalancePostTxn.setDayEndId(dayEnd.getDayEndId());
				imbalancePostTxn.setMessageId(underPostRef);
				imbalancePostTxn.setTransactionType(TransactionType.DAYEND_UNDERPOST);
				imbalancePostTxn.setBranchId(dayEnd.getBranchId());
				
				//txn.setSourceAccountNumber("");
				//txn.setDestinationAccountNumber("");
				imbalancePostTxn.setNarrative("");
				
			}
			if((balance>cashTendered) || (cashTendered>balance)){
				imbalancePostTxn=proxy.createProcessTransaction(imbalancePostTxn, getJaasUserName());
				LOG.debug("imbalance id ------------------------"+imbalancePostTxn.getId());
			}
			
			LOG.debug("Day end book entries");
		DayEnd dayEn2d =proxy.processDayEndBookEntries(dayEnd, getJaasUserName());
		//updateTellerFloats();
	
		this.approver=false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//rollback book entries
			//delete any posted book entries
			
			//mark dayend as still pending approval and update
			LOG.debug("Exception :::::::::"+e.getMessage());
			try {
	
//				proxy.rollBackDayEndBookEntries(dayEnd, getJaasUserName());
				proxy.deleteProcessTransactionsByDayEnd(dayEnd, getJaasUserName());
				super.setErrorMessage("Day end approval process has been aborted.");
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
			e.printStackTrace();
			return "rollback";
		}
	
			// Equation postings
			
			try {
				//boolean result1=false;
				//boolean result2=false;
				boolean result3=false;
				//boolean result4=false;
				boolean result5=false;
				
				/*
				if(receiptsTxn!=null){
					LOG.debug("1 breceipts posting");
					receiptsTxn=proxy.processEquationPosting(dayEnd, receiptsTxn, getJaasUserName());
					if(TransactionStatus.MANUAL_RESOLVE.equals(receiptsTxn.getStatus())){
						dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
						result1=true;
					}
					LOG.debug(" receipsts txn return     "+receiptsTxn);
					if(receiptsTxn!=null){
						LOG.debug(" updating receipsts txn return     "+receiptsTxn);
						receiptsTxn=proxy.updateProcessTxn(receiptsTxn, getJaasUserName());
					}
				}
				
				if(payOutTxn!=null){
					LOG.debug("2 payouts  posting");
					payOutTxn=proxy.processEquationPosting(dayEnd, payOutTxn, getJaasUserName());
					LOG.debug(" payouts txn return     "+payOutTxn);
					if(TransactionStatus.MANUAL_RESOLVE.equals(payOutTxn.getStatus())){
						dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
						result2=true;
					}
					if(payOutTxn!=null){
						LOG.debug(" updating payouts txn return     "+payOutTxn);
						payOutTxn=proxy.updateProcessTxn(payOutTxn, getJaasUserName());
					}
				}*/
				
				if(cashTenderedTxn!=null){
					LOG.debug("1 Cash tendered posting>>>>>>>>>>>>");
					cashTenderedTxn=proxy.processEquationPosting(dayEnd, cashTenderedTxn, getJaasUserName());
					LOG.debug(" cashTenderedTxn txn return     "+cashTenderedTxn);
					if(TransactionStatus.MANUAL_RESOLVE.equals(cashTenderedTxn.getStatus())){
						dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
						result5=true;
					}
					if(cashTenderedTxn!=null){
						LOG.debug(" updating cashTenderedTxn txn return     "+cashTenderedTxn);
						cashTenderedTxn=proxy.updateProcessTxn(cashTenderedTxn, getJaasUserName());
					}
				}
				
				if(imbalancePostTxn!=null){
					LOG.debug("3 imbalance receipts posting   "+imbalancePostTxn.getTransactionType());
					imbalancePostTxn=proxy.processEquationPosting(dayEnd, imbalancePostTxn, getJaasUserName());
					LOG.debug("imbalance    "+imbalancePostTxn);
					if(TransactionStatus.MANUAL_RESOLVE.equals(imbalancePostTxn.getStatus())){
						dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
						result3=true;
					}
					if(imbalancePostTxn!=null){
						LOG.debug(" updating imbalance txn return     "+imbalancePostTxn);
						imbalancePostTxn=proxy.updateProcessTxn(imbalancePostTxn, getJaasUserName());
					}
				}
				/*
				if(floatPostTxn!=null){
					LOG.debug("1 bfloat posting");
					floatPostTxn=proxy.processEquationPosting(dayEnd, floatPostTxn, getJaasUserName());
					LOG.debug("returned from ");
					if(TransactionStatus.MANUAL_RESOLVE.equals(floatPostTxn.getStatus())){
						dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
						result4=true;
					}
					LOG.debug("-------key point ------------ floatPostTxn txn return     "+floatPostTxn.getStatus());
					if(floatPostTxn!=null){
						LOG.debug(" updating floatPostTxn txn return     "+floatPostTxn.getStatus());
						floatPostTxn=proxy.updateProcessTxn(floatPostTxn, getJaasUserName());
					}
				}		*/
				
				//LOG.debug(" result    1 "+result1);
				//LOG.debug(" result    2 "+result2);
				LOG.debug(" result    5 "+result5);
				LOG.debug("result    3   "+result3);
				/*if(result1 || result2 || result3 || result4){
					proxy.editDayEnd(dayEnd, getJaasUserName());
					
					super.setErrorMessage("Day End Equation postings have failed. Please resolve this problem manually.");
					return "Manually Resolve";
				}*/
				
				if(result5|| result3){
					proxy.editDayEnd(dayEnd, getJaasUserName());
					
					super.setErrorMessage("Day End Equation postings have failed. Please resolve this problem manually.");
					return "Manually Resolve";
				}
				
			} catch (Exception e) {
				LOG.debug("posting exception some what or an update error or something");
				LOG.debug("---------------------we got a throwa__----------------------------------"+e.getMessage());
				e.printStackTrace();
				dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
				try {
					proxy.editDayEnd(dayEnd, getJaasUserName());
					super.setErrorMessage("Day End Equation postings have failed. Please resolve this problem manually.");
					return "manually resolve";
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		try {
			proxy.approveDayEnd(dayEnd, getJaasUserName());
			super.setInformationMessage("Day End has been successfully approved.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				return "";
	}
	
	
	
	public String approvalProcess(){
				ProcessServiceSOAPProxy proxy =new ProcessServiceSOAPProxy();
		try {
			zw.co.esolutions.ewallet.process.DayEndApprovalResponse response=proxy.processDayEndApproval(dayEnd,  getJaasUserName());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>response  info>>>>>>>"+response.getDayEndReponse());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>response status>>>>>>"+response.getStatus());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>cash details>>>>"+response.getDetailsCashTenndered());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>imblance details>>>>>>"+response.getDetailsImbalance());
			
			DayEndStatus dayEndStatus=response.getDayEndStatus();
			String dayEndStatusString=dayEndStatus.name();
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+dayEndStatusString);
			
			DayEndStatus status=DayEndStatus.valueOf(dayEndStatusString);
			System.out.println(">>>>>>>>>>>>>status >>>>"+status);
			
			if(status!=null){
				System.out.println(">>>>>>>>>>>>>status >>>>"+status.name());
			}
			dayEnd.setStatus(status);
			
			dayEnd=proxy.processDayEndBookEntries(dayEnd, getJaasUserName());
			//
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>DAY END BOOK ENTRIES DONE>>>>>>>>>>>>");
			
			dayEnd=proxy.editDayEnd(dayEnd, getJaasUserName());
			if(dayEnd!=null){
			System.out.println(">>>>>>>>>>>>>>>>dayEnd Status>>>>>>>>>"+dayEnd.getStatus().name());
		}
			if(ResponseCode.E000.name().equalsIgnoreCase(response.getStatus()) && (dayEnd != null && DayEndStatus.COMPLETED.equals(dayEnd.getStatus()))){
			super.setInformationMessage(response.getDayEndReponse());
			} else if(!DayEndStatus.COMPLETED.equals(dayEnd.getStatus())){
				super.setErrorMessage("Day End approval failed");
				this.approver=false;
				return "";
			}
			
				else{
					super.setErrorMessage(response.getDayEndReponse());
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			e.printStackTrace(System.err);
			super.setInformationMessage("Day End approval failed");
			LOG.debug("error message  "+e.getMessage());
			
		}
		this.approver=false;
		
			
				return "";
		
		
	}
	
	private void updateTellerFloats() {
		for (ProcessTransaction txn : getTellerFloats()) {
			txn.setDayEndId(this.dayEndId);
			ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
			try {
				proxy.updateProcessTxn(txn, getJaasUserName());
			} catch (Exception e) {
			
				e.printStackTrace();
			}
		}
		
	}




	private long getSumValue(TransactionType withdrawal,
			List<DayEndSummary> summaries) {
		
		for(DayEndSummary summary: summaries){
			if(withdrawal.equals(summary.getTransactionType())){
				return summary.getValueOfTxns();
			}
	
		}
		return 0;
	}


	
	public String disapprove(){
		ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
		try {
			
			dayEnd.setStatus(DayEndStatus.DISAPPROVED);
			System.out.println("Starting disapproval process>>>>>>>");
			DayEnd dayEndResult= proxy.disapproveDayEnd(dayEnd, super.getJaasUserName());
		
		DayEnd dayEndResponse=proxy.updateDayEnd(dayEnd, super.getJaasUserName());
			
		System.out.println(">>>>>>>>>>>>>>>>>>dayend response>>>>>>>>>>"+dayEndResponse.getStatus().name());
		
			if(dayEndResult!=null && dayEndResult.getDayEndId()!=null){
				System.out.println(">>>>>>>>>>>>>>>>>>>"+dayEndResult.getStatus());
			}
			System.out.println(">>>>>>>>return from the web service>>>>>>>>>>>");
			super.setInformationMessage("Teller DAY END has been rejected.");
		} catch (Exception e) {
			System.out.println(">>>>>>>>>>exception has occurred>>>>>>"+e.getCause());
			e.printStackTrace();
			System.out.println(">>>>>>>>>>exception has occurred>>>>>>"+e.getMessage());
			
		}
		return "";
		
	}



	public String getTellerId() {
		return tellerId;
	}



	public void setTellerId(String tellerId) {
		this.tellerId = tellerId;
	}
	
	
	public String back(){
		cleanBean();
	String search=getSearchString();
	LOG.debug("   the value "+(search!=null));
		if(search!=null){
			gotoPage("/teller/searchDayEndTxns.jspx");
			return "";
		}
		gotoPage("/teller/dayEndApproval.jspx");
		return "";
	}



	private String getSearchString() {
		String search=(String) super.getRequestScope().get("search");
		LOG.debug("   :::::::::::::::::search     "+search);
	
		return search;
	}




	public long getTotalPayouts() {
		return totalPayouts;
	}



	public void setTotalPayouts(long totalPayouts) {
		this.totalPayouts = totalPayouts;
	}



	public long getTotalReceipts() {
		return totalReceipts;
	}



	public void setTotalReceipts(long totalReceipts) {
		this.totalReceipts = totalReceipts;
	}

	public String viewPostings() {
		try {
			super.getRequestScope().put("messageId", super.getRequestParam().get("messageId"));
			super.getRequestScope().put("fromPage", "dayEndSummary.jspx");
			super.getRequestScope().put("dayEndId", getDayEndId());
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
			// TODO: handle exception
		}
		super.gotoPage("/teller/viewDayEndPostings.jspx");
		return "success";
	}




	



	public long getNetImbalance() {
		return netImbalance;
	}




	public void setNetImbalance(long netImbalance) {
		this.netImbalance = netImbalance;
	}




	public String getImbalanceCaption() {
		return imbalanceCaption;
	}




	public void setImbalanceCaption(String imbalanceCaption) {
		this.imbalanceCaption = imbalanceCaption;
	}




	public long getTellerFloatTotal() {
		return tellerFloatTotal;
	}




	public void setTellerFloatTotal(long tellerFloatTotal) {
		this.tellerFloatTotal = tellerFloatTotal;
	}




	public boolean isShowButtons() {
		return showButtons;
	}




	public void setShowButtons(boolean showButtons) {
		this.showButtons = showButtons;
	}




	public void setAgentDepositList(List<ProcessTransaction> agentDepositList) {
		this.agentDepositList = agentDepositList;
	}




	public List<ProcessTransaction> getAgentDepositList() {
		return agentDepositList;
	}




	public void setAgentDespsitSummary(DayEndSummary agentDespsitSummary) {
		this.agentDespsitSummary = agentDespsitSummary;
	}




	public DayEndSummary getAgentDespsitSummary() {
		return agentDespsitSummary;
	}
	
}
