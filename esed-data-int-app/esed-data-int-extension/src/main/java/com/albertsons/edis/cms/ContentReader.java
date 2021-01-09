package com.albertsons.edis.cms;

import java.io.IOException;

public interface ContentReader<T> {
    T read(ContentResource resource) throws IOException, ContentParserException;

    public static class ContentParserException extends Exception {
        public ContentParserException(Throwable cause) {
            super(cause);
        }
    }
}
