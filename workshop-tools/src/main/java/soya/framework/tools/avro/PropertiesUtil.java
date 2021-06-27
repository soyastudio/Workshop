package soya.framework.tools.avro;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class PropertiesUtil {

	static Map<String,  Map<String, String>> mapfOdProperties = new LinkedHashMap<String,  Map<String, String>>();

	/**
	 * This method is used fetch mapping configuration
	 * @param propertiesFileName
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> getProperties(String propertiesFileName) throws IOException {
		Map<String, String> map = null;
		
		//To DO  -- Need to add logic to check if file is updated.
		if (mapfOdProperties.containsKey(propertiesFileName)) {
			map = mapfOdProperties.get(propertiesFileName);
		} else {
			map = loadProperties(propertiesFileName);
			mapfOdProperties.put(propertiesFileName, map);
		}
		return map;
	}

	
	
	private static  Map<String, String> loadProperties(String propertiesFileName)  throws IOException {
		File mappingFile = new File(propertiesFileName);
		Properties properties = new Properties();
		FileInputStream ip = new FileInputStream(mappingFile);
		properties.load(ip);

		HashMap<String, String> map = new HashMap<String, String>();
		Set<Object> keys = properties.keySet();

		for (Object k : keys) {
			String key = (String) k;
			String value = properties.getProperty(key);
			map.put(key, value);
		}
		return map;
	}

}
