package com.albertsons.edis.cms.readers;


import com.albertsons.edis.cms.ContentReader;
import com.albertsons.edis.cms.ContentResource;

import java.io.IOException;

public class PlainTextReader implements ContentReader<String> {

    @Override
    public String read(ContentResource resource) throws IOException, ContentParserException {
        return resource.getAsString();
    }
}
