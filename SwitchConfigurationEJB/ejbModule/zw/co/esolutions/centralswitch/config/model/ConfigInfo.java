package zw.co.esolutions.centralswitch.config.model;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: ConfigInfo
 * 
 */
@Entity
@NamedQueries( { 
	@NamedQuery(name = "getConfigInfoByUSSDCode", query = "SELECT c FROM ConfigInfo c WHERE c.ussdCode = :ussdCode"),
	@NamedQuery(name="getConfigInfoBySMSCode", query = "SELECT c FROM ConfigInfo c WHERE c.smsCode = :smsCode"), 
	@NamedQuery(name="findConfigInfoByOwnerId", query = "SELECT c FROM ConfigInfo c WHERE c.ownerId = :ownerId")
	
})
public class ConfigInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 50, name = "configId")
	private String configId;

	@Column(length = 10, name = "smsCode")
	private String smsCode;

	@Column(length = 10, name = "ussdCode")
	private String ussdCode;

	@Column(length = 30, name = "requestQueueName")
	private String requestQueueName;

	@Column(length = 30, name = "replyQueueName")
	private String replyQueueName;

	@Column(length = 50, name = "ownerId", unique = true)
	private String ownerId;
	
	@Column(length = 10, name = "serviceId")
	private String serviceId;
	
	@Column(length = 30, name = "serviceProviderId")
	private String serviceProviderId;

	@Column(length = 30, name = "status")
	private String status;
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public ConfigInfo() {
		super();
	}

	/**
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return the serviceProviderId
	 */
	public String getServiceProviderId() {
		return serviceProviderId;
	}

	/**
	 * @param serviceProviderId the serviceProviderId to set
	 */
	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public String getConfigId() {
		return this.configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getUssdCode() {
		return ussdCode;
	}

	public void setUssdCode(String ussdCode) {
		this.ussdCode = ussdCode;
	}

	public String getRequestQueueName() {
		return requestQueueName;
	}

	public void setRequestQueueName(String requestQueueName) {
		this.requestQueueName = requestQueueName;
	}

	public String getReplyQueueName() {
		return replyQueueName;
	}

	public void setReplyQueueName(String replyQueueName) {
		this.replyQueueName = replyQueueName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

}
