package zw.co.esolutions.ewallet.resolve;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.process.UniversalProcessSearch;
import zw.co.esolutions.ewallet.util.DateUtil;

public class FindTransactionBean extends PageCodeBase {
	ProcessServiceSOAPProxy processService = new ProcessServiceSOAPProxy();
	private List<SelectItem> criteriaList;
	private List<SelectItem> statusList;
	private String selectedCriteria;
	private String selectedStatus;
	private String referenceId;
	private List<ProcessTransaction> txnList;
	private boolean renderStatus;
	private boolean renderReference;
	private Date toDate;
	private Date fromDate;
	
	
	
	public FindTransactionBean() {
		super();
		this.initializeDates();
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public boolean isRenderReference() {
		return renderReference;
	}
	public void setRenderReference(boolean renderReference) {
		this.renderReference = renderReference;
	}
	public void setCriteriaList(List<SelectItem> criteriaList) {
		this.criteriaList = criteriaList;
	}
	public List<SelectItem> getCriteriaList() {
		if (criteriaList == null) {
			criteriaList = new ArrayList<SelectItem>();
			criteriaList.add(new SelectItem("TXN_REF", "Transaction Reference"));
			criteriaList.add(new SelectItem("TXN_STATUS", "Transaction Status"));
		}
		return criteriaList;
	}
	public void setStatusList(List<SelectItem> statusList) {
		this.statusList = statusList;
	}
	public List<SelectItem> getStatusList() {
		if (statusList == null) {
			statusList = new ArrayList<SelectItem>();
		//	statusList.add(new SelectItem("all", "ALL"));
			for (TransactionStatus status: TransactionStatus.values()) {
				if(!(TransactionStatus.PAREQ.equals(status) || 
						TransactionStatus.VEREQ.equals(status) || 
						TransactionStatus.VERES.equals(status) ||
						TransactionStatus.FAILED.equals(status) ||
						TransactionStatus.COMPLETED.equals(status) ||
						TransactionStatus.AWAITING_COMPLETION_APPROVAL.equals(status) ||
						TransactionStatus.AWAITING_FAILURE_APPROVAL.equals(status) ||
						TransactionStatus.AWAITING_APPROVAL.equals(status) ||
						TransactionStatus.PAREQ.equals(status))) {
					statusList.add(new SelectItem(status.name(), status.name()));
				}
			}
		}
		return statusList;
	}
	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}
	public String getSelectedCriteria() {
		if(this.selectedCriteria == null) {
			this.selectedCriteria = "TXN_REF";
			this.renderReference = true;
		}
		return selectedCriteria;
	}
	public void setSelectedStatus(String selectedStatus) {
		this.selectedStatus = selectedStatus;
	}
	public String getSelectedStatus() {
		return selectedStatus;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setTxnList(List<ProcessTransaction> txnList) {
		this.txnList = txnList;
	}
	public List<ProcessTransaction> getTxnList() {
		return txnList;
	}
	
	public void setRenderStatus(boolean renderStatus) {
		this.renderStatus = renderStatus;
	}
	public boolean isRenderStatus() {
		return renderStatus;
	}
	public void processCriteriaValueChange(ValueChangeEvent event) {
		if ("TXN_STATUS".equals((String) event.getNewValue())) {
			setRenderStatus(true);
		}
	}
	
	public String search() {
		txnList = new ArrayList<ProcessTransaction>();
		if ("TXN_REF".equals(selectedCriteria)) {
			if (referenceId == null || referenceId.trim().equals("")) {
				super.setErrorMessage("Please enter the reference number.");
				return "failure";
			}
			try {
				UniversalProcessSearch uni = new UniversalProcessSearch();
				uni.setMessageId(this.getReferenceId());
				this.setTxnList(this.processService.getProcessTransactionsByAllAttributes(uni));
				if (this.getTxnList() == null || this.getTxnList().isEmpty()) {
					super.setInformationMessage("No Transactions found.");
					return "failure";
				}
			} catch (Exception e) {
				e.printStackTrace();
				super.setErrorMessage("Error retrieving transaction.");
				return "failure";
			}
			
		} else if("TXN_STATUS".equals(selectedCriteria)){
			if (selectedStatus == null || "".equals(selectedStatus)) {
				super.setErrorMessage("Please select the transaction status.");
				return "failure";
			}
			String str = this.checkAttributes();
			if(str != null) {
				super.setErrorMessage(str);
				return "failure";
			}
			
			// Check for ToDate
			if(this.getToDate() != null) {
				if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getToDate()), DateUtil.getEndOfDay(new Date()))) {
					super.setErrorMessage("To Date cannot come after this Today's Date.");
					return "failure";
				}
			}
			
			// toDate must be greater than fromDate
			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getFromDate()), DateUtil.getEndOfDay(getToDate()))) {
				super.setErrorMessage("From Date must be a day before(or the same with) To Date.");
				return "failure";
			}
			
			try {
				UniversalProcessSearch uni = new UniversalProcessSearch();
				uni.setManualResolve(true);
				if(!"all".equalsIgnoreCase(this.getSelectedStatus())) {
					uni.setStatus(zw.co.esolutions.ewallet.process.TransactionStatus.valueOf(selectedStatus));
				}
				uni.setFromDate(DateUtil.convertToXMLGregorianCalendar(DateUtil.getBeginningOfDay(getFromDate())));
				uni.setToDate(DateUtil.convertToXMLGregorianCalendar(DateUtil.getEndOfDay(this.getToDate())));
				
				this.setTxnList(this.processService.getProcessTransactionsByAllAttributes(uni));
				
				if (this.getTxnList() == null || this.getTxnList().isEmpty()) {
					super.setInformationMessage("No Transactions found.");
					return "failure";
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				super.setErrorMessage("Error retrieving transactions.");
				return "failure";
			}
		} else {
			super.setErrorMessage("Unsupported Operation.");
			return "failure";
		}
		
		if (!txnList.isEmpty()) {
			super.setInformationMessage(txnList.size() + " transaction(s) found.");
		}
		
		return "search";
	}
	public String viewAll() {
		super.setErrorMessage("Unsupported Operation.");
		return "viewAll";
	}
	
	@SuppressWarnings("unchecked")
	public String viewTransaction() {
		try {
			super.getRequestScope().put("messageId", super.getRequestParam().get("messageId"));
			this.clearFields();
			super.gotoPage("/resolve/transactionActions.jspx");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}
	
	public void handleValueChange(ValueChangeEvent event) {
		String item = (String)event.getNewValue();
		if(item != null) {
			try {
				System.out.println(">>>>>>>>>>>>>> Criteria = "+selectedCriteria);
				if("TXN_STATUS".equals(selectedCriteria)) {
					this.setRenderStatus(false);
					this.setRenderReference(true);
				} else {
					this.setRenderReference(false);
					this.setRenderStatus(true);
				}
				
			} catch (Exception e) {
				
			}
		}
	}
	
	private void clearFields() {
		/*this.criteriaList = null;
		this.referenceId = null;
		this.selectedCriteria = null;
		this.selectedStatus = null;
		this.statusList = null;*/
		this.txnList = null;
		
	}
	
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int initialLength = buffer.length();
		if(this.getFromDate() == null) {
			buffer.append("From Date, ");
			
		}
		if(this.getToDate() == null) {
			buffer.append("To Date, ");
		}
		int length = buffer.toString().length();
				
		if(length > initialLength) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	
	public void initializeDates() {
		this.fromDate = new Date(System.currentTimeMillis());
		this.toDate = new Date(System.currentTimeMillis());
	}
}
