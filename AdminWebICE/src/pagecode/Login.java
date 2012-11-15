/**
 * 
 */
package pagecode;

import zw.co.esolutions.ewallet.adminweb.LoginBean;
import com.icesoft.faces.component.ext.HtmlForm;
import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlInputSecret;


/**
 * @author stanford
 *
 */
public class Login extends PageCodeBase {

	protected LoginBean loginBean;
	protected HtmlOutputLabel label2;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlCommandButton button2;
	protected HtmlInputText text1;
	protected HtmlOutputLabel label3;
	protected HtmlInputSecret secret1;
	protected HtmlPanelGrid grid2;
	protected HtmlCommandButton button3;
	protected HtmlForm form1;
	protected HtmlPanelGrid grid1;
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

	protected HtmlOutputLabel getLabel2() {
		if (label2 == null) {
			label2 = (HtmlOutputLabel) findComponentInRoot("label2");
		}
		return label2;
	}

	protected HtmlPanelGroup getGroup1() {
		if (group1 == null) {
			group1 = (HtmlPanelGroup) findComponentInRoot("group1");
		}
		return group1;
	}

	protected HtmlOutputLabel getLabel1() {
		if (label1 == null) {
			label1 = (HtmlOutputLabel) findComponentInRoot("label1");
		}
		return label1;
	}

	protected HtmlCommandButton getButton2() {
		if (button2 == null) {
			button2 = (HtmlCommandButton) findComponentInRoot("button2");
		}
		return button2;
	}

	protected HtmlInputText getText1() {
		if (text1 == null) {
			text1 = (HtmlInputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlOutputLabel getLabel3() {
		if (label3 == null) {
			label3 = (HtmlOutputLabel) findComponentInRoot("label3");
		}
		return label3;
	}

	protected HtmlInputSecret getSecret1() {
		if (secret1 == null) {
			secret1 = (HtmlInputSecret) findComponentInRoot("secret1");
		}
		return secret1;
	}

	protected HtmlPanelGrid getGrid2() {
		if (grid2 == null) {
			grid2 = (HtmlPanelGrid) findComponentInRoot("grid2");
		}
		return grid2;
	}

	protected HtmlCommandButton getButton3() {
		if (button3 == null) {
			button3 = (HtmlCommandButton) findComponentInRoot("button3");
		}
		return button3;
	}

	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}

	protected HtmlPanelGrid getGrid1() {
		if (grid1 == null) {
			grid1 = (HtmlPanelGrid) findComponentInRoot("grid1");
		}
		return grid1;
	}

}