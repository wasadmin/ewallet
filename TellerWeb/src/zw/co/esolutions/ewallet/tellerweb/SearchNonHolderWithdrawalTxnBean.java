package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.process.NonHolderWithdrawalInfo;
import zw.co.esolutions.ewallet.process.ProcessResponse;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class SearchNonHolderWithdrawalTxnBean extends PageCodeBase {

	private static Logger LOG ;
		
		static{
			try {
				PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
				LOG = Logger.getLogger(SearchNonHolderWithdrawalTxnBean.class);
			} catch (Exception e) {
				System.err.println("Failed to initilise logger for " + SearchNonHolderWithdrawalTxnBean.class);
			}
		}
		
	
	private String targetMobile;
	private String secretCode;
	private double amount;
	private String reference;
	private List<ProcessTransaction> processTxnList;
	private boolean renderSearchValues;
	private String infoCaption;
	private double netCashHeld;
	
	
	public double getNetCashHeld() {
		ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService= new ProfileServiceSOAPProxy();
		Profile profile=profileService.getProfileByUserName(getJaasUserName());
		
		try {
			long netCash=proxy.getTellerNetCashPosition(profile.getId(), DateUtil.convertToXMLGregorianCalendar(new Date()));
			LOG.debug("net cash in cents b4 withdrawal >>>>>"+netCash);
			this.netCashHeld=MoneyUtil.convertToDollars(netCash);
			LOG.debug("net cash of teller >>>>>>>>>>>"+netCashHeld);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return netCashHeld;
	}





	public void setNetCashHeld(double netCashHeld) {
		this.netCashHeld = netCashHeld;
	}



	
	public String getInfoCaption() {
		ProfileServiceSOAPProxy profileService= new ProfileServiceSOAPProxy();
		ProcessServiceSOAPProxy serviceProxy= new ProcessServiceSOAPProxy();
		Date toDay=new Date();
		Profile profile=profileService.getProfileByUserName(getJaasUserName());
		String result=serviceProxy.canTellerMakeTransact(profile.getId(), DateUtil.convertToXMLGregorianCalendar(toDay), TransactionType.WITHDRAWAL_NONHOLDER);
		if(ResponseCode.E000.toString().equalsIgnoreCase(result)){
			
		}else{
			this.infoCaption=result;
		}
		return infoCaption;
	}
	public void setInfoCaption(String infoCaption) {
		this.infoCaption = infoCaption;
	}
	public boolean isRenderSearchValues() {
		ProfileServiceSOAPProxy profileService= new ProfileServiceSOAPProxy();
		ProcessServiceSOAPProxy serviceProxy= new ProcessServiceSOAPProxy();
		Date toDay=new Date();
		Profile profile=profileService.getProfileByUserName(getJaasUserName());
		String result=serviceProxy.canTellerMakeTransact(profile.getId(), DateUtil.convertToXMLGregorianCalendar(toDay), TransactionType.WITHDRAWAL_NONHOLDER);
		LOG.debug("non holder withdrawal result:::::::"+result);
		if(ResponseCode.E000.toString().equalsIgnoreCase(result)){
			renderSearchValues=true;
			this.infoCaption="";
		}else{
			LOG.debug("---------------------------infoCaption--------------------------"+infoCaption);
			renderSearchValues=false;
			super.setErrorMessage(result);
			super.setInformationMessage(result);
			//this.infoCaption=result;
			//setInfoCaption(result);
			//getInfoCaption();
			LOG.debug("---------------------------infoCaption--------------------------"+infoCaption);
		}
		
		return renderSearchValues;
	}
	public void setRenderSearchValues(boolean renderSearchValues) {
		this.renderSearchValues = renderSearchValues;
	}
	public String getTargetMobile() {
		return targetMobile;
	}
	public void setTargetMobile(String targetMobile) {
		this.targetMobile = targetMobile;
	}
	public String getSecretCode() {
		return secretCode;
	}
	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getReference() {
		return reference;
	}
	public void setProcessTxnList(List<ProcessTransaction> processTxnList) {
		this.processTxnList = processTxnList;
	}
	public List<ProcessTransaction> getProcessTxnList() {
		return processTxnList;
	}
	public String search() {
		try {
			
			MobileProfile mp = super.getCustomerService().getMobileProfileByMobileNumber(NumberUtil.formatMobileNumber(targetMobile));
			if(mp != null && mp.getId()!= null){
				super.setInformationMessage("Customer with mobile "+targetMobile+" is aleardy registered , Do a withdrawal ");
				return "failed";
			}
			NonHolderWithdrawalInfo info = new NonHolderWithdrawalInfo();
			//.setStatus(TransactionStatus.AWAITING_COLLECTION);
			info.setTargetMobile(NumberUtil.formatMobileNumber(targetMobile));
			info.setAmount(MoneyUtil.convertToCents(amount));
			info.setSecretCode(secretCode.toUpperCase());
			info.setReference(reference);
			
			ProcessTransaction txn = super.getProcessService().getProcessTransaction(info);
			
			if (txn != null && TransactionStatus.AWAITING_COLLECTION.equals(txn.getStatus())) {
				processTxnList = new ArrayList<ProcessTransaction>();
				processTxnList.add(txn);
			} else if(txn != null && TransactionStatus.COMPLETED.equals(txn.getStatus())) {
				super.setInformationMessage("Sorry. The funds for the  non-holder withdrawal have already been collected.");
				
				return "completed";
			}
			else if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())){
				super.setInformationMessage("This is a failed transaction. Transaction Ref : "+txn.getId());
				return "failed";
					
			}
			else if(txn != null && TransactionStatus.EXPIRED.equals(txn.getStatus())){
				super.setInformationMessage("This transaction was reversed because of late collection. Transaction Ref : "+txn.getId());
				return "failed";
			}
			else {
				super.setErrorMessage("Sorry. This transaction was not found.");
				return "failure";
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Transaction found.");
		return "search";
	}
	
	public String viewAll() {
		return "viewAll";
	}
	
	public String confirmWithdrawal() {
		String txnRefId = (String) super.getRequestParam().get("txnRefId");
		ProcessResponse processResponse = null;		
		try {
			LOG.debug("in confirm non holder >>>>>>>>>>>>>>>>>>>>>");
			ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
			LOG.debug("retrieving  process txn  >>>>>>>>>>>>>>>>>>>>>");
			ProcessTransaction txn = proxy.getProcessTransactionByMessageId(txnRefId);
			LOG.debug("done retrieving process txn >>>>>>>>>>>>>>>>>>>>>");
			double txnAmount = MoneyUtil.convertToDollars(txn.getAmount());
			LOG.debug("txn amount >>>>>>>>>>>>>>>>>>>>>"+txnAmount);
			LOG.debug("net cahs ????????"+getNetCashHeld());
			LOG.debug(" whats the resuklt   >>>>>>>" + (txnAmount > getNetCashHeld()));
			if(txnAmount > getNetCashHeld()){
				super.setErrorMessage("Teller has insufficent cash to perform this withdraw ("+MoneyUtil.convertCentsToDollarsPatternNoCurrency(txnAmount)+"). Cash available: "+MoneyUtil.convertCentsToDollarsPatternNoCurrency(getNetCashHeld())+"). Please obtain more float");
				return "failure";
			}
			Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			processResponse = super.getProcessService().confirmNonHolderWithdrawal(txnRefId, profile.getId());
			this.setProcessTxnList(new ArrayList<ProcessTransaction>());
			if(ResponseCode.E000.equals(ResponseCode.valueOf(processResponse.getResponseCode()))){
				super.setInformationMessage("Withdrawal has been processed successfuly.");
				return "confirmWithdrawal";
			}else{
				LOG.debug("in else after failure  >>>>>>>>>>>>>>>>>>>>>");
				super.setErrorMessage(ResponseCode.valueOf(processResponse.getResponseCode()).getDescription());
				return "failure";
			}
		} catch (Exception e) {
			LOG.debug("txn amount >>>>>>>>>>>>>>>>>>>>>"+e.getMessage());
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
	}
	
}
