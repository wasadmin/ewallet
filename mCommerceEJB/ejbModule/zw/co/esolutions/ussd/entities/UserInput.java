package zw.co.esolutions.ussd.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.util.GenerateKey;

@Entity
public class UserInput {

	@Id
	private String id;
	private String inputKey;
	private String inputValue;
	@Version
	private long version;
	
	
	
	public UserInput() {
		super();
		this.setId(GenerateKey.generateEntityId());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInputKey() {
		return inputKey;
	}
	public void setInputKey(String inputKey) {
		this.inputKey = inputKey;
	}
	public String getInputValue() {
		return inputValue;
	}
	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	
	
	
}
