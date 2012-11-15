package zw.co.esolutions.ussd.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ussd.services.USSDService;
import zw.co.esolutions.ussd.util.AccountType;
import zw.co.esolutions.ussd.util.DateUtils;
import zw.co.esolutions.ussd.util.SystemConstants;
import zw.co.esolutions.ussd.util.XmlUtils;

@Entity
public class USSDSession {
	
	@Id
	private String sessionId;
	@Column(length=15)
	private String mobileNumber;
	@Column(length=60)
	private String ussdRequestCode;
	@Column(length=60)
	private String serviceName;
	private boolean authenticated;
	private AccountType accountType;
	private int level;
	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	private List<UserInput> userInput;
	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	private List<UserAccount> userAccounts;
	@Version
	private long version;
	@Column(length=40, name = "BANKID")
	private String bankCode;
	@Temporal(TemporalType.TIMESTAMP)
	private Date transactionTime;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	@Column(length=30)
	private String status;
	@Column(length=60)
	private String customerId;
	@Column(length=350)
	private String optionsMenu;
	private boolean hasEwalletAccounts;
	private boolean hasBankAccounts;
	private transient String responseXml;
	private transient String ussdResponseString;
	private transient String action;
	
	public USSDSession() {
		super();
		dateCreated = new Date();
		status = SystemConstants.STATUS_NEW;
		
	}
	public USSDSession(Map<String, String> attributesMap) {
		this();
		
		this.setSessionId(attributesMap.get(SystemConstants.TRANSACTION_ID));
		this.setMobileNumber(attributesMap.get(SystemConstants.MSISDN));
		this.setServiceName(SystemConstants.SERVICE_NAME_AUTHENTICATION);
		this.setUssdRequestCode(attributesMap.get(SystemConstants.USSD_REQUEST_STRING));
		this.setTransactionTime(DateUtils.parseXmlDate(attributesMap.get(SystemConstants.TRANSACTION_TIME)));
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public boolean isAuthenticated() {
		return authenticated;
	}
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	public AccountType getAccountType() {
		return accountType;
	}
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public List<UserInput> getUserInput() {
		return userInput;
	}
	public void setUserInput(List<UserInput> userInput) {
		this.userInput = userInput;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public Date getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void addUserInput(String key,String value){
		if(userInput == null){
			userInput = new ArrayList<UserInput>();
		}
		UserInput input = getUserInput(key);
		if(input == null){
			input = new UserInput();
			userInput.add(input);
		}
		input.setInputKey(key);
		input.setInputValue(value);
	}
	
	public void removeUserInput(String key) {
		if(userInput != null){
			for(UserInput input : userInput){
				if(input.getInputKey().equals(key)){
					userInput.remove(input);
					return;
				}
			}
		}
		return;
	}
	
	private UserInput getUserInput(String key) {
		if(userInput != null){
			for(UserInput input : userInput){
				if(input.getInputKey().equals(key)){
					return input;
				}
			}
		}
		return null;
	}
	
	public String getResponseXml() {
		return responseXml;
	}
	public void setResponseXml(String responseXml) {
		this.responseXml = responseXml;
	}
	public String getUssdRequestCode() {
		return ussdRequestCode;
	}
	public void setUssdRequestCode(String ussdRequestCode) {
		this.ussdRequestCode = ussdRequestCode;
	}
	public String getUssdResponseString() {
		return ussdResponseString;
	}
	public void setUssdResponseString(String ussdResponseString) {
		this.ussdResponseString = ussdResponseString;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getOptionsMenu() {
		return optionsMenu;
	}
	public void setOptionsMenu(String optionsMenu) {
		this.optionsMenu = optionsMenu;
	}
	
	public void generateXml(){
		
		Map<String, String>  attributeMap = new HashMap<String, String>();
		attributeMap.put(SystemConstants.TRANSACTION_ID, sessionId);
		attributeMap.put(SystemConstants.USSD_RESPONSE_STRING, ussdResponseString);
		
		if(action == null){
			action = SystemConstants.ACTION_REQUEST;
		}
		attributeMap.put(SystemConstants.ACTION, action);
		responseXml = XmlUtils.composeXml(attributeMap);
		//System.out.println("************** generating Xml  responseXml " +  responseXml +  " Response String " + ussdResponseString);
	}
	public void endSession() {
		this.setAction(SystemConstants.ACTION_END);
		this.setStatus(SystemConstants.STATUS_DELETE);
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public List<UserAccount> getUserAccounts() {
		return userAccounts;
	}
	public void setUserAccounts(List<UserAccount> userAccounts) {
		this.userAccounts = userAccounts;
	}
	
	public void addUserAccount(String accountType,String accountNumber){
		if(userAccounts == null){
			userAccounts = new ArrayList<UserAccount>();
		}
		UserAccount account = new UserAccount(accountNumber,accountType);
		userAccounts.add(account);
	}
	
	public void addUserMerchantAccount(String merchantName,String accountNumber){
		if(userAccounts == null){
			userAccounts = new ArrayList<UserAccount>();
		}
		UserAccount account = getMerchantAccount(merchantName);
		if(account != null){
			return;
		}
		account = new UserAccount();
		account.setAccountType(SystemConstants.ACCOUNT_TYPE_MERCHANT);
		account.setMerchantName(merchantName);
		account.setAccountNumber(accountNumber);
		userAccounts.add(account);
	}
	
	public UserAccount getMerchantAccount(String merchantName){
		if(userAccounts != null){
			for(UserAccount userAccount : userAccounts){
				if(SystemConstants.ACCOUNT_TYPE_MERCHANT.equals(userAccount.getAccountType()) && merchantName.equals(userAccount.getMerchantName())){
					return userAccount;
				}
			}
		}
		return null;
	}
	
	public String getAccountsMenu(){
		int count = 1;
		StringBuffer buffer = new StringBuffer("\n");
		if(AccountType.E_WALLET.equals(accountType)){
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					if(SystemConstants.ACCOUNT_TYPE_EWALLET.equals(userAccount.getAccountType())){
						buffer.append(count++ + ". " + userAccount.getAccountNumber() + "\n");
					}
				}
			}
		}else if(AccountType.BANK_ACCOUNT.equals(accountType)){
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					if(SystemConstants.ACCOUNT_TYPE_BANK_ACCOUNT.equals(userAccount.getAccountType())){
						buffer.append(count++ + ". " + userAccount.getAccountNumber() + "\n");
					}
				}
			}
		} else {
			
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					
					if(SystemConstants.ACCOUNT_TYPE_EWALLET.equals(userAccount.getAccountType())){
						buffer.append(count++ + ". " + userAccount.getAccountNumber() + "\n");
					}
					
					if(SystemConstants.ACCOUNT_TYPE_BANK_ACCOUNT.equals(userAccount.getAccountType())){
						buffer.append(count++ + ". " + userAccount.getAccountNumber() + "\n");
					}
				}
			}
		}
		
		return buffer.toString();
	}
	
	public String generateAccountsOptionsMenu(){
		int count = 1;
		Map<String, String> map = new HashMap<String, String>();
		if(AccountType.E_WALLET.equals(accountType)){
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					if(SystemConstants.ACCOUNT_TYPE_EWALLET.equals(userAccount.getAccountType())){
					map.put(""+ count++, userAccount.getAccountNumber());
					}
				}
			}
		}else if(AccountType.BANK_ACCOUNT.equals(accountType)){
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					if(SystemConstants.ACCOUNT_TYPE_BANK_ACCOUNT.equals(userAccount.getAccountType())){
						map.put(""+ count++, userAccount.getAccountNumber());
					}
				}
			}
		} else {
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					
					if(SystemConstants.ACCOUNT_TYPE_EWALLET.equals(userAccount.getAccountType())){
						
						map.put(""+ count++, userAccount.getAccountNumber());
						
					} else if(SystemConstants.ACCOUNT_TYPE_BANK_ACCOUNT.equals(userAccount.getAccountType())){
						
						map.put(""+ count++, userAccount.getAccountNumber());
						
					}
				}
			}
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
		optionsMenu =MapUtil.convertAttributesMapToString(map);
		return optionsMenu;
	}
	
	public int numberOfAccounts(){
		int count = 0;
		if(AccountType.E_WALLET.equals(accountType)){
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					if(SystemConstants.ACCOUNT_TYPE_EWALLET.equals(userAccount.getAccountType())){
						count++;
					}
				}
			}
		}else if(AccountType.BANK_ACCOUNT.equals(accountType)){
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					if(SystemConstants.ACCOUNT_TYPE_BANK_ACCOUNT.equals(userAccount.getAccountType())){
						count++;
					}
				}
			}
		}
		return count;
	}
	
	
	public List<UserAccount> getAppropriateUserAccounts(){
		
		List<UserAccount> accounts = new ArrayList<UserAccount>();
		if(AccountType.E_WALLET.equals(accountType)){
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					if(SystemConstants.ACCOUNT_TYPE_EWALLET.equals(userAccount.getAccountType())){
						accounts.add(userAccount);
					}
				}
			}
		}else if(AccountType.BANK_ACCOUNT.equals(accountType)){
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					if(SystemConstants.ACCOUNT_TYPE_BANK_ACCOUNT.equals(userAccount.getAccountType())){
						accounts.add(userAccount);
					}
				}
			}
		}else{
		  accounts.addAll(userAccounts);
		}
		return accounts;
	}
	
	public String generateOptionsMenu(String options){
		String[] values= options.split(",");
		Map<String, String> map = new HashMap<String, String>();
		for(int i=1; i<=values.length ; i++ ){
			map.put(""+ i, values[i-1]);
		}
		optionsMenu =MapUtil.convertAttributesMapToString(map);
		return optionsMenu;
	}
	
	public void createOptionMenuFromMap(Map<String,String> optionsMap){
		optionsMenu =MapUtil.convertAttributesMapToString(optionsMap);
	}
	
	public Map<String,String> getOptionsMenuMap(){
		return MapUtil.convertAttributesStringToMap(optionsMenu);
	}
	public String getInput(String key) {
		if(userInput != null){
			for (UserInput input : userInput) {
				if(key.equals(input.getInputKey())){
					return input.getInputValue();
				}
			}
		}
		return null;
	}
	public void addBackAndExitToOptionsMenu() {
		Map<String,String> options = getOptionsMenuMap();
		if(options == null){
			options = new HashMap<String, String>();
		}
		options.put(SystemConstants.BACK_SYMBOL, SystemConstants.BACK);
		options.put(SystemConstants.EXIT_SYMBOL, SystemConstants.EXIT);
		optionsMenu =MapUtil.convertAttributesMapToString(options);
		
	}
	public boolean hasEwalletAccounts() {
		return hasEwalletAccounts;
	}
	public void setHasEwalletAccounts(boolean hasEwalletAccounts) {
		this.hasEwalletAccounts = hasEwalletAccounts;
	}
	public boolean hasBankAccounts() {
		return hasBankAccounts;
	}
	public void setHasBankAccounts(boolean hasBankAccounts) {
		this.hasBankAccounts = hasBankAccounts;
	}
	
	public void addExitToOptionsMenu() {
		Map<String,String> options = getOptionsMenuMap();
		if(options == null){
			options = new HashMap<String, String>();
		}
		options.put(SystemConstants.EXIT_SYMBOL, SystemConstants.EXIT);
		optionsMenu =MapUtil.convertAttributesMapToString(options);
	}
	
	public void addConfirmAndCancelToOptionsMenu() {
		Map<String,String> options = getOptionsMenuMap();
		if(options == null){
			options = new HashMap<String, String>();
		}
		options.put(SystemConstants.CONFIRM_SYMBOL, SystemConstants.CONFIRM);
		options.put(SystemConstants.CANCEL_SYMBOL, SystemConstants.CANCEL);
		optionsMenu =MapUtil.convertAttributesMapToString(options);
		
	}
	
    public UserAccount getEWalletUserAccount(){
		
			if(userAccounts != null){
				for(UserAccount userAccount : userAccounts){
					if(SystemConstants.ACCOUNT_TYPE_EWALLET.equals(userAccount.getAccountType())){
						return userAccount;
					}
				}
			}
		
		return null;
	}
    
    public boolean isAgent() {
    	boolean isAgent = false;
		String agentInfor = this.getInput(SystemConstants.AGENT_INITIATOR);
		if(agentInfor != null) {
			isAgent = true;
		}
		return isAgent;
    }
    
    public boolean isMerchactAccountSelected() {
    	boolean isARegistered = false;
		String registeredInfor = this.getInput(SystemConstants.MERCHANT_OWN_ACCOUNT);
		if(registeredInfor != null) {
			isARegistered = true;
		}
		return isARegistered;
    }
    
    public boolean isAutoPinChange() {
    	boolean isAuto = false;
		String pinChangeInfor = this.getInput(SystemConstants.AUTO_PIN_CHANGE);
		if(pinChangeInfor != null) {
			isAuto = true;
		}
		return isAuto;
    }
	
    public boolean isMerchantAccount(String merchantAccount){
    	boolean isValid = false;
		if(userAccounts != null){
			for(UserAccount userAccount : userAccounts){
				if(SystemConstants.ACCOUNT_TYPE_MERCHANT.equals(userAccount.getAccountType()) && merchantAccount.equals(userAccount.getAccountNumber())){
					return true;
				}
			}
		}
		return isValid;
	}
    
    public boolean isTXT() {
    	boolean isTxt = false;
    	String txt = this.getInput(SystemConstants.TOPUP_TYPE);
		if(USSDService.TXT.equals(txt)) {
			isTxt = true;
		} else {
			isTxt = false;
		}
		return isTxt;
    }
	
}