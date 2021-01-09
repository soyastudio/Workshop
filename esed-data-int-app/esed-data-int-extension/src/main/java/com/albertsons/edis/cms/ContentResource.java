package com.albertsons.edis.cms;

import java.io.IOException;
import java.io.InputStream;

public interface ContentResource<T> {
    String getPath();

    T getResource();

    String getAsString() throws IOException;

    InputStream getAsInputStream() throws IOException;
}
