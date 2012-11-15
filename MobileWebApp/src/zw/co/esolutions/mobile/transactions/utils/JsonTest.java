/**
 * 
 */
package zw.co.esolutions.mobile.transactions.utils;

import java.io.IOException;

import com.ibm.json.java.JSONObject;

/**
 * @author taurai
 *
 */
public class JsonTest {

	/**
	 * 
	 */
	public JsonTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JSONObject jb = new JSONObject();
		jb.put("accounts", "{name : 263773303584}, {name : 263733395363},");
		jb.put("loginResponse", "success");
		jb.put("mobile", "0733 075 073");
		try {
			System.out.println(">>>>>>>>>>>>>>>>>>. JSON : "+jb.serialize());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
}
