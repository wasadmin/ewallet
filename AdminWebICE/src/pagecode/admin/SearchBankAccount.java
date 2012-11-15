/**
 * 
 */
package pagecode.admin;

import com.icesoft.faces.component.ext.HtmlMessages;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.UIColumn;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlForm;
import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import javax.faces.component.UISelectItems;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import pagecode.PageCodeBase;

/**
 * @author stanford
 *
 */
public class SearchBankAccount extends PageCodeBase {

	protected HtmlMessages messages1;
	protected HtmlSelectOneMenu menu1;
	protected HtmlSelectOneMenu menu2;
	protected HtmlCommandButton button1;
	protected HtmlOutputText text41;
	protected HtmlOutputText text5;
	protected HtmlOutputText text8;
	protected HtmlOutputText text6;
	protected HtmlCommandLink link1;
	protected UIColumn columnEx1;
	protected HtmlDataTable tableEx1;
	protected HtmlForm form1;
	protected HtmlPanelGrid grid1;
	protected HtmlOutputLabel label2;
	protected UISelectItems selectItems2;
	protected HtmlOutputLabel label3;
	protected UISelectItems selectItems1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label4;
	protected HtmlOutputText text1;
	protected UIColumn columnEx11;
	protected HtmlOutputText text11;
	protected UIColumn columnEx2;
	protected HtmlOutputText text2;
	protected UIColumn columnEx4;
	protected HtmlOutputText text7;
	protected UIColumn columnEx3;
	protected HtmlOutputText text3;
	protected HtmlOutputText text4;

	protected HtmlMessages getMessages1() {
		if (messages1 == null) {
			messages1 = (HtmlMessages) findComponentInRoot("messages1");
		}
		return messages1;
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

	protected HtmlCommandButton getButton1() {
		if (button1 == null) {
			button1 = (HtmlCommandButton) findComponentInRoot("button1");
		}
		return button1;
	}

	protected HtmlOutputText getText41() {
		if (text41 == null) {
			text41 = (HtmlOutputText) findComponentInRoot("text41");
		}
		return text41;
	}

	protected HtmlOutputText getText5() {
		if (text5 == null) {
			text5 = (HtmlOutputText) findComponentInRoot("text5");
		}
		return text5;
	}

	protected HtmlOutputText getText8() {
		if (text8 == null) {
			text8 = (HtmlOutputText) findComponentInRoot("text8");
		}
		return text8;
	}

	protected HtmlOutputText getText6() {
		if (text6 == null) {
			text6 = (HtmlOutputText) findComponentInRoot("text6");
		}
		return text6;
	}

	protected HtmlCommandLink getLink1() {
		if (link1 == null) {
			link1 = (HtmlCommandLink) findComponentInRoot("link1");
		}
		return link1;
	}

	protected UIColumn getColumnEx1() {
		if (columnEx1 == null) {
			columnEx1 = (UIColumn) findComponentInRoot("columnEx1");
		}
		return columnEx1;
	}

	protected HtmlDataTable getTableEx1() {
		if (tableEx1 == null) {
			tableEx1 = (HtmlDataTable) findComponentInRoot("tableEx1");
		}
		return tableEx1;
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

	protected HtmlOutputLabel getLabel2() {
		if (label2 == null) {
			label2 = (HtmlOutputLabel) findComponentInRoot("label2");
		}
		return label2;
	}

	protected UISelectItems getSelectItems2() {
		if (selectItems2 == null) {
			selectItems2 = (UISelectItems) findComponentInRoot("selectItems2");
		}
		return selectItems2;
	}

	protected HtmlOutputLabel getLabel3() {
		if (label3 == null) {
			label3 = (HtmlOutputLabel) findComponentInRoot("label3");
		}
		return label3;
	}

	protected UISelectItems getSelectItems1() {
		if (selectItems1 == null) {
			selectItems1 = (UISelectItems) findComponentInRoot("selectItems1");
		}
		return selectItems1;
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

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlOutputText getText1() {
		if (text1 == null) {
			text1 = (HtmlOutputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected UIColumn getColumnEx11() {
		if (columnEx11 == null) {
			columnEx11 = (UIColumn) findComponentInRoot("columnEx11");
		}
		return columnEx11;
	}

	protected HtmlOutputText getText11() {
		if (text11 == null) {
			text11 = (HtmlOutputText) findComponentInRoot("text11");
		}
		return text11;
	}

	protected UIColumn getColumnEx2() {
		if (columnEx2 == null) {
			columnEx2 = (UIColumn) findComponentInRoot("columnEx2");
		}
		return columnEx2;
	}

	protected HtmlOutputText getText2() {
		if (text2 == null) {
			text2 = (HtmlOutputText) findComponentInRoot("text2");
		}
		return text2;
	}

	protected UIColumn getColumnEx4() {
		if (columnEx4 == null) {
			columnEx4 = (UIColumn) findComponentInRoot("columnEx4");
		}
		return columnEx4;
	}

	protected HtmlOutputText getText7() {
		if (text7 == null) {
			text7 = (HtmlOutputText) findComponentInRoot("text7");
		}
		return text7;
	}

	protected UIColumn getColumnEx3() {
		if (columnEx3 == null) {
			columnEx3 = (UIColumn) findComponentInRoot("columnEx3");
		}
		return columnEx3;
	}

	protected HtmlOutputText getText3() {
		if (text3 == null) {
			text3 = (HtmlOutputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected HtmlOutputText getText4() {
		if (text4 == null) {
			text4 = (HtmlOutputText) findComponentInRoot("text4");
		}
		return text4;
	}

}