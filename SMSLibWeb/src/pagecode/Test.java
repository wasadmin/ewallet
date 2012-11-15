/**
 * 
 */
package pagecode;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlMessages;

/**
 * @author taurai
 *
 */
public class Test extends PageCodeBase {

	protected UIComponent collector1;
	protected HtmlForm form1;
	protected UIComponent startServer1;
	protected HtmlMessages messages1;

	protected UIComponent getCollector1() {
		if (collector1 == null) {
			collector1 = (UIComponent) findComponentInRoot("collector1");
		}
		return collector1;
	}

	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}

	protected UIComponent getStartServer1() {
		if (startServer1 == null) {
			startServer1 = (UIComponent) findComponentInRoot("startServer1");
		}
		return startServer1;
	}

	protected HtmlMessages getMessages1() {
		if (messages1 == null) {
			messages1 = (HtmlMessages) findComponentInRoot("messages1");
		}
		return messages1;
	}

}