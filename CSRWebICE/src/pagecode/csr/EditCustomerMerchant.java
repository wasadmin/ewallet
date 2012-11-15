/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.csrweb.EditCustomerMerchantBean;

/**
 * @author stanford
 *
 */
public class EditCustomerMerchant extends PageCodeBase {

	protected EditCustomerMerchantBean editCustomerMerchantBean;

	/** 
	 * @managed-bean true
	 */
	protected EditCustomerMerchantBean getEditCustomerMerchantBean() {
		if (editCustomerMerchantBean == null) {
			editCustomerMerchantBean = (EditCustomerMerchantBean) getManagedBean("editCustomerMerchantBean");
		}
		return editCustomerMerchantBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setEditCustomerMerchantBean(
			EditCustomerMerchantBean editCustomerMerchantBean) {
		this.editCustomerMerchantBean = editCustomerMerchantBean;
	}

}