package zw.co.esolutions.ewallet.util;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;

public class EmailSender {

	private static Properties config = SystemConstants.configParams;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String[] to = {"zviko.kanyume@gmail.com"};
		String from = "ewallet@zb.co.zw";
		String subj = "Ewallet Test";
		String msgText = "Just A Test\nEwallet Test\n";
		//String fileName = "D:\\MyProjects\\mail\\EmailSender\\src\\sender\\email\\EmailSender.java";
		String file1 = "/home/zviko/FirstPdf.pdf";
		String file2 = "/home/zviko/StatementPdf.pdf";
		String[] fileNames = {file1,file2};
		EmailSender sender = new EmailSender();
		sender.sendCommonsMail(to, from, subj, msgText, fileNames);
		
	}

	public boolean postMail(String[] recipients, String from, String subject,
			String message, String fileName) {
		boolean result=false;
		
		for(String s:recipients)
			System.out.println("Sending Email To:"+s);
		System.out.println("Message"+message);
		boolean debug = false;
		Session session = null;
		try {
			// Set the host smtp address
			Properties props = new Properties();
			props.put("mail.smtp.host", config.getProperty("SYSTEM_SMTP_HOST_NAME"));
			props.put("mail.smtp.port", config.getProperty("SYSTEM_SMTP_PORT"));
			props.put("mail.smtp.auth", config.getProperty("SYSTEM_SMTP_AUTH_STATUS"));

			Authenticator auth = new SMTPAuthenticator();
			
			//session = Session.getDefaultInstance(props, auth);
			session=Session.getInstance(props);

			session.setDebug(debug);

			// create message
			Message msg = new MimeMessage(session);

			// Set internet address from
			InternetAddress addressFrom = new InternetAddress(from);
			msg.setFrom(addressFrom);

			// Set addresses to
			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			// Seting the subject
			msg.setSubject(subject);
			
			
			//Create and fill in message text
			MimeBodyPart msgText = new MimeBodyPart();
			msgText.setText(message);
						
			//Create multipart and add the body parts
			MimeMultipart mp = new MimeMultipart();
			mp.addBodyPart(msgText);
			
			if(fileName!=null){
				//Set the attatchment
				MimeBodyPart msgAtt = new MimeBodyPart();
				File file = new File(fileName);
				FileDataSource fds = new FileDataSource(fileName);
				msgAtt.setHeader("Content-Type", "application/pdf");
				System.out.println("After setting content type Now sending ");
				msgAtt.setDataHandler(new DataHandler(fds));
				msgAtt.setFileName(file.getName());
				mp.addBodyPart(msgAtt);
			}
			
			//Add the multipart to the message
			msg.setContent(mp);
			Transport.send(msg);
			result=true;
			//System.out.println("Sending succeded");
		} catch (Exception e) {
			System.out.println("email error email exception ::::::::::::::::::::::::::::::"+e.getMessage());
			
			e.printStackTrace();
		}
		System.out.println("Email sending result is :::::::"+result);
		return result;
	}

	class SMTPAuthenticator extends Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
			String username = config.getProperty("SYSTEM_SMTP_USER_NAME");
			String password = config.getProperty("SYSTEM_SMTP_PASSWORD");
			return new PasswordAuthentication(username, password);
		}

	}
	
	public void sendCommonsMail(String to[],String from,String subj, String msgText,String fileNames[]){
		
		Properties props = new Properties();
		props.put("mail.smtp.host", config.getProperty("SYSTEM_SMTP_HOST_NAME"));
		props.put("mail.smtp.port", config.getProperty("SYSTEM_SMTP_PORT"));
		props.put("mail.smtp.auth", config.getProperty("SYSTEM_SMTP_AUTH_STATUS"));
		String path = config.getProperty("PROCESS_MODULE_PATH");
		
		String recipients = "";
		try{
			MultiPartEmail email = new MultiPartEmail();
			System.out.println(fileNames.length);
			for(int i=0 ;i<fileNames.length;i++){
				System.out.println(fileNames[i]);
			}
			for(int i=0 ;i<fileNames.length;i++){
				
				File file = new File(path+"pdfs/"+fileNames[i]);
				System.out.println("File path :"+file.getAbsolutePath()+" name :"+file.getName());
				// Create the attachment
				  EmailAttachment attachment = new EmailAttachment();
				  attachment.setPath(file.getAbsolutePath());
				  attachment.setDisposition(EmailAttachment.ATTACHMENT);
				  attachment.setDescription("PDF Report");
				  attachment.setName(file.getName());
				  
				  // add the attachment
				  email.attach(attachment);
			}
		
			for(int a=0;a<to.length;a++){
				recipients=to[a]+",";
			}

		  // Create the email message
			 
			  email.setHostName(config.getProperty("SYSTEM_SMTP_HOST_NAME"));
			  email.addTo(recipients);
			  email.setFrom(from);
			  email.setSubject(subj);
			  email.setMsg(msgText);
			  

			  // send the email
			  email.send();
			  System.out.println("Email Sending successful ::::::::::::::::::::: true");
			  
		  }catch (Exception e) {
			  System.out.println("Email Sending failed :::::::::::::::::::::"+e.getMessage());
			e.printStackTrace();
		}

	}
	
	
}
