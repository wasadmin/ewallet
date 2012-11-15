/**
 * 
 */
package pagecode;

import javax.faces.component.html.HtmlPanelGrid;

/**
 * @author stanford
 *
 */
public class AccountStatementReport extends PageCodeBase {

	protected HtmlPanelGrid grid1;

	protected HtmlPanelGrid getGrid1() {
		if (grid1 == null) {
			grid1 = (HtmlPanelGrid) findComponentInRoot("grid1");
		}
		return grid1;
	}

}