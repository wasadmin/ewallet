package zw.co.esolutions.ewallet.resolve;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.ManualPojo;
import zw.co.esolutions.ewallet.process.ManualReturn;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorServiceSOAPProxy;

public class QueryResolutionBean extends PageCodeBase {
	private static Properties config = SystemConstants.configParams;
	private ManualPojo manualPojo;
	private List<SelectItem> applyMenu;
	private String applyItem;
	private double amount;
	private String narrative;
	private String sourceEwalletAccount;
	private String destinationEwalletAccount;
	private String sourceEquationAccount;
	private String destinationEquationAccount;
	private String sourceEwalletChargeAccount;
	private String destEWalletChargeAccount;
	private String sourceEqationChargeAccount;
	private String destinationEquationChargeAccount;
	private String originalTxnReference;
	private double charge;
	
	
	
	public String getDestinationEquationChargeAccount() {
		return destinationEquationChargeAccount;
	}

	public void setDestinationEquationChargeAccount(
			String destinationEquationChargeAccount) {
		this.destinationEquationChargeAccount = destinationEquationChargeAccount;
	}

	private long convertDoubleAmountToLong(double value){
		long valueLong=0;
		valueLong=MoneyUtil.convertToCents(value);
		return valueLong;
	}
	
	public String getOriginalTxnReference() {
		ReferenceGeneratorServiceSOAPProxy refProxy= new ReferenceGeneratorServiceSOAPProxy();
		
		//refProxy.getNextNumberInSequence("A", year, minValue, maxValue);
		return originalTxnReference;
	}




