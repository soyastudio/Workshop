package com.albertsons.edis.tools.xmlbeans;

import org.apache.xmlbeans.SchemaTypeSystem;

public class SampleXmlRenderer extends XmlSchemaBaseRenderer {

    @Override
    public String render(XmlSchemaBase base) {
        SchemaTypeSystem schemaTypeSystem = base.getSchemaTypeSystem();
        return XmlSchemaBase.createSampleForType(schemaTypeSystem.documentTypes()[0]);
    }
}
