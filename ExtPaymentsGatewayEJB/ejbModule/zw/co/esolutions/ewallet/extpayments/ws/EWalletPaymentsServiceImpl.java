package zw.co.esolutions.ewallet.extpayments.ws;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.extpayments.util.Constants;
import zw.co.esolutions.ewallet.extpayments.util.MessageSender;

/**
 * Session Bean implementation class EWalletPaymentsServiceImpl
 */
@Stateless
@WebService(endpointInterface="zw.co.esolutions.ewallet.extpayments.ws.EWalletPaymentsService", portName="EWalletPaymentsServiceSOAP", serviceName="EWalletPaymentsService")
public class EWalletPaymentsServiceImpl implements EWalletPaymentsService {

	
	@Resource(mappedName = Constants.SWITCH_QCF)
	private QueueConnectionFactory jmsQueueConnectionFactory;

	@Resource(mappedName = Constants.EXTPAYMENTS_REQUEST_Q)
	private Queue paymentsRequestQueue;

	private Connection jmsConnection;
	
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(EWalletPaymentsServiceImpl.class);
		} catch (Exception e) {
		}
	}
    
	@PostConstruct
	public void initialise() {
		try {
			jmsConnection = jmsQueueConnectionFactory.createConnection();
		} catch (JMSException e) {
			LOG.fatal("Failed to initialise Session Bean: " + this.getClass().getSimpleName(), e);
			e.printStackTrace(System.err);
		}
	}

	@PreDestroy
	public void cleanUp() {
		if (jmsConnection != null) {
			try {
				jmsConnection.close();
			} catch (JMSException e) {
				// Ignore this guy
			}
		}
	}

    @Override
    public String submitRequest(String xml)	{
    	
    	LOG.debug("@EXTPAY WebService: RECEIVED >> " + xml);
    	
    	String replyMsg = Constants.RC_ERROR + Constants.MESSAGE_SEPARATOR + Constants.GENERAL_ERROR_MESSAGE;
    	
    	try {
    		
    		LOG.debug("Forwading request to Q");
    		
    		boolean sent = MessageSender.sendTextToQueueDestination(jmsConnection, xml, paymentsRequestQueue, LOG);
    		
    		if (sent) {
    			replyMsg = Constants.RC_OK_SHORT + Constants.MESSAGE_SEPARATOR + Constants.SUCCESSFUL_SUBMIT_MSG;
    		} 
    		
    	} catch (Exception e) {
			LOG.fatal("Exception occurred" + e.getClass());
			e.printStackTrace(System.err);
		}
    	
    	LOG.debug("@EXTPAY Reply Msg: " + replyMsg);
    	
    	return replyMsg;
    	
    }
    

}
