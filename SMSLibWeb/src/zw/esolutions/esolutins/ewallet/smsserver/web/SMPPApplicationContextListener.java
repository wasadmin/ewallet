package zw.esolutions.esolutins.ewallet.smsserver.web;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import zw.co.esolutions.ewallet.sms.SmppService;

/**
 * Application Lifecycle Listener implementation class
 * SMPPApplicationContextListener
 * 
 */
public class SMPPApplicationContextListener implements ServletContextListener {
	@EJB
	private SmppService smppService;

	/**
	 * Default constructor.
	 */
	public SMPPApplicationContextListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			if (!this.smppService.loadConfiguration()) {
				System.out.println("Failed to start DONGLE.......");
				return;
			}
		} catch (Exception e) {
			System.out.println("Failed to start DONGLE.......server");
			e.printStackTrace();
			return;
		}
		System.out.println("Zvashanda");
	}

}
