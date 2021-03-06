/**
 * 
 */
package zw.co.esolutions.ewallet.tellerweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.Transaction;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;

/**
 * @author taurai
 *
 */
public class ViewDayEndPostingsBean extends PageCodeBase{
	
	private List<zw.co.esolutions.ewallet.bankservices.service.Transaction> transactions;
	private ProcessTransaction processTransaction;
	private String messageId;
	private String fromPage;
	private String header;
	private String tableHeader;
	private boolean renderBack;
	private String dayEndId;

	/**
	 * 
	 */
	public ViewDayEndPostingsBean() {
		if(this.getMessageId() == null) {
			this.setMessageId((String)super.getRequestScope().get("messageId"));
		} if(this.getFromPage() == null) {
			this.setFromPage((String)super.getRequestScope().get("fromPage"));
		}
	}

	public String home() {
		super.gotoPage("/teller/tellerHome.jspx");
		return "success";
	}
	
	public String back() {
		super.gotoPage("/teller/"+this.getFromPage());
		getRequestScope().put("dayEndId", this.getMessageId());
		getRequestScope().put("messageId", this.getMessageId());
		this.setFromPage(null);
		this.setHeader(null);
		this.setMessageId(null);
		this.setProcessTransaction(null);
		this.setTableHeader(null);
		this.setTransactions(null);
		return "success";
	}
	/**
	 * @param transactions the transactions to set
	 */
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	/**
	 * @return the transactions
	 */
	public List<Transaction> getTransactions() {
		if((this.transactions == null || this.transactions.isEmpty()) && (this.getMessageId() != null) ){
			try {
				//System.out.println(">>>>>>>>>>>>>>>>>> Transactions null");
				BankServiceSOAPProxy bes = new BankServiceSOAPProxy();
				this.transactions = bes.getTransactionByProcessTransactionMessageId(this.getMessageId());
				//System.out.println(">>>>>>>>>>>>>>>>>>> Txn = "+transactions);
				if(this.transactions == null || this.transactions.isEmpty()) {
					//System.out.println(">>>>>>>>>>>>>>>> Null Txn");
				} else {
					//System.out.println(">>>>>>>>>>>>>>>> Not Null Txn "+transactions.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return transactions;
	}

	/**
	 * @param processTransaction the processTransaction to set
	 */
	public void setProcessTransaction(ProcessTransaction processTransaction) {
		this.processTransaction = processTransaction;
	}

	/**
	 * @return the processTransaction
	 */
	public ProcessTransaction getProcessTransaction() {
		System.out.println("message id is ");
		if(this.processTransaction == null && this.getMessageId() != null) {
			try {
				ProcessServiceSOAPProxy ps = new ProcessServiceSOAPProxy();
				this.processTransaction = ps.getProcessTransactionByMessageId(this.getMessageId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return processTransaction;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		if(messageId == null) {
			this.messageId=(String)super.getRequestScope().get("messageId");
		}
		return messageId;
	}

	/**
	 * @param fromPage the fromPage to set
	 */
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	/**
	 * @return the fromPage
	 */
	public String getFromPage() {
		if(fromPage==null){
		this.fromPage=(String)super.getRequestScope().get("fromPage");
		}
		return fromPage;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		if(this.header == null && this.getProcessTransaction() != null) {
			try{ 
				this.header = "Postings for "+this.getProcessTransaction().getTransactionType().toString().replace("_", " ");
			} catch (Exception e) {
				
			}
		}
		return header;
	}

	/**
	 * @param tableHeader the tableHeader to set
	 */
	public void setTableHeader(String tableHeader) {
		this.tableHeader = tableHeader;
	}

	/**
	 * @return the tableHeader
	 */
	public String getTableHeader() {
		if(this.tableHeader == null && this.getProcessTransaction() != null) {
			this.tableHeader = this.getProcessTransaction().getTransactionType().toString().replace("_", " ");
		}
		return tableHeader;
	}

	public void setRenderBack(boolean renderBack) {
		this.renderBack = renderBack;
	}

	public boolean isRenderBack() {
		if(this.getFromPage() == null) {
			this.renderBack = false;
		} else {
			this.renderBack = true;
		}
		return renderBack;
	}

	public String getDayEndId() {
		if(dayEndId==null){
			dayEndId=(String) getRequestScope().get("dayEndId");
		}
		return dayEndId;
	}

	public void setDayEndId(String dayEndId) {
		this.dayEndId = dayEndId;
	}

	
	
	
}
