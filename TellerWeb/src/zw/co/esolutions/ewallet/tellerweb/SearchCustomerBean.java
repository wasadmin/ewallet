package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class SearchCustomerBean extends PageCodeBase {
	private List<SelectItem> criteriaList;
	private String selectedCriteria;
	private String searchField;
	private List<Customer> customerList;
	
	public List<SelectItem> getCriteriaList() {
		if (criteriaList == null) {
			criteriaList = new ArrayList<SelectItem>();
			criteriaList.add(new SelectItem("mobileNumber", "MOBILE NUMBER"));
			criteriaList.add(new SelectItem("lastName", "LASTNAME"));
		}
		return criteriaList;
	}
	public void setCriteriaList(List<SelectItem> criteriaList) {
		this.criteriaList = criteriaList;
	}
	public String getSelectedCriteria() {
		return selectedCriteria;
	}
	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}
	public String getSearchField() {
		return searchField;
	}
	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}
	
	
	public List<Customer> getCustomerList() {
		return customerList;
	}
	public void setCustomerList(List<Customer> customerList) {
		customerList = (customerList == null)? new ArrayList<Customer>() : customerList;
		this.customerList = customerList;
	}
	public String search() {
		
		try {
			
			customerList = new ArrayList<Customer>();
			
			if ("mobileNumber".equals(selectedCriteria)) {
				String mobileNumber = NumberUtil.formatMobileNumber(searchField);
				MobileProfile mobileProfile = super.getCustomerService().getMobileProfileByMobileNumber(mobileNumber);
				if (mobileProfile == null) {
					super.setErrorMessage("Customer with this mobile not found.");
					return "failure";
				} else {
					this.getCustomerList().add(mobileProfile.getCustomer());
				}
			} else if ("lastName".equals(selectedCriteria)) {
				List<Customer> customers = super.getCustomerService().getCustomerByLastName(searchField.toUpperCase());
				if (customers != null) {
					this.getCustomerList().addAll(customers);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Customer(s) found.");
		return "search";
	}
	
	@SuppressWarnings("unchecked")
	public String viewCustomer() {
		super.getRequestScope().put("customerId", super.getRequestParam().get("customerId"));
		super.gotoPage("/teller/viewCustomer.jspx");
		return "viewCustomer";
	}
	
}
