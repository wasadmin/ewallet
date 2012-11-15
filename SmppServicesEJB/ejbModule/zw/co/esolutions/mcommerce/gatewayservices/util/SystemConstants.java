package zw.co.esolutions.mcommerce.gatewayservices.util;

import java.io.FileInputStream;
import java.util.Properties;

public class SystemConstants {

	public static String SYSTEM_CONFIG_FOLDER;
	public static String SYSTEM_CONFIG_FILE;
	public static Properties configParams;
	
	static{
		String fileSep = System.getProperties().getProperty("file.separator");
        String root = fileSep;
        if (fileSep.equals("\\")) {
            fileSep = "\\\\";
            root = "c:\\";
        }
        SYSTEM_CONFIG_FOLDER = root + "opt" + fileSep + "eSolutions" + fileSep + "conf" + fileSep;
        SYSTEM_CONFIG_FILE = SystemConstants.SYSTEM_CONFIG_FOLDER + "gateway.conf";
       
        // read the system configurations
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(SystemConstants.SYSTEM_CONFIG_FILE));
            configParams = config;
            // System.out.println("config:: " + config);
        }
        catch (Exception ex) {
            System.out.println("Error: Could not read the system configuration file '" + SystemConstants.SYSTEM_CONFIG_FILE + "'. Make sure it exists.");
            System.out.println("Specific error: " + ex);
        }
	}
}
