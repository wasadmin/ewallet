package zw.co.esolutions.mcommerce.gatewayservices.smpp;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;

import zw.co.esolutions.mcommerce.gatewayservices.util.SystemConstants;

/**
 * Session Bean implementation class SMSSenderImp
 */
@Stateless
public class SMSSenderImp implements SMSSender {

	private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
	private static Properties config = SystemConstants.configParams;
	
	private static Logger log;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/gateway.log.properties");
			log = Logger.getLogger(SMSSenderImp.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + SMSSenderImp.class);
		}
	}
    /**
     * Default constructor. 
     */
    public SMSSenderImp() {
        // TODO Auto-generated constructor stub
    }
    
    public void sendMessage(String originator,String destination,String messageText) throws Exception{
    	SMPPSession session = new SMPPSession();
    	
    	try{
    		messageText = messageText.replaceAll("\\[nl\\]", "   ");
    	}catch(Exception e){
    		
    	}
    	String smppHost = config.getProperty("SYSTEM_SMPP_HOST");
		int smppPort = Integer.parseInt(config.getProperty("SYSTEM_SMPP_PORT"));
		String smppUser = config.getProperty("SYSTEM_SMPP_USER");
		String smppPassword = config.getProperty("SYSTEM_SMPP_PASSWORD");
		
		
        try {
        	log.debug("Binding to "+smppHost+" on port "+smppPort+" user:"+smppUser+" password:"+smppPassword);
            session.connectAndBind(smppHost, smppPort, new BindParameter(BindType.BIND_TX, smppUser, smppPassword, "cp", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
        } catch (IOException e) {
            log.debug("Failed connect and bind to host",e);
            e.printStackTrace();
        }
        
        try {
            String messageId = session.submitShortMessage("CMT", TypeOfNumber.ALPHANUMERIC, NumberingPlanIndicator.LAND_MOBILE, originator, TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, destination, new ESMClass(), (byte)0, (byte)1,  timeFormatter.format(new Date()), null, new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte)0, new GeneralDataCoding(false, true, MessageClass.CLASS1, Alphabet.ALPHA_DEFAULT), (byte)0, messageText.getBytes());
            log.debug("Message submitted, message_id is " + messageId);
        } catch (PDUException e) {
            // Invalid PDU parameter
        	log.debug("Invalid PDU parameter");
            
        } catch (ResponseTimeoutException e) {
            // Response timeout
        	log.debug("Response timeout",e);
            
        } catch (InvalidResponseException e) {
            // Invalid response
        	log.debug("Receive invalid respose",e);
            
        } catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
        	log.debug("Receive negative response",e);
            
        } catch (IOException e) {
        	log.debug("IO error occur",e);
            
        }
        
        session.unbindAndClose();
    }

}
