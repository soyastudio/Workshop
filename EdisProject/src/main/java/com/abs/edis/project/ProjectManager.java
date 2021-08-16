package com.abs.edis.project;

import com.google.gson.*;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class ProjectManager {
    private static String REQUIREMENT_DIR = "requirement";
    private static String WORK_DIR = "work";
    private static String TEST_DIR = "test";
    private static String HISTORY_DIR = "history";

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, Command> COMMANDS;

    static {
        COMMANDS = new LinkedHashMap<>();
        Class<?>[] classes = ProjectManager.class.getDeclaredClasses();
        for (Class<?> c : classes) {
            if (Command.class.isAssignableFrom(c) && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
                String name = c.getSimpleName();
                if (name.endsWith("Command")) {
                    name = name.substring(0, name.lastIndexOf("Command"));

                }

                try {
                    Command processor = (Command) c.newInstance();
                    COMMANDS.put(name.toUpperCase(), processor);

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Context context;

    public ProjectManager(Properties configuration) {
        this.context = new Context(configuration);
    }

    public String process(Node node) {
        JsonElement jsonElement = estimate(node);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            RequestMessage msg = GSON.fromJson(jsonElement, RequestMessage.class);
            try {
                return COMMANDS.get(msg.command.toUpperCase()).execute(new Session(context, msg));

            } catch (Exception e) {
                return e.getMessage();
            }
        }

        return null;
    }

    private static JsonElement estimate(Node node) {
        if (node.getTextContent() != null) {
            return new JsonPrimitive(node.getTextContent());

        } else if (node.getChildNodes().getLength() > 0) {
            if ("Item".equals(node.getFirstChild().getNodeName())) {
                JsonArray arr = new JsonArray();
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    arr.add(estimate(child));
                }

                return arr;

            } else {
                JsonObject obj = new JsonObject();
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    obj.add(child.getNodeName(), estimate(child));
                }

                return obj;
            }
        }

        return null;

    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        InputStream inputStream = ProjectManager.class.getClassLoader().getResourceAsStream("workspace.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProjectManager manager = new ProjectManager(properties);
        Session session = new Session(manager.context, "GroceryOrder");

        try {
            System.out.println(new MappingCommand().execute(session));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Context {
        private Properties configuration;

        private File schemaHome;
        private File srcHome;
        private File deployHome;
        private File buildHome;

        private SchemaService schemaService;
        private MappingService mappingService;

        private Context(Properties configuration) {
            this.configuration = configuration;

            schemaHome = new File(configuration.getProperty("edis.cmm.schema.home"));
            if (!schemaHome.exists()) {
                throw new RuntimeException(schemaHome.getAbsolutePath() + " does not exist.");
            }

            srcHome = new File(configuration.getProperty("edis.iib.src.home"));
            if (!srcHome.exists()) {
                throw new RuntimeException(srcHome.getAbsolutePath() + " does not exist.");
            }

            deployHome = new File(configuration.getProperty("edis.iib.deploy.home"));
            if (!deployHome.exists()) {
                throw new RuntimeException(deployHome.getAbsolutePath() + " does not exist.");
            }

            buildHome = new File(configuration.getProperty("edis.project.build.home"));
            if (!buildHome.exists()) {
                throw new RuntimeException(buildHome.getAbsolutePath() + " does not exist.");
            }

            this.schemaService = new SchemaService(schemaHome, configuration.getProperty("edis.service.schema.url"));
            this.mappingService = new MappingService(buildHome, configuration.getProperty("edis.service.mapping.url"));

        }

        public SchemaService getSchemaService() {
            return schemaService;
        }

        public MappingService getMappingService() {
            return mappingService;
        }
    }

    static class Session {
        private Context context;
        private String name;
        private Project settings;

        private File projectHome;
        private File requirementDir;
        private File workDir;
        private File testDir;
        private File historyDir;

        private Project project;

        private Session(Context context, RequestMessage requestMessage) {
            this(context, requestMessage.project);
            this.settings = requestMessage.settings;
        }

        private Session(Context context, String projectName) {
            this.context = context;
            this.name = projectName;

            this.projectHome = new File(context.buildHome, name);
            this.requirementDir = new File(projectName, REQUIREMENT_DIR);
            this.workDir = new File(projectHome, WORK_DIR);
            this.testDir = new File(projectHome, TEST_DIR);
            this.historyDir = new File(projectHome, HISTORY_DIR);

            if (!projectHome.exists()) {
                projectHome.mkdirs();

                this.requirementDir = new File(projectHome, REQUIREMENT_DIR);
                if (!requirementDir.exists()) {
                    requirementDir.mkdirs();
                }

                if (!workDir.exists()) {
                    workDir.mkdirs();
                }

                if (!testDir.exists()) {
                    testDir.mkdirs();
                }

                if (!historyDir.exists()) {
                    historyDir.mkdirs();
                }
            }

            File projectFile = new File(projectHome, "project.json");
            if (projectFile.exists()) {
                try {
                    Project project = GSON.fromJson(new FileReader(projectFile), Project.class);
                    if (project.isEnabled()) {
                        this.project = project;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class RequestMessage {
        private String command;
        private String project;
        private Project settings;
    }

    static class SchemaService {

        private File baseDir;
        private String url;

        public SchemaService(File baseDir, String url) {
            this.baseDir = baseDir;
            this.url = url;
        }

        public String call(String cmd, Project project) throws IOException {

            JsonObject object = new JsonObject();
            File file = new File(baseDir, project.getMappings().getSchema());
            object.addProperty("command", cmd.toUpperCase());
            object.addProperty("file", file.getAbsolutePath());

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(
                    GSON.toJson(object), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();

            return response.body().string();
        }

    }

    static class MappingService {
        private File baseDir;
        private String url;

        public MappingService(File baseDir, String url) {
            this.baseDir = baseDir;
            this.url = url;
        }

        private String call(String cmd, Project project) throws IOException {

            JsonObject object = new JsonObject();
            object.addProperty("command", cmd.toUpperCase());
            object.addProperty("context", baseDir.getAbsolutePath() + "/" + project.getName());

            JsonObject files = new JsonObject();
            files.addProperty("xpathDataType", project.getMappings().getXpathDataType());
            files.addProperty("mappingFile", project.getMappings().getMappingFile());

            object.add("files", files);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(
                    GSON.toJson(object), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();

            return response.body().string();
        }
    }

    interface Command {
        String execute(Session session) throws Exception;
    }

    static abstract class ServiceCommand implements Command {
        protected String getCommand() {
            String cmd = getClass().getSimpleName();
            if (cmd.endsWith("Command")) {
                cmd = cmd.substring(0, cmd.lastIndexOf("Command"));
            }

            return cmd.toUpperCase();
        }
    }

    static abstract class SchemaServiceCommand extends ServiceCommand {

        @Override
        public String execute(Session session) throws Exception {
            return session.context.getSchemaService().call(getCommand(), session.project);
        }

    }

    static class XPathDataTypeCommand extends SchemaServiceCommand {

    }

    static class XPathJsonTypeCommand extends SchemaServiceCommand {

    }

    static class SampleXmlCommand extends SchemaServiceCommand {

    }

    static class AvroSchemaCommand extends SchemaServiceCommand {

    }

    static class MappingServiceCommand extends ServiceCommand {

        @Override
        public String execute(Session session) throws Exception {
            return session.context.getMappingService().call(getCommand(), session.project);
        }
    }

    static class MappingCommand extends MappingServiceCommand {

    }

    static class UnknownMappingsCommand extends MappingServiceCommand {}

    static class XPathJsonTypeMappingsCommand extends MappingServiceCommand {}

    static class JsonTypeMappingsCommand extends MappingServiceCommand {}

    static class ArrayMappingsCommand extends MappingServiceCommand {}

    static class ConstructCommand extends MappingServiceCommand {}

    static class CreateCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            if (!session.projectHome.exists()) {
                session.projectHome.mkdirs();
            }

            // project
            File projectFile = new File(session.projectHome, "project.json");
            if (!projectFile.exists()) {
                projectFile.createNewFile();

                Project project = new Project(session.name);
                String contents = GSON.toJson(project);

                FileUtils.write(projectFile, contents, Charset.defaultCharset());
                return contents;

            } else {
                return IOUtils.toString(new FileInputStream(projectFile), Charset.defaultCharset());
            }

        }
    }

    static class InitializeCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            SchemaService schemaService = session.context.getSchemaService();
            Project project = session.project;
            Project.Mappings mappings = project.getMappings();

            File xpathDataType = new File(session.workDir, mappings.getXpathDataType());
            if (!xpathDataType.exists()) {
                xpathDataType.createNewFile();
                String dataType = schemaService.call("XPathDataType", session.project);
                FileUtils.write(xpathDataType, dataType, Charset.defaultCharset());
            }

            File jsonDataType = new File(session.workDir, mappings.getXpathJsonType());
            if (!jsonDataType.exists()) {
                jsonDataType.createNewFile();
                String dataType = schemaService.call("XpathJsonType", session.project);
                FileUtils.write(jsonDataType, dataType, Charset.defaultCharset());
            }

            File xpathMappings = new File(session.workDir, mappings.getXpathMappings());
            if (!xpathMappings.exists()) {
                xpathMappings.createNewFile();

            }

            File adjustment = new File(session.workDir, mappings.getXpathMappingAdjustments());
            if (!adjustment.exists()) {
                adjustment.createNewFile();
            }

            return null;
        }
    }
}
