package soya.framework.tao.xs;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;

public class XmlToAvroSchema {

    public static final String DEFAULT_NAMESPACE = "com.albertsons.esed.cmm";

    private XmlToAvroSchema() {
    }

    public static Schema fromXmlSchema(SchemaTypeSystem sts) {
        SchemaType schemaType = sts.globalTypes()[0];
        SchemaBuilder.FieldAssembler assembler = SchemaBuilder
                .record(schemaType.getName().getLocalPart()).namespace(DEFAULT_NAMESPACE)
                .fields();

        assemble(schemaType, assembler);

        return (Schema) assembler.endRecord();
    }

    public static Schema fromXmlSchema(SchemaType schemaType) {
        SchemaBuilder.FieldAssembler assembler = SchemaBuilder
                .record(schemaType.getName().getLocalPart()).namespace(DEFAULT_NAMESPACE)
                .fields();

        assemble(schemaType, assembler);

        return (Schema) assembler.endRecord();
    }

    private static SchemaBuilder.FieldAssembler assemble(SchemaType schemaType, SchemaBuilder.FieldAssembler assembler) {
        for (SchemaProperty ap : schemaType.getAttributeProperties()) {
            assemble(ap, assembler);
        }

        for (SchemaProperty sp : schemaType.getElementProperties()) {
            assemble(sp, assembler);
        }

        return assembler;
    }


    private static SchemaBuilder.FieldAssembler assemble(SchemaProperty schemaProperty, SchemaBuilder.FieldAssembler assembler) {

        SchemaType schemaType = schemaProperty.getType();
        for (SchemaProperty ap : schemaType.getAttributeProperties()) {
            assembleSimpleProperty(ap, assembler);
        }

        for (SchemaProperty sp : schemaType.getElementProperties()) {
            SchemaType st = sp.getType();
            if (st.isSimpleType()) {
                Type pt = BuildInTypeMapping.fromXmlTypeCode(XmlBeansUtils.getXMLBuildInType(sp.getType()).getCode());
                if (sp.getMaxOccurs() == null || sp.getMaxOccurs().intValue() > 1) {
                    // Array of Simple Type:
                    assembler.name(sp.getName().getLocalPart()).type(Schema.createArray(Schema.create(pt))).noDefault();

                } else {
                    assembleSimpleProperty(sp, assembler);
                }
            } else {
                String name = st.isAnonymousType() ? sp.getName().getLocalPart() : sp.getType().getName().getLocalPart();
                SchemaBuilder.FieldAssembler sub = SchemaBuilder.record(name).namespace(DEFAULT_NAMESPACE).fields();
                Schema subSchema = null;
                try {
                    subSchema = (Schema) assemble(sp.getType(), sub).endRecord();

                } catch (Exception e) {

                }
                if(subSchema != null) {
                    if (sp.getMaxOccurs() == null || sp.getMaxOccurs().intValue() > 1) {
                        // Array of Complex Type:
                        assemble(sp.getType(), sub);
                        assembler.name(sp.getName().getLocalPart()).type(Schema.createArray(subSchema)).noDefault();

                    } else {
                        assemble(sp.getType(), sub);
                        assembler.name(sp.getName().getLocalPart()).type(subSchema).noDefault();

                    }

                }
            }
        }

        return assembler;
    }

    private static SchemaBuilder.FieldAssembler assembleSimpleProperty(SchemaProperty sp, SchemaBuilder.FieldAssembler assembler) {
        boolean nullable = sp.getMaxOccurs() == null || sp.getMinOccurs().intValue() == 0;
        String defaultText = sp.getDefaultText();

        SchemaType st = sp.getType();
        Type pt = BuildInTypeMapping.fromXmlTypeCode(XmlBeansUtils.getXMLBuildInType(sp.getType()).getCode());

        SchemaBuilder.FieldTypeBuilder builder = assembler.name(sp.getName().getLocalPart()).type();

        if (sp.getMinOccurs() == null && sp.getMinOccurs().intValue() == 0) {
            builder.nullable();
        }

        switch (pt) {
            case BOOLEAN:
                SchemaBuilder.BooleanDefault booleanDefault = builder.booleanType();
                if (defaultText != null) {
                    booleanDefault.booleanDefault(Boolean.parseBoolean(defaultText));

                } else {
                    booleanDefault.noDefault();
                }
                break;

            case BYTES:
                SchemaBuilder.BytesDefault bytesDefault = builder.bytesType();
                if (defaultText != null) {
                    bytesDefault.bytesDefault(defaultText);
                } else {
                    bytesDefault.noDefault();
                }
            case INT:
                SchemaBuilder.IntDefault intDefault = builder.intType();
                if (defaultText != null) {
                    intDefault.intDefault(Integer.parseInt(defaultText));
                } else {
                    intDefault.noDefault();
                }
                break;
            case LONG:
                SchemaBuilder.LongDefault longDefault = builder.longType();
                if (defaultText != null) {
                    longDefault.longDefault(Long.parseLong(defaultText));
                } else {
                    longDefault.noDefault();
                }
                break;
            case FLOAT:
                SchemaBuilder.FloatDefault floatDefault = builder.floatType();
                if (defaultText != null) {
                    floatDefault.floatDefault(Float.parseFloat(defaultText));
                } else {
                    floatDefault.noDefault();
                }
                break;
            case DOUBLE:
                SchemaBuilder.DoubleDefault doubleDefault = builder.doubleType();
                if (defaultText != null) {
                    doubleDefault.doubleDefault(Double.parseDouble(defaultText));
                } else {
                    doubleDefault.noDefault();
                }
                break;
            default:
                SchemaBuilder.StringDefault stringDefault = builder.stringType();
                if(defaultText != null) {
                    stringDefault.stringDefault(defaultText);

                } else {
                    stringDefault.noDefault();
                }
        }

        return assembler;
    }

