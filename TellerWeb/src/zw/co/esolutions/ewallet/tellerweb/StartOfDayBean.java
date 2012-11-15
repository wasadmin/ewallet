package zw.co.esolutions.ewallet.tellerweb;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.Exception_Exception;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class StartOfDayBean extends PageCodeBase {
	
	
	
private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(StartOfDayBean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + StartOfDayBean.class);
		}
	}
	
	
	private List<ProcessTransaction> startDayTxns;
	private long totalFloats;
	
	public String viewTransactionInfo(){
		LOG.debug("View Start Day Txn    "+super.getRequestParam().get("startDayTxnId"));
		super.getRequestScope().put("startDayTxnId", super.getRequestParam().get("startDayTxnId"));
		
		super.gotoPage("/teller/viewPendingStartDaySummary.jspx");
		return "viewPendingStartDaySummary.jspx";
		
	
	}

	public List<ProcessTransaction> getStartDayTxns() {
		//get all ProcessTxnof type StartOfDay_FLOAT_IN for the day that are awaiting aproval
		ProcessServiceSOAPProxy service = new ProcessServiceSOAPProxy();
		ProfileServiceSOAPProxy proxy = new ProfileServiceSOAPProxy();
		Profile profile=proxy.getProfileByUserName(getJaasUserName());
		try {
			int count=0;
			totalFloats=0;
			LOG.debug("-------------------------profile branchid------------"+profile.getBranchId());
			LOG.debug("-------------------------status------------"+TransactionStatus.AWAITING_APPROVAL);
			startDayTxns=service.getStartOfDayTransactionByTransactionTypeAndBranchAndStatus(TransactionType.START_OF_DAY_FLOAT_IN, profile.getBranchId(), TransactionStatus.AWAITING_APPROVAL);
			for(ProcessTransaction txn  : startDayTxns){
				++count;
			
				totalFloats+=txn.getAmount();
				LOG.debug("-----------------------txn amount------------- kld----"+txn.getAmount());
				LOG.debug(count+" -----------------total flaot >>>>>>>>>>>>>>>>"+totalFloats);
				
			}
			LOG.debug(" -----------------total flaot >>>>>>>>>>>>>>>>"+totalFloats);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return startDayTxns;
	}

	public void setStartDayTxns(List<ProcessTransaction> startDayTxns) {
		this.startDayTxns = startDayTxns;
	}

	public long getTotalFloats() {
		return totalFloats;
	}

	public void setTotalFloats(long totalFloats) {
		this.totalFloats = totalFloats;
	}
	
	
	

}
