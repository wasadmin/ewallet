/**
 * 
 */
package pagecode;

import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import javax.faces.component.UISelectItems;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlForm;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;

/**
 * @author stanford
 *
 */
public class CreateBankAccount extends PageCodeBase {

	protected HtmlPanelGrid grid1;
	protected HtmlOutputLabel label2;
	protected UISelectItems selectItems3;
	protected UISelectItems selectItems1;
	protected UISelectItems selectItems2;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlCommandButton button2;
	protected HtmlForm form1;
	protected HtmlSelectOneMenu menu1;
	protected HtmlOutputLabel label4;
	protected HtmlSelectOneMenu menu3;
	protected HtmlOutputLabel label3;
	protected HtmlSelectOneMenu menu2;
	protected HtmlPanelGrid grid2;
	protected HtmlCommandButton button1;

	protected HtmlPanelGrid getGrid1() {
		if (grid1 == null) {
			grid1 = (HtmlPanelGrid) findComponentInRoot("grid1");
		}
		return grid1;
	}

	protected HtmlOutputLabel getLabel2() {
		if (label2 == null) {
			label2 = (HtmlOutputLabel) findComponentInRoot("label2");
		}
		return label2;
	}

	protected UISelectItems getSelectItems3() {
		if (selectItems3 == null) {
			selectItems3 = (UISelectItems) findComponentInRoot("selectItems3");
		}
		return selectItems3;
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

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlSelectOneMenu getMenu3() {
		if (menu3 == null) {
			menu3 = (HtmlSelectOneMenu) findComponentInRoot("menu3");
		}
		return menu3;
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

}