/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlCommandButton;
import com.ibm.faces.component.html.HtmlRequestLink;
import zw.co.esolutions.ewallet.csrweb.RegisterCustomer2Bean;
import javax.faces.component.UISelectItems;

/**
 * @author stanford
 *
 */
public class RegisterCustomer2 extends PageCodeBase {

	protected HtmlScriptCollector scriptCollector1;
	protected HtmlPanelGrid grid1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label2;
	protected HtmlOutputLabel label3;
	protected HtmlOutputLabel label5;
	protected HtmlOutputLabel label6;
	protected HtmlOutputLabel label7;
	protected HtmlOutputText text1;
	protected HtmlForm form1;
	protected HtmlInputText text2;
	protected HtmlInputText text3;
	protected HtmlInputText text4;
	protected HtmlSelectOneMenu menu1;
	protected HtmlInputText text5;
	protected HtmlInputText text7;
	protected HtmlInputText text8;
	protected HtmlInputText text9;
	protected HtmlSelectOneMenu menu4;
	protected HtmlInputText text10;
	protected HtmlInputText text11;
	protected HtmlInputText text12;
	protected HtmlInputText text13;
	protected HtmlSelectOneMenu menu5;
	protected HtmlSelectOneMenu menu6;
	protected HtmlPanelGrid grid2;
	protected HtmlPanelGroup group2;
	protected HtmlOutputLabel label8;
	protected HtmlOutputText text6;
	protected HtmlOutputLabel label9;
	protected HtmlSelectOneMenu menu8;
	protected HtmlSelectOneMenu menu9;
	protected HtmlOutputLabel label10;
	protected HtmlOutputLabel label11;
	protected HtmlInputText text14;
	protected HtmlOutputText text15;
	protected HtmlInputText text16;
	protected HtmlOutputText text17;
	protected HtmlInputText text18;
	protected HtmlOutputText text19;
	protected HtmlPanelGrid grid3;
	protected HtmlCommandButton button1;
	protected HtmlCommandButton button2;
	protected HtmlOutputText text20;
	protected HtmlRequestLink link1;
	protected HtmlOutputLabel label143;
	protected HtmlInputText text59;
	protected HtmlPanelGroup group3;
	protected HtmlOutputLabel label4;
	protected HtmlOutputLabel label12;
	protected HtmlSelectOneMenu menu2;
	protected HtmlSelectOneMenu menu3;
	protected HtmlSelectOneMenu menu7;
	protected RegisterCustomer2Bean registerCustomer2Bean;
	protected UISelectItems selectItems1;
	protected UISelectItems selectItems2;
	protected UISelectItems selectItems3;
	protected UISelectItems selectItems4;
	protected UISelectItems selectItems5;
	protected UISelectItems selectItems6;
	protected UISelectItems selectItems7;
	protected UISelectItems selectItems8;
	protected UISelectItems selectItems9;
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

