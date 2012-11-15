package zw.co.esolutions.ewallet.util;

public class BatchResults {
	private long autoRegNumber;
	private long alreadyRegistered;
	private long failedToPerformAutoReg;
	public long getAutoRegNumber() {
		return autoRegNumber;
	}
	public void setAutoRegNumber(long autoRegNumber) {
		this.autoRegNumber = autoRegNumber;
	}
	public long getAlreadyRegistered() {
		return alreadyRegistered;
	}
	public void setAlreadyRegistered(long alreadyRegistered) {
		this.alreadyRegistered = alreadyRegistered;
	}
	public long getFailedToPerformAutoReg() {
		return failedToPerformAutoReg;
	}
	public void setFailedToPerformAutoReg(long failedToPerformAutoReg) {
		this.failedToPerformAutoReg = failedToPerformAutoReg;
	}
	

}
