package zw.co.esolutions.ewallet.download.session;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.download.util.DownloadXMLUtils;
import zw.co.esolutions.ewallet.limitservices.service.EWalletException_Exception;
import zw.co.esolutions.ewallet.limitservices.service.LimitServiceSOAPProxy;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.msg.DownloadRequest;
import zw.co.esolutions.ewallet.msg.DownloadResponse;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;
import zw.co.esolutions.ewallet.msg.SynchronisationResponse;
import zw.co.esolutions.ewallet.process.DayEndBean;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.tariffservices.service.TariffServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.mcommerce.msg.Bank;
import zw.co.esolutions.mcommerce.msg.BankAccount;
import zw.co.esolutions.mcommerce.msg.BankBranch;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.ContactDetails;
import zw.co.esolutions.mcommerce.msg.Limit;
import zw.co.esolutions.mcommerce.msg.Merchant;
import zw.co.esolutions.mcommerce.msg.Tariff;
import zw.co.esolutions.mcommerce.msg.TariffTable;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand.Enum;

/**
 * Session Bean implementation class CentralSwitchProcessUtilImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class DownloadProcessUtilImpl implements DownloadProcessUtil {

	 /**
     * Default constructor. 
     */
    public DownloadProcessUtilImpl() {
        // TODO Auto-generated constructor stub
    }
    
    
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(DayEndBean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + DayEndBean.class);
		}
	}
    public SynchronisationResponse processRegMessage(DownloadResponse downloadResponse, BankRegistrationMessage.MessageCommand.Enum action) throws Exception {
    	SynchronisationResponse response = null;
    	
    	BankRegistrationMessage bankRegistrationMessage = downloadResponse.getDoc()
		.getBankRegistrationMessage();

    	if(bankRegistrationMessage.getBank() != null) {
    		response = this.processBank(downloadResponse, action);
    	} else 	if(bankRegistrationMessage.getBankBranch() != null) {
			response = this.processBankBranch(downloadResponse, action);
		}  else if(bankRegistrationMessage.getBankAccount() != null) {
			response = this.processBankAccount(downloadResponse, action);
		} else if(bankRegistrationMessage.getContactDetails() != null) {
			response = this.processContactDetails(downloadResponse, action);
		} else if(bankRegistrationMessage.getLimit() != null) {
			response = this.processLimit(downloadResponse, action);
		} else if(bankRegistrationMessage.getTariffTable() != null) {
			response = this.processTarifftable(downloadResponse, action);
		} else if(bankRegistrationMessage.getTariff() != null) {
			response = this.processTariff(downloadResponse, action);
		} else if(bankRegistrationMessage.getMerchant() != null) {
			response = this.processMerchant(downloadResponse, action);
		}
    	return response;
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
    
    public zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails findContactDetailsById(String contactDetailsId) {
    	ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
    	zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails contact = null;
        try {
			contact = contactDetailsService.findContactDetailsById(contactDetailsId);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return contact;
    }
    
    private SynchronisationResponse processBank(DownloadResponse downloadResponse, Enum action) {
    	BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
    	
    	SynchronisationResponse response = new SynchronisationResponse();
    	
    	BankRegistrationMessage bankRegistrationMessage = downloadResponse.getDoc()
    		.getBankRegistrationMessage();
    	Bank xmlBank = bankRegistrationMessage.getBank();
    	
		if (xmlBank == null) {
			return null;
		}
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			
			zw.co.esolutions.ewallet.bankservices.service.Bank bank = new zw.co.esolutions.ewallet.bankservices.service.Bank();
			bank.setId(xmlBank.getId());
			bank = DownloadXMLUtils.copyBank(xmlBank, bank);
			
			try {
				
				zw.co.esolutions.ewallet.bankservices.service.Bank existing = bankService.findBankById(bank.getId());
				if(existing != null && existing.getId() != null){
					response.setNarrative("Bank " + bank.getName() + " " + bank.getCode() + " has been registered successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
				zw.co.esolutions.ewallet.bankservices.service.Bank theBank=bankService.createBank(bank, EWalletConstants.SYSTEM);
				
				if(theBank == null || theBank.getName()==null) {
					response.setNarrative("Failed to create bank " + bank.getName() + " " + bank.getCode()  + ".");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				response.setNarrative("Bank " + bank.getName() + " " + bank.getCode() + " has been registered successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to register bank " + bank.getName() + " " + bank.getCode()  + ".");
				response.setResponseCode("110");
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action) || BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			zw.co.esolutions.ewallet.bankservices.service.Bank bank = bankService.findBankById(xmlBank.getId());
			try {
				
				if(bank != null && bank.getName()!= null){
					bank.setId(xmlBank.getId());
					bank = DownloadXMLUtils.copyBank(xmlBank, bank);
					zw.co.esolutions.ewallet.bankservices.service.Bank editedBank= bankService.editBank(bank, EWalletConstants.SYSTEM);
					if(editedBank == null || editedBank.getName() == null) {
						response.setNarrative("Failed to update bank " + bank.getName() + " " + bank.getCode()  + ".");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					
					response.setNarrative("Bank " + bank.getName() + " : " + bank.getCode() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}else{
					response.setNarrative("Bank " + xmlBank.getBankName() + " : " + xmlBank.getCode() + " does not exist.");
					response.setResponseCode("100");
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
    
    private SynchronisationResponse processMerchant(DownloadResponse downloadResponse, Enum action) {
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
    	SynchronisationResponse response = new SynchronisationResponse();
    	
    	BankRegistrationMessage bankRegistrationMessage = downloadResponse.getDoc()
			.getBankRegistrationMessage();
    	Merchant xmlMerchant = bankRegistrationMessage.getMerchant();
    	
		if (xmlMerchant == null) {
			return null;
		}
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			
			zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant = new zw.co.esolutions.ewallet.merchantservices.service.Merchant();
			merchant.setId(xmlMerchant.getId());
			merchant = DownloadXMLUtils.copyMerchant(xmlMerchant, merchant);
			
			try {
				
				zw.co.esolutions.ewallet.merchantservices.service.Merchant existing = merchantService.findMerchantById(merchant.getId());
				if(existing != null && existing.getId() != null){
					
					if (merchantService.getBankMerchantByMerchantId(existing.getId()) == null || merchantService.getBankMerchantByMerchantId(existing.getId()).isEmpty()) {
						BankMerchant bankMerchant = new BankMerchant();
						bankMerchant.setBankId(bankService.getBankByCode(downloadResponse.getDownloadRequest().getBankCode()).getId());
						bankMerchant.setEnabled(false);
						bankMerchant.setMerchant(existing);
						bankMerchant.setStatus(zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus.DRAFT);
						
						bankMerchant = merchantService.createBankMerchant(bankMerchant, EWalletConstants.SYSTEM);
						MessageSync.populateAndSync(bankMerchant, MessageAction.CREATE);
					}
					response.setNarrative("Merchant " + merchant.getName() + " " + merchant.getShortName() + " has been registered successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
				
				zw.co.esolutions.ewallet.merchantservices.service.Merchant m = merchantService.createMerchant(merchant, EWalletConstants.SYSTEM);
			
				if(m == null || m.getName() == null) {
					response.setNarrative("Failed to create Merchant " + merchant.getName() + " " + merchant.getShortName()  + ".");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				
				BankMerchant bankMerchant = new BankMerchant();
				bankMerchant.setBankId(bankService.getBankByCode(downloadResponse.getDownloadRequest().getBankCode()).getId());
				bankMerchant.setEnabled(false);
				bankMerchant.setMerchant(m);
				bankMerchant.setStatus(zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus.DRAFT);
				
				bankMerchant = merchantService.createBankMerchant(bankMerchant, EWalletConstants.SYSTEM);
				MessageSync.populateAndSync(bankMerchant, MessageAction.CREATE);
				
				if(bankMerchant == null || bankMerchant.getId()==null) {
					response.setNarrative("Failed to create Bank Merchant " + merchant.getName() + " " + merchant.getShortName()  + ".");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				
				response.setNarrative("Merchant " + merchant.getName() + " " + merchant.getShortName() + " has been registered successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to register merchant " + merchant.getName() + " " + merchant.getShortName()  + ".");
				response.setResponseCode("110");
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action) || BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant = merchantService.findMerchantById(xmlMerchant.getId());
			try {
				
				if(merchant != null && merchant.getName()!=null){
					merchant.setId(xmlMerchant.getId());
					merchant = DownloadXMLUtils.copyMerchant(xmlMerchant, merchant);
					zw.co.esolutions.ewallet.merchantservices.service.Merchant editedMerchant=merchantService.editMerchant(merchant, EWalletConstants.SYSTEM);
					if(editedMerchant == null || editedMerchant.getId() == null) {
						response.setNarrative("Failed to update merchant " + merchant.getName() + " " + merchant.getShortName()  + ".");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					
					response.setNarrative("Merchant " + merchant.getName() + " : " + merchant.getShortName() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}else{
					response.setNarrative("Merchant " + xmlMerchant.getName() + " : " + xmlMerchant.getShortName() + " does not exist.");
					response.setResponseCode("100");
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update Merchant for " + xmlMerchant.getName() + " : " + xmlMerchant.getShortName());
				response.setResponseCode("110");
				return response;
			}
		}
		return null;	
	}

    private SynchronisationResponse processBankAccount(DownloadResponse downloadResponse, BankRegistrationMessage.MessageCommand.Enum action) {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		LOG.debug("Procesing BANK ACCOUNT REG........");
		SynchronisationResponse response = new SynchronisationResponse();
		
		BankRegistrationMessage bankRegistrationMessage = downloadResponse.getDoc()
			.getBankRegistrationMessage();
		BankAccount xmlBankAccount = bankRegistrationMessage.getBankAccount();
	
		if (xmlBankAccount == null) {
			return null;
		}
		
		
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			LOG.debug("FOUND A CREATE.....");
			zw.co.esolutions.ewallet.bankservices.service.BankAccount bankAccount = new zw.co.esolutions.ewallet.bankservices.service.BankAccount();
			bankAccount.setId(xmlBankAccount.getId());
			bankAccount = DownloadXMLUtils.copyBankAccount(xmlBankAccount, bankAccount);
			
			try {
				zw.co.esolutions.ewallet.bankservices.service.BankAccount existing = bankService.findBankAccountById(bankAccount.getId());
				if(existing != null && existing.getId() != null){
					response.setNarrative("Bank Account " + bankAccount.getAccountNumber() + " has been registered successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
				
				if(!BankAccountClass.SYSTEM.equals(bankAccount.getAccountClass())){
					LOG.debug("CREATING A NON SYSTEM ACCOUNT.......");
					zw.co.esolutions.ewallet.bankservices.service.BankBranch bankBranch = bankService.findBankBranchById(xmlBankAccount.getBranchId());
				
					if(bankBranch == null || bankBranch.getId() == null) {
						response.setNarrative("No Bank Branch Failed to create bank account " + bankAccount.getAccountNumber() + ".");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
				
					bankAccount.setBranch(bankBranch);
				}
				zw.co.esolutions.ewallet.bankservices.service.BankAccount createdBankAccount =bankService.createBankAccount(bankAccount, EWalletConstants.SYSTEM);
				if(createdBankAccount == null || createdBankAccount.getId() == null) {
					response.setNarrative("Failed to create bank account " + bankAccount.getAccountNumber() + ". Returned NULL");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				response.setNarrative("Bank Account " + bankAccount.getAccountNumber() + " has been registered successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to register bank account " + bankAccount.getAccountNumber() + ".");
				response.setResponseCode("110");
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action) || BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.bankservices.service.BankAccount bankAccount = bankService.findBankAccountById(xmlBankAccount.getId());
				if(bankAccount != null || bankAccount.getId() != null){
					bankAccount = DownloadXMLUtils.copyBankAccount(xmlBankAccount, bankAccount);
					zw.co.esolutions.ewallet.bankservices.service.BankAccount editedBankAccount = bankService.editBankAccount(bankAccount, EWalletConstants.SYSTEM);
					if(editedBankAccount == null || editedBankAccount.getId() == null) {
						response.setNarrative("Failed to update Bank Account for " + xmlBankAccount.getAccountName() + " : " + xmlBankAccount.getAccountNumber());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Bank Account " + bankAccount.getAccountName() + " : " + bankAccount.getAccountNumber() +" has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}else{
					response.setNarrative("Bank Account " + xmlBankAccount.getAccountName() + " : " + xmlBankAccount.getAccountNumber() + " does not exist.");
					response.setResponseCode("100");
					return response;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setNarrative("Failed to update Bank Account for " + xmlBankAccount.getAccountName() + " : " + xmlBankAccount.getAccountNumber());
				response.setResponseCode("110");
				return response;
			}
		}
		return null;	
	}
	
	private SynchronisationResponse processTariff(DownloadResponse downloadResponse, Enum action) {
		TariffServiceSOAPProxy tariffService = new TariffServiceSOAPProxy();
		SynchronisationResponse response = new SynchronisationResponse();
		
		BankRegistrationMessage bankRegistrationMessage = downloadResponse.getDoc()
			.getBankRegistrationMessage();
		Tariff xmlTariff = bankRegistrationMessage.getTariff();
	
		if (xmlTariff == null) {
			return null;
		}
		
		zw.co.esolutions.ewallet.tariffservices.service.Tariff tariff;
		
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			tariff = new zw.co.esolutions.ewallet.tariffservices.service.Tariff();
			tariff.setId(xmlTariff.getId());
			tariff = DownloadXMLUtils.copyTariff(xmlTariff, tariff);
			try {
				zw.co.esolutions.ewallet.tariffservices.service.TariffTable table = tariffService.findTariffTableById(xmlTariff.getTariffTableId());
				tariff.setTariffTable(table);
				if(table == null || table.getId() == null) {
					return null;
				}
				zw.co.esolutions.ewallet.tariffservices.service.Tariff commission = tariffService.createCommission(tariff, EWalletConstants.SYSTEM);
				if(commission == null || commission.getId() == null) {
					response.setNarrative("Failed to create tariff.");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				response.setResponseCode(ResponseCode.E000.name());
				response.setNarrative("Tariff created successfully.");
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponseCode("100");
				response.setNarrative("Failed to create tariff " + e.getMessage());
			}
		} 
		else{
			response.setResponseCode("100");
			response.setNarrative("Unknown Operation for tarrifs");
		}
		return response;
	}

	private SynchronisationResponse processTarifftable(DownloadResponse downloadResponse, Enum action) {

		 TariffServiceSOAPProxy tariffService = new TariffServiceSOAPProxy();
			
		SynchronisationResponse response = new SynchronisationResponse();
		
		BankRegistrationMessage bankRegistrationMessage = downloadResponse.getDoc()
			.getBankRegistrationMessage();
		TariffTable xmlTariffTable = bankRegistrationMessage.getTariffTable();
	
		if (xmlTariffTable == null) {
			return null;
		}
		
		zw.co.esolutions.ewallet.tariffservices.service.TariffTable tariffTable;
		
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			tariffTable = new zw.co.esolutions.ewallet.tariffservices.service.TariffTable();
			tariffTable.setId(xmlTariffTable.getId());
			tariffTable = DownloadXMLUtils.copyTariffTable(xmlTariffTable, tariffTable);
			try {
				zw.co.esolutions.ewallet.tariffservices.service.TariffTable createdTariffTable = tariffService.createCommissionTable(tariffTable, EWalletConstants.SYSTEM);
				
				if(createdTariffTable == null || createdTariffTable.getId()==null) {
					response.setNarrative("Failed to create tariff table.");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				response.setResponseCode(ResponseCode.E000.name());
				response.setNarrative("Tariff Table created successfully.");
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponseCode("100");
				response.setNarrative("Failed to create tariff table. Reason : " + e.getMessage());
			}
		} 
//		else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action) || BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
		else{
			response.setResponseCode("100");
			response.setNarrative("Unknown Operation for tarrifs");
		}
		return response;
	
	}

	private SynchronisationResponse processLimit(DownloadResponse downloadResponse, Enum action) {
		LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();
		
		SynchronisationResponse response = new SynchronisationResponse();
		
		BankRegistrationMessage bankRegistrationMessage = downloadResponse.getDoc()
			.getBankRegistrationMessage();
		Limit xmlLimit = bankRegistrationMessage.getLimit();
	
		if (xmlLimit == null) {
			return null;
		}
		zw.co.esolutions.ewallet.limitservices.service.Limit limit;
		
		
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			
			limit = new zw.co.esolutions.ewallet.limitservices.service.Limit();
			limit.setId(xmlLimit.getId());
			limit = DownloadXMLUtils.copyLimit(xmlLimit, limit);
			zw.co.esolutions.ewallet.limitservices.service.Limit createdLimit = null;
			try {
				createdLimit = limitService.createLimit(limit, EWalletConstants.SYSTEM);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if(createdLimit == null || createdLimit.getId() == null) {
					response.setNarrative("Failed to create Limit.");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				response.setNarrative("Limit was created successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to create limit. Reason : " + e.getMessage());
				response.setResponseCode("100");
				return response;
			} 
		}else if(BankRegistrationMessage.MessageCommand.UPDATE.equals(action) || BankRegistrationMessage.MessageCommand.DELETE.equals(action)){
			try {
				limit = limitService.findLimitById(xmlLimit.getId());
				if (limit != null && limit.getId() != null) {
					limit = DownloadXMLUtils.copyLimit(xmlLimit, limit);
					zw.co.esolutions.ewallet.limitservices.service.Limit createdLimit = limitService.createLimit(limit, EWalletConstants.SYSTEM);
					if(createdLimit == null || createdLimit.getId() == null) { 
						response.setNarrative("Failed to update limit, returned null.");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Limit was updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}else{
					response.setNarrative("Limit does not exist.");
					response.setResponseCode("110");
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to update limit. Reason : " + e.getMessage());
				response.setResponseCode("100");
				return response;
			}
		}
		return null;
	}

	private SynchronisationResponse processContactDetails(DownloadResponse downloadResponse, Enum action) {
		ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
		
		SynchronisationResponse response = new SynchronisationResponse();
		
		BankRegistrationMessage bankRegistrationMessage = downloadResponse.getDoc()
			.getBankRegistrationMessage();
		ContactDetails xmlContactDetails = bankRegistrationMessage.getContactDetails();
	
		if (xmlContactDetails == null) {
			return null;
		}
		zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails contact ;
		
				
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			contact = new zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails();
			contact.setId(xmlContactDetails.getId());
			contact = DownloadXMLUtils.copyContactDetails(xmlContactDetails, contact);
			try {
				zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails existing = contactDetailsService.findContactDetailsById(contact.getId());
				if(existing != null && existing.getId() != null){
					response.setNarrative("Contact Details for " + contact.getContactName() + " have been created successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
				zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails contactDetails = contactDetailsService.createContactDetails(contact, EWalletConstants.SYSTEM);
				if(contactDetails == null || contactDetails.getId()== null) {
					response.setNarrative("Failed to create contact details for " + contact.getContactName() );
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				response.setNarrative("Contact Details for " + contact.getContactName() + " have been created successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to create contact details for " + contact.getContactName() + ". Reason : " + e.getMessage());
				response.setResponseCode("100");
				return response;
			} 
		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action) || BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				contact = contactDetailsService.findContactDetailsById(xmlContactDetails.getId());
				if(contact != null){
					contact = DownloadXMLUtils.copyContactDetails(xmlContactDetails, contact);
					zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails updatedContacteDetails =contactDetailsService.updateContactDetails(contact);
					if(updatedContacteDetails == null || updatedContacteDetails.getId() == null) {
						response.setNarrative("Failed to update Contact Details for " + xmlContactDetails.getContactName() + ". Returned null.");
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					response.setNarrative("Contact Details for " + contact.getContactName() + " have been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}else{
					response.setNarrative("Contact Details for " + xmlContactDetails.getContactName() + " could not be found.");
					response.setResponseCode("110");
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to update Contact Details for " + xmlContactDetails.getContactName() + ". Reason : " + e.getMessage());
				response.setResponseCode("110");
				return response;
			}
		}
		return null;
	}

	private SynchronisationResponse processBankBranch(DownloadResponse downloadResponse, Enum action) {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		SynchronisationResponse response = new SynchronisationResponse();
		
		BankRegistrationMessage bankRegistrationMessage = downloadResponse.getDoc()
			.getBankRegistrationMessage();
		BankBranch xmlBankBranch = bankRegistrationMessage.getBankBranch();
	
		if (xmlBankBranch == null) {
			return null;
		}
	
		if (BankRegistrationMessage.MessageCommand.CREATE.equals(action)) {
			
			zw.co.esolutions.ewallet.bankservices.service.BankBranch bankBranch = new zw.co.esolutions.ewallet.bankservices.service.BankBranch();
			bankBranch.setId(xmlBankBranch.getId());
			bankBranch = DownloadXMLUtils.copyBankBranch(xmlBankBranch, bankBranch);
			
			try {
				zw.co.esolutions.ewallet.bankservices.service.BankBranch existing = bankService.findBankBranchById(bankBranch.getId());
				if(existing != null && existing.getId() != null){
					response.setNarrative("Bank Branch " + bankBranch.getName() + " " + bankBranch.getCode() + " has been registered successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}
				zw.co.esolutions.ewallet.bankservices.service.Bank bank = bankService.findBankById(xmlBankBranch.getBankId());
				if(bank != null && bank.getId()!= null){
					bankBranch.setBank(bank);
				}else{
					response.setNarrative("Failed to register bank branch " + bankBranch.getName() + " " + bankBranch.getCode()  + ".");
					response.setResponseCode("110");
					return response;
				}
				zw.co.esolutions.ewallet.bankservices.service.BankBranch createdbankBranch = bankService.createBankBranch(bankBranch, EWalletConstants.SYSTEM);
				if(createdbankBranch == null || createdbankBranch.getId() == null) {
					response.setNarrative("Failed to register bank branch " + bankBranch.getName() + " " + bankBranch.getCode()  + ".");
					response.setResponseCode(ResponseCode.E900.name());
					return null;
				}
				response.setNarrative("Bank Branch " + bankBranch.getName() + " " + bankBranch.getCode() + " has been registered successfully.");
				response.setResponseCode(ResponseCode.E000.name());
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				response.setNarrative("Failed to register bank branch " + bankBranch.getName() + " " + bankBranch.getCode()  + ".");
				response.setResponseCode("110");
				return response;
			}

		} else if (BankRegistrationMessage.MessageCommand.UPDATE.equals(action) || BankRegistrationMessage.MessageCommand.DELETE.equals(action)) {
			try {
				zw.co.esolutions.ewallet.bankservices.service.BankBranch bankBranch = bankService.findBankBranchById(xmlBankBranch.getId());
				if(bankBranch != null && bankBranch.getId() != null){
					bankBranch = DownloadXMLUtils.copyBankBranch(xmlBankBranch, bankBranch);
					if(bankService.editBankBranch(bankBranch, EWalletConstants.SYSTEM) == null) {
						response.setNarrative("Failed to update Bank Branch for " + xmlBankBranch.getBranchCode() + " : " + xmlBankBranch.getBranchCode());
						response.setResponseCode(ResponseCode.E900.name());
						return response;
					}
					
					response.setNarrative("Bank Branch " + bankBranch.getName() + " : " + bankBranch.getCode() + " has been updated successfully.");
					response.setResponseCode(ResponseCode.E000.name());
					return response;
				}else{
					response.setNarrative("Bank Branch " + xmlBankBranch.getBranchName() + " : " + xmlBankBranch.getBranchCode() + " does not exist.");
					response.setResponseCode("100");
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
	
}
