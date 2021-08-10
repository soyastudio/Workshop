package com.abs.edis.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

public class ProjectManager {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Context context;

    public ProjectManager(Properties configuration) {
        this.context = new Context(configuration);
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("edis.project.home", "C:/Users/qwen002/IBM/IIBT10/workspace/AppBuild/BusinessObjects");
        properties.setProperty("edis.schema.service", "http://localhost:7800/edis-schema");
        properties.setProperty("edis.mappings.service", "http://localhost:7800/edis-schema");

        ProjectManager manager = new ProjectManager(properties);
        Session session = new Session(manager.context, "GroceryOrderFulfillment");

        try {
            new CreateCommand().execute(session);

            System.out.println(session.output);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    static class Context {
        private Properties configuration;
        private File home;

        private Context(Properties configuration) {
            this.configuration = configuration;

            if (configuration.containsKey("edis.project.home")) {
                home = new File(configuration.getProperty("edis.project.home"));
            }
        }
    }

    static class Session {
        private Context context;
        private String name;
        private File projectHome;

        private String output;

        private Session(Context context, String projectName) {
            this.context = context;
            this.name = projectName;

            this.projectHome = new File(context.home, projectName);
            if (!projectHome.exists()) {
                projectHome.mkdir();
            }
        }
    }

    interface Command {
        void execute(Session session) throws Exception;
    }

    static class CreateCommand implements Command {

        @Override
        public void execute(Session session) throws Exception {
            if(!session.projectHome.exists()) {
                session.projectHome.mkdir();
            }

            // project
            File projectFile = new File(session.projectHome, "project.json");
            if(!projectFile.exists()) {
                projectFile.createNewFile();
            }

            Project project = new Project(session.name);
            session.output = GSON.toJson(project);

        }
    }

    static class InitializeCommand implements Command {

        @Override
        public void execute(Session session) throws Exception {


        }
    }
}
