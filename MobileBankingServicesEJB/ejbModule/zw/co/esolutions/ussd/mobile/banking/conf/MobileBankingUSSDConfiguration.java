package zw.co.esolutions.ussd.mobile.banking.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import zw.co.esolutions.ussd.util.SystemConstants;



public class MobileBankingUSSDConfiguration {
	
	private Properties conf = new Properties();

	private static MobileBankingUSSDConfiguration config;

	private Locale locale;

	private ResourceBundle messages;

	private MessageFormat formatter;

	private static String filename;

	static {
		//USSDConfiguration.load();
	}

	private MobileBankingUSSDConfiguration() {
		// not needed!
	}

	private static void load(String bankCode) {
		try {
			MobileBankingUSSDConfiguration.filename = ("Linux".equalsIgnoreCase(System.getProperty("os.name", "Linux")) ? "" : "C:") + SystemConstants.USSD_CONFIG_FILE;
			MobileBankingUSSDConfiguration conf = new MobileBankingUSSDConfiguration(MobileBankingUSSDConfiguration.filename);
			
			//If Not Branch Codes
			if(SystemConstants.USSD_FILE_SERVICE_CODE_CASE.equalsIgnoreCase(bankCode)) {
				MobileBankingUSSDConfiguration.config = conf;
				return;
			}
			bankCode = conf.getStringValueOf(SystemConstants.BANK_CODE_CASE +bankCode);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> Bank Code Value = "+bankCode);
			if(conf != null && bankCode != null) {
				switch (Integer.parseInt(bankCode)) {
				//ZB Bank
				case 1 : 	MobileBankingUSSDConfiguration.filename = ("Linux".equalsIgnoreCase(System.getProperty("os.name", "Linux")) ? "" : "C:") + SystemConstants.ZB_CONFIG_FILE;
							MobileBankingUSSDConfiguration.config = new MobileBankingUSSDConfiguration(MobileBankingUSSDConfiguration.filename);	
					        break;
				
				//ZABG Bank
				case 2	: 	MobileBankingUSSDConfiguration.filename = ("Linux".equalsIgnoreCase(System.getProperty("os.name", "Linux")) ? "" : "C:") + SystemConstants.ZABG_CONFIG_FILE;
							MobileBankingUSSDConfiguration.config = new MobileBankingUSSDConfiguration(MobileBankingUSSDConfiguration.filename);					
							break;
				
				//CBZ Bank
				case 3	: 	MobileBankingUSSDConfiguration.filename = ("Linux".equalsIgnoreCase(System.getProperty("os.name", "Linux")) ? "" : "C:") + SystemConstants.CBZ_CONFIG_FILE;
							MobileBankingUSSDConfiguration.config = new MobileBankingUSSDConfiguration(MobileBankingUSSDConfiguration.filename);				
							break;
					
				//USSD Config File
				case 10	:	MobileBankingUSSDConfiguration.config = conf;					
							break;

				default	: 	//File Not Found
							MobileBankingUSSDConfiguration.config = null;
							break;
				}
				
			}
		} catch (Exception e) {
			//e.printStackTrace();
			//File Not Found
			MobileBankingUSSDConfiguration.config = null;
		}
	}

	public static void reload(String bankCode) {
		MobileBankingUSSDConfiguration.load(bankCode);
	}

	private MobileBankingUSSDConfiguration(String fileName) {
		try {
			MobileBankingUSSDConfiguration.filename = fileName;
			this.setConf(fileName);
			locale = new Locale(this.getStringValueOf("config.locale.lang", "en"), this.getStringValueOf("config.locale.country", "US"));
			// messages = ResourceBundle.getBundle("Messages", locale);
			formatter = new MessageFormat("", locale);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public static USSDConfiguration getInstance() {
		return USSDConfiguration.config;
	}*/
	
	public static MobileBankingUSSDConfiguration getInstance(String bankCode) {
		MobileBankingUSSDConfiguration.load(bankCode);
		return MobileBankingUSSDConfiguration.config;
	}

	public synchronized void store(OutputStream os, String header) throws IOException {
		conf.store(os, header);

	}

	public synchronized void store() throws IOException {
		FileOutputStream f = new FileOutputStream(MobileBankingUSSDConfiguration.filename);
		conf.store(f, "Configuration");
		f.close();
	}

	public synchronized void setValueOf(String attribute, String value) throws IOException {
		conf.setProperty(attribute, value);
		this.store();
	}

	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method returns null
	 * if the property is not found.
	 * 
	 * @param attribute
	 *            the configuration attribute
	 * @return the value in this configuration with the specified attribute
	 *         value.
	 */
	public String getStringValueOf(String attribute) {
		return conf.getProperty(attribute);
	}

	/**
	 * Gets a property and converts it into an int.
	 */
	public int getIntValueOf(String attribute, int defaultValue) {

		return Integer.parseInt(this.getStringValueOf(attribute, Integer.toString(defaultValue)));
	}

	/**
	 * Gets a property and converts it to long
	 */
	public long getLongValueOf(String attribute) {
		return Long.parseLong(this.getStringValueOf(attribute));
	}

	/**
	 * Gets a property and converts it into a byte.
	 */
	public byte getByteValueOf(String attribute, byte defaultValue) {

		return Byte.parseByte(this.getStringValueOf(attribute, Byte.toString(defaultValue)));
	}

	public String getStringValueOf(String attribute, String defaultValue) {
		String val = conf.getProperty(attribute, defaultValue).trim();
		return val;
	}

	/**
	 * @param fileName
	 *            The name of the file that contains the configurations
	 */
	public synchronized void setConf(String fileName) {
		try {
			conf.load(new FileInputStream(fileName));
		} catch (FileNotFoundException f) {
			f.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public String getMessage(String msgKey, Object[] args) {
		formatter.applyPattern(messages.getString(msgKey));
		return formatter.format(args);
	}

	public ResourceBundle getMessages() {
		return messages;
	}

	public MessageFormat getFormatter() {
		return formatter;
	}

	/**
	 * @return the filename
	 */
	public static String getFilename() {
		return filename;
	}

	@SuppressWarnings("unchecked")
	public Enumeration keys() {
		return conf.keys();
	}
	

}
