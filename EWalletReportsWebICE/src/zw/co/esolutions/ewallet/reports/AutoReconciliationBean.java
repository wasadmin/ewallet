/**
 * 
 */
package zw.co.esolutions.ewallet.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.enums.TransactionCategory;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.InputFile;

/**
 * @author taurai
 *
 */
public class AutoReconciliationBean extends PageCodeBase{


	private Date fromDate;
	private Date toDate;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<SelectItem> branchMenu;
	private String branchItem;
	private List<Bank> bankList;
	private List<BankBranch> bankBranchList;
	private boolean disableBranchMenu;
	private String sourceAccountNumber;
	private Profile profile;
	private BankBranch bankBranch;
	private FileInfo currentFile;
	private int fileProgress;
	private List<SelectItem> accTypeMenu;
	private String accTypeItem;
	private List<BankAccount> bankAccountList;
	private String transactionItem;
	private List<SelectItem> transactionMenu;
	
		
	/**
	 * 
	 */
	public AutoReconciliationBean() {
		this.initializeDates();
		if(this.getBankBranch() != null) {
			this.setBankItem(this.getBankBranch().getBank().getId());
			this.setBranchItem("all");
		}
		
	}
	
	
	public void handleBankValueChange(ValueChangeEvent event) {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		String item = (String)event.getNewValue();
		if(item != null) {
			System.out.println(">>>>>>>>>>>>>>>>>>> Bank Changed to ID = "+item);
			try {
				if(!(item.equalsIgnoreCase("all") || item.equalsIgnoreCase("none"))) {
					System.out.println(">>>>>>>>>>>>>>>>>>>> Real IDs");
					this.setBankBranchList(bankService.getBankBranchByBank(item));
					if(this.getBankBranchList() == null || this.getBankBranchList().isEmpty()) {
						System.out.println(">>>>>>>>>>>>>> No Branches");
						this.setDisableBranchMenu(true);
						this.setBranchMenu(new ArrayList<SelectItem>());
						this.getBranchMenu().add(new SelectItem("nothing", "No Branches"));
					} else {
						System.out.println(">>>>>>>>>>>> Branches Found");
						this.setDisableBranchMenu(false);
						this.setBranchMenu(new ArrayList<SelectItem>());
						this.getBranchMenu().add(new SelectItem("all", "ALL"));
						for(BankBranch bb : this.getBankBranchList()) {
							this.getBranchMenu().add(new SelectItem(bb.getId(), bb.getName()));
						}
						
					}
				} else {
					System.out.println(">>>>>>>>>>>>>>>>>>>> All Banks selected");
					this.setDisableBranchMenu(true);
					this.setBranchMenu(null);
										
				}
				
				this.setAccTypeMenu(null);
				
								
			} catch (Exception e) {
				
			}
		}
	}
	
	
	public void handleBranchValueChange(ValueChangeEvent event) {
		String item = (String)event.getNewValue();
		if(item != null) {
			try {
				System.out.println(">>>>>>>>>>>>> Branch Selected = "+item);
				if(!(item.equalsIgnoreCase("all") || item.equalsIgnoreCase("none"))) {
					
					this.loadBankAccounts(this.getBankItem(), item);
					System.out.println(">>>>>>>>>>>> Accounts Loaded ");
					this.setAccTypeMenu(new ArrayList<SelectItem>());
					
					if(this.getBankAccountList() == null || this.getBankAccountList().isEmpty()) {
						this.getAccTypeMenu().add(new SelectItem("nothing", "No Accounts"));
					} else {
						System.out.println(">>>>>>>>>>>>>>>> Branches not null");
						String acLabel = "";
						String accNumber = "";
						for(BankAccount ac : this.getBankAccountList()) {
							acLabel = ac.getType().toString();
							this.getAccTypeMenu().add(new SelectItem(ac.getId(), acLabel.replace("_", " ")));
							accNumber = ac.getAccountNumber();
						}
						this.setSourceAccountNumber(accNumber);
					}
					
				} else {
					System.out.println(">>>>>>>>>>>>>>>>>>> All Branches selected");
					this.setAccTypeMenu(null);
					
				}
				
			} catch (Exception e) {
				
			}
		}
	}
	
