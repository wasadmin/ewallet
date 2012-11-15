/**
 * 
 */
package zw.co.esolutions.ewallet.reports.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;

/**
 * @author taurai
 *
 */
public class GenerateReportUtil{
	
	public final static String JASPER_FOLDER = "jasperreports/";
	public final static String SOURCE_FOLDER = "reports/";
	public final static String CACHE_DIR = "cacheDir";
	
	public final static String PDF_FOLDER = "pdfs/";
	public final static String XLS_FOLDER = "excels/";
	
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(GenerateReportUtil.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + GenerateReportUtil.class);
		}
	}
	
	private static void log4(String message) {
		LOG.debug(message);
	}
	/**
	 * 
	 */
	public GenerateReportUtil() {
		// TODO Auto-generated constructor stub
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public static Connection establishConnection() throws ClassNotFoundException {
		Connection connection = null;
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
//			InitialContext context = new InitialContext();
//			DataSource dataSource = (DataSource)context.lookup("jdbc/REPORTS");
//			
//			connection = dataSource.getConnection();
			//connection.setReadOnly(true);
			connection = DriverManager.getConnection("jdbc:db2://10.136.192.229:50002/ewallet","db2admin","PassworD");
		} catch (SQLException exception) {
				exception.printStackTrace();
		} catch (Exception e) {
				e.printStackTrace();
		}
		return connection;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public static JRResultSetDataSource executeQuery(Connection connection, String query) throws SQLException {
		log4(">>>>>>>>>>>>>>>>>>>>> Database Call Start Time = "+new Date(System.currentTimeMillis()));
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
//		int count = 0;
//		while(resultSet.next()) {
//			++count;
//			if(count == 1) {
//				return new JRResultSetDataSource(resultSet);
//			}
//		}
//		return null;
		log4(">>>>>>>>>>>>>>>>>>>>> Database Call End Time = "+new Date(System.currentTimeMillis()));
		return new JRResultSetDataSource(resultSet);

	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public static JasperPrint generatePrintViaConnection(String query, Map<String, String> map,  Map<String, String> subMap, String applicationPath) throws ClassNotFoundException, SQLException, JRException {
		log4(">>>>>>>>> In jasper Util : "+query);
	 	JasperPrint jasperPrint = null;
	 	Connection connection = null;
		try {
			String fileId =  map.get("fileId");
			Map<String, Object> parameters = GenerateReportUtil.getReportMap(map);
			
			JasperReport jasperReport;
			connection = establishConnection();
			
			if(parameters.get("datasource") != null) {
				JRResultSetDataSource dataSource = executeQuery(connection, query);
				parameters.put("TableDataSource", dataSource);
				
			}
//		if(dataSource == null) {
//			return null;
//		}
			
			//Checking Existance of System Directories and Initialization
			GenerateReportUtil.checkDirectories(applicationPath);
			
			String sourceFile = (String)map.get("sourceFile");
			log4("Source File >>>>>>>> "+sourceFile);
			String[] sourceFileTokens = sourceFile.split("\\.");
			
			//Deleting Existing Files
			GenerateReportUtil.clearDuplicateFiles(fileId+sourceFileTokens[0] + ".pdf", GenerateReportUtil.PDF_FOLDER, applicationPath);
			GenerateReportUtil.clearDuplicateFiles(fileId+sourceFileTokens[0] + ".xls", GenerateReportUtil.XLS_FOLDER, applicationPath);
			
			File jasperFile = new File(applicationPath+GenerateReportUtil.JASPER_FOLDER + sourceFileTokens[0] + ".jasper");
			if(parameters.get(EWalletConstants.SUBREPORT) != null) {
				JasperReport subreport = GenerateReportUtil.generateSubReport(subMap, applicationPath);
				parameters.put("SubreportParameter", subreport);
			}
			parameters.put("connection_tauttee", connection);
			if(parameters.get("fromDate") != null) {
				parameters.put("fromDate", DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(DateUtil.convertFromLongStringToDate(map.get("fromDate")))));
				
			}
			if(parameters.get("toDate") != null) {
				parameters.put("toDate", DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(DateUtil.convertFromLongStringToDate(map.get("toDate")))));
			}
			
			if(parameters.get("asAtDate") != null) {
				parameters.put("asAtDate", DateUtil.convertDateToTimestamp(DateUtil.convertFromLongStringToDate(map.get("asAtDate"))));
			}
			
			if(!jasperFile.exists()) {
				jasperReport = JasperCompileManager.compileReport(applicationPath+GenerateReportUtil.SOURCE_FOLDER + parameters.get("sourceFile"));
				JasperCompileManager.compileReportToFile(applicationPath+GenerateReportUtil.SOURCE_FOLDER + parameters.get("sourceFile"), applicationPath+GenerateReportUtil.JASPER_FOLDER + sourceFileTokens[0] + ".jasper");
			} else {
				jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
			}
			
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
					
			JasperExportManager.exportReportToPdfFile(jasperPrint, applicationPath+GenerateReportUtil.PDF_FOLDER + fileId+sourceFileTokens[0] + ".pdf");
			
			//Generating Excel Files
			GenerateReportUtil.xlsExport(jasperPrint, applicationPath+GenerateReportUtil.XLS_FOLDER + fileId+sourceFileTokens[0] + ".xls");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					
				}
			}
		}
		
		return jasperPrint;
		
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public static JasperPrint generatePrintViaDatasource(String query, Map<String, String> map, Map<String, String> subMap, String applicationPath) throws ClassNotFoundException, SQLException, JRException {
		log4(">>>>>>>>>>>>>>>>> In Via Datasource");
		log4(">>>>>>>>> In jasper Util : "+query);
		long initTime = 0l;
		long endTime = 0l;
		Date fromDate = null;
		Date toDate = null;
	 	JasperPrint jasperPrint = null;
	 	Connection connection = null;
		try {
			String fileId =  map.get("fileId");
			Map<String, Object> parameters = GenerateReportUtil.getReportMap(map);
			
			JasperReport jasperReport;
			connection = establishConnection();
			
			JRResultSetDataSource dataSource = executeQuery(connection, query);
			
//		if(dataSource == null) {
//			return null;
//		}
			
			//Checking Existance of System Directories and Initialization
			GenerateReportUtil.checkDirectories(applicationPath);
			
			String sourceFile = (String)map.get("sourceFile");
			log4("Source File >>>>>>>> "+sourceFile);
			String[] sourceFileTokens = sourceFile.split("\\.");
			
			//Deleting Existing Files
			GenerateReportUtil.clearDuplicateFiles(fileId+sourceFileTokens[0] + ".pdf", GenerateReportUtil.PDF_FOLDER, applicationPath);
			GenerateReportUtil.clearDuplicateFiles(fileId+sourceFileTokens[0] + ".xls", GenerateReportUtil.XLS_FOLDER, applicationPath);
			
			File jasperFile = new File(applicationPath+GenerateReportUtil.JASPER_FOLDER + sourceFileTokens[0] + ".jasper");
			
			if(parameters.get(EWalletConstants.SUBREPORT) != null) {
				JasperReport subreport = GenerateReportUtil.generateSubReport(subMap, applicationPath);
				parameters.put("SubreportParameter", subreport);
				parameters.put("connection_tauttee", connection);
			}
			if(parameters.get("fromDate") != null) {
				parameters.put("fromDate", fromDate = DateUtil.convertDateToTimestamp(DateUtil.convertFromLongStringToDate(map.get("fromDate"))));
			}
			if(parameters.get("toDate") != null) {
				parameters.put("toDate", toDate = DateUtil.convertDateToTimestamp(DateUtil.convertFromLongStringToDate(map.get("toDate"))));
			}
			if(parameters.get("asAtDate") != null) {
				parameters.put("asAtDate", DateUtil.convertDateToTimestamp(DateUtil.convertFromLongStringToDate(map.get("asAtDate"))));
			}
			
			//Virtualizer Here!!
		   if(parameters.get("fromDate") != null && parameters.get("toDate") != null 
					&& DateUtil.daysBetween(toDate, fromDate) > 3) {
				log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Creating File Virtualizer .");
				@SuppressWarnings("unused")
				JRFileVirtualizer fileVirtualizer = new JRFileVirtualizer(3, applicationPath+GenerateReportUtil.CACHE_DIR);
					//parameters.put(JRParameter.REPORT_VIRTUALIZER, fileVirtualizer);

			}
			if(!jasperFile.exists()) {
				initTime = System.currentTimeMillis();
				jasperReport = JasperCompileManager.compileReport(applicationPath+GenerateReportUtil.SOURCE_FOLDER + parameters.get("sourceFile"));
				endTime = System.currentTimeMillis();
				log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Compile Report took "+(endTime - initTime)+ " milliseconds.");
				initTime = endTime;
				JasperCompileManager.compileReportToFile(applicationPath+GenerateReportUtil.SOURCE_FOLDER + parameters.get("sourceFile"), applicationPath+GenerateReportUtil.JASPER_FOLDER + sourceFileTokens[0] + ".jasper");
				endTime = System.currentTimeMillis();
				log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Compile Report to .jasper file took "+(endTime - initTime)+ " milliseconds.");
				initTime = endTime;
			} else {
				initTime = System.currentTimeMillis();
				jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
				endTime = System.currentTimeMillis();
				log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Loading .jasper file took "+(endTime - initTime)+ " milliseconds.");
				initTime = endTime;
			}
			
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			endTime = System.currentTimeMillis();
			log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Filling Report took "+(endTime - initTime)+ " milliseconds.");
			initTime = endTime;
					
			JasperExportManager.exportReportToPdfFile(jasperPrint, applicationPath+GenerateReportUtil.PDF_FOLDER + fileId+sourceFileTokens[0] + ".pdf");
			endTime = System.currentTimeMillis();
			log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Exporting Report to PDF took "+(endTime - initTime)+ " milliseconds.");
			initTime = endTime;
			
			//Generating Excel Files
			GenerateReportUtil.xlsExport(jasperPrint, applicationPath+GenerateReportUtil.XLS_FOLDER + fileId+sourceFileTokens[0] + ".xls");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					
				}
			}
		}
		if(jasperPrint != null) {
			log4(">>>>>>>>>>>>>>>>> Successfully Generated via datasource.");
		} else {
			log4(">>>>>>>>>>>>>>>>> Report generation failed via datasource.");
		}
		return jasperPrint;
		
	}

