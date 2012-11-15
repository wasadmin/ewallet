/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.csrweb.ApproveCustomerBean;

/**
 * @author stanford
 *
 */
public class ApproveCustomer extends PageCodeBase {

	protected ApproveCustomerBean approveCustomerBean;

	/** 
	 * @managed-bean true
	 */
	protected ApproveCustomerBean getApproveCustomerBean() {
		if (approveCustomerBean == null) {
			approveCustomerBean = (ApproveCustomerBean) getManagedBean("approveCustomerBean");
		}
		return approveCustomerBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setApproveCustomerBean(
			ApproveCustomerBean approveCustomerBean) {
		this.approveCustomerBean = approveCustomerBean;
	}

}