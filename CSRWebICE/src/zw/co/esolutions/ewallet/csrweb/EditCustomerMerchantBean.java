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

public class EditCustomerMerchantBean extends PageCodeBase {
	public String customerMerchantId;
	private List<SelectItem> bankMerchantList;
	private CustomerMerchant customerMerchant;
	private String selectedBankMerchant;
	
	public String getCustomerMerchantId() {
		if (customerMerchantId == null) {
			customerMerchantId = (String) super.getRequestParam().get("customerMerchantId");
		}
		return customerMerchantId;
	}
	public void setCustomerMerchantId(String customerMerchantId) {
		this.customerMerchantId = customerMerchantId;
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
		if (customerMerchant == null || customerMerchant.getId() == null) {
			if (this.getCustomerMerchantId() != null) {
				try {
					customerMerchant = super.getMerchantService().findCustomerMerchantById(customerMerchantId);
					setSelectedBankMerchant(customerMerchant.getBankMerchant().getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				customerMerchant = new CustomerMerchant();
			}
		}
		return customerMerchant;
	}
	public void setCustomerMerchant(CustomerMerchant customerMerchant) {
		this.customerMerchant = customerMerchant;
	}
	 
	public void setSelectedBankMerchant(String selectedBankMerchant) {
		this.selectedBankMerchant = selectedBankMerchant;
	}
	public String getSelectedBankMerchant() {
		if (selectedBankMerchant == null) {
			this.getCustomerMerchant();
		}
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
			
			customerMerchant.setBankMerchant(bankMerchant);
					
			super.getMerchantService().editCustomerMerchant(customerMerchant, super.getJaasUserName());
	//		MessageSync.populateAndSync(customerMerchant, MessageAction.UPDATE);
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		super.setInformationMessage("Customer merchant edited successfully.");
		super.getRequestScope().put("customerId", customerMerchant.getCustomerId());
		gotoPage("/csr/viewCustomer.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("customerId", customerMerchant.getCustomerId());
		gotoPage("/csr/viewCustomer.jspx");
		return "cancel";
	}
	
}
