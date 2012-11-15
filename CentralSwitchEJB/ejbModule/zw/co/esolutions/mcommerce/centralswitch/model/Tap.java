package zw.co.esolutions.mcommerce.centralswitch.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Tap {
	
	@Id 
	@Column(length = 30)
	private String id;
	
	@Column(length = 6)
	private String status;
	
	@Version private long version;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public long getVersion() {
		return version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
