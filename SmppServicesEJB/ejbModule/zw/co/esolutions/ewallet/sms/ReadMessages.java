// ReadMessages.java - Sample application.
//
// This application shows you the basic procedure needed for reading
// SMS messages from your GSM modem, in synchronous mode.
//
// Operation description:
// The application setup the necessary objects and connects to the phone.
// As a first step, it reads all messages found in the phone.
// Then, it goes to sleep, allowing the asynchronous callback handlers to
// be called. Furthermore, for callback demonstration purposes, it responds
// to each received message with a "Got It!" reply.
//
// Tasks:
// 1) Setup Service object.
// 2) Setup one or more Gateway objects.
// 3) Attach Gateway objects to Service object.
// 4) Setup callback notifications.
// 5) Run

package zw.co.esolutions.ewallet.sms;


public class ReadMessages
{
//	Service srv;
//
//	public void doIt() throws Exception
//	{
//		// Define a list which will hold the read messages.
//		List<InboundMessage> msgList;
//
//		// Create the notification callback method for inbound & status report
//		// messages.
//		InboundNotification inboundNotification = new InboundNotification();
//
//		// Create the notification callback method for inbound voice calls.
//		CallNotification callNotification = new CallNotification();
//
//		//Create the notification callback method for gateway statuses.
//		GatewayStatusNotification statusNotification = new GatewayStatusNotification();
//
//		OrphanedMessageNotification orphanedMessageNotification = new OrphanedMessageNotification();
//
//		try
//		{
//			System.out.println("Example: Read messages from a serial gsm modem.");
//			System.out.println(Library.getLibraryDescription());
//			System.out.println("Version: " + Library.getLibraryVersion());
//
//			// Create new Service object - the parent of all and the main interface
//			// to you.
//			this.srv = new Service();
//
//			// Create the Gateway representing the serial GSM modem.
//			SerialModemGateway gateway = new SerialModemGateway("modem.com1", "/dev/ttyACM0", 57600, "Nokia", "");
//
//			// Set the modem protocol to PDU (alternative is TEXT). PDU is the default, anyway...
//			gateway.setProtocol(Protocols.PDU);
//
//			// Do we want the Gateway to be used for Inbound messages?
//			gateway.setInbound(true);
//
//			// Do we want the Gateway to be used for Outbound messages?
//			gateway.setOutbound(true);
//
//			// Let SMSLib know which is the SIM PIN.
//			gateway.setSimPin("0000");
//
//			// Set up the notification methods.
//			this.srv.setInboundMessageNotification(inboundNotification);
//			this.srv.setCallNotification(callNotification);
//			this.srv.setGatewayStatusNotification(statusNotification);
//			this.srv.setOrphanedMessageNotification(orphanedMessageNotification);
//
//			// Add the Gateway to the Service object.
//			this.srv.addGateway(gateway);
//
//			// Similarly, you may define as many Gateway objects, representing
//			// various GSM modems, add them in the Service object and control all of them.
//
//			// Start! (i.e. connect to all defined Gateways)
//			this.srv.startService();
//
//			// Printout some general information about the modem.
//			System.out.println();
//			System.out.println("Modem Information:");
//			System.out.println("  Manufacturer: " + gateway.getManufacturer());
//			System.out.println("  Model: " + gateway.getModel());
//			System.out.println("  Serial No: " + gateway.getSerialNo());
//			System.out.println("  SIM IMSI: " + gateway.getImsi());
//			System.out.println("  Signal Level: " + gateway.getSignalLevel() + "%");
//			System.out.println("  Battery Level: " + gateway.getBatteryLevel() + "%");
//			System.out.println();
//
//			// In case you work with encrypted messages, its a good time to declare your keys.
//			// Create a new AES Key with a known key value. 
//			// Register it in KeyManager in order to keep it active. SMSLib will then automatically
//			// encrypt / decrypt all messages send to / received from this number.
//			this.srv.getKeyManager().registerKey("+306948494037", new AESKey(new SecretKeySpec("0011223344556677".getBytes(), "AES")));
//
//			// Read Messages. The reading is done via the Service object and
//			// affects all Gateway objects defined. This can also be more directed to a specific
//			// Gateway - look the JavaDocs for information on the Service method calls.
//			msgList = new ArrayList<InboundMessage>();
//			this.srv.readMessages(msgList, MessageClasses.ALL);
//			for (InboundMessage msg : msgList)
//				System.out.println(msg);
//			
//			// Sleep now. Emulate real world situation and give a chance to the notifications
//			// methods to be called in the event of message or voice call reception.
//			
//			System.out.println("Now Sleeping - Hit <enter> to stop service.");
//			System.in.read(); System.in.read();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			this.srv.stopService();
//		}
//	}
//
//	public class InboundNotification implements IInboundMessageNotification
//	{
//		public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg)
//		{
//			if (msgType == MessageTypes.INBOUND) System.out.println(">>> New Inbound message detected from Gateway: " + gateway.getGatewayId());
//			else if (msgType == MessageTypes.STATUSREPORT) System.out.println(">>> New Inbound Status Report message detected from Gateway: " + gateway.getGatewayId());
//			System.out.println(msg);
//		}
//	}
//
//	public class CallNotification implements ICallNotification
//	{
//		public void process(AGateway gateway, String callerId)
//		{
//			System.out.println(">>> New call detected from Gateway: " + gateway.getGatewayId() + " : " + callerId);
//		}
//	}
//
//	public class GatewayStatusNotification implements IGatewayStatusNotification
//	{
//		public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus)
//		{
//			System.out.println(">>> Gateway Status change for " + gateway.getGatewayId() + ", OLD: " + oldStatus + " -> NEW: " + newStatus);
//		}
//	}
//
//	public class OrphanedMessageNotification implements IOrphanedMessageNotification
//	{
//		public boolean process(AGateway gateway, InboundMessage msg)
//		{
//			System.out.println(">>> Orphaned message part detected from " + gateway.getGatewayId());
//			System.out.println(msg);
//			// Since we are just testing, return FALSE and keep the orphaned message part.
//			return false;
//		}
//	}
//
//	public static void main(String args[])
//	{
//		ReadMessages app = new ReadMessages();
//		try
//		{
//			app.doIt();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
}
