package zw.co.esolutions.ussd.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapUtil {
	
	public static final String EQUALS ="==";
	public static final String SEPERATOR ="!!";
	
	public static String convertAttributesMapToString(Map<String,String> attributesMap){
		StringBuffer sb = new StringBuffer();
		for (String key : attributesMap.keySet()) {
			sb.append(key + EQUALS + attributesMap.get(key) + SEPERATOR );
		}
		return sb.toString();
	}
	
	

	public static Map<String,String> convertUserInfoListToMap(List<String> stringList, String seperator){
		Map<String,String> result = new TreeMap<String, String>();
		String[] sa ;
		for(String string : stringList){
			sa= string.split("\\"+seperator);
			result.put(sa[0], sa[1]);
		}
		return result;
	}
	
	
	
	
	
	public static Map<String,String> convertAttributesStringToMap(String attributesString){
		if(attributesString == null){
			return null;
		}
		Map<String, String> attributesMap = new HashMap<String, String>();
		int startIndex = 0;
		int endIndex =0;
		endIndex=attributesString.indexOf(EQUALS,startIndex);
		String key,value;
		while(endIndex >= 0){
			
			key = attributesString.substring(startIndex,endIndex);
			startIndex = endIndex + EQUALS.length();
			endIndex=attributesString.indexOf(SEPERATOR,startIndex);
			value =attributesString.substring(startIndex,endIndex);
			startIndex = endIndex + SEPERATOR.length();
			
			attributesMap.put(key, value);
			endIndex=attributesString.indexOf(EQUALS,startIndex);
		}
		return attributesMap;
	}
	
	
	public static void main(String[] args){
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("name", "prince");
		attributesMap.put("surname", "kaguda");
		String str =convertAttributesMapToString(attributesMap);
		convertAttributesStringToMap(str);
		
	}

}
