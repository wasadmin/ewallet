/**
 * 
 */
package zw.co.esolutions.mcommerce.centralswitch.util;

import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessageDocument;
import zw.co.esolutions.mcommerce.msg.Merchant;
import zw.co.esolutions.mcommerce.msg.Merchant.Status;

/**
 * @author taurai
 *
 */
public class PushMessage {

	/**
	 * 
	 */
	public PushMessage() {
		// TODO Auto-generated constructor stub
	}
	
//	public static String synchBanks(String jndiQueueName, String bankId) throws Exception{
//		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
//		ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
//		try {
//			List<Bank> bankList = bankService.getBank();
//			if(bankList == null || bankList.isEmpty()) {
//				return "No Banks found to push to bank : ";
//			}
//			
//			for(Bank bk : bankList) {
//				
//				ContactDetails cont = contactDetailsService.findContactDetailsById(bk.getContactDetailsId());
//				
//				if (cont != null && cont.getId() != null) {
//					//Push Merchant
//					PushMessage.synch(jndiQueueName, bk, MessageAction.CREATE, bankId);
//					
//					//Push Contacts
//					PushMessage.synch(jndiQueueName, cont, MessageAction.CREATE, bankId);
//				}
//			}
//		} catch (Exception e) {
//			throw e;
//		}
//		return "success";
//	}
	
//	public static String synchMerchants(String jndiQueueName, String bankId) throws Exception{
//		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
//		ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
//		try {
//			List<zw.co.esolutions.ewallet.merchantservices.service.Merchant> merchantList = merchantService.getAllMerchants();
//			if(merchantList == null || merchantList.isEmpty()) {
//				return "No Merchants found to push to bank : ";
//			}
//			
//			for(zw.co.esolutions.ewallet.merchantservices.service.Merchant m : merchantList) {
//				
//				ContactDetails cont = contactDetailsService.findContactDetailsById(m.getContactDetailsId());
//				
//				if (cont != null && cont.getId() != null) {
//					//Push Merchant
//					PushMessage.synch(jndiQueueName, m, MessageAction.CREATE, bankId);
//					
//					//Push Contacts
//					PushMessage.synch(jndiQueueName, cont, MessageAction.CREATE, bankId);
//				}
//			}
//		} catch (Exception e) {
//			throw e;
//		}
//		return ResponseCode.E000.name();
//	}
	
//	public static void synch(String jndiQueueName, Object entity, MessageAction action, String bankId) {
//		BankRegistrationMessageDocument doc = null;
//		try {
//			if(entity instanceof Bank) {
//				doc = copyToXMLBankDoc((Bank)entity, action);
//			} else if(entity instanceof zw.co.esolutions.ewallet.merchantservices.service.Merchant) {
//				doc = copyToXMLMerchantDoc((zw.co.esolutions.ewallet.merchantservices.service.Merchant)entity, action);
//			} else if(entity instanceof ContactDetails) {
//				doc = copyToXMLContactDetailsDoc((ContactDetails)entity, action);
//			}
//			if(doc == null) {
//				throw new Exception("Null BankRegistrationMessageDocument");
//			}
//			sendMessage(jndiQueueName, doc, bankId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	private static BankRegistrationMessageDocument copyToXMLBankDoc(zw.co.esolutions.ewallet.bankservices.service.Bank bank, MessageAction action) {
	    	BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	    	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	    	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	    	zw.co.esolutions.mcommerce.msg.Bank b = msg.addNewBank();
	    	
	    	b.setId(bank.getId());
	    	b.setBankName(bank.getName());
	    	b.setCode(bank.getCode());
	    	b.setContactDetailsId(bank.getContactDetailsId());
	    	b.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(bank.getDateCreated())));
	    	if(bank.getStatus() != null) {
	    		b.setStatus(zw.co.esolutions.mcommerce.msg.Bank.Status.Enum.forString(bank.getStatus().toString()));
	    	}
	    	
	    	return doc;
	    }
	    
//	 private static BankRegistrationMessageDocument copyToXMLContactDetailsDoc(zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails contact, MessageAction action) {
//	    	BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
//		   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
//		   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
//		   	zw.co.esolutions.mcommerce.msg.ContactDetails c = msg.addNewContactDetails();
//		   	
//		   	c.setId(contact.getId());
//		   	c.setCity(contact.getCity());
//		   	c.setContactName(contact.getContactName());
//		   	c.setCountry(contact.getCountry());
//		   	c.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(contact.getDateCreated())));
//		   	c.setEmail(contact.getEmail());
//		   	c.setFax(contact.getFax());
//		   	c.setOwnerId(contact.getOwnerId());
//		   	c.setOwnerType(contact.getOwnerType());
//		   	c.setStreet(contact.getStreet());
//		   	c.setSurburb(contact.getSuburb());
//		   	c.setTelephone(contact.getTelephone());
//	    	return doc;
//	    }
	    
	    private static BankRegistrationMessageDocument copyToXMLMerchantDoc(zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant, MessageAction action) {
	    	BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	    	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	    	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	    	Merchant m = msg.addNewMerchant();
	    	
	    	m.setId(merchant.getId());
	    	m.setContactDetailsId(merchant.getContactDetailsId());
	    	m.setName(merchant.getName());
	    	m.setShortName(merchant.getShortName());
	    	m.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(merchant.getDateCreated())));
	    	if(merchant.getStatus() != null) {
	    		m.setStatus(Status.Enum.forString(merchant.getStatus().toString()));
	    	}
	    	return doc;
	    }
	    
//	    private static void sendMessage(String jndiQueueName, BankRegistrationMessageDocument doc, String bankId)throws Exception {
//	    	if (doc != null) {
//	    		doc.getBankRegistrationMessage().setBankId(bankId);
//		    		    	
//				String messageType = EWalletConstants.DOWNLOAD_TYPE;
//				DownloadResponse downloadResp = new DownloadResponse();
//				downloadResp.setDoc(doc);
//				MessageSender.send(jndiQueueName, downloadResp, messageType);
//	    	}
//		}

}
