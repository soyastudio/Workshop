package com.albertsons.edis.tools.xmlbeans;

import org.apache.xmlbeans.SchemaTypeSystem;

public class XmlSchemaBaseLoader implements Buffalo.BaseLoader<XmlSchemaBase> {

    private String schema;

    @Override
    public XmlSchemaBase create() {
        try {
            SchemaTypeSystem sts = XmlBeansUtils.getSchemaTypeSystem(WorkshopRepository.getFile(schema));
            return XmlSchemaBase.builder().schemaTypeSystem(sts).create();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
