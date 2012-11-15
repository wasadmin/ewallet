package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

public class AddBankAccountBean extends PageCodeBase {
	private String bankAccountId;
	private BankAccount bankAccount;
	private List<SelectItem> accountClassList;
	private String selectedAccountClass;
	private List<SelectItem> accountTypeList;
	private String selectedAccountType;
	private List<SelectItem> branchList;
	private String selectedBranch;
	private String customerId;
	private Customer customer;
	private boolean renderInfo;
	
	
	
	public boolean isRenderInfo() {
		return renderInfo;
	}
	public void setRenderInfo(boolean renderInfo) {
		this.renderInfo = renderInfo;
	}
	public String getBankAccountId() {
		
		return bankAccountId;
	}
	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}
	public BankAccount getBankAccount() {
		if(bankAccount==null){
			bankAccount= new BankAccount();
		}
		return bankAccount;
	}
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	
	public void processValueChange(ValueChangeEvent event){
		String newValue=(String) event.getNewValue();
		System.out.println("New value for account type  :"+newValue);
		if(newValue.equalsIgnoreCase(BankAccountType.E_WALLET.name())){
			renderInfo=false;
			System.out.println("rendering false");
		}else{
			System.out.println("rendering true");
			renderInfo=true;
		}
	}
	
	
	public List<SelectItem> getAccountClassList() {
		if (accountClassList == null) {
			accountClassList = new ArrayList<SelectItem>();
			for (BankAccountClass accountClass: BankAccountClass.values()) {
				if (accountClass.equals(BankAccountClass.SYSTEM)) {
					continue;
				}
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
	public List<SelectItem> getAccountTypeList() {
		if (accountTypeList == null) {
			accountTypeList = new ArrayList<SelectItem>();
			accountTypeList.add(new SelectItem("", "--select--"));
			accountTypeList.add(new SelectItem("E_WALLET", "E_WALLET"));
			accountTypeList.add(new SelectItem("SAVINGS", "SAVINGS"));
			accountTypeList.add(new SelectItem("CHEQUE", "CHEQUE"));
			accountTypeList.add(new SelectItem("CURRENT", "CURRENT"));
		}
		return accountTypeList;
	}
	public void setAccountTypeList(List<SelectItem> accountTypeList) {
		this.accountTypeList = accountTypeList;
	}
	public String getSelectedAccountType() {
		return selectedAccountType;
	}
	public void setSelectedAccountType(String selectedAccountType) {
		this.selectedAccountType = selectedAccountType;
	}
	public List<SelectItem> getBranchList() {
		if (branchList == null || branchList.isEmpty()) {
			branchList = new ArrayList<SelectItem>();
			branchList.add(new SelectItem("", "--select--"));
			try {
				Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				BankBranch b = super.getBankService().findBankBranchById(profile.getBranchId());
				List<BankBranch> branches = super.getBankService().getBankBranchByBank(b.getBank().getId());
				if (branches != null) {
					for (BankBranch branch: branches) {
						branchList.add(new SelectItem(branch.getId(), branch.getName()));
					}
				}
			} catch (Exception e) {
				
			}
		}
		return branchList;
	}
	public void setBranchList(List<SelectItem> branchList) {
		this.branchList = branchList;
	}
	public String getSelectedBranch() {
		return selectedBranch;
	}
	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	
	
	@SuppressWarnings("unchecked")
	public String submit() {
		//System.out.println("Customer   :::: "+customer);
		ProfileServiceSOAPProxy profileService=new ProfileServiceSOAPProxy();
		Profile capturerProfile=profileService.getProfileByUserName(getJaasUserName());
		bankAccount.setAccountClass(BankAccountClass.valueOf(selectedAccountClass));
		bankAccount.setType(BankAccountType.valueOf(selectedAccountType));
		bankAccount.setBranch(super.getBankService().findBankBranchById(selectedBranch));
		bankAccount.setOwnerType(OwnerType.CUSTOMER);
		bankAccount.setAccountHolderId(getCustomer().getId());
		bankAccount.setLevel(BankAccountLevel.INDIVIDUAL);
		bankAccount.setStatus(BankAccountStatus.AWAITING_APPROVAL);
		bankAccount.setAccountName(customer.getLastName() + " " + customer.getFirstNames());
		bankAccount.setRegistrationBranchId(customer.getBranchId());
		bankAccount.setBankAccountLastBranch(capturerProfile.getBranchId());
		if(BankAccountType.E_WALLET.equals(bankAccount.getType())){
			bankAccount.setCode("ewallet");
			try {

				
				//System.out.println("adding bank account last bank account branch   ::::::"+capturerProfile.getBranchId());

				bankAccount.setBankAccountLastBranch(capturerProfile.getBranchId());
				bankAccount.setAccountNumber(NumberUtil.formatMobileNumber(bankAccount.getAccountNumber()));
			} catch (Exception e) {
			super.setErrorMessage(e.getMessage());
				e.printStackTrace();
				return "";
			}
		}else{
			try{
				bankAccount.setAccountNumber(NumberUtil.validateAccountNumber(bankAccount.getAccountNumber()));
			}catch(Exception e){
				super.setErrorMessage(e.getMessage());
				return "";
			}
		}
		
		try {
			bankAccount = super.getBankService().createCustomerBankAccount(bankAccount,SystemConstants.SOURCE_APPLICATION_BANK, super.getJaasUserName());
			
			//Create Alert Options For Account
			MobileProfile primaryProfile = null;
			List<MobileProfile> mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customer.getId());
			for(MobileProfile p:mobileProfileList){
				if(p.isPrimary()){
					primaryProfile=p;
					break;
				}
			}
			if(primaryProfile!=null && bankAccount.getId()!=null){
				super.getAlertsService().createAlertOptionsForAccount(bankAccount.getId(), primaryProfile.getId(), super.getJaasUserName());
			}

			super.setInformationMessage("Bank Account created successfully");
		} catch (Exception e) {
			super.setErrorMessage(e.getMessage());
			e.printStackTrace();
		}

		
		
		gotoPage("/csr/viewCustomer.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		//super.getRequestScope().put("customerId", getBankAccount().getAccountHolderId());
		gotoPage("/csr/viewCustomer.jspx");
		return "cancel";
	}
	public String getCustomerId() {
		if(this.customerId==null){
			customerId=(String) getRequestScope().get("customerId");
		}
		System.out.println("customer id   "+customerId);
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Customer getCustomer() {
		if (this.customer==null && this.getCustomerId() != null) {
			customer = super.getCustomerService().findCustomerById(customerId);
			
		}
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
	
}
