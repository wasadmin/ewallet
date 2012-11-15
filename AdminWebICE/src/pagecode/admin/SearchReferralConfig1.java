/**
 * 
 */
package pagecode.admin;

import pagecode.PageCodeBase;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlDataTable;
import com.ibm.faces.component.UIColumnEx;
import zw.co.esolutions.ewallet.adminweb.SearchReferralConfigBean;

/**
 * @author stanford
 *
 */
public class SearchReferralConfig1 extends PageCodeBase {

	protected HtmlPanelGrid grid1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label2;
	protected HtmlForm form1;
	protected HtmlInputText text1;
	protected HtmlOutputLabel label3;
	protected HtmlInputText text2;
	protected HtmlCommandButton button1;
	protected HtmlCommandButton button2;
	protected UIColumn column1;
	protected HtmlOutputText text3;
	protected HtmlDataTable table1;
	protected HtmlOutputText text4;
	protected UIColumnEx columnEx1;
	protected HtmlOutputText text5;
	protected UIColumnEx columnEx2;
	protected HtmlOutputText text6;
	protected UIColumnEx columnEx3;
	protected HtmlOutputLabel label4;
	protected HtmlOutputText text7;
	protected UIColumnEx columnEx4;
	protected HtmlOutputText text8;
	protected UIColumnEx columnEx5;
	protected HtmlOutputText text9;
	protected HtmlOutputText text10;
	protected HtmlOutputText text11;
	protected HtmlOutputText text12;
	protected HtmlOutputText text13;
	protected HtmlOutputText text14;
	protected SearchReferralConfigBean searchReferralConfigBean;
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

	protected UIColumn getColumn1() {
		if (column1 == null) {
			column1 = (UIColumn) findComponentInRoot("column1");
		}
		return column1;
	}

	protected HtmlOutputText getText3() {
		if (text3 == null) {
			text3 = (HtmlOutputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected HtmlDataTable getTable1() {
		if (table1 == null) {
			table1 = (HtmlDataTable) findComponentInRoot("table1");
		}
		return table1;
	}

	protected HtmlOutputText getText4() {
		if (text4 == null) {
			text4 = (HtmlOutputText) findComponentInRoot("text4");
		}
		return text4;
	}

	protected UIColumnEx getColumnEx1() {
		if (columnEx1 == null) {
			columnEx1 = (UIColumnEx) findComponentInRoot("columnEx1");
		}
		return columnEx1;
	}

	protected HtmlOutputText getText5() {
		if (text5 == null) {
			text5 = (HtmlOutputText) findComponentInRoot("text5");
		}
		return text5;
	}

	protected UIColumnEx getColumnEx2() {
		if (columnEx2 == null) {
			columnEx2 = (UIColumnEx) findComponentInRoot("columnEx2");
		}
		return columnEx2;
	}

	protected HtmlOutputText getText6() {
		if (text6 == null) {
			text6 = (HtmlOutputText) findComponentInRoot("text6");
		}
		return text6;
	}

	protected UIColumnEx getColumnEx3() {
		if (columnEx3 == null) {
			columnEx3 = (UIColumnEx) findComponentInRoot("columnEx3");
		}
		return columnEx3;
	}

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlOutputText getText7() {
		if (text7 == null) {
			text7 = (HtmlOutputText) findComponentInRoot("text7");
		}
		return text7;
	}

	protected UIColumnEx getColumnEx4() {
		if (columnEx4 == null) {
			columnEx4 = (UIColumnEx) findComponentInRoot("columnEx4");
		}
		return columnEx4;
	}

	protected HtmlOutputText getText8() {
		if (text8 == null) {
			text8 = (HtmlOutputText) findComponentInRoot("text8");
		}
		return text8;
	}

	protected UIColumnEx getColumnEx5() {
		if (columnEx5 == null) {
			columnEx5 = (UIColumnEx) findComponentInRoot("columnEx5");
		}
		return columnEx5;
	}

	protected HtmlOutputText getText9() {
		if (text9 == null) {
			text9 = (HtmlOutputText) findComponentInRoot("text9");
		}
		return text9;
	}

	protected HtmlOutputText getText10() {
		if (text10 == null) {
			text10 = (HtmlOutputText) findComponentInRoot("text10");
		}
		return text10;
	}

	protected HtmlOutputText getText11() {
		if (text11 == null) {
			text11 = (HtmlOutputText) findComponentInRoot("text11");
		}
		return text11;
	}

	protected HtmlOutputText getText12() {
		if (text12 == null) {
			text12 = (HtmlOutputText) findComponentInRoot("text12");
		}
		return text12;
	}

	protected HtmlOutputText getText13() {
		if (text13 == null) {
			text13 = (HtmlOutputText) findComponentInRoot("text13");
		}
		return text13;
	}

	protected HtmlOutputText getText14() {
		if (text14 == null) {
			text14 = (HtmlOutputText) findComponentInRoot("text14");
		}
		return text14;
	}

	/** 
	 * @managed-bean true
	 */
	protected SearchReferralConfigBean getSearchReferralConfigBean() {
		if (searchReferralConfigBean == null) {
			searchReferralConfigBean = (SearchReferralConfigBean) getManagedBean("searchReferralConfigBean");
		}
		return searchReferralConfigBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setSearchReferralConfigBean(
			SearchReferralConfigBean searchReferralConfigBean) {
		this.searchReferralConfigBean = searchReferralConfigBean;
	}

}