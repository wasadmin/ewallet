package zw.co.esolutions.ussd.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.util.SystemConstants;


public class LoggerFactory {
	
	public static boolean configLoaded;
	
	public static Logger getLogger(Class klass){
		loadConfigurations();
		Logger logger = Logger.getLogger(klass);
		return logger;
	}

	private static void loadConfigurations() {
		if(configLoaded == false){
			PropertyConfigurator.configure(SystemConstants.LOG_PROPERTIES_FILE);
			configLoaded = true;
		}
	}
	
	
	

}
