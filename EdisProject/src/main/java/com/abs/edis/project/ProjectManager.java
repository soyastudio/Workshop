package com.abs.edis.project;

import com.google.gson.*;
import com.samskivert.mustache.Mustache;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ProjectManager {

    private static String REQUIREMENT_DIR = "requirement";
    private static String WORK_DIR = "work";
    private static String TEST_DIR = "test";
    private static String HISTORY_DIR = "history";

    private static String XPATH_SCHEMA_FILE = "xpath-schema.properties";
    private static String XPATH_MAPPING_FILE = "xpath-mapping.properties";
    private static String XPATH_ADJUSTMENT_FILE = "xpath-adjustment.properties";

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
                String cmd = msg.command.toUpperCase();
                if (cmd.contains(cmd)) {
                    return COMMANDS.get(cmd).execute(new Session(context, msg));
                } else {
                    return "Command does not exist: " + cmd;
                }

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

    private static String mustache(InputStream template, Project project) throws IOException {
        return Mustache.compiler().compile(
                new InputStreamReader(template)).execute(JsonUtils.toMap(GSON.toJsonTree(project).getAsJsonObject()));
    }

    private static String mustache(String template, JsonObject data) throws IOException {
        InputStream inputStream = ProjectManager.class.getClassLoader().getResourceAsStream(template);
        return Mustache.compiler().compile(
                new InputStreamReader(inputStream)).execute(JsonUtils.toMap(data));
    }

    private static void mustache(File destFile, InputStream template, Project project) throws IOException {
        FileUtils.writeByteArrayToFile(destFile,
                Mustache.compiler().compile(
                        new InputStreamReader(template)).execute(JsonUtils.toMap(GSON.toJsonTree(project).getAsJsonObject())).getBytes());
    }

    static class Context {

        private Properties configuration;

        private File schemaHome;
        private File srcHome;
        private File deployHome;
        private File buildHome;

        private SchemaService schemaService;
        private MappingService mappingService;
        private KafkaService kafkaService;

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
            this.kafkaService = new KafkaService(configuration.getProperty("edis.service.kafka.url"));

        }

        public SchemaService getSchemaService() {
            return schemaService;
        }

        public MappingService getMappingService() {
            return mappingService;
        }

        public KafkaService getKafkaService() {
            return kafkaService;
        }
    }

    static class Session {
        private Context context;
        private RequestMessage requestMessage;
        private ResponseMessage responseMessage;

        private String name;
        private File projectHome;
        private File requirementDir;
        private File workDir;
        private File testDir;
        private File historyDir;

        private File schemaFile;
        private File mappingFile;

        private Project project;

        private Session(Context context, RequestMessage requestMessage) {
            this.context = context;
            this.requestMessage = requestMessage;

            this.responseMessage = new ResponseMessage();
            this.responseMessage.command = requestMessage.command;
            this.responseMessage.project = requestMessage.project;

            if (requestMessage.project != null) {
                this.name = requestMessage.project;

                this.projectHome = new File(context.buildHome, name);
                File projectFile = new File(projectHome, "project.json");

                if (this.projectHome.exists() && projectFile.exists()) {
                    if (projectFile.exists()) {
                        try {
                            this.project = GSON.fromJson(new FileReader(projectFile), Project.class);

                            this.requirementDir = new File(projectHome, REQUIREMENT_DIR);
                            this.workDir = new File(projectHome, WORK_DIR);
                            this.testDir = new File(projectHome, TEST_DIR);
                            this.historyDir = new File(projectHome, HISTORY_DIR);

                            this.schemaFile = new File(context.schemaHome, project.getSchemaFile());
                            this.mappingFile = new File(requirementDir, project.getMappingFile());

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }

        public void log(String task, String result, String msg) {
            responseMessage.logs.add(new Logging(task, result, msg));
        }

        public void log(String task, Exception e) {
            responseMessage.logs.add(new Logging(task, "error", e.getMessage()));
        }
    }

    static class RequestMessage {
        private String command;
        private JsonObject parameters;
        private String project;
        private JsonElement message;
    }

    static class ResponseMessage {
        private String command;
        private String project;
        private JsonElement message;
        private List<Logging> logs = new ArrayList<>();
    }

    static class Logging {
        private String task;
        private String result;
        private String message;

        public Logging(String task, String result, String message) {
            this.task = task;
            this.result = result;
            this.message = message;
        }
    }

    static class SchemaService {

        private File baseDir;
        private String url;

        public SchemaService(File baseDir, String url) {
            this.baseDir = baseDir;
            this.url = url;
        }

        public String call(String cmd, String path) throws IOException {
            JsonObject object = new JsonObject();
            object.addProperty("command", cmd);
            object.addProperty("file", path);

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

        public String call(String cmd, Project project) throws IOException {

            JsonObject object = new JsonObject();
            File file = new File(baseDir, project.getSchemaFile());
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
            object.addProperty("mappingFile", project.getMappingFile());

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

    static class KafkaService {
        private String url;

        public KafkaService(String url) {
            this.url = url;
        }

        public String call(String commandLine, JsonElement input) throws IOException {

            JsonObject object = new JsonObject();
            object.addProperty("commandLine", commandLine);

            object.add("input", input);

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

    // Global
    static abstract class ContextCommand implements Command {
    }

    static class HelpCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            String result = null;
            RequestMessage requestMessage = session.requestMessage;
            if (requestMessage.parameters != null) {
                JsonObject params = requestMessage.parameters.getAsJsonObject();
                if (params.get("r") != null) {
                    // TODO:
                }

            }

            if (result == null) {
                JsonObject jsonObject = new JsonObject();

                JsonObject configuration = new JsonObject();
                jsonObject.add("configuration", configuration);

                JsonArray contextCommands = new JsonArray();
                jsonObject.add("context-commands", contextCommands);

                JsonArray schemaServices = new JsonArray();
                jsonObject.add("schema-commands", schemaServices);

                JsonArray mappingServices = new JsonArray();
                jsonObject.add("mapping-commands", mappingServices);

                JsonArray kafkaServices = new JsonArray();
                jsonObject.add("kafka-commands", kafkaServices);

                JsonArray projectManagement = new JsonArray();
                jsonObject.add("project-commands", projectManagement);

                COMMANDS.entrySet().forEach(e -> {
                    Command cmd = e.getValue();
                    if (cmd instanceof ContextCommand) {
                        contextCommands.add(e.getKey());

                    } else if (cmd instanceof SchemaServiceCommand) {
                        schemaServices.add(e.getKey());

                    } else if (cmd instanceof MappingServiceCommand) {
                        mappingServices.add(e.getKey());

                    } else if (cmd instanceof KafkaServiceCommand) {
                        kafkaServices.add(e.getKey());

                    } else if (cmd instanceof AbstractProjectCommand) {
                        projectManagement.add(e.getKey());

                    }
                });

                result = GSON.toJson(jsonObject);
            }

            return result;
        }
    }

    static class BodListCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            List<String> list = new ArrayList<>();
            Context ctx = session.context;

            File bod = new File(ctx.schemaHome, "BOD");
            File[] files = bod.listFiles();
            for (File f : files) {
                if (f.getName().endsWith(".xsd")) {
                    list.add(f.getName());
                }
            }

            return GSON.toJson(list);
        }
    }

    static class CmmCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            File bod = new File(session.context.schemaHome, "BOD");
            String path = bod.getAbsoluteFile() + "/" + session.requestMessage.message.getAsString();
            if (!path.endsWith(".xsd")) {
                path = path + ".xsd";
            }

            return session.context.schemaService.call("XPathDataType", path);
        }
    }

    static class JsonSchemaCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            File bod = new File(session.context.schemaHome, "BOD");
            String path = bod.getAbsoluteFile() + "/" + session.requestMessage.message.getAsString();
            if (!path.endsWith(".xsd")) {
                path = path + ".xsd";
            }

            return session.context.schemaService.call("XPathJsonType", path);
        }
    }

    static class SampleCmmCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            File bod = new File(session.context.schemaHome, "BOD");
            String path = bod.getAbsoluteFile() + "/" + session.requestMessage.message.getAsString();
            if (!path.endsWith(".xsd")) {
                path = path + ".xsd";
            }

            return session.context.schemaService.call("SampleXml", path);
        }
    }

    static class AvscCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            File bod = new File(session.context.schemaHome, "BOD");
            String path = bod.getAbsoluteFile() + "/" + session.requestMessage.message.getAsString();
            if (!path.endsWith(".xsd")) {
                path = path + ".xsd";
            }

            return session.context.schemaService.call("AvroSchema", path);
        }
    }

    static class SampleAvroCommand extends ContextCommand {
        @Override
        public String execute(Session session) throws Exception {
            File bod = new File(session.context.schemaHome, "BOD");
            String path = bod.getAbsoluteFile() + "/" + session.requestMessage.message.getAsString();
            if (!path.endsWith(".xsd")) {
                path = path + ".xsd";
            }

            return session.context.schemaService.call("SampleAvro", path);
        }
    }

    static class ProjectListCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            List<String> list = new ArrayList<>();
            Context ctx = session.context;

            File[] files = ctx.buildHome.listFiles();
            for (File dir : files) {
                File proj = new File(dir, "project.json");
                if (proj.exists()) {
                    list.add(dir.getName());
                }
            }

            return GSON.toJson(list);
        }
    }

    static class NewProjectCommand extends ContextCommand {

        @Override
        public String execute(Session session) {

            RequestMessage request = session.requestMessage;
            String name = request.project;
            String source = null;
            String version = null;
            if (request.parameters == null) {
                return "'parameters' is required.";

            } else {
                JsonObject parameters = request.parameters;
                source = parameters.get("source") != null ? parameters.get("source").getAsString() : null;
                version = parameters.get("version") != null ? parameters.get("version").getAsString() : null;
            }

            if (name == null) {
                return "'project' need specified.";
            }

            if (source == null) {
                return "'source' need specified through parameters";
            }

            if (version == null) {
                return "'version' need specified through parameters";
            }

            if (session.projectHome.exists()) {
                return "Project '" + name + "' already exists";

            } else {
                session.projectHome.mkdirs();
                session.project = new Project(name, source, version);

                // project
                try {
                    File projectFile = new File(session.projectHome, "project.json");
                    projectFile.createNewFile();

                    String contents = GSON.toJson(session.project);
                    FileUtils.write(projectFile, contents, Charset.defaultCharset());

                    session.responseMessage.message = GSON.toJsonTree(session.project);

                } catch (IOException e) {
                    session.log("create_project_file", e);
                }

                // requirement
                File req = new File(session.projectHome, REQUIREMENT_DIR);
                req.mkdir();

                session.log("import_mapping_file", "todo", session.project.getMappingFile());
                session.log("import_postman_collection", "todo", null);


                return GSON.toJson(session.responseMessage);

            }
        }
    }

    static class PostmanCollectionCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            RequestMessage requestMessage = session.requestMessage;
            Project project = session.project;
            if (project == null) {
                project = new Project(requestMessage.project, null, null);
            }

            InputStream inputStream = ProjectManager.class.getClassLoader().getResourceAsStream("mustache/postman_collection.json.mustache");

            return mustache(inputStream, project);
        }
    }

    static class CompressCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            String data = GSON.toJson(session.requestMessage.message);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data.getBytes());
            gzip.close();
            byte[] compressed = bos.toByteArray();
            bos.close();

            byte[] encoded = Base64.getEncoder().encode(compressed);
            return new String(encoded);
        }
    }

    static class DecompressCommand extends ContextCommand {

        @Override
        public String execute(Session session) throws Exception {
            byte[] data = session.requestMessage.message.getAsString().getBytes();
            byte[] compressed = Base64.getDecoder().decode(data);


            ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
            GZIPInputStream gis = new GZIPInputStream(bis);
            BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            gis.close();
            bis.close();
            return sb.toString();
        }
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

    // Schema Service:
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

    // Mapping Service:
    static class MappingServiceCommand extends ServiceCommand {

        @Override
        public String execute(Session session) throws Exception {
            return session.context.getMappingService().call(getCommand(), session.project);
        }
    }

    static class XPathJsonTypeMappingsCommand extends MappingServiceCommand {
    }

    static class MappingCommand extends MappingServiceCommand {
    }

    static class MappingTreeCommand extends MappingServiceCommand {
    }

    static class AutoAdjustCommand extends MappingCommand {
    }

    static class ConstructCommand extends MappingServiceCommand {
    }

    static class OutputXmlCommand extends MappingServiceCommand {
    }

    static class UnknownPathsCommand extends MappingServiceCommand {
    }

    static class MismatchCommand extends MappingServiceCommand {
    }

    static class UnknownMappingsCommand extends MappingServiceCommand {
    }

    static class JsonTypeMappingsCommand extends MappingServiceCommand {
    }

    static class ArrayMappingsCommand extends MappingServiceCommand {
    }

    static class TransformCommand extends MappingServiceCommand {
    }

    // Kafka Service
    static abstract class KafkaServiceCommand extends ServiceCommand {

        @Override
        public String execute(Session session) throws Exception {
            return session.context.getKafkaService().call(commandLine(session), session.requestMessage.message);
        }

        protected abstract String getServiceCommand();

        protected String commandLine(Session session) {
            StringBuilder builder = new StringBuilder("-a ").append(getServiceCommand().toUpperCase());
            JsonObject jsonObject = null;
            if (session.requestMessage.parameters != null) {
                jsonObject = session.requestMessage.parameters.getAsJsonObject();
                jsonObject.entrySet().forEach(e -> {
                    if (!"a".equals(e.getKey())) {
                        builder.append(" -").append(e.getKey()).append(" ").append(e.getValue().getAsString());
                    }
                });
            }

            return builder.toString();
        }
    }

    static class KafkaTopicListCommand extends KafkaServiceCommand {

        @Override
        protected String getServiceCommand() {
            return "TopicList";
        }

    }

    static class KafkaProduceCommand extends KafkaServiceCommand {

        @Override
        protected String getServiceCommand() {
            return "Produce";
        }

    }

    static class KafkaConsumeCommand extends KafkaServiceCommand {

        @Override
        protected String getServiceCommand() {
            return "Consume";
        }

    }

    static class KafkaPubAndSubCommand extends KafkaServiceCommand {

        @Override
        protected String getServiceCommand() {
            return "PubAndSub";
        }

    }

    // Project Management:
    static abstract class AbstractProjectCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            if (!session.projectHome.exists()) {
                return "Project '" + session.name + "' does not exist.";
            }

            File projectFile = new File(session.projectHome, "project.json");
            if (!projectFile.exists()) {
                return "File 'project.json' does not exist.";
            }

            return process(session);
        }

        protected abstract String process(Session session) throws Exception;
    }

    static class ProjectCommand extends AbstractProjectCommand {

        @Override
        protected String process(Session session) throws Exception {
            if (session.responseMessage.message != null) {
                Project project = GSON.fromJson(session.requestMessage.message, Project.class);
            }

            if (session.schemaFile.exists()) {
                session.log("check_exist_schema_file", "success", null);
            } else {
                session.log("check_exist_schema_file", "failure", null);
            }

            if (session.mappingFile.exists()) {
                session.log("check_exist_mapping_file", "success", null);
            } else {
                session.log("check_exist_mapping_file", "failure", null);
            }


            session.responseMessage.message = GSON.toJsonTree(session.project);
            return GSON.toJson(session.responseMessage);
        }
    }

    static class InitializeCommand extends AbstractProjectCommand {

        @Override
        protected String process(Session session) throws Exception {
            initRequirement(session);
            initWork(session);
            initTest(session);
            initHistory(session);

            return GSON.toJson(session.responseMessage);
        }

        private void initRequirement(Session session) {
            File dir = session.requirementDir;
            if (dir.mkdir()) {
                session.log("mk_requirement_dir", "success", null);
                session.log("set_mapping_file", "todo", null);

            } else {
                session.log("mk_requirement_dir", "failure", null);
            }

        }

        private void initWork(Session session) {
            File dir = session.workDir;
            if (dir.mkdir()) {
                session.log("mk_work_dir", "success", null);

                SchemaService schemaService = session.context.getSchemaService();
                Project project = session.project;

                File xpathDataType = new File(dir, XPATH_SCHEMA_FILE);
                if (!xpathDataType.exists()) {
                    try {
                        xpathDataType.createNewFile();

                        String dataType = schemaService.call("XPathDataType", session.project);
                        FileUtils.write(xpathDataType, dataType, Charset.defaultCharset());
                        session.log("parse_cmm_schema", "success", null);

                    } catch (IOException e) {
                        session.log("parse_cmm_schema", e);

                    }
                }

                File mapping = new File(session.workDir, XPATH_MAPPING_FILE);
                if (!mapping.exists()) {
                    try {
                        mapping.createNewFile();
                        session.log("create_mapping_adjustment_file", "success", null);

                    } catch (IOException e) {
                        session.log("create_mapping_adjustment_file", e);
                    }
                }

                File adjustment = new File(session.workDir, XPATH_ADJUSTMENT_FILE);
                if (!adjustment.exists()) {
                    try {
                        adjustment.createNewFile();
                        session.log("create_mapping_adjustment_file", "success", null);

                    } catch (IOException e) {
                        session.log("create_mapping_adjustment_file", e);
                    }
                }

            } else {
                session.log("mk_work_dir", "failure", null);
            }
        }

        private void initTest(Session session) throws IOException {

            File dir = session.testDir;
            if (dir.mkdir()) {
                session.log("mk_test_dir", "success", null);

                File config = new File(dir, "bod-test.json");
                if (!config.exists()) {
                    config.createNewFile();
                    session.log("create_test_config_file", "success", null);

                    JsonObject object = new JsonObject();
                    FileUtils.write(config, GSON.toJson(object), Charset.defaultCharset());

                    session.log("set_test_config_file", "todo", null);
                }

            } else {
                session.log("mk_test_dir", "failure", null);
            }

        }

        private void initHistory(Session session) throws IOException {
            File dir = session.historyDir;

            if (dir.mkdir()) {
                session.log("mk_history_dir", "success", null);

            } else {
                session.log("mk_history_dir", "failure", null);
            }

        }
    }

    static class MqsiCommand extends AbstractProjectCommand {

        @Override
        protected String process(Session session) throws Exception {
            String application = null;
            RequestMessage requestMessage = session.requestMessage;
            if (requestMessage.parameters != null && requestMessage.parameters.get("application") != null) {
                application = requestMessage.parameters.get("application").getAsString();

            } else {
                Project project = session.project;
                application = project.getApplication();

            }

            String path = session.context.deployHome.getAbsolutePath() + "\\ESEDA";

            JsonObject jo = new JsonObject();
            jo.addProperty("path", path);
            jo.addProperty("application", application);

            return mustache("mustache/msqi.mustache", jo);
        }
    }

    static class VersionCommand extends AbstractProjectCommand {

        @Override
        protected String process(Session session) throws Exception {
            Project project = session.project;
            File version = new File(session.historyDir, session.project.getVersion());
            if (version.exists()) {

            }

            version.mkdirs();
            File workDir = new File(version, "work");
            workDir.mkdirs();
            FileUtils.copyDirectory(session.workDir, workDir);

            File iibDir = new File(version, "iib");
            iibDir.mkdirs();

            return "null";
        }
    }

    static class ReadmeCommand extends AbstractProjectCommand {

        @Override
        public String process(Session session) throws Exception {
            InputStream inputStream = ProjectManager.class.getClassLoader().getResourceAsStream("mustache/readme.mustache");
            return mustache(inputStream, session.project);
        }
    }
}
