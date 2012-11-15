package zw.co.esolutions.ussd.ejb;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.services.USSDService;
import zw.co.esolutions.ussd.services.USSDServiceFactory;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;
import zw.co.esolutions.ussd.util.XmlUtils;

/**
 * Session Bean implementation class USSDSessionProcessorImpl
 */
@Stateless
public class USSDSessionProcessorImpl implements USSDSessionProcessor {
	
	@PersistenceContext
	private EntityManager em;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	

   
    public USSDSessionProcessorImpl() {
        
    }

	@Override
	public String processUSSDRequest(String xml) {
		logger.info("Receiving Request  " + xml );
		Map<String, String> attributesMap = parseXml(xml);
		for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
            logger.debug(entry.getKey() + " = " + entry.getValue());
        }
		USSDSession ussdSession = em.find(USSDSession.class, attributesMap.get(SystemConstants.TRANSACTION_ID));
		logger.debug(">>>>>>>>>>>>>>>> Session "+ussdSession);
		if(ussdSession == null){
			ussdSession = new USSDSession(attributesMap);
		}
		
		USSDService ussdService = USSDServiceFactory.getUSSDServiceInstance(ussdSession.getServiceName());
		logger.debug(">>>>>>>> Service Name "+ussdSession.getServiceName());
		ussdSession = ussdService.processUSSDRequest(ussdSession,attributesMap);
		ussdSession.generateXml();
		String responseXml = ussdSession.getResponseXml();
		
        logger.debug("---- session status " + ussdSession.getStatus());
		if(SystemConstants.STATUS_NEW.equals(ussdSession.getStatus())){
			if(!SystemConstants.ACTION_END.equals(ussdSession.getAction())){
				ussdSession.setStatus(SystemConstants.STATUS_ACTIVE);
				em.persist(ussdSession);
			}
		}else if(SystemConstants.STATUS_EDIT.equals(ussdSession.getStatus())){
			em.merge(ussdSession);
		}else if(SystemConstants.STATUS_DELETE.equals(ussdSession.getStatus())){
			em.remove(ussdSession);
		}
		//logger.debug("---- response xml " + responseXml);
		return responseXml;
	}
	
	private Map<String, String> parseXml(String xml){
		Map<String, String> attributesMap = new HashMap<String, String>();
		try{
			/*MethodCallDocument methodCallDocument = MethodCallDocument.Factory.parse(xml);
			MethodCall mc = methodCallDocument.getMethodCall();
			Member[] members = mc.getParams().getParam().getValue().getStruct().getMemberArray();
			for (Member member : members) {
				if(SystemConstants.TRANSACTION_TIME.equals(member.getName())){
					attributesMap.put(member.getName(), member.getValue().getDateTimeIso8601());
				}else{
					attributesMap.put(member.getName(), member.getValue().getString());
				}
			}*/
			
			attributesMap = XmlUtils.parseXml(xml);
		}catch(Exception e){
			e.printStackTrace();
		}
		return attributesMap;
	}

}
