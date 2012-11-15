/**
 * 
 */
package pagecode;

import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlCommandButton;
import zw.co.esolutions.ewallet.adminweb.ChangeProfilePasswordBean;

/**
 * @author stanford
 *
 */
public class ChangeProfilePassword extends PageCodeBase {

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
	protected HtmlForm form1;
	protected HtmlInputText text4;
	protected HtmlOutputLabel label6;
	protected HtmlOutputLabel label7;
	protected HtmlInputText text5;
	protected HtmlInputText text6;
	protected HtmlPanelGrid grid2;
	protected HtmlCommandButton button1;
	protected HtmlCommandButton button2;
	protected ChangeProfilePasswordBean changeProfilePasswordBean;
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
	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}
	protected HtmlInputText getText4() {
		if (text4 == null) {
			text4 = (HtmlInputText) findComponentInRoot("text4");
		}
		return text4;
	}
	protected HtmlOutputLabel getLabel6() {
		if (label6 == null) {
			label6 = (HtmlOutputLabel) findComponentInRoot("label6");
		}
		return label6;
	}
	protected HtmlOutputLabel getLabel7() {
		if (label7 == null) {
			label7 = (HtmlOutputLabel) findComponentInRoot("label7");
		}
		return label7;
	}
	protected HtmlInputText getText5() {
		if (text5 == null) {
			text5 = (HtmlInputText) findComponentInRoot("text5");
		}
		return text5;
	}
	protected HtmlInputText getText6() {
		if (text6 == null) {
			text6 = (HtmlInputText) findComponentInRoot("text6");
		}
		return text6;
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
	/** 
	 * @managed-bean true
	 */
	protected ChangeProfilePasswordBean getChangeProfilePasswordBean() {
		if (changeProfilePasswordBean == null) {
			changeProfilePasswordBean = (ChangeProfilePasswordBean) getManagedBean("changeProfilePasswordBean");
		}
		return changeProfilePasswordBean;
	}
	/** 
	 * @managed-bean true
	 */
	protected void setChangeProfilePasswordBean(
			ChangeProfilePasswordBean changeProfilePasswordBean) {
		this.changeProfilePasswordBean = changeProfilePasswordBean;
	}

}