package zw.co.esolutions.ewallet.customerservices.util;

import java.io.Serializable;

public class GenerateTxnPassCodeResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int firstIndex;
	private int secondIndex;
	
	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}
	public int getFirstIndex() {
		return firstIndex;
	}
	public void setSecondIndex(int secondIndex) {
		this.secondIndex = secondIndex;
	}
	public int getSecondIndex() {
		return secondIndex;
	}
	
	

}