    public static enum BuildInTypeMapping {
        BOOLEAN(XmlBeansUtils.XMLBuildInType.BOOLEAN, Type.BOOLEAN),
        BASE_64_BINARY(XmlBeansUtils.XMLBuildInType.BASE_64_BINARY, Type.BYTES),
        HEX_BINARY(XmlBeansUtils.XMLBuildInType.HEX_BINARY, Type.BYTES),
        ANY_URI(XmlBeansUtils.XMLBuildInType.ANY_URI, Type.STRING),
        QNAME(XmlBeansUtils.XMLBuildInType.QNAME, Type.STRING),
        NOTATION(XmlBeansUtils.XMLBuildInType.NOTATION, Type.STRING),
        FLOAT(XmlBeansUtils.XMLBuildInType.FLOAT, Type.FLOAT),
        DOUBLE(XmlBeansUtils.XMLBuildInType.DOUBLE, Type.DOUBLE),
        DECIMAL(XmlBeansUtils.XMLBuildInType.DECIMAL, Type.DOUBLE),
        STRING(XmlBeansUtils.XMLBuildInType.STRING, Type.STRING),
        DURATION(XmlBeansUtils.XMLBuildInType.DURATION, Type.LONG),
        DATE_TIME(XmlBeansUtils.XMLBuildInType.DATE_TIME, Type.STRING),
        TIME(XmlBeansUtils.XMLBuildInType.TIME, Type.STRING),
        DATE(XmlBeansUtils.XMLBuildInType.DATE, Type.STRING),
        G_YEAR_MONTH(XmlBeansUtils.XMLBuildInType.G_YEAR_MONTH, Type.STRING),
        G_YEAR(XmlBeansUtils.XMLBuildInType.G_YEAR, Type.STRING),
        G_MONTH_DAY(XmlBeansUtils.XMLBuildInType.G_MONTH_DAY, Type.STRING),
        G_DAY(XmlBeansUtils.XMLBuildInType.G_DAY, Type.STRING),
        G_MONTH(XmlBeansUtils.XMLBuildInType.G_MONTH, Type.STRING),
        INTEGER(XmlBeansUtils.XMLBuildInType.INTEGER, Type.LONG),
        LONG(XmlBeansUtils.XMLBuildInType.LONG, Type.LONG),
        INT(XmlBeansUtils.XMLBuildInType.INT, Type.INT),
        SHORT(XmlBeansUtils.XMLBuildInType.SHORT, Type.INT),
        BYTE(XmlBeansUtils.XMLBuildInType.HEX_BINARY, Type.STRING),
        NON_POSITIVE_INTEGER(XmlBeansUtils.XMLBuildInType.NON_POSITIVE_INTEGER, Type.LONG),
        NEGATIVE_INTEGER(XmlBeansUtils.XMLBuildInType.NEGATIVE_INTEGER, Type.LONG),
        NON_NEGATIVE_INTEGER(XmlBeansUtils.XMLBuildInType.NON_NEGATIVE_INTEGER, Type.LONG),
        POSITIVE_INTEGER(XmlBeansUtils.XMLBuildInType.POSITIVE_INTEGER, Type.LONG),
        UNSIGNED_LONG(XmlBeansUtils.XMLBuildInType.UNSIGNED_LONG, Type.LONG),
        UNSIGNED_INT(XmlBeansUtils.XMLBuildInType.UNSIGNED_INT, Type.LONG),
        UNSIGNED_SHORT(XmlBeansUtils.XMLBuildInType.UNSIGNED_SHORT, Type.INT),
        UNSIGNED_BYTE(XmlBeansUtils.XMLBuildInType.UNSIGNED_BYTE, Type.INT),
        NORMALIZED_STRING(XmlBeansUtils.XMLBuildInType.STRING, Type.STRING),
        TOKEN(XmlBeansUtils.XMLBuildInType.TOKEN, Type.STRING),
        NAME(XmlBeansUtils.XMLBuildInType.NAME, Type.STRING),
        NCNAME(XmlBeansUtils.XMLBuildInType.NCNAME, Type.STRING),
        LANGUAGE(XmlBeansUtils.XMLBuildInType.LANGUAGE, Type.STRING),
        ID(XmlBeansUtils.XMLBuildInType.ID, Type.STRING),
        IDREF(XmlBeansUtils.XMLBuildInType.IDREF, Type.STRING),
        IDREFS(XmlBeansUtils.XMLBuildInType.IDREFS, Type.STRING),
        ENTITY(XmlBeansUtils.XMLBuildInType.ENTITY, Type.STRING),
        ENTITIES(XmlBeansUtils.XMLBuildInType.ENTITIES, Type.STRING),
        NMTOKEN(XmlBeansUtils.XMLBuildInType.NMTOKEN, Type.STRING),
        NMTOKENS(XmlBeansUtils.XMLBuildInType.NMTOKENS, Type.STRING);

