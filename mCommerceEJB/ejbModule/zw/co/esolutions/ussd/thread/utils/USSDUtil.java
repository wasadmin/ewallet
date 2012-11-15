/**
 * 
 */
package zw.co.esolutions.ussd.thread.utils;

import org.apache.log4j.Logger;

import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.services.USSDService;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.web.services.FlowStatus;
import zw.co.esolutions.ussd.web.services.UssdTransaction;
import zw.co.esolutions.ussd.web.services.proxy.UssdServiceProxy;

/**
 * @author taurai
 *
 */
public class USSDUtil {
	static Logger logger = LoggerFactory.getLogger(USSDUtil.class);
	
	public USSDUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UssdTransaction waitForBankResponse(USSDSession session) {
		UssdTransaction txn = null;
		logger.debug("#########      IN waitForBankResponse method");
		try {
			USSDConfiguration conf = USSDService.getConfigUSSDInstance();
			

			long TRANSACTION_TIMEOUT = Long.parseLong(conf.getStringValueOf("TRANSACTION_TIMEOUT"));
			long RESPONSE_CHECK_INTERVAL = Long.parseLong(conf.getStringValueOf("RESPONSE_CHECK_INTERVAL"));
			
			long count = RESPONSE_CHECK_INTERVAL;

			boolean responseArrived = false;

			while (count <= TRANSACTION_TIMEOUT) {

				Thread.sleep(RESPONSE_CHECK_INTERVAL); // wait for configured time
														// interval

				logger.debug("#########      WAITED : " + (count / 1000) + " sec");

				count += RESPONSE_CHECK_INTERVAL;

			//	txn = UssdServiceProxy.getInstance().getTransactionUSSDSessionId(session.getSessionId());
				
				logger.debug("###########  USSD Transaction = "+txn);
				if(txn == null) {
					logger.debug("###########  USSD Transaction is NULL, continue waiting.");
					continue;
				}
				
				if (FlowStatus.COMPLETED.equals(txn.getFlowStatus())) {
					responseArrived = true;
					UssdServiceProxy.getInstance().deleteTransaction(txn.getUuid());
					break;
				} else {
					logger.debug("#######  RESPONSE NOT YET ARRIVED");

				}
			}
			
			logger.debug("#########      OUT of WAIT LOOP");
			
			if(responseArrived) {
				return txn;
			} else {
				if (txn != null && txn.getUuid() != null) {
					txn.setSendSms(true);
					UssdServiceProxy.getInstance().updateTransaction(txn);
					txn = null;
				}
			}
			
		} catch (Exception e) {
			
		}
		return txn;
	}
}
