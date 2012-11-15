package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

//import com.ibm.ws.webservices.xml.wassysapp.systemApp;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.CustomerWrapper;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.JsfUtil;


public class CustomerSearch extends PageCodeBase {
	private String mobileNumber;
	private String lastName;
	private String nationalId;
	private CustomerClass customerClass;
	private CustomerStatus status;
	private List<Customer> customers;
	private String selectedStatus;
	private String selectedCustomerClass;
	private Date startDate;
	private Date endDate;
	private List<SelectItem> customerClassList;
	private List<SelectItem> customerStatusList;
	private List<SelectItem> criteriaList;
	private String selectedCriteria;
	private String bankAccountNumber;
	private boolean renderBankInfo;
	private boolean renderCustomerInfo;
	
	public boolean isRenderBankInfo() {
		return renderBankInfo;
	}

	public void setRenderBankInfo(boolean renderBankInfo) {
		this.renderBankInfo = renderBankInfo;
	}

	public boolean isRenderCustomerInfo() {
		return renderCustomerInfo;
	}

	public void setRenderCustomerInfo(boolean renderCustomerInfo) {
		this.renderCustomerInfo = renderCustomerInfo;
	}

	public void processCriteriaChange(ValueChangeEvent e){
		String newValue=(String) e.getNewValue();
		if(newValue.equalsIgnoreCase("customerInformation")){
			renderBankInfo=false;
			renderCustomerInfo=true;
		}else if(newValue.equalsIgnoreCase("bankInformation")){
			renderBankInfo=true;
			renderCustomerInfo=false;
		}
		System.out.println("Value      "+newValue);
		System.out.println(" rendered Value Bank   "+renderBankInfo);
		System.out.println(" rendered Value Customer  "+renderCustomerInfo);
		
	}
	
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	public String getSelectedCriteria() {
		return selectedCriteria;
	}
	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}
	
	public List<SelectItem> getCriteriaList() {
		if (criteriaList == null) {
			criteriaList = new ArrayList<SelectItem>();
			criteriaList.add(new SelectItem("", "--Select--"));
			criteriaList.add(new SelectItem("customerInformation", "Customer Information"));
			criteriaList.add(new SelectItem("bankInformation", "Bank Information"));
		}
		return criteriaList;
	}
	public void setCriteriaList(List<SelectItem> criteriaList) {
		this.criteriaList = criteriaList;
	}
	
	public List<SelectItem> getCustomerClassList() {
		if(this.customerClassList==null){
			this.customerClassList=JsfUtil.getSelectItemsAsList(CustomerClass.values(), true);
		}
		return customerClassList;
	}


	public void setCustomerClassList(List<SelectItem> customerClassList) {
		this.customerClassList = customerClassList;
	}


	public List<SelectItem> getCustomerStatusList() {
		if(this.customerStatusList==null){
			this.customerStatusList=JsfUtil.getSelectItemsAsList(CustomerStatus.values(), true);
		}
		return customerStatusList;
	}


	public void setCustomerStatusList(List<SelectItem> customerStatusList) {
		this.customerStatusList = customerStatusList;
	}


	public String getSelectedStatus() {
		return selectedStatus;
	}


	public void setSelectedStatus(String selectedStatus) {
		this.selectedStatus = selectedStatus;
	}


	public String getSelectedCustomerClass() {
		return selectedCustomerClass;
	}


	public void setSelectedCustomerClass(String selectedCustomerClass) {
		this.selectedCustomerClass = selectedCustomerClass;
	}


	public String search(){
		CustomerWrapper wrapper= new CustomerWrapper();
		
		boolean attributesSet = false;
		
		if(renderCustomerInfo){
				
			if(this.getMobileNumber() != null && !"".equals(this.getMobileNumber())) {
				attributesSet = true;
				wrapper.setMobileNumber(this.getMobileNumber());
				
			}	
			
			if(this.getLastName()!=null && !"".equals(this.getLastName())){
				attributesSet = true;
				wrapper.setLastName(this.getLastName());
			}
			if(this.getNationalId()!=null && !"".equals(this.getNationalId())){
				attributesSet = true;
				wrapper.setNationalID(this.getNationalId());
			}
			if(this.getSelectedCustomerClass()!=null && !"".equals(this.getSelectedCustomerClass())){
				wrapper.setCustomerClass(CustomerClass.valueOf(this.getSelectedCustomerClass()));
				attributesSet = true;
			}
			
			if(this.getSelectedStatus()!=null && !"".equals(this.getSelectedStatus())){
				attributesSet = true;
				wrapper.setStatus(CustomerStatus.valueOf(selectedStatus));
			}
			if(this.getStartDate() != null){
				if(this.getEndDate() == null){
					this.setEndDate(new Date());
				}
				
				if(startDate.after(endDate)){
					Date tmp = startDate;
					startDate = endDate;
					endDate = tmp;
				}
				wrapper.setStartDate(DateUtil.convertToXMLGregorianCalendar(startDate));
				wrapper.setEndDate(DateUtil.convertToXMLGregorianCalendar(endDate));
				
				attributesSet=true;
			
		}
			if(attributesSet && wrapper!=null){
				try {
					System.out.println("Wrapper national id  " + wrapper.getEndDate());
					System.out.println("Wrapper getStart Date   "+wrapper.getStartDate());
					List<Customer> customers =null;
					Set<Customer> customerSet=new TreeSet<Customer>();
					
					
					customers = super.getCustomerService().getCustomersByWrapper(wrapper, super.getJaasUserName());
					if (customers != null && customers.size() > 0) {
						System.out.println("SiZE          "+customers.size());
						
						System.out.println("list 1");
						//customerSet.addAll(customers);
						System.out.println("List 2");
						//customers=null;
						//customers=getCustomerList(customerSet);
						customers = this.filterOutAgents(customers);
						long results = customers.size();
						this.setCustomers(customers);
						if(results == 0){
							super.setInformationMessage("No Results found.");
						}else{
							super.setInformationMessage("A total of " + results + " record(s) found.");
						}						
						
					} else {
						super.setInformationMessage("No Results found.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				super.setErrorMessage("Error occurred. Operation aborted");
				}
			}else{
				super.setInformationMessage("Please enter a search parameter(s).");
			}
			
		} else if(renderBankInfo){
			if(this.getBankAccountNumber()!=null && !"".equalsIgnoreCase(this.bankAccountNumber)){
				attributesSet=true;
			}
			if(attributesSet){
			
			BankServiceSOAPProxy proxy = new BankServiceSOAPProxy();
				// BankAccount
				// bankAccount=proxy.getBankAccountByAccountNumber(bankAccountNumber);
				List<BankAccount> bankAccounts = proxy
						.getBankAccountsByAccountNumber(bankAccountNumber);
				List<Customer> customerResults = getCustomers(bankAccounts);
				long results = customerResults.size();
			
				
				
			
			if(results>0){
			setCustomers(customerResults);
			
			super.setInformationMessage("A total of " + results + " record(s) found.");
			}else{
				super.setInformationMessage("No Results found.");
			}
			
			}else{
				super.setInformationMessage("Please enter a search parameter(s).");
			}
		}
		return "Search";
	}


	private List<Customer> getCustomerList(Set<Customer> customerSet) {
		List<Customer> list = new ArrayList<Customer>();
		for(Customer customer : customerSet){
			boolean result=list.add(customer);
			System.out.println("   Results    "+result);
		}
		return list;
	}

	private List<Customer> getCustomers(List<BankAccount> bankAccounts) {
		List<Customer> customers=new ArrayList<Customer>();
		CustomerServiceSOAPProxy proxy = new CustomerServiceSOAPProxy();
		long count=0;
		for(BankAccount bankAcc: bankAccounts){
			String customerId=bankAcc.getAccountHolderId();
			if(customerId!=null){
				System.out.println("Customer Id   "+customerId);
			Customer customer=proxy.findCustomerById(customerId);
			if(customer!=null){
				customers.add(customer);
				++count;
			}
			
			}
		
		}
		System.out.println("Count value ::::::"+count);
		return customers;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}


	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getNationalId() {
		return nationalId;
	}


	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}


	public CustomerClass getCustomerClass() {
		return customerClass;
	}


	public void setCustomerClass(CustomerClass customerClass) {
		this.customerClass = customerClass;
	}


	public CustomerStatus getStatus() {
		return status;
	}


	public void setStatus(CustomerStatus status) {
		this.status = status;
	}


	public List<Customer> getCustomers() {
		return customers;
	}


	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	@SuppressWarnings("unchecked")
	public String viewCustomer() {
		String customerId = (String)super.getRequestParam().get("customerId");
		Customer c = getCustomerService().findCustomerById(customerId);
		CustomerStatus status =c.getStatus();
		super.getRequestScope().put("customerId", customerId);
		super.getRequestScope().put("fromPage", "customerSearch.jspx");
		ProfileServiceSOAPProxy proxy= new ProfileServiceSOAPProxy();
		boolean approver = proxy.canDo(getJaasUserName(), "APPROVE");
	System.out.println("TEST BOOLEAN >>>>>>>>>>>>>>>is it an approver"+approver);
	
		if(CustomerClass.AGENT.equals(c.getCustomerClass())){
			super.gotoPage("/csr/viewAgent.jspx");
		}else{
			System.out.println(">>>>>>>>>>>>>>>>>page result>>>>>>>>>>>"+getPageResult(approver, status));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>customer status>>>>>>>"+status.toString());
			if(getPageResult(approver, status)){
				System.out.println("Approval set, peding approval");
			super.gotoPage("/csr/approveWalletCustomer.jspx");
			}else{
				System.out.println("else");
			super.gotoPage("/csr/viewCustomer.jspx");
			
			}
		}
		return "viewCustomer";
	}
	
	public boolean getPageResult(boolean isApprover, CustomerStatus status){
		if(isApprover && CustomerStatus.AWAITING_APPROVAL.equals(status)){
			return true;
		}else
			return false;
	}
	
	
	private List<Customer> filterOutAgents(List<Customer> cList){
		List<Customer> fList = new ArrayList<Customer>();
		for(Customer customer: cList){
			if(customer.getCustomerClass().name().equals(CustomerClass.AGENT.name())){
				continue;
			}else{
				fList.add(customer);
			}
		}
		return fList;
	}
}
