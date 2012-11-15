package zw.co.esolutions.ewallet.process;
import java.util.Date;
import javax.ejb.Local;
import zw.co.esolutions.ewallet.enums.OwnerType;
import zw.co.esolutions.ewallet.process.model.Batch;

@Local
public interface BatchProcessingBeanLocal {

	Batch createBatch(Batch batch) throws Exception;

	Batch updateBatch(Batch batch) throws Exception;

	Batch getBatchByEntityIdAndOwnerTypeAndBatchDate(String entityId,
			OwnerType ownerType, Date batchDate);

	Batch findBatchById(String batchId);

	Batch getLatestBatchByEntityIdAndOwnerType(String entityId,
			OwnerType ownerType);

	void deleteBatch(String batchId) throws Exception;

}
