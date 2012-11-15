/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.csrweb.ApproveBankAccountBean;
import zw.co.esolutions.ewallet.csrweb.ApproveCustomerMerchantBean;

/**
 * @author stanford
 *
 */
public class ApproveCustomerMerchant extends PageCodeBase {

	protected ApproveBankAccountBean approveBankAccountBean;
	protected ApproveCustomerMerchantBean approveCustomerMerchantBean;

	/** 
	 * @managed-bean true
	 */
	protected ApproveBankAccountBean getApproveBankAccountBean() {
		if (approveBankAccountBean == null) {
			approveBankAccountBean = (ApproveBankAccountBean) getManagedBean("approveBankAccountBean");
		}
		return approveBankAccountBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setApproveBankAccountBean(
			ApproveBankAccountBean approveBankAccountBean) {
		this.approveBankAccountBean = approveBankAccountBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected ApproveCustomerMerchantBean getApproveCustomerMerchantBean() {
		if (approveCustomerMerchantBean == null) {
			approveCustomerMerchantBean = (ApproveCustomerMerchantBean) getManagedBean("approveCustomerMerchantBean");
		}
		return approveCustomerMerchantBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setApproveCustomerMerchantBean(
			ApproveCustomerMerchantBean approveCustomerMerchantBean) {
		this.approveCustomerMerchantBean = approveCustomerMerchantBean;
	}

}