public static JasperReport generateSubReport(Map<String, String> map, String applicationPath) throws ClassNotFoundException, SQLException, JRException {
	
	JasperReport jasperReport;
	@SuppressWarnings("unused")
	JasperPrint jasperPrint;
	
	//String fileId =  map.get("fileId");
	Map<String, Object> parameters = GenerateReportUtil.getReportMap(map);
	
    //Checking Existance of System Directories and Initialization
	GenerateReportUtil.checkDirectories(applicationPath);
	
	String sourceFile = (String)parameters.get("sourceFile");
	String[] sourceFileTokens = sourceFile.split("\\.");
	
	File jasperFile = new File(applicationPath + GenerateReportUtil.JASPER_FOLDER + sourceFileTokens[0] + ".jasper");
	if(!jasperFile.exists()) {
		if(parameters.get("fromDate") != null) {
			parameters.put("fromDate", DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(DateUtil.convertFromLongStringToDate(map.get("fromDate")))));
		}
		if(parameters.get("toDate") != null) {
			parameters.put("toDate", DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(DateUtil.convertFromLongStringToDate(map.get("toDate")))));
		} if(map.get("index") != null) {
			parameters.put("index", Integer.parseInt(map.get("index")));
		}
		jasperReport = JasperCompileManager.compileReport(applicationPath + GenerateReportUtil.SOURCE_FOLDER + parameters.get("sourceFile"));
		JasperCompileManager.compileReportToFile(applicationPath + GenerateReportUtil.SOURCE_FOLDER + parameters.get("sourceFile"), applicationPath + GenerateReportUtil.JASPER_FOLDER  + sourceFileTokens[0] + ".jasper");
	} else {
		jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
	}
	
			
	log4(">>>>>>>>>>>>>>>>Sub Report Generated!!!!!");
	return jasperReport;
	
}

	public static String getJasperUserId(String userName) {
		String fileId = null;
		fileId = userName;
		if(fileId == null) {
			fileId = "eWallet_";
		} else {
			fileId = fileId+"_";
		}
		return fileId;
	}
	
	public static Map<String, Object> getReportMap(Map<String, String> map) {
		Map<String, Object> finalMap = new HashMap<String, Object>();
		for(String key : map.keySet()) {
			finalMap.put(key, map.get(key));
		}
		return finalMap;
	}
	
	public static void checkDirectories(String applicationPath) {
		try {
			File file = new File(applicationPath + GenerateReportUtil.PDF_FOLDER);
			if(!file.exists()) {
				file.mkdir();
			}
			file = new File(applicationPath + GenerateReportUtil.JASPER_FOLDER);
			if(!file.exists()) {
				file.mkdir();
			}
			file = new File(applicationPath + GenerateReportUtil.SOURCE_FOLDER);
			if(!file.exists()) {
				file.mkdir();
			}
			
			file = new File(applicationPath + GenerateReportUtil.CACHE_DIR);
			if(!file.exists()) {
				file.mkdir();
			}
			
			file = new File(applicationPath + GenerateReportUtil.XLS_FOLDER);
			if(!file.exists()) {
				file.mkdir();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public static void clearDuplicateFiles(String fileName, String folder, String applicationPath) {
		File file = new File(applicationPath + folder + fileName);
		
		try {
			if(file.exists()) {
				file.delete();
			} 
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void xlsExport(JasperPrint jasperPrint, String filePath) 	{
		try {
				/*JExcelApiExporter xlsExporter = new JExcelApiExporter();
				//xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
								
				xlsExporter.setParameter(JRXlsExporterParameter.JASPER_PRINT,jasperPrint);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
				xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
				xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,filePath);
				xlsExporter.exportReport();
									
				
				LOG.debug(">>>>>>>>>>>>>>>>>>>Done Generating XLS file!");*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		
	public static JasperPrint generatePrintViaCollectionDatasource(String query, Map<String, String> map, String applicationPath) throws ClassNotFoundException, SQLException, JRException {
		log4(">>>>>>>>>>>>>>>>> In Via Collection Datasource");
		log4(">>>>>>>>> Query : "+query);
		long initTime = 0l;
		long endTime = 0l;
		Date fromDate = null;
		Date toDate = null;
	 	JasperPrint jasperPrint = null;
	 	Connection connection = null;
	 	Collection<? extends Object> reportList; 
		try {
			String fileId =  map.get("fileId");
			Map<String, Object> parameters = GenerateReportUtil.getReportMap(map);
			
			JasperReport jasperReport;
			connection = establishConnection();
			
			//Checking Existance of System Directories and Initialization
			GenerateReportUtil.checkDirectories(applicationPath);
			
			String sourceFile = (String)map.get("sourceFile");
			log4("Source File >>>>>>>> "+sourceFile);
			String[] sourceFileTokens = sourceFile.split("\\.");
			
			//Deleting Existing Files
			GenerateReportUtil.clearDuplicateFiles(fileId+sourceFileTokens[0] + ".pdf", GenerateReportUtil.PDF_FOLDER, applicationPath);
			GenerateReportUtil.clearDuplicateFiles(fileId+sourceFileTokens[0] + ".xls", GenerateReportUtil.XLS_FOLDER, applicationPath);
			
			File jasperFile = new File(applicationPath+GenerateReportUtil.JASPER_FOLDER + sourceFileTokens[0] + ".jasper");
			
			if(parameters.get("fromDate") != null) {
				parameters.put("fromDate", fromDate = DateUtil.convertDateToTimestamp(DateUtil.convertFromLongStringToDate(map.get("fromDate"))));
			}
			if(parameters.get("toDate") != null) {
				parameters.put("toDate", toDate = DateUtil.convertDateToTimestamp(DateUtil.convertFromLongStringToDate(map.get("toDate"))));
			}
			if(parameters.get("asAtDate") != null) {
				parameters.put("asAtDate", DateUtil.convertDateToTimestamp(DateUtil.convertFromLongStringToDate(map.get("asAtDate"))));
			}
			
			//Virtualizer Here!!
		   if(parameters.get("fromDate") != null && parameters.get("toDate") != null 
					&& DateUtil.daysBetween(toDate, fromDate) > 3) {
				log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Creating File Virtualizer .");
				@SuppressWarnings("unused")
				JRFileVirtualizer fileVirtualizer = new JRFileVirtualizer(3, applicationPath+GenerateReportUtil.CACHE_DIR);
					//parameters.put(JRParameter.REPORT_VIRTUALIZER, fileVirtualizer);

			}
			if(!jasperFile.exists()) {
				initTime = System.currentTimeMillis();
				jasperReport = JasperCompileManager.compileReport(applicationPath+GenerateReportUtil.SOURCE_FOLDER + parameters.get("sourceFile"));
				endTime = System.currentTimeMillis();
				log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Compile Report took "+(endTime - initTime)+ " milliseconds.");
				initTime = endTime;
				JasperCompileManager.compileReportToFile(applicationPath+GenerateReportUtil.SOURCE_FOLDER + parameters.get("sourceFile"), applicationPath+GenerateReportUtil.JASPER_FOLDER + sourceFileTokens[0] + ".jasper");
				endTime = System.currentTimeMillis();
				log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Compile Report to .jasper file took "+(endTime - initTime)+ " milliseconds.");
				initTime = endTime;
			} else {
				initTime = System.currentTimeMillis();
				jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
				endTime = System.currentTimeMillis();
				log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Loading .jasper file took "+(endTime - initTime)+ " milliseconds.");
				initTime = endTime;
			}
			
			reportList = CollectionDatasourceUtil.populateAccountStatementCollection(query, parameters, connection);
			
			//log4(">>>>>>>>>>>>>>>>>> Report List = "+reportList);
			JRDataSource dataSource = createBeanCollectionDataSource(reportList);
			
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			endTime = System.currentTimeMillis();
			log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Filling Report took "+(endTime - initTime)+ " milliseconds.");
			initTime = endTime;
					
			JasperExportManager.exportReportToPdfFile(jasperPrint, applicationPath+GenerateReportUtil.PDF_FOLDER + fileId+sourceFileTokens[0] + ".pdf");
			endTime = System.currentTimeMillis();
			log4(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. Exporting Report to PDF took "+(endTime - initTime)+ " milliseconds.");
			initTime = endTime;
			
//			//Generating Excel Files
//			GenerateReportUtil.xlsExport(jasperPrint, applicationPath+GenerateReportUtil.XLS_FOLDER + fileId+sourceFileTokens[0] + ".xls");
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					
				}
			}
		}
		
		if(jasperPrint != null) {
			log4(">>>>>>>>>>>>>>>>> Successfully Generated via collection datasource.");
		} else {
			log4(">>>>>>>>>>>>>>>>> Report generation failed via collection datasource.");
		}
		return jasperPrint;
		
	}
	
	public static <E> JRDataSource createBeanCollectionDataSource(Collection <E> collection){
		JRDataSource datasource = new JRBeanCollectionDataSource(collection, false);
		return datasource;
	}
}
