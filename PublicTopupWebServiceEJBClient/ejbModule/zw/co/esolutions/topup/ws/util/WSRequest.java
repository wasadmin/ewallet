/**
 * 
 */
package zw.co.esolutions.topup.ws.util;

import java.io.Serializable;

/**
 * @author wasadmin
 * 
 */
public class WSRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public WSRequest() {
		// TODO Auto-generated constructor stub
	}

	private String uuid;
	private String sourceMobileNumber;
	private String targetMobileNumber;
	private long amount;
	private String bankId;
	private String serviceId;
	private String serviceProviderId;
	private ServiceCommand serviceCommand;
	private MNOName mnoName;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSourceMobileNumber() {
		return sourceMobileNumber;
	}

	public void setSourceMobileNumber(String sourceMobileNumber) {
		this.sourceMobileNumber = sourceMobileNumber;
	}

	public String getTargetMobileNumber() {
		return targetMobileNumber;
	}

	public void setTargetMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public ServiceCommand getServiceCommand() {
		return serviceCommand;
	}

	public void setServiceCommand(ServiceCommand serviceCommand) {
		this.serviceCommand = serviceCommand;
	}

	public MNOName getMnoName() {
		return mnoName;
	}

	public void setMnoName(MNOName mnoName) {
		this.mnoName = mnoName;
	}

}
