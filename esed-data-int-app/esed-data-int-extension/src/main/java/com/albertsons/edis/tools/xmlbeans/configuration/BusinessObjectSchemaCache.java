package com.albertsons.edis.tools.xmlbeans.configuration;

import org.apache.avro.Schema;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlException;
import soya.framework.tools.avro.XmlToAvroSchema;
import soya.framework.tools.xmlbeans.XmlBeansUtils;
import soya.framework.tools.xmlbeans.XmlSchemaBase;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BusinessObjectSchemaCache {
    private static BusinessObjectSchemaCache me;

    private Map<String, QName> nameMappings = new ConcurrentHashMap<>();
    private Map<QName, BusinessObjectSchema> schemas = new ConcurrentHashMap<>();

    static {
        me = new BusinessObjectSchemaCache();
    }

    private BusinessObjectSchemaCache() {
    }

    public List<String> definedBusinessObjects() {
        List<String> list = new ArrayList<>(nameMappings.keySet());
        Collections.sort(list);
        return list;
    }

    public boolean contains(String bod) {
        return nameMappings.containsKey(bod);
    }

    public SchemaTypeSystem getXmlSchemaTypeSystem(String bod) {
        if(!contains(bod)) {
            return null;
        }
        return schemas.get(nameMappings.get(bod)).sts;
    }

    public String generateXml(String bod) {
        if(!contains(bod)) {
            return null;
        }
        SchemaTypeSystem sts = schemas.get(nameMappings.get(bod)).sts;
        SchemaType[] globalElems = sts.documentTypes();
        SchemaType elem = globalElems[0];

        return XmlSchemaBase.createSampleForType(elem);
    }

    public Schema getAvroSchema(String bod) {
        if(!contains(bod)) {
            return null;
        }

        return schemas.get(nameMappings.get(bod)).avroSchema;
    }

    protected void load(File file) throws IOException, XmlException {
        try {
            BusinessObjectSchema bos = new BusinessObjectSchema(XmlBeansUtils.getSchemaTypeSystem(file));
            nameMappings.put(bos.name, bos.qName);
            schemas.put(bos.qName, bos);
        } catch (RuntimeException ex) {
            // TODO:
        }
    }

    public static BusinessObjectSchemaCache getInstance() {
        return me;
    }

    private static class BusinessObjectSchema {
        private final SchemaTypeSystem sts;
        private final SchemaType rootType;

        private final String name;
        private final QName qName;
        private final Schema avroSchema;

        private BusinessObjectSchema(SchemaTypeSystem sts) {
            this.sts = sts;

            this.rootType = sts.globalTypes()[0];
            this.qName = rootType.getName();
            this.name = qName.getLocalPart();
            this.avroSchema = XmlToAvroSchema.fromXmlSchema(sts);
        }
    }
}
