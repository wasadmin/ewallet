/**
 * 
 */
package zw.co.esolutions.topup.ws.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author blessing
 *
 */
public class TopupWebServiceConfig {
	
	private static TopupWebServiceConfig instance;
	private Properties properties = null;
	
	
	public static TopupWebServiceConfig getInstance() {
		if (instance == null) {
			instance = new TopupWebServiceConfig();
			
		}
		return instance;
	}
	/**
	 * 
	 */
	private TopupWebServiceConfig() {
		String fileName = "";
		// use lazy initialization to just load the file once.
		if (properties == null) {
			try {
			
				// get config filename
				String fileSep = System.getProperties().getProperty("file.separator");
				String root = fileSep;
				if (fileSep.equals("\\")) {
					fileSep = "\\\\";
					root = "c:\\";
				}
				// load properties
				fileName = root + "opt" + fileSep + "eSolutions" + fileSep + "conf" + fileSep + "TopupWebService.conf";
				
				properties = new Properties();
				properties.load(new FileInputStream(fileName));
			}
			catch (FileNotFoundException ex) {
				System.out.println("Web Service Config caught an exception.  Application config file '" + fileName + "' not found.  The Message is " + ex.getMessage());
			}
			catch (IOException ex) {
				System.out.println("Web Service Config caught an exception.  The Message is " + ex.getMessage());
			}
		}
	}
	
	
	
	public String getStringValueOf(String attribute) {
		return properties.getProperty(attribute);
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
	 * Gets a property and converts it to double
	 */
	public double getDoubleValueOf(String attribute) {
		return Double.parseDouble(this.getStringValueOf(attribute));
	}
	/**
	 * Gets a property and converts it to double
	 */
	public double getDoubleValueOf(String attribute,double defaultValue) {
		return Double.parseDouble(this.getStringValueOf(attribute,String.valueOf(defaultValue)));
	}


	/**
	 * Gets a property and converts it into a byte.
	 */
	public byte getByteValueOf(String attribute, byte defaultValue) {

		return Byte.parseByte(this.getStringValueOf(attribute, Byte.toString(defaultValue)));
	}

	public String getStringValueOf(String attribute, String defaultValue) {
		String val = properties.getProperty(attribute, defaultValue);
		if(val !=null)
			val.trim();
		return val;
	}
	
	public List<String> getValueList(String attribute){
		List<String> col = new ArrayList<String>();
		int i=0;
		String value = getStringValueOf(attribute+"."+i,null);
		while( value != null){
			col.add(value);
			value = getStringValueOf(attribute+"."+ ++i,null);
		}
		return col;
	}

	/**
	 * @param fileName
	 *            The name of the file that contains the configurations
	 */
	public synchronized void setConf(String fileName) {
		try {
			properties.load(new FileInputStream(fileName));
		} catch (FileNotFoundException f) {
			f.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	
}
