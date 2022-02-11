package com.vartool.web.module;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vartech.common.app.beans.ParamMap;

public class JsonUtils
{
  public static Map stringToJsonMap(String json)
  {
    return (Map)stringToObject(json, HashMap.class);
  }

  public static Object stringToJsonClass(String json, Class clazz) {
    return stringToObject(json, clazz);
  }

  public static String jsonToString(Object jsonObject) {
    return objectToString(jsonObject);
  }

  public static String gainJsonToString(Object jsonObject) {
    return gainJsonToString(jsonObject, true);
  }

  public static String gainJsonToString(Object jsonObject, boolean resultAddFlag) {
    if (resultAddFlag) {
      Map m = new HashMap();
      m.put("result", jsonObject);
      return objectToString(m);
    }
    return objectToString(jsonObject);
  }

  public static String objectToString(Object json)
  {
    ObjectMapper om = new ObjectMapper();
    try
    {
      return om.writeValueAsString(json);
    }
    catch (JsonMappingException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static <T> T stringToObject(String jsonString) {
    return (T) stringToObject(jsonString, ParamMap.class);
  }

  public static <T> T stringToObject(String jsonString, Class<T> valueType) {
	  return stringToObject( jsonString,  valueType, false);
  }
  
  /**
	 * 
	 * @Method Name  : stringToObject
	 * @Method 설명 : string to object  , 프로퍼티 업을때 err여부. 
	 * @작성자   : ytkim
	 * @작성일   : 2018. 10. 12. 
	 * @변경이력  :
	 * @param jsonString
	 * @param valueType
	 * @param ignoreProp
	 * @return
	 */
	public static <T> T stringToObject(String jsonString, Class<T> valueType, boolean ignoreProp) {
		try {
			ObjectMapper om = new ObjectMapper();
			if (ignoreProp) {
				om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			} else {
				om.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			}
			return om.readValue(jsonString, valueType);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T stringToObject(String jsonString, TypeReference typeReference) {
		return stringToObject(jsonString, typeReference,false) ;
	}
	public static <T> T stringToObject(String jsonString, TypeReference typeReference, boolean ignoreProp) {
		try {
			ObjectMapper om = new ObjectMapper();
			if (ignoreProp) {
				om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			} else {
				om.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			}
			return (T) om.readValue(jsonString, typeReference);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String callbackObjectToString(String callback, Object json) {
		String reval = objectToString(json);
		if (!"".equals(callback)) {
			reval = callback + "(" + reval + ")";
		}

		return reval;
	}
}