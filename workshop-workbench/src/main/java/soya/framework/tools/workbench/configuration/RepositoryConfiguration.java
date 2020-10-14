package soya.framework.tools.workbench.configuration;

import org.apache.commons.io.FilenameUtils;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

@Configuration
public class RepositoryConfiguration {

    @Autowired
    private Environment environment;

    private File home;
    private File cmmHome;
    private File projectHome;

    @PostConstruct
    void init() throws Exception {
        File userHome = new File(System.getProperty("user.home"));
        File soyaHome = new File(userHome, ".soya");
        if (!soyaHome.exists()) {
            soyaHome.mkdir();
        }

        File workshopConfigFile = new File(soyaHome, "workshop.properties");
        if (!workshopConfigFile.exists()) {
            workshopConfigFile.createNewFile();
        }

        Properties properties = new Properties();
        properties.load(new FileInputStream(workshopConfigFile));

        Enumeration<?> enumeration = properties.propertyNames();
        while(enumeration.hasMoreElements()) {
            String propName = (String) enumeration.nextElement();
            String propValue = properties.getProperty(propName);

            System.setProperty(propName, propValue);
        }

        if(properties.getProperty("soya.framework.workshop.repository.home") != null) {
            home = new File(properties.getProperty("soya.framework.workshop.repository.home"));
        }

        cmmHome = new File(home, "CMM");
        projectHome = new File(home, "BusinessObjects");

        File cmmBod = new File(cmmHome, "BOD");
        File[] files = cmmBod.listFiles();
        for (File f : files) {
            if ("xsd".equalsIgnoreCase(FilenameUtils.getExtension(f.getName()))) {
                try {
                    BusinessObjectSchemaCache.getInstance().load(f);

                } catch (XmlException e) {
                    System.out.println("==================== failure: " + f);

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }

    }

    @Bean
    public WorkbenchRepository workbenchRepository() {
        String repoHome = environment.getProperty("workbench.repository");
        File dir = new File(repoHome);
        return new WorkbenchRepository(dir);
    }

    public File getHome() {
        return home;
    }

    public File getCmmHome() {
        return cmmHome;
    }

    public File getProjectHome() {
        return projectHome;
    }
}
