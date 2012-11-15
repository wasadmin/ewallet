/**
 * 
 */
package pagecode;

import zw.co.esolutions.ussd.USSDManagedBean;
import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlForm;
import com.ibm.faces.component.html.HtmlPanelFormBox;
import com.ibm.faces.component.html.HtmlFormItem;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlPanelGrid;
import com.ibm.faces.component.html.HtmlCommandExButton;
import javax.faces.component.html.HtmlInputTextarea;

/**
 * @author prince
 *
 */
public class USSDService extends PageCodeBase {

	protected USSDManagedBean ussdManager;
	protected HtmlScriptCollector scriptCollector1;
	protected HtmlForm form1;
	protected HtmlPanelFormBox formBox1;
	protected HtmlFormItem formItem2;
	protected HtmlFormItem formItem3;
	protected HtmlFormItem formItem4;
	protected HtmlFormItem formItem5;
	protected HtmlInputText text1;
	protected HtmlInputText text2;
	protected HtmlInputText text3;
	protected HtmlInputText text4;
	protected HtmlPanelGrid grid1;
	protected HtmlCommandExButton button1;
	protected HtmlInputText text5;
	protected HtmlFormItem formItem1;
	protected HtmlFormItem formItem6;
	protected HtmlInputTextarea textarea1;
	/** 
	 * @managed-bean true
	 */
	protected USSDManagedBean getUssdManager() {
		if (ussdManager == null) {
			ussdManager = (USSDManagedBean) getManagedBean("ussdManager");
		}
		return ussdManager;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setUssdManager(USSDManagedBean ussdManager) {
		this.ussdManager = ussdManager;
	}

	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 = (HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}

	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}

	protected HtmlPanelFormBox getFormBox1() {
		if (formBox1 == null) {
			formBox1 = (HtmlPanelFormBox) findComponentInRoot("formBox1");
		}
		return formBox1;
	}

	protected HtmlFormItem getFormItem2() {
		if (formItem2 == null) {
			formItem2 = (HtmlFormItem) findComponentInRoot("formItem2");
		}
		return formItem2;
	}

	protected HtmlFormItem getFormItem3() {
		if (formItem3 == null) {
			formItem3 = (HtmlFormItem) findComponentInRoot("formItem3");
		}
		return formItem3;
	}

	protected HtmlFormItem getFormItem4() {
		if (formItem4 == null) {
			formItem4 = (HtmlFormItem) findComponentInRoot("formItem4");
		}
		return formItem4;
	}

	protected HtmlFormItem getFormItem5() {
		if (formItem5 == null) {
			formItem5 = (HtmlFormItem) findComponentInRoot("formItem5");
		}
		return formItem5;
	}

	protected HtmlInputText getText1() {
		if (text1 == null) {
			text1 = (HtmlInputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlInputText getText2() {
		if (text2 == null) {
			text2 = (HtmlInputText) findComponentInRoot("text2");
		}
		return text2;
	}

	protected HtmlInputText getText3() {
		if (text3 == null) {
			text3 = (HtmlInputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected HtmlInputText getText4() {
		if (text4 == null) {
			text4 = (HtmlInputText) findComponentInRoot("text4");
		}
		return text4;
	}

	protected HtmlPanelGrid getGrid1() {
		if (grid1 == null) {
			grid1 = (HtmlPanelGrid) findComponentInRoot("grid1");
		}
		return grid1;
	}

	protected HtmlCommandExButton getButton1() {
		if (button1 == null) {
			button1 = (HtmlCommandExButton) findComponentInRoot("button1");
		}
		return button1;
	}

	protected HtmlInputText getText5() {
		if (text5 == null) {
			text5 = (HtmlInputText) findComponentInRoot("text5");
		}
		return text5;
	}

	protected HtmlFormItem getFormItem1() {
		if (formItem1 == null) {
			formItem1 = (HtmlFormItem) findComponentInRoot("formItem1");
		}
		return formItem1;
	}

	protected HtmlFormItem getFormItem6() {
		if (formItem6 == null) {
			formItem6 = (HtmlFormItem) findComponentInRoot("formItem6");
		}
		return formItem6;
	}

	protected HtmlInputTextarea getTextarea1() {
		if (textarea1 == null) {
			textarea1 = (HtmlInputTextarea) findComponentInRoot("textarea1");
		}
		return textarea1;
	}

}