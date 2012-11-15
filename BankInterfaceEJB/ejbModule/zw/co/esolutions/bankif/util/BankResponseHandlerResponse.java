/**
 * 
 */
package zw.co.esolutions.bankif.util;

import java.io.Serializable;
import java.util.List;

import zw.co.esolutions.bankif.model.CommissionMessage;
import zw.co.esolutions.ewallet.msg.BankResponse;

/**
 * @author blessing
 *
 */
public class BankResponseHandlerResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4622953199551474697L;
	private BankResponse bankResponse;
	private List<CommissionMessage> commissionMessages;
	/**
	 * 
	 */
	public BankResponseHandlerResponse() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the bankResponse
	 */
	public BankResponse getBankResponse() {
		return bankResponse;
	}
	/**
	 * @param bankResponse the bankResponse to set
	 */
	public void setBankResponse(BankResponse bankResponse) {
		this.bankResponse = bankResponse;
	}
	/**
	 * @return the commissionMessages
	 */
	public List<CommissionMessage> getCommissionInfos() {
		return commissionMessages;
	}
	/**
	 * @param commissionMessages the commissionMessages to set
	 */
	public void setCommissionInfos(List<CommissionMessage> commissionMessages) {
		this.commissionMessages = commissionMessages;
	}
	
	public BankResponseHandlerResponse(BankResponse bankResponse, List<CommissionMessage> commissionMessages) {
		super();
		this.bankResponse = bankResponse;
		this.commissionMessages = commissionMessages;
	}
	
}
