package soya.framework.tools.avro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XMLUtility {

	private static String errorTopic;
	
	private static final Logger logger = LoggerFactory.getLogger(ConverterUtil.class);
	
	private final static String FIELD_NAME_SEPERATOR = "/";
	private final static String EMPTY_STRING = "";
	private final static String OPEN_SQUARE_BRACKET = "[";
	private final static String CLOSE_SQUARE_BRACKET = "]";
	private final static String HYPHEN_ARRAY = "-array";
	private final static String BACKSLASH_OPENBRACKET = "\\[";

	private static XPath xPath = XPathFactory.newInstance().newXPath();
	
	public static int getCount(String path, Document doc, Map<String, String> mappingPropertiesMap, String message) {
		String mappinpPath = EMPTY_STRING;
		List<String> listOfArrayIndex = new ArrayList<String>();
		String newPath = EMPTY_STRING;
		if (path.contains(OPEN_SQUARE_BRACKET)) {
			String[] paths = path.split(BACKSLASH_OPENBRACKET);

			for (String part : paths) {
				int indexOfArrayEnd = part.indexOf(CLOSE_SQUARE_BRACKET);
				if (indexOfArrayEnd > -1) {
					listOfArrayIndex.add(part.subSequence(0, indexOfArrayEnd).toString());
					newPath = newPath + HYPHEN_ARRAY + part.substring(++indexOfArrayEnd);
				} else {
					newPath = newPath + part;
				}
			}
		} else {
			newPath = path;
		}

		if (mappingPropertiesMap.containsKey(newPath)) {
			mappinpPath = mappingPropertiesMap.get(newPath);
			for (String indexValue : listOfArrayIndex) {
				mappinpPath = mappinpPath.replaceFirst(HYPHEN_ARRAY, OPEN_SQUARE_BRACKET + indexValue + CLOSE_SQUARE_BRACKET);

			}
		} else {

			mappinpPath = path;
		}

		int count = 0;
		try {
			XPathExpression xPathExpression = xPath.compile(mappinpPath);
			NodeList nodeList = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
			count = nodeList.getLength();
		} catch (Exception e) {
			logger.error(" Timestamp: " + new Timestamp(System.currentTimeMillis()) + " Error : " + e
					+ " ErrorDescription: Unable to convert xml to avro");
		}
		return count;
	}

	public static Object getValue(String path, Document doc, int index, String type,
			Map<String, String> mappingPropertiesMap, String message) {

		String value = EMPTY_STRING;
		String mappinpPath = EMPTY_STRING;
		List<String> listOfArrayIndex = new ArrayList<String>();
		String newPath = EMPTY_STRING;
		if (path.contains(OPEN_SQUARE_BRACKET)) {
			String[] paths = path.split(BACKSLASH_OPENBRACKET);

			for (String part : paths) {
				int indexOfArrayEnd = part.indexOf(CLOSE_SQUARE_BRACKET);
				if (indexOfArrayEnd > -1) {
					listOfArrayIndex.add(part.subSequence(0, indexOfArrayEnd).toString());
					newPath = newPath + HYPHEN_ARRAY + part.substring(++indexOfArrayEnd);
				} else {
					newPath = newPath + part;
				}
			}
		} else {
			newPath = path;
		}

		if (mappingPropertiesMap.containsKey(newPath)) {
			mappinpPath = mappingPropertiesMap.get(newPath);
		} else {
			//System.err.println("mapping path incorrect at left side of mappingproperty FIle :: "+ newPath);
            //System.exit(0);
            return ConverterUtil.processDataType(type, EMPTY_STRING, message);
		}

		for (String indexValue : listOfArrayIndex) {
			mappinpPath = mappinpPath.replaceFirst(HYPHEN_ARRAY, OPEN_SQUARE_BRACKET + indexValue + CLOSE_SQUARE_BRACKET);
		}

		try {
			XPathExpression xPathExpression = xPath.compile(mappinpPath);
			NodeList nodeList = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
			if (nodeList.item(index) != null) {
				value = nodeList.item(index).getTextContent().trim();
			} else {
				if (!mappinpPath.endsWith(FIELD_NAME_SEPERATOR)) {
					int idexOfBackSlash = mappinpPath.lastIndexOf(FIELD_NAME_SEPERATOR);
					String nameOfAttribute = mappinpPath.substring(mappinpPath.lastIndexOf('/') + 1).trim();
					xPathExpression = xPath.compile(mappinpPath.substring(0, idexOfBackSlash));
					nodeList = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
					if (nodeList.item(index) != null) {
						if (nameOfAttribute != null) {
							if (null != nodeList.item(index).getAttributes().getNamedItem(nameOfAttribute)) {
								value = nodeList.item(index).getAttributes().getNamedItem(nameOfAttribute)
										.getNodeValue().trim();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(" Timestamp: " + new Timestamp(System.currentTimeMillis()) + " Fetching Value: " + type
					+ " Fetching Value: " + value + " Error : " + e
					+ " ErrorDescription: Unable to convert xml to avro");
		}
		return ConverterUtil.processDataType(type, value, message);
	}

}
