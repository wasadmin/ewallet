package zw.co.esolutions.topup.ws.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import zw.co.esolutions.topup.ws.TopupUtil;
import zw.co.esolutions.topup.ws.model.TransactionInfo;
import zw.co.esolutions.topup.ws.model.TransactionInfoPK;
import zw.co.esolutions.topup.ws.util.WSRequest;

/**
 * Session Bean implementation class TopupUtilImpl
 */
@Stateless
public class TopupUtilImpl implements TopupUtil {
	@PersistenceContext(name = "TopupWebServiceEJB")
	EntityManager em;

    /**
     * Default constructor. 
     */
    public TopupUtilImpl() {

    }
    
    @Override
    public synchronized TransactionInfo findTransactionInfoByRequest(WSRequest request){
		TransactionInfo txn = null;
		try {
			txn = em.find(TransactionInfo.class, new TransactionInfoPK(request.getUuid(), request.getBankId(), request.getServiceCommand().name()));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return txn;
	}

	@Override
	public TransactionInfo createTransactionInfo(WSRequest request) {
		TransactionInfo txn = new TransactionInfo(request);
		em.persist(txn);
		return txn;
	}

	@Override
	public TransactionInfo updateTransactionInfo(TransactionInfo info) {
		return em.merge(info);
	}

}
