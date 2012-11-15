/**
 * 
 */
package pagecode;

import com.icesoft.faces.component.paneltabset.PanelTab;
import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.UIColumn;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlMessages;
import javax.faces.component.html.HtmlForm;
import com.ibm.faces.component.html.HtmlScriptCollector;
import com.ibm.faces.component.html.HtmlRequestLink;

/**
 * @author tauttee
 *
 */
public class ViewCustomer1 extends PageCodeBase {

	protected PanelTab bfpanel1;
	protected HtmlPanelGrid grid1;
	protected HtmlOutputLabel label2;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlCommandButton button1;
	protected HtmlPanelGrid grid2;
	protected HtmlOutputLabel label11;
	protected HtmlPanelGroup group2;
	protected HtmlOutputLabel label10;
	protected HtmlDataTable tableEx1;
	protected HtmlOutputLabel label19;
	protected HtmlOutputText text17;
	protected HtmlOutputText text18;
	protected HtmlOutputText text19;
	protected HtmlPanelGrid grid3;
	protected HtmlOutputLabel label26;
	protected HtmlPanelGroup group3;
	protected HtmlOutputLabel label20;
	protected HtmlCommandButton tabbedPanel1_back;
	protected HtmlCommandButton tabbedPanel1_next;
	protected HtmlCommandButton tabbedPanel1_finish;
	protected HtmlCommandButton tabbedPanel1_cancel;
	protected HtmlOutputText text1;
	protected HtmlOutputLabel label3;
	protected HtmlOutputText text2;
	protected HtmlOutputLabel label4;
	protected HtmlOutputText text3;
	protected HtmlOutputLabel label5;
	protected HtmlOutputText text4;
	protected HtmlOutputLabel label6;
	protected HtmlOutputText text5;
	protected HtmlOutputLabel label8;
	protected HtmlOutputText text7;
	protected HtmlOutputLabel label7;
	protected HtmlOutputText text6;
	protected HtmlOutputLabel label9;
	protected HtmlOutputText text8;
	protected HtmlOutputLabel label25;
	protected HtmlOutputText text26;
	protected HtmlPanelGrid grid4;
	protected HtmlCommandButton button2;
	protected PanelTab bfpanel2;
	protected HtmlOutputText text9;
	protected HtmlOutputLabel label12;
	protected HtmlOutputText text10;
	protected HtmlOutputLabel label13;
	protected HtmlOutputText text11;
	protected HtmlOutputLabel label14;
	protected HtmlOutputText text12;
	protected HtmlOutputLabel label15;
	protected HtmlOutputText text13;
	protected HtmlOutputLabel label16;
	protected HtmlOutputText text14;
	protected HtmlOutputLabel label17;
	protected HtmlOutputText text15;
	protected HtmlOutputLabel label18;
	protected HtmlOutputText text16;
	protected PanelTab bfpanel3;
	protected UIColumn column1;
	protected HtmlOutputText text20;
	protected UIColumn column2;
	protected HtmlOutputText text21;
	protected UIColumn column3;
	protected HtmlCommandLink link1;
	protected PanelTab bfpanel4;
	protected HtmlOutputText text28;
	protected HtmlOutputLabel label27;
	protected HtmlOutputText text27;
	protected HtmlOutputLabel label21;
	protected HtmlOutputText text22;
	protected HtmlOutputLabel label22;
	protected HtmlOutputText text23;
	protected HtmlOutputLabel label24;
	protected HtmlOutputText text25;
	protected HtmlOutputLabel label23;
	protected HtmlOutputText text24;
	protected UIColumn column35;
	protected HtmlCommandLink link14;
	protected HtmlOutputText text195;
	protected HtmlForm form1;
	protected HtmlMessages messages1;
	protected HtmlScriptCollector scriptCollector1;
	protected HtmlPanelGrid grid5;
	protected HtmlOutputText text29;
	protected HtmlOutputText text30;
	protected HtmlOutputText text31;
	protected HtmlRequestLink link4;
	protected HtmlOutputText text32;
	protected HtmlRequestLink link5;
	protected HtmlCommandLink link2;
	protected HtmlCommandLink link3;
	protected PanelTab getBfpanel1() {
		if (bfpanel1 == null) {
			bfpanel1 = (PanelTab) findComponentInRoot("bfpanel1");
		}
		return bfpanel1;
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

	protected HtmlCommandButton getButton1() {
		if (button1 == null) {
			button1 = (HtmlCommandButton) findComponentInRoot("button1");
		}
		return button1;
	}

	protected HtmlPanelGrid getGrid2() {
		if (grid2 == null) {
			grid2 = (HtmlPanelGrid) findComponentInRoot("grid2");
		}
		return grid2;
	}

	protected HtmlOutputLabel getLabel11() {
		if (label11 == null) {
			label11 = (HtmlOutputLabel) findComponentInRoot("label11");
		}
		return label11;
	}

	protected HtmlPanelGroup getGroup2() {
		if (group2 == null) {
			group2 = (HtmlPanelGroup) findComponentInRoot("group2");
		}
		return group2;
	}

	protected HtmlOutputLabel getLabel10() {
		if (label10 == null) {
			label10 = (HtmlOutputLabel) findComponentInRoot("label10");
		}
		return label10;
	}

	protected HtmlDataTable getTableEx1() {
		if (tableEx1 == null) {
			tableEx1 = (HtmlDataTable) findComponentInRoot("tableEx1");
		}
		return tableEx1;
	}

	protected HtmlOutputLabel getLabel19() {
		if (label19 == null) {
			label19 = (HtmlOutputLabel) findComponentInRoot("label19");
		}
		return label19;
	}

	protected HtmlOutputText getText17() {
		if (text17 == null) {
			text17 = (HtmlOutputText) findComponentInRoot("text17");
		}
		return text17;
	}

	protected HtmlOutputText getText18() {
		if (text18 == null) {
			text18 = (HtmlOutputText) findComponentInRoot("text18");
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

	protected HtmlOutputLabel getLabel26() {
		if (label26 == null) {
			label26 = (HtmlOutputLabel) findComponentInRoot("label26");
		}
		return label26;
	}

	protected HtmlPanelGroup getGroup3() {
		if (group3 == null) {
			group3 = (HtmlPanelGroup) findComponentInRoot("group3");
		}
		return group3;
	}

	protected HtmlOutputLabel getLabel20() {
		if (label20 == null) {
			label20 = (HtmlOutputLabel) findComponentInRoot("label20");
		}
		return label20;
	}

	protected HtmlCommandButton getTabbedPanel1_back() {
		if (tabbedPanel1_back == null) {
			tabbedPanel1_back = (HtmlCommandButton) findComponentInRoot("tabbedPanel1_back");
		}
		return tabbedPanel1_back;
	}

	protected HtmlCommandButton getTabbedPanel1_next() {
		if (tabbedPanel1_next == null) {
			tabbedPanel1_next = (HtmlCommandButton) findComponentInRoot("tabbedPanel1_next");
		}
		return tabbedPanel1_next;
	}

	protected HtmlCommandButton getTabbedPanel1_finish() {
		if (tabbedPanel1_finish == null) {
			tabbedPanel1_finish = (HtmlCommandButton) findComponentInRoot("tabbedPanel1_finish");
		}
		return tabbedPanel1_finish;
	}

	protected HtmlCommandButton getTabbedPanel1_cancel() {
		if (tabbedPanel1_cancel == null) {
			tabbedPanel1_cancel = (HtmlCommandButton) findComponentInRoot("tabbedPanel1_cancel");
		}
		return tabbedPanel1_cancel;
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

	protected HtmlOutputLabel getLabel9() {
		if (label9 == null) {
			label9 = (HtmlOutputLabel) findComponentInRoot("label9");
		}
		return label9;
	}

	protected HtmlOutputText getText8() {
		if (text8 == null) {
			text8 = (HtmlOutputText) findComponentInRoot("text8");
		}
		return text8;
	}

	protected HtmlOutputLabel getLabel25() {
		if (label25 == null) {
			label25 = (HtmlOutputLabel) findComponentInRoot("label25");
		}
		return label25;
	}

	protected HtmlOutputText getText26() {
		if (text26 == null) {
			text26 = (HtmlOutputText) findComponentInRoot("text26");
		}
		return text26;
	}

	protected HtmlPanelGrid getGrid4() {
		if (grid4 == null) {
			grid4 = (HtmlPanelGrid) findComponentInRoot("grid4");
		}
		return grid4;
	}

	protected HtmlCommandButton getButton2() {
		if (button2 == null) {
			button2 = (HtmlCommandButton) findComponentInRoot("button2");
		}
		return button2;
	}

	protected PanelTab getBfpanel2() {
		if (bfpanel2 == null) {
			bfpanel2 = (PanelTab) findComponentInRoot("bfpanel2");
		}
		return bfpanel2;
	}

	protected HtmlOutputText getText9() {
		if (text9 == null) {
			text9 = (HtmlOutputText) findComponentInRoot("text9");
		}
		return text9;
	}

	protected HtmlOutputLabel getLabel12() {
		if (label12 == null) {
			label12 = (HtmlOutputLabel) findComponentInRoot("label12");
		}
		return label12;
	}

	protected HtmlOutputText getText10() {
		if (text10 == null) {
			text10 = (HtmlOutputText) findComponentInRoot("text10");
		}
		return text10;
	}

	protected HtmlOutputLabel getLabel13() {
		if (label13 == null) {
			label13 = (HtmlOutputLabel) findComponentInRoot("label13");
		}
		return label13;
	}

	protected HtmlOutputText getText11() {
		if (text11 == null) {
			text11 = (HtmlOutputText) findComponentInRoot("text11");
		}
		return text11;
	}

	protected HtmlOutputLabel getLabel14() {
		if (label14 == null) {
			label14 = (HtmlOutputLabel) findComponentInRoot("label14");
		}
		return label14;
	}

	protected HtmlOutputText getText12() {
		if (text12 == null) {
			text12 = (HtmlOutputText) findComponentInRoot("text12");
		}
		return text12;
	}

	protected HtmlOutputLabel getLabel15() {
		if (label15 == null) {
			label15 = (HtmlOutputLabel) findComponentInRoot("label15");
		}
		return label15;
	}

	protected HtmlOutputText getText13() {
		if (text13 == null) {
			text13 = (HtmlOutputText) findComponentInRoot("text13");
		}
		return text13;
	}

	protected HtmlOutputLabel getLabel16() {
		if (label16 == null) {
			label16 = (HtmlOutputLabel) findComponentInRoot("label16");
		}
		return label16;
	}

	protected HtmlOutputText getText14() {
		if (text14 == null) {
			text14 = (HtmlOutputText) findComponentInRoot("text14");
		}
		return text14;
	}

	protected HtmlOutputLabel getLabel17() {
		if (label17 == null) {
			label17 = (HtmlOutputLabel) findComponentInRoot("label17");
		}
		return label17;
	}

	protected HtmlOutputText getText15() {
		if (text15 == null) {
			text15 = (HtmlOutputText) findComponentInRoot("text15");
		}
		return text15;
	}

	protected HtmlOutputLabel getLabel18() {
		if (label18 == null) {
			label18 = (HtmlOutputLabel) findComponentInRoot("label18");
		}
		return label18;
	}

	protected HtmlOutputText getText16() {
		if (text16 == null) {
			text16 = (HtmlOutputText) findComponentInRoot("text16");
		}
		return text16;
	}

	protected PanelTab getBfpanel3() {
		if (bfpanel3 == null) {
			bfpanel3 = (PanelTab) findComponentInRoot("bfpanel3");
		}
		return bfpanel3;
	}

	protected UIColumn getColumn1() {
		if (column1 == null) {
			column1 = (UIColumn) findComponentInRoot("column1");
		}
		return column1;
	}

	protected HtmlOutputText getText20() {
		if (text20 == null) {
			text20 = (HtmlOutputText) findComponentInRoot("text20");
		}
		return text20;
	}

	protected UIColumn getColumn2() {
		if (column2 == null) {
			column2 = (UIColumn) findComponentInRoot("column2");
		}
		return column2;
	}

	protected HtmlOutputText getText21() {
		if (text21 == null) {
			text21 = (HtmlOutputText) findComponentInRoot("text21");
		}
		return text21;
	}

	protected UIColumn getColumn3() {
		if (column3 == null) {
			column3 = (UIColumn) findComponentInRoot("column3");
		}
		return column3;
	}

	protected HtmlCommandLink getLink1() {
		if (link1 == null) {
			link1 = (HtmlCommandLink) findComponentInRoot("link1");
		}
		return link1;
	}

	protected PanelTab getBfpanel4() {
		if (bfpanel4 == null) {
			bfpanel4 = (PanelTab) findComponentInRoot("bfpanel4");
		}
		return bfpanel4;
	}

	protected HtmlOutputText getText28() {
		if (text28 == null) {
			text28 = (HtmlOutputText) findComponentInRoot("text28");
		}
		return text28;
	}

	protected HtmlOutputLabel getLabel27() {
		if (label27 == null) {
			label27 = (HtmlOutputLabel) findComponentInRoot("label27");
		}
		return label27;
	}

	protected HtmlOutputText getText27() {
		if (text27 == null) {
			text27 = (HtmlOutputText) findComponentInRoot("text27");
		}
		return text27;
	}

	protected HtmlOutputLabel getLabel21() {
		if (label21 == null) {
			label21 = (HtmlOutputLabel) findComponentInRoot("label21");
		}
		return label21;
	}

	protected HtmlOutputText getText22() {
		if (text22 == null) {
			text22 = (HtmlOutputText) findComponentInRoot("text22");
		}
		return text22;
	}

	protected HtmlOutputLabel getLabel22() {
		if (label22 == null) {
			label22 = (HtmlOutputLabel) findComponentInRoot("label22");
		}
		return label22;
	}

	protected HtmlOutputText getText23() {
		if (text23 == null) {
			text23 = (HtmlOutputText) findComponentInRoot("text23");
		}
		return text23;
	}

	protected HtmlOutputLabel getLabel24() {
		if (label24 == null) {
			label24 = (HtmlOutputLabel) findComponentInRoot("label24");
		}
		return label24;
	}

	protected HtmlOutputText getText25() {
		if (text25 == null) {
			text25 = (HtmlOutputText) findComponentInRoot("text25");
		}
		return text25;
	}

	protected HtmlOutputLabel getLabel23() {
		if (label23 == null) {
			label23 = (HtmlOutputLabel) findComponentInRoot("label23");
		}
		return label23;
	}

	protected HtmlOutputText getText24() {
		if (text24 == null) {
			text24 = (HtmlOutputText) findComponentInRoot("text24");
		}
		return text24;
	}

	protected UIColumn getColumn35() {
		if (column35 == null) {
			column35 = (UIColumn) findComponentInRoot("column35");
		}
		return column35;
	}

	protected HtmlCommandLink getLink14() {
		if (link14 == null) {
			link14 = (HtmlCommandLink) findComponentInRoot("link14");
		}
		return link14;
	}

	protected HtmlOutputText getText195() {
		if (text195 == null) {
			text195 = (HtmlOutputText) findComponentInRoot("text195");
		}
		return text195;
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

	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 = (HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}

	protected HtmlPanelGrid getGrid5() {
		if (grid5 == null) {
			grid5 = (HtmlPanelGrid) findComponentInRoot("grid5");
		}
		return grid5;
	}

	protected HtmlOutputText getText29() {
		if (text29 == null) {
			text29 = (HtmlOutputText) findComponentInRoot("text29");
		}
		return text29;
	}

	protected HtmlOutputText getText30() {
		if (text30 == null) {
			text30 = (HtmlOutputText) findComponentInRoot("text30");
		}
		return text30;
	}

	protected HtmlOutputText getText31() {
		if (text31 == null) {
			text31 = (HtmlOutputText) findComponentInRoot("text31");
		}
		return text31;
	}

	protected HtmlRequestLink getLink4() {
		if (link4 == null) {
			link4 = (HtmlRequestLink) findComponentInRoot("link4");
		}
		return link4;
	}

	protected HtmlOutputText getText32() {
		if (text32 == null) {
			text32 = (HtmlOutputText) findComponentInRoot("text32");
		}
		return text32;
	}

	protected HtmlRequestLink getLink5() {
		if (link5 == null) {
			link5 = (HtmlRequestLink) findComponentInRoot("link5");
		}
		return link5;
	}

	protected HtmlCommandLink getLink2() {
		if (link2 == null) {
			link2 = (HtmlCommandLink) findComponentInRoot("link2");
		}
		return link2;
	}

	protected HtmlCommandLink getLink3() {
		if (link3 == null) {
			link3 = (HtmlCommandLink) findComponentInRoot("link3");
		}
		return link3;
	}

}