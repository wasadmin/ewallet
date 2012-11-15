package zw.co.esolutions.ewallet.process.model;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Configuration
 *
 */
@Entity

@NamedQueries({@NamedQuery(name="getConfiguration", query = "SELECT c FROM Configuration c"),@NamedQuery(name="getConfigurationOrdered", query = "SELECT c FROM Configuration c ORDER BY c.key"),
@NamedQuery(name="getConfigurationByValue", query = "SELECT c FROM Configuration c WHERE c.value = :value"),
@NamedQuery(name="getConfigurationByVersion", query = "SELECT c FROM Configuration c WHERE c.version = :version")})
public class Configuration implements Serializable {

	   
	@Id
	private String key;
	private String value;
	@Version
	private long version;
	
	private static final long serialVersionUID = 1L;

	public Configuration() {
		super();
	}   
	public Configuration(String key, String value) {
		setKey(key);
		setValue(value);
	}
	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}   
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	
	
	
   
}
