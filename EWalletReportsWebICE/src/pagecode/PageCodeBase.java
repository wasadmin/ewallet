package pagecode;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRResultSetDataSource;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.BankAccountType;
import zw.co.esolutions.ewallet.enums.TransactionCategory;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.TxnGrp;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.TransactionLocationType;

/**
 * Provides a common base class for all generated code behind files.
 */
@SuppressWarnings("unchecked")
public abstract class PageCodeBase {

	
	public static final String ERROR_MESSAGE = "Error occured. Operation not completed.";
	protected final String BANKID_PARAM = "bankId";
	protected final String BRANCHID_RARAM = "branchId";
	protected final String BANK_DELIMETER_PARAM = "bankNameDelemeter";
	protected final String CONNECTION_DATASOURCE = "datasourcse";
	protected final String BALANCE_REQUEST = TransactionType.BALANCE_REQUEST.toString();
	protected final String DETAILED_REPORT = "Detailed";
	protected final String SUMMARY_REPORT = "Summary";
	private ProcessServiceSOAPProxy processService;
	
	protected String getExceptionsStati() {
		return "('"+TransactionStatus.CREDIT_REQUEST+"','"+TransactionStatus.MANUAL_RESOLVE+"','"+TransactionStatus.REVERSAL_REQUEST+"','"+TransactionStatus.TIMEOUT+"') ";
	}
	
	protected TxnGrp getTxnGroupByTxn(TransactionType txnType) {
		TxnGrp grp = null;
		if(TransactionType.TOPUP.equals(txnType)) {
			grp = TxnGrp.TOPUP;
		} else if (TransactionType.BILLPAY.equals(txnType)) {
			grp = TxnGrp.BILL_PAY;
		} else {
			//Balance Request
			grp = TxnGrp.BALANCE_REQUEST;
		}
		return grp;
	}
	
	protected boolean isCustomerAccount(zw.co.esolutions.ewallet.bankservices.service.BankAccountType accType) {
		return (zw.co.esolutions.ewallet.bankservices.service.BankAccountType.CHEQUE.equals(accType) || 
				zw.co.esolutions.ewallet.bankservices.service.BankAccountType.CURRENT.equals(accType) || 
				zw.co.esolutions.ewallet.bankservices.service.BankAccountType.SAVINGS.equals(accType) || 
				zw.co.esolutions.ewallet.bankservices.service.BankAccountType.MERCHANT_SUSPENSE.equals(accType) ||
				zw.co.esolutions.ewallet.bankservices.service.BankAccountType.E_WALLET.equals(accType));
	} 
	
	protected boolean isAgentAccount(zw.co.esolutions.ewallet.bankservices.service.BankAccountType accType) {
		return (zw.co.esolutions.ewallet.bankservices.service.BankAccountType.AGENT_EWALLET.equals(accType) || 
				zw.co.esolutions.ewallet.bankservices.service.BankAccountType.AGENT_COMMISSION_SUSPENSE.equals(accType));
	} 
	
	protected boolean isBranchAccount(zw.co.esolutions.ewallet.bankservices.service.BankAccountType accType) {
		return (zw.co.esolutions.ewallet.bankservices.service.BankAccountType.BRANCH_CASH_ACCOUNT.equals(accType) || 
				zw.co.esolutions.ewallet.bankservices.service.BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT.equals(accType) ||
				zw.co.esolutions.ewallet.bankservices.service.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT.equals(accType));
	}
	
	protected TransactionType[] getWithdrawals() {
		return new TransactionType[]{TransactionType.WITHDRAWAL, TransactionType.WITHDRAWAL_NONHOLDER, TransactionType.AGENT_CUSTOMER_WITHDRAWAL, TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL};
	}
	
	protected TransactionType[] getTransfers() {
		return new TransactionType[]{TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.EWALLET_TO_EWALLET_TRANSFER, TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, 
				TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, 
				TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.AGENT_EMONEY_TRANSFER, TransactionType.COMMISSION_TRANSFER ,TransactionType.COMMISSION_SWEEPING};
	}
	
	protected TransactionType[] getDeposits() {
		return new TransactionType[]{TransactionType.DEPOSIT, TransactionType.AGENT_CUSTOMER_DEPOSIT, TransactionType.AGENT_CASH_DEPOSIT};
	}
	
