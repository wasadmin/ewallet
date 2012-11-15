/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import com.ibm.faces.component.UIColumnEx;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlOutputLabel;

import zw.co.esolutions.ewallet.tellerweb.ApproveMobileProfileBean;

import com.ibm.faces.component.html.HtmlRequestLink;

/**
 * @author stanford
 *
 */
public class ApproveMobileProfile extends PageCodeBase {

	protected UIColumnEx columnEx1;
	protected HtmlOutputText text1;
	protected HtmlOutputText text2;
	protected UIColumnEx columnEx2;
	protected HtmlOutputText text3;
	protected UIColumnEx columnEx3;
	protected HtmlOutputLabel label1;
	protected HtmlOutputText text4;
	protected UIColumnEx columnEx4;
	protected HtmlOutputText text6;
	protected HtmlOutputText text7;
	protected HtmlOutputText text8;
	protected ApproveMobileProfileBean approveMobileProfileBean;
	protected HtmlRequestLink link1;
	protected HtmlOutputText text5;
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
	protected HtmlOutputLabel getLabel1() {
		if (label1 == null) {
			label1 = (HtmlOutputLabel) findComponentInRoot("label1");
		}
		return label1;
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
	protected HtmlOutputText getText8() {
		if (text8 == null) {
			text8 = (HtmlOutputText) findComponentInRoot("text8");
		}
		return text8;
	}
	/** 
	 * @managed-bean true
	 */
	protected ApproveMobileProfileBean getApproveMobileProfileBean() {
		if (approveMobileProfileBean == null) {
			approveMobileProfileBean = (ApproveMobileProfileBean) getManagedBean("approveMobileProfileBean");
		}
		return approveMobileProfileBean;
	}
	/** 
	 * @managed-bean true
	 */
	protected void setApproveMobileProfileBean(
			ApproveMobileProfileBean approveMobileProfileBean) {
		this.approveMobileProfileBean = approveMobileProfileBean;
	}
	protected HtmlRequestLink getLink1() {
		if (link1 == null) {
			link1 = (HtmlRequestLink) findComponentInRoot("link1");
		}
		return link1;
	}
	protected HtmlOutputText getText5() {
		if (text5 == null) {
			text5 = (HtmlOutputText) findComponentInRoot("text5");
		}
		return text5;
	}

}