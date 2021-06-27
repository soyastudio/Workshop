package soya.framework.tools.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class XmlToAvroTransformer {

    private Stack<Schema> stack = new Stack<>();

    private XmlToAvroTransformer(Schema schema) {
        stack.push(schema);
    }

    private GenericData.Record transform(Document doc) {
        GenericData.Record record = new GenericData.Record(stack.peek());

        String rootName = doc.getDocumentElement().getNodeName();
        NodeList nList = doc.getElementsByTagName(rootName);
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                String fieldName = getFieldName(child);
                Object fieldValue = transform(child, record);
                if (fieldValue != null) {
                    record.put(fieldName, fieldValue);
                }
            }
        }

        return record;
    }

    private Object transform(Node node, GenericData.Record container) {
        Object result = null;
        Schema parent = stack.peek();

        String fieldName = getFieldName(node);
        Schema.Field field = parent.getField(fieldName);

        if (field == null) {
            return null;

        } else if (Schema.Type.RECORD.equals(field.schema().getType())) {

            GenericData.Record record = null;
            Schema sc = field.schema();
            if (sc != null && Schema.Type.RECORD.equals(sc.getType())) {
                stack.push(sc);
                record = new GenericData.Record(sc);
            }

            NamedNodeMap attrs = node.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                String attrName = getFieldName(attr);
                if (sc.getField(attrName) != null) {
                    record.put(attrName, attr.getNodeValue());
                }
            }

            NodeList nList = node.getChildNodes();
            for (int i = 0; i < nList.getLength(); i++) {
                Node n = nList.item(i);
                Object v = transform(n, record);
                if (v != null) {
                    record.put(getFieldName(n), v);
                }
            }

            if ((sc != null && Schema.Type.RECORD.equals(sc.getType()))) {
                stack.pop();
            }

            result = record;

        } else if (Schema.Type.ARRAY.equals(field.schema().getType())) {
            Schema sc = field.schema().getElementType();
            stack.push(sc);

            GenericData.Array<GenericData.Record> arr = (GenericData.Array<GenericData.Record>) container.get(fieldName);
            if (arr == null) {
                arr = new GenericData.Array<>(field.schema(), new ArrayList<>());
                container.put(field.name(), arr);
            }

            if (sc.getType().equals(Schema.Type.RECORD)) {
                GenericData.Record rc = new GenericData.Record(sc);
                NodeList nList = node.getChildNodes();
                for (int i = 0; i < nList.getLength(); i++) {
                    Node n = nList.item(i);
                    Object v = transform(n, rc);
                    if (v != null) {
                        rc.put(getFieldName(n), v);
                    }
                }

                arr.add(rc);
            }

            stack.pop();

        } else {
            return node.getTextContent();
        }

        return result;
    }

    private String getFieldName(Node node) {
        String name = node.getNodeName();
        if (name.contains(":")) {
            name = name.substring(name.lastIndexOf(':') + 1);
        }

        return name;
    }

    public static GenericData.Record transform(Schema schema, Document doc) {
        return new XmlToAvroTransformer(schema).transform(doc);
    }

    public static void main(String[] args) {
        File avsc = new File("C:/github/Workshop/Repository/BusinessObjects/GroceryOrder/work/v1.12.2/GroceryOrder.avsc");
        File xmlFile = new File("C:/github/Workshop/Repository/BusinessObjects/GroceryOrder/work/v1.12.2/cmm.xml");

        File output = new File("C:/github/Workshop/Repository/BusinessObjects/GroceryOrder/work/v1.12.2/GroceryOrder.avro");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            Schema schema = new Schema.Parser().parse(avsc);

            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);

            GenericData.Record record = transform(schema, doc);

            System.out.println(record);

            if (!output.exists()) {
                output.createNewFile();
            }

            GenericRecord genericRecord;

            DatumWriter<GenericData.Record> writer = new GenericDatumWriter<>(schema);
            DataFileWriter<GenericData.Record> dataFileWriter = new DataFileWriter<>(writer);
            dataFileWriter.create(schema, output);
            dataFileWriter.append(record);
            dataFileWriter.close();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

}