	public void handleAccountTypeValueChange(ValueChangeEvent event) {
		String item = (String)event.getNewValue();
		if(item != null) {
			try {
				BankAccount ac = new BankServiceSOAPProxy().findBankAccountById(item);
				this.setSourceAccountNumber(ac.getAccountNumber());
				
			} catch (Exception e) {
				
			}
		}
	}
	
	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int initialLength = buffer.length();
		if(this.getBankItem().equalsIgnoreCase("none")) {
			buffer.append("Bank, ");
		} if( !this.getBankItem().equalsIgnoreCase("all")) {
			if(this.getBranchItem().equalsIgnoreCase("none")) {
				buffer.append("Branch, ");
			} 
		} 
		if(this.getFromDate() == null) {
			buffer.append("From Date, ");
			
		}
		if(this.getToDate() == null) {
			buffer.append("To Date, ");
		}
		if(this.getCurrentFile() == null) {
			buffer.append("CSV File, ");
		}
		int length = buffer.toString().length();
				
		if(length > initialLength) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public List<SelectItem> getBankMenu() {
		if(this.bankMenu == null) {
			bankMenu = new ArrayList<SelectItem>();
			if(this.getBankList() == null || this.getBankList().isEmpty()) {
				bankMenu.add(new SelectItem("nothing", "No Banks"));
			} else {
				bankMenu.add(new SelectItem("none", "<--select-->"));
				//bankMenu.add(new SelectItem("all", "All"));
				for(Bank bk : this.getBankList()) {
					bankMenu.add(new SelectItem(bk.getId(),bk.getName()));
				}
			}
		}
		return bankMenu;
	}
	public void setBankMenu(List<SelectItem> bankMenu) {
		this.bankMenu = bankMenu;
	}
	public String getBankItem() {
		return bankItem;
	}
	public void setBankItem(String bankItem) {
		this.bankItem = bankItem;
	}
	public List<SelectItem> getBranchMenu() {
		if(this.branchMenu == null) {
			this.branchMenu = new ArrayList<SelectItem>();
			if(this.getBankBranch() == null) {
				this.branchMenu.add(new SelectItem("none", "<--select-->"));
			} else {
				this.setDisableBranchMenu(false);
				this.setBranchMenu(new ArrayList<SelectItem>());
				this.getBranchMenu().add(new SelectItem("all", "ALL"));
				for(BankBranch bb : this.getBankBranchList()) {
					this.getBranchMenu().add(new SelectItem(bb.getId(), bb.getName()));
				}
				
				this.setAccTypeMenu(null);
				
			}
		}
		return branchMenu;
	}
	public void setBranchMenu(List<SelectItem> branchMenu) {
		this.branchMenu = branchMenu;
	}
	public String getBranchItem() {
		return branchItem;
	}
	public void setBranchItem(String branchItem) {
		this.branchItem = branchItem;
	}