	protected TransactionCategory[] getTransactionCategory() {
		return new TransactionCategory[]{TransactionCategory.ALL, TransactionCategory.MAIN, TransactionCategory.CHARGE, TransactionCategory.ADJUSTMENT};
	}
	
	protected TransactionType[] getNonHolderTxns() {
		return new TransactionType[]{TransactionType.WITHDRAWAL_NONHOLDER, TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL};
	}
	
	protected TransactionType[] getTopupTxns() {
		return new TransactionType[]{TransactionType.TOPUP, TransactionType.EWALLET_TOPUP};
	}
	
	protected TransactionType[] getBillTxns() {
		return new TransactionType[]{TransactionType.BILLPAY, TransactionType.EWALLET_BILLPAY};
	}
	
	protected TransactionType[] getAdjustmentsTxns() {
		return new TransactionType[]{TransactionType.ADJUSTMENT};
	}
	
	protected TransactionType[] getTxnLogTxnTypes() {
		return new TransactionType[] {TransactionType.DEPOSIT, TransactionType.WITHDRAWAL, TransactionType.WITHDRAWAL_NONHOLDER
				, TransactionType.BILLPAY, TransactionType.EWALLET_BILLPAY
				, TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.EWALLET_TO_EWALLET_TRANSFER, 
				TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER
				, TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER
				, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, TransactionType.TOPUP, TransactionType.EWALLET_TOPUP, TransactionType.AGENT_CUSTOMER_DEPOSIT
				, TransactionType.AGENT_CUSTOMER_WITHDRAWAL, TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL, TransactionType.AGENT_EMONEY_TRANSFER
				, TransactionType.AGENT_CASH_DEPOSIT, TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.AGENT_SUMMARY
				, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, TransactionType.BALANCE_REQUEST, TransactionType.MINI_STATEMENT
				,TransactionType.COMMISSION_TRANSFER, TransactionType.ADJUSTMENT , TransactionType.COMMISSION_SWEEPING};
	}
	
	protected TransactionType[] getSettlementTxnTypes() {
		return new TransactionType[] {TransactionType.DEPOSIT, TransactionType.WITHDRAWAL, TransactionType.WITHDRAWAL_NONHOLDER, TransactionType.BILLPAY
				, TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.EWALLET_TO_EWALLET_TRANSFER, 
				TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER
				, TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER
				, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, TransactionType.TOPUP, TransactionType.AGENT_CUSTOMER_DEPOSIT
				, TransactionType.AGENT_CUSTOMER_WITHDRAWAL, TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL, TransactionType.AGENT_EMONEY_TRANSFER
				,TransactionType.AGENT_CASH_DEPOSIT, TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER
				,TransactionType.COMMISSION_TRANSFER, TransactionType.ADJUSTMENT};
	}
	
	protected String getSettlementTxnQuery() {
		return "(p.transactionType IN ( '"+TransactionType.DEPOSIT+"', "+
			"'"+TransactionType.WITHDRAWAL+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER+"', "+
			"'"+TransactionType.BILLPAY+"', "+
			"'"+TransactionType.COMMISSION_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_BILLPAY+"', "+
			"'"+TransactionType.EWALLET_TO_EWALLET_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TOPUP+"', "+
			"'"+TransactionType.TOPUP+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_WITHDRAWAL+"', "+
			"'"+TransactionType.AGENT_EMONEY_TRANSFER+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_DEPOSIT+"', "+
			
			//Additional Types for Agents
			"'"+TransactionType.AGENT_CASH_DEPOSIT+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL+"', "+
			"'"+TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER+"', "+
			
			"'"+TransactionType.ADJUSTMENT+"', "+
			"'"+TransactionType.WITHDRAWAL_NONHOLDER+"') ) ";
	}
	
	protected String getTopQuery() {
		return "(p.transactionType IN ( '"+TransactionType.TOPUP+"', "+
			"'"+TransactionType.EWALLET_TOPUP+"') ) ";
	}
	
	protected String getBalQuery() {
		return "(p.transactionType IN ( '"+TransactionType.BALANCE+"', "+
			"'"+TransactionType.BALANCE_REQUEST+"') ) ";
	}
	
	protected String getAdjustmentQuery() {
		return "(p.transactionType IN ('"+TransactionType.ADJUSTMENT+"' ) ) ";
	}
	
	protected String getBillQuery() {
		return "(p.transactionType IN ( '"+TransactionType.BILLPAY+"', "+
			"'"+TransactionType.EWALLET_BILLPAY+"') ) ";
	}
	
