package com.albertsons.edis.cms.readers;

import com.albertsons.edis.cms.ContentReader;
import com.albertsons.edis.cms.ContentResource;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import java.io.File;
import java.io.IOException;

public class XmlBeansReader implements ContentReader<SchemaTypeSystem> {

    @Override
    public SchemaTypeSystem read(ContentResource resource) throws IOException, ContentParserException {
        try {
            return XmlBeans.compileXsd(new XmlObject[]{
                    XmlObject.Factory.parse((File)resource.getResource())}, XmlBeans.getBuiltinTypeSystem(), null);
        } catch (XmlException e) {
            throw new ContentParserException(e);
        }
    }
}
