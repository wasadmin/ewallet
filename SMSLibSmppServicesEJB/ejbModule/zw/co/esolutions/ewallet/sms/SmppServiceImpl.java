package zw.co.esolutions.ewallet.sms;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.smslib.AGateway;
import org.smslib.ICallNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.AGateway.Protocols;
import org.smslib.Message.MessageTypes;
import org.smslib.modem.SerialModemGateway;
/**
 * Session Bean implementation class SmppServiceImpl
 */
@Stateless
public class SmppServiceImpl implements SmppService {
	
	@EJB MessageProcessor pms;
	
	private Service srv;
    private InboundNotification inboundNotification;
    private OutboundNotification outboundNotification;
    private CallNotification callNotification;
    private InboundPollingThread inboundPollingThread;
    private SerialModemGateway gateway;
    private static boolean START;

    public SmppServiceImpl() {
        this.srv = new Service();
	this.inboundNotification = new InboundNotification();
	this.outboundNotification = new OutboundNotification();
	this.callNotification = new CallNotification();
	this.inboundPollingThread = null;
	this.srv.setInboundMessageNotification(this.inboundNotification);
	this.srv.setOutboundMessageNotification(this.outboundNotification);
	this.srv.setCallNotification(this.callNotification);
    }

    public static void setSTART(boolean START) {
    	SmppServiceImpl.START = START;
    }

    public void stopServer(){
        try {
            setSTART(false);
            srv.removeGateway(gateway);
            srv.stopService();

        } catch (Exception ex) {
            Logger.getLogger(SmppServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public boolean loadConfiguration()throws Exception{
         try {
            gateway = new SerialModemGateway("modem.tauttee", "/dev/ttyACM0", 57600, "Edge", "");
            gateway.setProtocol(Protocols.PDU);
            gateway.setInbound(true);
            gateway.setOutbound(true);
            //gateway.setSmscNumber("+26391010030");
            //gateway.setSmscNumber("+23675500000");
           // gateway.setSmscNumber("+263730100007");
            
            this.srv.addGateway(gateway);
            getService().startService();
            System.out.println(">>>>>>>>>>>>>>> Sms Num = " +gateway.getSmscNumber());
            // Printout some general information about the modem.
            System.out.println();
            System.out.println("Modem Information:");
            System.out.println("  Manufacturer: " + gateway.getManufacturer());
            System.out.println("  Model: " + gateway.getModel());
            System.out.println("  Signal Level: " + gateway.getSignalLevel() + "%");
            System.out.println();
            System.out.println("### Gate way created successfully.");
            inboundPollingThread = new InboundPollingThread();
            System.out.println("############ CreatedPolling Thread.");
            inboundPollingThread.start();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(SmppServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }


    public InboundPollingThread getInboundPollingThread() {
        return inboundPollingThread;
    }

    public Service getService() {
        return srv;
    }

     public void readMessages() throws Exception{
        System.out.println("################ Now entering Read Messages.");
    	 //list to hold read msgs
        List<InboundMessage> msgList;
            try{
                 msgList = new ArrayList<InboundMessage>();
                 //msgList.add(new InboundMessage(new Date(),"+263913407374","START", 0, "SM"));
                     try{
                        this.getService().readMessages(msgList, InboundMessage.MessageClasses.ALL);
                        System.out.println("############# List of messages : "+msgList.size());
                            if(!msgList.isEmpty()){
                            	for(InboundMessage msg1 : msgList) {
                            		System.out.println(">>>>>>>>>>>>>Message: "+msg1.getText());
                            	}
                            	if(msgList.get(0).getType()==MessageTypes.INBOUND){
                                    for (InboundMessage msg : msgList) {
                                        pms.processMessage(msg);
                                        System.out.println("###########"+msg.getText());
                                        this.getService().deleteMessage(msg);
                                    }
                                    
                                   // for (InboundMessage msg : msgList)
                                        //this.getService().deleteMessage(msg);
                                }
                            }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
            }catch (Exception e){
                   System.out.println("Is there any exception occurring!!!!");
                   e.printStackTrace();
            }finally{

            }
    }

    public void sendMessage(String phoneNumber, String responseMsg)
              throws Exception{
       synchronized (SmppServiceImpl.class) {
	    	try {
	            OutboundMessage msg;
	            // Send a message synchronously.
	            msg = new OutboundMessage(phoneNumber, responseMsg);
	            
	            //;
	            this.srv.queueMessage(msg);
//	            this.srv.sendMessage(msg);
	            System.out.println(">>>>>>>>>>>>>>>>>>. Sending To Phone "+msg.getText());
	           
	        } catch (Exception ex) {
	            Logger.getLogger(SmppServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
	        }
       }
    }

    public class CallNotification implements ICallNotification
	{
		public void process(AGateway gateway, String callerId)
		{
			System.out.println(">>> New call detected from Gateway: " + gateway.getGatewayId() + " : " + callerId);
		}
	}

    public class InboundNotification implements IInboundMessageNotification
	{
		public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg)
		{
			if (msgType == MessageTypes.INBOUND) System.out.println(">>> New Inbound message detected from Gateway: " + gateway.getGatewayId());
			else if (msgType == MessageTypes.STATUSREPORT) System.out.println(">>> New Inbound Status Report message detected from Gateway: " + gateway.getGatewayId());
			System.out.println(msg);
		}
	}
    
    public class OutboundNotification implements IOutboundMessageNotification
	{
		public void process(AGateway gateway, OutboundMessage msg)
		{
			System.out.println("Outbound handler called from Gateway: " + gateway.getGatewayId());
			System.out.println(msg);
		}
	}

    protected class InboundPollingThread extends Thread{
	@Override
	public void run(){
            setSTART(true);
            while(START){
                try{
                    getService().getLogger().logDebug("InboundPollingThread() run.", null, null);
                    readMessages();
                    Thread.sleep(10*1000);
                }catch (InterruptedException e){
                    getService().getLogger().logDebug("InboundPollingThread() interrupted.", null, null);
                }catch (Exception e){
                    getService().getLogger().logDebug("Error in InboundPollingThread()", e, null);
                }
            }
	}
    }

	@Override
	public void process(AGateway gateway, OutboundMessage outbound) {
//		srv.setQueueSendingNotification(this);
		System.out.println("CALL BACK Excecuted on " + gateway + " for message " + outbound);
	}

}