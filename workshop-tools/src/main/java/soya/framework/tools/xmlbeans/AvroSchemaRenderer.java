package soya.framework.tools.xmlbeans;

import org.apache.avro.Schema;
import soya.framework.tools.avro.XmlToAvroSchema;

public class AvroSchemaRenderer extends XmlSchemaBaseRenderer {

    @Override
    public String render(XmlSchemaBase base) {
        Schema schema = XmlToAvroSchema.fromXmlSchema(base.getSchemaTypeSystem());
        return schema.toString(true);
    }
}
