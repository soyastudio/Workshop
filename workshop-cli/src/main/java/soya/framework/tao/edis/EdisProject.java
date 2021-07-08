package soya.framework.tao.edis;

import com.google.gson.*;
import com.samskivert.mustache.Mustache;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.util.JsonUtils;
import soya.framework.tao.xs.XsKnowledgeBase;
import soya.framework.tao.xs.XsNode;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class EdisProject {

    private String home;

    private String name;
    private String type;

    private String application;
    private String source;
    private String consumer;
    private String version;

    private Mappings mappings;

    private MessageFlow messageFlow;

    private String deployEgNumber;

    private boolean createTestWorkspace;

    //
    public static String REQUIREMENT_DIR = "requirement";
    public static String WORK_DIR = "work";
    public static String TEST_DIR = "test";

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File homeDir;
    private static File cmmDir;
    private static File boDir;
    private static File workDir;

    private static Map<String, String> INPUT_PROPERTIES;
    private static Map<String, String> OUTPUT_PROPERTIES;
    private static Map<String, String> TRANSFORMER_PROPERTIES;
    private static Map<String, String> INPUT_AUDITOR_PROPERTIES;
    private static Map<String, String> OUTPUT_AUDITOR_PROPERTIES;
    private static Map<String, String> EXCEPTION_HANDLER_PROPERTIES;

    static {
        INPUT_PROPERTIES = new LinkedHashMap<>();
        INPUT_PROPERTIES.put("topicName", "???");

        OUTPUT_PROPERTIES = new LinkedHashMap<>();
        OUTPUT_PROPERTIES.put("topicName", "???");

        TRANSFORMER_PROPERTIES = new LinkedHashMap<>();

        INPUT_AUDITOR_PROPERTIES = new LinkedHashMap<>();

        OUTPUT_AUDITOR_PROPERTIES = new LinkedHashMap<>();

        EXCEPTION_HANDLER_PROPERTIES = new LinkedHashMap<>();
    }

    public static void main(String[] args) throws Exception {

        File cd = new File(Paths.get("").toAbsolutePath().toString());
        homeDir = cd.getParentFile();

        cmmDir = new File(homeDir, "CMM");
        boDir = new File(homeDir, "BusinessObjects");

        // Command line definition:
        Options options = new Options();
        options.addOption(Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Help ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("a")
                .longOpt("action")
                .hasArg(true)
                .desc("Action to execute ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("b")
                .longOpt("bod")
                .hasArg(true)
                .desc("Business Object ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("c")
                .longOpt("consumerTopic")
                .hasArg(true)
                .desc("Consumer Topic ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("e")
                .longOpt("env")
                .hasArg(true)
                .desc("Environment: LOC, DEV, QA, UAT1, UAT2, UAT3, UAT4, case insensitive, default is LOC. ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("Related file. ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("p")
                .longOpt("producerTopic")
                .hasArg(true)
                .desc("Producer Topic ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("s")
                .longOpt("source")
                .hasArg(true)
                .desc("Source organization. ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("t")
                .longOpt("target")
                .hasArg(true)
                .desc("Target organization. ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("v")
                .longOpt("version")
                .hasArg(true)
                .desc("Version. ([OPTIONAL])")
                .required(false)
                .build());

        // Parse command line and dispatch to method:
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        if (cmd.hasOption("b")) {
            String bod = cmd.getOptionValue("b");
            if (cmd.hasOption("a")) {
                String action = cmd.getOptionValue("a");
                EdisProject.class.getMethod(action, new Class[]{String.class, CommandLine.class}).invoke(null, new Object[]{bod, cmd});

            } else {

            }

        } else {

        }

    }

    public static void create(String bod, CommandLine cmd) throws Exception {
        System.out.println("Creating workspace for business object: " + bod + "...");

        if (!cmd.hasOption("s")) {
            System.out.println("Source organization is required to assign to argument '-s'");
            System.exit(1);
        }

        if (!cmd.hasOption("t")) {
            System.out.println("Target organization is required to assign to argument '-t'");
            System.exit(1);
        }

        if (!cmd.hasOption("v")) {
            System.out.println("Version is required to assign to argument '-v'");
            System.exit(1);
        }

        File dir = new File(boDir, bod);
        if (dir.exists()) {
            System.out.println("Workspace already exists for bod: " + bod);
            System.exit(1);

        } else {
            String src = cmd.getOptionValue("s");
            String target = cmd.getOptionValue("t");
            String consumerTopic = cmd.getOptionValue("c");
            String producerTopic = cmd.getOptionValue("p");
            String version = cmd.getOptionValue("v");

            dir.mkdirs();
            File projectFile = new File(dir, "project.json");
            projectFile.createNewFile();

            File req = new File(dir, REQUIREMENT_DIR);
            req.mkdirs();

            File work = new File(dir, WORK_DIR);
            work.mkdirs();

            EdisProject project = new EdisProject();
            project.name = bod;
            project.application = "ESED_" + bod + "_" + src + "_IH_Publisher";
            project.source = src;
            project.consumer = target;
            project.version = version;

            project.mappings = new Mappings();
            project.mappings.schema = "CMM/BOD/get" + bod + ".xsd";

            project.messageFlow = new MessageFlow(bod, src, "com.abs." + project.source.toLowerCase() + "." + bod);

            project.deployEgNumber = "???";

            FileUtils.writeByteArrayToFile(projectFile, GSON.toJson(project).getBytes());

            System.out.println("Workspace for business object: " + bod + " is created successfully.");

        }
    }

    public static void init(String bod, CommandLine cmd) throws Exception {
        System.out.println("Initializing project for business object: " + bod + "...");

        // Basic
        File workspace = new File(boDir, bod);
        String buildFile = cmd.hasOption("f") ? cmd.getOptionValue("f") : "project.json";

        File projectFile = new File(workspace, buildFile);
        EdisProject project = GSON.fromJson(new FileReader(projectFile), EdisProject.class);

        // README:
        File readme = new File(workspace, "README.md");
        if (!readme.exists()) {
            System.out.println("Creating README file...");
            readme.createNewFile();
            mustache(readme, new File(homeDir, "Templates/readme.mustache"), project);

        }

        // IIB build
        File iib = new File(workspace, "IIB");
        if(!iib.exists()) {
            System.out.println("Initializing IIB project");
            iib.mkdirs();

            File antXmlFile = new File(iib, "build.xml");
            if (!antXmlFile.exists()) {
                antXmlFile.createNewFile();
                mustache(antXmlFile, new File(homeDir, "Templates/build.xml.mustache"), project);
            }

            File antPropFile = new File(iib, "build.properties");
            if (!antPropFile.exists()) {
                antPropFile.createNewFile();
                mustache(antPropFile, new File(homeDir, "Templates/build.properties.mustache"), project);
            }

            File iibSrc = new File(iib, "src");
            iibSrc.mkdirs();

            File iibApp = new File(iibSrc, project.application);
            if (!iibApp.exists()) {
                iibApp.mkdirs();

                File appProjectFile = new File(iibApp, ".project");
                appProjectFile.createNewFile();
                mustache(appProjectFile, new File(homeDir, "Templates/application.project.mustache"), project);

                File appDescFile = new File(iibApp, "application.descriptor");
                appDescFile.createNewFile();
                mustache(appDescFile, new File(homeDir, "Templates/application.descriptor.mustache"), project);
            }

            File pkg = new File(iibApp, project.messageFlow.packageURI);
            pkg.mkdirs();
            File subFlow = new File(pkg, project.messageFlow.transformer.name + ".subflow");
            subFlow.createNewFile();
            mustache(subFlow, new File(homeDir, "Templates/CMM_Transformer.subflow.mustache"), project);

            File esql = new File(pkg, project.messageFlow.transformer.name + "_Compute.esql");
            esql.createNewFile();
            mustache(esql, new File(homeDir, "Templates/CMM_Transformer_Compute.esql.mustache"), project);

            File mainFlow = new File(pkg, project.messageFlow.name + ".msgflow.mustache");
            mainFlow.createNewFile();
            mustache(mainFlow, new File(homeDir, "Templates/IH_Publisher.msgflow.mustache"), project);

            File iibDeploy = new File(iib, "deploy");
            iibDeploy.mkdirs();
            File iibDeployA = new File(iibDeploy, "ESEDA");
            if (!iibDeployA.exists()) {

                iibDeployA.mkdirs();

                File overrideBase = new File(iibDeployA, project.application + ".BASE.override.properties");
                overrideBase.createNewFile();
                mustache(overrideBase, new File(homeDir, "Templates/override.base.mustache"), project);

                File overrideDev = new File(iibDeployA, project.application + ".DV.override.properties");
                overrideDev.createNewFile();
                mustache(overrideDev, new File(homeDir, "Templates/override.dv.mustache"), project);

                File overrideQa = new File(iibDeployA, project.application + ".QA.override.properties");
                overrideQa.createNewFile();
                mustache(overrideQa, new File(homeDir, "Templates/override.qa.mustache"), project);

                File overrideProd = new File(iibDeployA, project.application + ".PR.override.properties");
                overrideProd.createNewFile();
            }

            File iibDeployB = new File(iibDeploy, "ESEDB");
            if (!iibDeployB.exists()) {
                iibDeployB.mkdirs();

                File devDeployDescriptor = new File(iibDeployB, project.application + ".DV.deploy.properties");
                devDeployDescriptor.createNewFile();
                mustache(devDeployDescriptor, new File(homeDir, "Templates/deploy.dv.mustache"), project);

                File qaDeployDescriptor = new File(iibDeployB, project.application + ".QA.deploy.properties");
                qaDeployDescriptor.createNewFile();
                mustache(qaDeployDescriptor, new File(homeDir, "Templates/deploy.qa.mustache"), project);

                File prDeployDescriptor = new File(iibDeployB, project.application + ".PR.deploy.properties");
                prDeployDescriptor.createNewFile();
                mustache(prDeployDescriptor, new File(homeDir, "Templates/deploy.pr.mustache"), project);

            }
        }

        // working
        File work = new File(workspace, "work");
        if (!work.exists()) {
            System.out.println("Making work dir...");
            work.mkdirs();


        }

        // History
        File history = new File(workspace, "history");
        if (!history.exists()) {
            System.out.println("Making history dir...");
            history.mkdirs();
        }
    }

    public static void version(String bod, CommandLine cmd) throws Exception {
        if (!cmd.hasOption("v")) {
            System.out.println("Version is required to assign to argument '-v'");
            System.exit(1);
        }

        File workspace = new File(boDir, bod);
        File iib = new File(workspace, "IIB");
        File work = new File(workspace, "work");

        File history = new File(workspace, "history");
        if (!history.exists()) {
            System.out.println("Making history dir...");
            history.mkdirs();
        }

        File version = new File(history, cmd.getOptionValue("v"));
        if(!version.exists()) {
            version.mkdirs();
            FileUtils.copyDirectory(iib, version);

            File work2 = new File(version, "work");
            work2.mkdirs();
            FileUtils.copyDirectory(work, work2);

        }
    }

    public static void avsc(String bod, CommandLine cmd) throws Exception {
        System.out.println("Generate mappings for business object: " + bod + "...");

        File workspace = new File(boDir, bod);
        String buildFile = cmd.hasOption("f") ? cmd.getOptionValue("f") : "project.json";

        File projectFile = new File(workspace, buildFile);
        EdisProject project = GSON.fromJson(new FileReader(projectFile), EdisProject.class);
        Mappings mappings = project.mappings;

        File work = new File(workspace, WORK_DIR);
        File version = new File(work, project.version);

        File avsc = new File(version, project.name + ".avsc");
        if (avsc.exists()) {
            System.out.println("File '" + avsc + "' already exist.");
            System.exit(0);
        }

        // Knowledge Tree:
        File xsd = new File(homeDir, mappings.schema);
        KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree = XsKnowledgeBase.builder()
                .file(xsd)
                .create().knowledge();

        avsc.createNewFile();
        FileUtils.writeByteArrayToFile(avsc,
                GSON.toJson(JsonParser.parseString(XmlToAvroSchema1.fromXmlSchema(knowledgeTree.origin()).toString())).getBytes());

    }

    public static void mapping(String bod, CommandLine cmd) throws Exception {
        System.out.println("Generate mappings for business object: " + bod + "...");

        File workspace = new File(boDir, bod);
        String buildFile = cmd.hasOption("f") ? cmd.getOptionValue("f") : "project.json";

        File projectFile = new File(workspace, buildFile);
        EdisProject project = GSON.fromJson(new FileReader(projectFile), EdisProject.class);
        Mappings mappings = project.mappings;

        File work = new File(workspace, WORK_DIR);
        File version = new File(work, project.version);

        File xpathMappings = new File(version, mappings.constructFile);
        if (xpathMappings.exists()) {
            System.out.println("File '" + xpathMappings + "' already exist.");
            System.exit(0);
        }

        // Knowledge Tree:
        File xsd = new File(homeDir, mappings.schema);
        KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree = XsKnowledgeBase.builder()
                .file(xsd)
                .create().knowledge();

        xpathMappings.createNewFile();
        FileUtils.writeByteArrayToFile(xpathMappings,
                new XlsxMappingRenderer()
                        .mappingFile(new File(workspace, mappings.mappingFile))
                        .mappingSheet(mappings.mappingSheet)
                        .render(knowledgeTree).getBytes());

    }

    public static void xmlGenerator(String bod, CommandLine cmd) throws Exception {
        System.out.println("Building business object: " + bod + "...");

        File workspace = new File(boDir, bod);
        String buildFile = cmd.hasOption("f") ? cmd.getOptionValue("f") : "project.json";

        File projectFile = new File(workspace, buildFile);
        EdisProject project = GSON.fromJson(new FileReader(projectFile), EdisProject.class);

        KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree = XsKnowledgeBase.builder()
                .file(new File(homeDir, project.mappings.schema))
                .create().knowledge();

        File xlsx = new File(workspace, project.mappings.mappingFile);
        if (!xlsx.exists()) {
            throw new NullPointerException("File " + xlsx.getAbsolutePath() + " does not exist.");
        }
        new XlsxMappingAnnotator()
                .mappingFile(xlsx)
                .mappingSheet(project.mappings.mappingSheet)
                .annotate(knowledgeTree);

        // Build
        File work = new File(workspace, "work");
        if (!work.exists()) {
            System.out.println("Making work dir...");
            work.mkdirs();
        }
        File version = new File(work, project.version);
        if (!version.exists()) {
            System.out.println("Making " + version + " dir...");
            version.mkdirs();
        }

        File xpathMappingFile = new File(version, project.mappings.constructFile);
        if (!xpathMappingFile.exists()) {
            System.out.println("Construct file " + project.mappings.constructFile + " does not exist.");
            System.exit(0);
        }

        new XPathAssignmentAnnotator()
                .file(xpathMappingFile.getAbsolutePath())
                .annotate(knowledgeTree);


        // ESQL
        File esql = new File(version, project.application + ".esql");
        if (!esql.exists()) {
            System.out.println("Generating esql file: " + project.application + ".esql...");
            esql.createNewFile();
            FileUtils.writeByteArrayToFile(esql, new XmlEsqlRenderer()
                    .brokerSchema(project.messageFlow.brokerSchema)
                    .moduleName(project.messageFlow.transformer.name + "_Compute")
                    .inputRootVariable("_inputRootNode")
                    .inputRootReference("InputRoot.JSON.Data")
                    .render(knowledgeTree).getBytes());
        }

    }

    public static void jsonGenerator(String bod, CommandLine cmd) throws Exception {

        File workspace = new File(boDir, bod);
        String buildFile = cmd.hasOption("f") ? cmd.getOptionValue("f") : "project.json";

        File projectFile = new File(workspace, buildFile);
        EdisProject project = GSON.fromJson(new FileReader(projectFile), EdisProject.class);

        KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree = XsKnowledgeBase.builder()
                .file(new File(homeDir, project.mappings.schema))
                .create().knowledge();

        File xlsx = new File(workspace, project.mappings.mappingFile);
        if (!xlsx.exists()) {
            throw new NullPointerException("File " + xlsx.getAbsolutePath() + " does not exist.");
        }
        new XlsxMappingAnnotator()
                .mappingFile(xlsx)
                .mappingSheet(project.mappings.mappingSheet)
                .annotate(knowledgeTree);

        // Build
        File work = new File(workspace, "work");
        if (!work.exists()) {
            System.out.println("Making work dir...");
            work.mkdirs();
        }
        File version = new File(work, project.version);
        if (!version.exists()) {
            System.out.println("Making " + version + " dir...");
            version.mkdirs();
        }

        // Annotate from xpath-mapping file
        File xpathMappingFile = new File(version, project.mappings.constructFile);
        if (!xpathMappingFile.exists()) {
            System.out.println("Construct file " + project.mappings.constructFile + " does not exist.");
            System.exit(0);
        }
        new XPathAssignmentAnnotator()
                .file(xpathMappingFile.getAbsolutePath())
                .annotate(knowledgeTree);


        // ESQL
        File esql = new File(version, project.name + "_JSON_TRANSFORMER_Compute.esql");
        if (!esql.exists()) {
            esql.createNewFile();
            FileUtils.writeByteArrayToFile(esql, new JsonEsqlRenderer()
                    .brokerSchema(project.messageFlow.brokerSchema)
                    .moduleName(project.messageFlow.transformer.name + "_Compute")
                    .inputRootVariable("_inputRootNode")
                    .inputRootReference("InputRoot.JSON.Data")
                    .render(knowledgeTree).getBytes());
        }

    }

    public static void build(String bod, CommandLine cmd) throws Exception {
        System.out.println("Building business object: " + bod + "...");

        File workspace = new File(boDir, bod);
        String buildFile = cmd.hasOption("f") ? cmd.getOptionValue("f") : "project.json";

        File projectFile = new File(workspace, buildFile);
        EdisProject project = GSON.fromJson(new FileReader(projectFile), EdisProject.class);

        KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree = XsKnowledgeBase.builder()
                .file(new File(homeDir, project.mappings.schema))
                .create().knowledge();

        // Build
        File work = new File(workspace, "work");
        if (!work.exists()) {
            System.out.println("Making work dir...");
            work.mkdirs();
        }
        File version = new File(work, project.version);
        if (!version.exists()) {
            System.out.println("Making " + version + " dir...");
            version.mkdirs();
        }

        File xlsx = new File(workspace, project.mappings.mappingFile);
        if (!xlsx.exists()) {
            throw new NullPointerException("File " + xlsx.getAbsolutePath() + " does not exist.");
        }

        new XlsxMappingAnnotator()
                .mappingFile(xlsx)
                .mappingSheet(project.mappings.mappingSheet)
                .annotate(knowledgeTree);

        System.out.println("===================== !!!");

        File xpathMappingFile = new File(version, project.mappings.constructFile);
        if (!xpathMappingFile.exists()) {
            System.out.println("Generating " + project.mappings.constructFile + " file...");
            xpathMappingFile.createNewFile();
            FileUtils.writeByteArrayToFile(xpathMappingFile, new XPathAssignmentAnalyzer().enableLoopFeature().render(knowledgeTree).getBytes());

        }

        new XPathAssignmentAnnotator()
                .file(xpathMappingFile.getAbsolutePath())
                .annotate(knowledgeTree);

        File esedA = new File(version, "ESEDA");
        if (!esedA.exists()) {
            System.out.println("Making ESEDA dir... ");
            esedA.mkdirs();

            System.out.println("Generating " + project.application + ".BASE.override.properties" + " file...");
            File baseOverride = new File(esedA, project.application + ".BASE.override.properties");
            baseOverride.createNewFile();
            FileUtils.writeByteArrayToFile(baseOverride,
                    new MustacheRenderer()
                            .templateFile(new File(homeDir, "Templates/override.base.mustache"))
                            .variables(project)
                            .render(knowledgeTree).getBytes());

            System.out.println("Generating " + project.application + ".DV.override.properties" + " file...");
            File devOverride = new File(esedA, project.application + ".DV.override.properties");
            devOverride.createNewFile();
            FileUtils.writeByteArrayToFile(devOverride,
                    new MustacheRenderer()
                            .templateFile(new File(homeDir, "Templates/override.dv.mustache"))
                            .variables(project)
                            .render(knowledgeTree).getBytes());

            System.out.println("Generating " + project.application + ".QA.override.properties" + " file...");
            File qaOverride = new File(esedA, project.application + ".QA.override.properties");
            qaOverride.createNewFile();
            FileUtils.writeByteArrayToFile(qaOverride,
                    new MustacheRenderer()
                            .templateFile(new File(homeDir, "Templates/override.qa.mustache"))
                            .variables(project)
                            .render(knowledgeTree).getBytes());

            System.out.println("Generating " + project.application + ".PR.override.properties" + " file...");
            File prOverride = new File(esedA, project.application + ".PR.override.properties");
            prOverride.createNewFile();
        }

        File esedB = new File(version, "ESEDB");
        if (!esedB.exists()) {
            System.out.println("Making ESEDB dir...");
            esedB.mkdirs();

            System.out.println("Generating " + project.application + ".DV.deploy.properties" + " file...");
            File devDeployDescriptor = new File(esedB, project.application + ".DV.deploy.properties");
            devDeployDescriptor.createNewFile();
            FileUtils.writeByteArrayToFile(devDeployDescriptor,
                    new MustacheRenderer()
                            .templateFile(new File(homeDir, "Templates/deploy.dv.mustache"))
                            .variables(project)
                            .render(knowledgeTree).getBytes());

            System.out.println("Generating " + project.application + ".QA.deploy.properties" + " file...");
            File qaDeployDescriptor = new File(esedB, project.application + ".QA.deploy.properties");
            qaDeployDescriptor.createNewFile();
            FileUtils.writeByteArrayToFile(qaDeployDescriptor,
                    new MustacheRenderer()
                            .templateFile(new File(homeDir, "Templates/deploy.qa.mustache"))
                            .variables(project)
                            .render(knowledgeTree).getBytes());

            System.out.println("Generating " + project.application + ".PR.deploy.properties" + " file...");
            File prDeployDescriptor = new File(esedB, project.application + ".PR.deploy.properties");
            prDeployDescriptor.createNewFile();
            FileUtils.writeByteArrayToFile(prDeployDescriptor,
                    new MustacheRenderer()
                            .templateFile(new File(homeDir, "Templates/deploy.pr.mustache"))
                            .variables(project)
                            .render(knowledgeTree).getBytes());
        }

        // ESQL
        File esql = new File(version, project.application + ".esql");
        if (!esql.exists()) {
            System.out.println("Generating esql file: " + project.application + ".esql...");
            esql.createNewFile();
            FileUtils.writeByteArrayToFile(esql, new XmlEsqlRenderer()
                    .brokerSchema(project.messageFlow.brokerSchema)
                    .moduleName(project.messageFlow.transformer.name + "_Compute")
                    .inputRootVariable("_inputRootNode")
                    .inputRootReference("InputRoot.JSON.Data")
                    .render(knowledgeTree).getBytes());
        }

    }

    public static void validate(String bod, CommandLine cmd) throws Exception {
        System.out.println("Generate mappings for business object: " + bod + "...");

        File workspace = new File(boDir, bod);
        String buildFile = cmd.hasOption("f") ? cmd.getOptionValue("f") : "project.json";

        File projectFile = new File(workspace, buildFile);
        EdisProject project = GSON.fromJson(new FileReader(projectFile), EdisProject.class);
        Mappings mappings = project.mappings;

        File work = new File(workspace, WORK_DIR);
        File version = new File(work, project.version);

        File xpathMappings = new File(version, mappings.constructFile);
        if (!xpathMappings.exists()) {
            System.out.println("File '" + xpathMappings + "' does not exist.");
            System.exit(0);
        }

        // Knowledge Tree:
        File xsd = new File(homeDir, mappings.schema);
        KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree = XsKnowledgeBase.builder()
                .file(xsd)
                .create().knowledge();

        File xlsx = new File(workspace, mappings.mappingFile);
        new XlsxMappingAnnotator()
                .mappingFile(xlsx)
                .mappingSheet(mappings.mappingSheet)
                .annotate(knowledgeTree);

        File esql = new File(version, project.messageFlow.transformer.name + "_Compute.esql");

        String report = new ESQLValidator().esql(esql).render(knowledgeTree);
        System.out.println();
        System.out.println(report);

    }

    public static void overrideBar(String bod, CommandLine cmd) throws Exception {
        String[] commands = new String[]{"java", "-version"};
        Process process = null;
        ProcessBuilder pb = new ProcessBuilder(commands);
        //pb.directory(new File("path_to_working_directory")); //Set current directory
        //pb.redirectError(new File("path_to_log_file")); //Log errors in specified log file.
        try {
            process = pb.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("--------------- !!!");
    }

    public static void test(String bod, CommandLine cmd) throws Exception {

    }

    public static void cutoff(String bod, CommandLine cmd) throws Exception {
        File workspace = new File(boDir, bod);
        File projectFile = new File(workspace, "project.json");
        EdisProject project = GSON.fromJson(new FileReader(projectFile), EdisProject.class);

        System.out.println("Cutting off business object: " + bod + " version: " + project.version);

        File version = new File(workspace, WORK_DIR + "/" + project.version);
        File cutoffDir = new File(version, "CUTOFF");
        if (!cutoffDir.exists()) {
            cutoffDir.mkdirs();
            File esedA = new File(cutoffDir, "ESEDA");
            esedA.mkdirs();
            FileUtils.copyDirectory(new File(version, "ESEDA"), esedA);

            File esedB = new File(cutoffDir, "ESEDB");
            esedB.mkdirs();
            FileUtils.copyDirectory(new File(version, "ESEDB"), esedB);

            XSSFWorkbook workbook = new XSSFWorkbook();
            int rw = 0;
            int cl = 0;

            XSSFSheet index = workbook.createSheet("index");
            Object[][] indexData = {
                    {"BOD:", project.name},
                    {"Application: ", project.application},
                    {"Version:", project.version},
                    {"Created Date:", new SimpleDateFormat("yyyy-MM-dd").format(new Date())},
                    {"Message Flow:", project.messageFlow.name},
                    {"Source:", project.source},
                    {"Consumer:", project.consumer},
            };

            for (Object[] aBook : indexData) {
                Row row = index.createRow(rw);

                cl = 0;
                for (Object field : aBook) {
                    Cell cell = row.createCell(cl);
                    if (field instanceof String) {
                        cell.setCellValue((String) field);
                    } else if (field instanceof Integer) {
                        cell.setCellValue((Integer) field);
                    }

                    cl++;
                }

                rw++;
            }

            XSSFSheet iib = workbook.createSheet("IIB");
            Object[][] bookData = {
                    {"No.", "Step", "Task", "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"},
                    {1, "Prepare Cutoff Plan", "Task", "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"},
                    {2, "Create Kafka Inbound Topic", project.messageFlow.input.getProperty("topicName"), "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"},
                    {3, "Verify Kafka Inbound Topic", "", "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"},
                    {4, "Create Kafka Outbound Topic", "", "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"},
                    {5, "Verify Kafka outbound Topic", "", "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"},
                    {6, "Deploy DeployDescriptor File", "", "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"},
                    {7, "Deploy Override File", "", "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"},
                    {8, "Deploy Bar File", "Task", "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"},
                    {9, "Verify Production Readiness", "Task", "Description", "Owner", "Dependency", "Duration", "Start Date", "Start Date", "Status", "Remarks"}

            };

            rw = 0;

            for (Object[] aBook : bookData) {
                Row row = iib.createRow(rw);

                cl = 0;
                for (Object field : aBook) {
                    Cell cell = row.createCell(cl);
                    if (field instanceof String) {
                        cell.setCellValue((String) field);
                    } else if (field instanceof Integer) {
                        cell.setCellValue((Integer) field);
                    }
                    cl++;
                }

                rw++;
            }

            File plan = new File(cutoffDir, "cutoff-plan.xlsx");
            try (FileOutputStream outputStream = new FileOutputStream(plan)) {
                if (!plan.exists()) {
                    plan.createNewFile();
                }
                workbook.write(outputStream);
            }

        }
    }

    private static void mustache(File destFile, File templateFile, EdisProject project) throws IOException {
        FileUtils.writeByteArrayToFile(destFile,
                Mustache.compiler().compile(
                        new FileReader(templateFile)).execute(JsonUtils.toMap(GSON.toJsonTree(project).getAsJsonObject())).getBytes());
    }

    static class Mappings {
        private String schema = "";
        private String mappingFile = "???";
        private String mappingSheet = "???";
        private String sampleSheet = "???";
        private String constructFile = "xpath-mapping.properties";

    }

    static class MessageFlow {
        private String name;
        private String brokerSchema;
        private String packageURI;
        private JsonObject properties = new JsonObject();

        private Node input;
        private Node output;
        private Node transformer;

        private Node inputAuditor;
        private Node outputAuditor;
        private Node exceptionHandler;

        public MessageFlow() {
        }


        public MessageFlow(String bod, String src, String brokerSchema) {
            this.name = "ESED_" + bod + "_" + src + "_IH_Publisher";
            this.brokerSchema = brokerSchema;
            this.packageURI = brokerSchema.replaceAll("\\.", "/");

            this.input = new Node("KafkaConsumer", INPUT_PROPERTIES);
            this.output = new Node("KafkaProducer", OUTPUT_PROPERTIES);
            this.transformer = new Node(bod + "_" + src + "_Transformer", TRANSFORMER_PROPERTIES);

            this.inputAuditor = new Node("Audit_Validate_Input", INPUT_AUDITOR_PROPERTIES);
            this.exceptionHandler = new Node("ExceptionSubFlow", EXCEPTION_HANDLER_PROPERTIES);
            this.outputAuditor = new Node("Audit_Validate_Output", OUTPUT_AUDITOR_PROPERTIES);
        }
    }

    static class Node {
        private String name;
        private JsonObject properties = new JsonObject();

        public Node() {
        }

        public Node(String name, Map<String, String> properties) {
            this.name = name;
            if (properties != null) {
                this.properties = JsonParser.parseString(GSON.toJson(properties)).getAsJsonObject();
            }
        }

        public String getProperty(String name) {
            return properties.get(name) == null ? "" : properties.get(name).getAsString();
        }
    }

}
