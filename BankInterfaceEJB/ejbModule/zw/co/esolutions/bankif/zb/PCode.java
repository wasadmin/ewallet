package zw.co.esolutions.bankif.zb;

import zw.co.esolutions.ewallet.enums.TransactionType;

public enum PCode {
	PCODE42(TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER){
		public String getProcessingCode(){
			return "42";
		}
	},
	PCODE41(TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER){
		public String getProcessingCode(){
			return "41";
		}
	},
	PCODE40(TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER){
		public String getProcessingCode(){
			return "40";
		}
	},
	PCODE43(TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER){
		public String getProcessingCode(){
			return "43";
		}
	},
	PCODE22(TransactionType.DEPOSIT){
		public String getProcessingCode(){
			return "22";
		}
	},
	PCODEO1(TransactionType.WITHDRAWAL){
		public String getProcessingCode(){
			return "01";
		}
	},
	PCODEP3(TransactionType.TOPUP){
		public String getProcessingCode(){
			return "P3";
		}
	},
	PCODEP1(TransactionType.TOPUP){
		public String getProcessingCode(){
			return "P1";
		}
	},
	PCODEU5(TransactionType.BILLPAY){
		public String getProcessingCode(){
			return "U5";
		}
	},
	PCODEU6(TransactionType.EWALLET_BILLPAY){
		public String getProcessingCode(){
			return "U6";
		}
	},
	PCODE30(TransactionType.BALANCE){
		public String getProcessingCode(){
			return "30";
		}
	},
	PCODEP2(TransactionType.MINI_STATEMENT){
		public String getProcessingCode(){
			return "P2";
		}
	},
	PCODET0(TransactionType.TARIFF){
		public String getProcessingCode(){
			return "T0";
		}
	},
	PCODET2(TransactionType.TARIFF){
		public String getProcessingCode(){
			return "T2";
		}
	},
	PCODET1(TransactionType.TARIFF){
		public String getProcessingCode(){
			return "T1";
		}
	},
	PCODET6(TransactionType.TARIFF){
		public String getProcessingCode(){
			return "T6";
		}
	};

	private String description;
	
	PCode(TransactionType txnType) {		
		this.description = txnType.name();
	}
	
	public  String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getProcessingCode(){
		return "30";
	};
	
}

