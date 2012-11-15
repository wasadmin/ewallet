/**
 * 
 */
package zw.co.esolutions.ewallet.reports;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

/**
 * @author tauttee
 *
 */
public class TariffListBean extends PageCodeBase{
	
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(URLEncryptor.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + URLEncryptor.class);
		}
	}
	
	private static void log4(String message) {
		LOG.debug(message);
	}
	private Date toDate;
	private Date fromDate;
	private boolean latestReport;
	
	public TariffListBean() {
		super();
		this.initializeDates();
	}
	
	public String generateByRange() {
		String fileName = "commissions_list";
		String fileId = null;
		if(this.getFromDate() == null && this.getToDate() == null) {
			super.setErrorMessage("Both dates are required");
			return "failure";
		}
		if(this.getFromDate() == null) {
			super.setErrorMessage("From Date is required.");
			return "failure";
		} if(this.getToDate() == null) {
			super.setErrorMessage("To Date is required.");
			return "failure";
		}
		String query = null;
		Timestamp fromDate = new Timestamp(DateUtil.getBeginningOfDay(this.getFromDate()).getTime());
		Timestamp toDate = new Timestamp(DateUtil.getEndOfDay(this.getToDate()).getTime());
		Map<String, String> parameters = new HashMap<String, String>();
		
		try {
			
			//Checking for To Date to Display
			this.initializeDisplayableDates();
			
			parameters.put("reportTitle", "Effective Commissions As at "+DateUtil.convertToDateWithTime(this.getToDate()));
			parameters.put("sourceFile", fileName+".xml");
			
			//Enabling Report generation Via Connection
			parameters.put(EWalletConstants.JASPER_CONN, EWalletConstants.JASPER_CONN);
			
			query = "SELECT * FROM "+EWalletConstants.DATABASE_SCHEMA+".TARIFF, "+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE WHERE "+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE.id = "+EWalletConstants.DATABASE_SCHEMA+".TARIFF.tariffTable_id " +
			"AND "+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE.effectiveDate <= '"+fromDate+"' AND ("+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE.endDate IS NULL " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE.endDate >= '"+toDate+"')";
	       
			fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
			
			if(fileId == null) {
				super.setInformationMessage("No results to display.");
				return "failure";
			}
			
		} catch (Exception e) {
			super.setErrorMessage("An Error has occurred. Operation arborted.");
			return "failure";
		}
		
		super.setInformationMessage("Report generated successfully.");
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("commissionListReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public String generateCurrent() {
		String query = null;
		String fileName = "commissions_list";
		String fileId = null;
		this.setFromDate(new Date());
		this.setToDate(new Date());
		Timestamp fromDate = new Timestamp(DateUtil.getBeginningOfDay(this.getFromDate()).getTime());
		Timestamp toDate = new Timestamp(DateUtil.getEndOfDay(this.getToDate()).getTime());
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("reportTitle", "Effective Commissions As at "+DateUtil.convertToDateWithTime(this.getToDate()));
		parameters.put("sourceFile", fileName+".xml");
		parameters.put(EWalletConstants.JASPER_CONN, EWalletConstants.JASPER_CONN);
		try {
			query = "SELECT * FROM "+EWalletConstants.DATABASE_SCHEMA+".TARIFF, "+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE WHERE "+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE.id = "+EWalletConstants.DATABASE_SCHEMA+".TARIFF.tariffTable_id " +
			"AND "+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE.effectiveDate <= '"+fromDate+"' AND ("+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE.endDate IS NULL " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".TARIFFTABLE.endDate >= '"+toDate+"')";
	        
			fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
			
			if(fileId == null) {
				super.setInformationMessage("No results to display.");
				return "failure";
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An Error has occurred. Operation arborted.");
			return "failure";
		}
        super.setInformationMessage("Report generated successfully.");
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("commissionListReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	/**
	 * @param latestReport the latestReport to set
	 */
	public void setLatestReport(boolean latestReport) {
		this.latestReport = latestReport;
	}

	/**
	 * @return the latestReport
	 */
	public boolean isLatestReport() {
		return latestReport;
	}

	private void initializeDates() {
		Date date = new Date();
		this.setFromDate(DateUtil.getBeginningOfDay(DateUtil.add(date, Calendar.MONTH, -EWalletConstants.REPORT_MONTHS)));
		this.setToDate(DateUtil.getEndOfDay(date));
	}
	
	private void initializeDisplayableDates() {
		if(!this.isLatestReport()) {
			if(DateUtil.isDayBefore(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(DateUtil.getEndOfDay(this.getToDate()));
			} else if(DateUtil.isDayBeforeOrEqual(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(new Date());
			} else {
				this.setToDate(DateUtil.getEndOfDay(this.getToDate()));
			}
		}
	}
}
