package soya.framework.tools.workbench.resource;

import com.google.gson.*;
import io.swagger.annotations.Api;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.*;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@Path("/avro")
@Api(value = "Avro Service", hidden = false)
public class AvroResource {

    private void avroAPI() {
        Schema schema;
        SchemaBuilder schemaBuilder;
        Schema.Type type;
        Schema.Field field;

        LogicalType logicalType;
        Conversion<?> conversion;

        GenericData genericData;
        GenericData.Record record;
        GenericData.Array array;

        Encoder encoder;
        JsonEncoder jsonEncoder;
        DirectBinaryEncoder directBinaryEncoder;
        BufferedBinaryEncoder bufferedBinaryEncoder;
        BlockingBinaryEncoder blockingBinaryEncoder;

        DatumWriter datumWriter;
        GenericDatumWriter genericDatumWriter;

        Decoder decoder;
        JsonDecoder jsonDecoder;

        DatumReader datumReader;
        GenericDatumReader genericDatumReader;

        SpecificData specificData;
        ReflectData reflectData;
        SpecificRecord specificRecord;
        SpecificRecordBase specificRecordBase;

    }

    @POST
    @Path("/record")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response toAvro(String input) {
        Schema avsc = ReflectData.get().getSchema(AvroModel.class);

        System.out.println(avsc.toString(true));
        Conversion conversion;
        SpecificData specificData;

        LogicalType logicalType = new LogicalType("xxx");

        try {
            JsonObject jsonObject = JsonParser.parseString(input).getAsJsonObject();

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("avro/sample2_avro.avsc");
            Schema schema = new Schema.Parser().parse(is);
            GenericRecord record = createRecord(schema, jsonObject);

            return Response.ok(record).build();

        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response toAvroJson(String input) {
        try {
            JsonObject jsonObject = JsonParser.parseString(input).getAsJsonObject();

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("avro/sample2_avro.avsc");
            Schema schema = new Schema.Parser().parse(is);
            GenericRecord record = createRecord(schema, jsonObject);

            ByteArrayOutputStream outByteArray = new ByteArrayOutputStream();
            JsonEncoder encoderBin = EncoderFactory.get().jsonEncoder(schema, outByteArray);

            DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
            writer.write(record, encoderBin);
            encoderBin.flush();
            outByteArray.close();
            byte[] serializedBytes = outByteArray.toByteArray();

            return Response.ok(new String(serializedBytes)).build();

        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/serialize")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response index(String input) {
        try {
            JsonObject jsonObject = JsonParser.parseString(input).getAsJsonObject();

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("avro/sample2_avro.avsc");
            Schema schema = new Schema.Parser().parse(is);
            GenericRecord record = createRecord(schema, jsonObject);

            ByteArrayOutputStream outByteArray = new ByteArrayOutputStream();
            BinaryEncoder encoderBin = EncoderFactory.get().binaryEncoder(outByteArray, null);

            DatumWriter<GenericRecord> writer1 = new GenericDatumWriter<GenericRecord>(schema);
            writer1.write(record, encoderBin);
            encoderBin.flush();
            outByteArray.close();
            byte[] serializedBytes = outByteArray.toByteArray();

            return Response.ok(serializedBytes).build();

        } catch (IOException e) {
            return Response.status(500).build();
        }
    }


    private Schema createAvroSchema(SchemaType root, SchemaTypeSystem sts) {
        return null;
    }

    private SchemaType getRootBaseType(SchemaType type) {
        SchemaType baseType = type;
        while (baseType != null && !baseType.getBaseType().getName().getLocalPart().equalsIgnoreCase("anySimpleType")) {
            baseType = baseType.getBaseType();
        }

        return baseType;
    }

    private GenericData.Record createRecord(Schema schema, JsonObject jsonObject) {
        SpecificRecordBase specificRecordBase;

        GenericRecordBuilder builder = new GenericRecordBuilder(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            builder.set(e, getValue(e, jsonObject));
        });
        return builder.build();
    }

    private GenericData.Array<GenericData.Record> createArray(Schema schema, JsonArray jsonArray) {
        GenericData.Array<GenericData.Record> array = new GenericData.Array<GenericData.Record>(2, schema);
        Schema elementType = schema.getElementType();
        jsonArray.forEach(e -> {
            array.add(createRecord(elementType, e.getAsJsonObject()));
        });
        return array;
    }

    private Object getValue(Schema.Field field, JsonObject jsonObject) {
        Object value = null;
        Schema.Type type = field.schema().getType();
        JsonElement jsonElement = jsonObject.get(field.name());
        if (jsonElement.isJsonPrimitive()) {
            return getSimpleValue(type, jsonElement);

        } else if (jsonElement.isJsonObject()) {
            return createRecord(field.schema(), jsonElement.getAsJsonObject());

        } else if (jsonElement.isJsonArray()) {
            return createArray(field.schema(), jsonElement.getAsJsonArray());

        } else {
            return null;
        }
    }

    private Object getSimpleValue(Schema.Type type, JsonElement value) {
        if (value == null || JsonNull.INSTANCE.equals(value)) {
            return getDefaultValue(type);

        } else {
            switch (type) {
                case STRING:
                    return value.getAsString();

                case BOOLEAN:
                    return value.getAsBoolean();

                case INT:
                    return value.getAsInt();

                case LONG:
                    return value.getAsLong();

                case FLOAT:
                    return value.getAsFloat();

                case DOUBLE:
                    return value.getAsDouble();

                default:
                    return null;
            }
        }
    }

    private Object getDefaultValue(Schema.Type type) {
        switch (type) {
            case BOOLEAN:
                return Boolean.FALSE;

            case INT:
                return Integer.valueOf(0);

            case LONG:
                return Long.valueOf(0l);

            case FLOAT:
                return Float.valueOf(0);

            case DOUBLE:
                return Double.valueOf(0.0);

            default:
                return null;
        }
    }
}
