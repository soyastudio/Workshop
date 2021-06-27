package soya.framework.tools.avro;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecord;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransfromUtil {

	private final static String FIELD_NAME_SEPERATOR = "/";
	private final static String EMPTY_STRING = "";
	private final static String OPEN_SQUARE_BRACKET = "[";
	private final static String CLOSE_SQUARE_BRACKET = "]";
	private final static String ARRAY = "array";

	// private static ExecutorService executors = Executors.newCachedThreadPool();
	// private static Future<Void> future = null;

	public static Record transformToArvo(Schema schema, Map<String, String> mappingPropertiesMap, Document doc,
			String message) {

		Record avroRecord = new Record(schema);

		String rootName = doc.getDocumentElement().getNodeName();
		NodeList nList = doc.getElementsByTagName(rootName);

		// Map<String, String> mapOfFields = new LinkedHashMap<String, String>();

		setFieldValue(schema, avroRecord, EMPTY_STRING, doc, mappingPropertiesMap, nList, message);

		// System.out.println(avroRecord);

		return avroRecord;
	}

	protected static void setFieldValue(Schema schema, Record avroRecord, String parentFieldName,
			Document doc, Map<String, String> mappingPropertiesMap, NodeList nList, String message) {

		if (schema.getType().equals(Schema.Type.ARRAY)) {

			if (schema.getElementType().getType().equals(Schema.Type.RECORD)) {
				List<Field> fields = schema.getElementType().getFields();
				for (Field field : fields) {
					if (field.schema().getType().equals(Schema.Type.RECORD)) {
						Record childRecord = new Record(field.schema());
						avroRecord.put(field.name(), childRecord);
						setFieldValue(field.schema(), childRecord, getFieldName(parentFieldName, field.name(), 0), doc,
								mappingPropertiesMap, nList, message);
					} else if (field.schema().getType().equals(Schema.Type.ARRAY)) {

						String xPath = getXpath(parentFieldName, field.name(), field.schema().getType().toString(), doc,
								mappingPropertiesMap, nList);
						int numberOfRecord = XMLUtility.getCount(xPath, doc, mappingPropertiesMap, message);

						if (field.schema().getElementType().getType().equals(Schema.Type.RECORD)) {
							GenericArray<GenericRecord> list = new GenericData.Array<GenericRecord>(numberOfRecord,
									field.schema());

//							long arraytime = 0;
//							if (field.name().equals("RetailStoreLevelAttributes")
//									|| field.name().equals("CorporateItem")) {
//								arraytime = System.currentTimeMillis();
//							}

							if (numberOfRecord > 0) {
								avroRecord.put(field.name(), list);
								ExecutorService executor = Executors.newFixedThreadPool(5);
								for (int count = 0; count < numberOfRecord; count++) {
									Record arrayRecord = new Record(
											field.schema().getElementType());
									list.add(arrayRecord);
//									setFieldValue(field.schema(), arrayRecord,
//											getFieldName(parentFieldName, field.name(), count + 1), doc,
//											mappingPropertiesMap, nList, message);
									Runnable worker = new ArrayTransformUtil(field.schema(), arrayRecord,
											getFieldName(parentFieldName, field.name(), count + 1), doc,
											mappingPropertiesMap, nList, message);
									executor.execute(worker);
								}
								executor.shutdown();
								while (!executor.isTerminated()) {
								}

							} else {
								avroRecord.put(field.name(), list);
							}

//							if (field.name().equals("RetailStoreLevelAttributes")
//									|| field.name().equals("CorporateItem")) {
//								System.out.println("Array proceesing time ====> " + field.name() + " ====== "
//										+ (System.currentTimeMillis() - arraytime));
//							}

						} else {
							List<Object> list = new ArrayList<>();
							String type = field.schema().getType().toString();
							if (type.equals(Schema.Type.ARRAY.toString())) {
								type = field.schema().getElementType().getType().toString();
							}
							for (int count = 0; count < numberOfRecord; count++) {
								list.add(XMLUtility.getValue(xPath, doc, count, type, mappingPropertiesMap, message));
							}
							avroRecord.put(field.name(), list);
						}
					} else {
						String xPath = getXpath(parentFieldName, field.name(), field.schema().getType().toString(), doc,
								mappingPropertiesMap, nList);
						avroRecord.put(field.name(), XMLUtility.getValue(xPath, doc, 0,
								field.schema().getType().toString(), mappingPropertiesMap, message));
					}
				}
				;
			}
		} else {
			schema.getFields().forEach(field -> {
				if (field.schema().getType().equals(Schema.Type.RECORD)) {
					Record childRecord = new Record(field.schema());
					avroRecord.put(field.name(), childRecord);
					setFieldValue(field.schema(), childRecord, getFieldName(parentFieldName, field.name(), 0), doc,
							mappingPropertiesMap, nList, message);
				} else if (field.schema().getType().equals(Schema.Type.ARRAY)) {
					String xPath = getXpath(parentFieldName, field.name(), field.schema().getType().toString(), doc,
							mappingPropertiesMap, nList);

					int numberOfRecord = XMLUtility.getCount(xPath, doc, mappingPropertiesMap, message);

					if (field.schema().getElementType().getType().equals(Schema.Type.RECORD)) {
						GenericArray<GenericRecord> list = new GenericData.Array<GenericRecord>(numberOfRecord,
								field.schema());

//						long arraytime = 0;
//						if (field.name().equals("RetailStoreLevelAttributes") || field.name().equals("CorporateItem")) {
//							arraytime = System.currentTimeMillis();
//						}

						if (numberOfRecord > 0) {
							avroRecord.put(field.name(), list);

							ExecutorService executor = Executors.newFixedThreadPool(5);
							for (int count = 0; count < numberOfRecord; count++) {
								Record arrayRecord = new Record(
										field.schema().getElementType());
								list.add(arrayRecord);
//								setFieldValue(field.schema(), arrayRecord,
//										getFieldName(parentFieldName, field.name(), count + 1), doc,
//										mappingPropertiesMap, nList, message);
								Runnable worker = new ArrayTransformUtil(field.schema(), arrayRecord,
										getFieldName(parentFieldName, field.name(), count + 1), doc,
										mappingPropertiesMap, nList, message);
								executor.execute(worker);
							}
							executor.shutdown();
							while (!executor.isTerminated()) {
							}

						} else {
							avroRecord.put(field.name(), list);
						}

//						if (field.name().equals("RetailStoreLevelAttributes") || field.name().equals("CorporateItem")) {
//							System.out.println("Array proceesing time ====> " + field.name() + " ====== "
//									+ (System.currentTimeMillis() - arraytime));
//							// arraytime =System.currentTimeMillis();
//						}

					} else {
						List<Object> list = new ArrayList<>();
						String type = field.schema().getType().toString();
						if (type.equals(Schema.Type.ARRAY.toString())) {
							type = field.schema().getElementType().getType().toString();
						}
						for (int count = 0; count < numberOfRecord; count++) {
							list.add(XMLUtility.getValue(xPath, doc, count, type, mappingPropertiesMap, message));
						}
						avroRecord.put(field.name(), list);
					}
				} else {
					avroRecord.put(field.name(), getValue(parentFieldName, field.name(),
							field.schema().getType().toString(), doc, mappingPropertiesMap, nList, message));
				}

			});
		}
	}

	private static Object getValue(String parentFieldName, String name, String type, Document doc,
			Map<String, String> mappingPropertiesMap, NodeList nList, String message) {
		String xPath = getXpath(parentFieldName, name, type, doc, mappingPropertiesMap, nList);

		return XMLUtility.getValue(xPath, doc, 0, type, mappingPropertiesMap, message);
	}

	private static String getFieldName(String parentFieldName, String name, int count) {
		if (count > 0) {
			return parentFieldName + FIELD_NAME_SEPERATOR + name + OPEN_SQUARE_BRACKET + count + CLOSE_SQUARE_BRACKET;
		} else {
			return parentFieldName + FIELD_NAME_SEPERATOR + name;
		}
	}

	private static String getXpath(String parentFieldName, String name, String type, Document doc,
			Map<String, String> mappingPropertiesMap, NodeList nList) {

		String xPath = EMPTY_STRING;
		if (parentFieldName.endsWith(OPEN_SQUARE_BRACKET)) {
			xPath = parentFieldName + CLOSE_SQUARE_BRACKET
					+ (name.equalsIgnoreCase(ARRAY) ? EMPTY_STRING : FIELD_NAME_SEPERATOR + name);
		} else {
			xPath = parentFieldName + (name.equalsIgnoreCase(ARRAY) ? EMPTY_STRING : FIELD_NAME_SEPERATOR + name);
		}
		xPath = FIELD_NAME_SEPERATOR + nList.item(0).getNodeName() + xPath;
		return xPath;
	}
}