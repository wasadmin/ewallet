/**
 * 
 */
package zw.co.esolutions.ewallet.reports.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import zw.co.esolutions.ewallet.enums.TransactionCategory;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.reports.enums.TransactionMatchType;
import zw.co.esolutions.ewallet.transaction.pojo.AutoCSVBean;
import zw.co.esolutions.ewallet.transaction.pojo.AutoReconciliationPojo;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;

/**
 * @author taurai
 *
 */
public class AutoReconciliationUtil {

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(AutoReconciliationUtil.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + GenerateReportUtil.class);
		}
	}
	
	private static void log4(String message) {
		LOG.debug(message);
	}
	/**
	 * 
	 */
	public AutoReconciliationUtil() {
		// TODO Auto-generated constructor stub
	}
	

	public static Collection<AutoReconciliationPojo>  doAutoReconciliation(String query, Map<String, Object> parameters, Connection connection, File uploadFile) {
		Collection<AutoReconciliationPojo> autoReport = new ArrayList<AutoReconciliationPojo>();
		TransactionCategory txnCategory;
		try {
			txnCategory = TransactionCategory.valueOf((String) parameters.get("TXN_CATEGORY"));
			//now read the file into objects
			Map<String, AutoReconciliationPojo> equationItems = readAutoReconCSVFile(uploadFile, txnCategory, parameters);
			//log4(">>>>>>>>>>>>>>> Uploaded items = "+equationItems);
			if(equationItems == null ) {
				equationItems = new HashMap<String, AutoReconciliationPojo>();
			}
			
			Map<String, AutoReconciliationPojo> ewalletItems = populateEWalletItems(query, parameters, connection);
			//log4(">>>>>>>>>>>>>>> EWallet items = "+ewalletItems);
			if(ewalletItems == null ) {
				ewalletItems = new HashMap<String, AutoReconciliationPojo>();
			}
			
			Collection<AutoReconciliationPojo> equationOnly = new ArrayList<AutoReconciliationPojo>();
			Collection<AutoReconciliationPojo> ewalletOnly = new ArrayList<AutoReconciliationPojo>();
			Collection<AutoReconciliationPojo> mismatch = new ArrayList<AutoReconciliationPojo>();
						
			//By Status
			Collection<AutoReconciliationPojo> completedOnly = new ArrayList<AutoReconciliationPojo>();
			Collection<AutoReconciliationPojo> failedOnly = new ArrayList<AutoReconciliationPojo>();
			Collection<AutoReconciliationPojo> manualResolveOnly = new ArrayList<AutoReconciliationPojo>();
			Collection<AutoReconciliationPojo> creditRequestOnly = new ArrayList<AutoReconciliationPojo>();
			Collection<AutoReconciliationPojo> bankRequestOnly = new ArrayList<AutoReconciliationPojo>();
			Collection<AutoReconciliationPojo> timeoutOnly = new ArrayList<AutoReconciliationPojo>();
			Collection<AutoReconciliationPojo> otherTransactions = new ArrayList<AutoReconciliationPojo>();
			
			Set<String> allKeys = new HashSet<String>();
			
			allKeys.addAll(equationItems.keySet());
			
			/*if(TransactionCategory.CHARGE.equals(txnCategory)) {
				allKeys.addAll(ewalletItems.keySet());
			}*/
			allKeys.addAll(ewalletItems.keySet());
			
			AutoReconciliationPojo equationItem;
			AutoReconciliationPojo ewalletItem;
			for (String key : allKeys) {
				equationItem = equationItems.get(key);
				ewalletItem = ewalletItems.get(key);
				if(equationItem == null && ewalletItem == null){
					//ignore this one ... it does not exist anywhere
				} else if(equationItem == null){
					
					ewalletItem.setMatchType(TransactionMatchType.EWALLET_ONLY);
					ewalletItem.setMatchTypeNarrative(TransactionMatchType.EWALLET_ONLY.getDescription());
					ewalletItem.setNarrative("No matching Equation Transaction");
					
					ewalletOnly.add(adjustAmount(ewalletItem, parameters));
					
				} else if(ewalletItem == null){
					
					equationItem.setMatchType(TransactionMatchType.EQUATION_ONLY);
					equationItem.setMatchTypeNarrative(TransactionMatchType.EQUATION_ONLY.getDescription());
					equationItem.setNarrative("No matching E-Wallet Transaction");
					
					equationOnly.add(adjustAmount(equationItem, parameters));
					
				} else{
					//if we get here, then ok they exist in both, lets compare amount
					if(ewalletItem.getAmount() != equationItem.getAmount()){
						
						ewalletItem.setMatchType(TransactionMatchType.MISMATCH_AMOUNTS);
						ewalletItem.setMatchTypeNarrative(TransactionMatchType.MISMATCH_AMOUNTS.getDescription());
						ewalletItem.setNarrative("E-Wallet Amount : " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(adjustAmount(ewalletItem, parameters).getAmount()) + ", Equation Amount : " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(adjustAmount(equationItem, parameters).getAmount()));
						ewalletItem.setAmount(0);
						mismatch.add(ewalletItem);
												
					}else if(TransactionStatus.COMPLETED.equals(ewalletItem.getStatus()) || TransactionStatus.AWAITING_COLLECTION.equals(ewalletItem.getStatus())){
						
						equationItem.setMatchType(TransactionMatchType.PERFECT_MATCH);
						equationItem.setMatchTypeNarrative(TransactionMatchType.PERFECT_MATCH.getDescription());
						if(TransactionStatus.COMPLETED.equals(ewalletItem.getStatus())) {
							equationItem.setNarrative("Successful");
						} else {
							equationItem.setNarrative("Awaiting collection");
						}
						equationItem.setTransactionType(ewalletItem.getTransactionType());
						
						completedOnly.add(adjustAmount(equationItem, parameters));
						
					}else if(TransactionStatus.FAILED.equals(ewalletItem.getStatus())){
						
						equationItem.setMatchType(TransactionMatchType.FAILED_IN_EWALLET);
						equationItem.setMatchTypeNarrative(TransactionMatchType.FAILED_IN_EWALLET.getDescription());
						equationItem.setNarrative("FAILED in E-wallet, postings found in Equation");
						equationItem.setTransactionType(ewalletItem.getTransactionType());
						
						failedOnly.add(adjustAmount(equationItem, parameters));
						
					}else if(TransactionStatus.CREDIT_REQUEST.equals(ewalletItem.getStatus())){
						
						equationItem.setMatchType(TransactionMatchType.CREDIT_REQUEST);
						equationItem.setMatchTypeNarrative(TransactionMatchType.CREDIT_REQUEST.getDescription());
						equationItem.setNarrative("CREDIT REQUEST in E-wallet, postings found in Equation");
						equationItem.setTransactionType(ewalletItem.getTransactionType());
						
						creditRequestOnly.add(adjustAmount(equationItem, parameters));
						
					}else if(TransactionStatus.BANK_REQUEST.equals(ewalletItem.getStatus())){
						
						equationItem.setMatchType(TransactionMatchType.BANK_REQUEST);
						equationItem.setMatchTypeNarrative(TransactionMatchType.BANK_REQUEST.getDescription());
						equationItem.setNarrative("BANK REQUEST in E-wallet, postings found in Equation");
						equationItem.setTransactionType(ewalletItem.getTransactionType());
						
						bankRequestOnly.add(adjustAmount(equationItem, parameters));
						
					}else if(TransactionStatus.TIMEOUT.equals(ewalletItem.getStatus())){
						
						equationItem.setMatchType(TransactionMatchType.TIMEOUT);
						equationItem.setMatchTypeNarrative(TransactionMatchType.TIMEOUT.getDescription());
						equationItem.setNarrative("TIMEOUT in E-wallet, postings found in Equation");
						equationItem.setTransactionType(ewalletItem.getTransactionType());
						
						timeoutOnly.add(adjustAmount(equationItem, parameters));
						
					}else if(TransactionStatus.MANUAL_RESOLVE.equals(ewalletItem.getStatus())){
						
						equationItem.setMatchType(TransactionMatchType.MANUAL_RESOLVE);
						equationItem.setMatchTypeNarrative(TransactionMatchType.MANUAL_RESOLVE.getDescription());
						equationItem.setNarrative("MANUAL RESOLVE Required");
						equationItem.setTransactionType(ewalletItem.getTransactionType());
						
						manualResolveOnly.add(adjustAmount(equationItem, parameters));
						
					}else {
						
						equationItem.setMatchType(TransactionMatchType.OTHER);
						equationItem.setMatchTypeNarrative(TransactionMatchType.OTHER.getDescription());
						equationItem.setNarrative(ewalletItem.getStatus()+" in E-Wallet");
						equationItem.setTransactionType(ewalletItem.getTransactionType());
						
						otherTransactions.add(adjustAmount(equationItem, parameters));
						
					}
				}
			}
			if(equationOnly.isEmpty() && ewalletOnly.isEmpty()&& mismatch.isEmpty()){
				log4("All transcations reconciled successfully.");
			}
			
			autoReport.addAll(equationOnly);
			autoReport.addAll(ewalletOnly);
			autoReport.addAll(mismatch);
			
			//Status
			autoReport.addAll(completedOnly);
			autoReport.addAll(failedOnly);
			autoReport.addAll(creditRequestOnly);
			autoReport.addAll(bankRequestOnly);
			autoReport.addAll(manualResolveOnly);
			autoReport.addAll(otherTransactions);
			autoReport.addAll(timeoutOnly);
			
		} catch (Exception e) {
			log4(">>>>>>>>>> EXCEPTION, Message :::: "+e.getMessage());
		}
		return autoReport;
	}
	
	private static CellProcessor[] getAutoReconProcessor() {
		CellProcessor[] autoReconProcessors = new CellProcessor[] {
			
			//From Account String	
			new StrMinMax(0, 10), //stringFrom1
			new StrMinMax(0, 10), //stringFrom2
			new StrMinMax(0, 10), //stringFrom3
			
			//To Account String
			new StrMinMax(0, 10), //stringTo1
			new StrMinMax(0, 10), //stringTo2
			new StrMinMax(0, 10), //stringTo3
			
			//Reference
			new StrMinMax(0, 30), //Message Reference
			
			//Amount
			new Optional(new ParseLong()), // Amount In Double
		    
			//Transaction Date
			new StrMinMax(1, 50), //Date
			
			//Debit / Credit
		    new StrMinMax(0, 1), //Post Code e.g C or D
		  
		    //Value Date
			new StrMinMax(1, 50) //Date
		   		    
		};
		return autoReconProcessors;
	}
	
	
	private static Map<String, AutoReconciliationPojo> readAutoReconCSVFile(File uploadFile, TransactionCategory category, Map<String, Object> parameters) {
		
		CellProcessor[] autoReconProcessors;
		ICsvBeanReader inFile = null;
		String reference;
		AutoCSVBean csvBean;
        AutoReconciliationPojo pojo;
        String fromAccount;
        String toAccount;
        String account;
        String fromEQDate;
        String toEQDate;
        Map<String, AutoReconciliationPojo> uploadFileItems = new HashMap<String, AutoReconciliationPojo>();
		Set<AutoReconciliationPojo> autoCSVBeanSet = new HashSet<AutoReconciliationPojo>();
	        try {
	        	
	        fromEQDate = (String)parameters.get("fromEQDate");
	        toEQDate = (String)parameters.get("toEQDate");
	        	
	        log4(".............. About to call  csv Reader");
	         inFile = new CsvBeanReader(new FileReader(uploadFile), CsvPreference.EXCEL_PREFERENCE);
	         	 
	         autoReconProcessors = getAutoReconProcessor();
	          //If your files are without headers, use below
	          final String[] header = new String[] { "stringFrom1","stringFrom2", "stringFrom3","stringTo1","stringTo2", "stringTo3", "reference", "amount", "transactionDate", "postCode", "valueDate"};
	          //log4(">>>>>>>>>>>>>>>>> CSV Header = "+header);
	          
	         csv_loop : while( (csvBean = inFile.read(AutoCSVBean.class, header, autoReconProcessors)) != null) {
	        	 
	        	 reference = null;
	        	 //log4("Message ID = "+csvBean.getReference());
	        	 reference = csvBean.getReference();
	        	 
	        	 fromAccount = null;
	        	 fromAccount = csvBean.getStringFrom1()+csvBean.getStringFrom2()+csvBean.getStringFrom3();
	        	 //log4(">>>>>>>>>> From Account : "+fromAccount);
	        	 
	        	 toAccount = null;
	        	 toAccount = csvBean.getStringTo1()+csvBean.getStringTo2()+csvBean.getStringTo3();
	        	 //log4(">>>>>>>>>> To Account : "+toAccount);
	        	 
	        	 if(csvBean.getReference() == null || "".equalsIgnoreCase(reference)) {
	        		 continue csv_loop;
	        	 }
	        	 
	        	 account = null;
	        	 account = (String)parameters.get("accountNumber");
	        	 
	        	 if(!(fromAccount.equalsIgnoreCase(account) || toAccount.equalsIgnoreCase(account))) {
	        		 //log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> No Matching Accounts In File : To Account = "+toAccount+", From Acc = "+fromAccount);
	        		 continue csv_loop;
	        	 }
	        	 
	        	 if(!(fromEQDate.equalsIgnoreCase(csvBean.getValueDate()) || toEQDate.equalsIgnoreCase(csvBean.getValueDate()))) {
	        		 //log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Date out of range : "+csvBean.getValueDate());
	        		 continue csv_loop;
	        	 }
	        	 reference = reference.substring(3);
	        	 //log4("Refined Message ID = "+reference);
	        	 
	            if(TransactionCategory.CHARGE.equals(category)) {
	            	if(reference.startsWith("C")) {
	            		pojo = new AutoReconciliationPojo();
	    	            //log4(">>>>>>>>>>>>> Is a "+category+" Txn");
	    	            pojo.setAmount(csvBean.getAmount());
	            		//pojo.setAmount((long)csvBean.getAmount()); // A Fix for testing
	            		
	            		pojo.setMessageId(reference);
	    	            pojo.setTransactionType("Charges from Equation");
	    	            pojo.setTransactionDate(DateUtil.convertEQDateToDate(csvBean.getTransactionDate()));
	    	            pojo.setValueDate(DateUtil.convertEQDateToDate(csvBean.getValueDate()));
	    	            pojo.setFromAccount(fromAccount);
	    	            pojo.setToAccount(toAccount);
	    	            pojo.setPostCode(csvBean.getPostCode());
	    	            
	    	            autoCSVBeanSet.add(pojo);
	    	           
	            	} else {
	            		continue;
	            	}
	            } else if(TransactionCategory.ADJUSTMENT.equals(category)) {
	            	if(reference.startsWith("A")) {
	            		pojo = new AutoReconciliationPojo();
	    	            //log4(">>>>>>>>>>>>> Is a "+category+" Txn");
	    	            pojo.setAmount(csvBean.getAmount());
	            		//pojo.setAmount((long)csvBean.getAmount()); // A Fix for testing
	            		
	            		pojo.setMessageId(reference);
	    	            pojo.setTransactionType(TransactionType.ADJUSTMENT.toString());
	    	            pojo.setTransactionDate(DateUtil.convertEQDateToDate(csvBean.getTransactionDate()));
	    	            pojo.setValueDate(DateUtil.convertEQDateToDate(csvBean.getValueDate()));
	    	            pojo.setFromAccount(fromAccount);
	    	            pojo.setToAccount(toAccount);
	    	            pojo.setPostCode(csvBean.getPostCode());
	    	            
	    	            autoCSVBeanSet.add(pojo);
	    	           
	            	} else {
	            		continue;
	            	}
	            } else if(TransactionCategory.MAIN.equals(category)){
	            	if(reference.startsWith("E") || reference.startsWith("D") || reference.startsWith("Y")) {
	            		//log4(">>>>>>>>>>>>> Is a "+category+" Txn");
	            		pojo = new AutoReconciliationPojo();
	    	            
	    	            pojo.setAmount(csvBean.getAmount());
	            		//pojo.setAmount((long)csvBean.getAmount()); // A Fix for testing
	            		
	    	            pojo.setMessageId(reference);
	    	            pojo.setTransactionType("Transaction from Equation");
	    	            pojo.setTransactionDate(DateUtil.convertEQDateToDate(csvBean.getTransactionDate()));
	    	            pojo.setValueDate(DateUtil.convertEQDateToDate(csvBean.getValueDate()));
	    	            pojo.setFromAccount(fromAccount);
	    	            pojo.setToAccount(toAccount);
	    	            pojo.setPostCode(csvBean.getPostCode());
	    	            
	    	            //log4(">>>>>>>>>>>>>>>>>>>>>>>> date = "+pojo.getTransactionDate());
	    	            
	    	            autoCSVBeanSet.add(pojo);
	    	            
	            	} else {
	            		continue;
	            	}
	            } else if(TransactionCategory.ALL.equals(category)){
	            	//log4(">>>>>>>>>>>>> Is a "+category+" Txn");
            		pojo = new AutoReconciliationPojo();
    	            
    	            pojo.setAmount(csvBean.getAmount());
            		//pojo.setAmount((long)csvBean.getAmount()); // A Fix for testing
            		
    	            pojo.setMessageId(reference);
    	            pojo.setTransactionType("Transaction from Equation");
    	            pojo.setTransactionDate(DateUtil.convertEQDateToDate(csvBean.getTransactionDate()));
    	            pojo.setValueDate(DateUtil.convertEQDateToDate(csvBean.getValueDate()));
    	            pojo.setFromAccount(fromAccount);
    	            pojo.setToAccount(toAccount);
    	            pojo.setPostCode(csvBean.getPostCode());
    	            
    	            //log4(">>>>>>>>>>>>>>>>>>>>>>>> date = "+pojo.getTransactionDate());
    	            
    	            autoCSVBeanSet.add(pojo);
	            } else {
	            	log4("Unkown Transaction category = "+category);
	            }
	            
	            
	          }
	          
	          for(AutoReconciliationPojo autoRecon : autoCSVBeanSet) {
	        	  uploadFileItems.put(autoRecon.getMessageId(), autoRecon);
	          }
	          log4(">>>>>>>>>>>>>>>> Read CSV Completed, items : "+uploadFileItems.size());
	          return uploadFileItems;
	        }catch (Exception e) {
	        	log4(">>>>>>>>>> EXCEPTION, Message :::: "+e.getMessage());
			} finally {
				if(inFile != null) {
					try {
						inFile.close();
					} catch (IOException e) {
						log4(">>>>>>>>>> EXCEPTION, Message :::: "+e.getMessage());
					}
				}
	        }
			
			return null;
	}
	
	private static  Map<String, AutoReconciliationPojo>  populateEWalletItems(String query, Map<String, Object> parameters, Connection connection) {
		log4(">>>>>>>>>>>>>>>>>> Starting to populate e-Wallet data");
		Map<String, AutoReconciliationPojo> databaseItems = new HashMap<String, AutoReconciliationPojo>();
		Statement stmt;
		ResultSet resultSet;
		AutoReconciliationPojo item;
		String param;
		int numberOfQueries = 1;
		int count = 0;
		try {
			param = (String)parameters.get("query");
			
			if(!"empty".equalsIgnoreCase(param)) {
				numberOfQueries = 2;
			}
			
			stmt = connection.createStatement();
			
			while(count < numberOfQueries) {
				
				++ count; 
				
				if(count == 2) {
					query = param;
					log4(">>>>>>>> 2nd Query : "+query);
				}
				
				resultSet = stmt.executeQuery(query);
				while (resultSet.next()) {
					item = new AutoReconciliationPojo();
					item.setAmount(resultSet.getLong("amount"));
					item.setFromAccount(resultSet.getString("fromAccount"));
					item.setToAccount(resultSet.getString("toAccount"));
					item.setMessageId(resultSet.getString("messageId"));
					item.setTransactionType(resultSet.getString("transactionType"));
					item.setTransactionDate(resultSet.getDate("transactionDate"));
					item.setValueDate(resultSet.getDate("valueDate"));
					item.setStatus(TransactionStatus.valueOf(resultSet.getString("status")));
					databaseItems.put(item.getMessageId(), item);
					
				}
			}
			log4(">>>>>>>>>>>>>> Populate e-Wallet Data Completed, Size : "+databaseItems.size());
			return databaseItems;
		} catch (Exception e) {
			log4(">>>>>>>>>> EXCEPTION, Message :::: "+e.getMessage());
			return null;
		} 
		
	}
	
	public static void main(String[] args) {
		try {
			File uploadFile = new File("/home/taurai/csv extracts/4131847300840.CSV");
			readAutoReconCSVFile(uploadFile, TransactionCategory.MAIN, null);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private static AutoReconciliationPojo adjustAmount(AutoReconciliationPojo autoRecon, Map<String, Object> parameters) {
		String accountNumber;
		try {
			accountNumber = (String)parameters.get("accountNumber");
			//if(autoRecon.getPostCode() == null ) {
				if(accountNumber.equalsIgnoreCase(autoRecon.getFromAccount())) {
					autoRecon.setAmount(-autoRecon.getAmount());
				} else if(accountNumber.equalsIgnoreCase(autoRecon.getToAccount())) {
					// Live it positive
				}
			/*} else {
				if("C".equalsIgnoreCase(autoRecon.getPostCode())) {
					
				} else if ("D".equalsIgnoreCase(autoRecon.getPostCode())) {
					autoRecon.setAmount(-autoRecon.getAmount());
				}
			}*/
						
		} catch (Exception e) {
			// TODO: handle exception
		}
		return autoRecon;
	}

}