	public List<Bank> getBankList() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		if(this.bankList == null || this.bankList.isEmpty()) {
			try {
				List<Bank> banks = bankService.getBank();
				if(banks != null && !banks.isEmpty())  {
					bankList = new ArrayList<Bank>();
					for(Bank bk : banks) {
						if(bk.getName().contains(EWalletConstants.SYSTEM_BANKS_DELIMITER)) {
							bankList.add(bk);
						}
					}
				}
			} catch (Exception e) {
				
			}
		}
		return bankList;
	}

	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}

	public List<BankBranch> getBankBranchList() {
		if((this.bankBranchList == null || this.bankBranchList.isEmpty()) && this.getBankBranch() != null) {
			try {
				this.bankBranchList = new BankServiceSOAPProxy().getBankBranchByBank(this.getBankBranch().getBank().getId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bankBranchList;
	}

	public void setBankBranchList(List<BankBranch> bankBranchList) {
		this.bankBranchList = bankBranchList;
	}

	public void setDisableBranchMenu(boolean disableBranchMenu) {
		this.disableBranchMenu = disableBranchMenu;
	}

	public boolean isDisableBranchMenu() {
		return disableBranchMenu;
	}

	/**
	 * @param sourceAccountNumber the sourceAccountNumber to set
	 */
	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
	}

	/**
	 * @return the sourceAccountNumber
	 */
	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}

	private void initializeDates() {
		try {
			/*this.setFromDate(DateUtil.getBusinessDayBeginningOfDay(DateUtil.add(new Date(), Calendar.DAY_OF_MONTH, -1)));
			this.setToDate(DateUtil.getBusinessDayEndOfDay(new Date()));*/
			this.setFromDate(DateUtil.getBeginningOfDay(new Date()));
			this.setToDate(DateUtil.getEndOfDay(new Date()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		if(this.profile == null) {
			try {
				ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
				this.profile = ps.getProfileByUserName(super.getJaasUserName());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return profile;
	}

	/**
	 * @param bankBranch the bankBranch to set
	 */
	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	/**
	 * @return the bankBranch
	 */
	public BankBranch getBankBranch() {
		if(this.bankBranch == null && this.getProfile() != null) {
			try {
				BankServiceSOAPProxy bs = new BankServiceSOAPProxy();
				this.bankBranch = bs.findBankBranchById(this.getProfile().getBranchId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bankBranch;
	}
	
	public FileInfo getCurrentFile() {
		return currentFile;
	}

    public int getFileProgress() {
        return fileProgress;
    }
    
    /**
	 * @param accTypeItem the accTypeItem to set
	 */
	public void setAccTypeItem(String accTypeItem) {
		this.accTypeItem = accTypeItem;
	}
	/**
	 * @return the accTypeItem
	 */
	public String getAccTypeItem() {
		return accTypeItem;
	}
	/**
	 * @param accTypeMenu the accTypeMenu to set
	 */
	public void setAccTypeMenu(List<SelectItem> accTypeMenu) {
		this.accTypeMenu = accTypeMenu;
	}
	/**
	 * @return the accTypeMenu
	 */
	public List<SelectItem> getAccTypeMenu() {
		if(accTypeMenu == null) {
			accTypeMenu = new ArrayList<SelectItem>();
			this.loadBankAccounts(null, null);
			if(this.getBankAccountList() == null || this.getBankAccountList().isEmpty()) {
				accTypeMenu.add(new SelectItem("nothing", "No Accounts"));
			} else {
				try {
					BankServiceSOAPProxy bs = new BankServiceSOAPProxy();
					String acLabel = "";
					String accNumber = "";
					for(BankAccount ac : this.getBankAccountList()) {
						
						Bank bank =  bs.findBankById(ac.getAccountHolderId());
						
						if("all".equalsIgnoreCase(this.getBankItem())) {
							acLabel = bank.getName()+" : "+ac.getType().toString();
						} else {
							acLabel = ac.getType().toString();
						}
						accNumber = ac.getAccountNumber();
						
						accTypeMenu.add(new SelectItem(ac.getId(), acLabel.replace("_", " ")));
					}
					this.setSourceAccountNumber(accNumber);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return accTypeMenu;
	}
    


	public List<BankAccount> getBankAccountList() {
		return bankAccountList;
	}


	public void setBankAccountList(List<BankAccount> bankAccountList) {
		this.bankAccountList = bankAccountList;
	}




	public String getTransactionItem() {
		if(this.transactionItem == null) {
			this.transactionItem = TransactionCategory.ALL.name();
		}
		return transactionItem;
	}


	public void setTransactionItem(String transactionItem) {
		this.transactionItem = transactionItem;
	}


	public List<SelectItem> getTransactionMenu() {
		if(this.transactionMenu == null) {
			this.transactionMenu = new ArrayList<SelectItem>();
			
			for(TransactionCategory cat : super.getTransactionCategory()) {
				this.transactionMenu.add(new SelectItem(cat.name(), cat.name()));
			}
		}
		return transactionMenu;
	}


	public void setTransactionMenu(List<SelectItem> transactionMenu) {
		this.transactionMenu = transactionMenu;
	}


	public void uploadActionListener(ActionEvent actionEvent) {
		 InputFile inputFile =(InputFile) actionEvent.getSource();
	        FileInfo fileInfo = inputFile.getFileInfo();
	        String componentStatus = "";
	        boolean isInforMsg = false;
	        //file has been saved
	        if (fileInfo.isSaved()) {
	        	this.currentFile = fileInfo;
	        	isInforMsg = true;
	        	componentStatus = "File uploaded successfully";
	        }
	        //upload failed, generate custom messages
	        if (fileInfo.isFailed()) {
	            if(fileInfo.getStatus() == FileInfo.INVALID){
	                componentStatus = "Invalid File.";
	                
	            }
	            if(fileInfo.getStatus() == FileInfo.SIZE_LIMIT_EXCEEDED){
	                componentStatus = "Maximum file size (10MB) limit exceeded.";
	            }
	            if(fileInfo.getStatus() == FileInfo.INVALID_CONTENT_TYPE){
	                componentStatus = "Invalid content type.";
	            }
	            if(fileInfo.getStatus() == FileInfo.INVALID_NAME_PATTERN){
	                componentStatus = "Invalid file format. Required :  .csv .";
	            }
	        }
	        if(isInforMsg) {
	        	super.setInformationMessage(componentStatus);
	        } else {
	        	super.setErrorMessage(componentStatus);
	        }
	}

    /**
     * <p>This method is bound to the inputFile component and is executed
     * multiple times during the file upload process.  Every call allows
     * the transactions to finds out what percentage of the file has been uploaded.
     * This progress information can then be used with a progressBar component
     * for transactions feedback on the file upload progress. </p>
     *
     * @param event holds a InputFile object in its source which can be probed
     *              for the file upload percentage complete.
     */

	public void progressListener(EventObject event) {
        InputFile ifile = (InputFile) event.getSource();
        fileProgress = ifile.getFileInfo().getPercent();
	}

	
	public String submitRequest() {
			
			String msg = this.checkAttributes();
			if(msg != null) {
				//report error
				super.setErrorMessage(msg);
				return "failure";
			}
			
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
						
			String fileId = null;
			String orderBy = null;
			String queryExtension = null;
			String queryExtension2 = null;
			boolean isMainTxn = false;
			boolean isChargeTxn = false;
			boolean isAdjustmentTxn = false;
			boolean isAllTxns = false;
			String fileName = "auto_reconciliation_report";
			String uploadFileName = "";
			//String msg = this.checkAttributes();
			String query = null;
			String query2 = null;
			//String branchQuery = "(SELECT bc.id FROM "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc) ";
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("sourceFile", fileName+".xml");
			uploadFileName = this.getCurrentFile().getFileName();
			System.out.println(">>>>>>>>>>>>>>> Uploaded CSV = "+uploadFileName);
			parameters.put("upload", uploadFileName);
			parameters.put(EWalletConstants.JASPER_COLLECTION_DATASOURSE, EWalletConstants.JASPER_COLLECTION_DATASOURSE);
			parameters.put("TXN_CATEGORY", this.getTransactionItem());
			
			if(TransactionCategory.MAIN.equals(TransactionCategory.valueOf(this.getTransactionItem()))) {
				isMainTxn = true;
			} else if(TransactionCategory.CHARGE.equals(TransactionCategory.valueOf(this.getTransactionItem()))) {
				isChargeTxn = true;
			} else if(TransactionCategory.ADJUSTMENT.equals(TransactionCategory.valueOf(this.getTransactionItem()))) {
				isAdjustmentTxn = true;
			} else if(TransactionCategory.ALL.equals(TransactionCategory.valueOf(this.getTransactionItem()))) {
				isAllTxns = true;
			}
			
			
			if (this.getBranchItem() != null) {
				if (this.getBankItem().equalsIgnoreCase("nothing")
						|| this.getBranchItem().equalsIgnoreCase("nothing")) {
					super
							.setErrorMessage("You cannot continue without Bank and/or Branch selected. Consult your adminstrator.");
					return "failure";
				}
				
			}
			
			
			//Start Appending Queries
			
			//Dates
			queryExtension = "AND ( " +
					"(p.valueDate IS NOT NULL AND p.valueDate >= '"+DateUtil.convertDateToTimestamp(this.getFromDate())+"' " +
								"AND p.valueDate <= '"+DateUtil.convertDateToTimestamp(this.getToDate())+"' ) OR " +
					"(p.valueDate IS NULL AND p.dateCreated >= '"+DateUtil.convertDateToTimestamp(this.getFromDate())+"' " +
								"AND p.dateCreated <= '"+DateUtil.convertDateToTimestamp(this.getToDate())+"' ) " +
								") "+
					"AND p.id = ts.TRANSACTION_ID AND ts.status = '"+TransactionStatus.BANK_REQUEST+"' ";
			
			queryExtension = queryExtension + "AND p.status <> '"+TransactionStatus.DRAFT+"' AND p.status <> '"+TransactionStatus.FAILED+"' ";
			
			if(isAllTxns) {
				queryExtension2 = queryExtension;
			}
			try {
				BankAccount bankAccount = bankService.findBankAccountById(this.getAccTypeItem());
				String accountString = bankAccount.getType().toString().replace("_", " ");
				
				if(isMainTxn) {
					queryExtension = queryExtension+ "AND (p.DESTINATIONEQ3ACCOUNTNUMBER = '"+bankAccount.getAccountNumber()+"' OR p.SOURCEEQ3ACCOUNTNUMBER = '"+bankAccount.getAccountNumber()+"' ) "+
					"AND p.id NOT LIKE 'A%' ";
				} else if(isChargeTxn) {
					queryExtension = queryExtension+ "AND tc.TRANSACTIONTYPE = '"+TransactionType.TARIFF+"' ";
					
					if(BankAccountType.TARIFFS_CONTROL.equals(bankAccount.getType())) {
						queryExtension = queryExtension + "AND (tc.FROMEQ3ACCOUNT IS NULL OR tc.FROMEQ3ACCOUNT = '"+bankAccount.getAccountNumber()+"' OR tc.TOEQ3ACCOUNT = '"+bankAccount.getAccountNumber()+"' ) ";
					} else {
						queryExtension = queryExtension + "AND (tc.FROMEQ3ACCOUNT = '"+bankAccount.getAccountNumber()+"' OR tc.TOEQ3ACCOUNT = '"+bankAccount.getAccountNumber()+"' ) ";
					}
				}else if(isAdjustmentTxn) {
					queryExtension = queryExtension+ "AND (p.DESTINATIONEQ3ACCOUNTNUMBER = '"+bankAccount.getAccountNumber()+"' OR p.SOURCEEQ3ACCOUNTNUMBER = '"+bankAccount.getAccountNumber()+"' ) " +
							"AND p.id LIKE 'A%' ";
				} else if(isAllTxns) {
					
					//Main
					queryExtension = queryExtension+ "AND (p.DESTINATIONEQ3ACCOUNTNUMBER = '"+bankAccount.getAccountNumber()+"' OR p.SOURCEEQ3ACCOUNTNUMBER = '"+bankAccount.getAccountNumber()+"' ) ";
					
					//Charges
					queryExtension2 = queryExtension2+ "AND tc.TRANSACTIONTYPE = '"+TransactionType.TARIFF+"' ";
					
					if(BankAccountType.TARIFFS_CONTROL.equals(bankAccount.getType())) {
						queryExtension2 = queryExtension2 + "AND (tc.FROMEQ3ACCOUNT IS NULL OR tc.FROMEQ3ACCOUNT = '"+bankAccount.getAccountNumber()+"' OR tc.TOEQ3ACCOUNT = '"+bankAccount.getAccountNumber()+"' ) ";
					} else {
						queryExtension2 = queryExtension2 + "AND (tc.FROMEQ3ACCOUNT = '"+bankAccount.getAccountNumber()+"' OR tc.TOEQ3ACCOUNT = '"+bankAccount.getAccountNumber()+"' ) ";
					}
				}
				
				parameters.put("fromEQDate", DateUtil.convertDateToEquationDateString(this.getFromDate()));
				parameters.put("toEQDate", DateUtil.convertDateToEquationDateString(this.getToDate()));
				
				parameters.put("accountNumber", bankAccount.getAccountNumber());
				
				List<Bank> banks = new BankServiceSOAPProxy().getBank();
				if(banks != null && !banks.isEmpty()) {
					for(Bank bk : banks) {
						if(bk.getName().contains(EWalletConstants.SYSTEM_BANKS_DELIMITER)) {
							parameters.put(bk.getId(), bk.getName());
						}
					}
				}
				if(super.getJaasUserName() != null) {
					BankBranch branch = new BankServiceSOAPProxy().findBankBranchById(new ProfileServiceSOAPProxy().getProfileByUserName(super.getJaasUserName()).getBranchId());
					if(branch != null) {
						parameters.put("bankName", branch.getBank().getName());
					}
				}
				//Checking for To Date to Display
				//this.initializeDisplayableDates();
				
				if(this.getBankItem().equalsIgnoreCase("all")) {
					
					// No support for all banks
					
				} else {
					parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
					if(this.getBranchItem().equalsIgnoreCase("all")) {
						
						parameters = super.populateSMS(parameters, this.getBankItem());
						parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
								"Auto-Reconciliation ["+accountString+" : "+bankAccount.getAccountNumber()+"]");
						parameters.put("pageHeader", "All "+parameters.get(this.getBankItem()).toString()+
								" Branches Auto-Reconciliation Report from "+DateUtil.convertToDateWithTime(this.getFromDate())+
								" to "+DateUtil.convertToDateWithTime(this.getToDate()));
						
						//Clear BankId Parameter
						parameters.remove(this.getBankItem());
						
						if(isMainTxn) {
							query = "SELECT   p.DESTINATIONEQ3ACCOUNTNUMBER as toAccount, p.SOURCEEQ3ACCOUNTNUMBER as fromAccount, p.id as messageId, p.amount, p.transactionType, p.valueDate, p.dateCreated as transactionDate, p.status FROM " +
									""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
									""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
									""+EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONSTATE AS ts, " +
									EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p WHERE " +
									" (b.id = p.fromBankId) " +
									"AND fromBankId = '"+this.getBankItem()+"' "+
									"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
						} else if(isChargeTxn) {
							
							query = "SELECT  tc.FROMEQ3ACCOUNT as fromAccount, tc.TOEQ3ACCOUNT as toAccount, tc.id as messageId, tc.tariffAmount as amount, tc.transactionType, p.valueDate, p.dateCreated as transactionDate, p.status FROM " +
								""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
								""+EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONSTATE AS ts, " +
								""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
								EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONCHARGE AS tc LEFT OUTER JOIN "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p ON tc.PROCESSTRANSACTION = p.ID WHERE " +
								" (b.id = p.fromBankId) " +
								"AND fromBankId = '"+this.getBankItem()+"' "+
								"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
						} else if(isAllTxns) {
							
							//Main
							query = "SELECT   p.DESTINATIONEQ3ACCOUNTNUMBER as toAccount, p.SOURCEEQ3ACCOUNTNUMBER as fromAccount, p.id as messageId, p.amount, p.transactionType, p.valueDate, p.dateCreated as transactionDate, p.status FROM " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
							""+EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONSTATE AS ts, " +
							EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p WHERE " +
							" (b.id = p.fromBankId) " +
							"AND fromBankId = '"+this.getBankItem()+"' "+
							"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
							
							//Charge
							query2 = "SELECT  tc.FROMEQ3ACCOUNT as fromAccount, tc.TOEQ3ACCOUNT as toAccount, tc.id as messageId, tc.tariffAmount as amount, tc.transactionType, p.valueDate, p.dateCreated as transactionDate, p.status FROM " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
							""+EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONSTATE AS ts, " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
							EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONCHARGE AS tc LEFT OUTER JOIN "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p ON tc.PROCESSTRANSACTION = p.ID WHERE " +
							" (b.id = p.fromBankId) " +
							"AND fromBankId = '"+this.getBankItem()+"' "+
							"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
						}
								
						
						//orderBy ="ORDER BY CASE  WHEN p.transactionLocationId IN "+branchQuery+" THEN bc.name ELSE p.transactionLocationId end ASC, p.transactionLocationType ASC, p.dateCreated DESC";
						
					} else {
						BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
						parameters.put(bb.getId(), bb.getName());
						parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
						"Auto-Reconciliation Report ["+accountString+" : "+bankAccount.getAccountNumber()+"]");
				        parameters.put("pageHeader",parameters.get(this.getBankItem()).toString()+
						" : "+bankService.findBankBranchById(this.getBranchItem()).getName()+" Branch Auto-Reconciliation Report from "+DateUtil.convertToDateWithTime(this.getFromDate())+
						" to "+DateUtil.convertToDateWithTime(this.getToDate()));
				        

				        if(isMainTxn) {
					        query = "SELECT  p.DESTINATIONEQ3ACCOUNTNUMBER as toAccount, p.SOURCEEQ3ACCOUNTNUMBER as fromAccount, p.id as messageId, p.amount, p.transactionType, p.valueDate, p.dateCreated as transactionDate, p.status FROM " +
								""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
								""+EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONSTATE AS ts, " +
								""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
								EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p WHERE " +
								" (b.id = p.fromBankId) " +
								"AND fromBankId = '"+this.getBankItem()+"' "+
								"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
					        
				        } else if(isChargeTxn) {
				        	query = "SELECT  tc.FROMEQ3ACCOUNT as fromAccount, tc.TOEQ3ACCOUNT as toAccount, tc.id as messageId, tc.tariffAmount as amount, tc.transactionType, p.valueDate, p.dateCreated as transactionDate, p.status FROM " +
								""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
								""+EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONSTATE AS ts, " +
								""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
								EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONCHARGE AS tc LEFT OUTER JOIN "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p ON tc.PROCESSTRANSACTION = p.ID WHERE " +
								" (b.id = p.fromBankId) " +
								"AND fromBankId = '"+this.getBankItem()+"' "+
								"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
				        } else if(isAllTxns) {
					        
				        	//Main
				        	query = "SELECT  p.DESTINATIONEQ3ACCOUNTNUMBER as toAccount, p.SOURCEEQ3ACCOUNTNUMBER as fromAccount, p.id as messageId, p.amount, p.transactionType, p.valueDate, p.dateCreated as transactionDate, p.status FROM " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
							""+EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONSTATE AS ts, " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
							EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p WHERE " +
							" (b.id = p.fromBankId) " +
							"AND fromBankId = '"+this.getBankItem()+"' "+
							"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
				        	
				        	//Charge
				        	query2 = "SELECT  tc.FROMEQ3ACCOUNT as fromAccount, tc.TOEQ3ACCOUNT as toAccount, tc.id as messageId, tc.tariffAmount as amount, tc.transactionType, p.valueDate, p.dateCreated as transactionDate, p.status FROM " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
							""+EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONSTATE AS ts, " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
							EWalletConstants.DATABASE_SCHEMA+".TRANSACTIONCHARGE AS tc LEFT OUTER JOIN "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p ON tc.PROCESSTRANSACTION = p.ID WHERE " +
							" (b.id = p.fromBankId) " +
							"AND fromBankId = '"+this.getBankItem()+"' "+
							"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
				        	
				        	 //orderBy ="ORDER BY CASE  WHEN p.transactionLocationId IN "+branchQuery+" THEN bc.name ELSE p.transactionLocationId end ASC, p.transactionLocationType ASC, p.dateCreated DESC";
				        
			        } 
						
				        
						
					}
				}
				orderBy = "";
				
				if(queryExtension != null) {
					query = query + queryExtension + orderBy;
				} 
				
				if(isAllTxns) {
					query2 = query2 + queryExtension2;
					parameters.put("query", query2);
				} else {
					parameters.put("query", "empty");
				}
				
				fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
				parameters.put("fileId", fileId);
				
				if(fileId == null) {
					super.setInformationMessage("No results to display.");
					return "failure";
				}
				
				super.setInformationMessage("Report successfully generated.");
			} catch (Exception e) {
				e.printStackTrace();
				super.setErrorMessage("An error has occured. Report not generated.");
				return "failure";
			}
			//query = "SELECT p.id as messageId, p.amount, p.transactionType FROM BANKIF.BANKBRANCH AS bc, BANKIF.BANK AS b, BANKIF.PROCESSTRANSACTION AS p WHERE  (b.id = p.fromBankId) AND fromBankId = '589003' AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) AND p.dateCreated >= '2012-04-10 18:59:00.0' AND p.dateCreated <= '2012-04-11 19:00:00.0' AND (p.transactionType IN ( 'DEPOSIT', 'WITHDRAWAL', 'BANKACCOUNT_TO_BANKACCOUNT_TRANSFER', 'BANKACCOUNT_TO_EWALLET_TRANSFER', 'BANKACCOUNT_TO_NONHOLDER_TRANSFER', 'BILLPAY', 'COMMISSION_TRANSFER', 'EWALLET_TO_BANKACCOUNT_TRANSFER', 'EWALLET_BILLPAY', 'EWALLET_TO_EWALLET_TRANSFER', 'EWALLET_TO_NON_HOLDER_TRANSFER', 'EWALLET_TOPUP', 'TOPUP', 'AGENT_CUSTOMER_WITHDRAWAL', 'AGENT_EMONEY_TRANSFER', 'AGENT_CUSTOMER_DEPOSIT', 'AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL', 'AGENT_CASH_DEPOSIT', 'AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER', 'ADJUSTMENT', 'WITHDRAWAL_NONHOLDER') ) AND p.status = 'COMPLETED' ORDER BY CASE  WHEN p.transactionLocationId IN (SELECT bc.id FROM BANKIF.BANKBRANCH AS bc)  THEN bc.name ELSE p.transactionLocationId end ASC, p.transactionLocationType ASC, p.dateCreated DESC ";
			
			ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
			try {
				ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("autoReconcile.jspx"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.cleanBean();
			
			return "success";
	}
	
	private void loadBankAccounts(String bankId, String branchId) {
		try {
			if(branchId != null) {
				BankServiceSOAPProxy bs = new BankServiceSOAPProxy();
				List<BankAccount> accs = bs.getBankAccountByAccountHolderIdAndOwnerType(branchId, OwnerType.BANK_BRANCH);
				if(accs == null || accs.isEmpty()) {
					this.setBankAccountList(null);
				} else {
					this.setBankAccountList(new ArrayList<BankAccount>());
					for(BankAccount ac : accs) {
						if(BankAccountStatus.ACTIVE.equals(ac.getStatus())) {
							this.getBankAccountList().add(ac);
						}
					}
				}
				return;
			}
			if("all".equalsIgnoreCase(this.getBankItem())) {
				BankServiceSOAPProxy bs = new BankServiceSOAPProxy();
				this.setBankAccountList(bs.getBankAccountByStatusAndOwnerType(BankAccountStatus.ACTIVE, OwnerType.BANK));
				return;
			}
			
			if("all".equalsIgnoreCase(this.getBranchItem())) {
				BankServiceSOAPProxy bs = new BankServiceSOAPProxy();
				List<BankAccount> accs = bs.getBankAccountByAccountHolderIdAndOwnerType(this.getBankItem(), OwnerType.BANK);
				if(accs == null || accs.isEmpty()) {
					this.setBankAccountList(null);
				} else {
					this.setBankAccountList(new ArrayList<BankAccount>());
					for(BankAccount ac : accs) {
						if(BankAccountStatus.ACTIVE.equals(ac.getStatus())) {
							this.getBankAccountList().add(ac);
						}
					}
				}
				return;
			}
			
			if(!"all".equalsIgnoreCase(this.getBranchItem())) {
				BankServiceSOAPProxy bs = new BankServiceSOAPProxy();
				List<BankAccount> accs = bs.getBankAccountByAccountHolderIdAndOwnerType(this.getBranchItem(), OwnerType.BANK_BRANCH);
				if(accs == null || accs.isEmpty()) {
					this.setBankAccountList(null);
				} else {
					this.setBankAccountList(new ArrayList<BankAccount>());
					for(BankAccount ac : accs) {
						if(BankAccountStatus.ACTIVE.equals(ac.getStatus())) {
							this.getBankAccountList().add(ac);
						}
					}
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.setBankAccountList(null);
			return;
		}
		
	}
	
	private void cleanBean () {
		currentFile = null;
		fileProgress = 0;

	}

}
