package zw.co.esolutions.ewallet.reports;

import java.util.Date;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionCharge;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.tariffservices.service.Tariff;
import zw.co.esolutions.ewallet.tariffservices.service.TariffServiceSOAPProxy;
import zw.co.esolutions.ewallet.tariffservices.service.TariffTable;
import zw.co.esolutions.ewallet.util.DateUtil;

public class ViewTransactionBean extends PageCodeBase{
	
	private ProcessTransaction transaction;
	private String txnRef;
	private TariffTable commissionTariffTable;
	private TariffTable chargeTariffTable;
	private TransactionCharge charge;
	private boolean txnGrid;
	private boolean detailsGrid;
	private boolean commissionDetails;
	private boolean chargeDetails;
	private TransactionCharge txnCharge;
	private TransactionCharge commission;
	private Date valueDate;
	private Date txnDate;
	private Date chargeDate; 
	private Date tariffEffectiveDate;
	private Date tariffEndDate;
	private Date commissionDate;
	private Date commEffectiveDate;
	private Date commEndDate;
	private double txnAmount;
	private double chargeAmount;
	private double commAmount;
	private String location;
	private String locationType;
	private String intiator;
	private String approver;
	
	public ViewTransactionBean(){
		txnGrid = true;
		detailsGrid = false;
		this.commissionDetails = false;
	}
	