	protected String getDepositQuery() {
		return "(p.transactionType IN ( '"+TransactionType.DEPOSIT+"', "+
		    "'"+TransactionType.AGENT_CASH_DEPOSIT+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_DEPOSIT+"') ) ";
	}
	
	protected String getWithdrawalQuery() {
		return "(p.transactionType IN ( '"+TransactionType.WITHDRAWAL+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_WITHDRAWAL+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL+"', "+
			"'"+TransactionType.WITHDRAWAL_NONHOLDER+"') ) ";
	}
	
	protected String getNonHolderQuery() {
		return "(p.transactionType IN ( '"+TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL+"', "+
			"'"+TransactionType.WITHDRAWAL_NONHOLDER+"') ) ";
	}
	
	protected String getTransfersQuery() {
		return "(p.transactionType IN ( '"+TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER+"', "+
			"'"+TransactionType.COMMISSION_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER+"', "+
			"'"+TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.AGENT_EMONEY_TRANSFER+"', "+
			"'"+TransactionType.COMMISSION_SWEEPING+"', "+
			"'"+TransactionType.EWALLET_TO_EWALLET_TRANSFER+"') ) ";
	}
	
	protected String getReconciliationTxnTypeQuery() {
		return "(p.transactionType IN ( '"+TransactionType.DEPOSIT+"', "+
			"'"+TransactionType.WITHDRAWAL+"', "+
			//"'"+TransactionType.WITHDRAWAL_NONHOLDER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER+"', "+
			"'"+TransactionType.TOPUP+"', "+
			"'"+TransactionType.EWALLET_TOPUP+"', "+
			
			//New Stuff For Reconciliation
			"'"+TransactionType.AGENT_CASH_DEPOSIT+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_DEPOSIT+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_WITHDRAWAL+"', "+
			"'"+TransactionType.COMMISSION_SWEEPING+"', "+
			//"'"+TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL+"', "+ //Not sure of this one here for reconcilliation purposes
			
			"'"+TransactionType.EWALLET_TO_EWALLET_TRANSFER+"') ) ";
	}
	
	protected String getAutoReconciliationTxnTypeQuery() {
		String str = "(p.transactionType IN ( '"+TransactionType.DEPOSIT+"', "+
			"'"+TransactionType.WITHDRAWAL+"', "+
			"'"+TransactionType.WITHDRAWAL_NONHOLDER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.TOPUP+"', "+
			"'"+TransactionType.EWALLET_TOPUP+"', "+
			"'"+TransactionType.BILLPAY+"', "+
			"'"+TransactionType.EWALLET_BILLPAY+"', "+
			
			//Agent Transactions
			"'"+TransactionType.AGENT_CASH_DEPOSIT+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_DEPOSIT+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_WITHDRAWAL+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL+"', "+ 
			"'"+TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.AGENT_EMONEY_TRANSFER+"', "+
			
			"'"+TransactionType.COMMISSION_SWEEPING+"', "+
			"'"+TransactionType.COMMISSION_AGENT_DEPOSIT+"', "+
			"'"+TransactionType.COMMISSION_AGENT_WITHDRAWAL+"', "+
			"'"+TransactionType.COMMISSION_TRANSFER+"', "+
			
			"'"+TransactionType.EWALLET_TO_EWALLET_TRANSFER+"') ) ";
		return str;
	}
	
	protected String getAutoReconciliationChargeTxnTypeQuery() {
		String str = "(p.transactionType IS NOT NULL ) ";
		return str;
	}
	
	protected String getTxnGrpQuery(String grp) {
		String value = "";
		TxnGrp txn = TxnGrp.valueOf(grp);
		if(TxnGrp.BILL_PAY.equals(txn)) {
			value = this.getBillQuery();
		}else if(TxnGrp.DEPOSITS.equals(txn)) {
			value = this.getDepositQuery();
		} else if(TxnGrp.NON_HOLDER_TXNS.equals(txn)) {
			value = this.getNonHolderQuery();
		}  else if(TxnGrp.TOPUP.equals(txn)) {
			value = this.getTopQuery();
		}  else if(TxnGrp.TRANSFERS.equals(txn)) {
			value = this.getTransfersQuery();
		} else if(TxnGrp.WITHDRAWALS.equals(txn)) {
			value = this.getWithdrawalQuery();
		} else if(TxnGrp.BALANCE_REQUEST.equals(txn)) {
			value = this.getBalQuery();
		} else if(TxnGrp.ADJUSTMENT.equals(txn)) {
			value = this.getAdjustmentQuery();
		} 
		return value;
	}
	
