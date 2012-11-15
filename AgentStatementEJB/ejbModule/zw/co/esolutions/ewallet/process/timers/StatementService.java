package zw.co.esolutions.ewallet.process.timers;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name="StatementService")
public interface StatementService {
	
	public boolean checkTimer(@WebParam(name="description") String description);
}
