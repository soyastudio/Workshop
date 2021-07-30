package com.abs.edis.mapping;

import com.google.gson.*;
import org.apache.commons.cli.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class EdisMapping {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Options COMMAND_LINE_OPTIONS = new Options();
    private static CommandLineParser COMMAND_LINE_PARSER = new DefaultParser();
    private static Map<String, Command> COMMANDS;

    static {
        COMMAND_LINE_OPTIONS = new Options();
        COMMAND_LINE_OPTIONS.addOption(Option.builder("a")
                .longOpt("action")
                .hasArg(true)
                .desc("Task to execute.")
                .required(false)
                .build());


        COMMAND_LINE_OPTIONS.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("Task to execute.")
                .required(false)
                .build());


        COMMAND_LINE_OPTIONS.addOption(Option.builder("s")
                .longOpt("sheet")
                .hasArg(true)
                .desc("Task to execute.")
                .required(false)
                .build());


        COMMANDS = new LinkedHashMap<>();
        Class<?>[] classes = EdisMapping.class.getDeclaredClasses();
        for (Class<?> c : classes) {
            if (Command.class.isAssignableFrom(c) && !c.isInterface()) {
                String name = c.getSimpleName();
                if (name.endsWith("Command")) {
                    name = name.substring(0, name.lastIndexOf("Command"));
                    try {
                        Command command = (Command) c.newInstance();
                        COMMANDS.put(name.toUpperCase(Locale.ROOT), command);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private Context context;

    public EdisMapping(InputStream inputStream) {
        this.context = new Context(inputStream);
    }

    public String process(Node node) {
        Session session = createSession(node).init(context);
        try {
            process(session);
            return session.output;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void process(Session session) throws Exception {
        CommandLine cl = session.commandLine();
        String cmd = "CommandList";
        if (cl.hasOption("a")) {
            cmd = cl.getOptionValue("a");
        }

        Command command = COMMANDS.get(cmd.toUpperCase(Locale.ROOT));
        command.execute(session);
    }

    private Session createSession(Node node) {
        JsonObject jsonObject = estimate(node).getAsJsonObject();
        return GSON.fromJson(jsonObject, Session.class).init(context);
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

    static class Context {
        private File homeDir;
        private File workHome;

        private Context(InputStream inputStream) {
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            workHome = new File(properties.getProperty("work.home"));

        }

        public File getHomeDir() {
            return homeDir;
        }

        public File getWorkHome() {
            return workHome;
        }
    }

    static class Session {

        private transient Context context;
        private transient CommandLine cmd;

        private String commandLine;
        private JsonElement input;
        private String output;

        private Session init(Context context) {
            this.context = context;

            List<String> list = new ArrayList<>();
            if (commandLine != null) {
                StringTokenizer tokenizer = new StringTokenizer(commandLine);
                while (tokenizer.hasMoreTokens()) {
                    list.add(tokenizer.nextToken());
                }
            }

            try {
                this.cmd = COMMAND_LINE_PARSER.parse(COMMAND_LINE_OPTIONS, list.toArray(new String[list.size()]));

            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }

            return this;
        }

        public CommandLine commandLine() {
            return cmd;
        }
    }

    static class XPathMapping {

        private String target;
        private String name;
        private int level;
        private String dataType;
        private String constraints;
        private String cardinality;
        private String rule;
        private String source;

        private Map<String, Object> annotations = new LinkedHashMap<>();

        public XPathMapping target(String target) {
            this.target = target;
            String[] arr = target.split("/");
            this.name = arr[arr.length - 1];
            this.level = arr.length;

            return this;
        }

        public XPathMapping dataType(String dataType) {
            String token = dataType.trim().toLowerCase();
            if (token.contains("(")) {
                this.constraints = token.substring(token.indexOf("(") + 1, token.lastIndexOf(")"));
                this.dataType = token.substring(0, token.indexOf("("));

            } else if (token.contains(" ")) {
                this.dataType = token.substring(0, token.indexOf(" "));

            } else {
                this.dataType = token;
            }

            return this;
        }

        public XPathMapping cardinality(String cardinality) {
            this.cardinality = cardinality;
            return this;
        }

        public XPathMapping rule(String rule) {
            this.rule = rule;
            return this;
        }

        public XPathMapping source(String source) {
            this.source = source;
            return this;
        }

        public String parent() {
            if (target.contains("/")) {
                return target.substring(0, target.lastIndexOf("/"));

            } else {
                return null;
            }
        }

        public boolean mandatory() {
            return cardinality.startsWith("1-");
        }

        public boolean singleValue() {
            return cardinality.endsWith("-1");
        }

        public Object getAnnotation(String name) {
            return annotations.get(name);
        }

        public <T> T getAnnotation(String name, Class<T> type) {
            return (T) annotations.get(name);
        }

        public void annotate(String name, Object value) {
            annotations.put(name, value);
        }
    }

    static class Construction {
        private String type;
        private String variable;
        private Map<String, Array> arrays;

        public void addArray(Array array) {
            if (arrays == null) {
                arrays = new LinkedHashMap<>();
            }

            if (!arrays.containsKey(array.sourcePath)) {
                arrays.put(array.sourcePath, array);
            }
        }


    }

    static class Array {
        private String sourcePath;
        private String parent;
        private String name;
        private String variable;
        private String evaluation;

        public Array(String sourcePath) {
            this.sourcePath = sourcePath;
        }
    }

    static class Assignment {
        private String parent;
        private String evaluation;

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

    static abstract class AbstractXPathMappingCommand implements Command {
        private Set<String> vars = new LinkedHashSet<>();

        protected Map<String, Array> arrays = new LinkedHashMap<>();

        @Override
        public void execute(Session session) throws Exception {

            EdisProject project = null;
            if (session.input != null) {
                project = GSON.fromJson(session.input, EdisProject.class);
            }

            Map<String, XPathMapping> mappings = new LinkedHashMap<>();

            CommandLine cmd = session.commandLine();
            String path = cmd.getOptionValue("f");
            File mappingFile = new File(session.context.getWorkHome(), path);
            String mappingSheet = cmd.getOptionValue("s");

            if (!mappingFile.exists()) {
                throw new IllegalStateException("Can not find mapping file: " + mappingFile.toString());
            }

            XSSFWorkbook workbook = null;
            int targetIndex = 0;
            int typeIndex = 0;
            int cardinalityIndex = 0;
            int ruleIndex = 0;
            int sourceIndex = 0;

            boolean start = false;

            try {
                workbook = new XSSFWorkbook(mappingFile);
                Sheet sheet = workbook.getSheet(mappingSheet);

                Iterator<Row> sheetIterator = sheet.iterator();
                while (sheetIterator.hasNext()) {
                    Row currentRow = sheetIterator.next();
                    if (start) {
                        Cell targetCell = currentRow.getCell(targetIndex);
                        Cell typeCell = currentRow.getCell(typeIndex);
                        Cell cardinalityCell = currentRow.getCell(cardinalityIndex);
                        Cell ruleCell = currentRow.getCell(ruleIndex);
                        Cell sourceCell = currentRow.getCell(sourceIndex);

                        if (!isEmpty(targetCell)) {
                            String xpath = targetCell.getStringCellValue().trim();

                            mappings.put(xpath, new XPathMapping()
                                    .target(xpath)
                                    .dataType(isEmpty(typeCell) ? "" : typeCell.getStringCellValue())
                                    .cardinality(isEmpty(cardinalityCell) ? "" : cardinalityCell.getStringCellValue())
                                    .rule(ruleValue(ruleCell))
                                    .source(sourceValue(sourceCell)));
                        }

                    } else {
                        int first = currentRow.getFirstCellNum();
                        int last = currentRow.getLastCellNum();

                        boolean isLabelRow = false;
                        for (int i = first; i <= last; i++) {
                            Cell cell = currentRow.getCell(i);
                            if (cell != null && cell.getCellType().equals(CellType.STRING) && "#".equals(cell.getStringCellValue().trim())) {
                                isLabelRow = true;
                                start = true;
                            }

                            if (isLabelRow && cell != null && cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue() != null) {
                                String label = cell.getStringCellValue().trim();
                                if (label != null) {
                                    switch (label) {
                                        case "Target":
                                            targetIndex = i;
                                            break;
                                        case "DataType":
                                            typeIndex = i;
                                            break;
                                        case "Cardinality":
                                            cardinalityIndex = i;
                                            break;
                                        case "Mapping":
                                            ruleIndex = i;
                                            break;
                                        case "Source":
                                            sourceIndex = i;
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }

                recalculate(mappings);

                session.output = render(mappings, project);

            } catch (Exception e) {
                session.output = e.getMessage();

            } finally {
                workbook.close();
            }
        }

        private void recalculate(Map<String, XPathMapping> mappings) {
            for (Map.Entry<String, XPathMapping> entry : mappings.entrySet()) {
                XPathMapping mapping = entry.getValue();
                if (mapping.rule != null) {
                    touchParent(mappings, mapping);

                    String rule = mapping.rule.toUpperCase();
                    Assignment assignment = new Assignment();

                    if (rule.contains("DEFAULT")) {
                        assignment.evaluation = mapping.rule.substring(mapping.rule.indexOf("(") + 1, mapping.rule.lastIndexOf(")"));

                    } else if (rule.equals("ASSIGN")) {
                        String src = mapping.source;
                        if (src.contains("[*]")) {
                            String parent = src.substring(0, src.lastIndexOf("[*]")) + "[*]";
                            assignment.parent = parent;
                            if (arrays.containsKey(parent)) {
                                assignment.parent = parent;
                                Array parentArray = arrays.get(assignment.parent);
                                assignment.evaluation = parentArray.variable + src.substring(parent.length());

                            } else {
                                assignment.evaluation = "$." + src;
                            }
                        } else {
                            assignment.evaluation = "$." + src;
                        }

                    } else {
                        assignment.evaluation = "???";

                    }

                    mapping.annotate("assignment", assignment);
                }
            }
        }

        private void touchParent(Map<String, XPathMapping> mappings, XPathMapping mapping) {
            String parent = mapping.parent();
            while (parent != null) {
                XPathMapping parentMapping = mappings.get(parent);
                if (parentMapping != null && parentMapping.getAnnotation("construct") == null) {
                    Construction construction = new Construction();
                    String var = parentMapping.target;
                    if (var.contains("/")) {
                        var = var.substring(var.lastIndexOf("/") + 1);
                    }
                    construction.variable = getVariable(var + "_");
                    if (parentMapping.singleValue()) {
                        construction.type = "object";

                    } else {
                        construction.type = "array";

                        String path = mapping.source;
                        if (path.contains("[*]")) {
                            path = path.substring(0, path.lastIndexOf("[*]"));
                            String parentPath = null;
                            if (path.contains("[*]")) {
                                parentPath = path.substring(0, path.lastIndexOf("[*]")) + "[*]";
                                if (arrays.containsKey(parentPath)) {
                                    Array parentArray = arrays.get(parentPath);
                                }
                            }

                            String base = path;
                            if (base.contains(".")) {
                                base = base.substring(base.lastIndexOf(".") + 1);
                            }
                            base = getVariable(base);

                            path = path + "[*]";
                            if (!arrays.containsKey(path)) {
                                Array array = new Array(path);
                                arrays.put(array.sourcePath, array);

                                array.name = base;
                                array.variable = "_" + base;
                                array.parent = parentPath;

                                if (arrays.containsKey(array.parent)) {
                                    Array parentArray = arrays.get(array.parent);
                                    array.evaluation = parentArray.variable + array.sourcePath.substring(array.parent.length());
                                } else {
                                    array.evaluation = "$." + array.sourcePath;
                                }

                                construction.addArray(array);

                            }

                        }
                    }

                    parentMapping.annotate("construct", construction);
                    parent = parentMapping.parent();

                } else {
                    break;
                }
            }
        }

        private String getVariable(String base) {
            String token = base;
            int count = 0;
            while (vars.contains(token)) {
                count++;
                token = token.endsWith("_") ? (base + count) : (base + "_" + count);
            }
            vars.add(token);

            return token;
        }

        protected abstract String render(Map<String, XPathMapping> mappings, EdisProject project);

        private boolean isEmpty(Cell cell) {
            return cell == null || cell.getStringCellValue() == null || cell.getStringCellValue().trim().length() == 0;
        }

        private String ruleValue(Cell cell) {
            if (cell != null && cell.getStringCellValue() != null && cell.getStringCellValue().trim().length() > 0) {
                String token = cell.getStringCellValue().trim().toUpperCase();
                if (token.contains("DEFAULT")) {
                    String value = "???";
                    if (token.contains("'")) {
                        int begin = token.indexOf("'");
                        int end = token.lastIndexOf("'");
                        value = cell.getStringCellValue().substring(begin, end + 1);
                    }

                    return "DEFAULT(" + value + ")";

                } else if (token.contains("DIRECT")) {
                    return "ASSIGN";

                } else {
                    return "TODO";
                }
            }

            return null;
        }

        private String sourceValue(Cell cell) {

            if (cell != null && cell.getStringCellValue() != null) {
                String token = cell.getStringCellValue().trim();
                if (token.contains(" ")) {
                    return "???";
                } else {
                    return token.replaceAll("/", ".");
                }
            }

            return "";
        }

    }

    static class MappingsCommand extends AbstractXPathMappingCommand {
        @Override
        protected String render(Map<String, XPathMapping> mappings, EdisProject project) {
            List<XPathMapping> list = new ArrayList<>(mappings.values());
            return GSON.toJson(list);
        }
    }

    static class ArrayMappingsCommand extends AbstractXPathMappingCommand {
        @Override
        protected String render(Map<String, XPathMapping> mappings, EdisProject project) {
            return GSON.toJson(arrays.values());
        }
    }

    static class XPathMappingCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Map<String, XPathMapping> mappings, EdisProject project) {
            StringBuilder builder = new StringBuilder();

            mappings.entrySet().forEach(e -> {
                builder.append(e.getKey() + "=" + exp(e.getValue())).append("\n");
            });

            return builder.toString();
        }

        private String exp(XPathMapping mapping) {
            String ruleValue = mapping.rule;
            String sourceValue = mapping.source;

            if (ruleValue == null) {
                return sourceValue;

            } else if (ruleValue.endsWith(")")) {
                return ruleValue;

            } else {
                return ruleValue + "(" + sourceValue + ")";
            }
        }
    }

    static class XPathJsonTypeCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Map<String, XPathMapping> mappings, EdisProject project) {
            StringBuilder builder = new StringBuilder();
            mappings.entrySet().forEach(e -> {
                builder.append(e.getKey()).append("=");
                XPathMapping mapping = e.getValue();
                String jsonType = "string";

                String type = mapping.dataType.toLowerCase();
                if (type.contains("(")) {
                    type = type.substring(0, type.indexOf("("));
                }

                boolean arr = false;
                String cardinality = mapping.cardinality;
                if (cardinality.contains("-")) {
                    cardinality = cardinality.substring(mapping.cardinality.lastIndexOf('-') + 1);
                }

                if ("n".equalsIgnoreCase(cardinality) || Integer.parseInt(cardinality) > 1) {
                    arr = true;

                }

                if (arr) {
                    if (type.equals("boolean")) {
                        jsonType = "booleanArray";

                    } else if (type.equals("short")) {
                        jsonType = "shortArray";

                    } else if (type.equals("integer")) {
                        jsonType = "integerArray";

                    } else if (type.equals("long")) {
                        jsonType = "longArray";

                    } else if (type.equals("float")) {
                        jsonType = "floatArray";

                    } else if (type.equals("double")) {
                        jsonType = "doubleArray";

                    } else if (type.equals("decimal")) {
                        jsonType = "decimalArray";

                    } else if (type.startsWith("complex")) {
                        jsonType = "array";

                    } else {
                        jsonType = "stringArray";
                    }


                } else if (type.startsWith("complex")) {
                    jsonType = "object";

                } else if (type.equals("boolean")
                        || type.equals("short")
                        || type.equals("integer")
                        || type.equals("long")
                        || type.equals("float")
                        || type.equals("double")) {

                    jsonType = type;

                } else if (type.equals("decimal")) {
                    jsonType = "double";

                }

                builder.append(jsonType).append("\n");
            });

            return builder.toString();
        }
    }

    static class JsonTypeCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Map<String, XPathMapping> mappings, EdisProject project) {
            StringBuilder builder = new StringBuilder();
            mappings.entrySet().forEach(e -> {
                XPathMapping mapping = e.getValue();
                String jsonType = "string";

                String type = mapping.dataType.toLowerCase();
                if (type.contains("(")) {
                    type = type.substring(0, type.indexOf("("));
                }

                boolean arr = false;
                String cardinality = mapping.cardinality;
                if (cardinality.contains("-")) {
                    cardinality = cardinality.substring(mapping.cardinality.lastIndexOf('-') + 1);
                }

                if ("n".equalsIgnoreCase(cardinality) || Integer.parseInt(cardinality) > 1) {
                    arr = true;

                }

                if (arr) {
                    if (type.equals("boolean")) {
                        jsonType = "booleanArray";

                    } else if (type.equals("short")) {
                        jsonType = "shortArray";

                    } else if (type.equals("integer")) {
                        jsonType = "integerArray";

                    } else if (type.equals("long")) {
                        jsonType = "longArray";

                    } else if (type.equals("float")) {
                        jsonType = "floatArray";

                    } else if (type.equals("double")) {
                        jsonType = "doubleArray";

                    } else if (type.equals("decimal")) {
                        jsonType = "decimalArray";

                    } else if (type.startsWith("complex")) {
                        jsonType = "array";

                    } else {
                        jsonType = "stringArray";
                    }


                } else if (type.startsWith("complex")) {
                    jsonType = "object";

                } else if (type.equals("boolean")
                        || type.equals("short")
                        || type.equals("integer")
                        || type.equals("long")
                        || type.equals("float")
                        || type.equals("double")) {

                    jsonType = type;

                } else if (type.equals("decimal")) {
                    jsonType = "double";

                }

                if (!jsonType.equals("string") && !jsonType.equals("object")) {
                    builder.append(jsonType).append("(").append(mapping.target).append(");");
                }
            });

            return builder.toString();
        }
    }

    static class ConstructCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Map<String, XPathMapping> mappings, EdisProject project) {
            CodeBuilder builder = CodeBuilder.newInstance();

            mappings.entrySet().forEach(e -> {
                XPathMapping mapping = e.getValue();
                builder.append(e.getKey()).append("=");
                if (mapping.getAnnotation("construct") != null) {
                    Construction construction = mapping.getAnnotation("construct", Construction.class);
                    builder.append("construct()");
                    if (construction.arrays != null) {
                        construction.arrays.values().forEach(array -> {
                            builder.append(".array(")
                                    .append(array.name)
                                    .append(", ")
                                    .append(array.variable)
                                    .append(", ")
                                    .append(array.evaluation)
                                    .append(")");
                        });
                    }
                } else if (mapping.getAnnotation("assignment") != null) {
                    builder.append(mapping.getAnnotation("assignment", Assignment.class).evaluation);
                }

                builder.appendLine();
            });

            return builder.toString();
        }
    }

    static class EsqlCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Map<String, XPathMapping> mappings, EdisProject project) {
            EdisProject.MessageFlow flow = project.getMessageFlow();
            EdisProject.Node transformer = flow.getTransformer();
            CodeBuilder builder = CodeBuilder.newInstance();

            int indent = 0;

            builder.append("BROKER SCHEMA ").
                    appendLine(flow.getBrokerSchema()).appendLine()
                    .append("CREATE COMPUTE MODULE ").appendLine(transformer.getName() + "_Compute");

            indent++;
            builder.appendLine("-- Declare Namespace", indent)
                    .appendLine("DECLARE Abs NAMESPACE 'https://collab.safeway.com/it/architecture/info/default.aspx';", indent)
                    .appendLine();

            builder.appendLine("CREATE FUNCTION Main() RETURNS BOOLEAN", indent)
                    .appendLine("BEGIN", indent)
                    .appendLine();

            indent++;
            builder.appendLine("-- Declare Input Message Root", indent)
                    .appendLine("DECLARE _inputRootNode REFERENCE TO InputRoot.JSON.Data;", indent)
                    .appendLine();

            builder.appendLine("-- Declare Output Message Root", indent)
                    .appendLine("CREATE LASTCHILD OF OutputRoot DOMAIN 'XMLNSC';", indent)
                    .appendLine();

            for (Map.Entry<String, XPathMapping> e : mappings.entrySet()) {
                XPathMapping m = e.getValue();
                XPathMapping p = mappings.get(m.parent());
                if (m.parent() == null) {

                    builder.appendLine("-- " + m.target, indent + m.level)
                            .append("DECLARE ", indent + m.level)
                            .append(m.getAnnotation("construct", Construction.class).variable)
                            .append(" REFERENCE TO OutputRoot.XMLNSC.")
                            .append(m.name).appendLine(";")
                            .append("CREATE LASTCHILD OF OutputRoot.XMLNSC AS ", indent + m.level)
                            .append(m.getAnnotation("construct", Construction.class).variable)
                            .append(" TYPE XMLNSC.Folder NAME '")
                            .append(m.name)
                            .appendLine("';")
                            .append("SET OutputRoot.XMLNSC.", indent + m.level)
                            .append(m.name)
                            .appendLine(".(XMLNSC.NamespaceDecl)xmlns:Abs=Abs;")
                            .appendLine();


                } else if (m.getAnnotation("construct") != null) {
                    Construction mc = m.getAnnotation("construct", Construction.class);
                    Construction pc = p.getAnnotation("construct", Construction.class);

                    builder.appendLine("-- " + m.target, indent + m.level);
                    if ("object".equals(mc.type)) {
                        if (m.level > 2) {
                            builder.append("DECLARE ", indent + m.level)
                                    .append(mc.variable)
                                    .append(" REFERENCE TO ")
                                    .append(pc.variable)
                                    .appendLine(";")
                                    .append("CREATE LASTCHILD OF ", indent + m.level)
                                    .append(pc.variable)
                                    .append(" AS ")
                                    .append(mc.variable)
                                    .append(" TYPE XMLNSC.Folder NAME 'Abs:")
                                    .append(m.name)
                                    .appendLine("';")
                                    .appendLine();

                        } else {
                            builder.append("DECLARE ", indent + m.level)
                                    .append(mc.variable)
                                    .append(" REFERENCE TO ")
                                    .append(pc.variable)
                                    .appendLine(";")
                                    .append("CREATE LASTCHILD OF ", indent + m.level)
                                    .append(pc.variable)
                                    .append(" AS ")
                                    .append(mc.variable)
                                    .append(" TYPE XMLNSC.Folder NAME '")
                                    .append(m.name)
                                    .appendLine("';")
                                    .appendLine();

                        }

                    } else if ("array".equals(m.getAnnotation("construct"))) {

                    }

                } else if (m.getAnnotation("assignment") != null) {
                    Assignment assignment = m.getAnnotation("assignment", Assignment.class);
                    String evaluation = assignment.evaluation;
                    if (evaluation.startsWith("$.")) {
                        evaluation = evaluation.substring(2);
                    }
                    Construction pc = p.getAnnotation("construct", Construction.class);
                    builder.appendLine("-- " + m.target, indent + m.level)
                            .append("SET ", indent + m.level)
                            .append(pc.variable)
                            .append(".(XMLNSC.Field)Abs:")
                            .append(m.name)
                            .append(" = ")
                            .append(evaluation)
                            .appendLine(";")
                            .appendLine();

                }
            }

            builder.appendLine("RETURN TRUE;", indent);
            indent--;

            builder.appendLine("END;", indent);

            indent--;
            builder.appendLine("END MODULE;");

            return builder.toString();
        }
    }

}
