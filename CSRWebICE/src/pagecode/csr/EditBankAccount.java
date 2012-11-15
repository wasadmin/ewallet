/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlCommandButton;
import zw.co.esolutions.ewallet.csrweb.EditBankAccountBean;
import javax.faces.component.UISelectItems;

/**
 * @author stanford
 *
 */
public class EditBankAccount extends PageCodeBase {

	protected HtmlScriptCollector scriptCollector1;
	protected HtmlPanelGrid grid1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label2;
	protected HtmlForm form1;
	protected HtmlInputText text1;
	protected HtmlOutputLabel label3;
	protected HtmlInputText text2;
	protected HtmlOutputLabel label4;
	protected HtmlInputText text3;
	protected HtmlOutputLabel label5;
	protected HtmlInputText text4;
	protected HtmlOutputLabel label6;
	protected HtmlInputText text5;
	protected HtmlOutputLabel label7;
	protected HtmlOutputLabel label8;
	protected HtmlOutputLabel label10;
	protected HtmlSelectOneMenu menu1;
	protected HtmlSelectOneMenu menu2;
	protected HtmlSelectOneMenu menu4;
	protected HtmlPanelGrid grid2;
	protected HtmlCommandButton button1;
	protected HtmlCommandButton button2;
	protected EditBankAccountBean editBankAccountBean;
	protected UISelectItems selectItems1;
	protected UISelectItems selectItems2;
	protected UISelectItems selectItems4;

	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 = (HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}

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

	protected HtmlInputText getText2() {
		if (text2 == null) {
			text2 = (HtmlInputText) findComponentInRoot("text2");
		}
		return text2;
	}

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlInputText getText3() {
		if (text3 == null) {
			text3 = (HtmlInputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected HtmlOutputLabel getLabel5() {
		if (label5 == null) {
			label5 = (HtmlOutputLabel) findComponentInRoot("label5");
		}
		return label5;
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

	protected HtmlInputText getText5() {
		if (text5 == null) {
			text5 = (HtmlInputText) findComponentInRoot("text5");
		}
		return text5;
	}

	protected HtmlOutputLabel getLabel7() {
		if (label7 == null) {
			label7 = (HtmlOutputLabel) findComponentInRoot("label7");
		}
		return label7;
	}

	protected HtmlOutputLabel getLabel8() {
		if (label8 == null) {
			label8 = (HtmlOutputLabel) findComponentInRoot("label8");
		}
		return label8;
	}

	protected HtmlOutputLabel getLabel10() {
		if (label10 == null) {
			label10 = (HtmlOutputLabel) findComponentInRoot("label10");
		}
		return label10;
	}

	protected HtmlSelectOneMenu getMenu1() {
		if (menu1 == null) {
			menu1 = (HtmlSelectOneMenu) findComponentInRoot("menu1");
		}
		return menu1;
	}

	protected HtmlSelectOneMenu getMenu2() {
		if (menu2 == null) {
			menu2 = (HtmlSelectOneMenu) findComponentInRoot("menu2");
		}
		return menu2;
	}

	protected HtmlSelectOneMenu getMenu4() {
		if (menu4 == null) {
			menu4 = (HtmlSelectOneMenu) findComponentInRoot("menu4");
		}
		return menu4;
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
	protected EditBankAccountBean getEditBankAccountBean() {
		if (editBankAccountBean == null) {
			editBankAccountBean = (EditBankAccountBean) getManagedBean("editBankAccountBean");
		}
		return editBankAccountBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setEditBankAccountBean(
			EditBankAccountBean editBankAccountBean) {
		this.editBankAccountBean = editBankAccountBean;
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

	protected UISelectItems getSelectItems4() {
		if (selectItems4 == null) {
			selectItems4 = (UISelectItems) findComponentInRoot("selectItems4");
		}
		return selectItems4;
	}

}