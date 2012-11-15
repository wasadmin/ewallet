package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.DayEnd;
import zw.co.esolutions.ewallet.process.DayEndSummary;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionType;

public class TellerSummary extends PageCodeBase {
	private String dayEndId;
	private DayEnd dayEnd;
	private String tellerId;

	private long tellerFloatTotal;
	private long netImbalance;
	
	private long totalPayouts;
	private long totalReceipts;
	
	private List<ProcessTransaction> tellerFloats;
	private List<ProcessTransaction> withDrawalList;
	private List<ProcessTransaction>nonHolderWithdrawalList;
	private List<ProcessTransaction> depositList;
	private List<ProcessTransaction> agentDepositList;
	
	private List<DayEndSummary> summaryList;
	private DayEndSummary despsitSummary;
	private DayEndSummary withDrawalSummary;
	private DayEndSummary nonHolderSummary;
	private DayEndSummary tellerFloatSummary;
	private DayEndSummary agentDepositSummary;
	
	private long netBalance;
	private long transactions;
	private String imbalanceCaption;
	
	
	
private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(TellerSummary.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + TellerSummary.class);
		}
	}
	
	
	

	public TellerSummary() {
		super();
	//	LOG.debug("End day Summary Bean");
	//	LOG.debug("  Teller Summary jfkfkf   +++++"+getDayEndId());getDayEndId();
		populateDayEnd();
		List<DayEndSummary> list=populateSummaries();
		setNetBalance(list);
		calculateNumberofTxn(list);
		
	}

	private void calculateNumberofTxn(List<DayEndSummary> summaries) {
		long count=0;
		for(DayEndSummary summary : summaries){
			count=count+summary.getNumberOfTxn();
		}
		transactions=count;
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

	public long getNetImbalance() {
		return netImbalance;
	}

	public void setNetImbalance(long netImbalance) {
		this.netImbalance = netImbalance;
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

	public List<ProcessTransaction> getTellerFloats() {
		return tellerFloats;
	}

	public void setTellerFloats(List<ProcessTransaction> tellerFloats) {
		this.tellerFloats = tellerFloats;
	}

	public DayEndSummary getTellerFloatSummary() {
		return tellerFloatSummary;
	}

	public void setTellerFloatSummary(DayEndSummary tellerFloatSummary) {
		this.tellerFloatSummary = tellerFloatSummary;
	}

	private List<DayEndSummary> populateSummaries() {
		if(dayEndId!=null && dayEnd !=null){
			ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
			try {
				LOG.debug(" Teller summary dayend    "+dayEnd.getBranchId());
				
				summaryList=proxy.getDayEndSummaryByDayEndId(dayEndId, getJaasUserName());
				//summaryList=dayEnd.getDayendsummaryList();
				withDrawalSummary=getSummaryByTransactionType(summaryList,TransactionType.WITHDRAWAL);
				despsitSummary=getSummaryByTransactionType(summaryList,TransactionType.DEPOSIT);
				nonHolderSummary=getSummaryByTransactionType(summaryList,TransactionType.WITHDRAWAL_NONHOLDER);
				tellerFloatSummary=getSummaryByTransactionType(summaryList, TransactionType.START_OF_DAY_FLOAT_IN);
				agentDepositSummary = getSummaryByTransactionType(summaryList, TransactionType.AGENT_CASH_DEPOSIT);
				tellerFloats=getTransactions(tellerFloatSummary, tellerId);
				withDrawalList=getTransactions(withDrawalSummary,tellerId);
				depositList=getTransactions(despsitSummary,tellerId);
				nonHolderWithdrawalList=getTransactions(nonHolderSummary,tellerId);
				agentDepositList = getTransactions(agentDepositSummary, tellerId);
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
			
			LOG.debug(" teller summary  fnfn   "+summary.getProcesstransactionList().size());
			trxnList=proxy.getProcessTransactionsByTellerIdAndDayEndSummary(tellerId, summary.getId());
			return  trxnList;
		}else
		return new ArrayList<ProcessTransaction>();
	}

	private DayEndSummary getSummaryByTransactionType(
			List<DayEndSummary> summaryList2, TransactionType withdrawal) {
		for(DayEndSummary summary : summaryList2){
			if(summary.getTransactionType().equals(withdrawal)){
				return summary;
			}
		}
		return null;
	}

	private void populateDayEnd() {
		LOG.debug(" Teller summary Populating day End     "+this.dayEndId +"     hdjd  " +getDayEndId());
		if(getDayEndId()!=null && dayEnd==null){
			ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
			try {
				this.dayEnd= proxy.findDayEndById(getDayEndId(), getJaasUserName());
				this.tellerId=dayEnd.getTellerId();
				LOG.debug(" Teller SummaryDay End value  "+dayEnd);
				LOG.debug("Teller Summary Day End value  "+dayEnd.getBranchId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		 if(getDayEndIdFromRequest()!=null){
			dayEndId=getDayEndIdFromRequest();
		}
		return dayEndId;
	}

	
	private String getDayEndIdFromRequest(){
		String id=(String) super.getRequestScope().get("dayEndId");
		LOG.debug(" id from dayend  "+id);
		return id;
	}
	public void setDayEndId(String dayEndId) {
		this.dayEndId = dayEndId;
	}

	public DayEnd getDayEnd() {
		
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
	
	
	private void setNetBalance(List<DayEndSummary> summaries) {
		
		long withDrawalSum=0;
		long depositSum=0;
		long nonHolderSum=0;
		long tellerFloat=0;
		long agentDepositSum = 0;
		withDrawalSum=getSum(summaries,TransactionType.WITHDRAWAL);
		depositSum=getSum(summaries, TransactionType.DEPOSIT);
		nonHolderSum=getSum(summaries, TransactionType.WITHDRAWAL_NONHOLDER);
		tellerFloat=getSum(summaries,TransactionType.START_OF_DAY_FLOAT_IN);
		agentDepositSum = getSum(summaries,TransactionType.AGENT_CASH_DEPOSIT);
		
		DayEnd dayEnd= getDayEnd();
		//ProcessServiceSOAPProxy proxy=new ProcessServiceSOAPProxy();
		//this.netBalance=depositSum-withDrawalSum-nonHolderSum;
		netBalance=tellerFloat +depositSum + agentDepositSum -withDrawalSum-nonHolderSum;
		
		this.totalPayouts=withDrawalSum+nonHolderSum;
		this.totalReceipts=depositSum + agentDepositSum;
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
		for(DayEndSummary summary: summaries){
			if(withdrawal.equals(summary.getTransactionType())){
				return summary.getValueOfTxns();
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



	public String getTellerId() {
		return tellerId;
	}

	public void setTellerId(String tellerId) {
		this.tellerId = tellerId;
	}

	public List<ProcessTransaction> getAgentDepositList() {
		return agentDepositList;
	}

	public void setAgentDepositList(List<ProcessTransaction> agentDepositList) {
		this.agentDepositList = agentDepositList;
	}

	public DayEndSummary getAgentDepositSummary() {
		return agentDepositSummary;
	}

	public void setAgentDepositSummary(DayEndSummary agentDepositSummary) {
		this.agentDepositSummary = agentDepositSummary;
	}
	
}
