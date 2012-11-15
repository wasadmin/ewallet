/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlCommandButton;

/**
 * @author stanford
 *
 */
public class RegisterCustomer extends PageCodeBase {

	protected HtmlScriptCollector scriptCollector1;
	protected HtmlOutputLabel label7;
	protected HtmlPanelGrid grid52;
	protected HtmlForm form1;
	protected HtmlMessages messages1;
	protected HtmlPanelGrid grid1;
	protected HtmlOutputLabel label2;
	protected HtmlSelectOneMenu menu1;
	protected UISelectItems selectItems7;
	protected HtmlOutputLabel label3;
	protected HtmlInputText text1;
	protected HtmlOutputLabel label411;
	protected HtmlInputText text202;
	protected HtmlOutputLabel label5;
	protected HtmlInputText text3;
	protected HtmlOutputLabel label6;
	protected HtmlInputText text4;
	protected HtmlOutputLabel label796;
	protected HtmlSelectOneMenu menu286;
	protected UISelectItems selectItems5;
	protected HtmlOutputLabel label866;
	protected HtmlSelectOneMenu menu375;
	protected UISelectItems selectItems6;
	protected HtmlOutputLabel label20;
	protected HtmlSelectOneMenu menu744;
	protected UISelectItems selectItems4;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlPanelGrid grid11;
	protected HtmlOutputLabel label16;
	protected HtmlInputText text12;
	protected HtmlOutputLabel label73;
	protected HtmlInputText text6;
	protected HtmlOutputLabel label9;
	protected HtmlInputText text8;
	protected HtmlOutputLabel label106;
	protected HtmlInputText text9;
	protected HtmlOutputLabel label115;
	protected HtmlInputText text10;
	protected HtmlOutputLabel label129;
	protected HtmlInputText text11;
	protected HtmlPanelGroup group12;
	protected HtmlOutputLabel label13;
	protected HtmlOutputLabel label10;
	protected HtmlOutputLabel label12;
	protected HtmlOutputLabel label11;
	protected HtmlInputText text14;
	protected HtmlSelectOneMenu menu2;
	protected HtmlOutputText text15;
	protected HtmlInputText text16;
	protected HtmlSelectOneMenu menu3;
	protected UISelectItems selectItems8;
	protected HtmlOutputText text17;
	protected HtmlInputText text18;
	protected HtmlSelectOneMenu menu7;
	protected UISelectItems selectItems9;
	protected HtmlOutputText text19;
	protected HtmlPanelGroup group2;
	protected HtmlOutputLabel label8;
	protected HtmlPanelGrid grid91;
	protected HtmlPanelGroup group3;
	protected HtmlOutputLabel label4;
	protected HtmlOutputLabel label143;
	protected HtmlInputText text59;
	protected HtmlPanelGrid grid43;
	protected HtmlCommandButton button2;
	protected HtmlCommandButton button1;
	protected HtmlOutputLabel label14;
	protected HtmlOutputLabel label15;
	protected HtmlOutputLabel label17;

	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 = (HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}

	protected HtmlOutputLabel getLabel7() {
		if (label7 == null) {
			label7 = (HtmlOutputLabel) findComponentInRoot("label7");
		}
		return label7;
	}

