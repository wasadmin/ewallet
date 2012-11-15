/**
 * 
 */
package zw.co.esolutions.ewallet.tellerweb;

import java.util.Date;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.limitservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.limitservices.service.LimitPeriodType;
import zw.co.esolutions.ewallet.process.ProcessResponse;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.RequestInfo;
import zw.co.esolutions.ewallet.process.TransactionLocationType;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;

/**
 * @author tauttee
 *
 */
public class ConfirmCashWithdrawalBean extends PageCodeBase{
	
	
	private String mobileNumber;
	private Customer customer;
	private BankBranch branch;
	private double amount;
	private boolean disableAmount;
	private BankAccount bankAccount;
	private Limit limit;
	private Limit txnLimit;
	private Limit balLimit;
	private double netCashHeld;
	
	public ConfirmCashWithdrawalBean() {
		super();
		if(this.getMobileNumber() == null) {
			this.setMobileNumber((String)super.getRequestScope().get("mobileNumber"));
			
		}
		amount = 0.00;
	}
	
	
	
	
	
	public double getNetCashHeld() {
		ProcessServiceSOAPProxy proxy = new ProcessServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService= new ProfileServiceSOAPProxy();
		Profile profile=profileService.getProfileByUserName(getJaasUserName());
		
		try {
			long netCash=proxy.getTellerNetCashPosition(profile.getId(), DateUtil.convertToXMLGregorianCalendar(new Date()));
			System.out.println("net cash in cents b4 withdrawal >>>>>"+netCash);
			this.netCashHeld=MoneyUtil.convertToDollars(netCash);
			System.out.println("net cash of teller >>>>>>>>>>>"+netCashHeld);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return netCashHeld;
	}





	public void setNetCashHeld(double netCashHeld) {
		this.netCashHeld = netCashHeld;
	}





	@SuppressWarnings("unchecked")
	public String finish() {
		ProcessResponse processResponse;
		String response = null;
		Profile profile = null;
		String userName = null;
		MobileProfile mobileProfile = null;
		if(this.getAmount() < 0) {
			super.setErrorMessage("You cannot withdraw amount less than USD0.00.");
			return "success";
		}
		System.out.println("withdrawal amount>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+this.getAmount());
		System.out.println("netcash amount>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+this.getNetCashHeld());
		if(this.getAmount()> this.getNetCashHeld()){
			super.setErrorMessage("Teller has insufficent cash to perform this withdraw ("+MoneyUtil.convertCentsToDollarsPatternNoCurrency(this.getAmount() )+"). Cash available: "+MoneyUtil.convertCentsToDollarsPatternNoCurrency(this.getNetCashHeld())+"). Please obtain more float");
			
			return "success";
		}
		RequestInfo info = new RequestInfo();
		try {
			userName = super.getJaasUserName();
			if(userName == null) {
				super.setErrorMessage("Session expired.");
				return "failure";
			}
			profile = super.getProfileService().getProfileByUserName(userName);
			info.setProfileId(profile.getId());
			info.setAmount(MoneyUtil.convertToCents(this.getAmount()));
			info.setSourceMobile(this.getMobileNumber());
			info.setBankCode(this.getBranch().getBank().getCode());
			info.setLocationType(TransactionLocationType.BANK_BRANCH);
			info.setTransactionType(TransactionType.WITHDRAWAL);
			
			
			Bank bank = super.getBankService().findBankBranchById(this.getCustomer().getBranchId()).getBank();
			mobileProfile = super.getCustomerService().getMobileProfileByBankAndMobileNumber(bank.getId(), this.getMobileNumber());
			info.setSourceMobileProfileId(mobileProfile.getId());
			info.setSourceBankId(bank.getId());
			info.setTargetBankId(bank.getId());
			
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> 1 Mobile Profile = "+ mobileProfile);
			response = super.getProcessService().validateEwalletHolderWithdrawal(mobileProfile.getId(), info.getAmount(), bank.getId(), TransactionLocationType.BANK_BRANCH);
			if(!ResponseCode.E000.name().equals(response)){
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>  Response "+ ResponseCode.valueOf(response).getDescription());
				this.setErrorMessage(ResponseCode.valueOf(response).getDescription());
				return "failure";
			}
			
			processResponse = super.getProcessService().processEwalletWithdrawal(info);
		    response = processResponse.getResponseCode();
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error occured. Operation arboted.");
			return "failure";
		}
		if(!response.equalsIgnoreCase(ResponseCode.E000.name())) {
			super.setErrorMessage(ResponseCode.valueOf(response).getDescription());
			return "failure";
		}
		this.setDisableAmount(false);
		super.setInformationMessage("Withdrawal of "+MoneyUtil.convertDollarsToPattern(this.getAmount())+" was successful.");
		
		//Setting Bank Account
		this.getBranch();
		
		if(mobileProfile != null && this.getBankAccount() != null) {
			super.getRequestScope().put("processResponse", processResponse);
			super.getRequestScope().put("mobileProfileId", mobileProfile.getId());
			super.getRequestScope().put("bankAccountId", this.getBankAccount().getId());
			super.getRequestScope().put("header", "Successful Withdrawal Confirmation ");
			super.gotoPage("/teller/confirmTransactions.jspx");
		}
		return "success";
	}
	
	public String back() {
		super.gotoPage("/teller/withdrawCash.jspx");
		return "success";
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Customer getCustomer() {
		if(this.customer == null && this.getMobileNumber() != null) {
			try {
				this.customer = super.getCustomerService().getMobileProfileByMobileNumber(this.getMobileNumber()).getCustomer();
			} catch (Exception e) {
				
			}
		}
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setBranch(BankBranch branch) {
		this.branch = branch;
	}

	public BankBranch getBranch() {
		if(this.branch == null && this.getCustomer() != null) {
			try {
				BankAccount ewalletBankAccount =super.getBankService().getUniqueBankAccountByAccountNumber(getMobileNumber());
				if(ewalletBankAccount==null || ewalletBankAccount.getId()==null){
					ewalletBankAccount=super.getBankService().getBankAccountByAccountHolderAndTypeAndOwnerType(this.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, NumberUtil.formatMobileNumber(this.getMobileNumber()));
				}
				this.setBankAccount(ewalletBankAccount);	
				this.branch = this.getBankAccount().getBranch();
			} catch (Exception e) {
				
			}
		}
		return branch;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getAmount() {
		return amount;
	}

	public void setDisableAmount(boolean disableAmount) {
		this.disableAmount = disableAmount;
	}

	public boolean isDisableAmount() {
		return disableAmount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setLimit(Limit limit) {
		this.limit = limit;
	}

	public Limit getLimit() {
		
		if(this.limit == null && this.getBankAccount() != null) {
			try {
				this.limit = super.getLimitService().getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.WITHDRAWAL,
						BankAccountClass.valueOf(this.getBankAccount().getAccountClass().toString()),
						DateUtil.convertToXMLGregorianCalendar(new Date()), LimitPeriodType.DAILY, 
						this.getBankAccount().getBranch().getBank().getId());
				
			} catch (Exception e) {
				
			}
		}
		return limit;
	}

	/**
	 * @param tLimit the tLimit to set
	 */
	public void setTxnLimit(Limit txnLimit) {
		this.txnLimit = txnLimit;
	}

	/**
	 * @return the tLimit
	 */
	public Limit getTxnLimit() {
		if(this.txnLimit == null && this.getBankAccount() != null) {
			try {
				this.txnLimit = super.getLimitService().getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.WITHDRAWAL,
						BankAccountClass.valueOf(this.getBankAccount().getAccountClass().toString()),
						DateUtil.convertToXMLGregorianCalendar(new Date()), LimitPeriodType.TRANSACTION, 
						this.getBankAccount().getBranch().getBank().getId());
			} catch (Exception e) {
				
			}
		}
		return txnLimit;
	}

	/**
	 * @param balLimit the balLimit to set
	 */
	public void setBalLimit(Limit balLimit) {
		this.balLimit = balLimit;
	}

	/**
	 * @return the balLimit
	 */
	public Limit getBalLimit() {
		if(this.balLimit == null && this.getBankAccount() != null) {
			try {
				this.balLimit = super.getLimitService().getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE,
						BankAccountClass.valueOf(this.getBankAccount().getAccountClass().toString()),
						DateUtil.convertToXMLGregorianCalendar(new Date()), LimitPeriodType.DAILY, 
						this.getBankAccount().getBranch().getBank().getId());
				if(this.balLimit == null) {
					this.balLimit = super.getLimitService().getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE,
							BankAccountClass.valueOf(this.getBankAccount().getAccountClass().toString()),
							DateUtil.convertToXMLGregorianCalendar(new Date()), LimitPeriodType.TRANSACTION, 
							this.getBankAccount().getBranch().getBank().getId());
				}
			} catch (Exception e) {
				
			}
		}
		return balLimit;
	}
	
	

}
