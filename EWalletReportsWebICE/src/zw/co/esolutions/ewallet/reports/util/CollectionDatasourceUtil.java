package zw.co.esolutions.ewallet.reports.util;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import zw.co.esolutions.ewallet.bankservices.service.AccountBalance;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.transaction.pojo.AccountStatementPojo;
import zw.co.esolutions.ewallet.util.DateUtil;

public class CollectionDatasourceUtil {
	
	public static Collection<AccountStatementPojo>  populateAccountStatementCollection(String query, Map<String, Object> parameters, Connection connection) {
		
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String accountId = (String)parameters.get("accountId");	
		Timestamp time = (Timestamp)parameters.get("asAtDate");
		Date d = DateUtil.convertTimestampToDate(time);
		
		Collection<AccountStatementPojo> accountStatementReort = new ArrayList<AccountStatementPojo>();		
		AccountStatementPojo statement = new AccountStatementPojo();
		try{
			AccountBalance balance = bankService.getOpeningBalance(accountId,DateUtil.convertToXMLGregorianCalendar(d));
			statement.setSumBefore(balance.getAmount());
		}catch(Exception e){
			e.printStackTrace();
			statement.setSumBefore(0);
		}
		statement.setAccountId(accountId);
		statement.setAmount(0);
		statement.setMessageId("");
		statement.setNarrative("");
		statement.setTransactionType(null);
		statement.setValueDate(null);
		
		accountStatementReort.add(statement);
		
		return accountStatementReort;
	}
}
