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

import java.io.*;
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

    private static XPathMapping parse(String line) {
        XPathMapping mapping = null;
        if (line.contains("=")) {
            String target = line.substring(0, line.indexOf("=")).trim();
            mapping = new XPathMapping().target(target);

            String value = line.substring(line.indexOf("=") + 1).trim();
            StringTokenizer tokenizer = new StringTokenizer(value, "::");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                int begin = token.indexOf("(");
                int end = token.lastIndexOf(")");
                if (begin > 0 && end > begin) {
                    String name = token.substring(0, begin);
                    String param = token.substring(begin + 1, end);

                    if ("type".equals(name)) {
                        mapping.dataType(param);

                    } else if ("cardinality".equals(name)) {
                        mapping.cardinality(param);

                    } else if ("construct".equals(name)) {

                    } else if ("array".equals(name)) {

                    } else if ("assign".equals(name)) {

                    } else if ("parent".equals(name)) {
                        if (mapping.assignment != null) {
                            mapping.assignment.parent = param;

                        } else if (mapping.construction != null) {

                        }
                    }
                }
            }
        }

        return mapping;
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
        private transient EdisProject project;

        private Map<String, XPathMapping> schema;

        private Map<String, XPathMapping> mappings;
        private Map<String, Array> arrays;


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

            this.project = GSON.fromJson(input, EdisProject.class);

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

        private String version;

        private Construction construction;
        private Assignment assignment;

        private String unknown;

        public XPathMapping target(String target) {
            this.target = target;
            String[] arr = target.split("/");
            this.name = arr[arr.length - 1];
            this.level = arr.length;

            return this;
        }

        public XPathMapping dataType(String dataType) {
            String token = dataType.trim();
            if (token.contains("(")) {
                this.constraints = token.substring(token.indexOf("(") + 1, token.lastIndexOf(")")).trim();
                this.dataType = token.substring(0, token.indexOf("(")).trim();

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

        public XPathMapping version(String version) {
            this.version = version;
            return this;
        }

        public XPathMapping unknown(String unknown) {
            this.unknown = unknown;
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
    }

    static class Construction {
        private String type;
        private String variable;
        private List<Array> arrays;

        public void addArray(Array array) {
            if (arrays == null) {
                arrays = new ArrayList<>();
            }

            arrays.add(array);
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

        @Override
        public void execute(Session session) throws Exception {
            session.schema = schema(session);
            session.mappings = mappings(session);

            recalculate(session);

            session.output = render(session);

        }

        protected String getJsonType(String type) {
            String result = null;
            String token = type.toLowerCase();

            switch (token) {
                case "date":
                case "time":
                case "datetime":
                case "normalizedstring":
                    result = "string";
                    break;

                case "decimal":
                    result = "double";
                    break;

                case "int":
                case "positiveinteger":
                case "negativeinteger":
                    result = "integer";
                    break;

                case "string":
                case "boolean":
                case "short":
                case "integer":
                case "long":
                case "float":
                case "double":
                    result = token;

                default:
                    result = "string";
            }

            return result;
        }

        private Map<String, XPathMapping> schema(Session session) throws IOException {
            Map<String, XPathMapping> mappings = new LinkedHashMap<>();

            File schemaFile = new File(session.context.getWorkHome(), session.project.getMappings().getSchema());
            BufferedReader br = new BufferedReader(new FileReader(schemaFile));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("=")) {
                    XPathMapping mapping = parse(line);
                    mappings.put(mapping.target, mapping);
                }
            }

            return mappings;
        }

        private Map<String, XPathMapping> mappings(Session session) throws Exception {
            Properties adjustments = new Properties();
            File adjustFile = new File(session.context.getWorkHome(), session.project.getMappings().getMappingAdjustment());
            if (adjustFile.exists()) {
                adjustments.load(new FileInputStream(adjustFile));
            }

            Map<String, XPathMapping> mappings = new LinkedHashMap<>();
            CommandLine cmd = session.commandLine();
            File mappingFile = new File(session.context.getWorkHome(), session.project.getMappings().getMappingFile());
            String mappingSheet = session.project.getMappings().getMappingSheet();

            if (!mappingFile.exists()) {
                throw new IllegalStateException("Can not find mapping file: " + mappingFile.toString());
            }

            XSSFWorkbook workbook = null;
            int targetIndex = 0;
            int typeIndex = 0;
            int cardinalityIndex = 0;
            int ruleIndex = 0;
            int sourceIndex = 0;
            int versionIndex = 0;

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
                        Cell versionCell = currentRow.getCell(versionIndex);

                        if (!isEmpty(targetCell)) {
                            String xpath = targetCell.getStringCellValue().trim();
                            String path = getCorrectPath(xpath, session);
                            if (path != null) {
                                mappings.put(path, new XPathMapping()
                                        .target(path)
                                        .dataType(isEmpty(typeCell) ? "" : typeCell.getStringCellValue())
                                        .cardinality(isEmpty(cardinalityCell) ? "" : cardinalityCell.getStringCellValue())
                                        .rule(ruleValue(ruleCell))
                                        .source(sourceValue(sourceCell))
                                        .version(cellValue(versionCell))
                                );

                            } else if (adjustments.containsKey(xpath)) {
                                XPathMapping adj = new XPathMapping()
                                        .target(xpath)
                                        .dataType(isEmpty(typeCell) ? "" : typeCell.getStringCellValue())
                                        .cardinality(isEmpty(cardinalityCell) ? "" : cardinalityCell.getStringCellValue())
                                        .rule(ruleValue(ruleCell))
                                        .source(sourceValue(sourceCell))
                                        .version(cellValue(versionCell));

                                String value = adjustments.getProperty(xpath);

                                boolean ignore = false;
                                StringTokenizer tokenizer = new StringTokenizer(value, "::");
                                while (tokenizer.hasMoreTokens()) {
                                    String token = tokenizer.nextToken();
                                    int begin = token.indexOf("(");
                                    int end = token.lastIndexOf(")");
                                    String func = token.substring(0, begin);
                                    String param = token.substring(begin + 1, end);

                                    if ("ignore".equals(func)) {
                                        ignore = true;
                                        break;

                                    } else if ("path".equals(func)) {
                                        adj.target(param);

                                    }
                                }

                                if(!ignore) {
                                    mappings.put(adj.target, adj);
                                }

                            } else {
                                mappings.put(xpath, new XPathMapping()
                                        .target(xpath)
                                        .dataType(isEmpty(typeCell) ? "" : typeCell.getStringCellValue())
                                        .cardinality(isEmpty(cardinalityCell) ? "" : cardinalityCell.getStringCellValue())
                                        .rule(ruleValue(ruleCell))
                                        .source(sourceValue(sourceCell))
                                        .version(cellValue(versionCell))
                                        .unknown("path")
                                );
                            }

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
                                        case "Version":
                                            versionIndex = i;
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {

            } finally {
                workbook.close();
            }

            return mappings;
        }

        private String getCorrectPath(String xpath, Session session) {
            if (session.schema.containsKey(xpath)) {
                return xpath;

            } else {
                int slash = xpath.lastIndexOf("/");
                String attrPath = xpath.substring(0, slash + 1) + "@" + xpath.substring(slash + 1);
                if (session.schema.containsKey(attrPath)) {
                    return attrPath;

                } else {
                    for (String path : session.schema.keySet()) {
                        if (path.equalsIgnoreCase(xpath)) {
                            return path;
                        }
                    }

                    return null;
                }
            }

        }

        private void recalculate(Session session) {
            Map<String, XPathMapping> schema = session.schema;
            Map<String, XPathMapping> mappings = session.mappings;

            Map<String, Array> arrays = session.arrays;
            if (arrays == null) {
                arrays = new LinkedHashMap<>();
                session.arrays = arrays;
            }
            Set<String> vars = new LinkedHashSet<>();

            for (Map.Entry<String, XPathMapping> entry : mappings.entrySet()) {
                XPathMapping mapping = entry.getValue();

                if (mapping.rule != null) {
                    touchParent(mappings, mapping, vars, arrays);

                    String rule = mapping.rule.toUpperCase();
                    Assignment assignment = new Assignment();

                    if (rule.contains("DEFAULT")) {
                        assignment.evaluation = mapping.rule.substring(mapping.rule.indexOf("(") + 1, mapping.rule.lastIndexOf(")"));

                    } else if (rule.equals("DIRECT")) {
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

                    mapping.assignment = assignment;
                }
            }
        }

        private void touchParent(Map<String, XPathMapping> mappings, XPathMapping mapping, Set<String> vars, Map<String, Array> arrays) {
            String parent = mapping.parent();
            while (parent != null) {
                XPathMapping parentMapping = mappings.get(parent);
                if (parentMapping != null && parentMapping.construction == null) {
                    Construction construction = new Construction();
                    String var = parentMapping.target;
                    if (var.contains("/")) {
                        var = var.substring(var.lastIndexOf("/") + 1);
                    }
                    construction.variable = getVariable(var + "_", vars);
                    if (parentMapping.singleValue()) {
                        construction.type = "complex";

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
                            base = getVariable(base, vars);

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

                    parentMapping.construction = construction;
                    parent = parentMapping.parent();

                } else {
                    break;
                }
            }
        }

        private String getVariable(String base, Set<String> vars) {
            String token = base;
            int count = 0;
            while (vars.contains(token)) {
                count++;
                token = token.endsWith("_") ? (base + count) : (base + "_" + count);
            }
            vars.add(token);

            return token;
        }

        private boolean isEmpty(Cell cell) {
            return cell == null || cell.getStringCellValue() == null || cell.getStringCellValue().trim().length() == 0;
        }

        private String cellValue(Cell cell) {
            if (cell != null && CellType.STRING.equals(cell.getCellType())) {
                return cell.getStringCellValue().trim();
            }

            return null;
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
                    return "DIRECT";

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

        protected abstract String render(Session session);

    }

    static class MappingsCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            StringBuilder builder = new StringBuilder();
            session.mappings.entrySet().forEach(e -> {
                builder.append(e.getKey()).append("=").append("type(");
                XPathMapping mapping = e.getValue();
                String type = mapping.dataType != null && mapping.dataType.trim().length() > 0 ? mapping.dataType : "???";

                if (Character.isUpperCase(type.charAt(0))) {
                    type = "" + Character.toLowerCase(type.charAt(0)) + type.substring(1);
                }

                builder.append(type).append(")").append("::").append("cardinality(").append(mapping.cardinality).append(")");

                if (mapping.rule != null) {
                    builder.append("::");
                    if ("DEFAULT".equals(mapping.rule)) {
                        builder.append(mapping.rule);

                    } else if ("DIRECT".equals(mapping.rule)) {
                        builder.append("DIRECT(").append(mapping.source).append(")");

                    } else {
                        builder.append("TODO()");
                    }

                    if (mapping.version != null) {
                        builder.append("::").append("version(").append(mapping.version).append(")");
                    }
                }

                if (mapping.unknown != null) {
                    builder.append("::").append("unknown(").append(mapping.unknown).append(")");
                }

                builder.append("\n");


            });

            return builder.toString();
        }
    }

    static class UnknownMappingsCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            StringBuilder builder = new StringBuilder();
            session.mappings.entrySet().forEach(e -> {
                XPathMapping mapping = e.getValue();
                if (mapping.unknown != null) {
                    builder.append(e.getKey()).append("=").append("type(");
                    String type = mapping.dataType != null && mapping.dataType.trim().length() > 0 ? mapping.dataType : "???";

                    if (Character.isUpperCase(type.charAt(0))) {
                        type = "" + Character.toLowerCase(type.charAt(0)) + type.substring(1);
                    }

                    builder.append(type).append(")").append("::").append("cardinality(").append(mapping.cardinality).append(")");

                    if (mapping.rule != null) {
                        builder.append("::");
                        if ("DEFAULT".equals(mapping.rule)) {
                            builder.append(mapping.rule);

                        } else if ("DIRECT".equals(mapping.rule)) {
                            builder.append("DIRECT(").append(mapping.source).append(")");

                        } else {
                            builder.append("TODO()");
                        }

                        if (mapping.version != null) {
                            builder.append("::").append("version(").append(mapping.version).append(")");
                        }
                    }

                    builder.append("::").append("unknown(").append(mapping.unknown).append(")");

                    builder.append("\n");
                }

            });

            return builder.toString();
        }
    }

    static class MappingsDataTypeCommand extends AbstractXPathMappingCommand {
        @Override
        protected String render(Session session) {
            StringBuilder builder = new StringBuilder();
            session.mappings.entrySet().forEach(e -> {
                builder.append(e.getKey()).append("=").append("type(");
                XPathMapping mapping = e.getValue();
                String type = mapping.dataType != null && mapping.dataType.trim().length() > 0 ? mapping.dataType : "???";

                if (Character.isUpperCase(type.charAt(0))) {
                    type = "" + Character.toLowerCase(type.charAt(0)) + type.substring(1);
                }

                builder.append(type).append(")").append("::").append("cardinality(").append(mapping.cardinality).append(")").append("\n");


            });

            return builder.toString();
        }
    }

    static class MappingsJsonTypeCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            StringBuilder builder = new StringBuilder();
            session.mappings.entrySet().forEach(e -> {
                builder.append(e.getKey()).append("=").append("type(");
                XPathMapping mapping = e.getValue();
                String type = mapping.dataType != null && mapping.dataType.trim().length() > 0 ? mapping.dataType : "???";
                String cardinality = mapping.cardinality;

                if (Character.isUpperCase(type.charAt(0))) {
                    type = "" + Character.toLowerCase(type.charAt(0)) + type.substring(1);
                }

                if (session.schema.containsKey(mapping.target)) {
                    type = getJsonType(session.schema.get(mapping.target).dataType);
                    cardinality = session.schema.get(mapping.target).cardinality;
                }

                builder.append(type).append(")").append("::").append("cardinality(").append(mapping.cardinality).append(")").append("\n");

            });

            return builder.toString();
        }
    }

    static class XmlToJsonTypeMappingsCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            StringBuilder builder = new StringBuilder();

            Map<String, XPathMapping> schema = session.schema;
            session.mappings.entrySet().forEach(e -> {

                XPathMapping mapping = e.getValue();
                String cardinality = schema.containsKey(e.getKey()) ? schema.get(e.getKey()).cardinality : mapping.cardinality;

                if (mapping.construction != null && !cardinality.endsWith("-1")) {
                    builder.append(mapping.target).append("=").append("array").append("\n");

                } else if (mapping.assignment != null) {
                    String type = getJsonType(mapping.dataType);
                    if (!cardinality.endsWith("-1")) {
                        type = type + "Array";
                    }
                    if (!"string".equals(type)) {
                        builder.append(mapping.target).append("=").append(type).append("\n");
                    }

                }
            });

            return builder.toString();
        }
    }

    static class JsonTypeMappingsCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            StringBuilder builder = new StringBuilder();

            Map<String, XPathMapping> schema = session.schema;
            session.mappings.entrySet().forEach(e -> {

                XPathMapping mapping = e.getValue();
                String cardinality = schema.containsKey(e.getKey()) ? schema.get(e.getKey()).cardinality : mapping.cardinality;

                if (mapping.construction != null && !cardinality.endsWith("-1")) {
                    builder.append("array(").append(mapping.target).append(")").append(";");


                } else if (mapping.assignment != null) {
                    String type = getJsonType(mapping.dataType);
                    if (!cardinality.endsWith("-1")) {
                        type = type + "Array";
                    }

                    if (!"string".equals(type)) {
                        builder.append(type).append("(").append(mapping.target).append(")").append(";");
                    }

                }
            });

            return builder.toString();
        }
    }

    static class MappingsConstructCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            StringBuilder builder = new StringBuilder();
            session.mappings.entrySet().forEach(e -> {
                builder.append(e.getKey()).append("=").append("type(");
                XPathMapping mapping = e.getValue();
                String type = mapping.dataType != null && mapping.dataType.trim().length() > 0 ? mapping.dataType : "???";
                String cardinality = mapping.cardinality;

                if (Character.isUpperCase(type.charAt(0))) {
                    type = "" + Character.toLowerCase(type.charAt(0)) + type.substring(1);
                }

                if (session.schema.containsKey(mapping.target)) {
                    type = getJsonType(session.schema.get(mapping.target).dataType);
                    cardinality = session.schema.get(mapping.target).cardinality;

                }

                builder.append(type).append(")").append("::").append("cardinality(").append(mapping.cardinality).append(")");

                if (mapping.construction != null) {
                    Construction construction = mapping.construction;
                    builder.append("::").append("construct(").append(construction.variable).append(")");

                    if (construction.arrays != null) {
                        construction.arrays.forEach(a -> {
                            builder.append("::").append("array(")
                                    .append(a.sourcePath).append(", ")
                                    .append(a.name).append(", ")
                                    .append(a.variable).append(", ")
                                    .append(a.evaluation)
                                    .append(")");
                            if (a.parent != null) {
                                builder.append("::").append("parent(").append(a.parent).append(")");
                            }
                        });
                    }

                } else if (mapping.assignment != null) {
                    Assignment assignment = mapping.assignment;
                    builder.append("::").append("assign(").append(assignment.evaluation).append(")");
                    if (assignment.parent != null) {
                        builder.append("::").append("parent(").append(assignment.parent).append(")");
                    }

                }

                builder.append("\n");

            });

            return builder.toString();
        }
    }

    static class MappingsValidationCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            List<String> list = new ArrayList<>();
            session.mappings.entrySet().forEach(e -> {

                if (!session.schema.containsKey(e.getKey())) {
                    list.add("Unknown target path: " + e.getKey());
                } else {
                    XPathMapping m1 = session.schema.get(e.getKey());
                    XPathMapping m2 = session.mappings.get(e.getKey());

                    if (!m1.dataType.equals(m2.dataType)) {
                        list.add("Type mismatch for " + e.getKey() + ": schema type is " + m1.dataType + " while mapping type is " + m2.dataType);
                    }

                    if (!m1.cardinality.equals(m2.cardinality)) {
                        list.add("Cardinality mismatch for " + e.getKey() + ": schema cardinality is " + m1.cardinality + " while mapping cardinality is " + m2.cardinality);
                    }
                }

            });

            return GSON.toJson(list);
        }
    }

    static class ArrayMappingsCommand extends AbstractXPathMappingCommand {
        @Override
        protected String render(Session session) {
            return GSON.toJson(session.arrays.values());
        }
    }

    static class XPathMappingCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            StringBuilder builder = new StringBuilder();

            session.mappings.entrySet().forEach(e -> {
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

    static class ConstructTreeCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            CodeBuilder builder = CodeBuilder.newInstance();

            session.mappings.entrySet().forEach(e -> {
                XPathMapping mapping = e.getValue();
                builder.append(e.getKey()).append("=");
                if (mapping.construction != null) {
                    Construction construction = mapping.construction;
                    builder.append("construct()");
                    if (construction.arrays != null) {
                        construction.arrays.forEach(array -> {
                            builder.append(".array(")
                                    .append(array.name)
                                    .append(", ")
                                    .append(array.variable)
                                    .append(", ")
                                    .append(array.evaluation)
                                    .append(")");
                        });
                    }
                } else if (mapping.assignment != null) {
                    builder.append(mapping.assignment.evaluation);
                }

                builder.appendLine();
            });

            return builder.toString();
        }
    }

    static class EsqlCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Session session) {
            EdisProject.MessageFlow flow = session.project.getMessageFlow();
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

            for (Map.Entry<String, XPathMapping> e : session.mappings.entrySet()) {
                XPathMapping m = e.getValue();
                XPathMapping p = session.mappings.get(m.parent());
                if (m.parent() == null) {

                    builder.appendLine("-- " + m.target, indent + m.level)
                            .append("DECLARE ", indent + m.level)
                            .append(m.construction.variable)
                            .append(" REFERENCE TO OutputRoot.XMLNSC.")
                            .append(m.name).appendLine(";")
                            .append("CREATE LASTCHILD OF OutputRoot.XMLNSC AS ", indent + m.level)
                            .append(m.construction.variable)
                            .append(" TYPE XMLNSC.Folder NAME '")
                            .append(m.name)
                            .appendLine("';")
                            .append("SET OutputRoot.XMLNSC.", indent + m.level)
                            .append(m.name)
                            .appendLine(".(XMLNSC.NamespaceDecl)xmlns:Abs=Abs;")
                            .appendLine();


                } else if (m.construction != null) {
                    Construction mc = m.construction;
                    Construction pc = p.construction;

                    builder.appendLine("-- " + m.target, indent + m.level);
                    if ("complex".equals(mc.type)) {
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

                    } else if ("array".equals("???")) {

                    }

                } else if (m.assignment != null) {
                    Assignment assignment = m.assignment;
                    String evaluation = assignment.evaluation;
                    if (evaluation.startsWith("$.")) {
                        evaluation = evaluation.substring(2);
                    }
                    Construction pc = p.construction;
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
