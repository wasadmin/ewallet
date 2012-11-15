package zw.co.esolutions.ussd.web.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.agentservice.service.proxy.AgentServiceProxy;
import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.bankservices.proxy.BankServiceProxy;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.BankStatus;
import zw.co.esolutions.ewallet.customerservices.proxy.CustomerServiceProxy;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.msg.MobileWebRequestMessage;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.mobile.thread.utils.MobileWebMessageSenderThread;
import zw.co.esolutions.ussd.entities.USSDTransaction;
import zw.co.esolutions.ussd.entities.WebSession;
import zw.co.esolutions.ussd.mobile.banking.conf.MobileBankingUSSDConfiguration;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;

/**
 * Session Bean implementation class USSDServiceImpl
 */
@WebService(serviceName = "MobileCommerceService", endpointInterface = "zw.co.esolutions.ussd.web.services.MobileCommerceService", 
		portName = "MobileCommerceServiceSOAP")
@Stateless
public class MobileCommerceServiceImpl implements MobileCommerceService {
	
	static Logger logger = LoggerFactory.getLogger(MobileCommerceServiceImpl.class);
	
	@PersistenceContext
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public MobileCommerceServiceImpl() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public USSDTransaction createTransaction(USSDTransaction transaction)
			{
		transaction.setDateCreated(new Date(System.currentTimeMillis()));
		em.persist(transaction);
		return transaction;
	}

	@Override
	public void deleteTransaction(String uuid) {
		USSDTransaction txn = em.find(USSDTransaction.class, uuid);
		txn = em.merge(txn);
		em.remove(txn);
		
	}

	@Override
	public USSDTransaction findTransaction(String uuid) {
		USSDTransaction txn = em.find(USSDTransaction.class, uuid);
		return txn;
	}

