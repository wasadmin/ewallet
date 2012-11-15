/**
 * 
 */
package pagecode;

import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import zw.co.esolutions.ewallet.adminweb.ViewBankAccountBean;

/**
 * @author stanford
 *
 */
public class ViewBankAccount extends PageCodeBase {

	protected HtmlPanelGrid grid1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label2;
	protected HtmlOutputText text1;
	protected HtmlOutputLabel label3;
	protected HtmlOutputText text2;
	protected HtmlOutputLabel label4;
	protected HtmlOutputText text3;
	protected HtmlOutputLabel label5;
	protected HtmlOutputText text4;
	protected HtmlOutputLabel label6;
	protected HtmlOutputText text5;
	protected HtmlOutputLabel label7;
	protected HtmlOutputText text6;
	protected HtmlOutputLabel label8;
	protected HtmlOutputText text7;
	protected HtmlPanelGrid grid2;
	protected HtmlCommandButton button1;
	protected HtmlCommandButton button2;
	protected HtmlForm form1;
	protected ViewBankAccountBean viewBankAccountBean;

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

	protected HtmlOutputText getText1() {
		if (text1 == null) {
			text1 = (HtmlOutputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlOutputLabel getLabel3() {
		if (label3 == null) {
			label3 = (HtmlOutputLabel) findComponentInRoot("label3");
		}
		return label3;
	}

	protected HtmlOutputText getText2() {
		if (text2 == null) {
			text2 = (HtmlOutputText) findComponentInRoot("text2");
		}
		return text2;
	}

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlOutputText getText3() {
		if (text3 == null) {
			text3 = (HtmlOutputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected HtmlOutputLabel getLabel5() {
		if (label5 == null) {
			label5 = (HtmlOutputLabel) findComponentInRoot("label5");
		}
		return label5;
	}

	protected HtmlOutputText getText4() {
		if (text4 == null) {
			text4 = (HtmlOutputText) findComponentInRoot("text4");
		}
		return text4;
	}

	protected HtmlOutputLabel getLabel6() {
		if (label6 == null) {
			label6 = (HtmlOutputLabel) findComponentInRoot("label6");
		}
		return label6;
	}

	protected HtmlOutputText getText5() {
		if (text5 == null) {
			text5 = (HtmlOutputText) findComponentInRoot("text5");
		}
		return text5;
	}

	protected HtmlOutputLabel getLabel7() {
		if (label7 == null) {
			label7 = (HtmlOutputLabel) findComponentInRoot("label7");
		}
		return label7;
	}

	protected HtmlOutputText getText6() {
		if (text6 == null) {
			text6 = (HtmlOutputText) findComponentInRoot("text6");
		}
		return text6;
	}

	protected HtmlOutputLabel getLabel8() {
		if (label8 == null) {
			label8 = (HtmlOutputLabel) findComponentInRoot("label8");
		}
		return label8;
	}

	protected HtmlOutputText getText7() {
		if (text7 == null) {
			text7 = (HtmlOutputText) findComponentInRoot("text7");
		}
		return text7;
	}

	protected HtmlPanelGrid getGrid2() {
		if (grid2 == null) {
			grid2 = (HtmlPanelGrid) findComponentInRoot("grid2");
		}
		return grid2;
	}

	protected HtmlCommandButton getButton1() {
		if (button1 == null) {
			button1 = (HtmlCommandButton) findComponentInRoot("button1");
		}
		return button1;
	}

	protected HtmlCommandButton getButton2() {
		if (button2 == null) {
			button2 = (HtmlCommandButton) findComponentInRoot("button2");
		}
		return button2;
	}

	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}

	/** 
	 * @managed-bean true
	 */
	protected ViewBankAccountBean getViewBankAccountBean() {
		if (viewBankAccountBean == null) {
			viewBankAccountBean = (ViewBankAccountBean) getManagedBean("viewBankAccountBean");
		}
		return viewBankAccountBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setViewBankAccountBean(
			ViewBankAccountBean viewBankAccountBean) {
		this.viewBankAccountBean = viewBankAccountBean;
	}

}