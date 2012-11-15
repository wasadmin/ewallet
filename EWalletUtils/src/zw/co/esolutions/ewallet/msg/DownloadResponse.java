/**
 * 
 */
package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

import zw.co.esolutions.mcommerce.msg.BankRegistrationMessageDocument;

/**
 * @author taurai
 *
 */
public class DownloadResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BankRegistrationMessageDocument getDoc() {
		return doc;
	}
	public void setDoc(BankRegistrationMessageDocument doc) {
		this.doc = doc;
	}
	public DownloadRequest getDownloadRequest() {
		return downloadRequest;
	}
	public void setDownloadRequest(DownloadRequest downloadRequest) {
		this.downloadRequest = downloadRequest;
	}
	private BankRegistrationMessageDocument doc;
	private DownloadRequest downloadRequest;
	/**
	 * 
	 */
	public DownloadResponse() {
		// TODO Auto-generated constructor stub
	}

}
