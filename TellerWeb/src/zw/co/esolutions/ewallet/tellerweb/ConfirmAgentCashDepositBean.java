package zw.co.esolutions.ewallet.tellerweb;

import java.util.Date;

import pagecode.PageCodeBase;

import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.limitservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.limitservices.service.LimitPeriodType;
import zw.co.esolutions.ewallet.limitservices.service.TransactionType;
import zw.co.esolutions.ewallet.process.DepositInfo;
import zw.co.esolutions.ewallet.process.ProcessResponse;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.TransactionLocationType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;

public class ConfirmAgentCashDepositBean extends PageCodeBase{
	
	private String mobileNumber;
	private Customer customer;
	private BankBranch branch;
	private Agent agent;
	private double amount;
	private boolean disableAmount;
	private BankAccount bankAccount;
	private Limit limit;
	private Limit deDailyLimit;
	private Limit deTxnLimit;
	
	
	public ConfirmAgentCashDepositBean() {
		super();
		if(this.getMobileNumber() == null) {
			this.setMobileNumber((String)super.getRequestScope().get("mobileNumber"));
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public String finish() {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		ProcessServiceSOAPProxy processService = new ProcessServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		
		ProcessResponse processResponse;
		String response = null;
		Profile profile = null;
		String userName = null;
		MobileProfile mobileProfile = null;
		if(this.getAmount() < 0) {
			super.setErrorMessage("You cannot deposit amount less than 0.");
			return "success";
		}
		
		if((response = this.validateDepositAmount()) != null) {
			super.setErrorMessage(response);
			return "failure";
		}
		DepositInfo info = new DepositInfo();
		try {
			userName = super.getJaasUserName();
			if(userName == null) {
				super.setErrorMessage("Session expired.");
				return "failure";
			}
			profile = profileService.getProfileByUserName(userName);
			info.setProfileId(profile.getId());
			info.setAmount(MoneyUtil.convertToCents(this.getAmount()));
			info.setMobileNumber(this.getMobileNumber());
			info.setBankCode(this.getBranch().getBank().getCode());
			info.setRunningBalance(this.getBankAccount().getRunningBalance());
			
			mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(this.getBranch().getBank().getId(), info.getMobileNumber());
			
			response = processService.validateAgentDeposit(mobileProfile.getId(), info.getAmount(), this.getBranch().getBank().getId(), TransactionLocationType.BANK_BRANCH);
			
			if(!ResponseCode.E000.name().equalsIgnoreCase(response)) {
				super.setErrorMessage(response);
				return "failure";
			}
			processResponse = processService.depositAgentCash(info);
			response = processResponse.getResponseCode();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			super.setErrorMessage("An error occured. Operation arboted.");
			return "failure";
		}
		if(!ResponseCode.E000.name().equalsIgnoreCase(response)) {
			super.setErrorMessage(ResponseCode.valueOf(response).getDescription());
			return "failure";
		}
		//Setting Bank Account
		this.getBranch();
		
		
		this.setDisableAmount(false);
		super.setInformationMessage("Deposit of "+MoneyUtil.convertDollarsToPattern(this.getAmount())+" was successful.");
		if(mobileProfile != null && this.getBankAccount() != null) {
			super.getRequestScope().put("processResponse", processResponse);
			super.getRequestScope().put("mobileProfileId", mobileProfile.getId());
			super.getRequestScope().put("bankAccountId", this.getBankAccount().getId());
			super.getRequestScope().put("header", "Successful Deposit Confirmation ");
			super.gotoPage("/teller/confirmTransactions.jspx");
		}
		return "success";
	}
	
	public String back() {
		super.gotoPage("/teller/agentCashDeposit.jspx");
		return "success";
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Customer getCustomer() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		if(this.customer == null && this.getMobileNumber() != null) {
			try {
				this.customer = super.getCustomerService().getMobileProfileByMobileNumber(this.getMobileNumber()).getCustomer();
				this.agent = agentService.getAgentByCustomerId(customer.getId());
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
					ewalletBankAccount=super.getBankService().getBankAccountByAccountHolderAndTypeAndOwnerType(this.getCustomer().getId(), BankAccountType.AGENT_EWALLET, OwnerType.AGENT, this.getMobileNumber());
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
		System.out.println("BankAccount : " + this.getBankAccount());
		if(this.limit == null && this.getBankAccount() != null) {
			System.out.println("Going forward....");
			try {
				this.limit = super.getLimitService().getValidLimitOnDateByBankId(TransactionType.BALANCE,
						BankAccountClass.valueOf(this.getBankAccount().getAccountClass().toString()),
						DateUtil.convertToXMLGregorianCalendar(new Date()), LimitPeriodType.DAILY, 
						this.getBankAccount().getBranch().getBank().getId());
				if(this.limit == null) {
					this.limit = super.getLimitService().getValidLimitOnDateByBankId(TransactionType.BALANCE,
							BankAccountClass.valueOf(this.getBankAccount().getAccountClass().toString()),
							DateUtil.convertToXMLGregorianCalendar(new Date()), LimitPeriodType.TRANSACTION, 
							this.getBankAccount().getBranch().getBank().getId());
				}
				System.out.println("Limit :::::::::::::::"+limit);
			} catch (Exception e) {
				System.out.println("Exception thrown : " + e.getMessage());
			}
		}
		System.out.println("Limit : " + limit);
		return limit;
	}
	
	private String validateDepositAmount() {
		this.getCustomer();
		this.getBranch();
		if(this.getLimit() != null && this.getLimit().getId() != null) {
			if(this.getLimit().getMaxValue() < (MoneyUtil.convertToCents(this.getAmount()) + this.getBankAccount().getRunningBalance())) {
				return "Deposit amount "+getAmount()+" is exceeding maximum balance by "+MoneyUtil.convertCentsToDollarsPattern(
						(this.getBankAccount().getRunningBalance() + MoneyUtil.convertToCents(this.getAmount()))-this.getLimit().getMaxValue())+
						". You can deposit "+MoneyUtil.convertCentsToDollarsPattern(this.getLimit().getMaxValue() - this.getBankAccount().getRunningBalance())+" only.";
			}
		} else {
			return "No Balance Limit (Transaction or Daily) Set.";
		}
		return null;
	}

	/**
	 * @param dDailyLimit the dDailyLimit to set
	 */
	public void setDeDailyLimit(Limit deDailyLimit) {
		this.deDailyLimit = deDailyLimit;
	}

	/**
	 * @return the dDailyLimit
	 */
	public Limit getDeDailyLimit() {
		if(this.deDailyLimit == null && this.getBankAccount() != null) {
			try {
				this.deDailyLimit = super.getLimitService().getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.AGENT_CASH_DEPOSIT,
						BankAccountClass.valueOf(this.getBankAccount().getAccountClass().toString()),
						DateUtil.convertToXMLGregorianCalendar(new Date()), LimitPeriodType.DAILY, 
						this.getBankAccount().getBranch().getBank().getId());
			} catch (Exception e) {
				
			}
		}
		return deDailyLimit;
	}

	/**
	 * @param dTxnLimit the dTxnLimit to set
	 */
	public void setDepositTxnLimit(Limit deTxnLimit) {
		this.deTxnLimit = deTxnLimit;
	}

	/**
	 * @return the dTxnLimit
	 */
	public Limit getDeTxnLimit() {
		if(this.deTxnLimit == null && this.getBankAccount() != null) {
			try {
				this.deTxnLimit = super.getLimitService().getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.AGENT_CASH_DEPOSIT,
						BankAccountClass.valueOf(this.getBankAccount().getAccountClass().toString()),
						DateUtil.convertToXMLGregorianCalendar(new Date()), LimitPeriodType.TRANSACTION, 
						this.getBankAccount().getBranch().getBank().getId());
			} catch (Exception e) {
				
			}
		}
		return deTxnLimit;
	}

	public Agent getAgent() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		if(this.customer == null && this.getMobileNumber() != null) {
			try {
				this.customer = super.getCustomerService().getMobileProfileByMobileNumber(this.getMobileNumber()).getCustomer();
				this.agent = agentService.getAgentByCustomerId(customer.getId());
			} catch (Exception e) {
				
			}
		}
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
}
