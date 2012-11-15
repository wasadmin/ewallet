/**
 * 
 */
package pagecode.admin;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.adminweb.CreateBankMerchantBean;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlInputText;
import javax.faces.component.UISelectItems;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlPanelGroup;

/**
 * @author stanford
 *
 */
public class CreateBankMerchant extends PageCodeBase {

	protected CreateBankMerchantBean createBankMerchantBean;
	protected HtmlOutputLabel label3884;
	protected HtmlInputText accountNumber;
	protected UISelectItems selectItems1;
	protected HtmlSelectOneMenu menu1;
	protected HtmlOutputLabel label4;
	protected HtmlOutputText text3;
	protected HtmlOutputLabel label5;
	protected HtmlInputText text4;
	protected HtmlOutputLabel label6;
	protected HtmlInputText text5;
	protected HtmlOutputLabel label7;
	protected HtmlInputText text6;
	protected HtmlOutputLabel label8;
	protected HtmlInputText text7;
	protected HtmlOutputLabel label9;
	protected HtmlInputText text8;
	protected HtmlOutputLabel label10;
	protected HtmlInputText text9;
	protected HtmlOutputLabel label11;
	protected HtmlInputText text10;
	protected HtmlOutputLabel label12;
	protected HtmlInputText text11;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label38899;
	/** 
	 * @managed-bean true
	 */
	protected CreateBankMerchantBean getCreateBankMerchantBean() {
		if (createBankMerchantBean == null) {
			createBankMerchantBean = (CreateBankMerchantBean) getManagedBean("createBankMerchantBean");
		}
		return createBankMerchantBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setCreateBankMerchantBean(
			CreateBankMerchantBean createBankMerchantBean) {
		this.createBankMerchantBean = createBankMerchantBean;
	}

	protected HtmlOutputLabel getLabel3884() {
		if (label3884 == null) {
			label3884 = (HtmlOutputLabel) findComponentInRoot("label3884");
		}
		return label3884;
	}

	protected HtmlInputText getAccountNumber() {
		if (accountNumber == null) {
			accountNumber = (HtmlInputText) findComponentInRoot("accountNumber");
		}
		return accountNumber;
	}

	protected UISelectItems getSelectItems1() {
		if (selectItems1 == null) {
			selectItems1 = (UISelectItems) findComponentInRoot("selectItems1");
		}
		return selectItems1;
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

	protected HtmlInputText getText6() {
		if (text6 == null) {
			text6 = (HtmlInputText) findComponentInRoot("text6");
		}
		return text6;
	}

	protected HtmlOutputLabel getLabel8() {
		if (label8 == null) {
			label8 = (HtmlOutputLabel) findComponentInRoot("label8");
		}
		return label8;
	}

	protected HtmlInputText getText7() {
		if (text7 == null) {
			text7 = (HtmlInputText) findComponentInRoot("text7");
		}
		return text7;
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

	protected HtmlOutputLabel getLabel10() {
		if (label10 == null) {
			label10 = (HtmlOutputLabel) findComponentInRoot("label10");
		}
		return label10;
	}

	protected HtmlInputText getText9() {
		if (text9 == null) {
			text9 = (HtmlInputText) findComponentInRoot("text9");
		}
		return text9;
	}

	protected HtmlOutputLabel getLabel11() {
		if (label11 == null) {
			label11 = (HtmlOutputLabel) findComponentInRoot("label11");
		}
		return label11;
	}

	protected HtmlInputText getText10() {
		if (text10 == null) {
			text10 = (HtmlInputText) findComponentInRoot("text10");
		}
		return text10;
	}

	protected HtmlOutputLabel getLabel12() {
		if (label12 == null) {
			label12 = (HtmlOutputLabel) findComponentInRoot("label12");
		}
		return label12;
	}

	protected HtmlInputText getText11() {
		if (text11 == null) {
			text11 = (HtmlInputText) findComponentInRoot("text11");
		}
		return text11;
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

	protected HtmlOutputLabel getLabel38899() {
		if (label38899 == null) {
			label38899 = (HtmlOutputLabel) findComponentInRoot("label38899");
		}
		return label38899;
	}

}