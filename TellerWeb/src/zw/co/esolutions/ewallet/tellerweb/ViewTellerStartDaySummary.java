package zw.co.esolutions.ewallet.tellerweb;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tivoli.pd.jasn1.boolean32;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;

public class ViewTellerStartDaySummary extends PageCodeBase {
	private String tellerId;
	private boolean approver;
	private ProcessTransaction startOfDayTxn;
	private long netCashHeld;
	private String processTxnId;
	private long totalTellerFloat;
	private List<ProcessTransaction> tellerFloats;
	private boolean showButtons;
	
	
	
	private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(ViewTellerStartDaySummary.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ViewTellerStartDaySummary.class);
		}
	}
	
	
	
	
	
	public boolean isShowButtons() {
		return showButtons;
	}
	public void setShowButtons(boolean showButtons) {
		this.showButtons = showButtons;
	}
	public List<ProcessTransaction> getTellerFloats() {
		ProcessServiceSOAPProxy proxy= new ProcessServiceSOAPProxy();
		String tellerId=getStartOfDayTxn().getProfileId();
		try {
			LOG.debug(">>>>>>>>>>>>>>>>>>> "+ tellerId);
			this.tellerFloats=proxy.getStartDayTxnsByTellerAndDateRangeAndStatus(tellerId, DateUtil.convertToXMLGregorianCalendar(new Date()), DateUtil.convertToXMLGregorianCalendar(new Date()));
			LOG.debug("float size >>>>>>>>>>>>>>>>>>>"+tellerFloats.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return tellerFloats;
	}
	public void setTellerFloats(List<ProcessTransaction> tellerFloats) {
		this.tellerFloats = tellerFloats;
	}
	public long getTotalTellerFloat() {
		long floats=0;
		for(ProcessTransaction txn : getTellerFloats()){
			floats +=txn.getAmount();
			
		}
		totalTellerFloat=floats;
		return totalTellerFloat;
	}
	public void setTotalTellerFloat(long totalTellerFloat) {
		this.totalTellerFloat = totalTellerFloat;
	}
	public String getTellerId() {
		return tellerId;
	}
	public void setTellerId(String tellerId) {
		this.tellerId = tellerId;
	}
	public boolean isApprover() {
		ProfileServiceSOAPProxy proxy= new ProfileServiceSOAPProxy();
		
		this.approver=proxy.canDo(getJaasUserName(), "startOfDayApproval");
		return approver;
	}
	public void setApprover(boolean approver) {
		this.approver = approver;
	}
	public ProcessTransaction getStartOfDayTxn() {
		ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
		try {
			this.startOfDayTxn=proxy.findProcessTransactionById(getProcessTxnId());
			showButtons=getRenderButton(startOfDayTxn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return startOfDayTxn;
	}
	private boolean getRenderButton(ProcessTransaction startOfDayTxn2) {
		if(TransactionStatus.AWAITING_APPROVAL.equals(startOfDayTxn2.getStatus())){
			return true;
		}
		return false;
	}
	public void setStartOfDayTxn(ProcessTransaction startOfDayTxn) {
		this.startOfDayTxn = startOfDayTxn;
	}
	
	
	
	public String approveStartOfDayTxn(){
		ProcessServiceSOAPProxy service=new ProcessServiceSOAPProxy();
		try {
			ProcessTransaction startDaytxn=service.approveTellerDailyFloat(startOfDayTxn.getId(), getJaasUserName());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>.first check>>>>>>>>>>>>>>>>>>>>process transaction>>>>>"+(startDaytxn==null || startDaytxn.getId() == null));
			
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>.first check>>>>>>>>>>>>>>>>>>>>process transaction>>>>>did it fail"+(startOfDayTxn.getStatus()!=null && !(startDaytxn.getStatus().equals(TransactionStatus.COMPLETED))));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>narrative "+startDaytxn.getNarrative());
			if((startDaytxn==null || startDaytxn.getId() == null) || (startOfDayTxn.getStatus()!=null && !(startDaytxn.getStatus().equals(TransactionStatus.COMPLETED)))){
				super.setErrorMessage("Failed to process approval of teller float. ");
			}else if(startDaytxn.getStatus().equals(TransactionStatus.COMPLETED)){
				super.setInformationMessage("Approval of teller float successful.");
				this.approver=false;
				this.showButtons=false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			super.setErrorMessage("Approval of teller float failed.");
			e.printStackTrace();
		}
		return "";
	}
	public long getNetCashHeld() {
		ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
		String profileId=getStartOfDayTxn().getProfileId();
		try {
			this.netCashHeld=proxy.getTellerNetCashPosition(profileId, DateUtil.convertToXMLGregorianCalendar(new Date()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return netCashHeld;
	}
	public void setNetCashHeld(long netCashHeld) {
		this.netCashHeld = netCashHeld;
	}
	public String getProcessTxnId() {
		this.processTxnId=(String)super.getRequestScope().get("startDayTxnId");
		return processTxnId;
	}
	public void setProcessTxnId(String processTxnId) {
		this.processTxnId = processTxnId;
	}
	
	public String back(){
		gotoPage("/teller/startOfDayApproval.jspx");
		return "";
	}
	public String disapprove(){
		ProcessServiceSOAPProxy service=new ProcessServiceSOAPProxy();
		try {
			service.disapproveTellerDailyFloat(getStartOfDayTxn().getId(), getJaasUserName());
			super.setInformationMessage("Teller Float has been rejected.");
			
		} catch (Exception e) {
			super.setErrorMessage("Error occured operation aborted.");
			e.printStackTrace();
		}
		return "";
}
}
