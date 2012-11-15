package pagecode;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot ;
import javax.faces.context.FacesContext;
import javax.security.auth.Subject;

import zw.co.esolutions.ewallet.alertsservices.service.AlertsServiceSOAPProxy;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.limitservices.service.LimitServiceSOAPProxy;
import zw.co.esolutions.ewallet.limitservices.service.TransactionType;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.referralservices.service.ReferralServiceSOAPProxy;
import zw.co.esolutions.ewallet.tariffservices.service.TariffServiceSOAPProxy;

/**
 * Provides a common base class for all generated Page Code files.
 */
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class PageCodeBase {
	
	public static final String ERROR_MESSAGE = "Error occured. Operation not completed.";
	private BankServiceSOAPProxy bankServiceSOAPProxy;
	private ContactDetailsServiceSOAPProxy contactDetailsServiceSOAPProxy;
	private LimitServiceSOAPProxy limitServiceSOAPProxy;
	private ProfileServiceSOAPProxy profileServiceSOAPProxy;
	private ReferralServiceSOAPProxy referralServiceSOAPProxy;
	private TariffServiceSOAPProxy tariffServiceSOAPProxy;
	private AuditTrailServiceSOAPProxy auditTrailServiceSOAPProxy;
	private AlertsServiceSOAPProxy alertsServiceSOAPProxy;
	private MerchantServiceSOAPProxy merchantServiceSOAPProxy;
	private CustomerServiceSOAPProxy customerServiceSOAPProxy;
	
	
	public PageCodeBase() {
	}
	
	public TransactionType[] getLimitTnxs() {
		return new TransactionType[]{TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, 
				TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, 
				TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.EWALLET_TO_EWALLET_TRANSFER, 
				TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, TransactionType.WITHDRAWAL_NONHOLDER, 
				TransactionType.WITHDRAWAL, TransactionType.TOPUP, TransactionType.BILLPAY, TransactionType.DEPOSIT,
				TransactionType.AGENT_CASH_DEPOSIT, TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER, 
				TransactionType.AGENT_CUSTOMER_DEPOSIT, TransactionType.AGENT_CUSTOMER_WITHDRAWAL, 
				TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL, TransactionType.AGENT_ACCOUNT_BALANCE
				};
		
	}
	public TransactionType[] getAgentLimitTnxs() {
		return new TransactionType[]{TransactionType.AGENT_ACCOUNT_BALANCE, TransactionType.AGENT_CASH_DEPOSIT, TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER, 
				TransactionType.AGENT_CUSTOMER_DEPOSIT, TransactionType.AGENT_CUSTOMER_WITHDRAWAL, 
				TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL
				};
		
	}
	public TransactionType[] getBranchLimitTnxs() {
		return new TransactionType[]{TransactionType.BALANCE, TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, 
				TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, 
				TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.EWALLET_TO_EWALLET_TRANSFER, 
				TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, TransactionType.WITHDRAWAL_NONHOLDER, 
				TransactionType.WITHDRAWAL, TransactionType.TOPUP, TransactionType.BILLPAY, TransactionType.DEPOSIT,
				TransactionType.RTGS,
		};
		
	}
	
	public zw.co.esolutions.ewallet.tariffservices.service.TransactionType[] getTariffTnxs() {
		return new zw.co.esolutions.ewallet.tariffservices.service.TransactionType[]{
				//zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BALANCE, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.EWALLET_TO_EWALLET_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.WITHDRAWAL_NONHOLDER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.WITHDRAWAL, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.TOPUP, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BILLPAY, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.DEPOSIT,
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.MINI_STATEMENT,
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BALANCE_REQUEST,
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CASH_DEPOSIT, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER,
				
				
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CASH_DEPOSIT, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CUSTOMER_DEPOSIT, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CUSTOMER_WITHDRAWAL, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL,
				
				//Commission Earned
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.COMMISSION_AGENT_DEPOSIT, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.COMMISSION_AGENT_WITHDRAWAL, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.COMMISSION_AGENT_REGISTRATION,
				
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.RTGS	
				
				
				};
		
	}
	
	public zw.co.esolutions.ewallet.tariffservices.service.TransactionType[] getBranchTariffTnxs() {
		return new zw.co.esolutions.ewallet.tariffservices.service.TransactionType[] {
				//zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BALANCE, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.EWALLET_TO_EWALLET_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.WITHDRAWAL_NONHOLDER, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.WITHDRAWAL, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.TOPUP, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BILLPAY, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.DEPOSIT,
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.MINI_STATEMENT,
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BALANCE_REQUEST,
				//zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CASH_DEPOSIT, 
				//zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER
				
		};
		
	}
	
	public zw.co.esolutions.ewallet.tariffservices.service.TransactionType[] getAgentCommissionEarnedTnxs() {
		return new zw.co.esolutions.ewallet.tariffservices.service.TransactionType[]{
				//Commission Earned
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.COMMISSION_AGENT_DEPOSIT, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.COMMISSION_AGENT_WITHDRAWAL, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.COMMISSION_AGENT_REGISTRATION
				
				};
		
	}
	
	
	public zw.co.esolutions.ewallet.tariffservices.service.TransactionType[] getAgentCustomerChargesTnxs() {
		return new zw.co.esolutions.ewallet.tariffservices.service.TransactionType[]{
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CUSTOMER_DEPOSIT, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CUSTOMER_WITHDRAWAL, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL
		};
		
	}
	
	public zw.co.esolutions.ewallet.tariffservices.service.TransactionType[] getAgentChargesTnxs() {
		return new zw.co.esolutions.ewallet.tariffservices.service.TransactionType[]{
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CASH_DEPOSIT, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER
		};
		
	}


	protected void gotoPage(String pageName) {
		if (pageName != null) {
			FacesContext context = getFacesContext();
			UIViewRoot newView =
				context.getApplication().getViewHandler().createView(
					context,
					pageName);
			context.setViewRoot(newView);
			context.renderResponse();
		}
	}

	/**
	 * <p>Return the {@link UIComponent} (if any) with the specified
	 * <code>id</code>, searching recursively starting at the specified
	 * <code>base</code>, and examining the base component itself, followed
	 * by examining all the base component's facets and children.
	 * Unlike findComponent method of {@link UIComponentBase}, which
	 * skips recursive scan each time it finds a {@link NamingContainer},
	 * this method examines all components, regardless of their namespace
	 * (assuming IDs are unique).
	 *
	 * @param base Base {@link UIComponent} from which to search
	 * @param id Component identifier to be matched
	 */
	public static UIComponent findComponent(UIComponent base, String id) {

		// Is the "base" component itself the match we are looking for?
		if (id.equals(base.getId())) {
			return base;
		}

		// Search through our facets and children
		UIComponent kid = null;
		UIComponent result = null;
		Iterator kids = base.getFacetsAndChildren();
		while (kids.hasNext() && (result == null)) {
			kid = (UIComponent) kids.next();
			if (id.equals(kid.getId())) {
				result = kid;
				break;
			}
			result = findComponent(kid, id);
			if (result != null) {
				break;
			}
		}
		return result;
	}

	public static UIComponent findComponentInRoot(String id) {
		UIComponent ret = null;

		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			UIComponent root = context.getViewRoot();
			ret = findComponent(root, id);
		}

		return ret;
	}

	/**
	 * Place an Object on the tree's attribute map
	 * 
	 * @param key
	 * @param value
	 */
	protected void putTreeAttribute(String key, Object value) {
		getFacesContext().getViewRoot().getAttributes().put(key, value);
	}

	/**
	 * Retrieve an Object from the tree's attribute map
	 * @param key
	 * @return
	 */
	protected Object getTreeAttribute(String key) {
		return getFacesContext().getViewRoot().getAttributes().get(key);
	}

	/**
	 * Return the result of the resolved expression
	 * 
	 * @param expression
	 * @return
	 */
	protected Object resolveExpression(String expression) {
		Object value = null;
		if ((expression.indexOf("#{") != -1)
			&& (expression.indexOf("#{") < expression.indexOf('}'))) {
			value =	getFacesContext().getApplication().getExpressionFactory().createValueExpression(
						getFacesContext().getELContext(), expression, Object.class).getValue(getFacesContext().getELContext());
		} else {
			value = expression;
		}
		return value;
	}
	
	/**
	 * Return the managed bean with the given name
	 * 
	 * @param mgdBeanName   the name of the managed bean to retrieve
	 * @return
	 */	
	protected Object getManagedBean( String mgdBeanName ) {
		String expression = "#{" + mgdBeanName + "}";
		return resolveExpression(expression);
	}	

	/**
	 * Resolve all parameters passed in via the argNames/argValues array pair, and 
	 * add them to the provided paramMap. If a parameter can not be resolved, then it
	 * will attempt to be retrieved from a cachemap stored using the cacheMapKey
	 * 
	 * @param paramMap
	 * @param argNames
	 * @param argValues
	 * @param cacheMapKey
	 */
	protected void resolveParams(
		Map paramMap,
		String[] argNames,
		String[] argValues,
		String cacheMapKey) {

		Object rawCache = getTreeAttribute(cacheMapKey);
		Map cache = Collections.EMPTY_MAP;
		if (rawCache instanceof Map) {
			cache = (Map) rawCache;
		}
		for (int i = 0; i < argNames.length; i++) {
			Object result = resolveExpression(argValues[i]);
			if (result == null) {
				result = cache.get(argNames[i]);
			}
			paramMap.put(argNames[i], result);
		}
		putTreeAttribute(cacheMapKey, paramMap);
	}
	
	


	/** 
	 * Returns a full system path for a file path given relative to the web project
	 */
	protected static String getRealPath(String relPath) {
		String path = relPath;
		try {
			URL url =
				FacesContext.getCurrentInstance()
					.getExternalContext()
					.getResource(
					relPath);
			if (url != null) {
				path = url.getPath();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return path;
	}
	
	/** 
	 * Returns an InputStream for a resource at the given path
	 */
	protected static InputStream getResourceInputStream(String relPath) {
		return FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(relPath);
	}
	
	protected void logException(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);
		log(stringWriter.toString());
	}

	protected void log(String message) {
		System.out.println(message);
	}

	public Map getApplicationScope() {
		return getFacesContext().getExternalContext().getApplicationMap();
	}

	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public Map getRequestParam() {
		return getFacesContext().getExternalContext().getRequestParameterMap();
	}

	public Map getRequestScope() {
		return getFacesContext().getExternalContext().getRequestMap();
	}

	public Map getSessionScope() {
		return getFacesContext().getExternalContext().getSessionMap();
	}

	public void setErrorMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}
	public void setInformationMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
	}
	public void setWarningMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
	}
	
	public String getJaasUserName() {
		String userName = null;
		Subject subject = null;
		
		subject = (Subject)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("CurrentUser");
		if(subject == null) {
			return null;
		}
		for( Principal p : subject.getPrincipals().toArray( new Principal [1])) {
			userName = p.getName();
		}
		return userName;
	}

	public AuditTrailServiceSOAPProxy getAuditService() {
		if(auditTrailServiceSOAPProxy==null)
			auditTrailServiceSOAPProxy = new AuditTrailServiceSOAPProxy();
		return auditTrailServiceSOAPProxy;
	}

	public BankServiceSOAPProxy getBankService() {
		if(bankServiceSOAPProxy==null)
			bankServiceSOAPProxy = new BankServiceSOAPProxy();
		return bankServiceSOAPProxy;
	}

	public ContactDetailsServiceSOAPProxy getContactDetailsService() {
		if(contactDetailsServiceSOAPProxy==null)
			contactDetailsServiceSOAPProxy = new ContactDetailsServiceSOAPProxy();
		return contactDetailsServiceSOAPProxy;
	}

	public LimitServiceSOAPProxy getLimitService() {
		if(limitServiceSOAPProxy==null)
			limitServiceSOAPProxy = new LimitServiceSOAPProxy();
		return limitServiceSOAPProxy;
	}

	public ProfileServiceSOAPProxy getProfileService() {
		if(profileServiceSOAPProxy==null)
			profileServiceSOAPProxy = new ProfileServiceSOAPProxy();
		return profileServiceSOAPProxy;
	}

	public ReferralServiceSOAPProxy getReferralService() {
		if(referralServiceSOAPProxy==null)
			profileServiceSOAPProxy=new ProfileServiceSOAPProxy();
		return referralServiceSOAPProxy;
	}

	public TariffServiceSOAPProxy getTariffService() {
		if(tariffServiceSOAPProxy==null)
			tariffServiceSOAPProxy = new TariffServiceSOAPProxy();
		return tariffServiceSOAPProxy;
	}
	
	public AlertsServiceSOAPProxy getAlertsService(){
		if(alertsServiceSOAPProxy==null)
			alertsServiceSOAPProxy = new AlertsServiceSOAPProxy();
		return alertsServiceSOAPProxy;
	}
	
	public MerchantServiceSOAPProxy getMerchantService(){
		if(merchantServiceSOAPProxy==null){
			merchantServiceSOAPProxy = new MerchantServiceSOAPProxy();
		}
		return merchantServiceSOAPProxy;
	}
	
	public CustomerServiceSOAPProxy getCustomerService(){
		if(customerServiceSOAPProxy==null){
			customerServiceSOAPProxy = new CustomerServiceSOAPProxy();
		}
		return customerServiceSOAPProxy;
	}
}