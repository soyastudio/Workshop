package com.albertsons.edis.cms;

import com.albertsons.edis.cms.readers.PlainTextReader;
import com.albertsons.edis.cms.readers.XmlBeansReader;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ContentRepositoryService {

    private static Map<String, ContentReader> defaultReaders = new HashMap<>();

    private File repositoryHome;
    private Map<String, ContentReader> readers;

    static {
        defaultReaders.put(".txt", new PlainTextReader());
        defaultReaders.put(".text", new PlainTextReader());
        defaultReaders.put(".xsd", new XmlBeansReader());
    }

    public ContentRepositoryService(File repositoryHome) {
        this.repositoryHome = repositoryHome;
        this.readers = new HashMap<>(defaultReaders);
    }

    public URL getRepositoryHome() {
        try {
            return repositoryHome.toURI().toURL();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public ContentResource<?> getContentResource(String path) {
        return new FileContentResource(path, new File(repositoryHome, path));
    }

    private ContentReader findReader(ContentResource resource) {
        String path = resource.getPath();
        String token = path;
        if (path.contains("/")) {
            int lastSlash = path.lastIndexOf("/");
            token = token.substring(lastSlash + 1);
        }

        if (readers.containsKey(token)) {
            return readers.get(token);

        } else if (token.contains(".")) {
            int point = token.lastIndexOf('.');
            token = token.substring(point);

            if (readers.containsKey(token.toLowerCase())) {
                return readers.get(token.toLowerCase());

            } else {
                return new PlainTextReader();

            }
        }
        return new PlainTextReader();
    }

    public static class FileContentResource extends ContentResourceWrapper<File> {
        protected FileContentResource(String path, File wrappedResource) {
            super(path, wrappedResource);
        }

        @Override
        public String getAsString() throws IOException {
            return IOUtils.toString(new FileInputStream(getResource()), Charset.defaultCharset());
        }

        @Override
        public InputStream getAsInputStream() throws IOException {
            return new FileInputStream(getResource());
        }


    }
}
