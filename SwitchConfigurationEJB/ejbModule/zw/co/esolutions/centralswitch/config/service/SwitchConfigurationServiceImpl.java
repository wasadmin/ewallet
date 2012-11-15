package zw.co.esolutions.centralswitch.config.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import zw.co.esolutions.centralswitch.config.model.ConfigInfo;
import zw.co.esolutions.centralswitch.config.util.ConfigConstants;

/**
 * Session Bean implementation class SwitchConfigurationServiceImpl
 */
@Stateless
@WebService(endpointInterface="zw.co.esolutions.centralswitch.config.service.SwitchConfigurationService", serviceName="SwitchConfigurationService", portName="SwitchConfigurationServiceSOAP")
public class SwitchConfigurationServiceImpl implements SwitchConfigurationService {
	
	@PersistenceContext
	private EntityManager em;
	
	public SwitchConfigurationServiceImpl() {

	}

	public List<ConfigInfo> getConfigInfoByUSSDCode(String ussdCode) throws Exception{
		try {
			Query query = em.createNamedQuery("getConfigInfoByUSSDCode");
			query.setParameter("ussdCode", ussdCode);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public  List<ConfigInfo> getConfigInfoBySMSCode(String smsCode) throws Exception{
		try {
			Query query = em.createNamedQuery("getConfigInfoBySMSCode");
			query.setParameter("smsCode", smsCode);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ConfigInfo createConfigInfo(ConfigInfo configInfo) throws Exception{
		em.persist(configInfo);
		return configInfo;
	}
	
	public ConfigInfo editConfigInfo(ConfigInfo configInfo) throws Exception{
		return em.merge(configInfo);
	}
	
	public ConfigInfo findConfigInfoByOwnerId(String ownerId) throws Exception{
		Query query = em.createNamedQuery("findConfigInfoByOwnerId");
		query.setParameter("ownerId", ownerId);
		return (ConfigInfo)query.getSingleResult();
	}
	
	public ConfigInfo approveConfigInfo(ConfigInfo configInfo) throws Exception{
		if(ConfigConstants.STATUS_AWAITING_APPROVAL.equalsIgnoreCase(configInfo.getStatus())){
			configInfo.setStatus(ConfigConstants.STATUS_ACTIVE);
		}else{
			//ignore this
		}
		return em.merge(configInfo);
	}
	
	public ConfigInfo deleteConfigInfo(ConfigInfo configInfo) throws Exception{
		em.remove(configInfo);
		return configInfo;
	}
}