	public void setOriginalTxnReference(String originalTxnReference) {
		this.originalTxnReference = originalTxnReference;
	}




	
	
	
	public String save(){
		System.out.println("12233333>>>>>>in save method");
		String response=null;
	
	String checkResponse=checkAttributes(getApplyItem());
	System.out.println(">>>>>>in save method checkResponse  "+checkResponse);
	if(!checkResponse.equalsIgnoreCase(ResponseCode.E000.toString())){
		super.setInformationMessage(checkResponse);
	}
	else{
		System.out.println(">>>>>>in save method in else");
		try {
			//ProfileServiceSOAPProxy proxy= new ProfileServiceSOAPProxy();
			//Profile profile = proxy.getU
			
			System.out.println("@@@@         checking narrative length>");
			
			String reason = this.getManualPojo().getReason();
			if (reason != null) {
				long NARRATIVE_LENGTH = Long.parseLong(config.getProperty("NARRATIVE_LENGTH"));
				System.out.println("#######  NARRATIVE_LENGTH = " + NARRATIVE_LENGTH);

				if (NARRATIVE_LENGTH < reason.length()) {
					super.setErrorMessage("Narrative must not exceed " + NARRATIVE_LENGTH + " characters. You have entered " + reason.length() + " characters.");
					return "failure";
				}
			}
			
			System.out.println(">>>>>>in save method setting amount");
			String result=validateAccounts();
			this.manualPojo=setManulaPojoAmounts();
			System.out.println(">>>>>>in save method setting transclass");
			this.manualPojo=setManualPojoTransClass();
			System.out.println(">>>>>>in save method set username");
			this.manualPojo.setUserName(getJaasUserName());
			System.out.println(">>>>>>in save method calling servid");
			this.manualPojo.setTransactionType(TransactionType.ADJUSTMENT);
			
			System.out.println("#######     Formatting mobile accounts.");
			manualPojo = this.formatMobileAccounts(manualPojo);
			System.out.println("#######     Finished Formatting mobile accounts.");
			
			ManualReturn responseObj=super.getProcessService().manualResolve(manualPojo);
			response=responseObj.getResponse();
			ProcessTransaction txn= responseObj.getTxn();
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>  >>>>>>>>"+response);
			System.out.println(">>>>>>>>>>>>id>>>>>>>>>>>>>"+txn.getId());
			System.out.println(">>>>>>>>>>>>source account>>>>>>>>>>>>>"+txn.getSourceAccountNumber());
			System.out.println(">>>>>>>>>>>>destination account>>>>>>>>>>>>>"+txn.getDestinationAccountNumber());
			System.out.println(">>>>>>>>>>>>>>txn Tariff>>>>>>>>>>>>>>>>>>>"+txn.getTariffAmount());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+txn);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>check>>>>>>>"+ResponseCode.E000.getDescription().equalsIgnoreCase(response));
			if(ResponseCode.E000.getDescription().equalsIgnoreCase(response)){
				System.out.println(">>>>>>in save method sucess in view");
				super.getRequestScope().put("txnId", txn.getId());
				super.getRequestScope().put("manualPojo", this.manualPojo);
				super.setInformationMessage("Direst postings successfully saved and Awaiting Approval.");
				gotoPage("resolve/queryView.jspx");
				return "";
			}else{
				System.out.println("in else >>>>>>>>>>>>>");
				super.setInformationMessage(response);
			}
			
			
		} catch (Exception e) {
			super.setErrorMessage(response);
			e.printStackTrace();
			
		}
	}
		return "";
	}

	
	private String validateAccounts() {
		String applyItem2 =this.applyItem;
		if("E-Wallet".equalsIgnoreCase(applyItem2)){
			this.manualPojo.setTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.EWALLET);
			this.manualPojo.setChargeTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.EWALLET);
			
			
		}else if("Equation".equalsIgnoreCase(applyItem2)){
			this.manualPojo.setTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.EQUATION);
			this.manualPojo.setChargeTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.EQUATION);
			
		}else if("Both".equalsIgnoreCase(applyItem2)){
			this.manualPojo.setTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.BOTH);	
			this.manualPojo.setChargeTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.BOTH);
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+this.manualPojo.getTransactionClass());
		return null;
	}

	private ManualPojo setManualPojoTransClass() {
		String applyItem2 =this.applyItem;
		System.out.println(">>>>>>>>>>>>in setTansclass>>>>>>>>>>"+applyItem2);
		if("E-Wallet".equalsIgnoreCase(applyItem2)){
			this.manualPojo.setTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.EWALLET);
			this.manualPojo.setChargeTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.EWALLET);
			
			
		}else if("Equation".equalsIgnoreCase(applyItem2)){
			this.manualPojo.setTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.EQUATION);
			this.manualPojo.setChargeTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.EQUATION);
			
		}else if("Both".equalsIgnoreCase(applyItem2)){
			this.manualPojo.setTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.BOTH);	
			this.manualPojo.setChargeTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.BOTH);
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+this.manualPojo.getTransactionClass());
		return this.manualPojo;
	}

	private ManualPojo setManulaPojoAmounts() {
		
		long charge=convertDoubleAmountToLong(this.charge);
		long amount=convertDoubleAmountToLong(this.amount);
		System.out.println(">>>>>>setManual pojo charge >>>>"+charge);
		System.out.println(">>>>>>setManual pojo amounts >>>>"+amount);
		
		this.manualPojo.setChargeAmount(charge);
		this.manualPojo.setChargeTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.BOTH);
		this.manualPojo.setAmount(amount);
		
		return this.manualPojo;
	}

	private String checkEquationAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int initialLength = buffer.length();
		ManualPojo pojo=this.getManualPojo();
		if(pojo.getSourceEQ3AccountNumber()==null || "".equalsIgnoreCase(pojo.getSourceEQ3AccountNumber()) ) {
			buffer.append("Source Equation Account, ");
		} if(pojo.getDestinationEQ3AccountNumber() == null || "".equalsIgnoreCase(pojo.getDestinationEQ3AccountNumber() )) {
			buffer.append("Destination Equation Account, ");
		}
		
		if (manualPojo.getChargeAmount() > 0L) {

			if(pojo.getFromEQ3ChargeAccount() == null || "".equalsIgnoreCase(pojo.getFromEQ3ChargeAccount())) {
				buffer.append("Equation Source Charge account, ");
			}
				
			if(pojo.getToEQ3ChargeAccount() == null || "".equalsIgnoreCase(pojo.getToEQ3ChargeAccount())) {
				buffer.append("Equation Destination account, ");
			} 
		}
		
		int length = buffer.toString().length();
				
		if(length > initialLength) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return ResponseCode.E000.toString();
	}
	
	
	private String checkEwalletAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int initialLength = buffer.length();
		ManualPojo pojo=this.getManualPojo();
		if(pojo.getSourceAccountNumber()==null || "".equalsIgnoreCase(pojo.getSourceAccountNumber()) ) {
			buffer.append("Ewallet Source Account, ");
		} if(pojo.getDestinationAccountNumber() == null || "".equalsIgnoreCase(pojo.getDestinationAccountNumber() )) {
			buffer.append("Destination Equation Account, ");

		}
		if (manualPojo.getChargeAmount() > 0L) {
			if(pojo.getFromEwalletChargeAccount() == null || "".equalsIgnoreCase(pojo.getFromEwalletChargeAccount())) {
				buffer.append("Ewallet Source Charge account, ");
				
			} if(pojo.getToEwalletChargeAccount()== null || "".equalsIgnoreCase(pojo.getToEwalletChargeAccount())) {
				buffer.append("Ewallet Destination Charge account, ");
				
			}
		}
		
		int length = buffer.toString().length();
		
				
		if(length > initialLength) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return ResponseCode.E000.toString();
	}
	

	private String checkAttributes(String applyItem2) {
		String response=null;
		System.out.println(">>>>>>>>>>>>>>>>>>check attributes>>>>>>>>>>>>>"+applyItem2);
		if("E-Wallet".equalsIgnoreCase(applyItem2)){
			response=checkEwalletAttributes();
			
		}else if("Equation".equalsIgnoreCase(applyItem2)){
			response=checkEquationAttributes();
			
		}else if("Both".equalsIgnoreCase(applyItem2)){
			response=checkBothAttributes();
		}
		System.out.println(">>>>>>>>>>>>>>>>>>check attributes response>>>>>>>>>>>>>"+applyItem2);
		
		return response;
	}

	private String checkBothAttributes() {
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int initialLength = buffer.length();
		ManualPojo pojo=this.getManualPojo();
		if(pojo.getSourceAccountNumber()==null || "".equalsIgnoreCase(pojo.getSourceAccountNumber()) ) {
			buffer.append("Ewallet Source Account, ");
		} if(pojo.getDestinationAccountNumber() == null || "".equalsIgnoreCase(pojo.getDestinationAccountNumber() )) {
			buffer.append("Destination Equation Account, ");
		}

		if (manualPojo.getChargeAmount() > 0L) {

			if(pojo.getFromEwalletChargeAccount() == null || "".equalsIgnoreCase(pojo.getFromEwalletChargeAccount())) {
				buffer.append("Ewallet Source Charge account, ");
				
			} if(pojo.getToEwalletChargeAccount()== null || "".equalsIgnoreCase(pojo.getToEwalletChargeAccount())) {
				buffer.append("Ewallet Destination Charge account, ");
				
			} 
		}
		
		if(pojo.getSourceEQ3AccountNumber()==null || "".equalsIgnoreCase(pojo.getSourceEQ3AccountNumber()) ) {
			buffer.append("Source Equation Account, ");
		} if(pojo.getDestinationEQ3AccountNumber() == null || "".equalsIgnoreCase(pojo.getDestinationEQ3AccountNumber() )) {
			buffer.append("Destination Equation Account, ");
		}
		
		if (manualPojo.getChargeAmount() > 0L) {

			if(pojo.getFromEQ3ChargeAccount() == null || "".equalsIgnoreCase(pojo.getFromEQ3ChargeAccount())) {
				buffer.append("Equation Source Charge account, ");	
			} 
			
			if(pojo.getToEQ3ChargeAccount() == null || "".equalsIgnoreCase(pojo.getToEQ3ChargeAccount())) {
				buffer.append("Equation Destination account, ");
			}
				
		} 
		
		int length = buffer.toString().length();
				
		if(length > initialLength) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return ResponseCode.E000.toString();
	}

	public ManualPojo getManualPojo() {
		if(manualPojo==null){
			manualPojo= new ManualPojo();
		}
		return manualPojo;
	}


	public void setManualPojo(ManualPojo manualPojo) {
		this.manualPojo = manualPojo;
	}



	public List<SelectItem> getApplyMenu() {
		if(this.applyMenu == null) {
			this.applyMenu = new ArrayList<SelectItem>();
			//JsfUtil.getSelectItemsAsList(zw.co.esolutions.ewallet.process.TransactionClass.values());
			applyMenu.add(new SelectItem("E-Wallet", "E-Wallet"));
			applyMenu.add(new SelectItem("Equation", "Equation"));
			applyMenu.add(new SelectItem("Both", "Both"));
		}
		//manualPojo.setTransactionClass(zw.co.esolutions.ewallet.process.TransactionClass.EQUATION);
		return applyMenu;
	}



	public void setApplyMenu(List<SelectItem> applyMenu) {
		this.applyMenu = applyMenu;
	}


	public String getApplyItem() {
		return applyItem;
	}


	public void setApplyItem(String applyItem) {
		this.applyItem = applyItem;
	}


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public String getNarrative() {
		return narrative;
	}


	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}


	public String getSourceEwalletAccount() {
		return sourceEwalletAccount;
	}


	public void setSourceEwalletAccount(String sourceEwalletAccount) {
		this.sourceEwalletAccount = sourceEwalletAccount;
	}


	public String getDestinationEwalletAccount() {
		return destinationEwalletAccount;
	}


	public void setDestinationEwalletAccount(String destinationEwalletAccount) {
		this.destinationEwalletAccount = destinationEwalletAccount;
	}


	public String getSourceEquationAccount() {
		return sourceEquationAccount;
	}


	public void setSourceEquationAccount(String sourceEquationAccount) {
		this.sourceEquationAccount = sourceEquationAccount;
	}


	public String getDestinationEquationAccount() {
		return destinationEquationAccount;
	}


	public void setDestinationEquationAccount(String destinationEquationAccount) {
		this.destinationEquationAccount = destinationEquationAccount;
	}


	public double getCharge() {
		return charge;
	}


	public void setCharge(double charge) {
		this.charge = charge;
	}
	
	public String cancel(){
		
		
		return "";
	}

	public String getSourceEwalletChargeAccount() {
		return sourceEwalletChargeAccount;
	}

	public void setSourceEwalletChargeAccount(String sourceEwalletChargeAccount) {
		this.sourceEwalletChargeAccount = sourceEwalletChargeAccount;
	}

	public String getDestEWalletChargeAccount() {
		return destEWalletChargeAccount;
	}

	public void setDestEWalletChargeAccount(String destEWalletChargeAccount) {
		this.destEWalletChargeAccount = destEWalletChargeAccount;
	}

	public String getSourceEqationChargeAccount() {
		return sourceEqationChargeAccount;
	}

	public void setSourceEqationChargeAccount(String sourceEqationChargeAccount) {
		this.sourceEqationChargeAccount = sourceEqationChargeAccount;
	}
	
	private ManualPojo formatMobileAccounts(ManualPojo manualPojo) throws Exception {
		if (NumberUtil.validateMobileNumber(manualPojo.getSourceAccountNumber())) {
			manualPojo.setSourceAccountNumber(NumberUtil.formatMobile(manualPojo.getSourceAccountNumber()));
		}
		
		if (NumberUtil.validateMobileNumber(manualPojo.getDestinationAccountNumber())) {
			manualPojo.setDestinationAccountNumber(NumberUtil.formatMobile(manualPojo.getDestinationAccountNumber()));
		}
		
		if (NumberUtil.validateMobileNumber(manualPojo.getFromEwalletChargeAccount())) {
			manualPojo.setFromEwalletChargeAccount(NumberUtil.formatMobile(manualPojo.getFromEwalletChargeAccount()));
		}
		
		if (NumberUtil.validateMobileNumber(manualPojo.getToEwalletChargeAccount())) {
			manualPojo.setToEwalletChargeAccount(NumberUtil.formatMobile(manualPojo.getToEwalletChargeAccount()));
		}
		
		return manualPojo;
	}

	
}
