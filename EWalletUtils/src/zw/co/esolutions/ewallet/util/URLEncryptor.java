package zw.co.esolutions.ewallet.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class URLEncryptor {
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(URLEncryptor.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + URLEncryptor.class);
		}
	}
	
	private static void log4(String message) {
		LOG.debug(message);
	}
	private static int number = 3;
	@SuppressWarnings("unused")
	private static int maxChar = 126;
	private static int overFlow3 = 126;
	private static int overFlow2 = 125; 
	private static int overFlow1 = 124;
	private static int maxAllowed = 123;
	private static CharSequence OVERFLOW_3 = "Z1Z2Z6";
	private static CharSequence OVERFLOW_2 = "Y1Y2Y5"; 
	private static CharSequence OVERFLOW_1 = "X1X2X4";
	
	public URLEncryptor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String source = "uTxnLog.jspx SELECT BANKIF.PROCESSTRANSACTION.dateCreated,BANKACCOUNT valueDate, messageId, transactionType, sourceMobile, sourceAccountNumber, targetMobile, destinationAccountNumber, amount, utilityAccount, BANKIF.PROCESSTRANSACTION.status, responseCode, narrative, branchId, fromBankId, transactionLocationType, transactionLocationId, balance, customerName FROM BANKIF.BANKBRANCH, BANKIF.BANK, BANKIF.PROCESSTRANSACTION WHERE  (BANKIF.BANK.id = fromBankId) AND fromBankId = '589003' AND BANKIF.BANKBRANCH.ID = branchId AND BANKIF.PROCESSTRANSACTION.dateCreated >= '2011-05-11 00:00:00.0' AND BANKIF.PROCESSTRANSACTION.dateCreated <= '2011-05-31 23:59:59.099' ORDER BY BANKIF.BANKBRANCH.NAME ASC, BANKIF.PROCESSTRANSACTION.dateCreated DESC ";
		String str = source;
		log4("MAX Character = "+(char)(126));
		log4(" Source     = "+source);
		source = encryptUrl(source);
		log4(" Encrypeted = "+source);
		source = decryptUrl(source);
		log4(" Decrypted  = "+source);
		log4("Is Org str eqaul to source = "+str.equals(source));
	}
	
	public static String encryptUrl(String url) {
		return encryptReportsUrl(url);
	}
	
	public static String decryptUrl(String str) {
		return decryptReportsUrl(str);		
	}
	
	private static List<Character> getURLCharacter() {
		List<Character> chars = Arrays.asList(getUrlCharArray());
		return chars;
	}
	
	private static List<CharSequence> getURLEscapeCodes() {
		List<CharSequence> chars = Arrays.asList(getUrlEscapeCodesArray());
		return chars;
	}
	
	private static Map<Character, CharSequence> getUrlEscapeCharacersMapByCharacter() {
		Map<Character, CharSequence> map = new HashMap<Character, CharSequence>();
		CharSequence[] urlEscapeArray = getUrlEscapeCodesArray();
		int index = 0;
		for(Character ch : getURLCharacter()) {
			map.put(ch, urlEscapeArray[index]);
			index++;
		}
		return map;
	}
	
	private static Map<CharSequence, Character> getUrlCharacersMapByCode() {
		Map<CharSequence , Character> map = new HashMap<CharSequence, Character>();
		Character[] urlCharArray = getUrlCharArray();
		int index = 0;
		for(CharSequence chsq : getURLEscapeCodes()) {
			map.put(chsq, urlCharArray[index]);
			index++;
		}
		return map;
	}
	
	private static Character[] getUrlCharArray() {
		char space = " ".charAt(0);
		Character[] charArray = {space, '$','&', '`', ':', '<', '>', '[', ']', '{', '{', '"', '+', '#', '%', 
				'@', '/', ';', '=', '?', '\\', '^', '|', '~', '\'', ',', '_', '!', '*'};
		return charArray;
	}
	
	private static CharSequence[] getUrlEscapeCodesArray() {
		CharSequence[] charArray = {"%20", "%24","%36", "%60", "%3A", "%3C", "%3E", "%5B", "%5D", "%7B", "%7D", "%22", "%2B",
				"%23", "%25", "%40", "%2F", "%3B", "%3D", "%3F", "%5C", "%5E", "%7C", "%7E", "%27", "%2E", "%5F", "%21", "%2A"};
		return charArray;
	}
	
	public static String encodeUrl(String url) {
		String str = null;
		List<Character> collection = getURLCharacter();
		Map<Character, CharSequence> map = getUrlEscapeCharacersMapByCharacter();
		if(url != null) {
			StringBuffer buffer = new StringBuffer();
			long index = 0;
			boolean isSpecialChar = false;
			char actual; 
			for(char ch : url.toCharArray()) {
				actual = ch;
				if(actual <= maxAllowed) {
					ch = (char)(ch + number);
					if(collection.contains(new Character(ch))) {
						isSpecialChar = true;
					} else {
						isSpecialChar = false;
					}
					
					if(isSpecialChar) {
						CharSequence s = map.get(ch);
						buffer.append(s);
						//log4("Sign : "+ch+""+"   String = "+s);
					} else {
						buffer.append(ch);
					} 
				} else {
					if(actual == overFlow1) {
						buffer.append(OVERFLOW_1);
					} else if(actual == overFlow2) {
						buffer.append(OVERFLOW_2);
					} else {
						buffer.append(OVERFLOW_3);
					}
				}
				index++;
			}
			
			str = buffer.toString();
		
		}
		url =str;
		return url;
	}
	
	public static String decodeURL(String str) {
		String url = null;
		List<CharSequence> collection = getURLEscapeCodes();
		Map<CharSequence, Character> map = getUrlCharacersMapByCode();
		
		if(str != null) {
			StringBuffer buffer = new StringBuffer();
			long index = 0;
			long lastIndex = 0;
			boolean isSpecialChar = false;
			boolean isOverFlow = false;
			boolean isMisfit = false;
			char retrievedChar = 't';
			CharSequence retrievedCharSeq = "seq";
			CharSequence[] overFlows = {OVERFLOW_1, OVERFLOW_2, OVERFLOW_3};
			for(char ch : str.toCharArray()) {
				
				//Check for overflow characters
				loop1 : for(CharSequence value : overFlows) {
					if(str.substring((int)index).length() < value.length()) {
						isMisfit = true;
					} else {
						isMisfit = false;
					}
					if(!isMisfit && str.substring((int)index, (int)index + value.length()).equals(value)) {
						isOverFlow = true;
						lastIndex = (int)index + value.length();
						retrievedCharSeq = value;
						break loop1;
					} else {
						isOverFlow = false;
					}
				}
				
			if(!isOverFlow) {
				loop : for(CharSequence value : collection) {
					if(str.substring((int)index).length() < value.length()) {
						isMisfit = true;
					} else {
						isMisfit = false;
					}
					if(!isMisfit && str.substring((int)index, (int)index + value.length()).equals(value)) {
						isSpecialChar = true;
						lastIndex = (int)index + value.length();
						retrievedChar = (Character)map.get(value);
						break loop;
					} else {
						isSpecialChar = false;
					}
				}
				
				
				if(isSpecialChar) {
					buffer.append((char)(retrievedChar - number));
								
				} else {
					if(index < lastIndex && index > 0) {
						// Do nothing
					} else {
						buffer.append((char)(ch-number));
					}
				
				}
			} else {
				if(OVERFLOW_1.equals(retrievedCharSeq)) {
					buffer.append((char)(overFlow1));
				} else if(OVERFLOW_2.equals(retrievedCharSeq)) {
					buffer.append((char)(overFlow2));
				} else {
					buffer.append((char)(overFlow3));
				}
			}
				
				index++;
				
				
			}
			url = buffer.toString();
		}
		return url;
		
		
	}
	
	private static String encryptReportsUrl(String url) {
		List<CharSequence> collection = getEntityNames();
		Map<CharSequence, CharSequence> map = getEntitiesShortNamesMapByEntitiesNames();
		for(CharSequence seq : collection) {
			try {
				url = url.replace(seq, map.get(seq));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return url;
	}
	
	private static String decryptReportsUrl(String str) {
		String url = null;
		List<CharSequence> collection = getEntityShortNames();
		Map<CharSequence, CharSequence> map = getEntityNamesMapByShortNames();
		for(CharSequence seq : collection) {
			str = str.replace(seq, map.get(seq));
		}
		url = str;
		return url;
		
		
	}
	
	private static List<CharSequence> getEntityNames() {
		List<CharSequence> chars = Arrays.asList(getEntitiesCharArray());
		return chars;
	}
	
	private static List<CharSequence> getEntityShortNames() {
		List<CharSequence> chars = Arrays.asList(getEntitiesShortNamesArray());
		return chars;
	}
	
	private static Map<CharSequence, CharSequence> getEntityNamesMapByShortNames() {
		Map<CharSequence, CharSequence> map = new HashMap<CharSequence, CharSequence>();
		CharSequence[] entityNamesArray = getEntitiesCharArray();
		int index = 0;
		for(CharSequence ch : getEntitiesShortNamesArray()) {
			map.put(ch, entityNamesArray[index]);
			index++;
		}
		return map;
	}
	
	private static Map<CharSequence, CharSequence> getEntitiesShortNamesMapByEntitiesNames() {
		Map<CharSequence , CharSequence> map = new HashMap<CharSequence, CharSequence>();
		CharSequence[] entityShortNamesArray = getEntitiesShortNamesArray();
		int index = 0;
		for(CharSequence chsq : getEntitiesCharArray()) {
			map.put(chsq, entityShortNamesArray[index]);
			index++;
		}
		return map;
	}
	
	private static CharSequence[] getEntitiesCharArray() {
		CharSequence[] charArray = {"BANKACCOUNT", "BankAccount", "bankAccount", "BANKBRANCH", "BankBranch", "bankBranch",EWalletConstants.DATABASE_SCHEMA, "BANK","Bank", "bank",
									"MOBILEPROFILE", "MobileProfile", "mobileProfile", "CUSTOMER", "Customer", "customer", 
									"primary", "Primary", "DATECREATED", "dateCreated", "DATEOFBIRTH", "dateOfBirth", //Fields
									"CONTACTDETAILS", "ContactDeatails", "contactDetails", "SELECT", "select",
									"PROCESSTRANSACTION", "ProcessTransaction", "procrssTransaction", 
									"TRANSACTIONTYPE","TransactionType", "transactionType", "TXNTYPE","txnType", "TxnType",
									"TRANCACTION", "Transaction", "transaction", "ACCOUNTBALANCE", "AccountBalance", "accountBalance", 
									"ACCOUNT", "Account", "account", 
									"WHERE", "where", " AND ", "FROM", "from", "valueDate",
									"SOURCE", "Source", "source", "DESTINATION", "Destination", "destination", "Target", "target",
									"NUMBER", "Number", "number", 
									"ORDER BY", "CASE", "NAME", "BRANCH", "Branch", "branch", 
									"DISTINCT", "LEFT", "RIGHT", "JOIN"};
		return charArray;
	}
	
	private static CharSequence[] getEntitiesShortNamesArray() {
		/*CharSequence[] charArray = {"BAT", "tBA", "btA","RBC", "BcB", "bhB","ifbt", "KBN","kBn", "nkb",
				"PFMO", "PfoM", "Pfom", "SCTR", "Crts", "rSct", 
				"pyr", "ryP", "DCRT", "dCcd", "FDBT", "rtO",
				"DETC", "CntDe", "ctDns", "LExA", "vaes", 
				"PTCN", "TxP","pnT", 
				"TYXP", "yTpT", "tcsT","TXNTY", "txnTy", "TxnTy", 
				"TRVP", "Trpv", "vtpr", "BLACC", "BlcAc", "aBcv",
				"CCAT", "cAcT", "caTc", 
				"HWR", "rhw", "NAND ", "MFR", "rfm",
				"STRCv", "tcvSr", "csvrt", "VDES", "Devs", "wdst", "Tgrt", "gtrt",
				"RMBR", "bruN", "nbrw",
				"rdO0l", "eEe", "yYy", "HYBY", "Bhct", "tBch"};*/
		CharSequence[] charArray = {"%20", "%24", "%36","%60", "%3A", "%3C","%3E", "%5B","%5D", "%7B",
				"%7D", "%22", "%2B", "%23", "%25", "%40", 
				"%2F", "%3B", "%3D", "%3F", "%5C", "%5E",
				"%7C", "%7E", "%27", "%2E", "%5F", 
				"%21", "%2A", /* End of Real Escape Characters*/"pnT", 
				"TYXP", "yTpT", "tcsT","TXNTY", "txnTy", "TxnTy", 
				"TRVP", "Trpv", "vtpr", "BLACC", "BlcAc", "aBcv",
				"CCAT", "cAcT", "caTc", 
				"HWR", "rhw", "nDnA", "MFR", "rfm", "tEvd",
				"STRCv", "tcvSr", "csvrt", "VDES", "Devs", "wdst", "Tgrt", "gtrt",
				"RMBR", "bruN", "nbrw",
				"rdO0l", "eEe", "yYy", "HYBY", "Bhct", "tBch", 
				"%99", "%9A", "%9C", "%9E"};
		
		return charArray;
	}
	

}
