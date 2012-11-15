package zw.co.esolutions.ewallet.sms;

import java.util.Properties;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.smslib.AGateway;
import org.smslib.AGateway.GatewayStatuses;
import org.smslib.IGatewayStatusNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.smpp.BindAttributes;
import org.smslib.smpp.jsmpp.JSMPPGateway;

import zw.co.esolutions.mcommerce.gatewayservices.util.SystemConstants;

/**
 * Session Bean implementation class TransceiverImp
 */
@Stateless
public class TransceiverImp implements Transceiver {

	private static Properties config = SystemConstants.configParams;
	
	private static Logger log;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/centalswitch.log.properties");
			log = Logger.getLogger(Transceiver.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + Transceiver.class);
		}
	}
	
	/*public void initialize() throws Exception{
		log.debug("Initializing Gateway ..");
		String gatewayId = "eSolutions"; 
//			config.getProperty("SYSTEM_GATEWAYID");
		String smppHost = config.getProperty("SYSTEM_SMPP_HOST");
		int smppPort = Integer.parseInt(config.getProperty("SYSTEM_SMPP_PORT"));
		String smppUser = config.getProperty("SYSTEM_SMPP_USER");
		String smppPassword = config.getProperty("SYSTEM_SMPP_PASSWORD");
		log.debug("Binding to "+smppHost+" on port "+smppPort+" user:"+smppUser+" password:"+smppPassword);
		JSMPPGateway gateway = new JSMPPGateway(gatewayId, smppHost, smppPort, new BindAttributes(smppUser, smppPassword, "cp", org.smslib.smpp.BindAttributes.BindType.TRANSCEIVER));
		//JSMPPGateway gateway = new JSMPPGateway("smppcon", "smsc6.routotelecom.com", 7777, new BindAttributes("1168924", "tqmnwx9m", "cp", BindType.TRANSCEIVER));
		Service.getInstance().addGateway(gateway);
		Service.getInstance().setInboundMessageNotification(new InboundNotification());
		Service.getInstance().setGatewayStatusNotification(new GatewayStatusNotification());
		Service.getInstance().setOutboundMessageNotification(new OutboundNotification());
		Service.getInstance().startService();	
		log.debug("Gateway Initialized");
	}
	
	public void cleanup(){
		try {
			
			Service.getInstance().stopService();
			log.debug("Gateway stopped.");
		} catch (Exception e) {
			log.debug("Failed to stop gateway", e);
		}
	}

	public String sendMessage(String gatewayId,String originator,String destination,String messageText) throws Exception{
		
		OutboundMessage msg = new OutboundMessage();
		msg.setGatewayId(gatewayId);
		msg.setFrom(originator);
		msg.setRecipient(destination);
		msg.setText(messageText);
		// Request Delivery Report
		msg.setStatusReport(true);
		
		Service.getInstance().sendMessage(msg);
		//log.debug(msg);
		log.debug(">>>>>"+msg.getMessageId());
		return msg.getMessageId()+"";
	}
	
//	public static void main(String args[])
//	{
//		
//		try
//		{
//			Transceiver app = new TransceiverImp();
//			app.sendMessage("smppcon", "ZB eWallet", "263773260934", "This just another test");
//			
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}

	public class OutboundNotification implements IOutboundMessageNotification
	{
		public void process(AGateway gateway, OutboundMessage msg)
		{
			log.debug("Outbound handler called from Gateway: " + gateway.getGatewayId());
			log.debug(msg);
		}
	}

	public class InboundNotification implements IInboundMessageNotification
	{
		public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg)
		{
			if (msgType == MessageTypes.INBOUND){
				log.debug(">>> New Inbound message detected from Gateway: " + gateway.getGatewayId());
				log.debug(msg);
			}else if (msgType == MessageTypes.STATUSREPORT){
				log.debug(System.currentTimeMillis()+"\t Message "+msg.getOriginator());
				log.debug(msg);
			}
			
		}
	}

	public class GatewayStatusNotification implements IGatewayStatusNotification
	{
		public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus)
		{
			log.debug(">>> Gateway Status change for " + gateway.getGatewayId() + ", OLD: " + oldStatus + " -> NEW: " + newStatus);
		}
	}*/
}
