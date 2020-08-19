package soya.framework.tools.xmlbeans;

import org.apache.xmlbeans.SchemaTypeSystem;

public class SampleXmlRenderer implements Buffalo.Renderer<XmlSchemaBase> {

    @Override
    public String render(XmlSchemaBase base) {
        SchemaTypeSystem schemaTypeSystem = base.getSchemaTypeSystem();
        return XmlSchemaBase.createSampleForType(schemaTypeSystem.documentTypes()[0]);
    }
}