	protected HtmlPanelGrid getGrid52() {
		if (grid52 == null) {
			grid52 = (HtmlPanelGrid) findComponentInRoot("grid52");
		}
		return grid52;
	}

	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}

	protected HtmlMessages getMessages1() {
		if (messages1 == null) {
			messages1 = (HtmlMessages) findComponentInRoot("messages1");
		}
		return messages1;
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

	protected HtmlSelectOneMenu getMenu1() {
		if (menu1 == null) {
			menu1 = (HtmlSelectOneMenu) findComponentInRoot("menu1");
		}
		return menu1;
	}

	protected UISelectItems getSelectItems7() {
		if (selectItems7 == null) {
			selectItems7 = (UISelectItems) findComponentInRoot("selectItems7");
		}
		return selectItems7;
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

	protected HtmlOutputLabel getLabel411() {
		if (label411 == null) {
			label411 = (HtmlOutputLabel) findComponentInRoot("label411");
		}
		return label411;
	}

	protected HtmlInputText getText202() {
		if (text202 == null) {
			text202 = (HtmlInputText) findComponentInRoot("text202");
		}
		return text202;
	}

	protected HtmlOutputLabel getLabel5() {
		if (label5 == null) {
			label5 = (HtmlOutputLabel) findComponentInRoot("label5");
		}
		return label5;
	}

	protected HtmlInputText getText3() {
		if (text3 == null) {
			text3 = (HtmlInputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected HtmlOutputLabel getLabel6() {
		if (label6 == null) {
			label6 = (HtmlOutputLabel) findComponentInRoot("label6");
		}
		return label6;
	}

	protected HtmlInputText getText4() {
		if (text4 == null) {
			text4 = (HtmlInputText) findComponentInRoot("text4");
		}
		return text4;
	}

	protected HtmlOutputLabel getLabel796() {
		if (label796 == null) {
			label796 = (HtmlOutputLabel) findComponentInRoot("label796");
		}
		return label796;
	}

	protected HtmlSelectOneMenu getMenu286() {
		if (menu286 == null) {
			menu286 = (HtmlSelectOneMenu) findComponentInRoot("menu286");
		}
		return menu286;
	}

	protected UISelectItems getSelectItems5() {
		if (selectItems5 == null) {
			selectItems5 = (UISelectItems) findComponentInRoot("selectItems5");
		}
		return selectItems5;
	}

	protected HtmlOutputLabel getLabel866() {
		if (label866 == null) {
			label866 = (HtmlOutputLabel) findComponentInRoot("label866");
		}
		return label866;
	}

	protected HtmlSelectOneMenu getMenu375() {
		if (menu375 == null) {
			menu375 = (HtmlSelectOneMenu) findComponentInRoot("menu375");
		}
		return menu375;
	}

	protected UISelectItems getSelectItems6() {
		if (selectItems6 == null) {
			selectItems6 = (UISelectItems) findComponentInRoot("selectItems6");
		}
		return selectItems6;
	}

	protected HtmlOutputLabel getLabel20() {
		if (label20 == null) {
			label20 = (HtmlOutputLabel) findComponentInRoot("label20");
		}
		return label20;
	}

	protected HtmlSelectOneMenu getMenu744() {
		if (menu744 == null) {
			menu744 = (HtmlSelectOneMenu) findComponentInRoot("menu744");
		}
		return menu744;
	}

	protected UISelectItems getSelectItems4() {
		if (selectItems4 == null) {
			selectItems4 = (UISelectItems) findComponentInRoot("selectItems4");
		}
		return selectItems4;
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

	protected HtmlPanelGrid getGrid11() {
		if (grid11 == null) {
			grid11 = (HtmlPanelGrid) findComponentInRoot("grid11");
		}
		return grid11;
	}

	protected HtmlOutputLabel getLabel16() {
		if (label16 == null) {
			label16 = (HtmlOutputLabel) findComponentInRoot("label16");
		}
		return label16;
	}

	protected HtmlInputText getText12() {
		if (text12 == null) {
			text12 = (HtmlInputText) findComponentInRoot("text12");
		}
		return text12;
	}

	protected HtmlOutputLabel getLabel73() {
		if (label73 == null) {
			label73 = (HtmlOutputLabel) findComponentInRoot("label73");
		}
		return label73;
	}

	protected HtmlInputText getText6() {
		if (text6 == null) {
			text6 = (HtmlInputText) findComponentInRoot("text6");
		}
		return text6;
	}

	protected HtmlOutputLabel getLabel9() {
		if (label9 == null) {
			label9 = (HtmlOutputLabel) findComponentInRoot("label9");
		}
		return label9;
	}

	protected HtmlInputText getText8() {
		if (text8 == null) {
			text8 = (HtmlInputText) findComponentInRoot("text8");
		}
		return text8;
	}

	protected HtmlOutputLabel getLabel106() {
		if (label106 == null) {
			label106 = (HtmlOutputLabel) findComponentInRoot("label106");
		}
		return label106;
	}

	protected HtmlInputText getText9() {
		if (text9 == null) {
			text9 = (HtmlInputText) findComponentInRoot("text9");
		}
		return text9;
	}

	protected HtmlOutputLabel getLabel115() {
		if (label115 == null) {
			label115 = (HtmlOutputLabel) findComponentInRoot("label115");
		}
		return label115;
	}

	protected HtmlInputText getText10() {
		if (text10 == null) {
			text10 = (HtmlInputText) findComponentInRoot("text10");
		}
		return text10;
	}

	protected HtmlOutputLabel getLabel129() {
		if (label129 == null) {
			label129 = (HtmlOutputLabel) findComponentInRoot("label129");
		}
		return label129;
	}

	protected HtmlInputText getText11() {
		if (text11 == null) {
			text11 = (HtmlInputText) findComponentInRoot("text11");
		}
		return text11;
	}

	protected HtmlPanelGroup getGroup12() {
		if (group12 == null) {
			group12 = (HtmlPanelGroup) findComponentInRoot("group12");
		}
		return group12;
	}

	protected HtmlOutputLabel getLabel13() {
		if (label13 == null) {
			label13 = (HtmlOutputLabel) findComponentInRoot("label13");
		}
		return label13;
	}

	protected HtmlOutputLabel getLabel10() {
		if (label10 == null) {
			label10 = (HtmlOutputLabel) findComponentInRoot("label10");
		}
		return label10;
	}

	protected HtmlOutputLabel getLabel12() {
		if (label12 == null) {
			label12 = (HtmlOutputLabel) findComponentInRoot("label12");
		}
		return label12;
	}

	protected HtmlOutputLabel getLabel11() {
		if (label11 == null) {
			label11 = (HtmlOutputLabel) findComponentInRoot("label11");
		}
		return label11;
	}

	protected HtmlInputText getText14() {
		if (text14 == null) {
			text14 = (HtmlInputText) findComponentInRoot("text14");
		}
		return text14;
	}

	protected HtmlSelectOneMenu getMenu2() {
		if (menu2 == null) {
			menu2 = (HtmlSelectOneMenu) findComponentInRoot("menu2");
		}
		return menu2;
	}

	protected HtmlOutputText getText15() {
		if (text15 == null) {
			text15 = (HtmlOutputText) findComponentInRoot("text15");
		}
		return text15;
	}

	protected HtmlInputText getText16() {
		if (text16 == null) {
			text16 = (HtmlInputText) findComponentInRoot("text16");
		}
		return text16;
	}

	protected HtmlSelectOneMenu getMenu3() {
		if (menu3 == null) {
			menu3 = (HtmlSelectOneMenu) findComponentInRoot("menu3");
		}
		return menu3;
	}

	protected UISelectItems getSelectItems8() {
		if (selectItems8 == null) {
			selectItems8 = (UISelectItems) findComponentInRoot("selectItems8");
		}
		return selectItems8;
	}

	protected HtmlOutputText getText17() {
		if (text17 == null) {
			text17 = (HtmlOutputText) findComponentInRoot("text17");
		}
		return text17;
	}

	protected HtmlInputText getText18() {
		if (text18 == null) {
			text18 = (HtmlInputText) findComponentInRoot("text18");
		}
		return text18;
	}

	protected HtmlSelectOneMenu getMenu7() {
		if (menu7 == null) {
			menu7 = (HtmlSelectOneMenu) findComponentInRoot("menu7");
		}
		return menu7;
	}

	protected UISelectItems getSelectItems9() {
		if (selectItems9 == null) {
			selectItems9 = (UISelectItems) findComponentInRoot("selectItems9");
		}
		return selectItems9;
	}

	protected HtmlOutputText getText19() {
		if (text19 == null) {
			text19 = (HtmlOutputText) findComponentInRoot("text19");
		}
		return text19;
	}

	protected HtmlPanelGroup getGroup2() {
		if (group2 == null) {
			group2 = (HtmlPanelGroup) findComponentInRoot("group2");
		}
		return group2;
	}

	protected HtmlOutputLabel getLabel8() {
		if (label8 == null) {
			label8 = (HtmlOutputLabel) findComponentInRoot("label8");
		}
		return label8;
	}

	protected HtmlPanelGrid getGrid91() {
		if (grid91 == null) {
			grid91 = (HtmlPanelGrid) findComponentInRoot("grid91");
		}
		return grid91;
	}

	protected HtmlPanelGroup getGroup3() {
		if (group3 == null) {
			group3 = (HtmlPanelGroup) findComponentInRoot("group3");
		}
		return group3;
	}

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlOutputLabel getLabel143() {
		if (label143 == null) {
			label143 = (HtmlOutputLabel) findComponentInRoot("label143");
		}
		return label143;
	}

	protected HtmlInputText getText59() {
		if (text59 == null) {
			text59 = (HtmlInputText) findComponentInRoot("text59");
		}
		return text59;
	}

	protected HtmlPanelGrid getGrid43() {
		if (grid43 == null) {
			grid43 = (HtmlPanelGrid) findComponentInRoot("grid43");
		}
		return grid43;
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

	protected HtmlOutputLabel getLabel14() {
		if (label14 == null) {
			label14 = (HtmlOutputLabel) findComponentInRoot("label14");
		}
		return label14;
	}

	protected HtmlOutputLabel getLabel15() {
		if (label15 == null) {
			label15 = (HtmlOutputLabel) findComponentInRoot("label15");
		}
		return label15;
	}

	protected HtmlOutputLabel getLabel17() {
		if (label17 == null) {
			label17 = (HtmlOutputLabel) findComponentInRoot("label17");
		}
		return label17;
	}

}