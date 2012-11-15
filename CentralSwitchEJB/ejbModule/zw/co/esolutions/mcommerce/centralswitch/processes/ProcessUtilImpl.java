package zw.co.esolutions.mcommerce.centralswitch.processes;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.centralswitch.config.service.ConfigInfo;
import zw.co.esolutions.centralswitch.config.service.SwitchConfigurationServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankBranchStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.enums.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.msg.SynchronisationResponse;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.centralswitch.util.CentralSwitchXMLUtils;
import zw.co.esolutions.mcommerce.msg.Agent;
import zw.co.esolutions.mcommerce.msg.Bank;
import zw.co.esolutions.mcommerce.msg.BankAccount;
import zw.co.esolutions.mcommerce.msg.BankBranch;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand.Enum;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessageDocument;
import zw.co.esolutions.mcommerce.msg.Customer;
import zw.co.esolutions.mcommerce.msg.CustomerMerchant;
import zw.co.esolutions.mcommerce.msg.Merchant;
import zw.co.esolutions.mcommerce.msg.Merchant.Status;
import zw.co.esolutions.mcommerce.msg.MobileProfile;
import zw.co.esolutions.topup.ws.impl.MnoName;
import zw.co.esolutions.topup.ws.impl.ServiceCommand;
import zw.co.esolutions.topup.ws.impl.TopupWebServicePortProxy;
import zw.co.esolutions.topup.ws.impl.WsRequest;
import zw.co.esolutions.topup.ws.impl.WsResponse;

