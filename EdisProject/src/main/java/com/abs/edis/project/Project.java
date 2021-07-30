package com.abs.edis.project;

import com.google.gson.*;
import com.samskivert.mustache.Mustache;
import org.apache.avro.Schema;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.util.JsonUtils;
import soya.framework.tao.xs.XmlBeansUtils;
import soya.framework.tao.xs.XmlToAvroSchema;
import soya.framework.tao.xs.XsKnowledgeBase;
import soya.framework.tao.xs.XsNode;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Project {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Options COMMAND_LINE_OPTIONS;
    private static CommandLineParser COMMAND_LINE_PARSER = new DefaultParser();

    private static Map<String, Command> COMMANDS;

    static {
        COMMANDS = new LinkedHashMap<>();
        Class<?>[] classes = Project.class.getDeclaredClasses();
        for (Class<?> c : classes) {
            if (Command.class.isAssignableFrom(c) && !c.isInterface()) {
                String name = c.getSimpleName();
                if (name.endsWith("Command")) {
                    name = name.substring(0, name.lastIndexOf("Command"));
                    try {
                        Command processor = (Command) c.newInstance();
                        COMMANDS.put(name.toUpperCase(Locale.ROOT), processor);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }


        // Command line definition:
        COMMAND_LINE_OPTIONS = new Options();
        COMMAND_LINE_OPTIONS.addOption(Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Help ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("a")
                .longOpt("action")
                .hasArg(true)
                .desc("Action to execute ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("b")
                .longOpt("bod")
                .hasArg(true)
                .desc("Business Object ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("c")
                .longOpt("consumerTopic")
                .hasArg(true)
                .desc("Consumer Topic ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("e")
                .longOpt("env")
                .hasArg(true)
                .desc("Environment: LOC, DEV, QA, UAT1, UAT2, UAT3, UAT4, case insensitive, default is LOC. ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("Related file. ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("p")
                .longOpt("producerTopic")
                .hasArg(true)
                .desc("Producer Topic ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("s")
                .longOpt("source")
                .hasArg(true)
                .desc("Source organization. ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("t")
                .longOpt("target")
                .hasArg(true)
                .desc("Target organization. ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("v")
                .longOpt("version")
                .hasArg(true)
                .desc("Version. ([OPTIONAL])")
                .required(false)
                .build());

    }

    private Context context;

    public Project(InputStream inputStream) {
        this.context = GSON.fromJson(JsonParser.parseReader(new InputStreamReader(inputStream)), Context.class);
        this.context.init();

    }

    public String process(Node input) {
        Session session = createSession(input);
        try {
            process(session);

        } catch (Exception exception) {
            session.output = exception.getMessage();
        }
        return session.output;
    }

    public void process(Session session) throws Exception {
        CommandLine cl = session.commandLine();
        String cmd = "CommandList";
        if (cl.hasOption("a")) {
            cmd = cl.getOptionValue("a");
        }

        Command processor = COMMANDS.get(cmd.toUpperCase(Locale.ROOT));
        if (processor == null) {
            processor = new CommandListCommand();
        }

        processor.execute(session);
    }

    public Session createSession(Node node) {
        JsonObject jsonObject = estimate(node).getAsJsonObject();
        return GSON.fromJson(jsonObject, Session.class).init(context);
    }

    public JsonElement estimate(Node node) {
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

    private static void mustache(File destFile, File templateFile, EdisProject project) throws IOException {
        FileUtils.writeByteArrayToFile(destFile,
                Mustache.compiler().compile(
                        new FileReader(templateFile)).execute(JsonUtils.toMap(GSON.toJsonTree(project).getAsJsonObject())).getBytes());
    }

    static class Context {
        private transient File homeDir;
        private transient File cmmDir;
        private transient File templateDir;
        private transient File srcDir;
        private transient File deployDir;
        private transient File boDir;

        private Env env;

        private void init() {
            homeDir = new File(env.home);
            cmmDir = new File(homeDir, env.cmm);
            templateDir = new File(homeDir, env.template);
            srcDir = new File(homeDir, env.src);
            deployDir = new File(homeDir, env.deployment);
            boDir = new File(homeDir, env.bo);
        }
    }

    static class Env {
        private String home;
        private String cmm;
        private String src;
        private String deployment;
        private String template;
        private String bo;
    }

    static class Session {
        private transient Context context;

        private String commandLine;
        private JsonElement input;

        private String output;

        private Session init(Context context) {
            this.context = context;

            return this;
        }

        public Context getContext() {
            return context;
        }

        public CommandLine commandLine() {
            List<String> list = new ArrayList<>();
            if (commandLine != null) {
                StringTokenizer tokenizer = new StringTokenizer(commandLine);
                while (tokenizer.hasMoreTokens()) {
                    list.add(tokenizer.nextToken());
                }
            }

            try {
                return COMMAND_LINE_PARSER.parse(COMMAND_LINE_OPTIONS, list.toArray(new String[list.size()]));

            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        public JsonElement getInput() {
            return input;
        }

        public String getOutput() {
            return output;
        }

    }

    interface Command {
        void execute(Session session) throws Exception;
    }

    static class CommandListCommand implements Command {

        @Override
        public void execute(Session session) {
            session.output = GSON.toJson(COMMANDS.keySet());
        }
    }

    static class ContextCommand implements Command {
        @Override
        public void execute(Session session) {
            session.output = GSON.toJson(session.getContext());
        }
    }

    static class SessionCommand implements Command {
        @Override
        public void execute(Session session) {
            session.output = GSON.toJson(session);
        }
    }

    static class SampleXmlCommand implements Command {

        @Override
        public void execute(Session session) {

            Context ctx = session.getContext();
            CommandLine cmd = session.commandLine();

            if (cmd.hasOption("f")) {
                try {
                    SchemaTypeSystem sts = XmlBeansUtils.getSchemaTypeSystem(new File(ctx.cmmDir, cmd.getOptionValue("f")));
                    String result = SampleXmlUtil.createSampleForType(sts.documentTypes()[0]);
                    result = result.replace("xmlns:def", "xmlns:Abs").replaceAll("def:", "Abs:");

                    session.output = result;

                } catch (XmlException | IOException e) {
                    throw new RuntimeException(e);

                }

            } else if (cmd.hasOption("b")) {

            }
        }
    }

    static class AvscCommand implements Command {

        @Override
        public void execute(Session session) {

            Context ctx = session.getContext();
            CommandLine cmd = session.commandLine();

            if (cmd.hasOption("f")) {
                Schema schema = XmlToAvroSchema.fromXmlSchema(new File(ctx.cmmDir, cmd.getOptionValue("f")));
                session.output = schema.toString(true);

            } else if (cmd.hasOption("b")) {

            }
        }
    }

    static class SampleAvroCommand implements Command {

        @Override
        public void execute(Session session) {

            Context ctx = session.getContext();
            CommandLine cmd = session.commandLine();

            if (cmd.hasOption("f")) {
                try {
                    SchemaTypeSystem sts = XmlBeansUtils.getSchemaTypeSystem(new File(ctx.cmmDir, cmd.getOptionValue("f")));
                    session.output = SampleXmlUtil.createSampleForType(sts.documentTypes()[0]);

                } catch (XmlException | IOException e) {
                    throw new RuntimeException(e);

                }

            } else if (cmd.hasOption("b")) {

            }
        }
    }

    static abstract class AbstractCommand implements Command {
        protected Context context;
        protected CommandLine commandLine;
        protected EdisProject project;
        protected KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree;

        @Override
        public void execute(Session session) throws Exception {
            this.context = session.getContext();
            this.commandLine = session.commandLine();
            this.project = GSON.fromJson(session.input, EdisProject.class);

            File xsd = new File(session.getContext().cmmDir, project.getMappings().getSchema());
            if (!xsd.exists()) {
                throw new RuntimeException("File does not exist: " + xsd.getAbsolutePath());
            }

            this.knowledgeTree = XsKnowledgeBase.builder()
                    .file(xsd)
                    .create().knowledge();

            session.output = process();
        }

        protected abstract String process() throws Exception;

        protected void fromTemplate(File dest, String templateName) throws IOException {
            if (!dest.exists()) {
                dest.createNewFile();
                mustache(dest, new File(context.templateDir, templateName), project);
            }
        }
    }

    static class CreateCommand extends AbstractCommand {

        @Override
        protected String process() throws Exception {
            File projectHome = new File(context.boDir, project.getName());
            projectHome.mkdirs();

            File requirement = new File(projectHome, "requirement");
            requirement.mkdirs();

            fromTemplate(new File(projectHome, "READMD.md"), "readme.md.mustache");
            fromTemplate(new File(projectHome, "build.properties"), "build.properties.mustache");
            fromTemplate(new File(projectHome, "build.xml"), "build.xml.mustache");

            File srcDir = new File(projectHome, "src");
            srcDir.mkdirs();

            File appDir = new File(srcDir, project.getApplication());
            appDir.mkdirs();

            fromTemplate(new File(appDir, ".project"), "project.xml.mustache");

            File deployDir = new File(projectHome, "deploy");
            deployDir.mkdirs();

            File esedA = new File(deployDir, "ESEDA");
            esedA.mkdirs();

            File overrideBase = new File(esedA, project.getApplication() + ".BASE.override.properties");
            if (!overrideBase.exists()) {
                overrideBase.createNewFile();
            }

            File overrideDev = new File(esedA, project.getApplication() + ".DEV.override.properties");
            if (!overrideDev.exists()) {
                overrideDev.createNewFile();
            }

            File overrideQa = new File(esedA, project.getApplication() + ".QA.override.properties");
            if (!overrideQa.exists()) {
                overrideQa.createNewFile();
            }

            File esedB = new File(deployDir, "ESEDB");
            esedB.mkdirs();

            File cutoff = new File(deployDir, "CUTOFF");
            cutoff.mkdirs();

            File checklist = new File(cutoff, project.getName() + "-" + project.getVersion() + "_Cutoff_Plan.txt");
            checklist.createNewFile();

            return "";
        }
    }

    static class XPathCommand extends AbstractCommand {

        @Override
        protected String process() throws Exception {
            StringBuilder builder = new StringBuilder();
            Iterator<String> iterator = knowledgeTree.paths();
            while (iterator.hasNext()) {
                String path = iterator.next();
                XsNode node = knowledgeTree.get(path).origin();
                builder.append(path).append("=");
                if (XsNode.XsNodeType.Folder.equals(node.getNodeType())) {
                    if (BigInteger.ONE.equals(node.getMaxOccurs())) {
                        builder.append("object");
                    } else {
                        builder.append("array");
                    }
                } else if (XsNode.XsNodeType.Attribute.equals(node.getNodeType())) {
                    builder.append("string");
                } else {
                    builder.append(getSimpleType(node.getSchemaType()));

                    if (!BigInteger.ONE.equals(node.getMaxOccurs())) {
                        builder.append("Array");
                    }

                }
                builder.append("\n");
            }
            return builder.toString();
        }

        private String getSimpleType(SchemaType schemaType) {
            String type = null;

            SchemaType base = schemaType;
            while (!base.isSimpleType()) {
                base = base.getBaseType();
            }
            String className = XmlBeansUtils.getXMLBuildInType(base).getJavaType().getSimpleName();
            switch (className) {
                case "boolean":
                    type = "boolean";
                    break;
                case "float":
                    type = "float";
                    break;
                case "double":
                case "BigDecimal":
                    type = "double";
                    break;
                case "BigInteger":
                case "int":
                    type = "integer";
                    break;
                case "short":
                    type = "short";
                    break;
                default:
                    type = "string";
            }

            return type;
        }
    }
}
