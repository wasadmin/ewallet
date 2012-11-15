package zw.co.esolutions.ewallet.search;

import java.util.Scanner;

public class SearchUtil {
	

	public boolean appendWhere(String queryMessage){
		Scanner sc= new Scanner(queryMessage);
		String token=null;
		do{
			token=sc.findInLine("WHERE");
			if(token !=null && token.equalsIgnoreCase("WHERE")){
				return false;
			}
		}while(token !=null );
		return true;
	}
	public  static boolean appendAnd(String queryMessage){
		int count=0;
		boolean result=false;
		Scanner sc= new Scanner(queryMessage);
		String token=null;
		String pattern ="\\w\\.\\w+";
		do{
			token=sc.findInLine(pattern);
			if(token !=null){
				
				++count;
				//return true;
			}
		}while(token !=null );
		System.out.println("Count  "+count);
		if(count>=2){
			return true;
		}else
			
		return result;
		
	}
	
	
	public static void main(String [] args){
		String query="SELECT m.customer FROM MobileProfile m WHERE m.customer.lastName ='MPOFU'";
		String query2="SELECT m.customer FROM MobileProfile m WHERE";
		System.out.println("Result 1:::::::::::::"+appendAnd(query));
		System.out.println("Result 2::::::::::"+appendAnd(query2));
	}
	
public boolean generateQuery(String queryMessage){
		
		Scanner sc= new Scanner(queryMessage);
		String token=null;
		String pattern ="\\w\\.\\w+";
		do{
			token=sc.findInLine(pattern);
			if(token !=null){

				return true;
			}
		}while(token !=null );
		return false;
		
	}
	
	
	public boolean checkStringLen(String value){
		if(value != null){
			value = value.trim();
			if(value.length()>0)
				return true;
			else
				return false;
		}else 
			return false;
	}

}
