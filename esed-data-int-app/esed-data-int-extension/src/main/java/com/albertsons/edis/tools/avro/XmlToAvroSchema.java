package com.albertsons.edis.tools.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tools.xmlbeans.XmlBeansUtils;

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
        for (SchemaProperty sp : schemaType.getElementProperties()) {
            SchemaType st = sp.getType();
            if (st.isSimpleType()) {
                Schema.Type pt = XmlToAvroSchema.BuildInTypeMapping.fromXmlTypeCode(XmlBeansUtils.getXMLBuildInType(sp.getType()).getCode());
                if (sp.getMaxOccurs() == null || sp.getMaxOccurs().intValue() > 1) {
                    // Array of Simple Type:
                    assembler.name(sp.getName().getLocalPart()).type(Schema.createArray(Schema.create(pt)));

                } else if (sp.getMinOccurs() == null || sp.getMinOccurs().intValue() == 0) {
                    assembler.name(sp.getName().getLocalPart()).type(Schema.create(pt))
                            .noDefault();
                } else {
                    assembler.name(sp.getName().getLocalPart()).type(Schema.create(pt))
                            .noDefault();
                }
            } else {
                String name = st.isAnonymousType() ? sp.getName().getLocalPart() : sp.getType().getName().getLocalPart();
                SchemaBuilder.FieldAssembler sub = SchemaBuilder.record(name).namespace(DEFAULT_NAMESPACE).fields();
                if (sp.getMaxOccurs() == null || sp.getMaxOccurs().intValue() > 1) {
                    // Array of Complex Type:
                    assemble(sp.getType(), sub);
                    assembler.name(sp.getName().getLocalPart()).type(Schema.createArray((Schema) sub.endRecord())).noDefault();

                } else {
                    assemble(sp.getType(), sub);
                    assembler.name(sp.getName().getLocalPart()).type((Schema) sub.endRecord()).noDefault();

                }
            }
        }

        return assembler;
    }

    public static enum BuildInTypeMapping {
        BOOLEAN(XmlBeansUtils.XMLBuildInType.BOOLEAN, Schema.Type.BOOLEAN),
        BASE_64_BINARY(XmlBeansUtils.XMLBuildInType.BASE_64_BINARY, Schema.Type.BYTES),
        HEX_BINARY(XmlBeansUtils.XMLBuildInType.HEX_BINARY, Schema.Type.BYTES),
        ANY_URI(XmlBeansUtils.XMLBuildInType.ANY_URI, Schema.Type.STRING),
        QNAME(XmlBeansUtils.XMLBuildInType.QNAME, Schema.Type.STRING),
        NOTATION(XmlBeansUtils.XMLBuildInType.NOTATION, Schema.Type.STRING),
        FLOAT(XmlBeansUtils.XMLBuildInType.FLOAT, Schema.Type.FLOAT),
        DOUBLE(XmlBeansUtils.XMLBuildInType.DOUBLE, Schema.Type.DOUBLE),
        DECIMAL(XmlBeansUtils.XMLBuildInType.DECIMAL, Schema.Type.DOUBLE),
        STRING(XmlBeansUtils.XMLBuildInType.STRING, Schema.Type.STRING),
        DURATION(XmlBeansUtils.XMLBuildInType.DURATION, Schema.Type.LONG),
        DATE_TIME(XmlBeansUtils.XMLBuildInType.DATE_TIME, Schema.Type.STRING),
        TIME(XmlBeansUtils.XMLBuildInType.TIME, Schema.Type.STRING),
        DATE(XmlBeansUtils.XMLBuildInType.DATE, Schema.Type.STRING),
        G_YEAR_MONTH(XmlBeansUtils.XMLBuildInType.G_YEAR_MONTH, Schema.Type.STRING),
        G_YEAR(XmlBeansUtils.XMLBuildInType.G_YEAR, Schema.Type.STRING),
        G_MONTH_DAY(XmlBeansUtils.XMLBuildInType.G_MONTH_DAY, Schema.Type.STRING),
        G_DAY(XmlBeansUtils.XMLBuildInType.G_DAY, Schema.Type.STRING),
        G_MONTH(XmlBeansUtils.XMLBuildInType.G_MONTH, Schema.Type.STRING),
        INTEGER(XmlBeansUtils.XMLBuildInType.INTEGER, Schema.Type.LONG),
        LONG(XmlBeansUtils.XMLBuildInType.LONG, Schema.Type.LONG),
        INT(XmlBeansUtils.XMLBuildInType.INT, Schema.Type.INT),
        SHORT(XmlBeansUtils.XMLBuildInType.SHORT, Schema.Type.INT),
        BYTE(XmlBeansUtils.XMLBuildInType.HEX_BINARY, Schema.Type.STRING),
        NON_POSITIVE_INTEGER(XmlBeansUtils.XMLBuildInType.NON_POSITIVE_INTEGER, Schema.Type.LONG),
        NEGATIVE_INTEGER(XmlBeansUtils.XMLBuildInType.NEGATIVE_INTEGER, Schema.Type.LONG),
        NON_NEGATIVE_INTEGER(XmlBeansUtils.XMLBuildInType.NON_NEGATIVE_INTEGER, Schema.Type.LONG),
        POSITIVE_INTEGER(XmlBeansUtils.XMLBuildInType.POSITIVE_INTEGER, Schema.Type.LONG),
        UNSIGNED_LONG(XmlBeansUtils.XMLBuildInType.UNSIGNED_LONG, Schema.Type.LONG),
        UNSIGNED_INT(XmlBeansUtils.XMLBuildInType.UNSIGNED_INT, Schema.Type.LONG),
        UNSIGNED_SHORT(XmlBeansUtils.XMLBuildInType.UNSIGNED_SHORT, Schema.Type.INT),
        UNSIGNED_BYTE(XmlBeansUtils.XMLBuildInType.UNSIGNED_BYTE, Schema.Type.INT),
        NORMALIZED_STRING(XmlBeansUtils.XMLBuildInType.STRING, Schema.Type.STRING),
        TOKEN(XmlBeansUtils.XMLBuildInType.TOKEN, Schema.Type.STRING),
        NAME(XmlBeansUtils.XMLBuildInType.NAME, Schema.Type.STRING),
        NCNAME(XmlBeansUtils.XMLBuildInType.NCNAME, Schema.Type.STRING),
        LANGUAGE(XmlBeansUtils.XMLBuildInType.LANGUAGE, Schema.Type.STRING),
        ID(XmlBeansUtils.XMLBuildInType.ID, Schema.Type.STRING),
        IDREF(XmlBeansUtils.XMLBuildInType.IDREF, Schema.Type.STRING),
        IDREFS(XmlBeansUtils.XMLBuildInType.IDREFS, Schema.Type.STRING),
        ENTITY(XmlBeansUtils.XMLBuildInType.ENTITY, Schema.Type.STRING),
        ENTITIES(XmlBeansUtils.XMLBuildInType.ENTITIES, Schema.Type.STRING),
        NMTOKEN(XmlBeansUtils.XMLBuildInType.NMTOKEN, Schema.Type.STRING),
        NMTOKENS(XmlBeansUtils.XMLBuildInType.NMTOKENS, Schema.Type.STRING);

        private final XmlBeansUtils.XMLBuildInType xmlBuildInType;
        private final Schema.Type avroType;

        private BuildInTypeMapping(XmlBeansUtils.XMLBuildInType xmlBuildInType, Schema.Type avroType) {
            this.xmlBuildInType = xmlBuildInType;
            this.avroType = avroType;
        }

        public static Schema.Type fromXmlTypeCode(int code) {
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
