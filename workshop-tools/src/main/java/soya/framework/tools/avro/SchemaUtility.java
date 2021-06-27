package soya.framework.tools.avro;

import org.apache.avro.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class SchemaUtility {


	private static Map<String, Schema> mapOfSchema = new LinkedHashMap<String, Schema>();


	public static Schema getSchemaFromCache(String schemaName) {
		Schema schema = null;

		if (mapOfSchema.containsKey(schemaName)) {
			schema = mapOfSchema.get(schemaName);
		} 

		return schema;
	} 
	
	public static Schema getSchema(String schemaName, InputStream stream) throws IOException {
		
		Schema.Parser parser = new Schema.Parser();
		Schema schema = parser.parse(stream);
		mapOfSchema.put(schemaName, schema);
		
		return schema;
	} 
}