        private final XmlBeansUtils.XMLBuildInType xmlBuildInType;
        private final Type avroType;

        private BuildInTypeMapping(XmlBeansUtils.XMLBuildInType xmlBuildInType, Type avroType) {
            this.xmlBuildInType = xmlBuildInType;
            this.avroType = avroType;
        }

        public static Type fromXmlTypeCode(int code) {
            switch (code) {
                case SchemaType.BTC_BOOLEAN:
                    return BOOLEAN.avroType;

                case SchemaType.BTC_BASE_64_BINARY:
                    return BASE_64_BINARY.avroType;

                case SchemaType.BTC_HEX_BINARY:
                    return HEX_BINARY.avroType;

                case SchemaType.BTC_ANY_URI:
                    return ANY_URI.avroType;

                case SchemaType.BTC_QNAME:
                    return QNAME.avroType;

                case SchemaType.BTC_NOTATION:
                    return NOTATION.avroType;

                case SchemaType.BTC_FLOAT:
                    return FLOAT.avroType;

                case SchemaType.BTC_DOUBLE:
                    return DOUBLE.avroType;

                case SchemaType.BTC_DECIMAL:
                    return DECIMAL.avroType;

                case SchemaType.BTC_STRING:
                    return STRING.avroType;

                case SchemaType.BTC_DURATION:
                    return DURATION.avroType;

                case SchemaType.BTC_DATE_TIME:
                    return DATE_TIME.avroType;

                case SchemaType.BTC_TIME:
                    return TIME.avroType;

                case SchemaType.BTC_DATE:
                    return DATE.avroType;

                case SchemaType.BTC_G_YEAR_MONTH:
                    return G_YEAR_MONTH.avroType;

                case SchemaType.BTC_G_YEAR:
                    return G_YEAR.avroType;

                case SchemaType.BTC_G_MONTH_DAY:
                    return G_MONTH_DAY.avroType;

                case SchemaType.BTC_G_DAY:
                    return G_DAY.avroType;

                case SchemaType.BTC_G_MONTH:
                    return G_MONTH.avroType;

                case SchemaType.BTC_INTEGER:
                    return INTEGER.avroType;

                case SchemaType.BTC_LONG:
                    return LONG.avroType;

                case SchemaType.BTC_INT:
                    return INT.avroType;

                case SchemaType.BTC_SHORT:
                    return SHORT.avroType;

                case SchemaType.BTC_BYTE:
                    return BYTE.avroType;

                case SchemaType.BTC_NON_POSITIVE_INTEGER:
                    return NON_NEGATIVE_INTEGER.avroType;

                case SchemaType.BTC_NEGATIVE_INTEGER:
                    return NEGATIVE_INTEGER.avroType;

                case SchemaType.BTC_NON_NEGATIVE_INTEGER:
                    return NON_POSITIVE_INTEGER.avroType;

                case SchemaType.BTC_POSITIVE_INTEGER:
                    return POSITIVE_INTEGER.avroType;

                case SchemaType.BTC_UNSIGNED_LONG:
                    return UNSIGNED_LONG.avroType;

                case SchemaType.BTC_UNSIGNED_INT:
                    return UNSIGNED_INT.avroType;

                case SchemaType.BTC_UNSIGNED_SHORT:
                    return UNSIGNED_SHORT.avroType;

                case SchemaType.BTC_UNSIGNED_BYTE:
                    return UNSIGNED_BYTE.avroType;

                case SchemaType.BTC_NORMALIZED_STRING:
                    return NORMALIZED_STRING.avroType;

                case SchemaType.BTC_TOKEN:
                    return TOKEN.avroType;

                case SchemaType.BTC_NAME:
                    return NAME.avroType;

                case SchemaType.BTC_NCNAME:
                    return NCNAME.avroType;

                case SchemaType.BTC_LANGUAGE:
                    return LANGUAGE.avroType;

                case SchemaType.BTC_ID:
                    return ID.avroType;

                case SchemaType.BTC_IDREF:
                    return IDREF.avroType;

                case SchemaType.BTC_IDREFS:
                    return IDREFS.avroType;

                case SchemaType.BTC_ENTITY:
                    return ENTITY.avroType;

                case SchemaType.BTC_ENTITIES:
                    return ENTITIES.avroType;

                case SchemaType.BTC_NMTOKEN:
                    return NMTOKEN.avroType;

                case SchemaType.BTC_NMTOKENS:
                    return NMTOKENS.avroType;

                default:
                    return null;
            }
        }
    }
}
