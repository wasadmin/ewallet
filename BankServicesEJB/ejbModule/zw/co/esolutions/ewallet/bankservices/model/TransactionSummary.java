package zw.co.esolutions.ewallet.bankservices.model;

import java.io.Serializable;

public class TransactionSummary implements Serializable {

	private static final long serialVersionUID = 1L;

	private String header;
	private long openingBal;
	private long closingBal;
	private long netMovement;
	private String summaryDetails;
	private String description;

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public long getOpeningBal() {
		return openingBal;
	}

	public void setOpeningBal(long openingBal) {
		this.openingBal = openingBal;
	}

	public long getClosingBal() {
		return closingBal;
	}

	public void setClosingBal(long closingBal) {
		this.closingBal = closingBal;
	}

	public long getNetMovement() {
		return netMovement;
	}

	public void setNetMovement(long netMovement) {
		this.netMovement = netMovement;
	}

	public String getSummaryDetails() {
		return summaryDetails;
	}

	public void setSummaryDetails(String summaryDetails) {
		this.summaryDetails = summaryDetails;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
