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
import com.ibm.faces.component.html.HtmlCommandExButton;
import zw.co.esolutions.ewallet.reports.TopXCustomersReportBean;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlMessages;

/**
 * @author stanford
 *
 */
public class TopXCustomersReport extends PageCodeBase {

	protected HtmlPanelGrid grid1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label2;
	protected HtmlForm form1;
	protected HtmlSelectOneMenu menu1;
	protected HtmlOutputLabel label3;
	protected HtmlInputText text1;
	protected HtmlCommandExButton button1;
	protected TopXCustomersReportBean topXCustomersReportBean;
	protected UISelectItems selectItems1;
	protected HtmlMessages messages1;
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

	protected HtmlInputText getText1() {
		if (text1 == null) {
			text1 = (HtmlInputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlCommandExButton getButton1() {
		if (button1 == null) {
			button1 = (HtmlCommandExButton) findComponentInRoot("button1");
		}
		return button1;
	}

	/** 
	 * @managed-bean true
	 */
	protected TopXCustomersReportBean getTopXCustomersReportBean() {
		if (topXCustomersReportBean == null) {
			topXCustomersReportBean = (TopXCustomersReportBean) getManagedBean("topXCustomersReportBean");
		}
		return topXCustomersReportBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setTopXCustomersReportBean(
			TopXCustomersReportBean topXCustomersReportBean) {
		this.topXCustomersReportBean = topXCustomersReportBean;
	}

	protected UISelectItems getSelectItems1() {
		if (selectItems1 == null) {
			selectItems1 = (UISelectItems) findComponentInRoot("selectItems1");
		}
		return selectItems1;
	}

	protected HtmlMessages getMessages1() {
		if (messages1 == null) {
			messages1 = (HtmlMessages) findComponentInRoot("messages1");
		}
		return messages1;
	}

}