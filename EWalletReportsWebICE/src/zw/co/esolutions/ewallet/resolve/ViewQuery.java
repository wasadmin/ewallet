package zw.co.esolutions.ewallet.resolve;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.ManualPojo;
import zw.co.esolutions.ewallet.process.ProcessTransaction;

public class ViewQuery extends PageCodeBase {
	private String processTxnId;
	
	private ProcessTransaction txn;
	private ManualPojo manualPojo;
	
	
	

	public ManualPojo getManualPojo() {
		this.manualPojo=(ManualPojo)super.getRequestScope().get("manualPojo");
		System.out.println(">>>>>>>>>>>>>>>manualpojo in view>>>>>>>>>>>"+this.manualPojo);
		return manualPojo;
	}

	public void setManualPojo(ManualPojo manualPojo) {
		this.manualPojo = manualPojo;
	}

	public String getProcessTxnId() {
		this.processTxnId=(String) super.getRequestScope().get("txnId");
		System.out.println(">>>>>>>>>>in view processtxnid>>>>"+this.processTxnId);
		return processTxnId;
	}

	public void setProcessTxnId(String processTxnId) {
		this.processTxnId = processTxnId;
	}

	public ProcessTransaction getTxn() {
		if(getProcessTxnId()!=null){
			try {
				this.txn=super.getProcessService().findProcessTransactionById(this.processTxnId);
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>txn>>>>>>>>>>>>>"+txn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return txn;
	}

	public void setTxn(ProcessTransaction txn) {
		this.txn = txn;
	}
	
	

}