/**
 * Session Bean implementation class ProcessUtilImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ProcessUtilImpl implements ProcessUtil {

	public ProcessUtilImpl() {

	}

	@Resource(mappedName = "jms/EWalletQCF")
	private QueueConnectionFactory jmsQueueConnectionFactory;

	@Resource(mappedName = EWalletConstants.ECONET_SMS_OUT_QUEUE)
	private Queue smsReplyQueue;

	private Connection jmsConnection;

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(ProcessUtilImpl.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ProcessUtilImpl.class);
		}
	}

	@PostConstruct
	protected void initialise() {
		try {
			jmsConnection = jmsQueueConnectionFactory.createConnection();
		} catch (JMSException e) {
			LOG.fatal("Failed to initialise MDB " + this.getClass().getSimpleName(), e);
			e.printStackTrace(System.err);
			throw new EJBException(e);
		}
	}

	@PreDestroy
	public void cleanUp() {
		if (jmsConnection != null) {
			try {
				jmsConnection.close();
			} catch (JMSException e) {
				// Ignore this guy
			}
		}
	}

	public SynchronisationResponse processRegMessage(BankRegistrationMessage bankRegistrationMessage, BankRegistrationMessage.MessageCommand.Enum action) throws Exception {
		SynchronisationResponse response = null;

		if (bankRegistrationMessage.getBank() != null && bankRegistrationMessage.getBank().getId() != null) {
			response = this.processBank(bankRegistrationMessage.getBank(), action);
		} else if (bankRegistrationMessage.getBankBranch() != null && bankRegistrationMessage.getBankBranch().getId() != null) {
			response = this.processBankBranch(bankRegistrationMessage.getBankBranch(), action);
		} else if (bankRegistrationMessage.getCustomer() != null && bankRegistrationMessage.getCustomer().getId() != null) {
			response = this.processCustomer(bankRegistrationMessage.getCustomer(), action);
		} else if (bankRegistrationMessage.getMobileProfile() != null && bankRegistrationMessage.getMobileProfile().getId() != null) {
			response = this.processMobileProfile(bankRegistrationMessage.getMobileProfile(), action);
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Response = " + response);
			MobileProfile mp = bankRegistrationMessage.getMobileProfile();
			String notification = mp.getNotification();
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Notification = " + notification);
			if (notification != null && (response != null && ResponseCode.E000.name().equalsIgnoreCase(response.getResponseCode()))) {
				String mobileNumber = mp.getMobileNumber();
				LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Mobile Numnber = " + mobileNumber);
				MessageSender.sendSMSToMobileDestination(jmsConnection, notification, mobileNumber, smsReplyQueue, LOG);

			}

		} else if (bankRegistrationMessage.getBankAccount() != null && bankRegistrationMessage.getBankAccount().getId() != null) {
			response = this.processBankAccount(bankRegistrationMessage.getBankAccount(), action);
		} else if (bankRegistrationMessage.getBankMerchant() != null && bankRegistrationMessage.getBankMerchant().getId() != null) {
			response = this.processBankMerchant(bankRegistrationMessage.getBankMerchant(), action);
		} else if (bankRegistrationMessage.getCustomerMerchant() != null && bankRegistrationMessage.getCustomerMerchant().getId() != null) {
			 response = this.processCustomerMerchant(bankRegistrationMessage.getCustomerMerchant(), action);
		} else if (bankRegistrationMessage.getMerchant() != null && bankRegistrationMessage.getMerchant().getId() != null) {
			response = this.processMerchant(bankRegistrationMessage.getMerchant(), action);
		} else if (bankRegistrationMessage.getAgent() != null && bankRegistrationMessage.getAgent().getId() != null) {
			response = this.processAgent(bankRegistrationMessage.getAgent(), action);
		}
		return response;
	}

	

	private SynchronisationResponse processCustomerMerchant(CustomerMerchant xmlCustomerMerchant, Enum action) {

		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();

		LOG.debug("Procesing CUSTOMER MERCHANT REG........");
		SynchronisationResponse response = new SynchronisationResponse();
		if (xmlCustomerMerchant == null) {
			return null;
		}

		zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant existingCustomerMerchant = merchantService.findCustomerMerchantById(xmlCustomerMerchant.getId());
		if (existingCustomerMerchant == null || existingCustomerMerchant.getId() == null) {
			// do a create
			action = BankRegistrationMessage.MessageCommand.CREATE;
		} else {
			action = BankRegistrationMessage.MessageCommand.UPDATE;
			if(CustomerMerchantStatus.DELETED.equals(existingCustomerMerchant.getStatus())) {
				LOG.debug("CUSTOMER MERCHANT REG already deleted.");
				//Ignore this
				return null;
			}
		}

		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			LOG.debug("FOUND A CREATE CUSTOMER MERCHANT ");
			zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant customerMerchant = new zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant();
			customerMerchant.setId(xmlCustomerMerchant.getId());
			customerMerchant = CentralSwitchXMLUtils.copyCustomerMerchant(xmlCustomerMerchant, customerMerchant);

			try {
				LOG.debug("Create a CUSTOMER merchant with ID " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId());
				return this.createCustomerMerchant(customerMerchant, xmlCustomerMerchant);
			} catch (Exception e) {
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action)) {
			LOG.debug("FOUND AN UPDATE CUSTOMER MERCHANT");
			try {
				existingCustomerMerchant = merchantService.findCustomerMerchantById(xmlCustomerMerchant.getId());
				if (existingCustomerMerchant != null && existingCustomerMerchant.getId() != null) {
					existingCustomerMerchant = CentralSwitchXMLUtils.copyCustomerMerchant(xmlCustomerMerchant, existingCustomerMerchant);

					zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant editedCustomerMerchant = merchantService.editCustomerMerchant(existingCustomerMerchant, EWalletConstants.SYSTEM);
					if (editedCustomerMerchant == null || editedCustomerMerchant.getId() == null) {
						response.setNarrative("Failed to update CUSTOMER MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("CUSTOMER MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId() + " has been updated successfully");
					LOG.debug(response.getNarrative());
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					existingCustomerMerchant = new zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant();
					existingCustomerMerchant.setId(xmlCustomerMerchant.getId());
					existingCustomerMerchant = CentralSwitchXMLUtils.copyCustomerMerchant(xmlCustomerMerchant, existingCustomerMerchant);
					return this.createCustomerMerchant(existingCustomerMerchant, xmlCustomerMerchant);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update CUSTOMER MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId());
				response.setResponseCode("110");
				return response;
			}
		} else if (BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant customerMerchant = merchantService.findCustomerMerchantById(xmlCustomerMerchant.getId());
				if (customerMerchant != null && customerMerchant.getId() != null) {
					customerMerchant = CentralSwitchXMLUtils.copyCustomerMerchant(xmlCustomerMerchant, customerMerchant);
					zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant editedCustomerMerchant = merchantService.editCustomerMerchant(customerMerchant, EWalletConstants.SYSTEM);
					if (editedCustomerMerchant == null || editedCustomerMerchant.getId() == null) {
						response.setNarrative("Failed to update CUSTOMER MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId() + ". Web service exception");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("CUSTOMER MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					response.setNarrative("CUSTOMER MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update CUSTOMER MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId());
				response.setResponseCode("110");
				return response;
			}
		}
		return null;	
	}

	
	private SynchronisationResponse createCustomerMerchant(zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant customerMerchant, CustomerMerchant xmlCustomerMerchant) throws Exception {
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
		SynchronisationResponse response = new SynchronisationResponse();
		zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant existing = merchantService.findCustomerMerchantById(xmlCustomerMerchant.getId());

		if (existing == null || existing.getId() == null) {
			zw.co.esolutions.ewallet.merchantservices.service.BankMerchant bankMerchant = merchantService.findBankMerchantById(xmlCustomerMerchant.getBankMerchantId());
			// we find the branch
			if (bankMerchant == null || bankMerchant.getId() == null) {
				response.setNarrative("No Bank Merchant, Failed to create bank merchant "  + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getBankId());
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}

			customerMerchant = CentralSwitchXMLUtils.copyCustomerMerchant(xmlCustomerMerchant, customerMerchant);
			customerMerchant.setBankMerchant(bankMerchant);
						
			customerMerchant = merchantService.createCustomerMerchant(customerMerchant, EWalletConstants.SYSTEM);
			
			if (customerMerchant == null || customerMerchant.getId() == null) {
				response.setNarrative("Failed to create CUSTOMER MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId());
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}
			response.setNarrative("CUSTOMER  MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId() + " has been registered successfully.");
			response.setResponseCode(ResponseCode.E000.name());
			return response;
		} else {
			// bank account already exist, we can't do a create
			response.setNarrative("CUSTOMER  MERCHANT " + xmlCustomerMerchant.getId() + " | " + xmlCustomerMerchant.getBankMerchantId() + "|" + xmlCustomerMerchant.getCustomerId() + " has cannot be created. Other one already created.");
			response.setResponseCode(ResponseCode.E505.name());
			return response;
		}
	
	}

	private SynchronisationResponse processMerchant(Merchant xmlMerchant, Enum action) {

		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();

		LOG.debug("Procesing BANK MERCHANT REG........");
		SynchronisationResponse response = new SynchronisationResponse();
		if (xmlMerchant == null) {
			return null;
		}

		zw.co.esolutions.ewallet.merchantservices.service.Merchant existingMerchant = merchantService.findMerchantById(xmlMerchant.getId());
		if (existingMerchant == null || existingMerchant.getId() == null) {
			// do a create
			action = BankRegistrationMessage.MessageCommand.CREATE;
		} else {
			action = BankRegistrationMessage.MessageCommand.UPDATE;
			if(BankMerchantStatus.DELETED.toString().equalsIgnoreCase(existingMerchant.getStatus().toString())) {
				LOG.debug("MERCHANT already deleted.");
				//Ignore this
				return null;
			}
		}

		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			LOG.debug("FOUND A CREATE MERCHANT ");
			zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant = new zw.co.esolutions.ewallet.merchantservices.service.Merchant();
			merchant.setId(xmlMerchant.getId());
			merchant = CentralSwitchXMLUtils.copyMerchant(xmlMerchant, merchant);

			try {
				LOG.debug("Create a merchant : " + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName());
				return this.createMerchant(merchant, xmlMerchant);
			} catch (Exception e) {
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action)) {
			LOG.debug("FOUND AN UPDATE MERCHANT");
			try {
				zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant = merchantService.findMerchantById(xmlMerchant.getId());
				if (merchant != null && merchant.getId() != null) {
					merchant = CentralSwitchXMLUtils.copyMerchant(xmlMerchant, merchant);

					zw.co.esolutions.ewallet.merchantservices.service.Merchant editedMerchant = merchantService.editMerchant(merchant, EWalletConstants.SYSTEM);
					if (editedMerchant == null || editedMerchant.getId() == null) {
						response.setNarrative("Failed to update MERCHANT " + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("MERCHANT " + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName() + " has been updated successfully");
					LOG.debug(response.getNarrative());
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					merchant = new zw.co.esolutions.ewallet.merchantservices.service.Merchant();
					merchant.setId(xmlMerchant.getId());
					merchant = CentralSwitchXMLUtils.copyMerchant(xmlMerchant, merchant);
					return this.createMerchant(merchant, xmlMerchant);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update MERCHANT " + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName() );
				response.setResponseCode("110");
				return response;
			}
		} else if (BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				existingMerchant = merchantService.findMerchantById(xmlMerchant.getId());
				if (existingMerchant != null && existingMerchant.getId() != null) {
					existingMerchant = CentralSwitchXMLUtils.copyMerchant(xmlMerchant, existingMerchant);
					zw.co.esolutions.ewallet.merchantservices.service.Merchant editedMerchant = merchantService.editMerchant(existingMerchant, EWalletConstants.SYSTEM);
					if (editedMerchant == null || editedMerchant.getId() == null) {
						response.setNarrative("Failed to update MERCHANT " + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName() + ". Web service exception");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("MERCHANT " + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					response.setNarrative("MERCHANT "  + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName() +  " never existed here.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update MERCHANT :"  + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName() );
				response.setResponseCode("110");
				return response;
			}
		}
		return null;
	
	}

	private SynchronisationResponse createMerchant(zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant, Merchant xmlMerchant) throws Exception {
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
		SynchronisationResponse response = new SynchronisationResponse();
		zw.co.esolutions.ewallet.merchantservices.service.Merchant existing = merchantService.findMerchantById(xmlMerchant.getId());

		if (existing == null || existing.getId() == null) {
			merchant = new zw.co.esolutions.ewallet.merchantservices.service.Merchant();
			merchant.setId(xmlMerchant.getId());
			merchant = CentralSwitchXMLUtils.copyMerchant(xmlMerchant, merchant);
			merchant = merchantService.createMerchant(merchant, EWalletConstants.SYSTEM);
			
			if (merchant == null || merchant.getId() == null) {
				response.setNarrative("Failed to create MERCHANT "  + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName() );
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}
			response.setNarrative("MERCHANT "  + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName() + " has been registered successfully.");
			response.setResponseCode(ResponseCode.E000.name());
			return response;
		} else {
			// bank account already exist, we can't do a create
			response.setNarrative("MERCHANT "  + xmlMerchant.getId() + " | " + xmlMerchant.getName() + "|" + xmlMerchant.getShortName() + " has cannot be created. Other one already created.");
			response.setResponseCode(ResponseCode.E505.name());
			return response;
		}

	}

	public List<zw.co.esolutions.ewallet.bankservices.service.Bank> getAllBanks() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		List<zw.co.esolutions.ewallet.bankservices.service.Bank> bankList = null;
		try {
			bankList = bankService.getBank();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bankList;
	}

	public List<zw.co.esolutions.ewallet.merchantservices.service.Merchant> getAllMerchants() {
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
		List<zw.co.esolutions.ewallet.merchantservices.service.Merchant> merchantList = null;
		try {
			merchantList = merchantService.getAllMerchants();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return merchantList;
	}

	

	public BankRegistrationMessageDocument copyToXMLBankDoc(zw.co.esolutions.ewallet.bankservices.service.Bank bank, MessageAction action) {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
		BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
		msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
		zw.co.esolutions.mcommerce.msg.Bank b = msg.addNewBank();

		b.setId(bank.getId());
		b.setBankName(bank.getName());
		b.setCode(bank.getCode());
		b.setContactDetailsId(bank.getContactDetailsId());
		b.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(bank.getDateCreated())));
		if (bank.getStatus() != null) {
			b.setStatus(zw.co.esolutions.mcommerce.msg.Bank.Status.Enum.forString(bank.getStatus().toString()));
		}

		return doc;
	}


	public BankRegistrationMessageDocument copyToXMLMerchantDoc(zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant, MessageAction action) {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
		BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
		msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
		Merchant m = msg.addNewMerchant();

		m.setId(merchant.getId());
		m.setContactDetailsId(merchant.getContactDetailsId());
		m.setName(merchant.getName());
		m.setShortName(merchant.getShortName());
		m.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(merchant.getDateCreated())));
		if (merchant.getStatus() != null) {
			m.setStatus(Status.Enum.forString(merchant.getStatus().toString()));
		}
		return doc;
	}

	private SynchronisationResponse processBank(Bank xmlBank, Enum action) {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

		SynchronisationResponse response = new SynchronisationResponse();
		if (xmlBank == null) {
			return null;
		}
		zw.co.esolutions.ewallet.bankservices.service.Bank bk = bankService.findBankById(xmlBank.getId());
		if (!(bk == null || bk.getId() == null)) {
			action = BankRegistrationMessage.MessageCommand.UPDATE;
		}
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {

			zw.co.esolutions.ewallet.bankservices.service.Bank bank = new zw.co.esolutions.ewallet.bankservices.service.Bank();
			bank.setId(xmlBank.getId());
			bank = CentralSwitchXMLUtils.copyBank(xmlBank, bank);

			try {

				zw.co.esolutions.ewallet.bankservices.service.Bank existing = bankService.findBankById(bank.getId());
				if (existing != null && existing.getId() != null) {
					response.setNarrative("Bank " + bank.getName() + " " + bank.getCode() + " has been registered successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
				zw.co.esolutions.ewallet.bankservices.service.Bank createdBank = bankService.createBank(bank, EWalletConstants.SYSTEM);
				if (createdBank == null || createdBank.getId() == null) {
					response.setNarrative("Failed to create bank " + bank.getName() + " " + bank.getCode() + ".");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				response.setNarrative("Bank " + bank.getName() + " " + bank.getCode() + " has been registered successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to register bank " + bank.getName() + " " + bank.getCode() + ".");
				response.setResponseCode("110");
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action) || BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			zw.co.esolutions.ewallet.bankservices.service.Bank bank = bankService.findBankById(xmlBank.getId());
			try {

				if (bank != null && bank.getId() != null) {
					bank.setId(xmlBank.getId());
					bank = CentralSwitchXMLUtils.copyBank(xmlBank, bank);
					zw.co.esolutions.ewallet.bankservices.service.Bank editedBank = bankService.editBank(bank, EWalletConstants.SYSTEM);
					if (editedBank == null || editedBank.getId() == null) {
						response.setNarrative("Failed to update bank " + bank.getName() + " " + bank.getCode() + ".");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}

					response.setNarrative("Bank " + bank.getName() + " : " + bank.getCode() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					// create it
					bank = new zw.co.esolutions.ewallet.bankservices.service.Bank();
					bank = CentralSwitchXMLUtils.copyBank(xmlBank, bank);
					bank.setId(xmlBank.getId());
					bank.setVersion(0);
					zw.co.esolutions.ewallet.bankservices.service.Bank createdBank = bankService.createBank(bank, EWalletConstants.SYSTEM);
					if (createdBank == null || createdBank.getId() == null) {
						response.setNarrative("Failed to update Bank for " + xmlBank.getBankName() + " : " + xmlBank.getCode());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					} else {
						response.setNarrative("Bank " + xmlBank.getBankName() + " : " + xmlBank.getCode() + " does not exist.");
						response.setResponseCode("100");
					}
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update Bank for " + xmlBank.getBankName() + " : " + xmlBank.getCode());
				response.setResponseCode("110");
				return response;
			}
		}
		return null;
	}
	
	private SynchronisationResponse processBankMerchant(zw.co.esolutions.mcommerce.msg.BankMerchant xmlBankMerchant, BankRegistrationMessage.MessageCommand.Enum action){

		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();

		LOG.debug("Procesing BANK MERCHANT REG........");
		SynchronisationResponse response = new SynchronisationResponse();
		if (xmlBankMerchant == null) {
			return null;
		}

		BankMerchant existingBankMerchant = merchantService.findBankMerchantById(xmlBankMerchant.getId());
		if (existingBankMerchant == null || existingBankMerchant.getId() == null) {
			// do a create
			action = BankRegistrationMessage.MessageCommand.CREATE;
		} else {
			action = BankRegistrationMessage.MessageCommand.UPDATE;
			if(BankMerchantStatus.DELETED.toString().equalsIgnoreCase(existingBankMerchant.getStatus().toString())) {
				LOG.debug("BANK MERCHANT REG already deleted.");
				//Ignore this
				return null;
			}
		}

		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			LOG.debug("FOUND A CREATE BANK MERCHANT ");
			BankMerchant bankMerchant = new BankMerchant();
			bankMerchant.setId(xmlBankMerchant.getId());
			bankMerchant = CentralSwitchXMLUtils.copyBankMerchant(xmlBankMerchant, bankMerchant);

			try {
				LOG.debug("Create a BANK merchant with ID " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId());
				return this.createBankMerchant(bankMerchant, xmlBankMerchant);
			} catch (Exception e) {
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action)) {
			LOG.debug("FOUND AN UPDATE BANK MERCHANT");
			try {
				BankMerchant bankMerchant = merchantService.findBankMerchantById(xmlBankMerchant.getId());
				if (bankMerchant != null && bankMerchant.getId() != null) {
					bankMerchant = CentralSwitchXMLUtils.copyBankMerchant(xmlBankMerchant, bankMerchant);

					BankMerchant editedBankMerchant = merchantService.editBankMerchant(bankMerchant, EWalletConstants.SYSTEM);
					if (editedBankMerchant == null || editedBankMerchant.getId() == null) {
						response.setNarrative("Failed to update Bank Account for " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Bank Merchant " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId()+ " has been updated successfully");
					LOG.debug(response.getNarrative());
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					bankMerchant = new BankMerchant();
					bankMerchant.setId(xmlBankMerchant.getId());
					bankMerchant = CentralSwitchXMLUtils.copyBankMerchant(xmlBankMerchant, bankMerchant);
					return this.createBankMerchant(bankMerchant, xmlBankMerchant);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update Bank Merchant " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId());
				response.setResponseCode("110");
				return response;
			}
		} else if (BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				BankMerchant bankMerchant = merchantService.findBankMerchantById(xmlBankMerchant.getId());
				if (bankMerchant != null && bankMerchant.getId() != null) {
					bankMerchant = CentralSwitchXMLUtils.copyBankMerchant(xmlBankMerchant, bankMerchant);
					BankMerchant editedBankMerchant = merchantService.editBankMerchant(bankMerchant, EWalletConstants.SYSTEM);
					if (editedBankMerchant == null || editedBankMerchant.getId() == null) {
						response.setNarrative("Failed to update Bank merchant " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId() + ". Web service exception");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Bank Merchant " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					response.setNarrative("Bank Merchant " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId()+ " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update Bank Merchant for " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId());
				response.setResponseCode("110");
				return response;
			}
		}
		return null;
	
	}
	
	private SynchronisationResponse createBankMerchant(BankMerchant bankMerchant, zw.co.esolutions.mcommerce.msg.BankMerchant xmlBankMerchant) throws Exception {
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
		SynchronisationResponse response = new SynchronisationResponse();
		BankMerchant existing = merchantService.findBankMerchantById(xmlBankMerchant.getId());

		if (existing == null || existing.getId() == null) {
			zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant = merchantService.findMerchantById(xmlBankMerchant.getMerchantId());
			// we find the branch
			if (merchant == null || merchant.getId() == null) {
				response.setNarrative("No Merchant, Failed to create bank merchant "  + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId());
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}

			bankMerchant.setMerchant(merchant);
			
			bankMerchant = merchantService.createBankMerchant(bankMerchant, EWalletConstants.SYSTEM);
			
			if (bankMerchant == null || bankMerchant.getId() == null) {
				response.setNarrative("Failed to create bank merchant " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId());
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}
			response.setNarrative("Bank Merchant " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId() + " has been registered successfully.");
			response.setResponseCode(ResponseCode.E000.name());
			return response;
		} else {
			// bank account already exist, we can't do a create
			response.setNarrative("Bank Merchant " + xmlBankMerchant.getId() + " | " + xmlBankMerchant.getMerchantId() + "|" + xmlBankMerchant.getBankId() + " has cannot be created. Other one already created.");
			response.setResponseCode(ResponseCode.E505.name());
			return response;
		}

	}

	
	private SynchronisationResponse processBankAccount(BankAccount xmlBankAccount, BankRegistrationMessage.MessageCommand.Enum action) {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

		LOG.debug("Procesing BANK ACCOUNT REG........");
		SynchronisationResponse response = new SynchronisationResponse();
		if (xmlBankAccount == null) {
			return null;
		}

		zw.co.esolutions.ewallet.bankservices.service.BankAccount existingAcc = bankService.findBankAccountById(xmlBankAccount.getId());
		if (existingAcc == null || existingAcc.getId() == null) {
			// do a create
			action = BankRegistrationMessage.MessageCommand.CREATE;
		} else {
			action = BankRegistrationMessage.MessageCommand.UPDATE;
			if(BankAccountStatus.DELETED.toString().equalsIgnoreCase(xmlBankAccount.getStatus().toString())) {
				//Ignore this
				return null;
			}
		}

		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			LOG.debug("FOUND A CREATE BANK ACCOUNT");
			zw.co.esolutions.ewallet.bankservices.service.BankAccount bankAccount = new zw.co.esolutions.ewallet.bankservices.service.BankAccount();
			bankAccount.setId(xmlBankAccount.getId());
			bankAccount = CentralSwitchXMLUtils.copyBankAccount(xmlBankAccount, bankAccount);

			try {
				LOG.debug("Create a BANK Account with ID " + xmlBankAccount.getId());
				return this.createBankAccount(bankAccount, xmlBankAccount);
			} catch (Exception e) {
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action)) {
			LOG.debug("FOUND AN UPDATE BANK ACCOUNT");
			try {
				zw.co.esolutions.ewallet.bankservices.service.BankAccount bankAccount = bankService.findBankAccountById(xmlBankAccount.getId());
				if (bankAccount != null && bankAccount.getId() != null) {
					bankAccount = CentralSwitchXMLUtils.copyBankAccount(xmlBankAccount, bankAccount);

					zw.co.esolutions.ewallet.bankservices.service.BankAccount editedBankAccount = bankService.editBankAccount(bankAccount, EWalletConstants.SYSTEM);
					if (editedBankAccount == null || editedBankAccount.getId() == null) {
						response.setNarrative("Failed to update Bank Account for " + xmlBankAccount.getAccountName() + " : " + xmlBankAccount.getAccountNumber());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Bank Account " + bankAccount.getAccountName() + " : " + bankAccount.getAccountNumber() + " has been updated successfully. Is Primary = " + bankAccount.isPrimaryAccount());
					LOG.debug(response.getNarrative());
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					bankAccount = bankService.findBankAccountById(xmlBankAccount.getId());
					bankAccount.setId(xmlBankAccount.getId());
					return this.createBankAccount(bankAccount, xmlBankAccount);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update Bank Account for " + xmlBankAccount.getAccountName() + " : " + xmlBankAccount.getAccountNumber());
				response.setResponseCode("110");
				return response;
			}
		} else if (BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.bankservices.service.BankAccount bankAccount = bankService.findBankAccountById(xmlBankAccount.getId());
				if (bankAccount != null && bankAccount.getId() != null) {
					bankAccount = CentralSwitchXMLUtils.copyBankAccount(xmlBankAccount, bankAccount);
					zw.co.esolutions.ewallet.bankservices.service.BankAccount editedBankAccount = bankService.editBankAccount(bankAccount, EWalletConstants.SYSTEM);
					if (editedBankAccount == null || editedBankAccount.getId() == null) {
						response.setNarrative("Failed to update Bank Account for " + xmlBankAccount.getAccountName() + " : " + xmlBankAccount.getAccountNumber() + ". Web service exception");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Bank Account " + bankAccount.getAccountName() + " : " + bankAccount.getAccountNumber() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					response.setNarrative("Bank Account " + xmlBankAccount.getAccountName() + " : " + xmlBankAccount.getAccountNumber() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update Bank Account for " + xmlBankAccount.getAccountName() + " : " + xmlBankAccount.getAccountNumber() + ". Exception " + e1.getMessage());
				response.setResponseCode("110");
				return response;
			}
		}
		return null;
	}

	private SynchronisationResponse createBankAccount(zw.co.esolutions.ewallet.bankservices.service.BankAccount bankAccount, BankAccount xmlBankAccount) throws Exception {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		SynchronisationResponse response = new SynchronisationResponse();
		zw.co.esolutions.ewallet.bankservices.service.BankAccount existing = bankService.findBankAccountById(xmlBankAccount.getId());

		if (existing == null || existing.getId() == null) {
			// bank account does not exist
			zw.co.esolutions.ewallet.bankservices.service.BankBranch bankBranch = bankService.findBankBranchById(xmlBankAccount.getBranchId());
			// we find the branch
			if (bankBranch == null || bankBranch.getId() == null) {
				response.setNarrative("No Bank Branch Failed to create bank account " + bankAccount.getAccountNumber() + ".");
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}

			bankAccount.setBranch(bankBranch);
			zw.co.esolutions.ewallet.bankservices.service.BankAccount ba;
			if (OwnerType.CUSTOMER.equals(bankAccount.getOwnerType())) {
				ba = bankService.createCustomerBankAccount(bankAccount, SystemConstants.SOURCE_APPLICATION_SWITCH, EWalletConstants.SYSTEM);
			} else {
				ba = bankService.createBankAccount(bankAccount, EWalletConstants.SYSTEM);
			}
			if (ba == null || ba.getId() == null) {
				response.setNarrative("Failed to create bank account " + bankAccount.getAccountNumber() + ". Returned NULL");
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}
			response.setNarrative("Bank Account " + bankAccount.getAccountNumber() + " has been registered successfully.");
			response.setResponseCode(ResponseCode.E000.name());
			return response;
		} else {
			// bank account already exist, we can't do a create
			response.setNarrative("Bank Account " + bankAccount.getAccountNumber() + " has cannot be created. Other one already created.");
			response.setResponseCode(ResponseCode.E505.name());
			return response;
		}

	}

	private SynchronisationResponse updateMobileProfile(zw.co.esolutions.ewallet.customerservices.service.MobileProfile existing, MobileProfile xmlMobileProfile) {

		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		SynchronisationResponse response = new SynchronisationResponse();

		try {
			zw.co.esolutions.ewallet.customerservices.service.MobileProfile mobileProfile = customerService.findMobileProfileById(xmlMobileProfile.getId());
			if (mobileProfile != null && mobileProfile.getId() != null) {
				mobileProfile = CentralSwitchXMLUtils.copyMobileProfile(xmlMobileProfile, mobileProfile);
				zw.co.esolutions.ewallet.customerservices.service.Customer customer;
				customer = customerService.findCustomerById(xmlMobileProfile.getCustomerId());
				if (customer != null && customer.getId() != null) {
					mobileProfile.setCustomer(customer);
				}
				zw.co.esolutions.ewallet.customerservices.service.MobileProfile editedProfile = customerService.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
				if (editedProfile == null || editedProfile.getId() == null) {
					response.setNarrative("Failed to update Mobile info with mobile number " + xmlMobileProfile.getMobileNumber() + ".");
					response.setResponseCode(ResponseCode.E900.name());
					return response;
				}
				response.setNarrative("Mobile Profile with mobile number " + xmlMobileProfile.getMobileNumber() + " has been updated successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;

			} else {
				return this.createMobileProfile(mobileProfile, xmlMobileProfile);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			response.setNarrative("Failed to update Mobile info with mobile number " + xmlMobileProfile.getMobileNumber() + ".");
			response.setResponseCode("110");
			return response;
		}
	}

	private SynchronisationResponse createMobileProfile(zw.co.esolutions.ewallet.customerservices.service.MobileProfile mobileProfile, MobileProfile xmlMobileProfile) {
		SynchronisationResponse response = new SynchronisationResponse();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		try {
			zw.co.esolutions.ewallet.customerservices.service.MobileProfile existing = customerService.findMobileProfileById(mobileProfile.getId());

			if (existing != null && existing.getId() != null) {
				response = this.updateMobileProfile(existing, xmlMobileProfile);
				return response;
			} else {
				zw.co.esolutions.ewallet.customerservices.service.Customer customer = customerService.findCustomerById(xmlMobileProfile.getCustomerId());
				if (customer == null || customer.getId() == null) {
					LOG.debug("in else customer nt found ");
					response.setNarrative("Customer with id : " + xmlMobileProfile.getCustomerId() + " could not be found.");
					response.setResponseCode(ResponseCode.E900.name());
					return response;
				} else {
					zw.co.esolutions.ewallet.customerservices.service.MobileProfile mpNew = new zw.co.esolutions.ewallet.customerservices.service.MobileProfile();
					mpNew = CentralSwitchXMLUtils.copyMobileProfile(xmlMobileProfile, mobileProfile);
					mpNew.setCustomer(customer);

					//Note, this is done to force the subscriber to change the generated pin, DON'T DELETE 
                    mpNew.setMobileProfileEditBranch(EWalletConstants.CHANGE_PIN);
                    
					mobileProfile = customerService.createMobileProfile(mpNew, SystemConstants.SOURCE_APPLICATION_SWITCH, EWalletConstants.SYSTEM);
					LOG.debug("done creating mobile profile:::::::::::::::::" + mpNew);
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setNarrative("Customer with id : " + xmlMobileProfile.getCustomerId() + " could not be found.");
			response.setResponseCode(ResponseCode.E900.name());
			return response;
		}
	}

	private SynchronisationResponse processMobileProfile(MobileProfile xmlMobileProfile, Enum action) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		SynchronisationResponse response = new SynchronisationResponse();
		LOG.debug("Synch Mobile Profile : ACTION : " + action);
		if (xmlMobileProfile == null) {
			return null;
		}
		zw.co.esolutions.ewallet.customerservices.service.MobileProfile mp = customerService.findMobileProfileById(xmlMobileProfile.getId());
		if (mp != null && mp.getId() != null) {
			action = BankRegistrationMessage.MessageCommand.UPDATE;
		} else {
			action = BankRegistrationMessage.MessageCommand.CREATE;
			if(MobileProfileStatus.DELETED.toString().equalsIgnoreCase(xmlMobileProfile.getStatus().toString())) {
				//Ignore this
				return null;
			}
		}

		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			zw.co.esolutions.ewallet.customerservices.service.MobileProfile mobileProfile = new zw.co.esolutions.ewallet.customerservices.service.MobileProfile();
			mobileProfile.setId(xmlMobileProfile.getId());
			mobileProfile = CentralSwitchXMLUtils.copyMobileProfile(xmlMobileProfile, mobileProfile);

			response = this.createMobileProfile(mobileProfile, xmlMobileProfile);
			return response;

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.customerservices.service.MobileProfile mobileProfile = customerService.findMobileProfileById(xmlMobileProfile.getId());
				if (mobileProfile != null && mobileProfile.getId() != null) {
					mobileProfile = CentralSwitchXMLUtils.copyMobileProfile(xmlMobileProfile, mobileProfile);
					mobileProfile = customerService.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
					if (mobileProfile == null || mobileProfile.getId() == null) {
						response.setNarrative("Failed to update Mobile Profile with number " + xmlMobileProfile.getMobileNumber());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Mobile Profile " + mobileProfile.getMobileNumber() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					LOG.debug("Update success::::::::::::::::::::::::::::::::::::: for ::::::::::::::::::::" + mobileProfile.getMobileNumber());
					return response;
				} else {
					LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Mobile profile>>>>>>>>>>>>>>>>>>>>>>>>creating in update delete>>>>>" + mobileProfile.getMobileNumber());
					return this.createMobileProfile(mobileProfile, xmlMobileProfile);
				}
			} catch (Exception e) {
				LOG.debug("exception occured error msg>>>>>>>>>>>>>>>>>>>>" + e.getMessage());
				e.printStackTrace();
			}
		} else if (BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {

			try {
				zw.co.esolutions.ewallet.customerservices.service.MobileProfile mobileProfile = customerService.findMobileProfileById(xmlMobileProfile.getId());

				if (mobileProfile != null && mobileProfile.getId() != null) {

					mobileProfile = CentralSwitchXMLUtils.copyMobileProfile(xmlMobileProfile, mobileProfile);

					mobileProfile = customerService.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
					if (mobileProfile == null || mobileProfile.getId() == null) {
						response.setNarrative("Failed to update Mobile Profile with number " + xmlMobileProfile.getMobileNumber());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Mobile Profile " + mobileProfile.getMobileNumber() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
			} catch (Exception e) {
				LOG.debug("exception occured error msg>>>>>>>>>>>>>>>>>>>>" + e.getMessage());
				e.printStackTrace();
				response.setNarrative("Failed to update Mobile Profile with number " + xmlMobileProfile.getMobileNumber());
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}

		}
		return null;
	}


	private SynchronisationResponse processBankBranch(BankBranch xmlBankBranch, Enum action) {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

		SynchronisationResponse response = new SynchronisationResponse();
		if (xmlBankBranch == null) {
			return null;
		}

		zw.co.esolutions.ewallet.bankservices.service.BankBranch bh = bankService.findBankBranchById(xmlBankBranch.getId());
		if (!(bh == null || bh.getId() == null)) {
			action = BankRegistrationMessage.MessageCommand.UPDATE;
		} else {
			if(BankBranchStatus.DELETED.toString().equalsIgnoreCase(xmlBankBranch.getStatus().toString())) {
				//Ignore this
				return null;
			}
		}

		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {

			zw.co.esolutions.ewallet.bankservices.service.BankBranch bankBranch = new zw.co.esolutions.ewallet.bankservices.service.BankBranch();
			bankBranch.setId(xmlBankBranch.getId());
			bankBranch = CentralSwitchXMLUtils.copyBankBranch(xmlBankBranch, bankBranch);

			try {
				zw.co.esolutions.ewallet.bankservices.service.BankBranch existing = bankService.findBankBranchById(bankBranch.getId());
				if (existing != null && existing.getId() != null) {
					response.setNarrative("Bank Branch " + bankBranch.getName() + " " + bankBranch.getCode() + " has been registered successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
				zw.co.esolutions.ewallet.bankservices.service.Bank bank = bankService.findBankById(xmlBankBranch.getBankId());
				if (bank != null && bank.getId() != null) {
					bankBranch.setBank(bank);
				} else {
					response.setNarrative("Failed to register bank branch " + bankBranch.getName() + " " + bankBranch.getCode() + ". Could not find bank");
					response.setResponseCode("110");
					return response;
				}
				zw.co.esolutions.ewallet.bankservices.service.BankBranch createdBranch = bankService.createBankBranch(bankBranch, EWalletConstants.SYSTEM);
				if (createdBranch == null || createdBranch.getId() == null) {
					response.setNarrative("Failed to register bank branch " + bankBranch.getName() + " " + bankBranch.getCode() + ". Failed to create branch using bank service");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				response.setNarrative("Bank Branch " + bankBranch.getName() + " " + bankBranch.getCode() + " has been registered successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to register bank branch " + bankBranch.getName() + " " + bankBranch.getCode() + ". Sys Exception" + e.getMessage());
				response.setResponseCode("110");
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action) || BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.bankservices.service.BankBranch bankBranch = bankService.findBankBranchById(xmlBankBranch.getId());
				if (bankBranch != null && bankBranch.getId() != null) {
					bankBranch = CentralSwitchXMLUtils.copyBankBranch(xmlBankBranch, bankBranch);
					zw.co.esolutions.ewallet.bankservices.service.BankBranch editedBranch = bankService.editBankBranch(bankBranch, EWalletConstants.SYSTEM);
					if (editedBranch == null || editedBranch.getId() == null) {
						response.setNarrative("Failed to update Bank Branch for " + xmlBankBranch.getBranchCode() + " : " + xmlBankBranch.getBranchCode());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}

					response.setNarrative("Bank Branch " + bankBranch.getName() + " : " + bankBranch.getCode() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					// create it
					bankBranch = new zw.co.esolutions.ewallet.bankservices.service.BankBranch();
					bankBranch = CentralSwitchXMLUtils.copyBankBranch(xmlBankBranch, bankBranch);
					bankBranch.setId(xmlBankBranch.getId());
					bankBranch.setVersion(0);
					zw.co.esolutions.ewallet.bankservices.service.BankBranch createdBranch = bankService.createBankBranch(bankBranch, EWalletConstants.SYSTEM);
					if (createdBranch == null || createdBranch.getId() == null) {
						response.setNarrative("Failed to update Bank Branch for " + xmlBankBranch.getBranchName() + " : " + xmlBankBranch.getBranchCode());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					} else {
						response.setNarrative("Bank Branch " + xmlBankBranch.getBranchName() + " : " + xmlBankBranch.getBranchCode() + " does not exist.");
						response.setResponseCode("100");
					}
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update Bank Branch for " + xmlBankBranch.getBranchCode() + " : " + xmlBankBranch.getBranchCode());
				response.setResponseCode("110");
				return response;
			}
		}
		return null;

	}

	private SynchronisationResponse processCustomer(Customer xmlCustomer, Enum action) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();

		SynchronisationResponse response = new SynchronisationResponse();
		if (xmlCustomer == null) {
			return null;
		}

		zw.co.esolutions.ewallet.customerservices.service.Customer cs = customerService.findCustomerById(xmlCustomer.getId());
		if (!(cs == null || cs.getId() == null)) {
			action = BankRegistrationMessage.MessageCommand.UPDATE;
		} else {
			action = BankRegistrationMessage.MessageCommand.CREATE;
			if(CustomerStatus.DELETED.toString().equalsIgnoreCase(xmlCustomer.getStatus().toString())) {
				//Ignore this
				return null;
			}
		}
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {

			zw.co.esolutions.ewallet.customerservices.service.Customer customer = new zw.co.esolutions.ewallet.customerservices.service.Customer();
			customer.setId(xmlCustomer.getId());
			customer = CentralSwitchXMLUtils.copyCustomer(xmlCustomer, customer);
			return this.createCustomer(customer);

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.customerservices.service.Customer customer = customerService.findCustomerById(xmlCustomer.getId());
				if (customer != null && customer.getId() != null) {
					customer = CentralSwitchXMLUtils.copyCustomer(xmlCustomer, customer);
					zw.co.esolutions.ewallet.customerservices.service.Customer editedCustomer = customerService.updateCustomer(customer, EWalletConstants.SYSTEM);
					if (editedCustomer == null || editedCustomer.getId() == null) {
						response.setNarrative("Failed to create Customer " + xmlCustomer.getTitle() + " " + xmlCustomer.getFirstNames() + " " + xmlCustomer.getLastName() + ". Returned NULL.");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Customer : " + customer.getTitle() + " " + customer.getFirstNames() + " " + customer.getLastName() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					return this.createCustomer(customer);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to create Customer " + xmlCustomer.getTitle() + " " + xmlCustomer.getFirstNames() + " " + xmlCustomer.getLastName() + ".");
				response.setResponseCode("100");
				return response;
			}
		} else if (BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.customerservices.service.Customer customer = customerService.findCustomerById(xmlCustomer.getId());
				if (customer != null && customer.getId() != null) {
					customer = CentralSwitchXMLUtils.copyCustomer(xmlCustomer, customer);
					zw.co.esolutions.ewallet.customerservices.service.Customer editedCustomer = customerService.updateCustomer(customer, EWalletConstants.SYSTEM);
					if (editedCustomer == null || editedCustomer.getId() == null) {
						response.setNarrative("Failed to create Customer " + xmlCustomer.getTitle() + " " + xmlCustomer.getFirstNames() + " " + xmlCustomer.getLastName() + ". Returned NULL.");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Customer : " + customer.getTitle() + " " + customer.getFirstNames() + " " + customer.getLastName() + " has been deleted successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					response.setNarrative("Customer : " + customer.getTitle() + " " + customer.getFirstNames() + " " + customer.getLastName() + " has been deleted successfully.");

					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to delete Customer " + xmlCustomer.getTitle() + " " + xmlCustomer.getFirstNames() + " " + xmlCustomer.getLastName() + ".");
				response.setResponseCode("100");
				return response;
			}
		} else {
			return null;
		}
	}

	private SynchronisationResponse createCustomer(zw.co.esolutions.ewallet.customerservices.service.Customer customer) {
		SynchronisationResponse response = new SynchronisationResponse();
		try {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			zw.co.esolutions.ewallet.customerservices.service.Customer existing = customerService.findCustomerById(customer.getId());
			if (existing != null && existing.getId() != null) {
				response.setNarrative("Customer " + customer.getTitle() + " " + customer.getFirstNames() + " " + customer.getLastName() + " has been registered successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			}
			zw.co.esolutions.ewallet.customerservices.service.Customer createdCustomer = customerService.createCustomer(customer, EWalletConstants.SYSTEM);
			if (createdCustomer == null || createdCustomer.getId() == null) {
				response.setNarrative("Failed to create Customer " + customer.getTitle() + " " + customer.getFirstNames() + " " + customer.getLastName() + ".");
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}
			response.setNarrative("Customer " + customer.getTitle() + " " + customer.getFirstNames() + " " + customer.getLastName() + " has been registered successfully.");
			response.setResponseCode(ResponseCode.E000.name());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			response.setNarrative("Failed to create Customer " + customer.getTitle() + " " + customer.getFirstNames() + " " + customer.getLastName() + ".");
			response.setResponseCode("100");
			return response;
		}
	}
	
	private SynchronisationResponse createAgent(zw.co.esolutions.ewallet.agentservice.service.Agent agent) {
		SynchronisationResponse response = new SynchronisationResponse();
		try {
			AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
			zw.co.esolutions.ewallet.agentservice.service.Agent existing = agentService.findAgentById(agent.getId());
			if (existing != null && existing.getId() != null) {
				response.setNarrative("Agent " + agent.getAgentName() + " " + agent.getAgentNumber() + " has been registered successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			}
			zw.co.esolutions.ewallet.agentservice.service.Agent createdAgent = agentService.createAgent(agent, EWalletConstants.SYSTEM);
			if (createdAgent == null || createdAgent.getId() == null) {
				response.setNarrative("Failed to create Agent "  + agent.getAgentName() + " " + agent.getAgentNumber() + ".");
				response.setResponseCode(ResponseCode.E900.name());
				return response;
			}
			response.setNarrative("Agent " + agent.getAgentName() + " " + agent.getAgentNumber() + " has been registered successfully.");
			response.setResponseCode(ResponseCode.E000.name());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			response.setNarrative("Failed to create Agent "  + agent.getAgentName() + " " + agent.getAgentNumber() + ".");
			response.setResponseCode("100");
			return response;
		}
	}

	public WsResponse sendTopupRequest(ResponseInfo responseInfo) throws Exception {
		WsRequest creditRequest = new WsRequest();
		WsResponse response = null;
		String proxyParameter = null;
		proxyParameter = SystemConstants.configParams.getProperty("PROXY_ON");
		LOG.debug(">>>>>>>>>>credit request>>>>>>>>>>amount>>>>>>>>>>>>>>>>" + proxyParameter);

		creditRequest = populateMNOCreditRequest(creditRequest, responseInfo);

		if (SystemConstants.PROXY_ON.equalsIgnoreCase(proxyParameter)) {
			String proxyHost = SystemConstants.configParams.getProperty("PROXY_HOST");
			String proxyPort = SystemConstants.configParams.getProperty("PROXY_PORT");
			System.getProperties().put("proxySet", "true");
			System.getProperties().put("http.proxyHost", proxyHost);
			System.getProperties().put("http.proxyPort", proxyPort);

		}

		TopupWebServicePortProxy topupWebService = new TopupWebServicePortProxy();

		((BindingProvider) topupWebService._getDescriptor().getProxy()).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "esolutions");
		((BindingProvider) topupWebService._getDescriptor().getProxy()).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "350lt0pup");
		try {
			response = topupWebService.processRequest(creditRequest);

		} catch (Exception e) {
			LOG.debug("Webservice call or An Uknown Exception thrown " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return response;
	}

	public WsResponse sendPhoneValidation(RequestInfo requestInfo) throws Exception {
		String proxyParameter = SystemConstants.configParams.getProperty("PROXY_ON");

		WsRequest request = populateMNOPhoneValidationRequest(new WsRequest(), requestInfo);

		if (SystemConstants.PROXY_ON.equalsIgnoreCase(proxyParameter)) {
			String proxyHost = SystemConstants.configParams.getProperty("PROXY_HOST");
			String proxyPort = SystemConstants.configParams.getProperty("PROXY_PORT");
			System.getProperties().put("proxySet", "true");
			System.getProperties().put("http.proxyHost", proxyHost);
			System.getProperties().put("http.proxyPort", proxyPort);
		}
		TopupWebServicePortProxy topupWebService = new TopupWebServicePortProxy();
		((BindingProvider) topupWebService._getDescriptor().getProxy()).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "esolutions");
		((BindingProvider) topupWebService._getDescriptor().getProxy()).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "350lt0pup");
		try {
			WsResponse response = topupWebService.processRequest(request);
			return response;
		} catch (Exception e) {
			LOG.debug("Webservice call or An Uknown Exception thrown " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
	}

	public WsResponse submitBillPayRequest(ResponseInfo responseInfo) throws Exception {
		// EconetvasSOAPProxy econetvasService = new EconetvasSOAPProxy();
		WsRequest request = new WsRequest();
		WsResponse response = null;
		try {
			request = this.populateMNOCreditRequest(request, responseInfo);
			request.setServiceCommand(ServiceCommand.BILLPAY);
			LOG.debug("Webservice call returns " + response);
			String proxyParameter = null;
			proxyParameter = SystemConstants.configParams.getProperty("PROXY_ON");
			if (SystemConstants.PROXY_ON.equalsIgnoreCase(proxyParameter)) {
				String proxyHost = SystemConstants.configParams.getProperty("PROXY_HOST");
				String proxyPort = SystemConstants.configParams.getProperty("PROXY_PORT");
				System.getProperties().put("http.proxyHost", proxyHost);
				System.getProperties().put("http.proxyPort", proxyPort);
			}
			TopupWebServicePortProxy topupWebService = new TopupWebServicePortProxy();

			((BindingProvider) topupWebService._getDescriptor().getProxy()).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "esolutions");
			((BindingProvider) topupWebService._getDescriptor().getProxy()).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "350lt0pup");
			LOG.debug("Now posting to Econet");
			response = topupWebService.processRequest(request);
			LOG.debug("Got the response " + response);
		} catch (Exception e) {
			LOG.debug("Webservice call or An Uknown Exception thrown ");
			e.printStackTrace();
			throw e;
		}
		return response;
	}

	private WsRequest dummyPopulateMNOCreditRequest(WsRequest request, ResponseInfo responseInfo)throws Exception{
		SwitchConfigurationServiceSOAPProxy config = new SwitchConfigurationServiceSOAPProxy();
		request.setAmount(responseInfo.getRequestInfo().getAmount());
		request.setUuid(responseInfo.getRequestInfo().getMessageId());

		request.setTargetMobileNumber(responseInfo.getRequestInfo().getTargetMobile());
		request.setSourceMobileNumber(responseInfo.getRequestInfo().getSourceMobile());
		request.setBankId(responseInfo.getRequestInfo().getSourceBankId());
		request.setMnoName(MnoName.ECONET);
		request.setServiceCommand(ServiceCommand.TOPUP);
		ConfigInfo info = config.findConfigInfoByOwnerId(responseInfo.getRequestInfo().getSourceBankId());
		request.setServiceId(info.getServiceId());
		request.setServiceProviderId(info.getServiceProviderId());
		LOG.debug("DONE POPULATING " + request.getTargetMobileNumber() + " " + request.getServiceProviderId() + " " + request.getServiceId() + " " + request.getSourceMobileNumber() + " " + request.getServiceCommand() + " " + request.getBankId() + " " + request.getMnoName() + " " + request.getAmount() + " " + request.getSourceMobileNumber() + " " + request.getUuid());
		return request;
	}
	
	
	private WsRequest populateMNOCreditRequest(WsRequest request, ResponseInfo responseInfo) throws Exception {
		SwitchConfigurationServiceSOAPProxy config = new SwitchConfigurationServiceSOAPProxy();
		request.setAmount(responseInfo.getRequestInfo().getAmount());
		request.setUuid(responseInfo.getRequestInfo().getMessageId());

		request.setTargetMobileNumber(responseInfo.getRequestInfo().getTargetMobile());
		request.setSourceMobileNumber(responseInfo.getRequestInfo().getSourceMobile());
		request.setBankId(responseInfo.getRequestInfo().getSourceBankId());
		request.setMnoName(MnoName.ECONET);
		request.setServiceCommand(ServiceCommand.TOPUP);
		ConfigInfo info = config.findConfigInfoByOwnerId(responseInfo.getRequestInfo().getSourceBankId());
		request.setServiceId(info.getServiceId());
		request.setServiceProviderId(info.getServiceProviderId());
		LOG.debug("DONE POPULATING " + request.getTargetMobileNumber() + " " + request.getServiceProviderId() + " " + request.getServiceId() + " " + request.getSourceMobileNumber() + " " + request.getServiceCommand() + " " + request.getBankId() + " " + request.getMnoName() + " " + request.getAmount() + " " + request.getSourceMobileNumber() + " " + request.getUuid());
		return request;
	}
	
	private WsRequest populateMNOPhoneValidationRequest(WsRequest request, RequestInfo requestInfo) throws Exception {
		SwitchConfigurationServiceSOAPProxy config = new SwitchConfigurationServiceSOAPProxy();
		request.setAmount(0L);
		request.setUuid(requestInfo.getMessageId());

		request.setTargetMobileNumber(requestInfo.getTargetMobile());
		request.setSourceMobileNumber(requestInfo.getSourceMobile());
		request.setBankId(requestInfo.getSourceBankId());
		request.setMnoName(MnoName.ECONET);
		request.setServiceCommand(ServiceCommand.TOPUP);
		ConfigInfo info = config.findConfigInfoByOwnerId(requestInfo.getSourceBankId());
		request.setServiceId(info.getServiceId());
		request.setServiceProviderId(info.getServiceProviderId());
		LOG.debug("DONE POPULATING " + request.getTargetMobileNumber() + " " + request.getServiceProviderId() + " " + request.getServiceId() + " " + request.getSourceMobileNumber() + " " + request.getServiceCommand() + " " + request.getBankId() + " " + request.getMnoName() + " " + request.getAmount() + " " + request.getSourceMobileNumber() + " " + request.getUuid());
		return request;
	}
	
	private SynchronisationResponse processAgent(Agent xmlAgent, Enum action) {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();

		SynchronisationResponse response = new SynchronisationResponse();
		if (xmlAgent == null) {
			return null;
		}

		zw.co.esolutions.ewallet.agentservice.service.Agent a = agentService.findAgentById(xmlAgent.getId());
		if (!(a == null || a.getId() == null)) {
			action = BankRegistrationMessage.MessageCommand.UPDATE;
		} else {
			action = BankRegistrationMessage.MessageCommand.CREATE;
		}
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {

			zw.co.esolutions.ewallet.agentservice.service.Agent agent = new zw.co.esolutions.ewallet.agentservice.service.Agent();
			agent.setId(xmlAgent.getId());
			agent = CentralSwitchXMLUtils.copyAgent(xmlAgent, agent);
			return this.createAgent(agent);

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.agentservice.service.Agent agent = agentService.findAgentById(xmlAgent.getId());
				if (agent != null && agent.getId() != null) {
					agent = CentralSwitchXMLUtils.copyAgent(xmlAgent, agent);
					zw.co.esolutions.ewallet.agentservice.service.Agent editedAgent = agentService.updateAgent(agent, EWalletConstants.SYSTEM);
					if (editedAgent == null || editedAgent.getId() == null) {
						response.setNarrative("Failed to create Agent " + xmlAgent.getAgentName() + " " + xmlAgent.getAgentNumber() + ". Returned NULL.");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Agent : " + xmlAgent.getAgentName() + " " + xmlAgent.getAgentNumber() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					return this.createAgent(agent);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to create Agent " + xmlAgent.getAgentName() + " " + xmlAgent.getAgentNumber() + ".");
				response.setResponseCode("100");
				return response;
			}
		} else if (BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.agentservice.service.Agent agent = agentService.findAgentById(xmlAgent.getId());
				if (agent != null && agent.getId() != null) {
					agent = CentralSwitchXMLUtils.copyAgent(xmlAgent, agent);
					zw.co.esolutions.ewallet.agentservice.service.Agent editedAgent = agentService.updateAgent(agent, EWalletConstants.SYSTEM);
					if (editedAgent == null || editedAgent.getId() == null) {
						response.setNarrative("Failed to create Customer " + xmlAgent.getAgentName() + " " + xmlAgent.getAgentNumber() + ". Returned NULL.");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Agent : "+ xmlAgent.getAgentName() + " " + xmlAgent.getAgentNumber() +  " has been deleted successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				} else {
					response.setNarrative("Agent : " + xmlAgent.getAgentName() + " " + xmlAgent.getAgentNumber() + " has been deleted successfully.");

					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to delete Agent " + xmlAgent.getAgentName() + " " + xmlAgent.getAgentNumber() +  ".");
				response.setResponseCode("100");
				return response;
			}
		} else {
			return null;
		}
	}


}
