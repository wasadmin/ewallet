package zw.co.esolutions.ewallet.sms;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.smslib.InboundMessage;

import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.mcommerce.msg.Message;
import zw.co.esolutions.mcommerce.msg.MessageDocument;
import zw.co.esolutions.mcommerce.msg.MobileOriginatedShortMessage;
import zw.co.esolutions.mcommerce.msg.MobileOriginatedShortMessage.MobileNetworkOperator;
import zw.co.esolutions.mcommerce.msg.MobileOriginatedShortMessage.OriginatorId;

/**
 * Session Bean implementation class MessageProcessorImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MessageProcessorImpl implements MessageProcessor {

    /**
     * Default constructor. 
     */
    public MessageProcessorImpl() {
        // TODO Auto-generated constructor stub
    }
    
    public void processMessage(InboundMessage msg) {
        MessageDocument mdoc = MessageDocument.Factory.newInstance();
        Message message = mdoc.addNewMessage();
        MobileOriginatedShortMessage mosn = message.addNewMobileOriginatedShortMessage();
        
        //Setting fields
        mosn.setDateReceived(Calendar.getInstance());
        mosn.setMobileNetworkOperator(MobileNetworkOperator.ECONET);
        mosn.setOriginatorId(OriginatorId.ECONET_SMPP);
        mosn.setShortMessage(msg.getText());
        mosn.setSubscriberId(msg.getOriginator());
                
    	this.pushMessage(mdoc.toString());
    }
    
    public void pushMessage(String message) {
    	try {
			MessageSender.sendSMPPRequest(EWalletConstants.ECONET_SMS_IN_QUEUE, message, EWalletConstants.SMPP_QCF);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
