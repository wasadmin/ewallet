package zw.co.esolutions.ewallet.customerservices.wrapper;

import java.io.Serializable;
import java.util.Date;

import zw.co.esolutions.ewallet.enums.CustomerClass;
import zw.co.esolutions.ewallet.enums.CustomerStatus;

public class CustomerWrapper implements Serializable {
	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lastName;
	private String nationalID;
	private CustomerClass customerClass;
	private CustomerStatus status;
	private String mobileNumber;
	private Date startDate;
	private Date endDate;
	private String bankAccountNumber;
	private String finacialInstitution;
	private String registrationBranch;
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getNationalID() {
		return nationalID;
	}
	public void setNationalID(String nationalID) {
		this.nationalID = nationalID;
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
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	public String getFinacialInstitution() {
		return finacialInstitution;
	}
	public void setFinacialInstitution(String finacialInstitution) {
		this.finacialInstitution = finacialInstitution;
	}
	public String getRegistrationBranch() {
		return registrationBranch;
	}
	public void setRegistrationBranch(String registrationBranch) {
		this.registrationBranch = registrationBranch;
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
	

	
	
}
