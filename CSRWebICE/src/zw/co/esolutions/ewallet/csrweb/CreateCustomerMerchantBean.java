package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;

import zw.co.esolutions.ewallet.csr.msg.MessageAction;
import zw.co.esolutions.ewallet.csr.msg.MessageSync;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchantStatus;

public class CreateCustomerMerchantBean extends PageCodeBase {
	public String customerId;
	private List<SelectItem> bankMerchantList;
	private CustomerMerchant customerMerchant = new CustomerMerchant();
	private String selectedBankMerchant;
	
	@SuppressWarnings("unchecked")
	public String getCustomerId() {
		if (customerId == null) {
			customerId = (String) super.getRequestParam().get("customerId");
		}
		if (customerId == null) {
			customerId = (String) super.getRequestScope().get("customerId");
		}
		if (customerId != null) {
			super.getRequestScope().put("customerId", customerId);
		}
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public List<SelectItem> getBankMerchantList() {
		if (bankMerchantList == null || bankMerchantList.isEmpty()) {
			bankMerchantList = new ArrayList<SelectItem>();
			bankMerchantList.add(new SelectItem("none", "--Select--"));
			List<BankMerchant> bankMerchants = super.getMerchantService().getBankMerchantByStatus(BankMerchantStatus.ACTIVE);
			if (bankMerchants != null) {
				for (BankMerchant bankMerchant: bankMerchants) {
					bankMerchantList.add(new SelectItem(bankMerchant.getId(), bankMerchant.getMerchant().getShortName()));
				}
			}
		}
		return bankMerchantList;
	}
	public void setBankMerchantList(List<SelectItem> bankMerchantList) {
		this.bankMerchantList = bankMerchantList;
	}
	public CustomerMerchant getCustomerMerchant() {
		return customerMerchant;
	}
	public void setCustomerMerchant(CustomerMerchant customerMerchant) {
		this.customerMerchant = customerMerchant;
	}
	 
	public void setSelectedBankMerchant(String selectedBankMerchant) {
		this.selectedBankMerchant = selectedBankMerchant;
	}
	public String getSelectedBankMerchant() {
		return selectedBankMerchant;
	}
	@SuppressWarnings("unchecked")
	public String submit() {
		if ("none".equals(selectedBankMerchant)) {
			super.setErrorMessage("Please select the merchant.");
			return "failure";
		}
		
		if (this.getCustomerMerchant().getCustomerAccountNumber() == null 
				|| customerMerchant.getCustomerAccountNumber().trim().equals("")) {
			super.setErrorMessage("Account Number cannot be blank.");
			return "failure";
		}
		
		try {
			BankMerchant bankMerchant = super.getMerchantService().findBankMerchantById(selectedBankMerchant);
			
			CustomerMerchant cm = super.getMerchantService()
				.getCustomerMerchantByBankMerchantIdAndCustomerIdAndCustomerAccountNumber(
						bankMerchant.getId(), this.getCustomerId(), customerMerchant.getCustomerAccountNumber());
			if (cm != null && cm.getId() != null) {
				super.setErrorMessage("This merchant is already registered.");
				return "failure";
			}
			
			customerMerchant.setBankId(bankMerchant.getBankId());
			customerMerchant.setBankMerchant(bankMerchant);
			customerMerchant.setCustomerId(getCustomerId());
			customerMerchant.setStatus(CustomerMerchantStatus.AWAITING_APPROVAL);
		
			super.getMerchantService().createCustomerMerchant(customerMerchant, super.getJaasUserName());
//			MessageSync.populateAndSync(customerMerchant, MessageAction.CREATE);
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		super.setInformationMessage("Customer merchant registered successfully.");
		super.getRequestScope().put("customerId", customerId);
		gotoPage("/csr/viewCustomer.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("customerId", customerId);
		gotoPage("/csr/viewCustomer.jspx");
		return "cancel";
	}
	
	
}
