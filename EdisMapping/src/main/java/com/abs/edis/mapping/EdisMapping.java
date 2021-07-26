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
        private String dataType;
        private String cardinality;
        private String rule;
        private String source;

        public XPathMapping target(String target) {
            this.target = target;
            return this;
        }

        public XPathMapping dataType(String dataType) {
            this.dataType = dataType;
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

                session.output = render(mappings);

            } catch (Exception e) {
                session.output = e.getMessage();

            } finally {
                workbook.close();
            }
        }

        protected abstract String render(Map<String, XPathMapping> mappings);

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
        protected String render(Map<String, XPathMapping> mappings) {
            List<XPathMapping> list = new ArrayList<>(mappings.values());
            return GSON.toJson(list);
        }
    }

    static class XPathMappingCommand extends AbstractXPathMappingCommand {

        @Override
        protected String render(Map<String, XPathMapping> mappings) {
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
        protected String render(Map<String, XPathMapping> mappings) {
            StringBuilder builder = new StringBuilder();
            mappings.entrySet().forEach(e -> {
                XPathMapping mapping = e.getValue();
                String jsonType = "string";

                String type = mapping.dataType.toLowerCase();
                if (type.contains("(")) {
                    type = type.substring(0, type.indexOf("("));
                }

                String cardinality = mapping.cardinality;
                if (cardinality.contains("-")) {
                    cardinality = cardinality.substring(mapping.cardinality.lastIndexOf('-') + 1);
                }

                if ("n".equalsIgnoreCase(cardinality) || Integer.parseInt(cardinality) > 1) {
                    jsonType = "array";

                } else if (type.startsWith("complex")) {
                    jsonType = "object";

                } else if (type.equals("boolean")
                        || type.equals("short")
                        || type.equals("integer")
                        || type.equals("long")
                        || type.equals("float")
                        || type.equals("double")) {

                    jsonType = type;

                } else if(type.equals("decimal")) {
                    jsonType = "double";

                }

                if (!jsonType.equals("string") && !jsonType.equals("object")) {
                    builder.append(jsonType).append("(").append(mapping.target).append(");");
                }
            });

            return builder.toString();
        }
    }

}
