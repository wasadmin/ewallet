/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlCommandButton;

import zw.co.esolutions.ewallet.tellerweb.EditMobileProfileBean;

import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlPanelGrid;

/**
 * @author stanford
 *
 */
public class EditMobileProfile extends PageCodeBase {

	protected HtmlScriptCollector scriptCollector1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label2;
	protected HtmlOutputText text1;
	protected HtmlOutputLabel label3;
	protected HtmlOutputText text2;
	protected HtmlMessages messages1;
	protected HtmlPanelGroup group2;
	protected HtmlOutputLabel label4;
	protected HtmlOutputLabel label5;
	protected HtmlOutputLabel label6;
	protected HtmlSelectOneMenu menu1;
	protected HtmlInputText text3;
	protected HtmlOutputLabel label7;
	protected HtmlOutputText text4;
	protected HtmlCommandButton button1;
	protected HtmlCommandButton button2;
	protected EditMobileProfileBean editMobileProfileBean;
	protected UISelectItems selectItems1;
	protected HtmlCommandLink link1;
	protected HtmlForm form1;
	protected HtmlPanelGrid grid1;
	protected HtmlPanelGrid grid2;
	protected HtmlPanelGrid grid3;

	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 = (HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
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

	protected HtmlMessages getMessages1() {
		if (messages1 == null) {
			messages1 = (HtmlMessages) findComponentInRoot("messages1");
		}
		return messages1;
	}

	protected HtmlPanelGroup getGroup2() {
		if (group2 == null) {
			group2 = (HtmlPanelGroup) findComponentInRoot("group2");
		}
		return group2;
	}

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlOutputLabel getLabel5() {
		if (label5 == null) {
			label5 = (HtmlOutputLabel) findComponentInRoot("label5");
		}
		return label5;
	}

	protected HtmlOutputLabel getLabel6() {
		if (label6 == null) {
			label6 = (HtmlOutputLabel) findComponentInRoot("label6");
		}
		return label6;
	}

	protected HtmlSelectOneMenu getMenu1() {
		if (menu1 == null) {
			menu1 = (HtmlSelectOneMenu) findComponentInRoot("menu1");
		}
		return menu1;
	}

	protected HtmlInputText getText3() {
		if (text3 == null) {
			text3 = (HtmlInputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected HtmlOutputLabel getLabel7() {
		if (label7 == null) {
			label7 = (HtmlOutputLabel) findComponentInRoot("label7");
		}
		return label7;
	}

	protected HtmlOutputText getText4() {
		if (text4 == null) {
			text4 = (HtmlOutputText) findComponentInRoot("text4");
		}
		return text4;
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
	protected EditMobileProfileBean getEditMobileProfileBean() {
		if (editMobileProfileBean == null) {
			editMobileProfileBean = (EditMobileProfileBean) getManagedBean("editMobileProfileBean");
		}
		return editMobileProfileBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setEditMobileProfileBean(
			EditMobileProfileBean editMobileProfileBean) {
		this.editMobileProfileBean = editMobileProfileBean;
	}

	protected UISelectItems getSelectItems1() {
		if (selectItems1 == null) {
			selectItems1 = (UISelectItems) findComponentInRoot("selectItems1");
		}
		return selectItems1;
	}

	protected HtmlCommandLink getLink1() {
		if (link1 == null) {
			link1 = (HtmlCommandLink) findComponentInRoot("link1");
		}
		return link1;
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

	protected HtmlPanelGrid getGrid2() {
		if (grid2 == null) {
			grid2 = (HtmlPanelGrid) findComponentInRoot("grid2");
		}
		return grid2;
	}

	protected HtmlPanelGrid getGrid3() {
		if (grid3 == null) {
			grid3 = (HtmlPanelGrid) findComponentInRoot("grid3");
		}
		return grid3;
	}

}