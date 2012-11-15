package zw.co.esolutions.mcommerce.refgen.service;
import javax.ejb.Local;
import javax.jws.WebParam;
import javax.jws.WebService;

@Local
@WebService(name = "ReferenceGeneratorService")
public interface ReferenceGeneratorService {
	long getNextNumberInSequence(@WebParam(name="sequenceName") String sequenceName, @WebParam(name="year")String year, @WebParam(name="minValue")long minValue, @WebParam(name="maxValue")long maxValue) ;
	
	public String generateUUID(@WebParam(name="sequenceName") String sequenceName, @WebParam(name="prefix") String prefix, @WebParam(name="year") String year, @WebParam(name="minValue") long minValue, @WebParam(name="maxValue") long maxValue);
	
//	public String generateAgentNumber();
}
