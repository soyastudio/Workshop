package soya.framework.tools.xmlbeans;

import com.google.common.base.CaseFormat;
import com.google.gson.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import soya.framework.tools.xmlbeans.Buffalo.Annotator;
import soya.framework.tools.xmlbeans.XmlSchemaBase.MappingNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class XlsxMappingAnnotator implements Annotator<XmlSchemaBase>, MappingFeature {

    private List<String> sourceFiles;
    private List<String> excludes;

    private String mappingFile;
    private String mappingSheet;

    private CaseFormat targetCaseFormat = CaseFormat.UPPER_CAMEL;
    private CaseFormat sourceCaseFormat = CaseFormat.LOWER_CAMEL;

    private transient int targetIndex;
    private transient int ruleIndex;
    private transient int sourceIndex;

    private Set<String> ignores = new HashSet<>();
    private Map<String, String> sourcePaths = new LinkedHashMap<>();
    private Map<String, String> sourcePathIndex = new LinkedHashMap<>();

    private JsonObject sourceSchema = new JsonObject();

    public XlsxMappingAnnotator() {
    }

    public void annotate(XmlSchemaBase base) {
        if (sourceFiles != null) {
            sourceFiles.forEach(sf -> {
                try {
                    FileReader reader = new FileReader(WorkshopRepository.getFile(sf));
                    JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                    extract(root, null);
                    base.annotate(SOURCE_PATHS, sourcePaths);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        sourcePaths.keySet().forEach(e -> {
            sourcePathIndex.put(e.toUpperCase(), e);
        });

        if (excludes != null) {
            ignores.addAll(excludes);
        }

        File excelFile = WorkshopRepository.getFile(mappingFile);
        XSSFWorkbook workbook = null;

        try {
            workbook = new XSSFWorkbook(excelFile);
            Sheet mappingSheet = workbook.getSheet(this.mappingSheet);
            loadMappings(base, mappingSheet);
            base.annotate("SOURCE_MAPPING", sourceSchema);

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    private void loadSourcePaths(XmlSchemaBase base, Sheet sheet) {
        StringBuilder builder = new StringBuilder();

        int payloadIndex = -1;
        Iterator<Row> sheetIterator = sheet.iterator();
        while (sheetIterator.hasNext()) {
            Row currentRow = sheetIterator.next();
            if (payloadIndex < 0) {
                int first = currentRow.getFirstCellNum();
                int last = currentRow.getLastCellNum();
                boolean isLabelRow = false;
                for (int i = first; i <= last; i++) {
                    Cell cell = currentRow.getCell(i);
                    if (cell != null && cell.getCellType().equals(CellType.STRING) && "{".equals(cell.getStringCellValue().trim())) {
                        String contents = cell.getStringCellValue().trim();
                        if (contents.contains("//")) {
                            contents = contents.substring(0, contents.indexOf("//")).trim();
                        }
                        builder.append(contents);
                        payloadIndex = cell.getColumnIndex();
                        isLabelRow = true;
                    }
                }
            } else {
                StringBuilder buf = new StringBuilder();
                for (int i = payloadIndex; i <= currentRow.getLastCellNum(); i++) {
                    Cell cell = currentRow.getCell(i);
                    if (!isEmpty(cell) && cell.getCellType().equals(CellType.STRING)) {
                        buf.append(cell.getStringCellValue().trim());
                    }
                }

                String contents = buf.toString();
                if (contents.contains("//")) {
                    contents = contents.substring(0, contents.indexOf("//")).trim();
                }

                builder.append(contents);
            }
        }

        String json = builder.toString();


        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        extract(root, null);
        base.annotate(SOURCE_PATHS, sourcePaths);
    }

    private void loadMappings(XmlSchemaBase base, Sheet mappingSheet) {

        boolean start = false;
        Iterator<Row> sheetIterator = mappingSheet.iterator();
        while (sheetIterator.hasNext()) {
            Row currentRow = sheetIterator.next();

            if (start) {
                Cell targetCell = currentRow.getCell(targetIndex);
                Cell ruleCell = currentRow.getCell(ruleIndex);
                Cell sourceCell = currentRow.getCell(sourceIndex);

                String targetPath = isEmpty(targetCell) ? null : trimPath(targetCell.getStringCellValue(), targetCaseFormat);
                String mappingRule = isEmpty(ruleCell) ? null : ruleCell.getStringCellValue().trim();

                String sourcePath = isEmpty(sourceCell) ? null : trimPath(sourceCell.getStringCellValue(), sourceCaseFormat);

                // check unknown mapping:
                UnknownMapping unknownMapping = checkUnknown(targetPath, mappingRule, sourcePath, base);
                if (unknownMapping.unknownType != null) {
                    base.annotateAsArrayElement(UNKNOWN_MAPPINGS, unknownMapping);
                }

                if (base.get(targetPath) != null && mappingRule != null) {
                    MappingNode node = base.get(targetPath);

                    Mapping mapping = new Mapping();
                    mapping.mappingRule = mappingRule;
                    mapping.sourcePath = sourcePath;

                    if (sourcePath != null && sourcePath.trim().length() > 0) {
                        annotateSourceSchema(sourceSchema, sourcePath, targetPath);
                    }

                    String destType = node.getNodeType().equals(XmlSchemaBase.NodeType.Attribute) ? "string" : node.getDataType();
                    String srcType = sourcePaths.get(sourcePath);
                    if (srcType != null && "string".equalsIgnoreCase(destType) && !checkTypeCompatible(srcType, destType)) {
                        UnknownMapping incompatible = new UnknownMapping();
                        incompatible.targetPath = node.getPath();
                        incompatible.sourcePath = sourcePath;
                        incompatible.unknownType = UnknownType.TYPE_INCOMPATIBLE;

                        base.annotateAsArrayElement(UNKNOWN_MAPPINGS, incompatible);
                    }

                    if (!ignored(node.getPath())) {
                        node.annotate(MAPPING, mapping);
                        markParent(node);
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
                    }

                    if (isLabelRow && cell != null && cell.getCellType().equals(CellType.STRING)) {
                        String label = cell.getStringCellValue();
                        if (label != null) {
                            switch (label) {
                                case "Target":
                                    targetIndex = i;
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

                start = targetIndex * ruleIndex * sourceIndex != 0;
            }
        }
    }

    private String trimPath(String path, CaseFormat caseFormat) {
        StringBuilder builder = new StringBuilder();
        String result = path.replaceAll("\\.", "/");
        String[] array = result.split("/");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                builder.append("/");
            }

            builder.append(extract(array[i].trim(), caseFormat));
        }

        return builder.toString();

    }

    private String extract(String s, CaseFormat caseFormat) {
        if (s == null || s.isEmpty()) {
            return "UNKNOWN";
        }

        String result = s;
        while (!match(result)) {
            if (result.length() == 0) {
                return "UNKNOWN";
            }
            result = result.substring(1);
        }

        if (CaseFormat.UPPER_CAMEL.equals(caseFormat)) {
            result = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, result);
        }

        return result;
    }

    private boolean match(String s) {
        char c = s.charAt(0);
        if (c == '_' || c == '@' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
            return true;
        }

        return false;
    }


    private void annotateSourceSchema(JsonObject sourceSchema, String sourcePath, String targetPath) {
        String token = sourcePath;
        if(sourcePathIndex.containsKey(token.toUpperCase())) {
            token = sourcePathIndex.get(token.toUpperCase());
        }

        JsonObject parent = sourceSchema;
        String path = token;
        int slash = path.indexOf('/');
        while (slash > 0) {
            String name = path.substring(0, slash);
            path = path.substring(slash + 1);
            slash = path.indexOf("/");
            if (parent.get(name) == null) {
                parent.add(name, new JsonObject());
            }

            parent = parent.get(name).getAsJsonObject();
        }

        if (parent.get(path) != null) {
            JsonArray array = new JsonArray();
            array.add(parent.get(path));
            array.add(new JsonPrimitive(targetPath));

            parent.remove(path);
            parent.add(path, array);

        } else {
            parent.add(path, new JsonPrimitive(targetPath));

        }
    }

    private UnknownMapping checkUnknown(String targetPath, String mappingRule, String sourcePath, XmlSchemaBase base) {
        UnknownMapping unknownMapping = new UnknownMapping();
        unknownMapping.targetPath = targetPath;
        unknownMapping.mappingRule = mappingRule;
        unknownMapping.sourcePath = sourcePath;

        if (ignored(targetPath) || mappingRule == null) {
            // no mapping, do nothing

        } else if (base.get(targetPath) == null) {
            unknownMapping.unknownType = UnknownType.UNKNOWN_TARGET_PATH;

        } else if (mappingRule != null && mappingRule.toUpperCase().contains("DIRECT") && mappingRule.toUpperCase().contains("MAPPING")
                && sourcePath != null && !sourcePaths.containsKey(sourcePath)) {
            if (sourcePath.contains(" ") || sourcePath.contains("\n")) {
                unknownMapping.unknownType = UnknownType.ILLEGAL_SOURCE_PATH;

            } else {
                unknownMapping.unknownType = UnknownType.UNKNOWN_SOURCE_PATH;

            }

        }

        return unknownMapping;
    }

    private boolean checkTypeCompatible(String sourceType, String destType) {
        /*if(sourceType.equalsIgnoreCase(destType) || destType.equalsIgnoreCase("string")) {
            return true;
        }*/

        return true;
    }

    private void extract(JsonObject jsonObject, String parent) {
        String prefix = parent == null ? "" : parent + "/";
        jsonObject.entrySet().forEach(e -> {
            String path = prefix + e.getKey();
            JsonElement element = e.getValue();
            if (element.isJsonArray()) {
                path = path + "[*]";
                sourcePaths.put(path, "array");

                JsonArray array = element.getAsJsonArray();
                String finalPath = path;
                array.forEach(c -> {
                    if (c.isJsonObject()) {
                        extract(c.getAsJsonObject(), finalPath);
                    }
                });

            } else if (element.isJsonObject()) {
                sourcePaths.put(path, "object");
                extract(element.getAsJsonObject(), path);

            } else if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    sourcePaths.put(path, "boolean");

                } else if (primitive.isNumber()) {
                    sourcePaths.put(path, "number");

                } else {
                    sourcePaths.put(path, "string");
                }
            }

        });
    }

    private boolean ignored(String xpath) {
        if (xpath == null) {
            return true;
        }

        String path = xpath;
        for (String prefix : ignores) {
            if (path.equals(prefix) || path.startsWith(prefix + "/")) {
                return true;
            }
        }

        return false;
    }

    private void markParent(MappingNode node) {
        MappingNode parent = node.getParent();
        while (parent != null) {
            parent.annotate(MAPPED, true);
            parent = parent.getParent();
        }
    }

    private boolean isEmpty(Cell cell) {
        return cell == null || cell.getStringCellValue() == null || cell.getStringCellValue().trim().length() == 0;
    }

    enum Label {
        Target,
        Mapping,
        Source;
    }
}
