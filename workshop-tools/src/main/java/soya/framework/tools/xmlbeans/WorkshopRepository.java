package soya.framework.tools.xmlbeans;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

public class WorkshopRepository {
    private static File repositoryHome;

    static {
        repositoryHome = new File(System.getProperty("soya.framework.workshop.repository.home"));
    }

    public static File getFile(String path) {
        File file = new File(repositoryHome, path);
        if(!file.exists()) {
            file = new File(path);
        }

        if(!file.exists()) {
            throw new IllegalArgumentException("Cannot find file: " + path);
        }

        return file;
    }

    public static InputStream getResourceAsInputStream(String path) {
        try {
            return new FileInputStream(new File(repositoryHome, path));

        } catch (FileNotFoundException e) {
            try {
                return new FileInputStream(new File(path));
            } catch (FileNotFoundException fileNotFoundException) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static String getResourceAsString(String path) {
        try {
            return IOUtils.toString(getResourceAsInputStream(path), Charset.defaultCharset());

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
