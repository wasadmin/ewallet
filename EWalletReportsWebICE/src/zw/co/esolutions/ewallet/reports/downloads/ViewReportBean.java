/**
 * 
 */
package zw.co.esolutions.ewallet.reports.downloads;

import pagecode.PageCodeBase;

/**
 * @author taurai
 *
 */
public class ViewReportBean extends PageCodeBase{

	private String pdfName;
	private String pageName;
	/**
	 * 
	 */
	public ViewReportBean() {
		super();
		System.out.println(">>>>>>>>>>>>>>> ViewReportBean ");
		if(this.getPdfName() == null) {
			//this.setPdfName((String)super.getRequestScope().get("pdfName"));
		}
		if(this.getPageName() == null) {
			//this.setPageName((String) super.getRequestScope().get("pageName"));
		}
		
	}
	/**
	 * @param pdfName the pdfName to set
	 */
	public void setPdfName(String pdfName) {
		this.pdfName = pdfName;
	}
	/**
	 * @return the pdfName
	 */
	public String getPdfName() {
		
		return pdfName;
	}
	/**
	 * @param pageName the pageName to set
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	/**
	 * @return the pageName
	 */
	public String getPageName() {
		
		return pageName;
	}
	

}