	@Override
	public USSDTransaction getTransactionByUSSDSessionId(String sessionId) {
		USSDTransaction txn = null;
		try {
			Query query = em.createQuery("select u from USSDTransaction u where u.sessionId = :sessionId");
			query.setParameter("sessionId", sessionId);
			txn = (USSDTransaction)query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
		return txn;
	}

	@Override
	public USSDTransaction updateTransaction(USSDTransaction transaction)
			{
		transaction = em.merge(transaction);
		return transaction;
	}
	
	@Override
	public WebSession createWebSession(WebSession webSession)
			{
		webSession.setDateCreated(new Date(System.currentTimeMillis()));
		if(webSession.getId() == null) {
			webSession.setId(GenerateKey.generateEntityId());
		}
		em.persist(webSession);
		return webSession;
	}

	@Override
	public void deleteWebSession(String id) {
		WebSession txn = em.find(WebSession.class, id);
		txn = em.merge(txn);
		em.remove(txn);
		
	}

	@Override
	public WebSession findWebSessionById(String id) {
		WebSession txn = em.find(WebSession.class, id);
		return txn;
	}

	@Override
	public WebSession getFailedWebSession(String mobileNumber, String bankId) {
		WebSession txn = null;
		try {
			Query query = em.createQuery("select u from WebSession u where u.mobileNumber = :mobileNumber AND u.bankId = :bankId AND u.status = 'FAILED'");
			query.setParameter("mobileNumber", mobileNumber);
			query.setParameter("bankId", bankId);
			txn = (WebSession)query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
		return txn;
	}

	@Override
	public WebSession getWebSessionByMobileAndBankAndStatus(String mobileNumber, String bankId, String status) {
		WebSession txn = null;
		try {
			Query query = em.createQuery("select u from WebSession u where u.mobileNumber = :mobileNumber AND u.bankId = :bankId AND u.status = :status");
			query.setParameter("mobileNumber", mobileNumber);
			query.setParameter("bankId", bankId);
			query.setParameter("status", status);
			txn = (WebSession)query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
		return txn;
	}
	
	@Override
	public WebSession getWebSessionByReferenceId(String referenceId) {
		WebSession txn = null;
		try {
			Query query = em.createQuery("select u from WebSession u where u.referenceId = :referenceId");
			query.setParameter("referenceId", referenceId);
			txn = (WebSession)query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
		return txn;
	}
	
	@Override
	public WebSession updateWebSession(WebSession webSession)
			{
		webSession = em.merge(webSession);
		return webSession;
	}
	
	public boolean sendTransaction(MobileWebRequestMessage mobileWebReq) {
		logger.debug("###########&&&& MobileWeb Props : \n\t { Source Mobile "+mobileWebReq.getSourceMobileNumber() +"\n\t" +
				"Transaction Type = "+mobileWebReq.getTransactionType()+"\n\t" +
						"Src Account = "+mobileWebReq.getSourceBankAccount()+"\n\t" +
						"Src BankId = "+mobileWebReq.getSourceBankId()+"\n\t" +
						"Amount = "+mobileWebReq.getAmount()+"\n\t" +
						"Session Id = "+mobileWebReq.getUuid()+"\n\t" +
						"Target Mobile = "+mobileWebReq.getTargetMobileNumber()+"\n\t" +
						"Target Bank Id = "+mobileWebReq.getTargetBankId()+"\n\t" +
						"Target Account = "+mobileWebReq.getTargetBankAccount()+"\n\t" +
						"Transaction Location Type = "+mobileWebReq.getTransactionLocationType()+"\n\t" +
						"MNO = "+mobileWebReq.getMno()+" } \n\t");
		boolean isSent = false;
		try {
			isSent = sendMessage(mobileWebReq);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSent;
	}
	
	private boolean sendMessage(MobileWebRequestMessage mobileWebReq){
		logger.debug(">>>>>>>>>>> Send Message to Switch");
		try{
			new Thread(new MobileWebMessageSenderThread(mobileWebReq)).start();
		}catch(Exception e){
			return false;
		}
		logger.debug(">>>>>>>>>>> Send Message to Switch");
		return true;
	}
	
	public String getTargetBankCodeForTargetAccount(String sessionId, String targetAccount, String targetMobile) {
		//logger.debug(">>>>>>>>>>>>>>>>>>>>>> Now Calling getTargetBankCodeForTargetAccount ");
		String bankCode = null;
		String bankId;
		String accountNumber;
		MobileProfile mobileProfile;
		boolean isEWallet = false;
		try {
			CustomerServiceSOAPProxy customerService = CustomerServiceProxy.getInstance();
			
			USSDTransaction txn = this.getTransactionByUSSDSessionId(sessionId);
			//logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Txn = "+txn);
			
			MobileBankingUSSDConfiguration confi = MobileBankingUSSDConfiguration.getInstance(txn.getBankCode());
			//logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Conf = "+confi);
			
			bankCode = confi.getStringValueOf(SystemConstants.BANK_CODE_MAIN);
			//logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> Main Bank Code = "+bankCode);
			
			bankId = getBankId(bankCode);
			//logger.debug(">>>>>>>>>>>>>>>>>>>>>>> Main Bank ID = "+bankId);
			
			accountNumber =targetAccount != null ?  targetAccount : targetMobile;
			//logger.debug(">>>>>>>>>>>>>>>>>>>>>>> Account Number = "+accountNumber);
			
			String mobileNumber = null;
			 try {
					mobileNumber =  NumberUtil.formatMobileNumber(accountNumber);
					isEWallet = true;
			 } catch (Exception e) {
				e.printStackTrace();
			}
			 
			 //logger.debug(">>>>>>>>>>>>>>>>>>>>> Mobile Number = "+mobileNumber);
			 if(isEWallet) {
				
				 //This is ewallet now find MobileProfile
				mobileProfile = customerService.getMobileProfileByBankIdMobileNumberAndStatus(bankId, mobileNumber, MobileProfileStatus.ACTIVE);
				//logger.debug(">>>>>>>>>>>>>>>>>>>> Mobile Profile = "+mobileProfile);
				
				if(mobileProfile == null || mobileProfile.getId() == null) {
					bankId = null;
					bankCode = null;
					
					bankCode = confi.getStringValueOf(SystemConstants.BANK_CODE_SUB);
					//logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Sub Bank Code = "+bankCode);
					
					bankId = getBankId(bankCode);
					//logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Sub Bank Id = "+bankId);
					
					mobileProfile = customerService.getMobileProfileByBankIdMobileNumberAndStatus(bankId, mobileNumber, MobileProfileStatus.ACTIVE);
					//logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>> Mobile Profile = "+mobileProfile);
					
					if(mobileProfile == null || mobileProfile.getId() == null) {
						bankId = null;
						bankCode = null;
					}
				}
				
								
			} else {
				
				//It's a bank account
				
				//It's a bank account But still has issues 
				BankServiceSOAPProxy bankService = BankServiceProxy.getInstance();
				BankAccount account = bankService.getUniqueBankAccountByAccountNumberAndBankId(accountNumber, bankId);
				
				if(account == null  || account.getId() == null){
					
					bankCode = null;
					bankId = null;
					
					bankCode = getBankId(confi.getStringValueOf(SystemConstants.BANK_CODE_SUB));
					bankId = getBankId(bankCode);
					
					account = bankService.getUniqueBankAccountByAccountNumberAndBankId(accountNumber, bankId);
														
				} 
				
				if(account == null || account.getId() == null) {
					bankCode = null;
					bankId = null;
				} else {
					Customer customer = customerService.findCustomerById(account.getAccountHolderId());
					String branchId = customer.getBranchId();
					BankBranch bankBranch = bankService.findBankBranchById(branchId);
					bankId = bankBranch.getBank().getId();
					bankCode = bankBranch.getBank().getCode();
				}
				
			}
			
					
		} catch (Exception e) {
			e.printStackTrace();
			bankCode = null;
			bankId = null;
		}
		//logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Returning getTargetBankCodeForTargetAccount");
		return bankCode;
	
	}
	
	public boolean isAgentMobile(String bankId, String formattedMobileNumber) {
		try {
			 bankId = getBankId(bankId);
			logger.debug("Validating agent registration");
			MobileProfile mobileProfile = CustomerServiceProxy.getInstance().getMobileProfileByBankAndMobileNumber(bankId, formattedMobileNumber);
			if (mobileProfile != null) {
				logger.debug("Customer is of class : " + mobileProfile.getCustomer().getCustomerClass());
				if (CustomerClass.AGENT.equals(mobileProfile.getCustomer().getCustomerClass())) {
					logger.debug("Customer is an AGENT.. going on");
					return true;			
				} else {
					logger.debug("Customer is not an agent.. REJECT");
					return false;
				}
			} else {
				logger.debug("MobileProfile not found..");
				return false;
			}
		} catch (Exception e) {
			logger.error("Exception occured " + e);
			return false;
		}
	}
	
	public String validateAgentDeposit(String sourceAccount, String targetMobile) {
		 try {
			targetMobile = NumberUtil.formatMobileNumber(targetMobile);
		} catch (Exception e) {
			return SystemConstants.AGENT_AUTH_DEPOSIT_CASH+" \nTarget mobile "+targetMobile+" is invalid. \nEnter Target Mobile";
		}
		if(sourceAccount.equalsIgnoreCase(targetMobile)) {
			logger.debug("Src and Dest accounts are the same... REJECT");
			return SystemConstants.AGENT_AUTH_DEPOSIT_CASH+" \nAgent cannot deposit into own agent account. \nEnter Target Mobile";
		} 
		MobileProfile mobile = getMobileProfileByMobileNumber(targetMobile);
		logger.debug("Checking if target is not agent mobile.."+mobile);
		if (isAgentMobile(mobile.getBankId(), targetMobile)) {
			logger.debug("Agent is depositing at another agent.. REJECT");
			return SystemConstants.AGENT_AUTH_DEPOSIT_CASH+" \nAn agent can only deposit at the bank. \nEnter Target Mobile";
		}
		
		return ResponseCode.E000.name();
		
	}
	
	public String validateAgentTransfer(String sourceAccount, String targetAccount, String bankCode) {
		 String bankId = null;
		try {
			 bankId = getBankId(bankCode);
			 targetAccount = NumberUtil.formatMobileNumber(targetAccount);
			 if(sourceAccount.equalsIgnoreCase(targetAccount)) {
					logger.debug("Src and Dest accounts are the same... REJECT");
					return SystemConstants.AGENT_AUTH_SEND_MONEY+" \nAgent cannot transfer into own agent-ewallet account. \nEnter Target Account";
			  }
			return SystemConstants.AGENT_AUTH_SEND_MONEY+" \nTarget account cannot be a mobile. \nEnter Target Account";
		} catch (Exception e) {
			//Do nothing, it must be a bankaccount
		}
		 
		MobileProfile agentProfile = CustomerServiceProxy.getInstance().getMobileProfileByBankAndMobileNumber(bankId, sourceAccount);
		BankAccount equationAccount = BankServiceProxy.getInstance().getUniqueBankAccountByAccountNumberAndBankId(targetAccount, bankId);
		
		if (equationAccount == null || equationAccount.getId() == null) {
			logger.debug("Target BankAccount is not registered on agent profile.. REJECT");
			return SystemConstants.AGENT_AUTH_SEND_MONEY+" \nTarget account is not registered on your agent profile. \nEnter Target Account";
		} else {
			logger.debug("Target BankAccount found.. checking if it belongs to Agent..");
			if (equationAccount.getAccountHolderId().equals(agentProfile.getCustomer().getId())) {
				logger.debug("BankAccount belongs to agent.. allow transfer..");
				
			} else {
				logger.debug("BankAccount does not belongs to agent.. REJECT");
				return SystemConstants.AGENT_AUTH_SEND_MONEY+" \nAgent cannot transfer to third party accounts. \nEnter Target Account";
			}
		}
		return ResponseCode.E000.name();
		
	}
	
	
	public String validateAgentCustomerWithdrawal(String agentNumber) {
		Agent agent = AgentServiceProxy.getInstance().getAgentByAgentNumber(agentNumber);
		
		if (agent == null || agent.getId() == null) {
			return SystemConstants.AGENT_AUTH_WITHDRAW_CASH+" \nAgent " + agentNumber + " is not registered \nEnter Agent Number";

		}
		
		return ResponseCode.E000.name();
		
	}
	
	public String getAgentNumberByMobileNumberAndBankId(String mobileNumber, String bankCode) {
		String bankId;
		try {
			bankId = getBankId(bankCode);
			MobileProfile mobileProfile = CustomerServiceProxy.getInstance().getMobileProfileByBankAndMobileNumber(bankId, mobileNumber);
			Agent agent = AgentServiceProxy.getInstance().getAgentByCustomerId(mobileProfile.getCustomer().getId());
			return agent.getAgentNumber();
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isNonHolderAccount(String targetAccount, String bankCode) {
		boolean nonHolder = false;
		try {
			String bankId = null;
			BankAccount acc = null;
			try {
				MobileBankingUSSDConfiguration config = MobileBankingUSSDConfiguration.getInstance(bankCode);
				bankId = getBankId(config.getStringValueOf(SystemConstants.BANK_CODE_MAIN));
				acc = new BankServiceSOAPProxy().getUniqueBankAccountByAccountNumberAndBankId(targetAccount, bankId);
				if(acc == null || acc.getId() == null) {
					bankId = getBankId(config.getStringValueOf(SystemConstants.BANK_CODE_SUB));
					acc = new BankServiceSOAPProxy().getUniqueBankAccountByAccountNumberAndBankId(targetAccount, bankId);
				}
			} catch (Exception e) {
				return false;
			}
			if (acc == null || acc.getId() == null) {
				nonHolder = true;
			}
		} catch (Exception e) {
			return false;
		}
		return nonHolder;
	}
	
	public List<String> getActiveBankNames() {
		List<String> banks = new ArrayList<String>();
		try {
			List<Bank> bankList = BankServiceProxy.getInstance().getBankByStatus(BankStatus.ACTIVE);
			for(Bank b : bankList) {
				banks.add(b.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return banks;
	}
	
	
	private MobileProfile getMobileProfileByMobileNumber(String mobileNumber) {
		try {
			MobileProfile mobileProfile = CustomerServiceProxy.getInstance().getMobileProfileByMobileNumber(mobileNumber);
			return mobileProfile;
		} catch (Exception e) {
			return null;
		}
	}


	private String getBankId(String bankCode) {
		String bankId = null;
		try {
			bankId = BankServiceProxy.getInstance().getBankByCode(bankCode).getId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bankId;
	}
	
	

}
