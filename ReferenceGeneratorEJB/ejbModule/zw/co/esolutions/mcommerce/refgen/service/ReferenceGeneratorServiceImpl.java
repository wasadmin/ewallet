package zw.co.esolutions.mcommerce.refgen.service;

import java.text.DecimalFormat;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import zw.co.esolutions.mcommerce.refgen.model.Counter;
import zw.co.esolutions.mcommerce.refgen.model.CounterPK;
import javax.ejb.Local;


/**
 * Session Bean implementation class ReferenceGeneratorServiceImpl
 */
@Stateless
@WebService(endpointInterface="zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorService", serviceName="ReferenceGeneratorService", portName="ReferenceGeneratorServiceSOAP")
public class ReferenceGeneratorServiceImpl implements ReferenceGeneratorService {
	@PersistenceContext
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public ReferenceGeneratorServiceImpl() {
        // TODO Auto-generated constructor stub
    }

    
    public long getNextNumberInSequence(String bankCode, String dateString, long minValue, long maxValue) {
		synchronized (ReferenceGeneratorServiceImpl.class) {
			Counter counter = em.find(Counter.class, new CounterPK(bankCode, dateString));
			long count = 1L;
			if (counter == null) {
				counter = new Counter(bankCode, count, dateString);
				if(maxValue > minValue){
					counter.setMaxValue(maxValue);
					counter.setMinValue(minValue);
				}else{
					counter.setMinValue(maxValue);
					counter.setMaxValue(minValue);
				}
				em.persist(counter);
			} else {
				if(counter.getMaxValue() == 0){
					counter.setMaxValue(1000000000L - 1);
				}
				if (counter.getCount() == counter.getMaxValue()) {
					counter.setCount(0);
				} else {
					counter.setCount(counter.getCount() + 1);
				}
				count = counter.getCount();
				em.merge(counter);
			}
			return count;
		}
	}


	public String generateUUID(String sequenceName, String prefix, String year, long minValue, long maxValue) {
		
		long id = this.getNextNumberInSequence(sequenceName, year, minValue, maxValue);
		
		DecimalFormat nineDigitIntFormat = new DecimalFormat("000000000");
		
		return (prefix + year + nineDigitIntFormat.format(id)).toUpperCase();
		
	}


//	@Override
//	public String generateAgentNumber() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
