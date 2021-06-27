package soya.framework.tools.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.Map;

public class ArrayTransformUtil implements Runnable{

	private Schema schema;
	private GenericData.Record avroRecord;
	private String parentFieldName;
	private Document doc;
	private Map<String, String> mappingPropertiesMap; 
	private NodeList nList;
	private String message;
	
	public ArrayTransformUtil(Schema schema, GenericData.Record avroRecord, String parentFieldName, Document doc,
			Map<String, String> mappingPropertiesMap, NodeList nList, String message) {
		this.schema = schema;
		this.avroRecord = avroRecord;
		this.parentFieldName = parentFieldName;
		this.doc = doc;
		this.mappingPropertiesMap = mappingPropertiesMap;
		this.nList = nList;
		this.message = message;
	}
	
	@Override
	public void run() {
		TransfromUtil.setFieldValue(schema, avroRecord, parentFieldName, doc,
				mappingPropertiesMap, nList, message);
	}

}
