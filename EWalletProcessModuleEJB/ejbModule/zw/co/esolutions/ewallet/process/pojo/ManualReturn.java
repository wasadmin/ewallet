package zw.co.esolutions.ewallet.process.pojo;

import zw.co.esolutions.ewallet.process.model.ProcessTransaction;

public class ManualReturn {
	private String response;
	private ProcessTransaction txn;
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public ProcessTransaction getTxn() {
		return txn;
	}
	public void setTxn(ProcessTransaction txn) {
		this.txn = txn;
	}
	
	
}
