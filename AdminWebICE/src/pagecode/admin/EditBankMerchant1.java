/**
 * 
 */
package pagecode.admin;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.adminweb.EditBankMerchantBean;

import com.icesoft.faces.component.ext.HtmlForm;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.component.ext.HtmlMessages;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;

import javax.faces.component.UISelectItems;

/**
 * @author stanford
 *
 */
public class EditBankMerchant1 extends PageCodeBase {

	protected EditBankMerchantBean editBankMerchantBean;
	protected HtmlOutputLabel label332;
	protected HtmlOutputLabel label45;
	protected HtmlOutputLabel label59;
	protected HtmlOutputText merchantName;
	protected HtmlOutputText status;
	protected HtmlOutputText shortName;
	protected HtmlInputText accountNumber;
	protected HtmlOutputLabel label5990;
	protected HtmlForm form2;
	protected HtmlSelectOneMenu menu1;
	protected HtmlPanelGrid grid1;
	protected HtmlForm form1;
	protected HtmlMessages messages1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label3;
	protected HtmlPanelGrid grid2;
	protected HtmlCommandButton button1;
	protected HtmlCommandButton button2;
	protected UISelectItems selectItems1;
	/** 
	 * @managed-bean true
	 */
	protected EditBankMerchantBean getEditBankMerchantBean() {
		if (editBankMerchantBean == null) {
			editBankMerchantBean = (EditBankMerchantBean) getManagedBean("editBankMerchantBean");
		}
		return editBankMerchantBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setEditBankMerchantBean(
			EditBankMerchantBean editBankMerchantBean) {
		this.editBankMerchantBean = editBankMerchantBean;
	}

	protected HtmlOutputLabel getLabel332() {
		if (label332 == null) {
			label332 = (HtmlOutputLabel) findComponentInRoot("label332");
		}
		return label332;
	}

	protected HtmlOutputLabel getLabel45() {
		if (label45 == null) {
			label45 = (HtmlOutputLabel) findComponentInRoot("label45");
		}
		return label45;
	}

	protected HtmlOutputLabel getLabel59() {
		if (label59 == null) {
			label59 = (HtmlOutputLabel) findComponentInRoot("label59");
		}
		return label59;
	}

	protected HtmlOutputText getMerchantName() {
		if (merchantName == null) {
			merchantName = (HtmlOutputText) findComponentInRoot("merchantName");
		}
		return merchantName;
	}

	protected HtmlOutputText getStatus() {
		if (status == null) {
			status = (HtmlOutputText) findComponentInRoot("status");
		}
		return status;
	}

	protected HtmlOutputText getShortName() {
		if (shortName == null) {
			shortName = (HtmlOutputText) findComponentInRoot("shortName");
		}
		return shortName;
	}

	protected HtmlInputText getAccountNumber() {
		if (accountNumber == null) {
			accountNumber = (HtmlInputText) findComponentInRoot("accountNumber");
		}
		return accountNumber;
	}

	protected HtmlOutputLabel getLabel5990() {
		if (label5990 == null) {
			label5990 = (HtmlOutputLabel) findComponentInRoot("label5990");
		}
		return label5990;
	}

	protected HtmlForm getForm2() {
		if (form2 == null) {
			form2 = (HtmlForm) findComponentInRoot("form2");
		}
		return form2;
	}

	protected HtmlSelectOneMenu getMenu1() {
		if (menu1 == null) {
			menu1 = (HtmlSelectOneMenu) findComponentInRoot("menu1");
		}
		return menu1;
	}

	protected HtmlPanelGrid getGrid1() {
		if (grid1 == null) {
			grid1 = (HtmlPanelGrid) findComponentInRoot("grid1");
		}
		return grid1;
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

	protected HtmlOutputLabel getLabel3() {
		if (label3 == null) {
			label3 = (HtmlOutputLabel) findComponentInRoot("label3");
		}
		return label3;
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

	protected HtmlCommandButton getButton2() {
		if (button2 == null) {
			button2 = (HtmlCommandButton) findComponentInRoot("button2");
		}
		return button2;
	}

	protected UISelectItems getSelectItems1() {
		if (selectItems1 == null) {
			selectItems1 = (UISelectItems) findComponentInRoot("selectItems1");
		}
		return selectItems1;
	}

}