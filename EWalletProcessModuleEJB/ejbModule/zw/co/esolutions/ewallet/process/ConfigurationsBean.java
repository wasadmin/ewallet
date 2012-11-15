package zw.co.esolutions.ewallet.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import zw.co.esolutions.ewallet.process.model.Configuration;
import zw.co.esolutions.ewallet.process.timers.MonthlyProcessorLocal;
import zw.co.esolutions.ewallet.util.SystemConstants;



/**
 * Session Bean implementation class ConfigurationsBean
 */
@Stateless
public class ConfigurationsBean implements ConfigurationsLocal {
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private MonthlyProcessorLocal monthlyProcessor;
	
	
	
    public ConfigurationsBean() {
        // TODO Auto-generated constructor stub
    }
    
    @SuppressWarnings("unchecked")
	public List<Configuration> getAllConfigurations(){
    		List<Configuration> results = null;
			Query query = em.createNamedQuery("getConfiguration");
			results = (List<Configuration>) query.getResultList();
			if(results== null || results.size()==0){
				results = initialiaseConfiguration();
			}
		
		return results;
    }

	private List<Configuration> initialiaseConfiguration() {
		Map<String,String> configMap = getMapOfConfigConstants();
		List<Configuration> configs = new ArrayList<Configuration>();
	    for(String key : configMap.keySet()){
	    	Configuration config = new Configuration(key,configMap.get(key));
	    	configs.add(config);
	    	em.persist(config);
	    }
		return configs;
	}

	private Map<String, String> getMapOfConfigConstants() {
		Map<String,String> map = new HashMap<String, String>();
		map.put(SystemConstants.MONTHLY_PROCESSING_DAY, null);
		map.put(SystemConstants.MONTHLY_PROCESSING_HOUR, null);
		map.put(SystemConstants.MONTHLY_PROCESSING_MINUTE, null);
		map.put(SystemConstants.MONTHLY_PROCESSING_LAST_CHARGING_DATE, null);
		map.put(SystemConstants.DISPATCH_METHOD, null);
		return map;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Configuration getConfiguration(String key){
		return em.find(Configuration.class, key);
	}
	
	public void updateConfiguration(Configuration config){
		if(config != null){
			String tmpValue = config.getValue();
			config = em.find(Configuration.class, config.getKey());
			if(config != null){
				config.setValue(tmpValue);
				em.persist(config);
			}
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void setConfiguration(String key,String value){
		
		Configuration config = em.find(Configuration.class,key);
		if(config == null){
			config = new Configuration(key,value);
			em.persist(config);
		}else{
			config.setValue(value);
			em.merge(config);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Map<String, String> getConfigMap() {
		List<Configuration> configs = getAllConfigurations();
		Map<String,String> configMap = new HashMap<String, String>();
		if(configs != null){
			for(Configuration config : configs){
				configMap.put(config.getKey(), config.getValue());
			}
		}
		return configMap;
	}

	public String getStringValue(String key) {
		Configuration config = getConfiguration(key);
		if(config != null){
			return config.getValue();
		}
		return null;
	}

	
	public void updateMonthlyConfigurations(String dayOfTheMonth,String monthlyHour, String monthlyMinute) {
		try {
			monthlyProcessor.scheduleTimer(dayOfTheMonth, monthlyHour, monthlyMinute, true, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setConfiguration(SystemConstants.MONTHLY_PROCESSING_DAY, dayOfTheMonth);
		setConfiguration(SystemConstants.MONTHLY_PROCESSING_HOUR, monthlyHour);
		setConfiguration(SystemConstants.MONTHLY_PROCESSING_MINUTE, monthlyMinute);
	}

	
	public Date getNextMonthlyProcessingTime() {
		return monthlyProcessor.getCurrentDispatchTime();
	}

	
	public void cancelMonthlyTimer() {
		try {
			monthlyProcessor.cancelTimer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public void cancelAllTimers(){
		try {
			monthlyProcessor.cancelTimer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<Configuration> initialiaseOrUpdateConfigurations(Map<String,String> configMap) {
		List<Configuration> configs = new ArrayList<Configuration>();
		Configuration conf = null;
	    for(String key : configMap.keySet()){
	    	Configuration config = new Configuration(key,configMap.get(key));
	    	conf = em.find(Configuration.class, key);
	    	if(conf == null || conf.getKey() == null) {
	    		em.persist(config);
	    	} else {
	    		conf.setValue(configMap.get(key));
	    		config = em.merge(conf);
	    	}
	    	configs.add(config);
	    }
		return configs;
	}

}
