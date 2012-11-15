/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlForm;
import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlCommandButton;

/**
 * @author stanford
 *
 */
public class RegisterCustomer5 extends PageCodeBase {

	protected HtmlOutputLabel label4;
	protected HtmlPanelGrid grid3;
	protected HtmlForm form1;
	protected HtmlScriptCollector scriptCollector1;
	protected HtmlMessages messages1;
	protected HtmlOutputText text1;
	protected HtmlCommandLink link1;
	protected HtmlOutputText text20;
	protected HtmlOutputLabel label6;
	protected HtmlSelectOneMenu menu8;
	protected UISelectItems selectItems1;
	protected HtmlOutputLabel label1;
	protected HtmlInputText text2;
	protected HtmlOutputLabel label2;
	protected HtmlInputText text3;
	protected HtmlOutputLabel label3;
	protected HtmlInputText text4;
	protected HtmlOutputLabel label5;
	protected HtmlOutputText text6;
	protected HtmlOutputLabel label9;
	protected HtmlSelectOneMenu menu1;
	protected UISelectItems selectItems4;
	protected HtmlPanelGroup group4;
	protected HtmlOutputLabel label13;
	protected HtmlPanelGrid grid4;
	protected HtmlOutputLabel label15;
	protected HtmlSelectOneMenu menu4;
	protected UISelectItems selectItems2;
	protected HtmlOutputLabel label16;
	protected HtmlInputText text7;
	protected HtmlOutputLabel label17;
	protected HtmlInputText text8;
	protected HtmlOutputLabel label18;
	protected HtmlInputText text5;
	protected HtmlOutputLabel label19;
	protected HtmlInputText text9;
	protected HtmlOutputLabel label20;
	protected HtmlSelectOneMenu menu5;
	protected UISelectItems selectItems5;
	protected HtmlPanelGroup group5;
	protected HtmlOutputLabel label14;
	protected HtmlPanelGrid grid5;
	protected HtmlOutputLabel label22;
	protected HtmlSelectOneMenu menu6;
	protected UISelectItems selectItems3;
	protected HtmlOutputLabel label23;
	protected HtmlInputText text10;
	protected HtmlOutputLabel label24;
	protected HtmlInputText text11;
	protected HtmlOutputLabel label25;
	protected HtmlInputText text12;
	protected HtmlOutputLabel label26;
	protected HtmlInputText text13;
	protected HtmlOutputLabel label27;
	protected HtmlSelectOneMenu menu9;
	protected UISelectItems selectItems6;
	protected HtmlPanelGroup group6;
	protected HtmlOutputLabel label21;
	protected HtmlPanelGrid grid9;
	protected HtmlCommandButton button2;
	protected HtmlCommandButton button1;
	protected HtmlSelectOneMenu menu2;
	protected HtmlOutputLabel label7;
	protected HtmlSelectOneMenu menu3;
	protected HtmlOutputLabel label8;
	protected UISelectItems selectItems7;
	protected UISelectItems selectItems8;
	protected UISelectItems selectItems9;
	protected HtmlSelectOneMenu menu7;

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlPanelGrid getGrid3() {
		if (grid3 == null) {
			grid3 = (HtmlPanelGrid) findComponentInRoot("grid3");
		}
		return grid3;
	}

	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}

	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 = (HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}

	protected HtmlMessages getMessages1() {
		if (messages1 == null) {
			messages1 = (HtmlMessages) findComponentInRoot("messages1");
		}
		return messages1;
	}

	protected HtmlOutputText getText1() {
		if (text1 == null) {
			text1 = (HtmlOutputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlCommandLink getLink1() {
		if (link1 == null) {
			link1 = (HtmlCommandLink) findComponentInRoot("link1");
		}
		return link1;
	}

	protected HtmlOutputText getText20() {
		if (text20 == null) {
			text20 = (HtmlOutputText) findComponentInRoot("text20");
		}
		return text20;
	}

	protected HtmlOutputLabel getLabel6() {
		if (label6 == null) {
			label6 = (HtmlOutputLabel) findComponentInRoot("label6");
		}
		return label6;
	}

	protected HtmlSelectOneMenu getMenu8() {
		if (menu8 == null) {
			menu8 = (HtmlSelectOneMenu) findComponentInRoot("menu8");
		}
		return menu8;
	}

	protected UISelectItems getSelectItems1() {
		if (selectItems1 == null) {
			selectItems1 = (UISelectItems) findComponentInRoot("selectItems1");
		}
		return selectItems1;
	}

	protected HtmlOutputLabel getLabel1() {
		if (label1 == null) {
			label1 = (HtmlOutputLabel) findComponentInRoot("label1");
		}
		return label1;
	}

	protected HtmlInputText getText2() {
		if (text2 == null) {
			text2 = (HtmlInputText) findComponentInRoot("text2");
		}
		return text2;
	}

	protected HtmlOutputLabel getLabel2() {
		if (label2 == null) {
			label2 = (HtmlOutputLabel) findComponentInRoot("label2");
		}
		return label2;
	}

	protected HtmlInputText getText3() {
		if (text3 == null) {
			text3 = (HtmlInputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected HtmlOutputLabel getLabel3() {
		if (label3 == null) {
			label3 = (HtmlOutputLabel) findComponentInRoot("label3");
		}
		return label3;
	}

	protected HtmlInputText getText4() {
		if (text4 == null) {
			text4 = (HtmlInputText) findComponentInRoot("text4");
		}
		return text4;
	}

	protected HtmlOutputLabel getLabel5() {
		if (label5 == null) {
			label5 = (HtmlOutputLabel) findComponentInRoot("label5");
		}
		return label5;
	}

	protected HtmlOutputText getText6() {
		if (text6 == null) {
			text6 = (HtmlOutputText) findComponentInRoot("text6");
		}
		return text6;
	}

	protected HtmlOutputLabel getLabel9() {
		if (label9 == null) {
			label9 = (HtmlOutputLabel) findComponentInRoot("label9");
		}
		return label9;
	}

	protected HtmlSelectOneMenu getMenu1() {
		if (menu1 == null) {
			menu1 = (HtmlSelectOneMenu) findComponentInRoot("menu1");
		}
		return menu1;
	}

	protected UISelectItems getSelectItems4() {
		if (selectItems4 == null) {
			selectItems4 = (UISelectItems) findComponentInRoot("selectItems4");
		}
		return selectItems4;
	}

	protected HtmlPanelGroup getGroup4() {
		if (group4 == null) {
			group4 = (HtmlPanelGroup) findComponentInRoot("group4");
		}
		return group4;
	}

	protected HtmlOutputLabel getLabel13() {
		if (label13 == null) {
			label13 = (HtmlOutputLabel) findComponentInRoot("label13");
		}
		return label13;
	}

	protected HtmlPanelGrid getGrid4() {
		if (grid4 == null) {
			grid4 = (HtmlPanelGrid) findComponentInRoot("grid4");
		}
		return grid4;
	}

	protected HtmlOutputLabel getLabel15() {
		if (label15 == null) {
			label15 = (HtmlOutputLabel) findComponentInRoot("label15");
		}
		return label15;
	}

	protected HtmlSelectOneMenu getMenu4() {
		if (menu4 == null) {
			menu4 = (HtmlSelectOneMenu) findComponentInRoot("menu4");
		}
		return menu4;
	}

	protected UISelectItems getSelectItems2() {
		if (selectItems2 == null) {
			selectItems2 = (UISelectItems) findComponentInRoot("selectItems2");
		}
		return selectItems2;
	}

	protected HtmlOutputLabel getLabel16() {
		if (label16 == null) {
			label16 = (HtmlOutputLabel) findComponentInRoot("label16");
		}
		return label16;
	}

	protected HtmlInputText getText7() {
		if (text7 == null) {
			text7 = (HtmlInputText) findComponentInRoot("text7");
		}
		return text7;
	}

	protected HtmlOutputLabel getLabel17() {
		if (label17 == null) {
			label17 = (HtmlOutputLabel) findComponentInRoot("label17");
		}
		return label17;
	}

	protected HtmlInputText getText8() {
		if (text8 == null) {
			text8 = (HtmlInputText) findComponentInRoot("text8");
		}
		return text8;
	}

	protected HtmlOutputLabel getLabel18() {
		if (label18 == null) {
			label18 = (HtmlOutputLabel) findComponentInRoot("label18");
		}
		return label18;
	}

	protected HtmlInputText getText5() {
		if (text5 == null) {
			text5 = (HtmlInputText) findComponentInRoot("text5");
		}
		return text5;
	}

	protected HtmlOutputLabel getLabel19() {
		if (label19 == null) {
			label19 = (HtmlOutputLabel) findComponentInRoot("label19");
		}
		return label19;
	}

	protected HtmlInputText getText9() {
		if (text9 == null) {
			text9 = (HtmlInputText) findComponentInRoot("text9");
		}
		return text9;
	}

	protected HtmlOutputLabel getLabel20() {
		if (label20 == null) {
			label20 = (HtmlOutputLabel) findComponentInRoot("label20");
		}
		return label20;
	}

	protected HtmlSelectOneMenu getMenu5() {
		if (menu5 == null) {
			menu5 = (HtmlSelectOneMenu) findComponentInRoot("menu5");
		}
		return menu5;
	}

	protected UISelectItems getSelectItems5() {
		if (selectItems5 == null) {
			selectItems5 = (UISelectItems) findComponentInRoot("selectItems5");
		}
		return selectItems5;
	}

	protected HtmlPanelGroup getGroup5() {
		if (group5 == null) {
			group5 = (HtmlPanelGroup) findComponentInRoot("group5");
		}
		return group5;
	}

	protected HtmlOutputLabel getLabel14() {
		if (label14 == null) {
			label14 = (HtmlOutputLabel) findComponentInRoot("label14");
		}
		return label14;
	}

	protected HtmlPanelGrid getGrid5() {
		if (grid5 == null) {
			grid5 = (HtmlPanelGrid) findComponentInRoot("grid5");
		}
		return grid5;
	}

	protected HtmlOutputLabel getLabel22() {
		if (label22 == null) {
			label22 = (HtmlOutputLabel) findComponentInRoot("label22");
		}
		return label22;
	}

	protected HtmlSelectOneMenu getMenu6() {
		if (menu6 == null) {
			menu6 = (HtmlSelectOneMenu) findComponentInRoot("menu6");
		}
		return menu6;
	}

	protected UISelectItems getSelectItems3() {
		if (selectItems3 == null) {
			selectItems3 = (UISelectItems) findComponentInRoot("selectItems3");
		}
		return selectItems3;
	}

	protected HtmlOutputLabel getLabel23() {
		if (label23 == null) {
			label23 = (HtmlOutputLabel) findComponentInRoot("label23");
		}
		return label23;
	}

	protected HtmlInputText getText10() {
		if (text10 == null) {
			text10 = (HtmlInputText) findComponentInRoot("text10");
		}
		return text10;
	}

	protected HtmlOutputLabel getLabel24() {
		if (label24 == null) {
			label24 = (HtmlOutputLabel) findComponentInRoot("label24");
		}
		return label24;
	}

	protected HtmlInputText getText11() {
		if (text11 == null) {
			text11 = (HtmlInputText) findComponentInRoot("text11");
		}
		return text11;
	}

	protected HtmlOutputLabel getLabel25() {
		if (label25 == null) {
			label25 = (HtmlOutputLabel) findComponentInRoot("label25");
		}
		return label25;
	}

	protected HtmlInputText getText12() {
		if (text12 == null) {
			text12 = (HtmlInputText) findComponentInRoot("text12");
		}
		return text12;
	}

	protected HtmlOutputLabel getLabel26() {
		if (label26 == null) {
			label26 = (HtmlOutputLabel) findComponentInRoot("label26");
		}
		return label26;
	}

	protected HtmlInputText getText13() {
		if (text13 == null) {
			text13 = (HtmlInputText) findComponentInRoot("text13");
		}
		return text13;
	}

	protected HtmlOutputLabel getLabel27() {
		if (label27 == null) {
			label27 = (HtmlOutputLabel) findComponentInRoot("label27");
		}
		return label27;
	}

	protected HtmlSelectOneMenu getMenu9() {
		if (menu9 == null) {
			menu9 = (HtmlSelectOneMenu) findComponentInRoot("menu9");
		}
		return menu9;
	}

	protected UISelectItems getSelectItems6() {
		if (selectItems6 == null) {
			selectItems6 = (UISelectItems) findComponentInRoot("selectItems6");
		}
		return selectItems6;
	}

	protected HtmlPanelGroup getGroup6() {
		if (group6 == null) {
			group6 = (HtmlPanelGroup) findComponentInRoot("group6");
		}
		return group6;
	}

	protected HtmlOutputLabel getLabel21() {
		if (label21 == null) {
			label21 = (HtmlOutputLabel) findComponentInRoot("label21");
		}
		return label21;
	}

	protected HtmlPanelGrid getGrid9() {
		if (grid9 == null) {
			grid9 = (HtmlPanelGrid) findComponentInRoot("grid9");
		}
		return grid9;
	}

	protected HtmlCommandButton getButton2() {
		if (button2 == null) {
			button2 = (HtmlCommandButton) findComponentInRoot("button2");
		}
		return button2;
	}

	protected HtmlCommandButton getButton1() {
		if (button1 == null) {
			button1 = (HtmlCommandButton) findComponentInRoot("button1");
		}
		return button1;
	}

	protected HtmlSelectOneMenu getMenu2() {
		if (menu2 == null) {
			menu2 = (HtmlSelectOneMenu) findComponentInRoot("menu2");
		}
		return menu2;
	}

	protected HtmlOutputLabel getLabel7() {
		if (label7 == null) {
			label7 = (HtmlOutputLabel) findComponentInRoot("label7");
		}
		return label7;
	}

	protected HtmlSelectOneMenu getMenu3() {
		if (menu3 == null) {
			menu3 = (HtmlSelectOneMenu) findComponentInRoot("menu3");
		}
		return menu3;
	}

	protected HtmlOutputLabel getLabel8() {
		if (label8 == null) {
			label8 = (HtmlOutputLabel) findComponentInRoot("label8");
		}
		return label8;
	}

	protected UISelectItems getSelectItems7() {
		if (selectItems7 == null) {
			selectItems7 = (UISelectItems) findComponentInRoot("selectItems7");
		}
		return selectItems7;
	}

	protected UISelectItems getSelectItems8() {
		if (selectItems8 == null) {
			selectItems8 = (UISelectItems) findComponentInRoot("selectItems8");
		}
		return selectItems8;
	}

	protected UISelectItems getSelectItems9() {
		if (selectItems9 == null) {
			selectItems9 = (UISelectItems) findComponentInRoot("selectItems9");
		}
		return selectItems9;
	}

	protected HtmlSelectOneMenu getMenu7() {
		if (menu7 == null) {
			menu7 = (HtmlSelectOneMenu) findComponentInRoot("menu7");
		}
		return menu7;
	}

}