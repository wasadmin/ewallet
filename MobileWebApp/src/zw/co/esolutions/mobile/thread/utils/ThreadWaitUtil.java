/**
 * 
 */
package zw.co.esolutions.mobile.thread.utils;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.services.proxy.MobileWebServiceProxy;
import zw.co.esolutions.mobile.web.conf.MobileWebConfiguration;
import zw.co.esolutions.mobile.web.utils.WebStatus;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.web.services.WebSession;

/**
 * @author taurai
 *
 */
public class ThreadWaitUtil {
	static Logger logger = LoggerFactory.getLogger(ThreadWaitUtil.class);
	
	public ThreadWaitUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WebSession waitForBankResponse(String sessionId) {
		WebSession txn = null;
		logger.debug("#########      IN waitForBankResponse method");
		try {
			MobileWebConfiguration conf = MobileWebConfiguration.getInstance();
			

			long TRANSACTION_TIMEOUT = Long.parseLong(conf.getStringValueOf("TRANSACTION_TIMEOUT"));
			long RESPONSE_CHECK_INTERVAL = Long.parseLong(conf.getStringValueOf("RESPONSE_CHECK_INTERVAL"));
			
			long count = RESPONSE_CHECK_INTERVAL;

			boolean responseArrived = false;

			while (count <= TRANSACTION_TIMEOUT) {

				Thread.sleep(RESPONSE_CHECK_INTERVAL); // wait for configured time
														// interval

				logger.debug("#########      WAITED : " + (count / 1000) + " sec");

				count += RESPONSE_CHECK_INTERVAL;

				txn = MobileWebServiceProxy.getInstance().findWebSessionById(sessionId);
				
				logger.debug("###########  WebSession = "+txn);
				if(txn == null) {
					logger.debug("###########  WebSession is NULL, continue waiting.");
					continue;
				}
				
				if (WebStatus.COMLETED.toString().equals(txn.getStatus())) {
					responseArrived = true;
					MobileWebServiceProxy.getInstance().deleteWebSession(sessionId);
					break;
				} else {
					logger.debug("#######  RESPONSE NOT YET ARRIVED");

				}
			}
			
			logger.debug("#########      OUT of WAIT LOOP");
			
			if(responseArrived) {
				return txn;
			} else {
				if (txn != null && txn.getReferenceId() != null) {
					txn.setSendSms(true);
					MobileWebServiceProxy.getInstance().updateWebSession(txn);
					txn = null;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txn;
	}
}
