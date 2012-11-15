package zw.co.esolutions.mobile.web.conf;

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

import zw.co.esolutions.mobile.web.utils.WebConstants;




public class MobileWebConfiguration {
	
	private Properties conf = new Properties();

	private static MobileWebConfiguration config;

	private Locale locale;

	private ResourceBundle messages;

	private MessageFormat formatter;

	private static String filename;

	static {
		//USSDConfiguration.load();
	}

	private MobileWebConfiguration() {
		// not needed!
	}

	private static void load() {
		try {
			MobileWebConfiguration.filename = ("Linux".equalsIgnoreCase(System.getProperty("os.name", "Linux")) ? "" : "C:") + WebConstants.MOBILE_WEB_CONFIG_FILE;
			MobileWebConfiguration.config = new MobileWebConfiguration(MobileWebConfiguration.filename);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void reload(String serviceCode) {
		MobileWebConfiguration.load();
	}

	private MobileWebConfiguration(String fileName) {
		try {
			MobileWebConfiguration.filename = fileName;
			this.setConf(fileName);
			locale = new Locale(this.getStringValueOf("config.locale.lang", "en"), this.getStringValueOf("config.locale.country", "US"));
			// messages = ResourceBundle.getBundle("Messages", locale);
			formatter = new MessageFormat("", locale);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static MobileWebConfiguration getInstance() {
		MobileWebConfiguration.load();
		return MobileWebConfiguration.config;
	}

	public synchronized void store(OutputStream os, String header) throws IOException {
		conf.store(os, header);

	}

	public synchronized void store() throws IOException {
		FileOutputStream f = new FileOutputStream(MobileWebConfiguration.filename);
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
