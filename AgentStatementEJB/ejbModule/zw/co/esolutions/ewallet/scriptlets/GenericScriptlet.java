/**
 * 
 */
package zw.co.esolutions.ewallet.scriptlets;

import java.sql.Timestamp;
import java.util.Date;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.BankAccountType;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.process.TransactionLocationType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;

/**
 * @author taurai
 *
 */
public class GenericScriptlet extends JRDefaultScriptlet {
	
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(GenericScriptlet.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + GenericScriptlet.class);
		}
	}
	
	private static void log4(String message) {
		LOG.debug(message);
	}
	/**
	 * 
	 */
	public GenericScriptlet() {
		// TODO Auto-generated constructor stub
	}
	@Override
	
	public void afterReportInit() {
		//System.out.println(">>>>>>>>>>>>>>>>>>> Afta Scriptlet");
		try {
			//System.out.println(">>>>>>>>>>> Bank Id "+super.getFieldValue("bankId"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String dateCreated() {
		String value = "";
		try {
			Date date = DateUtil.convertTimestampToDate((Timestamp)super.getFieldValue("dateCreated"));
			value = DateUtil.convertToDateWithTime(date);
			super.setVariableValue("v_dateCreated", value);
		} catch (JRScriptletException e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String dateOfBirth() {
		String value = "";
		try {
			Date date = DateUtil.convertTimestampToDate((Timestamp)super.getFieldValue("DATEOFBIRTH"));
			value = DateUtil.convertDateToString(date);
		} catch (JRScriptletException e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String simpleDate() {
		String value = "";
		try {
			Date date = DateUtil.convertTimestampToDate((Timestamp)super.getFieldValue("DATEOFBIRTH"));
			value = DateUtil.convertToShortUploadDateFormatNumbersOnly(date);
			
		} catch (JRScriptletException e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String valueDate() {
		String value = "";
		try {
			Date date = DateUtil.convertTimestampToDate((Timestamp)super.getFieldValue("valueDate"));
			value = DateUtil.convertToDateWithTime(date);
			super.setVariableValue("v_valueDate", value);
		} catch (JRScriptletException e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String responseCode() {
		String value = "";
		try {
			value = (String)super.getFieldValue("responseCode");
			value = value.replace("E", "");
			super.setVariableValue("v_responseCode", value);
		} catch (Exception e) {
			
		}
		return value;
	}
	
	public String targetMobile() {
		String value = "";
		try {
			value = (String)super.getFieldValue("targetMobile");
			value = value.replace("263", "0");
			super.setVariableValue("v_targetMobile", value);
		} catch (Exception e) {
			
		}
		return value;
	}
	
	public String profileId() {
		String value = "";
		try {
			ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
			BankServiceSOAPProxy bs = new BankServiceSOAPProxy();
			value = (String)super.getFieldValue("profileId");
			Profile p = ps.findProfileById(value);
			@SuppressWarnings("unused")
			BankBranch bb = bs.findBankBranchById(p.getBranchId());
			value = p.getLastName()+" "+p.getFirstNames()+" ["+p.getUserName()+"] ";//      "+bb.getBank().getName()+" : "+bb.getName();
			super.setVariableValue("v_profileId", value);
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public long depositCount() {
		long value = 0;
		try {
			String type = (String)super.getFieldValue("transactionType");
			Object obj = super.getVariableValue("v_depositCount");
			if(obj != null) {
				value = (Long)obj;
			}
			if(TransactionType.DEPOSIT.equals(TransactionType.valueOf(type))) {
				System.out.println(">>>>>>>>>>>>>>> Init Value "+value);
				++value;
				
			}
			super.setVariableValue("v_depositCount", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String sourceMobile() {
		String value = "";
		try {
			value = (String)super.getFieldValue("sourceMobile");
			value = value.replace("263", "0");
			super.setVariableValue("v_sourceMobile", value);
		} catch (Exception e) {
			
		}
		return value;
	}
	
	public String mobile() {
		String value = "";
		try {
			value = (String)super.getFieldValue("mobileNumber");
			value = value.replace("263", "0");
			
		} catch (Exception e) {
			
		}
		return value;
	}
	
	public String narrative() {
		String value = "";
		try {
			value = (String)super.getFieldValue("narrative");
			value = value.replace(BankAccountType.E_WALLET.toString(), "account with amount from E-Wallet");
			
		} catch (Exception e) {
			
		}
		return value;
	}

	public String transactionType() {
		String value = "";
		try {
			value = (String)super.getFieldValue("transactionType");
			TransactionType txnType = TransactionType.valueOf(value);
			if(TransactionType.EWALLET_BILLPAY.equals(txnType)) {
				value = TransactionType.BILLPAY.toString();
			} else if(TransactionType.EWALLET_TOPUP.equals(txnType)) {
				value = TransactionType.TOPUP.toString();
			} else if(TransactionType.BALANCE.equals(txnType)) {
				value = TransactionType.BALANCE_REQUEST.toString();
			}
			value = value.replace("_", " ");
			super.setVariableValue("v_transactionType", value);
		} catch (Exception e) {
			
		}
		return value;
	}
	public String status() {
		String value = "";
		try {
			value = (String)super.getFieldValue("status");
			value = value.replace("_", " ");
			super.setVariableValue("v_status", value);
		} catch (Exception e) {
			
		}
		return value;
	}
	
	public String branchId() {
		String value = "";
		try {
			BankServiceSOAPProxy bes = new BankServiceSOAPProxy();
			Object obj = super.getFieldValue("transactionLocationId");
			if(obj == null) {
				obj = super.getFieldValue("branchId");
			}
			BankBranch br = bes.findBankBranchById((String)obj);
			if(br != null && br.getId() != null) {
				value = br.getName();
			} else {
				value = (String)obj;
				
			}
			super.setVariableValue("v_branchId", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String bankId() {
		String value = "";
		try {
			BankServiceSOAPProxy bes = new BankServiceSOAPProxy();
			Object obj = super.getFieldValue("fromBankId");
			if(obj == null) {
				obj = super.getFieldValue("bankId");
			}
			LOG.debug(">>>>>>>>>>>>>>>>>>In Scriptlet, Bank ID = "+(String)obj);
			Bank br = bes.findBankById((String)obj);
			if(br != null && br.getId() != null) {
				value = br.getName();
			} else {
				value = (String)obj;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String txnLocationStart() {
		String value = "";
		try {
			BankServiceSOAPProxy bes = new BankServiceSOAPProxy();
			Object obj = super.getFieldValue("transactionLocationId");
			
			if(obj == null) {
				return "";
			}
			
			BankBranch br = bes.findBankBranchById((String)obj);
			if(br != null && br.getId() != null) {
				value = br.getName();
				value = "Transactions at Branch : "+value;
			} else {
				value = (String)obj;
				value = "SMS Transactions : "+value.replace("_", " ").replace("Txns", "");
				
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String txnLocationEnd() {
		String value = "";
		try {
			BankServiceSOAPProxy bes = new BankServiceSOAPProxy();
			Object obj = super.getFieldValue("transactionLocationId");
			
			if(obj == null) {
				return "";
			}
			
			BankBranch br = bes.findBankBranchById((String)obj);
			if(br != null && br.getId() != null) {
				value = br.getName();
				value = "End of Transactions at Branch : "+value;
			} else {
				value = (String)obj;
				value = "End of SMS Transactions : "+value.replace("_", " ").replace("Txns", "");
				
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String txnLocation() {
		String value = "";
		try {
			BankServiceSOAPProxy bes = new BankServiceSOAPProxy();
			Object obj = super.getFieldValue("transactionLocationId");
			
			if(obj == null) {
				return "";
			}
			
			BankBranch br = bes.findBankBranchById((String)obj);
			if(br != null && br.getId() != null) {
				value = br.getName();
				
			} else {
				value = (String)obj;
				
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String branchCode() {
		String value = "";
		try {
			BankServiceSOAPProxy bes = new BankServiceSOAPProxy();
			Object obj = null;
			if(obj == null) {
				obj = super.getFieldValue("branchId");
			}
			BankBranch br = bes.findBankBranchById((String)obj);
			if(br != null) {
				value = br.getCode();
			} else {
				value = (String)obj;
				for(TransactionLocationType loc : TransactionLocationType.values()) {
					if(value.contains(loc.toString())) {
						value = loc.toString();
					}
				}
			}
			//super.setVariableValue("v_branchId", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String fromBankId() {
		String value = "";
		try {
			BankServiceSOAPProxy bes = new BankServiceSOAPProxy();
			Bank br = bes.findBankById((String)super.getFieldValue("fromBankId"));
			value = br.getName();
			super.setVariableValue("v_bankId", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String balSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("balSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_balSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String btbSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("btbSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_btbSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String withNonSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("withNonSum");
			if(obj == null) {
				value = "0.00";
			} else {
				value = "-"+MoneyUtil.convertCentsToDollarsPatternNoCurrency((Long)obj);
			}
			//super.setVariableValue("v_withNonSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String withSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("withSum");
			if(obj == null) {
				value = "0.00";
			} else {
				value = "-"+MoneyUtil.convertCentsToDollarsPatternNoCurrency((Long)obj);
			}
			super.setVariableValue("v_withSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String topupSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("topupSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_topupSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String miniSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("miniSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_miniSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String etnSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("etnSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_etnSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String eteSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("eteSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_eteSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String etbSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("etbSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_etbSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String depositSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("depositSum");
			if(obj == null) {
				value = "0.00";
			} else {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency((Long)obj);
			}
			//System.out.println(">>>>>>>>> Prifile Id In SubReport = "+super.getParameterValue("profileId"));
			//super.setVariableValue("v_depositSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String bankCommissionSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("bankCommissionSum");
			if(obj == null) {
				value = "0.00";
			} else {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency((Long)obj);
			}
			
			//super.setVariableValue("v_depositSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return value;
	}
	
	public String agentCommissionSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("bankAgentCommissionSum");
			if(obj == null) {
				value = "0.00";
			} else {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency((Long)obj);
			}
			
			//super.setVariableValue("v_depositSum", value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return value;
	}
	
	public String billSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("billSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_billSum", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String tellerSum() {
		String value = "";
		long deposit = 0;
		long with = 0;
		long withNon = 0;
		long total = 0;
		try {
			log4(">>>>>>>>>>>>>> Teller Id = "+super.getParameterValue("profileId"));
			Object obj = super.getFieldValue("depositSum");
			if(obj != null) {
				deposit = (Long)obj;
			}
			obj = null;
			obj = super.getFieldValue("withSum");
			if(obj != null) {
				with = (Long)obj;
			}
			
			obj = null;
			obj = super.getFieldValue("withNonSum");
			if(obj != null) {
				withNon = (Long)obj;
			}
			
			total = deposit - with - withNon;
			if(total < 0) {
				total = (-1)*total;
				value = "-"+MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			} else if(total == 0) {
				value = "0.00";
			}else {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			}
			//super.setVariableValue("v_billSum", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String deductedCommissionSum() {
		String value = "";
		long bankComm = 0;
		long agentComm = 0;
		long total = 0;
		try {
			Object obj = super.getFieldValue("bankCommissionSum");
			if(obj != null) {
				bankComm = (Long)obj;
			}
			obj = null;
			obj = super.getFieldValue("bankAgentCommissionSum");
			if(obj != null) {
				agentComm = (Long)obj;
			}
			
			obj = null;
			
			total = bankComm - agentComm;
			if(total < 0) {
				total = (-1)*total;
				value = "-"+MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			} else if(total == 0) {
				value = "0.00";
			}else {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			}
			//super.setVariableValue("v_billSum", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log4(" Deduceted value = "+total);
		return value;
	}
	
	public long tellerLongSum() {
		long deposit = 0;
		long with = 0;
		long withNon = 0;
		long total = 0;
		long parameter = 0;
		try {
			Object obj = super.getFieldValue("depositSum");
			if(obj != null) {
				deposit = (Long)obj;
			}
			obj = null;
			obj = super.getFieldValue("withSum");
			if(obj != null) {
				with = (Long)obj;
			}
			
			obj = null;
			obj = super.getFieldValue("withNonSum");
			if(obj != null) {
				withNon = (Long)obj;
			}
			
			obj = null;
			obj = super.getParameterValue("tellerAmount");
			if(obj != null) {
				parameter = (Long)obj;
			}
			
			total = deposit - with - withNon + parameter;
			super.setVariableValue("tellerAmountSub", total);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}
	
	public long bankLongSum() {
		long bankComm = 0;
		long agentComm = 0;
		long total = 0;
		long parameter = 0;
		try {
			Object obj = super.getFieldValue("bankCommissionSum");
			if(obj != null) {
				bankComm = (Long)obj;
			}
			obj = null;
			obj = super.getFieldValue("bankAgentCommissionSum");
			if(obj != null) {
				agentComm = (Long)obj;
			}
			
			
			obj = null;
			obj = super.getParameterValue("bankAmount");
			if(obj != null) {
				parameter = (Long)obj;
			}
			
			total = bankComm - agentComm + parameter;
			super.setVariableValue("bankAmountSub", total);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return total;
	}
	
	
 public String btnSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("btnSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_btnSum", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
 
 public String sumBefore() {
		String value = "";
		try {
			Object obj = super.getFieldValue("sumBefore");
			long sum = 0;
			if(obj == null) {
				value = "";
			} else {
				sum = (Long)obj;
			}
			if(sum != 0) {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(sum);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String allTotalCashMovement() {
		String value = "";
		try {
			Object obj = super.getVariableValue("tellerAmount");
			if(obj == null) {
				value = "0.00";
			} else {
				long total = (Long)obj;
				if(total < 0) {
					total = (-1)*total;
					value = "-"+MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
				} else if(total == 0){
					value = "0.00";
				} else {
					value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String allTotalCommAnalysis() {
		String value = "";
		try {
			Object obj = super.getVariableValue("bankAmount");
			if(obj == null) {
				value = "0.00";
			} else {
				long total = (Long)obj;
				if(total < 0) {
					total = (-1)*total;
					value = "-"+MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
				} else if(total == 0){
					value = "0.00";
				} else {
					value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String bteSum() {
		String value = "";
		try {
			Object obj = super.getFieldValue("bteSum");
			if(obj == null) {
				value = "0.00";
			}
			super.setVariableValue("v_bteSum", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String agentCommissionAmount() {
		String value = "";
		try {
			Object obj = super.getFieldValue("agentCommissionAmount");
			if(obj == null) {
				value = "0.00";
			} else {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency((Long)obj);
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public long printTellerSum() {
		long value = 0;
		try {
			Object obj = super.getVariableValue("tellerAmount");
			if(obj == null) {
				value = 0;
			} else {
				value = (Long)obj;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return value;
	}
	
	public String asAtDescription() {
		String value = " ";
		try {
			Object ob = super.getParameterValue("asAtDate");
			if(ob != null) {
				Date date = DateUtil.convertTimestampToDate((Timestamp)ob);
				value ="BALANCE AS AT "+DateUtil.convertToDateWithTime(date) +" : ";
			}
			super.setVariableValue("v_as_at", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	public String asAtFromDescription() {
		String value = " ";
		try {
			Object ob = super.getParameterValue("fromDate");
			if(ob != null) {
				Date date = DateUtil.convertTimestampToDate((Timestamp)ob);
				value ="OPENING BALANCE AS AT "+DateUtil.convertToDateWithTime(date) +" : ";
			}
			super.setVariableValue("v_as_at_FromDate", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	public String asAtOpeningBalDescription() {
		String value = " ";
		try {
			Object ob = super.getParameterValue("fromDate");
			if(ob != null) {
				Date date = DateUtil.convertTimestampToDate((Timestamp)ob);
				value ="OPENING BALANCE AS AT "+DateUtil.convertToDateWithTime(date) +" : ";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String asAtClosingBalDescription() {
		String value = " ";
		try {
			Object ob = super.getParameterValue("toDate");
			if(ob != null) {
				Date date = DateUtil.convertTimestampToDate((Timestamp)ob);
				value ="CLOSING BALANCE AS AT "+DateUtil.convertToDateWithTime(date) +" : ";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String accountBalance() {
		String value = "ACCOUNT BALANCE : ";
		try {
			/*Object ob = super.getParameterValue("toDate");
			if(ob != null) {
				Date date = DateUtil.convertTimestampToDate((Timestamp)ob);
				value ="ACCOUNT BALANCE AS AT "+DateUtil.convertToDateWithTime(date) +" : ";
				value = "ACCOUNT BALANCE : ";
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String blank() {
		return "";
	}
	
	public String grandTotalAcc() {
		long allTotal = 0;
		long sumBefore = 0;
		long total = 0;
		String value = "0.00";
		try {
			Object obj = super.getVariableValue("allTotal");
			if(obj != null) {
				allTotal = (Long)obj;
			}
			obj = null;
			obj = super.getFieldValue("sumBefore");
			if(obj != null) {
				sumBefore = (Long)obj;
			}
			
			total = allTotal + sumBefore;
			value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			super.setVariableValue("grand_total", value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public Long customerBranchTotal() {
		long total = 0;
		long bbActive = 0;
		long bbAwait = 0;
		long bbReject = 0;
		long bbDelete = 0;
		try {
			Object obj = super.getVariableValue("bbActive");
			if(obj != null) {
				bbActive = (Long)obj;
			}
			obj = null;
			obj = super.getVariableValue("bbAwait");
			if(obj != null) {
				bbAwait = (Long)obj;
			}
			
			obj = null;
			obj = super.getVariableValue("bbReject");
			if(obj != null) {
				bbReject = (Long)obj;
			}
			
			obj = null;
			obj = super.getVariableValue("bbDelete");
			if(obj != null) {
				bbDelete = (Long)obj;
			}
			
			total = bbActive + bbAwait + bbReject + bbDelete;
			super.setVariableValue("bbTotal", total);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}
	
	public String openingBalCredit() {
		long total = 0;
		String value = "";
		try {
			Object obj = super.getFieldValue("sumBefore");
			if(obj != null) {
				total = (Long)obj;
			}
			if(total > 0) {
			 value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			}
			
			//super.setVariableValue("v_sum_before", value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	 }
	
	public String openingBalDebit() {
		long total = 0;
		String value = "";
		try {
			Object obj = super.getFieldValue("sumBefore");
			if(obj != null) {
				total = (Long)obj;
			}
			if(total < 0) {
			 value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(-total);
			}
			
			//super.setVariableValue("v_sum_before", value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	 }
	
	public String balTotals() {
		long total = 0;
		long credit = 0;
		long debit = 0;
		long sumBefore = 0;
		String value = "0.00";
		try {
			Object obj = super.getVariableValue("v_credit");
			if(obj != null) {
				credit = (Long)obj;
			}
			obj = null;
			obj = super.getVariableValue("v_debit");
			if(obj != null) {
				debit = (Long)obj;
			}
			
			obj = null;
			obj = super.getVariableValue("v_sumBefore");
			if(obj != null) {
				sumBefore = (Long)obj;
			}
			
			total = credit + debit +sumBefore;
			if(total < 0) {
			 value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(-(debit + sumBefore));
			} else if(total > 0) {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(credit + sumBefore);
			} else {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(credit + sumBefore);
			}
			
			//super.setVariableValue("v_sum_before", value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	 }
	
	public String debitCF() {
		long total = 0;
		long credit = 0;
		long debit = 0;
		long sumBefore = 0;
		String value = "";
		try {
			Object obj = super.getVariableValue("v_credit");
			if(obj != null) {
				credit = (Long)obj;
			}
			obj = null;
			obj = super.getVariableValue("v_debit");
			if(obj != null) {
				debit = (Long)obj;
			}
			
			obj = null;
			obj = super.getVariableValue("v_sumBefore");
			if(obj != null) {
				sumBefore = (Long)obj;
			}
			
			total = credit + debit +sumBefore;
			if(total > 0) {
			 value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	 }
	
	public String creditCF() {
		long total = 0;
		long credit = 0;
		long debit = 0;
		long sumBefore = 0;
		String value = "";
		try {
			Object obj = super.getVariableValue("v_credit");
			if(obj != null) {
				credit = (Long)obj;
			}
			obj = null;
			obj = super.getVariableValue("v_debit");
			if(obj != null) {
				debit = (Long)obj;
			}
			
			obj = null;
			obj = super.getVariableValue("v_sumBefore");
			if(obj != null) {
				sumBefore = (Long)obj;
			}
			
			total = credit + debit +sumBefore;
			if(total < 0) {
			 value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(-total);
			}
			
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	 }
	
	public String debitBF() {
		long total = 0;
		long credit = 0;
		long debit = 0;
		long sumBefore = 0;
		String value = "";
		try {
			Object obj = super.getVariableValue("v_credit");
			if(obj != null) {
				credit = (Long)obj;
			}
			obj = null;
			obj = super.getVariableValue("v_debit");
			if(obj != null) {
				debit = (Long)obj;
			}
			
			obj = null;
			obj = super.getVariableValue("v_sumBefore");
			if(obj != null) {
				sumBefore = (Long)obj;
			}
			
			total = credit + debit +sumBefore;
			if(total < 0) {
			 value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(-total);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	 }
	
	public String creditBF() {
		long total = 0;
		long credit = 0;
		long debit = 0;
		long sumBefore = 0;
		String value = "";
		try {
			Object obj = super.getVariableValue("v_credit");
			if(obj != null) {
				credit = (Long)obj;
			}
			obj = null;
			obj = super.getVariableValue("v_debit");
			if(obj != null) {
				debit = (Long)obj;
			}
			
			obj = null;
			obj = super.getVariableValue("v_sumBefore");
			if(obj != null) {
				sumBefore = (Long)obj;
			}
			
			total = credit + debit +sumBefore;
			if(total > 0) {
			 value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			}
			
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	 }
	
	
	public String credit() {
		long total = 0;
		String value = "";
		try {
			Object obj = super.getFieldValue("amount");
			if(obj != null) {
				total = (Long)obj;
			}
			if(total > 0) {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			}
			//super.setVariableValue("v_sum_before", value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String debit() {
		long total = 0;
		String value = "";
		try {
			Object obj = super.getFieldValue("amount");
			if(obj != null) {
				total = (Long)obj;
			}
			if(total < 0) {
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(-total);
			}
			//super.setVariableValue("v_sum_before", value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String openingBal() {
		long total = 0;
		String value = "0.00";
		try {
			Object obj = super.getFieldValue("sumBefore");
			if(obj != null) {
				total = (Long)obj;
			}
			
			value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(total);
			//super.setVariableValue("v_sum_before", value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String addTxnToAmount(){
		String value ="0.00";
		try{
			String bal = (String)super.getFieldValue("AMOUNT");
			
			long lBal = 0;
			
			if(bal != null){
				lBal = Long.parseLong(bal);
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(lBal);
				
			}else{
				value = MoneyUtil.convertCentsToDollarsPatternNoCurrency(lBal);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
}
