package soya.framework.tools.xmlbeans;

import org.apache.xmlbeans.SchemaTypeSystem;

import java.io.File;

public class XmlSchemaBaseLoader implements Buffalo.BaseLoader<XmlSchemaBase> {

    private String source;

    @Override
    public XmlSchemaBase create() {
        try {
            SchemaTypeSystem sts = XmlBeansUtils.getSchemaTypeSystem(WorkshopRepository.getFile(source));
            return XmlSchemaBase.builder().schemaTypeSystem(sts).create();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
