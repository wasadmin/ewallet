package zw.co.esolutions.ewallet.reports.downloads;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
//import com.ibm.ws.management.wsdm.resource.Servlet;

/**
 * Servlet implementation class DownloadServlet
 */
public class DownloadServlet extends  HttpServlet{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init() throws ServletException {
		System.out.println(">>>>>>>>>>>>>>>>>>>> INIT  ....");
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(">>>>>>>>>>>>>>>>>>>> GET  ....");
		String fileName = request.getParameter("fileName");
		byte[] byteArr = (byte[])request.getSession().getAttribute("pdfFile");
		
		File file;
		boolean isXls = false;
		boolean isPdf = false;
		String applicationPath  = request.getParameter("applicationPath");
		
       if(fileName != null) {
    	   if(fileName.endsWith(".pdf")) {
    		   isPdf = true; 
    		   file = new File(applicationPath+GenerateReportUtil.PDF_FOLDER+fileName);
    	   } else if(fileName.endsWith(".xls")) {
    		   isXls = true;
    		   file = new File(applicationPath+GenerateReportUtil.XLS_FOLDER+fileName);
    	   } else {
    		   file = null;
    	   }
		   ServletContext context = this.getServletConfig().getServletContext(); 
			if (context != null) {

			ServletOutputStream servletOutputStream = null;
			try {
				if(isPdf) {
					response.setContentType("application/pdf");
					response.setHeader("Content-Disposition", "attachment;filename=\""
						+ fileName + "\""); 
				} else if(isXls) {
					response.setContentType("application/ms-excel");
					response.setHeader("Content-Disposition", "attachment;filename=\""
							+ fileName + "\"");
				}
				
				if(byteArr == null){
					BufferedInputStream io = new BufferedInputStream(new FileInputStream(file));
					
					
					List<Byte> bytes = new ArrayList<Byte>();
					int b =0;
					while ( (b= io.read()) != -1){
						bytes.add((byte)b);
					}
					
					
					byteArr = new byte[bytes.size()];
					for(int i =0 ;  i< bytes.size() ; i++){
						byteArr[i] = bytes.get(i);
					}
				}
	           
				//response.setContentLength(byteArr.length);
				servletOutputStream = response.getOutputStream();
				servletOutputStream.write(byteArr);
				//servletOutputStream.flush();
				servletOutputStream.close();
				
				//Clearing File System
				DownloadServlet.clearFiles(file);
				request.getSession().removeAttribute("pdfFile");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	   }
       }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(">>>>>>>>>>>>>>>>>>>> POST  ....");
	}
	
	public static void clearFiles(File file) {
		System.out.println(">>>>>>>>>>>>>> Servlet Clear Files File >>>>> "+file);
		
		try {
			if(file != null) {
				if(file.exists()) {
					file.delete();
				} 
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean removeDirectory(File directory) {

		  // System.out.println("removeDirectory " + directory);

		  if (directory == null)
		    return false;
		  if (!directory.exists())
		    return true;
		  if (!directory.isDirectory())
		    return false;

		  String[] list = directory.list();

		  // Some JVMs return null for File.list() when the
		  // directory is empty.
		  if (list != null) {
		    for (int i = 0; i < list.length; i++) {
		      File entry = new File(directory, list[i]);

		      //        System.out.println("\tremoving entry " + entry);

		      if (entry.isDirectory())
		      {
		        if (!removeDirectory(entry))
		          return false;
		      }
		      else
		      {
		        if (!entry.delete())
		          return false;
		      }
		    }
		  }

		  return directory.delete();
		}

}
