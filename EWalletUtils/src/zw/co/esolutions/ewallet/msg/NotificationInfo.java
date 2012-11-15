package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;

public class NotificationInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String receiptientMobileNumber;
	private String narrative;
	private MobileNetworkOperator mno;
	private String destinationQueue;
	
	public NotificationInfo(String receiptientMobileNumber, String narrative, MobileNetworkOperator mno, String destinationQueue) {
		super();
		this.receiptientMobileNumber = receiptientMobileNumber;
		this.narrative = narrative;
		this.mno = mno;
		this.destinationQueue = destinationQueue;
	}
	
	/**
	 * @return the receiptientMobileNumber
	 */
	public String getReceiptientMobileNumber() {
		return receiptientMobileNumber;
	}
	/**
	 * @param receiptientMobileNumber the receiptientMobileNumber to set
	 */
	public void setReceiptientMobileNumber(String receiptientMobileNumber) {
		this.receiptientMobileNumber = receiptientMobileNumber;
	}
	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		return narrative;
	}
	/**
	 * @param narrative the narrative to set
	 */
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	/**
	 * @return the mno
	 */
	public MobileNetworkOperator getMno() {
		return mno;
	}
	/**
	 * @param mno the mno to set
	 */
	public void setMno(MobileNetworkOperator mno) {
		this.mno = mno;
	}
	
	/**
	 * @return the destinationQueue
	 */
	public String getDestinationQueue() {
		return destinationQueue;
	}
	/**
	 * @param destinationQueue the destinationQueue to set
	 */
	public void setDestinationQueue(String destinationQueue) {
		this.destinationQueue = destinationQueue;
	}
	
	@Override
	public String toString() {
		return this.getDestinationQueue() +" " + this.getNarrative() + " " + this.getReceiptientMobileNumber() + " " + this.getMno();
	}
	
}
