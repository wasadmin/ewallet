<%--
/*
 * It Works
 */
--%>

<%@ page errorPage="error.jsp" %>
<%@ page import="net.sf.jasperreports.engine.*" %>
<%@ page import="net.sf.jasperreports.engine.util.*" %>
<%@ page import="net.sf.jasperreports.engine.export.*" %>
<%@ page import="net.sf.jasperreports.j2ee.servlets.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>

<%
	JasperPrint jasperPrint = (JasperPrint)session.getAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE);
	int pageIndex = 0;
	int lastPageIndex = 0;
	String subMapString = null;
	Map<String, String> subMap  = null;
	
	String pageName = request.getParameter("pageName");
	pageName = URLEncryptor.decryptUrl(pageName);
	
	String pdfName = request.getParameter("pdfName");
	pdfName = URLEncryptor.decryptUrl(pdfName);
	
	String xlsName = pdfName.replaceAll(".pdf",".xls");
	
	StringBuffer sbuffer = new StringBuffer();
	try {
	if (request.getParameter("reload") != null || jasperPrint == null)
	{
	    String mapString = URLEncryptor.decryptUrl(request.getParameter(EWalletConstants.MASTER_REPORT));
	    Map<String, String> map = MapUtil.convertAttributesStringToMap(mapString);
	    
	    if(EWalletConstants.SUBREPORT.equalsIgnoreCase(map.get(EWalletConstants.SUBREPORT))) {
			subMapString = URLEncryptor.decryptUrl(request.getParameter(EWalletConstants.SUBREPORT));
	    	subMap = MapUtil.convertAttributesStringToMap(subMapString);
	    }
		
		if(EWalletConstants.JASPER_CONN.equalsIgnoreCase(map.get(EWalletConstants.JASPER_CONN))) {
		
			jasperPrint = GenerateReportUtil.generatePrintViaConnection(URLEncryptor.decryptUrl((String)request.getParameter("query")), map, subMap, (String)application.getRealPath("/")+"/");
		
		} else if(EWalletConstants.JASPER_COLLECTION_DATASOURSE.equalsIgnoreCase(map.get(EWalletConstants.JASPER_COLLECTION_DATASOURSE)) ){
			
			jasperPrint = GenerateReportUtil.generatePrintViaCollectionDatasource(URLEncryptor.decryptUrl((String)request.getParameter("query")), map, (String)application.getRealPath("/")+"/");
	        
		} else {
			
			jasperPrint = GenerateReportUtil.generatePrintViaDatasource(URLEncryptor.decryptUrl((String)request.getParameter("query")), map, subMap, (String)application.getRealPath("/")+"/");
	        
		}
		session.setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, null);
	}
	
	JRHtmlExporter exporter = new JRHtmlExporter();
	
	
	if (jasperPrint.getPages() != null)
	{
		lastPageIndex = jasperPrint.getPages().size() - 1;
	}

	String pageStr = request.getParameter("page");
	try
	{
		pageIndex = Integer.parseInt(pageStr);
	}
	catch(Exception e)
	{
	//e.printStackTrace();
	}
	
	if (pageIndex < 0)
	{
		pageIndex = 0;
	}

	if (pageIndex > lastPageIndex)
	{
		pageIndex = lastPageIndex;
	}
	
	
	exporter.setParameter(JRExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
	exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
	
	//exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, sbuffer);
	exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "../servlets/image?image=");
	//exporter.setParameter(JRExporterParameter.PAGE_INDEX, Integer.valueOf(pageIndex));
	//exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, " ZB MOBILE BANKING DASHBOARD");
	exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
	exporter.setParameter(JRHtmlExporterParameter.OUTPUT_WRITER, out);
	
	String copyRight = new String("\u00A9");
	Calendar cal = Calendar.getInstance();
	cal.setTime(new Date());
	exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, " "+copyRight+new Integer(cal.get(Calendar.YEAR)).toString());
	exporter.exportReport();
	} catch(Exception e) {
		e.printStackTrace();
	}
	
%>


<%@page import="zw.co.esolutions.ewallet.reports.util.GenerateReportUtil"%>
<%@page import="zw.co.esolutions.ewallet.util.MapUtil"%>
<%@page import="zw.co.esolutions.ewallet.util.EWalletConstants"%>
<%@page import="zw.co.esolutions.ewallet.util.URLEncryptor"%><html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<head>
  <style type="text/css">
    .buttons {
    background: #fff url(../theme/images/skin/shadow.jpg) bottom repeat-x;
    border: 1px solid #ccc;
    color: #666;
    font-size: 10px;
    margin-top: 5px;
    overflow: hidden;
    padding: 0;
	}	
  </style>
  <link href="../theme/stylesheet.css" rel="stylesheet" type="text/css" />
  <title>DASHBOARD</title>
</head>

<body text="#000000" link="#000000" alink="#000000" vlink="#000000" style="width:100%;max-width:960px;height:100%;margin:0 auto">
<div class="buttons">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
<tr>
  <td width="50%">&nbsp;</td>
  <td align="left">
    <table width="100%" cellpadding="5" cellspacing="20" border="0">
      <tr>
      <td>
      	<form action="/EWalletReportsWebICE/DownloadServlet" method="get"> <p/><br>
   			<a href="/EWalletReportsWebICE/DownloadServlet?fileName=<%= pdfName %>&applicationPath=<%= (String)application.getRealPath("/")+"/" %>">
   			<img alt="PDF" src="../theme/images/pdf.png"/></a> 
		</form>	
      </td>
      <td>&nbsp;&nbsp;&nbsp;</td>
      <td>
      	<form action="/EWalletReportsWebICE/DownloadServlet" method="get"> <p/><br>
   			<a href="/EWalletReportsWebICE/DownloadServlet?fileName=<%= xlsName %>&applicationPath=<%= (String)application.getRealPath("/")+"/" %>">
   			<img alt="EXCEL" src="../theme/images/excel.png"/></a> 
		</form>
      </td>
      <td>&nbsp;&nbsp;&nbsp;</td>
      <td>
      	<form action="/EWalletReportsWebICE/DispatchingServlet" method="get"> <p/><br>
   			<a href="/EWalletReportsWebICE/DispatchingServlet?pageName=<%= pageName %>"> <span class="commandButton">BACK</span></a> 
		</form>	
      </td>
     </tr>
    </table>
   </td>
  <td width="50%">&nbsp;</td>
</tr>
</table>
</div>
</body>
</html>
