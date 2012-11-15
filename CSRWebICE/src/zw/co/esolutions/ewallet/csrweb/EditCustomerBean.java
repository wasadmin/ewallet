package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.Gender;
import zw.co.esolutions.ewallet.customerservices.service.MaritalStatus;
import zw.co.esolutions.ewallet.enums.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.enums.Title;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
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
	
	
	
	private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(EditCustomerBean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + EditCustomerBean.class);
		}
	}
	

	
	
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
		if (this.getCustomerId() != null && customer==null) {
				customer = super.getCustomerService().findCustomerById(customerId);
				this.setSelectedCustomerClass(customer.getCustomerClass().name());
				selectedGender = (customer.getGender() == null)? Gender.UNSPECIFIED.name() : customer.getGender().name();
				selectedMaritalStatus = (customer.getMaritalStatus() == null)? MaritalStatus.UNSPECIFIED.name() : customer.getMaritalStatus().name();
				if (customer.getTitle() == null || "".equals(customer.getTitle().trim())) {
					this.setSelectedTitle(Title.NONE.name());
				} else {
					this.setSelectedTitle(customer.getTitle());
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
				contactDetails = super.getContactService().findContactDetailsById(customer.getContactDetailsId());
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
			ProfileServiceSOAPProxy profileProxy= new ProfileServiceSOAPProxy();
			Profile capturerProfile=profileProxy.getProfileByUserName(getJaasUserName());
			if (this.fieldsAreValid()) {
				getCustomer().setTitle(selectedTitle);
				getCustomer().setGender(Gender.valueOf(selectedGender));
				getCustomer().setMaritalStatus(MaritalStatus.valueOf(selectedMaritalStatus));
				getCustomer().setCustomerClass(CustomerClass.valueOf(selectedCustomerClass));
				getCustomer().setDateOfBirth(DateUtil.convertToXMLGregorianCalendar(dateOfBirth));
				LOG.debug("#######              selectedTitle :" + selectedTitle);
//				BankBranch branch = bankService.findBankBranchById(selectedBranch);
//				getBankAccount().setBranch(branch);
//				getBankAccount().setAccountClass(BankAccountClass.valueOf(selectedAccountClass));
//				
//				getCustomer().setBranchId(branch.getId());
				
				customer.setStatus(CustomerStatus.AWAITING_APPROVAL);
				
				customer.setCustomerLastBranch(capturerProfile.getBranchId());
				LOG.debug(">>>>>>>>>>>>>>>"+customer.getLastName());
				super.getCustomerService().updateCustomer(customer, super.getJaasUserName());
				LOG.debug("     postal address  :::::::"+getContactDetails().getPostalAddress());
				LOG.debug("     postal address  :::::::"+getContactDetails().getStreet());
			
				super.getContactService().editContactDetails(getContactDetails(),super.getJaasUserName());

//				bankService.updateBankAccount(bankAccount);
				
				
			} else {
				return "failure";
			}
		} catch(Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		cleanUpSessionScope();
		this.getRequestScope().remove("customerId");
		super.setInformationMessage("Customer details updated successfully.");
		customer=null;
		super.getRequestScope().put("customerId", this.getCustomerId());
		this.setFieldsToNull();
		super.gotoPage("/csr/viewCustomer.jspx");
		return "submit";
	}
	
	private void setFieldsToNull() {
		this.setContactDetails(null);
		this.setCustomer(null);
		this.setCustomerId(null);
		this.setDateOfBirth(null);
		this.setSelectedAccountClass(null);
		this.setSelectedCustomerClass(null);
		this.setSelectedGender(null);
		this.setSelectedTitle(null);
		this.setSelectedMaritalStatus(null);
	}
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("customerId", this.getCustomerId());
		super.gotoPage("/csr/viewCustomer.jspx");
		return "cancel";
	}
}