	protected HtmlOutputLabel getLabel3() {
		if (label3 == null) {
			label3 = (HtmlOutputLabel) findComponentInRoot("label3");
		}
		return label3;
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

	protected HtmlOutputLabel getLabel7() {
		if (label7 == null) {
			label7 = (HtmlOutputLabel) findComponentInRoot("label7");
		}
		return label7;
	}

	protected HtmlOutputText getText1() {
		if (text1 == null) {
			text1 = (HtmlOutputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}

	protected HtmlInputText getText2() {
		if (text2 == null) {
			text2 = (HtmlInputText) findComponentInRoot("text2");
		}
		return text2;
	}

	protected HtmlInputText getText3() {
		if (text3 == null) {
			text3 = (HtmlInputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected HtmlInputText getText4() {
		if (text4 == null) {
			text4 = (HtmlInputText) findComponentInRoot("text4");
		}
		return text4;
	}

	protected HtmlSelectOneMenu getMenu1() {
		if (menu1 == null) {
			menu1 = (HtmlSelectOneMenu) findComponentInRoot("menu1");
		}
		return menu1;
	}

	protected HtmlInputText getText5() {
		if (text5 == null) {
			text5 = (HtmlInputText) findComponentInRoot("text5");
		}
		return text5;
	}

	protected HtmlInputText getText7() {
		if (text7 == null) {
			text7 = (HtmlInputText) findComponentInRoot("text7");
		}
		return text7;
	}

	protected HtmlInputText getText8() {
		if (text8 == null) {
			text8 = (HtmlInputText) findComponentInRoot("text8");
		}
		return text8;
	}

	protected HtmlInputText getText9() {
		if (text9 == null) {
			text9 = (HtmlInputText) findComponentInRoot("text9");
		}
		return text9;
	}

	protected HtmlSelectOneMenu getMenu4() {
		if (menu4 == null) {
			menu4 = (HtmlSelectOneMenu) findComponentInRoot("menu4");
		}
		return menu4;
	}

	protected HtmlInputText getText10() {
		if (text10 == null) {
			text10 = (HtmlInputText) findComponentInRoot("text10");
		}
		return text10;
	}

	protected HtmlInputText getText11() {
		if (text11 == null) {
			text11 = (HtmlInputText) findComponentInRoot("text11");
		}
		return text11;
	}

	protected HtmlInputText getText12() {
		if (text12 == null) {
			text12 = (HtmlInputText) findComponentInRoot("text12");
		}
		return text12;
	}

	protected HtmlInputText getText13() {
		if (text13 == null) {
			text13 = (HtmlInputText) findComponentInRoot("text13");
		}
		return text13;
	}

	protected HtmlSelectOneMenu getMenu5() {
		if (menu5 == null) {
			menu5 = (HtmlSelectOneMenu) findComponentInRoot("menu5");
		}
		return menu5;
	}

	protected HtmlSelectOneMenu getMenu6() {
		if (menu6 == null) {
			menu6 = (HtmlSelectOneMenu) findComponentInRoot("menu6");
		}
		return menu6;
	}

	protected HtmlPanelGrid getGrid2() {
		if (grid2 == null) {
			grid2 = (HtmlPanelGrid) findComponentInRoot("grid2");
		}
		return grid2;
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

	protected HtmlSelectOneMenu getMenu8() {
		if (menu8 == null) {
			menu8 = (HtmlSelectOneMenu) findComponentInRoot("menu8");
		}
		return menu8;
	}

	protected HtmlSelectOneMenu getMenu9() {
		if (menu9 == null) {
			menu9 = (HtmlSelectOneMenu) findComponentInRoot("menu9");
		}
		return menu9;
	}

	protected HtmlOutputLabel getLabel10() {
		if (label10 == null) {
			label10 = (HtmlOutputLabel) findComponentInRoot("label10");
		}
		return label10;
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

	protected HtmlOutputText getText19() {
		if (text19 == null) {
			text19 = (HtmlOutputText) findComponentInRoot("text19");
		}
		return text19;
	}

	protected HtmlPanelGrid getGrid3() {
		if (grid3 == null) {
			grid3 = (HtmlPanelGrid) findComponentInRoot("grid3");
		}
		return grid3;
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

	protected HtmlOutputText getText20() {
		if (text20 == null) {
			text20 = (HtmlOutputText) findComponentInRoot("text20");
		}
		return text20;
	}

	protected HtmlRequestLink getLink1() {
		if (link1 == null) {
			link1 = (HtmlRequestLink) findComponentInRoot("link1");
		}
		return link1;
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

	protected HtmlOutputLabel getLabel12() {
		if (label12 == null) {
			label12 = (HtmlOutputLabel) findComponentInRoot("label12");
		}
		return label12;
	}

	protected HtmlSelectOneMenu getMenu2() {
		if (menu2 == null) {
			menu2 = (HtmlSelectOneMenu) findComponentInRoot("menu2");
		}
		return menu2;
	}

	protected HtmlSelectOneMenu getMenu3() {
		if (menu3 == null) {
			menu3 = (HtmlSelectOneMenu) findComponentInRoot("menu3");
		}
		return menu3;
	}

	protected HtmlSelectOneMenu getMenu7() {
		if (menu7 == null) {
			menu7 = (HtmlSelectOneMenu) findComponentInRoot("menu7");
		}
		return menu7;
	}

	/** 
	 * @managed-bean true
	 */
	protected RegisterCustomer2Bean getRegisterCustomer2Bean() {
		if (registerCustomer2Bean == null) {
			registerCustomer2Bean = (RegisterCustomer2Bean) getManagedBean("registerCustomer2Bean");
		}
		return registerCustomer2Bean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setRegisterCustomer2Bean(
			RegisterCustomer2Bean registerCustomer2Bean) {
		this.registerCustomer2Bean = registerCustomer2Bean;
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

	protected UISelectItems getSelectItems3() {
		if (selectItems3 == null) {
			selectItems3 = (UISelectItems) findComponentInRoot("selectItems3");
		}
		return selectItems3;
	}

	protected UISelectItems getSelectItems4() {
		if (selectItems4 == null) {
			selectItems4 = (UISelectItems) findComponentInRoot("selectItems4");
		}
		return selectItems4;
	}

	protected UISelectItems getSelectItems5() {
		if (selectItems5 == null) {
			selectItems5 = (UISelectItems) findComponentInRoot("selectItems5");
		}
		return selectItems5;
	}

	protected UISelectItems getSelectItems6() {
		if (selectItems6 == null) {
			selectItems6 = (UISelectItems) findComponentInRoot("selectItems6");
		}
		return selectItems6;
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

}