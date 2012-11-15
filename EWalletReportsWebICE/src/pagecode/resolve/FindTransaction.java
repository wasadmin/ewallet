/**
 * 
 */
package pagecode.resolve;

import pagecode.PageCodeBase;
import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlInputText;
import com.ibm.faces.component.UIColumnEx;
import javax.faces.component.html.HtmlOutputText;
import com.ibm.faces.component.html.HtmlDataTableEx;
import com.ibm.faces.component.html.HtmlRequestLink;
import javax.faces.component.UISelectItems;
import zw.co.esolutions.ewallet.resolve.FindTransactionBean;
import javax.faces.component.html.HtmlCommandButton;
import com.ibm.faces.component.html.HtmlPanelBox;

/**
 * @author stanford
 *
 */
public class FindTransaction extends PageCodeBase {

	protected HtmlScriptCollector scriptCollector1;
	protected HtmlPanelGrid grid1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label2;
	protected HtmlForm form1;
	protected HtmlSelectOneMenu menu1;
	protected HtmlOutputLabel label3;
	protected HtmlInputText text1;
	protected HtmlOutputLabel label4;
	protected HtmlSelectOneMenu menu2;
	protected UIColumnEx columnEx1;
	protected HtmlOutputText text2;
	protected HtmlDataTableEx tableEx1;
	protected HtmlOutputText text3;
	protected UIColumnEx columnEx2;
	protected HtmlOutputText text4;
	protected UIColumnEx columnEx3;
	protected HtmlOutputLabel label5;
	protected HtmlOutputText text5;
	protected HtmlOutputText text6;
	protected HtmlOutputText text7;
	protected HtmlRequestLink link1;
	protected UISelectItems selectItems1;
	protected UISelectItems selectItems2;
	protected FindTransactionBean findTransactionBean;
	protected HtmlCommandButton button1;
	protected HtmlCommandButton button2;
	protected HtmlPanelBox box1;

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

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlSelectOneMenu getMenu2() {
		if (menu2 == null) {
			menu2 = (HtmlSelectOneMenu) findComponentInRoot("menu2");
		}
		return menu2;
	}

	protected UIColumnEx getColumnEx1() {
		if (columnEx1 == null) {
			columnEx1 = (UIColumnEx) findComponentInRoot("columnEx1");
		}
		return columnEx1;
	}

	protected HtmlOutputText getText2() {
		if (text2 == null) {
			text2 = (HtmlOutputText) findComponentInRoot("text2");
		}
		return text2;
	}

	protected HtmlDataTableEx getTableEx1() {
		if (tableEx1 == null) {
			tableEx1 = (HtmlDataTableEx) findComponentInRoot("tableEx1");
		}
		return tableEx1;
	}

	protected HtmlOutputText getText3() {
		if (text3 == null) {
			text3 = (HtmlOutputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected UIColumnEx getColumnEx2() {
		if (columnEx2 == null) {
			columnEx2 = (UIColumnEx) findComponentInRoot("columnEx2");
		}
		return columnEx2;
	}

	protected HtmlOutputText getText4() {
		if (text4 == null) {
			text4 = (HtmlOutputText) findComponentInRoot("text4");
		}
		return text4;
	}

	protected UIColumnEx getColumnEx3() {
		if (columnEx3 == null) {
			columnEx3 = (UIColumnEx) findComponentInRoot("columnEx3");
		}
		return columnEx3;
	}

	protected HtmlOutputLabel getLabel5() {
		if (label5 == null) {
			label5 = (HtmlOutputLabel) findComponentInRoot("label5");
		}
		return label5;
	}

	protected HtmlOutputText getText5() {
		if (text5 == null) {
			text5 = (HtmlOutputText) findComponentInRoot("text5");
		}
		return text5;
	}

	protected HtmlOutputText getText6() {
		if (text6 == null) {
			text6 = (HtmlOutputText) findComponentInRoot("text6");
		}
		return text6;
	}

	protected HtmlOutputText getText7() {
		if (text7 == null) {
			text7 = (HtmlOutputText) findComponentInRoot("text7");
		}
		return text7;
	}

	protected HtmlRequestLink getLink1() {
		if (link1 == null) {
			link1 = (HtmlRequestLink) findComponentInRoot("link1");
		}
		return link1;
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

	/** 
	 * @managed-bean true
	 */
	protected FindTransactionBean getFindTransactionBean() {
		if (findTransactionBean == null) {
			findTransactionBean = (FindTransactionBean) getManagedBean("findTransactionBean");
		}
		return findTransactionBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setFindTransactionBean(
			FindTransactionBean findTransactionBean) {
		this.findTransactionBean = findTransactionBean;
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

	protected HtmlPanelBox getBox1() {
		if (box1 == null) {
			box1 = (HtmlPanelBox) findComponentInRoot("box1");
		}
		return box1;
	}

}