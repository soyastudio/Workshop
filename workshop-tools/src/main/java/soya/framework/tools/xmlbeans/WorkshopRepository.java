package soya.framework.tools.xmlbeans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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

    public static InputStream fromResource(String path) {
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
}
