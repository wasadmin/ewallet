/**
 * 
 */
package pagecode;

import com.ibm.faces.component.html.HtmlScriptCollector;
import com.ibm.faces.component.html.HtmlDataTableEx;
import com.ibm.faces.component.UIColumnEx;
import javax.faces.component.html.HtmlOutputText;
import com.ibm.faces.component.html.HtmlRequestLink;
import com.ibm.faces.component.html.HtmlPanelBox;
import javax.faces.component.html.HtmlOutputLabel;
import zw.co.esolutions.ewallet.adminweb.ApproveProfileBean;

/**
 * @author stanford
 *
 */
public class ApproveProfile extends PageCodeBase {

	protected HtmlScriptCollector scriptCollector1;
	protected HtmlDataTableEx tableEx1;
	protected UIColumnEx columnEx1;
	protected HtmlOutputText text1;
	protected HtmlOutputText text2;
	protected UIColumnEx columnEx2;
	protected HtmlOutputText text3;
	protected UIColumnEx columnEx3;
	protected HtmlOutputText text4;
	protected UIColumnEx columnEx4;
	protected UIColumnEx columnEx5;
	protected HtmlOutputText text5;
	protected HtmlRequestLink link1;
	protected UIColumnEx columnEx6;
	protected HtmlOutputText text7;
	protected HtmlRequestLink link2;
	protected HtmlPanelBox box1;
	protected HtmlOutputLabel label1;
	protected HtmlOutputText text6;
	protected HtmlOutputText text8;
	protected HtmlOutputText text9;
	protected HtmlOutputText text10;
	protected ApproveProfileBean approveProfileBean;

	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 = (HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}

	protected HtmlDataTableEx getTableEx1() {
		if (tableEx1 == null) {
			tableEx1 = (HtmlDataTableEx) findComponentInRoot("tableEx1");
		}
		return tableEx1;
	}

	protected UIColumnEx getColumnEx1() {
		if (columnEx1 == null) {
			columnEx1 = (UIColumnEx) findComponentInRoot("columnEx1");
		}
		return columnEx1;
	}

	protected HtmlOutputText getText1() {
		if (text1 == null) {
			text1 = (HtmlOutputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlOutputText getText2() {
		if (text2 == null) {
			text2 = (HtmlOutputText) findComponentInRoot("text2");
		}
		return text2;
	}

	protected UIColumnEx getColumnEx2() {
		if (columnEx2 == null) {
			columnEx2 = (UIColumnEx) findComponentInRoot("columnEx2");
		}
		return columnEx2;
	}

	protected HtmlOutputText getText3() {
		if (text3 == null) {
			text3 = (HtmlOutputText) findComponentInRoot("text3");
		}
		return text3;
	}

	protected UIColumnEx getColumnEx3() {
		if (columnEx3 == null) {
			columnEx3 = (UIColumnEx) findComponentInRoot("columnEx3");
		}
		return columnEx3;
	}

	protected HtmlOutputText getText4() {
		if (text4 == null) {
			text4 = (HtmlOutputText) findComponentInRoot("text4");
		}
		return text4;
	}

	protected UIColumnEx getColumnEx4() {
		if (columnEx4 == null) {
			columnEx4 = (UIColumnEx) findComponentInRoot("columnEx4");
		}
		return columnEx4;
	}

	protected UIColumnEx getColumnEx5() {
		if (columnEx5 == null) {
			columnEx5 = (UIColumnEx) findComponentInRoot("columnEx5");
		}
		return columnEx5;
	}

	protected HtmlOutputText getText5() {
		if (text5 == null) {
			text5 = (HtmlOutputText) findComponentInRoot("text5");
		}
		return text5;
	}

	protected HtmlRequestLink getLink1() {
		if (link1 == null) {
			link1 = (HtmlRequestLink) findComponentInRoot("link1");
		}
		return link1;
	}

	protected UIColumnEx getColumnEx6() {
		if (columnEx6 == null) {
			columnEx6 = (UIColumnEx) findComponentInRoot("columnEx6");
		}
		return columnEx6;
	}

	protected HtmlOutputText getText7() {
		if (text7 == null) {
			text7 = (HtmlOutputText) findComponentInRoot("text7");
		}
		return text7;
	}

	protected HtmlRequestLink getLink2() {
		if (link2 == null) {
			link2 = (HtmlRequestLink) findComponentInRoot("link2");
		}
		return link2;
	}

	protected HtmlPanelBox getBox1() {
		if (box1 == null) {
			box1 = (HtmlPanelBox) findComponentInRoot("box1");
		}
		return box1;
	}

	protected HtmlOutputLabel getLabel1() {
		if (label1 == null) {
			label1 = (HtmlOutputLabel) findComponentInRoot("label1");
		}
		return label1;
	}

	protected HtmlOutputText getText6() {
		if (text6 == null) {
			text6 = (HtmlOutputText) findComponentInRoot("text6");
		}
		return text6;
	}

	protected HtmlOutputText getText8() {
		if (text8 == null) {
			text8 = (HtmlOutputText) findComponentInRoot("text8");
		}
		return text8;
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

	/** 
	 * @managed-bean true
	 */
	protected ApproveProfileBean getApproveProfileBean() {
		if (approveProfileBean == null) {
			approveProfileBean = (ApproveProfileBean) getManagedBean("approveProfileBean");
		}
		return approveProfileBean;
	}

	/** 
	 * @managed-bean true
	 */
	protected void setApproveProfileBean(ApproveProfileBean approveProfileBean) {
		this.approveProfileBean = approveProfileBean;
	}

}