/**
 * 
 */
package pagecode.teller;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.tellerweb.LoginBean;

import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlPanelGrid;

/**
 * @author stanford
 *
 */
public class EditCustomer extends PageCodeBase {

	protected LoginBean loginBean;
	protected HtmlCommandButton button2;
	protected HtmlPanelGrid grid4;
	protected HtmlCommandButton button1;

	/** 
	 * @managed-bean true
	 */
	protected LoginBean getLoginBean() {
		if (loginBean == null) {
			loginBean = (LoginBean) getManagedBean("loginBean");
		}
		return loginBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	protected HtmlCommandButton getButton2() {
		if (button2 == null) {
			button2 = (HtmlCommandButton) findComponentInRoot("button2");
		}
		return button2;
	}

	protected HtmlPanelGrid getGrid4() {
		if (grid4 == null) {
			grid4 = (HtmlPanelGrid) findComponentInRoot("grid4");
		}
		return grid4;
	}

	protected HtmlCommandButton getButton1() {
		if (button1 == null) {
			button1 = (HtmlCommandButton) findComponentInRoot("button1");
		}
		return button1;
	}

}