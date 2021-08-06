package com.abs.edis.schema;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.UnresolvedUnionException;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
            result = forRecord(node, field.schema());

        } else if (Schema.Type.ARRAY.equals(field.schema().getType())) {
            forArray(node, container, field, field.schema());

        } else if (Schema.Type.UNION.equals(field.schema().getType())) {
            List<Schema> schemaList = field.schema().getTypes();
            for (Schema s : schemaList) {
                if (Schema.Type.NULL.equals(s.getType())) {

                } else if (Schema.Type.RECORD.equals(s.getType())) {
                    result = forRecord(node, s);

                } else if (Schema.Type.ARRAY.equals(s.getType())) {
                    forArray(node, container, field, s);

                } else if (node.getTextContent() != null) {
                    result = forTextContent(node.getTextContent(), s);
                }
            }
        } else if (node.getTextContent() != null) {
            result = forTextContent(node.getTextContent(), field.schema());

        }

        return result;
    }

    private Object forTextContent(String value, Schema schema) {
        if (Schema.Type.BOOLEAN.equals(schema.getType())) {
            return Boolean.parseBoolean(value);

        } else if (Schema.Type.DOUBLE.equals(schema.getType())) {
            return Double.parseDouble(value);

        } else if (Schema.Type.INT.equals(schema.getType())) {
            return Integer.parseInt(value);

        } else if (Schema.Type.LONG.equals(schema.getType())) {
            return Long.parseLong(value);

        } else if (Schema.Type.FLOAT.equals(schema.getType())) {
            return Float.parseFloat(value);

        } else {
            return value;
        }
    }

    private Object forRecord(Node node, Schema sc) {
        GenericData.Record record = null;
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

        return record;
    }

    private void forArray(Node node, GenericData.Record container, Schema.Field field, Schema schema) {
        Schema sc = field.schema().getElementType();
        stack.push(sc);

        GenericData.Array<GenericData.Record> arr = (GenericData.Array<GenericData.Record>) container.get(field.name());
        if (arr == null) {
            arr = new GenericData.Array<>(schema, new ArrayList<>());
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

        } else {
            System.out.println("====================== todo");
        }

        stack.pop();
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

        /*File output = new File("C:/github/Workshop/Repository/BusinessObjects/GroceryOrder/work/v1.12.2/GroceryOrder.avro");
        if (!output.exists()) {
            output.createNewFile();
        }*/

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            Schema schema = new Schema.Parser().parse(avsc);

            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);

            GenericData.Record record = transform(schema, doc);
            System.out.println(record);

            GenericDatumWriter<GenericData.Record> writer = new AvroMessageWriter(record.getSchema());
            byte[] data = new byte[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Encoder jsonEncoder = null;
            try {
                jsonEncoder = EncoderFactory.get().jsonEncoder(schema, stream);
                writer.write(record, jsonEncoder);

                jsonEncoder.flush();
                data = stream.toByteArray();

                System.out.println("================ " + data.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    static class AvroMessageWriter extends GenericDatumWriter<GenericData.Record> {
        public AvroMessageWriter(Schema schema) {
            super(schema);
        }

        protected void writeWithoutConversion(Schema schema, Object datum, Encoder out) throws IOException {
            try {
                super.writeWithoutConversion(schema, datum, out);

            } catch (NullPointerException e) {
                //e.printStackTrace();

            } catch (ClassCastException e) {
                super.writeWithoutConversion(schema, convert(schema, datum), out);

            } catch (UnresolvedUnionException e) {
                if (String.class.equals(datum.getClass())) {
                    super.writeWithoutConversion(schema, convert(schema, datum), out);

                } else if (GenericData.Record.class.equals(datum.getClass())) {
                    if (Schema.Type.UNION.equals(schema.getType())) {
                        List<Schema> schemas = schema.getTypes();
                        for (Schema sc : schemas) {
                            if (Schema.Type.RECORD.equals(sc.getType())) {
                                super.writeWithoutConversion(sc, datum, out);
                            }
                        }
                    } else {
                        //GenericData.Record rc = (GenericData.Record) datum;
                        //System.out.println("------------- " + rc.getSchema().getName() + ": " + rc.getSchema().getValueType());
                        // writeWithoutConversion(rc.getSchema(), datum, out);
                    }
                }

            } catch (AvroRuntimeException e) {
                //e.printStackTrace();

            } catch (IOException e) {
                // e.printStackTrace();
            }
        }

        private Object convert(Schema sc, Object datum) {
            if (datum == null) {
                return null;
            }

            if (Schema.Type.BOOLEAN.equals(sc.getType())) {
                return Boolean.parseBoolean(datum.toString());

            } else if (Schema.Type.DOUBLE.equals(sc.getType())) {
                return Double.parseDouble(datum.toString());

            } else if (Schema.Type.FLOAT.equals(sc.getType())) {
                return Float.parseFloat(datum.toString());

            } else if (Schema.Type.INT.equals(sc.getType())) {
                return Integer.parseInt(datum.toString());

            } else if (Schema.Type.LONG.equals(sc.getType())) {
                return Long.parseLong(datum.toString());

            } else if (Schema.Type.UNION.equals(sc.getType())) {
                List<Schema> schemas = sc.getTypes();
                for (Schema schema : schemas) {
                    Schema.Type type = schema.getType();

                    if (Schema.Type.BOOLEAN.equals(type)) {
                        return Boolean.parseBoolean(datum.toString());

                    } else if (Schema.Type.DOUBLE.equals(type)) {
                        return Double.parseDouble(datum.toString());

                    } else if (Schema.Type.FLOAT.equals(type)) {
                        return Float.parseFloat(datum.toString());

                    } else if (Schema.Type.INT.equals(type)) {
                        return Integer.parseInt(datum.toString());

                    } else if (Schema.Type.LONG.equals(type)) {
                        return Long.parseLong(datum.toString());

                    }
                }

            }

            return datum;
        }
    }

}
