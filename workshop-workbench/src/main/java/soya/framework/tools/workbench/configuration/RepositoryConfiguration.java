package soya.framework.tools.workbench.configuration;

import org.apache.commons.io.FilenameUtils;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Configuration
public class RepositoryConfiguration {

    @Autowired
    private Environment environment;

    private File home;

    private File cmmHome;

    private File requirements;

    @PostConstruct
    void init() {
        home = new File("D:/github/workshop/install/repository");
        if (!home.exists()) {
            home = new File("C:/Workshop/Repository");
        }

        cmmHome = new File(home, "CMM");
        requirements = new File(home, "BusinessObjects");

        File cmmBod = new File(cmmHome, "BOD");
        File[] files = cmmBod.listFiles();
        for (File f : files) {
            if ("xsd".equalsIgnoreCase(FilenameUtils.getExtension(f.getName()))) {
                try {
                    BusinessObjectSchemaCache.getInstance().load(f);
                } catch (XmlException e) {
                    System.out.println("==================== failure: " + f);
                    e.printStackTrace();

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

    public File getRequirements() {
        return requirements;
    }
}
