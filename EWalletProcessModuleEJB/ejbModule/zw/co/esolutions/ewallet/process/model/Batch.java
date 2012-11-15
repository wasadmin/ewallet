package zw.co.esolutions.ewallet.process.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import zw.co.esolutions.ewallet.enums.OwnerType;

/**
 * Entity implementation class for Entity: BatchProcessor
 *
 */
@Entity

public class Batch implements Serializable {

	@Id
	@Column(length = 30)
	private String id;
	@Column(length = 30)
	private String entityId;
	private int startIndex;
	private int maxValue;
	private Date batchDate;
	private Date nextBatchDate;
	private boolean complete;
	private Date dateCreated;
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private OwnerType ownerType;
	private long totalPoolAmount;
	private long countValue;
	@Version
	private long version;
	private static final long serialVersionUID = 1L;

	public Batch() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public Date getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(Date batchDate) {
		this.batchDate = batchDate;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public OwnerType getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(OwnerType ownerType) {
		this.ownerType = ownerType;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public void setNextBatchDate(Date nextBatchDate) {
		this.nextBatchDate = nextBatchDate;
	}

	public Date getNextBatchDate() {
		return nextBatchDate;
	}

	public void setTotalPoolAmount(long totalPoolAmount) {
		this.totalPoolAmount = totalPoolAmount;
	}

	public long getTotalPoolAmount() {
		return totalPoolAmount;
	}

	public void setCountValue(long countValue) {
		this.countValue = countValue;
	}

	public long getCountValue() {
		return countValue;
	}
   
}
