package zw.co.esolutions.ewallet.tellerweb.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: TellerCode
 *
 */
@Entity

public class TellerCode implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id @Column(length = 30) private String id;
	@Column(length=10) private String code;
	@Column(length=30) private String profileId;
	@Column(length=30) private String supervisorId;
	@Column private Date dateCreated;
	@Version private long version;

	public TellerCode() {
		super();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(String supervisorId) {
		this.supervisorId = supervisorId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
   
}