	protected Map<String, String> populateSMS(Map<String, String> map, String bankId) {
		for(TransactionLocationType loc : TransactionLocationType.values()) {
			try {
				Bank bk = new BankServiceSOAPProxy().findBankById(bankId);
				String sms = bk.getName()+ " "+loc.toString()+" Txns";
				map.put(sms, sms);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	protected TransactionType[] getTxnTypes() {
		return new TransactionType[] {TransactionType.BALANCE_REQUEST, TransactionType.BALANCE_REQUEST, TransactionType.MINI_STATEMENT
				, TransactionType.DEPOSIT, TransactionType.WITHDRAWAL, TransactionType.WITHDRAWAL_NONHOLDER, TransactionType.EWALLET_BILLPAY
				, TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.EWALLET_TO_EWALLET_TRANSFER, 
				TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, TransactionType.EWALLET_TOPUP
				, TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER
				, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, TransactionType.TOPUP, TransactionType.AGENT_CUSTOMER_DEPOSIT
				, TransactionType.AGENT_CUSTOMER_WITHDRAWAL, TransactionType.AGENT_EMONEY_TRANSFER
				, TransactionType.COMMISSION_SWEEPING, TransactionType.COMMISSION_TRANSFER, TransactionType.ADJUSTMENT};
	}
	
	protected String getAllTxnForProcessTxnQuery() {
		return "(p.transactionType IN ( '"+TransactionType.DEPOSIT+"', "+
			"'"+TransactionType.WITHDRAWAL+"', "+
			"'"+TransactionType.BALANCE+"', "+
			"'"+TransactionType.BALANCE_REQUEST+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER+"', "+
			"'"+TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER+"', "+
			"'"+TransactionType.BILLPAY+"', "+
			//"'"+TransactionType.COMMISSION_SWEEPING+"' "+
			"'"+TransactionType.COMMISSION_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_BILLPAY+"', "+
			"'"+TransactionType.EWALLET_TO_EWALLET_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER+"', "+
			"'"+TransactionType.EWALLET_TOPUP+"', "+
			"'"+TransactionType.TOPUP+"', "+
			
			"'"+TransactionType.AGENT_CUSTOMER_WITHDRAWAL+"', "+
			"'"+TransactionType.AGENT_EMONEY_TRANSFER+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_DEPOSIT+"', "+
			"'"+TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL+"', "+
			
			"'"+TransactionType.AGENT_CASH_DEPOSIT+"', "+
			"'"+TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER+"', "+
			"'"+TransactionType.AGENT_SUMMARY+"', "+
			
			"'"+TransactionType.MINI_STATEMENT+"', "+
			"'"+TransactionType.ADJUSTMENT+"', "+
			"'"+TransactionType.COMMISSION_SWEEPING+"', "+
			"'"+TransactionType.WITHDRAWAL_NONHOLDER+"') ) ";
	}
	public static JRResultSetDataSource executeQuery(Connection connection, String query) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
//		int count = 0;
//		while(resultSet.next()) {
//			++count;
//			if(count == 1) {
//				return new JRResultSetDataSource(resultSet);
//			}
//		}
//		return null;
		return new JRResultSetDataSource(resultSet);

	}
	public static boolean deleteDirectory(String sFilePath)
	{
	  File oFile = new File(sFilePath);
	  if(oFile.isDirectory())
	  {
	    File[] aFiles = oFile.listFiles();
	    for(File oFileCur: aFiles)
	    {
	    	deleteDirectory(oFileCur.getAbsolutePath());
	    }
	  }
	  return oFile.delete();
	}
	
	public PageCodeBase() {
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
	
	public static String getJaasUserName() {
		String userName = null;
		
		userName = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("CurrentUser");
		
		return userName;
	}
	
	public BankAccountType[] getAccountType() {
		return new BankAccountType[]{BankAccountType.POOL_CONTROL, BankAccountType.PAYOUT_CONTROL, };
	}
	
	public ProcessServiceSOAPProxy getProcessService() {
		if(processService == null) {
			processService = new ProcessServiceSOAPProxy();
		}
		return processService;
	}
	
	
}