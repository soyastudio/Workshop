package com.abs.edis.commons;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class XmlToAvroConverter {
    public static final String AVRO_CONFLUENT_FORMAT = "AVRO_CONFLUENT";
    public static final String AVRO_BINARY_FORMAT = "AVRO_BINARY";
    public static final String AVRO_JSON_FORMAT = "AVRO_JSON";
    public static final String PLAIN_JSON_FORMAT = "PLAIN_JSON";

    private static final String ALPHABET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
    private static final byte MAGIC_BYTE = 0;
    private static final int ID_SIZE = 4;
    
    private XmlToAvroConverter() {
    }

    public static Schema parse(byte[] data) {
        return new Schema.Parser().parse(unzip(data));
    }

    public static byte[] convert(Node xml, Schema schema, int id, String format) throws IOException, ParserConfigurationException, SAXException {

        GenericData.Record record = createRecord(schema, xml);
        if (AVRO_CONFLUENT_FORMAT.equalsIgnoreCase(format)) {
            return toAvroConfluent(record, schema, id);

        } else if (AVRO_BINARY_FORMAT.equalsIgnoreCase(format)) {
            return toAvroBinary(record, schema);

        } else if (AVRO_JSON_FORMAT.equalsIgnoreCase(format)) {
            return toAvroJson(record, schema);

        } else {
            return toJson(record);
        }
    }

    public static String read(byte[] data, Schema schema, String format) throws IOException {
        if (AVRO_CONFLUENT_FORMAT.equalsIgnoreCase(format)) {
            return fromAvroConfluent(data, schema).toString();

        } else if (AVRO_BINARY_FORMAT.equalsIgnoreCase(format)) {
            return fromAvroBinary(data, schema).toString();

        } else if (AVRO_JSON_FORMAT.equalsIgnoreCase(format)) {
            return fromAvroJson(data, schema).toString();

        } else {
            return fromJson(data);
        }
    }

    public static byte[] zip(final String str) {
        if ((str == null) || (str.length() == 0)) {
            throw new IllegalArgumentException("Cannot zip null or empty string");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encode(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to zip content", e);
        }
    }

    public static String unzip(final byte[] data) {
        if ((data == null) || (data.length == 0)) {
            throw new IllegalArgumentException("Cannot unzip null or empty bytes");
        }

        byte[] compressed = Base64.getDecoder().decode(data);

        if (!isZipped(compressed)) {
            return new String(compressed);
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        StringBuilder output = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            output.append(line);
                        }
                        return output.toString();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to unzip content", e);
        }
    }

    private static byte[] toJson(GenericData.Record record) {
        return new GsonBuilder().setPrettyPrinting().create()
                .toJson(JsonParser.parseString(record.toString()))
                .getBytes();
    }

    private static String fromJson(byte[] data) {
        return new String(data);
    }

    private static byte[] toAvroConfluent(GenericData.Record record, Schema schema, int id) throws IOException, ParserConfigurationException, SAXException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(MAGIC_BYTE);
        
        out.write(ByteBuffer.allocate(ID_SIZE).putInt(id).array());

        BinaryEncoder encoder = EncoderFactory.get().directBinaryEncoder(out, null);
        DatumWriter writer = new GenericDatumWriter(schema);
        writer.write(record, encoder);
        encoder.flush();
        out.close();

        return out.toByteArray();
    }

    private static GenericRecord fromAvroConfluent(byte[] data, Schema schema) throws IOException {
    	DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        datumReader.setSchema(schema);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        if (buffer.get() != MAGIC_BYTE) {
            throw new RuntimeException("Unknown magic byte!");
        }

        int schemaId = buffer.getInt();
        int length = buffer.limit() - 1 - ID_SIZE;
        byte[] bytes = new byte[length];
        buffer.get(bytes, 0, length);

        Decoder decoder = DecoderFactory.get().directBinaryDecoder(new ByteArrayInputStream(bytes), null);
        return datumReader.read(null, decoder);
    }

    private static byte[] toAvroBinary(GenericData.Record record, Schema schema) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(record, encoder);
        encoder.flush();
        out.close();
        return out.toByteArray();
    }

    private static GenericRecord fromAvroBinary(byte[] data, Schema schema) throws IOException {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        datumReader.setSchema(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        return datumReader.read(null, decoder);
    }

    private static byte[] toAvroJson(GenericData.Record record, Schema schema) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        Encoder encoder = EncoderFactory.get().jsonEncoder(schema, out);
        writer.write(record, encoder);
        encoder.flush();
        out.close();
        return out.toByteArray();
    }

    private static GenericRecord fromAvroJson(byte[] data, Schema schema) throws IOException {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        datumReader.setSchema(schema);
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, new ByteArrayInputStream(data));
        return datumReader.read(null, decoder);
    }

    public static GenericData.Record createRecord(Schema schema, Node node) {
        GenericData.Record record = new GenericData.Record(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            record.put(e.name(), create(e.name(), e.schema(), node));
        });

        return record;
    }

    public static GenericRecord read(byte[] data, Schema schema) throws Exception {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        datumReader.setSchema(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        return datumReader.read(null, decoder);
    }

    private static GenericData.Record createEmptyRecord(Schema schema) {
        GenericData.Record record = new GenericData.Record(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            record.put(e.name(), getDefaultValue(e.schema()));
        });
        return record;
    }

    private static List<Node> getChildrenByName(Node node, String name) {
        List<Node> list = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            String nodeName = child.getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(":") + 1);
            }

            if (name.equals(nodeName)) {
                list.add(child);
            }
        }
        return list;
    }

    private static Object create(String name, Schema schema, Node node) {

        Schema.Type type = schema.getType();
        Element element = (Element) node;

        if (element.getAttribute(name) != null && !element.getAttribute(name).isEmpty()) {
            return element.getAttribute(name);

        } else if (element.getAttribute(name) != null && !element.getAttribute(name).isEmpty()) {
            return element.getAttribute(name);

        } else if (type.equals(Schema.Type.ARRAY)) {
            return createArray(name, schema, node);

        } else if (type.equals(Schema.Type.RECORD) || type.equals(Schema.Type.MAP)) {
            List<Node> children = getChildrenByName(node, name);
            if (children.size() == 1 && children.get(0).getNodeType() == Node.ELEMENT_NODE) {
                return createRecord(schema, children.get(0));

            } else {
                return new GenericData.Record(schema);
            }

        } else if (type.equals(Schema.Type.UNION)) {
            return generateUnion(name, schema, node);

        } else {
            String value = null;
            List<Node> children = getChildrenByName(node, name);
            if (children.size() == 1) {
                value = children.get(0).getTextContent();
            }

            return convert(value, schema);
        }
    }

    private static Object convert(String value, Schema schema) {
        if (value == null) {
            return getDefaultValue(schema);
        }

        switch (schema.getType()) {
            case BOOLEAN:
                return Boolean.parseBoolean(value);

            case BYTES:
                return value.getBytes();

            case DOUBLE:
                return Double.parseDouble(value);

            case FLOAT:
                return Float.parseFloat(value);

            case INT:
                return Integer.parseInt(value);

            case LONG:
                return Long.parseLong(value);

            case NULL:
                return null;

            case STRING:
                return value;

            case ENUM:
                return generateEnumSymbol(value, schema);

            case FIXED:
                return generateFixed(value, schema);

            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private static Object generateUnion(String name, Schema type, Node node) {
        List<Node> children = getChildrenByName(node, name);
        if (children.size() > 0) {
            List<Schema> schemas = type.getTypes();
            for (Schema sc : schemas) {
                if (!Schema.Type.NULL.equals(sc.getType())) {
                    return create(name, sc, node);
                }
            }
        }

        return null;
    }

    private static Object createArray(String name, Schema schema, Node node) {

        Collection<Object> result = new ArrayList<>();
        List<Node> children = getChildrenByName(node, name);
        children.forEach(e -> {
            Schema elementType = schema.getElementType();
            if (Schema.Type.RECORD.equals(elementType.getType())) {
                result.add(createRecord(elementType, e));

            } else {
                result.add(convert(e.getTextContent(), elementType));

            }
        });

        return result;
    }

    private static Object generateFixed(String value, Schema schema) {
        if (value == null) {
            return getDefaultFixedValue(schema);

        } else {
            return value;

        }
    }

    private static Object generateEnumSymbol(String value, Schema schema) {
        List<String> values = schema.getEnumSymbols();
        for (String e : values) {
            if (e.equals(value)) {
                return e;
            }
        }

        throw new IllegalArgumentException("No value for enum type: " + value);
    }

    private static Object getDefaultValue(Schema schema) {
        switch (schema.getType()) {
            case BOOLEAN:
                return Boolean.FALSE;

            case BYTES:
                return "".getBytes();

            case DOUBLE:
                return 0.0;

            case FLOAT:
                return 0.0f;

            case INT:
                return 0;

            case LONG:
                return 0L;

            case NULL:
                return null;

            case STRING:
                return "";

            case RECORD:
            case MAP:
                return createEmptyRecord(schema);

            case ARRAY:
                return new ArrayList<>();

            case UNION:
                return getDefaultUnionValue(schema);

            case ENUM:
                return getDefaultEnumSymbol(schema);

            case FIXED:
                return getDefaultFixedValue(schema);

            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private static Object getDefaultUnionValue(Schema schema) {
        Object value = null;
        List<Schema> list = schema.getTypes();
        for (Schema sc : list) {
            if (Schema.Type.NULL.equals(sc.getType())) {
                return null;

            } else if (value == null) {
                value = getDefaultValue(schema);
            }
        }

        return value;
    }

    private static Object getDefaultEnumSymbol(Schema schema) {
        List<String> values = schema.getEnumSymbols();
        if (values.size() > 0) {
            return values.get(0);

        } else {
            return null;

        }
    }

    private static Object getDefaultFixedValue(Schema schema) {
        StringBuilder builder = new StringBuilder();
        int len = schema.getFixedSize();
        char[] arr = ALPHABET.toCharArray();
        for (int i = 0; i < len; i++) {
            int random = ThreadLocalRandom.current().nextInt();
            if (random < 0) {
                random = -1 * random;
            }
            int index = random % 52;
            builder.append(arr[index]);
        }
        return builder.toString();
    }

    public static String getRandom(int len) {
        StringBuilder builder = new StringBuilder();
        char[] arr = ALPHABET.toCharArray();
        for (int i = 0; i < len; i++) {
            int random = ThreadLocalRandom.current().nextInt();
            if (random < 0) {
                random = -1 * random;
            }
            int index = random % 52;
            builder.append(arr[index]);
        }
        return builder.toString();
    }

    private static void write(GenericRecord record, Schema schema, OutputStream out) throws IOException {
        GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        //Encoder encoder = EncoderFactory.get().jsonEncoder(schema, out);

        writer.write(record, encoder);
        encoder.flush();
        out.close();
    }

    private static boolean isZipped(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
                && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    public static String toXmlString(Node node) {
        StringBuilder builder = new StringBuilder();
        print(node, builder);
        return builder.toString();
    }

    private static void print(Node node, StringBuilder builder) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String name = node.getNodeName();
            if (name.contains(":")) {
                name = name.substring(name.indexOf(":") + 1);
            }
            builder.append("<").append(name).append(">");

            NodeList children = node.getChildNodes();
            if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
                builder.append(node.getTextContent());
            } else {
                for (int i = 0; i < children.getLength(); i++) {
                    print(children.item(i), builder);
                }

            }

            builder.append("</").append(name).append(">");
        }
    }

    public static byte[] sampleRecordAsJson(Schema schema) {
        return toJson(sampleRecord(schema));
    }

    public static GenericData.Record sampleRecord(Schema schema) {
    	GenericRecordBuilder builder = new GenericRecordBuilder(schema);
        for (Schema.Field field : schema.getFields()) {
            builder.set(field, generateObject(field.schema(), field.name()));
        }
        return builder.build();
    } 
    
    private static Object generateObject(Schema schema, String fieldName) {

        switch (schema.getType()) {
            case ARRAY:
                return generateArray(schema, fieldName);
            case RECORD:
                return sampleRecord(schema);
            case UNION:
                return generateUnion(schema, fieldName);
                
            case BOOLEAN:
                return generateBoolean();
            case BYTES:
            	return generateBytes(fieldName);
            case DOUBLE:
                return generateDouble(fieldName);
            case ENUM:
                return generateEnumSymbol(schema);
            case FLOAT:
                return generateFloat(fieldName);
            case INT:
                return generateInt(fieldName);
            case LONG:
                return generateLong(fieldName);
            case NULL:
                return null;
            case STRING:
                return generateString(fieldName);
                
            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private static Collection<Object> generateArray(Schema schema, String fieldName) {
        int length = 1;
        Collection<Object> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(generateObject(schema.getElementType(), fieldName));
        }
        return result;
    }

    private static Object generateUnion(Schema schema, String fieldName) {
        List<Schema> schemas = schema.getTypes();
        //return generateObject(schemas.get(random.nextInt(schemas.size())));
        for(Schema sc: schemas) {
            if(!Schema.Type.NULL.equals(sc.getType())) {
                return generateObject(sc, fieldName);
            }
        }

        return generateObject(schemas.get(0), fieldName);
    }
    
    private static GenericEnumSymbol generateEnumSymbol(Schema schema) {
        List<String> enums = schema.getEnumSymbols();
        return new
                GenericData.EnumSymbol(schema, enums.get(0));
    }
    
    private static ByteBuffer generateBytes(String fieldName) {
        return ByteBuffer.wrap(fieldName.getBytes());
    }
    
    private static Boolean generateBoolean() {
        return Boolean.TRUE;
    }

    private static Double generateDouble(String fieldName) {
        return 99.99;
    }

    private static Float generateFloat(String fieldName) {
        return 19.99f;
    }

    private static Integer generateInt(String fieldName) {
        return 987654321;
    }

    private static Long generateLong(String fieldName) {
        return 987654321l;
    }

    private static String generateString(String fieldName) {
        if(fieldName == null) {
            return "string";
        }

        return valueOf(fieldName);

    }
    
    private static String valueOf(String fieldName) {
    	char[] arr = fieldName.toCharArray();
    	StringBuilder builder = new StringBuilder();
    	for(char c: arr) {
    		if(!Character.isAlphabetic(c)) {
    			builder.append(c);
    			
    		} else if(Character.isUpperCase(c)) {
    			builder.append("_").append(c);
    			
    		} else {
    			builder.append(Character.toUpperCase(c));
    		}
    	}
    	
    	String value = builder.toString();
    	if(value.endsWith("_I_D")) {
            value = value.replace("_I_D", "_ID");
        }

        if(value.endsWith("_T_S")) {
            value = value.replace("_T_S", "_TS");
        }

        if(value.endsWith("_IND")) {
            value = "Y";
        }

        if(value.endsWith("_DT_TM") || value.endsWith("_DT") || value.endsWith("_TM") || value.endsWith("_TS")) {
            value = "2021-03-16T12:21:47.403Z";
        }

        return value;
    }

}

