package zw.co.esolutions.ewallet.process;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import zw.co.esolutions.ewallet.enums.BankAccountType;
import zw.co.esolutions.ewallet.enums.DayEndStatus;
import zw.co.esolutions.ewallet.enums.TransactionActionType;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.process.model.DayEnd;
import zw.co.esolutions.ewallet.process.model.DayEndSummary;

@Local
public interface DayEndBeanLocal {
	public DayEnd createDayEnd(DayEnd dayEnd, String userName) throws Exception;

	public DayEnd editDayEnd(DayEnd dayEnd, String userName) throws Exception;

	public DayEndSummary createDayEndSummary(DayEndSummary summary, String userName) throws Exception;

	public DayEnd findDayEndById(String dayEndId, String userName);

	public DayEndSummary editDayEndSummary(DayEndSummary summary, String userName) throws Exception;

	public DayEndSummary findDayEndSummary(String id, String userName);

	public List<DayEnd> getDayEndByTellerIdAndDayEndDate(String tellerId, Date dayEndDate);

	public DayEnd approveDayEnd(DayEnd dayEnd, String userName) throws Exception;

	public List<DayEnd> getDayEndByDayEndStatus(DayEndStatus dayEndStatus, String userName);

	public List<DayEnd> getDayEndByDayEndStatusAndDateRange(DayEndStatus dayEndStatus, Date fromDate, Date toDate, String userName);

	public List<DayEndSummary> getDayEndSummaryByDayEndId(String dayEndId, String userName);

	public List<DayEnd> getDayEndByDayEndStatusAndBranch(DayEndStatus dayEndStatus, String userName);

	public DayEnd disapproveDayEnd(DayEnd dayEnd, String userName) throws Exception;

	List<DayEndSummary> getDayEndSummariesByDayEnd(String dayEndId);

	DayEnd deleteDayEnd(DayEnd dayEnd, String userName) throws Exception;

	List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndBranch(DayEndStatus dayEndStatus, Date fromDate, Date toDate, String branch);

	List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndTeller(DayEndStatus dayEndStatus, Date fromDate, Date toDate, String teller);

	public DayEnd updateDayEnd(DayEnd dayEnd, String userName) throws Exception;

}
