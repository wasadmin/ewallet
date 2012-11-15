package zw.co.esolutions.ewallet.process;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import zw.co.esolutions.ewallet.enums.OwnerType;
import zw.co.esolutions.ewallet.process.model.Batch;
import zw.co.esolutions.ewallet.util.GenerateKey;

/**
 * Session Bean implementation class BatchProcessingBean
 */
@Stateless
public class BatchProcessingBean implements BatchProcessingBeanLocal {
	
	@PersistenceContext
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public BatchProcessingBean() {
        // TODO Auto-generated constructor stub
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Batch createBatch(Batch batch) throws Exception {
    	try {
			batch.setDateCreated(new Date(System.currentTimeMillis()));
			if(batch.getId() == null) {
				batch.setId(GenerateKey.generateEntityId());
			}
			em.persist(batch);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return batch;
    }
  
    //@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Batch updateBatch(Batch batch) throws Exception {
    	try {
    		batch = em.merge(batch);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return batch;
    }
    
    public Batch getBatchByEntityIdAndOwnerTypeAndBatchDate(String entityId, OwnerType ownerType, Date batchDate) {
    	String sql = "SELECT b FROM Batch b WHERE b.entityId = :entityId AND b.ownerType = :ownerType AND b.complete = :complete AND b.batchDate = :batchDate ";
    	Batch b = null;
    	try {
			Query q = em.createQuery(sql);
			q.setParameter("entityId", entityId);
			q.setParameter("ownerType", ownerType);
			q.setParameter("batchDate", batchDate);
			b = (Batch)q.getSingleResult();
		} catch (NoResultException ne) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	return b;
    }
    
    public Batch findBatchById(String batchId) {
    	Batch b = null;
    	try {
			b = em.find(Batch.class, batchId);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return b;
    }
    
    public Batch getLatestBatchByEntityIdAndOwnerType(String entityId, OwnerType ownerType) {
    	String sql = "SELECT b FROM Batch b WHERE b.entityId = :entityId AND b.ownerType = :ownerType AND b.dateCreated " +
    			"IN (SELECT MAX(b1.dateCreated) FROM Batch b1 WHERE b1.entityId = :entityId AND b1.ownerType = :ownerType)";
    	Batch b = null;
    	try {
			Query q = em.createQuery(sql);
			q.setParameter("entityId", entityId);
			q.setParameter("ownerType", ownerType);
			b = (Batch)q.getSingleResult();
		} catch (NoResultException ne) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	return b;
    }
    
    public void deleteBatch(String batchId) throws Exception {
    	try {
			Batch batch = em.find(Batch.class, batchId);
			batch = em.merge(batch);
			em.refresh(batch);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }

}
