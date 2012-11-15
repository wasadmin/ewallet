package zw.co.esolutions.ewallet.agentservice.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Date;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: AgentNumber
 *
 */
@Entity

public class AgentNumber implements Serializable {

	   
	@Id
	@Column(length = 30)
	private String id;
	@Column(length = 10)
	private String prefix;
	@Column(length = 10)
	private long number;
	private Date dateCreated;
	@Version
	private long version;
	private static final long serialVersionUID = 1L;

	public AgentNumber() {
		super();
	}   
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}   
	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}   
	public long getNumber() {
		return this.number;
	}

	public void setNumber(long number) {
		this.number = number;
	}   
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}   
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
	
	public String toString(){
		return getPrefix()+getNumber();
	}
}
