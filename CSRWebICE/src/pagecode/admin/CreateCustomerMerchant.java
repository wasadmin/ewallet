/**
 * 
 */
package pagecode.admin;

import pagecode.PageCodeBase;
import javax.faces.component.html.HtmlSelectOneMenu;
import zw.co.esolutions.ewallet.csrweb.CreateCustomerMerchantBean;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlCommandButton;

/**
 * @author stanford
 *
 */
public class CreateCustomerMerchant extends PageCodeBase {

	protected HtmlSelectOneMenu menu1;
	protected CreateCustomerMerchantBean createCustomerMerchantBean;
	protected HtmlInputText text1;
	protected HtmlCommandButton button2;
	protected HtmlCommandButton button1;

	protected HtmlSelectOneMenu getMenu1() {
		if (menu1 == null) {
			menu1 = (HtmlSelectOneMenu) findComponentInRoot("menu1");
		}
		return menu1;
	}

	/** 
	 * @managed-bean true
	 */
	protected CreateCustomerMerchantBean getCreateCustomerMerchantBean() {
		if (createCustomerMerchantBean == null) {
			createCustomerMerchantBean = (CreateCustomerMerchantBean) getManagedBean("createCustomerMerchantBean");
		}
		return createCustomerMerchantBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setCreateCustomerMerchantBean(
			CreateCustomerMerchantBean createCustomerMerchantBean) {
		this.createCustomerMerchantBean = createCustomerMerchantBean;
	}

	protected HtmlInputText getText1() {
		if (text1 == null) {
			text1 = (HtmlInputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlCommandButton getButton2() {
		if (button2 == null) {
			button2 = (HtmlCommandButton) findComponentInRoot("button2");
		}
		return button2;
	}

	protected HtmlCommandButton getButton1() {
		if (button1 == null) {
			button1 = (HtmlCommandButton) findComponentInRoot("button1");
		}
		return button1;
	}

}