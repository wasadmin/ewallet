package zw.co.esolutions.ewallet.tellerweb;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.Transaction;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class ViewStartDayTxnPostings extends PageCodeBase {
	private List<Transaction> transactions;
	private ProcessTransaction processTransaction;
	private String messageId;
	private String fromPage;
	private String header;
	private String tableHeader;
	private boolean renderBack;
	private boolean renderTeller;
	private Profile profile;

	/**
	 * 
	 */
	
	
	private static Logger LOG ;
		
		static{
			try {
				PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
				LOG = Logger.getLogger(ViewStartDayTxnPostings.class);
			} catch (Exception e) {
				System.err.println("Failed to initilise logger for " + ViewStartDayTxnPostings.class);
			}
		}
		
		
		
	
	
	public ViewStartDayTxnPostings() {
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
	
	@SuppressWarnings("unchecked")
	public String back() {
		super.gotoPage("/teller/"+this.getFromPage());
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
				LOG.debug(">>>>>>>>>>>>>>>>>> Transactions null");
				BankServiceSOAPProxy bes = new BankServiceSOAPProxy();
				this.transactions = bes.getTransactionByProcessTransactionMessageId(this.getMessageId());
				LOG.debug(">>>>>>>>>>>>>>>>>>> Txn = "+transactions);
				if(this.transactions == null || this.transactions.isEmpty()) {
					LOG.debug(">>>>>>>>>>>>>>>> Null Txn");
				} else {
					LOG.debug(">>>>>>>>>>>>>>>> Not Null Txn "+transactions.size());
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

	public void setRenderTeller(boolean renderTeller) {
		this.renderTeller = renderTeller;
	}

	public boolean isRenderTeller() {
		return renderTeller;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Profile getProfile() {
		if(this.profile == null && this.getProcessTransaction() != null) {
			if(this.getProcessTransaction().getProfileId() != null) {
				this.setRenderTeller(true);
				try { 
					ProfileServiceSOAPProxy pf = new ProfileServiceSOAPProxy();
					this.profile = pf.findProfileById(this.getProcessTransaction().getProfileId());
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			
			}
		}
		return profile;
	}

}
