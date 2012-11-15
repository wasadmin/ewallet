/**
 * 
 */
package pagecode.teller;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.tellerweb.ApproveBankAccountBean;

/**
 * @author stanford
 *
 */
public class ApproveBankAccount extends PageCodeBase {

	protected ApproveBankAccountBean approveBankAccountBean;

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

}