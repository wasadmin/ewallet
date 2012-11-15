/**
 * 
 */
package zw.co.esolutions.ewallet.reports;

import javax.faces.event.ActionEvent;

import pagecode.PageCodeBase;

/**
 * @author taurai
 *
 */
public class TestBean extends PageCodeBase{

	/**
	 * 
	 */
	public TestBean() {
		// TODO Auto-generated constructor stub
	}
	
	public String generateByRange() {
		return "success";
	}
	
	public String generateCurrent() {
		System.out.println(">>>>>>>>>>>>>>>>> Tapinda in Current....");
		
		/*FacesContext context = FacesContext.getCurrentInstance(); 
		File file;
		BufferedInputStream input = null;
		HttpServletResponse response;
		try {
			file = new File("/home/taurai/IBM/rationalsdp/projects/eSolutions/zbbank1/EWalletReportsWebICE/WebContent/downloads/tariff_list.pdf");
			//Looooooo
		 	input = new BufferedInputStream(new FileInputStream(file));
		 	
		 	
			if (!context.getResponseComplete()) { 
				response = (HttpServletResponse) context.getExternalContext().getResponse();
				int contentLength = input.available();
	            response.setContentLength(contentLength);
				response.setContentType("application/pdf"); 
				response.setHeader("Content-disposition", "attachment; filename=report.pdf");
				while (contentLength-- > 0) {
	                 response.getOutputStream().write(input.read());
	             } 
			 	context.responseComplete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
     		try {
    	         input.close();
    	    } catch (IOException e) {    	                 	              	
    	   }
    	}
		*/
		return "success";
	}

	public void download(ActionEvent event){
		/*FacesContext context = FacesContext.getCurrentInstance();
		if (!context.getResponseComplete()) {

		HttpServletResponse response = (HttpServletResponse) context
		.getExternalContext().getResponse();
		ServletOutputStream servletOutputStream = null;
		try {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment;filename=\""
					+ "tariff_list.pdf" + "\""); 
           BufferedInputStream io = new BufferedInputStream(new FileInputStream(new File("/home/taurai/IBM/rationalsdp/projects/eSolutions/zbbank1/EWalletReportsWebICE/WebContent/downloads/tariff_list.pdf")));
			
			
			List<Byte> bytes = new ArrayList<Byte>();
			int b =0;
			while ( (b= io.read()) != -1){
				bytes.add((byte)b);
			}
			
			
			byte[] byteArr = new byte[bytes.size()];
			for(int i =0 ;  i< bytes.size() ; i++){
				byteArr[i] = bytes.get(i);
			}
			//response.setContentLength(byteArr.length);
			servletOutputStream = response.getOutputStream();
			servletOutputStream.write(byteArr);
			//servletOutputStream.flush();
			servletOutputStream.close();
			context.responseComplete(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//InputStream reportStream = context.getExternalContext().getResourceAsStream("/home/taurai/IBM/rationalsdp/projects/eSolutions/zbbank1/EWalletReportsWebICE/WebContent/jasperreports/tariff_list.jasper");
		System.out.println(" ServletOutputStream Stream !!!!!!!!! >>>>>>>>>>>>>>>>>>> = "+servletOutputStream);
		}
*/
	}

}
