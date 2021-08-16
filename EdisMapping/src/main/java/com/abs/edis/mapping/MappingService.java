package com.abs.edis.mapping;

import com.google.gson.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.*;

public class MappingService {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, Command> COMMANDS;

    static {
        COMMANDS = new LinkedHashMap<>();
        Class<?>[] classes = MappingService.class.getDeclaredClasses();
        for (Class<?> c : classes) {
            if (Command.class.isAssignableFrom(c) && !c.isInterface()) {
                String name = c.getSimpleName();
                if (name.endsWith("Command")) {
                    name = name.substring(0, name.lastIndexOf("Command"));
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
    }

    public static void main(String[] args) {
        Request request = new Request();
        request.command = "Mapping";
        request.context = "C:/Users/qwen002/IBM/IIBT10/workspace/AppBuild/BusinessObjects/GroceryOrder";
        request.files = new FileSet();
        request.files.xpathDataType = "xpath-data-type.properties";
        request.files.mappingFile = "GroceryOrder_ERUMS_to_Canonical_Mapping_v1.13.1.xlsx";

        try {
            String result = COMMANDS.get(request.command.toUpperCase()).execute(new Session(request));

            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String process(Node node) {
        JsonElement jsonElement = estimate(node);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            Request request = GSON.fromJson(jsonElement, Request.class);
            try {
                return COMMANDS.get(request.command.toUpperCase()).execute(new Session(request));

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

    private static XPathMapping parseLine(String line) {
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

    private static String getJsonType(String type) {
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

    static class FileSet {

        private String mappingFile = "???";
        private String mappingSheet = "???";

        private String xpathDataType = "xpath-data-type.properties";
        private String xpathJsonType = "xpath-json-type.properties";

        private String xpathMappings = "xpath-mappings.properties";
        private String xpathMappingAdjustments = "xpath-mapping-adjustment.properties";
        private String xpathConstruction = "xpath-construction.properties";

        public String getMappingFile() {
            return mappingFile;
        }

        public String getMappingSheet() {
            return mappingSheet;
        }

        public String getXpathDataType() {
            return xpathDataType;
        }

        public String getXpathJsonType() {
            return xpathJsonType;
        }

        public String getXpathMappings() {
            return xpathMappings;
        }

        public String getXpathMappingAdjustments() {
            return xpathMappingAdjustments;
        }

        public String getXpathConstruction() {
            return xpathConstruction;
        }
    }

    static class Request {
        private String command;
        private String context;
        private FileSet files;
    }

    static class Session {
        private Request request;

        private File requirementDir;
        private File workDir;

        private File mappingFile;
        private File adjustmentFile;

        private Map<String, XPathMapping> schema;
        private Map<String, XPathMapping> mappings;
        private Map<String, Array> arrays = new LinkedHashMap<>();

        private Session(Request request) throws IOException {
            this.request = request;

            File base = new File(request.context);
            this.requirementDir = new File(base, "requirement");
            this.workDir = new File(base, "work");

            FileSet fileset = request.files;
            this.schema = schema(new File(workDir, fileset.xpathDataType));

            this.mappingFile = new File(requirementDir, fileset.mappingFile);
            this.adjustmentFile = new File(workDir, fileset.getXpathMappingAdjustments());

            if (mappingFile.exists()) {
                XSSFWorkbook workbook = null;
                try {
                    workbook = new XSSFWorkbook(mappingFile);
                    Iterator<Sheet> iterator = workbook.sheetIterator();
                    while (iterator.hasNext()) {
                        Sheet sheet = iterator.next();
                        if (isMappingSheet(sheet)) {
                            this.mappings = parse(sheet);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (workbook != null) {
                        workbook.close();

                    }
                }
            }

            if (mappings != null && mappings.size() > 0) {
                recalculate();
            }
        }

        private Map<String, XPathMapping> schema(File file) throws IOException {
            Map<String, XPathMapping> mappings = new LinkedHashMap<>();

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("=")) {
                    XPathMapping mapping = parseLine(line);
                    mappings.put(mapping.target, mapping);
                }
            }

            return mappings;
        }

        private boolean isMappingSheet(Sheet sheet) {
            int rowNum = Math.min(10, sheet.getLastRowNum());
            for (int i = sheet.getFirstRowNum(); i < rowNum; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    int colNum = Math.max(5, row.getLastCellNum());
                    for (int j = row.getFirstCellNum(); j < colNum; j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null && CellType.STRING.equals(cell.getCellType())
                                && cell.getStringCellValue() != null
                                && "#".equals(cell.getStringCellValue().trim())) {
                            return true;
                        }
                    }

                }
            }

            return false;
        }

        private Map<String, XPathMapping> parse(Sheet sheet) throws Exception {
            Properties adjustments = new Properties();
            if (adjustmentFile.exists()) {
                adjustments.load(new FileInputStream(adjustmentFile));
            }

            Map<String, XPathMapping> mappings = new LinkedHashMap<>();

            int targetIndex = 0;
            int typeIndex = 0;
            int cardinalityIndex = 0;
            int ruleIndex = 0;
            int sourceIndex = 0;
            int versionIndex = 0;

            boolean start = false;

            try {
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
                            String path = getCorrectPath(xpath);
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

                                if (!ignore) {
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
                e.printStackTrace();
            }

            return mappings;
        }

        private void recalculate() {
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
                        mapping.unknown = "rule";

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
                                Array array = new Array(parentMapping.target, path);
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

        private String getCorrectPath(String xpath) {
            if (schema.containsKey(xpath)) {
                return xpath;

            } else {
                int slash = xpath.lastIndexOf("/");
                String attrPath = xpath.substring(0, slash + 1) + "@" + xpath.substring(slash + 1);
                if (schema.containsKey(attrPath)) {
                    return attrPath;

                } else {
                    for (String path : schema.keySet()) {
                        if (path.equalsIgnoreCase(xpath)) {
                            return path;
                        }
                    }

                    return null;
                }
            }
        }

        private boolean isEmpty(Cell cell) {
            return cell == null
                    || !CellType.STRING.equals(cell.getCellType())
                    || cell.getStringCellValue() == null
                    || cell.getStringCellValue().trim().length() == 0;
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
        private String targetPath;
        private String sourcePath;
        private String parent;
        private String name;
        private String variable;
        private String evaluation;

        public Array(String targetPath, String sourcePath) {
            this.targetPath = targetPath;
            this.sourcePath = sourcePath;
        }
    }

    static class Assignment {
        private String parent;
        private String evaluation;

    }

    static class TreeNode {
        private XPathMapping mapping;

        private TreeNode parent;
        private List<TreeNode> children = new ArrayList<>();

        public TreeNode(Session session) {
            Map<String, TreeNode> map = new LinkedHashMap<>();

            session.mappings.values().forEach(e -> {
                if (e.parent() == null) {
                    this.mapping = e;
                    map.put(e.target, this);

                } else {
                    String parentPath = e.parent();
                    TreeNode parent = map.get(parentPath);

                    TreeNode node = new TreeNode(e);
                    node.parent = parent;
                    parent.children.add(node);

                    map.put(e.target, node);

                }
            });

        }

        private TreeNode(XPathMapping mapping) {
            this.mapping = mapping;
        }
    }

    interface Command {
        String execute(Session session) throws Exception;
    }

    static class MappingCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {

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

    static class UnknownMappingsCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
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

    static class XPathJsonTypeMappingsCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
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
                        type = type + "_array";
                    }

                    if (!"string".equals(type)) {
                        builder.append(mapping.target).append("=").append(type).append("\n");
                    }

                }
            });

            return builder.toString();
        }
    }

    static class JsonTypeMappingsCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
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
                        type = type + "_array";
                    }

                    if (!"string".equals(type)) {
                        builder.append(type).append("(").append(mapping.target).append(")").append(";");
                    }

                }
            });

            return builder.toString();
        }
    }

    static class ArrayMappingsCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            return GSON.toJson(session.arrays.values());
        }
    }

    static class ConstructCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            TreeNode tree = new TreeNode(session);
            CodeBuilder builder = CodeBuilder.newInstance();
            printNode(tree, builder);

            return builder.toString();
        }

        private void printNode(TreeNode node, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            builder.append("", mapping.level - 1);

            builder.append(node.mapping.name).append(": // ");
            builder.append(mapping.target).append(" = function()");
            builder.appendLine();
            node.children.forEach(c -> {
                printNode(c, builder);
            });
        }
    }
}
