package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.Gender;
import zw.co.esolutions.ewallet.customerservices.service.MaritalStatus;
import zw.co.esolutions.ewallet.tellerweb.enums.Title;
import zw.co.esolutions.ewallet.util.DateUtil;

public class EditCustomerBean extends PageCodeBase {
	private String customerId;
	private Customer customer;
	private ContactDetails contactDetails;
	private BankAccount bankAccount;
	private List<SelectItem> titleList;
	private String selectedTitle;
	private List<SelectItem> genderList;
	private String selectedGender;
	private List<SelectItem> maritalStatusList;
	private String selectedMaritalStatus;
	private List<SelectItem> customerClassList;
	private String selectedCustomerClass;
	private List<SelectItem> bankList;
	private String selectedBank;
	private List<SelectItem> branchList;
	private String selectedBranch;
	private List<SelectItem> accountClassList;
	private String selectedAccountClass;
	private Date dateOfBirth;
	
	public String getCustomerId() {
		if (customerId == null) {
			customerId = (String) super.getRequestParam().get("customerId");
		}
		if (customerId == null) {
			customerId = (String) super.getRequestScope().get("customerId");
		}
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Customer getCustomer() {
		if (customer == null || customer.getId() == null) {
			if (this.getCustomerId() != null) {
				customer = super.getCustomerService().findCustomerById(customerId);
				this.setSelectedCustomerClass(customer.getCustomerClass().name());
				this.setSelectedGender(customer.getGender().name());
				this.setSelectedMaritalStatus(customer.getMaritalStatus().name());
				this.setSelectedTitle(customer.getTitle());
			} else {
				customer = new Customer();
			}
		}
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public ContactDetails getContactDetails() {
		if (contactDetails == null || contactDetails.getId() == null) {
			if (this.getCustomer() != null && customer.getId() != null) {
				contactDetails = super.getContactDetailsService().findContactDetailsById(customer.getContactDetailsId());
			} else {
				contactDetails = new ContactDetails();
			}
		}
		return contactDetails;
	}
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	
//	public BankAccount getBankAccount() {
//		if (bankAccount == null || bankAccount.getId() == null) {
//			if (this.getCustomerId() != null) {
//				try {
//					bankAccount = bankService.getBankAccountByAccountHolderAndType(customerId, BankAccountType.E_WALLET);
//					this.setSelectedBank(bankAccount.getBranch().getBank().getId());
//					this.setSelectedBranch(bankAccount.getBranch().getId());
//					this.setSelectedAccountClass(bankAccount.getAccountClass().name());
//				} catch (Exception e) {
//					
//				}
//			} else {
//				bankAccount = new BankAccount();
//			}
//		}
//		return bankAccount;
//	}
//	public void setBankAccount(BankAccount bankAccount) {
//		this.bankAccount = bankAccount;
//	}
//	
	public List<SelectItem> getTitleList() {
		if (titleList == null) {
			titleList = new ArrayList<SelectItem>();
			
			for (Title title: Title.values()) {
				titleList.add(new SelectItem(title.name(), title.name()));
			}
		}
		return titleList;
	}
	public void setTitleList(List<SelectItem> titleList) {
		this.titleList = titleList;
	}
	public String getSelectedTitle() {
		return selectedTitle;
	}
	public void setSelectedTitle(String selectedTitle) {
		this.selectedTitle = selectedTitle;
	}
	public List<SelectItem> getGenderList() {
		if (genderList == null) {
			genderList = new ArrayList<SelectItem>();
			
			for (Gender gender: Gender.values()) {
				genderList.add(new SelectItem(gender.name(), gender.name()));
			}
		}
		return genderList;
	}
	public void setGenderList(List<SelectItem> genderList) {
		this.genderList = genderList;
	}
	public String getSelectedGender() {
		return selectedGender;
	}
	public void setSelectedGender(String selectedGender) {
		this.selectedGender = selectedGender;
	}
	public List<SelectItem> getMaritalStatusList() {
		if (maritalStatusList == null) {
			maritalStatusList = new ArrayList<SelectItem>();
			
			for (MaritalStatus maritalStatus: MaritalStatus.values()) {
				maritalStatusList.add(new SelectItem(maritalStatus.name(), maritalStatus.name()));
			}
		}
		return maritalStatusList;
	}
	public void setMaritalStatusList(List<SelectItem> maritalStatusList) {
		this.maritalStatusList = maritalStatusList;
	}
	public String getSelectedMaritalStatus() {
		return selectedMaritalStatus;
	}
	public void setSelectedMaritalStatus(String selectedMaritalStatus) {
		this.selectedMaritalStatus = selectedMaritalStatus;
	}
	public List<SelectItem> getCustomerClassList() {
		if (customerClassList == null) {
			customerClassList = new ArrayList<SelectItem>();
	
			for (CustomerClass customerClass: CustomerClass.values()) {
				customerClassList.add(new SelectItem(customerClass.name(), customerClass.name()));
			}
		}
		return customerClassList;
	}
	public void setCustomerClassList(List<SelectItem> customerClassList) {
		this.customerClassList = customerClassList;
	}
	public String getSelectedCustomerClass() {
		return selectedCustomerClass;
	}
	public void setSelectedCustomerClass(String selectedCustomerClass) {
		this.selectedCustomerClass = selectedCustomerClass;
	}
//	}
//	public List<SelectItem> getBranchList() {
//		if (branchList == null) {
//			branchList = new ArrayList<SelectItem>();
//	
//			try {
//				List<BankBranch> branches = bankService.getBankBranch();
//				if (branches != null) {
//					for (BankBranch branch: branches) {
//						branchList.add(new SelectItem(branch.getId(), branch.getName()));
//					}
//				}
//			} catch (Exception e) {
//				
//			}
//		}
//		return branchList;
//	}
//	public void setBranchList(List<SelectItem> branchList) {
//		this.branchList = branchList;
//	}
//	public String getSelectedBranch() {
//		return selectedBranch;
//	}
//	public void setSelectedBranch(String selectedBranch) {
//		this.selectedBranch = selectedBranch;
//	}
	public List<SelectItem> getAccountClassList() {
		if (accountClassList == null) {
			accountClassList = new ArrayList<SelectItem>();
	
			for (BankAccountClass accountClass: BankAccountClass.values()) {
				accountClassList.add(new SelectItem(accountClass.name(), accountClass.name()));
			}
		}
		return accountClassList;
	}
	public void setAccountClassList(List<SelectItem> accountClassList) {
		this.accountClassList = accountClassList;
	}
	public String getSelectedAccountClass() {
		return selectedAccountClass;
	}
	public void setSelectedAccountClass(String selectedAccountClass) {
		this.selectedAccountClass = selectedAccountClass;
	}
	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Date getDateOfBirth() {
		dateOfBirth = (dateOfBirth == null)? new Date() : dateOfBirth;
		return dateOfBirth;
	}
//	public void processBankValueChange(ValueChangeEvent event) {
//		String value = (String) event.getNewValue();
//		if (value != null && !value.equals("none")) {
//			try {
//				branchList = new ArrayList<SelectItem>();
//				List<BankBranch> branches = bankService.getBankBranchByBank(value);
//				if (branches != null) {
//					for (BankBranch branch: branches) {
//						branchList.add(new SelectItem(branch.getId(), branch.getName()));
//					}
//				}
//			} catch(Exception e) {
//				
//			}
//		}
//	}
	public boolean fieldsAreValid() {
		if (selectedTitle == null || selectedTitle.equals("none")) {
			super.setErrorMessage("Please select the title.");
			return false;
		}
		if (selectedGender == null || selectedGender.equals("none")) {
			super.setErrorMessage("Please select the gender.");
			return false;
		}
		if (selectedMaritalStatus == null || selectedMaritalStatus.equals("none")) {
			super.setErrorMessage("Please select the marital status.");
			return false;
		}
		if (selectedCustomerClass == null || selectedCustomerClass.equals("none")) {
			super.setErrorMessage("Please select the customer class.");
			return false;
		}
//		if (selectedBank == null || selectedBank.equals("none")) {
//			super.setErrorMessage("Please select the bank.");
//			return false;
//		}
//		if (selectedBranch == null || selectedBranch.equals("none")) {
//			super.setErrorMessage("Please select the branch.");
//			return false;
//		}
//		if (selectedAccountClass == null || selectedAccountClass.equals("none")) {
//			super.setErrorMessage("Please select the account class.");
//			return false;
//		}
		return true;
	}
	
	public void doEditAction(ActionEvent event) {
		customerId = (String) event.getComponent().getAttributes().get("customerId");
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		try {
			if (this.fieldsAreValid()) {
				getCustomer().setTitle(selectedTitle);
				getCustomer().setGender(Gender.valueOf(selectedGender));
				getCustomer().setMaritalStatus(MaritalStatus.valueOf(selectedMaritalStatus));
				getCustomer().setCustomerClass(CustomerClass.valueOf(selectedCustomerClass));
				getCustomer().setDateOfBirth(DateUtil.convertToXMLGregorianCalendar(dateOfBirth));
				
//				BankBranch branch = bankService.findBankBranchById(selectedBranch);
//				getBankAccount().setBranch(branch);
//				getBankAccount().setAccountClass(BankAccountClass.valueOf(selectedAccountClass));
//				
//				getCustomer().setBranchId(branch.getId());
				
				customer.setStatus(CustomerStatus.AWAITING_APPROVAL);
				super.getCustomerService().updateCustomer(customer, super.getJaasUserName());
			
				super.getContactDetailsService().updateContactDetails(getContactDetails());

//				bankService.updateBankAccount(bankAccount);
		
			} else {
				return "failure";
			}
		} catch(Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Customer details updated successfully.");
		super.getRequestScope().put("customerId", this.getCustomerId());
		super.gotoPage("/teller/viewCustomer.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("customerId", this.getCustomerId());
		super.gotoPage("/teller/viewCustomer.jspx");
		return "cancel";
	}
}