	public String getDetails(){
		
		ProcessServiceSOAPProxy processService = new ProcessServiceSOAPProxy();
		TariffServiceSOAPProxy tariffService = new TariffServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		
		try{
			if(txnRef == null || txnRef == ""){
				super.setErrorMessage("Please enter Transaction Reference ");
				return "";
			}
			
			this.transaction = processService.findProcessTransactionById(txnRef);
			
			if(transaction == null || transaction.getMessageId()== null){
				super.setErrorMessage("Transaction not found confirm Reference NUmber ");
				return "";
			}
			
			txnDate = DateUtil.convertToDate(transaction.getDateCreated());
			valueDate = DateUtil.convertToDate(transaction.getValueDate());
			txnAmount = (transaction.getAmount()/100.00);
			
			List<TransactionCharge> chargeList = processService.getTransactionChargeByProcessTransactionId(txnRef);
			
			for(TransactionCharge tc:chargeList){
				if(TransactionType.TARIFF.toString().equalsIgnoreCase(tc.getTransactionType().toString())){
					this.charge = tc;
					chargeDate = DateUtil.convertToDate(tc.getDateCreated());
					chargeAmount = (charge.getTariffAmount()/100.00);
				}else if(TransactionType.COMMISSION.toString().equalsIgnoreCase(tc.getTransactionType().toString())){
					this.commission = tc;
					commissionDate = DateUtil.convertToDate(tc.getDateCreated());
					commAmount = (commission.getTariffAmount()/100.00);
				}else{
					//Ignore charge yacho hatiizive
				}
			}
			
			if(commission != null){
				this.commissionDetails = true;
				Tariff tariff = tariffService.findTariffById(transaction.getAgentCommissionId());
				commissionTariffTable = tariff.getTariffTable();
				commEffectiveDate = DateUtil.convertToDate(commissionTariffTable.getEffectiveDate());
				commEffectiveDate = DateUtil.convertToDate(commissionTariffTable.getEndDate());
			}else{
				this.commissionDetails = false;
			}
			
			if(charge != null){
				this.chargeDetails = true;
				Tariff tariff = tariffService.findTariffById(transaction.getTariffId());
				chargeTariffTable = tariff.getTariffTable();
				tariffEffectiveDate = DateUtil.convertToDate(chargeTariffTable.getEffectiveDate());
				tariffEndDate = DateUtil.convertToDate(chargeTariffTable.getEndDate());
			}else{
				this.chargeDetails = false;
			}
			
			txnGrid = false;
			detailsGrid = true;
			
			if(TransactionLocationType.BANK_BRANCH.toString().equalsIgnoreCase(transaction.getTransactionLocationType().toString())){
				BankBranch branch = bankService.findBankBranchById(transaction.getBranchId());
				location = branch.getBank().getName()+" "+branch.getName();
				locationType = "BANK BRANCH";
				if(transaction.getProfileId()!=null){
					intiator = profileService.findProfileById(transaction.getProfileId()).toString();
				}
				if(transaction.getNonTellerId()!= null){
					approver = profileService.findProfileById(transaction.getNonTellerId()).toString();
				}else{
					approver = "";
				}
				
			}else if(TransactionLocationType.AGENT.toString().equalsIgnoreCase(transaction.getTransactionLocationType().toString())){
				location = "AGENT";
				locationType = "AGENT";
				intiator = transaction.getCustomerName();
				approver = "";
			}else{
				location = "SMS";
				locationType = "MOBILE PHONE";
				intiator = transaction.getCustomerName();
				approver = "";
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		return "";
	}
	
	public String back(){
		
		txnGrid = true;
		detailsGrid = false;
		commissionDetails = false;
		transaction = new ProcessTransaction();
		txnRef = "";
		commissionTariffTable = new TariffTable();
		chargeTariffTable = new TariffTable();
		charge = new TransactionCharge(); 
		txnCharge = new TransactionCharge();	
		commission = new TransactionCharge();
		
		return "";
	}
	public ProcessTransaction getTransaction() {
		return transaction;
	}
	public void setTransaction(ProcessTransaction transaction) {
		this.transaction = transaction;
	}
	public String getTxnRef() {
		return txnRef;
	}
	public void setTxnRef(String txnRef) {
		this.txnRef = txnRef;
	}
	
	public TransactionCharge getCharge() {
		return charge;
	}
	public void setCharge(TransactionCharge charge) {
		this.charge = charge;
	}

	public boolean isTxnGrid() {
		return txnGrid;
	}

	public void setTxnGrid(boolean txnGrid) {
		this.txnGrid = txnGrid;
	}

	public boolean isDetailsGrid() {
		return detailsGrid;
	}

	public void setDetailsGrid(boolean detailsGrid) {
		this.detailsGrid = detailsGrid;
	}

	public TariffTable getCommissionTariffTable() {
		return commissionTariffTable;
	}

	public void setCommissionTariffTable(TariffTable commissionTariffTable) {
		this.commissionTariffTable = commissionTariffTable;
	}

	public TariffTable getChargeTariffTable() {
		return chargeTariffTable;
	}

	public void setChargeTariffTable(TariffTable chargeTariffTable) {
		this.chargeTariffTable = chargeTariffTable;
	}

	public TransactionCharge getTxnCharge() {
		return txnCharge;
	}

	public void setTxnCharge(TransactionCharge txnCharge) {
		this.txnCharge = txnCharge;
	}

	public TransactionCharge getCommission() {
		return commission;
	}

	public void setCommission(TransactionCharge commission) {
		this.commission = commission;
	}

	public boolean isCommissionDetails() {
		return commissionDetails;
	}

	public void setCommissionDetails(boolean commissionDetails) {
		this.commissionDetails = commissionDetails;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(Date txnDate) {
		this.txnDate = txnDate;
	}

	public Date getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}

	public Date getTariffEffectiveDate() {
		return tariffEffectiveDate;
	}

	public void setTariffEffectiveDate(Date tariffEffectiveDate) {
		this.tariffEffectiveDate = tariffEffectiveDate;
	}

	public Date getTariffEndDate() {
		return tariffEndDate;
	}

	public void setTariffEndDate(Date tariffEndDate) {
		this.tariffEndDate = tariffEndDate;
	}

	public Date getCommissionDate() {
		return commissionDate;
	}

	public void setCommissionDate(Date commissionDate) {
		this.commissionDate = commissionDate;
	}

	public Date getCommEffectiveDate() {
		return commEffectiveDate;
	}

	public void setCommEffectiveDate(Date commEffectiveDate) {
		this.commEffectiveDate = commEffectiveDate;
	}

	public Date getCommEndDate() {
		return commEndDate;
	}

	public void setCommEndDate(Date commEndDate) {
		this.commEndDate = commEndDate;
	}

	public double getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(double txnAmount) {
		this.txnAmount = txnAmount;
	}

	public double getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(double chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public double getCommAmount() {
		return commAmount;
	}

	public void setCommAmount(double commAmount) {
		this.commAmount = commAmount;
	}

	public boolean isChargeDetails() {
		return chargeDetails;
	}

	public void setChargeDetails(boolean chargeDetails) {
		this.chargeDetails = chargeDetails;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getIntiator() {
		return intiator;
	}

	public void setIntiator(String intiator) {
		this.intiator = intiator;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}
	
}
