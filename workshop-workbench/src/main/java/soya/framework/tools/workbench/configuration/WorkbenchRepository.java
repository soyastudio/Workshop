package soya.framework.tools.workbench.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorkbenchRepository {
    private final File home;
    private File requirementHome;
    private File projectHome;

    public WorkbenchRepository(File home) {
        this.home = home;
        this.requirementHome = new File(home, "documents/requirements");
        this.projectHome = new File(home, "projects");
    }

    public File getHome() {
        return home;
    }

    public File getRequirementHome() {
        return requirementHome;
    }

    public List<String> projects() {
        List list = new ArrayList();
        File[] files = projectHome.listFiles();
        for(File file: files) {
            if(file.isDirectory() && new File(file, "build.xml").exists()) {
                list.add(file.getName());
            }
        }

        return list;
    }

    public File projectDirectory(String project) {
        return new File(projectHome, project);
    }
}
