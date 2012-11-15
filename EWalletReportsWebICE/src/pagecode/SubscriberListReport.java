/**
 * 
 */
package pagecode;

import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlCommandButton;
import zw.co.esolutions.ewallet.reports.SubscriberListReportBean;
import javax.faces.component.UISelectItems;

/**
 * @author stanford
 *
 */
public class SubscriberListReport extends PageCodeBase {

	protected HtmlPanelGrid grid1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label2;
	protected HtmlForm form1;
	protected HtmlSelectOneMenu menu1;
	protected HtmlOutputLabel label3;
	protected HtmlSelectOneMenu menu2;
	protected HtmlOutputLabel label4;
	protected HtmlInputText text1;
	protected HtmlOutputLabel label5;
	protected HtmlInputText text2;
	protected HtmlCommandButton button1;
	protected SubscriberListReportBean subscriberListReportBean;
	protected UISelectItems selectItems1;
	protected UISelectItems selectItems2;
	protected HtmlPanelGrid getGrid1() {
		if (grid1 == null) {
			grid1 = (HtmlPanelGrid) findComponentInRoot("grid1");
		}
		return grid1;
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

	protected HtmlOutputLabel getLabel2() {
		if (label2 == null) {
			label2 = (HtmlOutputLabel) findComponentInRoot("label2");
		}
		return label2;
	}

	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}

	protected HtmlSelectOneMenu getMenu1() {
		if (menu1 == null) {
			menu1 = (HtmlSelectOneMenu) findComponentInRoot("menu1");
		}
		return menu1;
	}

	protected HtmlOutputLabel getLabel3() {
		if (label3 == null) {
			label3 = (HtmlOutputLabel) findComponentInRoot("label3");
		}
		return label3;
	}

	protected HtmlSelectOneMenu getMenu2() {
		if (menu2 == null) {
			menu2 = (HtmlSelectOneMenu) findComponentInRoot("menu2");
		}
		return menu2;
	}

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlInputText getText1() {
		if (text1 == null) {
			text1 = (HtmlInputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlOutputLabel getLabel5() {
		if (label5 == null) {
			label5 = (HtmlOutputLabel) findComponentInRoot("label5");
		}
		return label5;
	}

	protected HtmlInputText getText2() {
		if (text2 == null) {
			text2 = (HtmlInputText) findComponentInRoot("text2");
		}
		return text2;
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
	protected SubscriberListReportBean getSubscriberListReportBean() {
		if (subscriberListReportBean == null) {
			subscriberListReportBean = (SubscriberListReportBean) getManagedBean("subscriberListReportBean");
		}
		return subscriberListReportBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setSubscriberListReportBean(
			SubscriberListReportBean subscriberListReportBean) {
		this.subscriberListReportBean = subscriberListReportBean;
	}

	protected UISelectItems getSelectItems1() {
		if (selectItems1 == null) {
			selectItems1 = (UISelectItems) findComponentInRoot("selectItems1");
		}
		return selectItems1;
	}

	protected UISelectItems getSelectItems2() {
		if (selectItems2 == null) {
			selectItems2 = (UISelectItems) findComponentInRoot("selectItems2");
		}
		return selectItems2;
	}

}