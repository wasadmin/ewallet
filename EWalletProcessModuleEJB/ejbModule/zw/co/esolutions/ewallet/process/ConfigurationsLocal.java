package zw.co.esolutions.ewallet.process;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import zw.co.esolutions.ewallet.process.model.Configuration;

@Local
public interface ConfigurationsLocal {
	
	public List<Configuration> getAllConfigurations();
	
	public Map<String,String> getConfigMap();
	
	public Configuration getConfiguration(String key);
	
	public void updateConfiguration(Configuration config);
	
	public void setConfiguration(String key,String value);
	
	public String getStringValue(String key);

	public void updateMonthlyConfigurations(String dayOfTheMonth,String monthlyHour, String monthlyMinute);

	public Date getNextMonthlyProcessingTime();
	
	public void cancelMonthlyTimer();
	
	public void cancelAllTimers();

	List<Configuration> initialiaseOrUpdateConfigurations(
			Map<String, String> configMap);

}
