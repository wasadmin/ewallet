/**
 * 
 */
package pagecode.admin;

import pagecode.PageCodeBase;
import javax.faces.component.html.HtmlPanelGrid;
import com.icesoft.faces.component.ext.HtmlForm;
import com.icesoft.faces.component.ext.HtmlMessages;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import zw.co.esolutions.ewallet.adminweb.CreateReferralConfigBean;

/**
 * @author stanford
 *
 */
public class CreateReferralConfig2 extends PageCodeBase {

	protected HtmlPanelGrid grid1;
	protected HtmlForm form1;
	protected HtmlMessages messages1;
	protected HtmlPanelGroup group1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputLabel label2;
	protected HtmlForm form2;
	protected HtmlInputText text1;
	protected HtmlOutputLabel label3;
	protected HtmlInputText text2;
	protected HtmlOutputLabel label4;
	protected HtmlInputText text3;
	protected HtmlOutputLabel label5;
	protected HtmlInputText text4;
	protected HtmlPanelGrid grid2;
	protected HtmlCommandButton button1;
	protected HtmlCommandButton button2;
	protected CreateReferralConfigBean createReferralConfigBean;
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

	protected HtmlOutputLabel getLabel2() {
		if (label2 == null) {
			label2 = (HtmlOutputLabel) findComponentInRoot("label2");
		}
		return label2;
	}

	protected HtmlForm getForm2() {
		if (form2 == null) {
			form2 = (HtmlForm) findComponentInRoot("form2");
		}
		return form2;
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

	protected HtmlOutputLabel getLabel4() {
		if (label4 == null) {
			label4 = (HtmlOutputLabel) findComponentInRoot("label4");
		}
		return label4;
	}

	protected HtmlInputText getText3() {
		if (text3 == null) {
			text3 = (HtmlInputText) findComponentInRoot("text3");
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

	/** 
	 * @managed-bean true
	 */
	protected CreateReferralConfigBean getCreateReferralConfigBean() {
		if (createReferralConfigBean == null) {
			createReferralConfigBean = (CreateReferralConfigBean) getManagedBean("createReferralConfigBean");
		}
		return createReferralConfigBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setCreateReferralConfigBean(
			CreateReferralConfigBean createReferralConfigBean) {
		this.createReferralConfigBean = createReferralConfigBean;
	}

}