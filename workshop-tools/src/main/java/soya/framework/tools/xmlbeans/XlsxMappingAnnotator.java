package soya.framework.tools.xmlbeans;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    private String mappingFile;
    private String mappingSheet;
    private List<String> sourceSheet;

    private List<String> excludes;

    private transient int targetIndex;
    private transient int ruleIndex;
    private transient int sourceIndex;

    private Set<String> ignores = new HashSet<>();
    private Set<String> sourcePaths = new LinkedHashSet<>();

    public XlsxMappingAnnotator() {
    }

    public void annotate(XmlSchemaBase base) {

        if (sourceFiles != null) {
            sourceFiles.forEach(sf -> {
                try {
                    FileReader reader = new FileReader(new File(sf));
                    JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                    extract(root, null);
                    base.annotate(SOURCE_PATHS, sourcePaths);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        if (excludes != null) {
            ignores.addAll(excludes);
        }

        File excelFile = new File(this.mappingFile);
        XSSFWorkbook workbook = null;

        try {
            workbook = new XSSFWorkbook(excelFile);

            if (sourceSheet != null) {
                for (String source : sourceSheet) {
                    Sheet s = workbook.getSheet(source);
                    loadSourcePaths(base, s);
                }
            }

            Sheet mappingSheet = workbook.getSheet(this.mappingSheet);
            loadMappings(base, mappingSheet);

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
                    if (!isEmpty(cell)&& cell.getCellType().equals(CellType.STRING)) {
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


        System.out.println(json);


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


                String targetPath = isEmpty(targetCell) ? null : targetCell.getStringCellValue().trim();
                String mappingRule = isEmpty(ruleCell) ? null : ruleCell.getStringCellValue().trim();
                String sourcePath = isEmpty(sourceCell) ? null : sourceCell.getStringCellValue().trim();

                if (sourcePath != null && !sourcePaths.contains(sourcePath)) {
                    UnknownMapping unknownMapping = new UnknownMapping();
                    unknownMapping.unknownType = UnknownType.UNKNOWN_SOURCE_PATH;
                    unknownMapping.targetPath = targetPath;
                    unknownMapping.sourcePath = sourcePath;
                    unknownMapping.mappingRule = mappingRule;
                    base.annotateAsArrayElement(UNKNOWN_MAPPINGS, unknownMapping);

                }

                if (targetPath != null && mappingRule != null && !ignores.contains(targetPath)) {
                    Mapping mapping = new Mapping();
                    mapping.mappingRule = mappingRule;
                    mapping.sourcePath = sourcePath;

                    MappingNode node = base.get(targetPath);
                    if (node != null && !ignored(node)) {
                        if (node.getAnnotation(MAPPING) == null) {
                            node.annotate(MAPPING, mapping);
                            markParent(node);

                        }
                    } else {
                        UnknownMapping unknownMapping = new UnknownMapping();
                        unknownMapping.unknownType = UnknownType.UNKNOWN_TARGET_PATH;
                        unknownMapping.targetPath = targetPath;
                        unknownMapping.sourcePath = sourcePath;
                        unknownMapping.mappingRule = mappingRule;
                        base.annotateAsArrayElement(UNKNOWN_MAPPINGS, unknownMapping);
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

    private void extract(JsonObject jsonObject, String parent) {
        String prefix = parent == null ? "" : parent + "/";
        jsonObject.entrySet().forEach(e -> {
            String path = prefix + e.getKey();
            JsonElement element = e.getValue();
            if (element.isJsonArray()) {
                path = path + "[*]";
                sourcePaths.add(path);

                JsonArray array = element.getAsJsonArray();
                String finalPath = path;
                array.forEach(c -> {
                    if (c.isJsonObject()) {
                        extract(c.getAsJsonObject(), finalPath);
                    }
                });

            } else {
                sourcePaths.add(path);
                if (element.isJsonObject()) {
                    extract(element.getAsJsonObject(), path);
                }

            }

        });
    }

    private boolean ignored(MappingNode node) {
        String path = node.getPath();
        for (String prefix : ignores) {
            if (path.startsWith(prefix + "/")) {
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
