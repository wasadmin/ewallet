/**
 * 
 */
package pagecode.csr;

import pagecode.PageCodeBase;
import javax.faces.component.html.HtmlOutputText;
import com.ibm.faces.component.html.HtmlScriptCollector;

/**
 * @author taurai
 *
 */
public class Index extends PageCodeBase {

	protected HtmlOutputText text1;
	protected HtmlScriptCollector scriptCollector1;

	protected HtmlOutputText getText1() {
		if (text1 == null) {
			text1 = (HtmlOutputText) findComponentInRoot("text1");
		}
		return text1;
	}

	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 = (HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}

}