package zw.co.esolutions.ewallet.extpayments.ws;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name="EWalletPaymentsService")
public interface EWalletPaymentsService {

	String submitRequest(@WebParam(name="iso8583xml") String xml);

}
