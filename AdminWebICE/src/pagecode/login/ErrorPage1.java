/**
 * 
 */
package pagecode.login;

import pagecode.PageCodeBase;
import javax.faces.component.html.HtmlForm;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import zw.co.esolutions.ewallet.adminweb.ErrorPageBean;

/**
 * @author stanford
 *
 */
public class ErrorPage1 extends PageCodeBase {

	protected HtmlForm form1;
	protected HtmlCommandButton button1;
	protected ErrorPageBean errorPageBean;

	public String back() {
		return "back";
	}
	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}

	protected HtmlCommandButton getButton1() {
		if (button1 == null) {
			button1 = (HtmlCommandButton) findComponentInRoot("button1");
		}
		return button1;
	}
	/** 
	 * @managed-bean true
	 */
	protected ErrorPageBean getErrorPageBean() {
		if (errorPageBean == null) {
			errorPageBean = (ErrorPageBean) getManagedBean("errorPageBean");
		}
		return errorPageBean;
	}
	/** 
	 * @managed-bean true
	 */
	protected void setErrorPageBean(ErrorPageBean errorPageBean) {
		this.errorPageBean = errorPageBean;
	}

}