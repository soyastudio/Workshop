package soya.framework.tools.xmlbeans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import soya.framework.tools.xmlbeans.Buffalo.Annotator;
import soya.framework.tools.xmlbeans.XmlSchemaBase.MappingNode;
import soya.framework.tools.xmlbeans.XmlSchemaBase.NodeType;

public class XlsxMappingAnnotator implements Annotator<XmlSchemaBase>, MappingFeature {
    private String url;
    private String sheet;
    private List<?> targetFix;
    private List<?> sourceFix;

    public XlsxMappingAnnotator() {
    }

    public void annotate(XmlSchemaBase base) {
        Map<String, String> targetFixMap = new HashMap();
        Map<String, String> sourceFixMap = new HashMap();
        if (this.targetFix != null) {
            this.targetFix.forEach((e) -> {
                Map<String, String> t = (Map)e;
                targetFixMap.putAll(t);
            });
        }

        if (this.sourceFix != null) {
            this.sourceFix.forEach((e) -> {
                Map<String, String> s = (Map)e;
                sourceFixMap.putAll(s);
            });
        }

        File excelFile = new File(this.url);
        XSSFWorkbook workbook = null;

        try {
            workbook = new XSSFWorkbook(excelFile);
            boolean start = false;
            int[] labelIndex = new int[]{-1, -1, -1, -1, -1, -1};
            Sheet mappingSheet = workbook.getSheet(this.sheet);
            Iterator sheetIterator = mappingSheet.iterator();

            while(true) {
                Row currentRow;
                Cell dataType;
                Cell mapping;
                short first;
                short last;
                do {
                    do {
                        label96:
                        do {
                            while(sheetIterator.hasNext()) {
                                currentRow = (Row)sheetIterator.next();
                                if (!start) {
                                    first = currentRow.getFirstCellNum();
                                    last = currentRow.getLastCellNum();
                                    dataType = currentRow.getCell(first);
                                    continue label96;
                                }

                                Cell c0 = currentRow.getCell(labelIndex[0]);
                                if (c0 != null) {
                                    Cell target = currentRow.getCell(labelIndex[1]);
                                    dataType = currentRow.getCell(labelIndex[2]);
                                    Cell cardinality = currentRow.getCell(labelIndex[3]);
                                    mapping = currentRow.getCell(labelIndex[4]);
                                    Cell source = currentRow.getCell(labelIndex[5]);
                                    if (target != null) {
                                        String targetPath = target.getStringCellValue().trim();
                                        String fixedTargetPath = targetPath;
                                        if (targetFixMap.containsKey(targetPath)) {
                                            fixedTargetPath = (String)targetFixMap.get(targetPath);
                                        }

                                        if (base.getMappings().containsKey(fixedTargetPath)) {
                                            MappingNode mappingNode = (MappingNode)base.getMappings().get(fixedTargetPath);
                                            if (cardinality != null && !cardinality.getStringCellValue().trim().endsWith("-1")) {
                                                mappingNode.annotateAsMappedElement("mapping", "cardinality", cardinality.getStringCellValue().trim());
                                            }

                                            String sourcePath;
                                            if (mapping != null && mapping.getStringCellValue().trim().length() > 0) {
                                                sourcePath = mapping.getStringCellValue().trim();
                                                mappingNode.annotateAsMappedElement("mapping", "mappingRule", sourcePath);
                                                if (!mappingNode.getNodeType().equals(NodeType.Folder) && dataType != null && dataType.getStringCellValue().trim().length() > 0) {
                                                    mappingNode.annotateAsMappedElement("mapping", "dataType", dataType.getStringCellValue().trim());
                                                }
                                            }

                                            if (source != null && source.getStringCellValue().trim().length() > 0) {
                                                sourcePath = source.getStringCellValue().trim();
                                                if (sourceFixMap.containsKey(sourcePath)) {
                                                    sourcePath = (String)sourceFixMap.get(sourcePath);
                                                }

                                                mappingNode.annotateAsMappedElement("mapping", "sourcePath", sourcePath);
                                            }
                                        } else if (targetPath != null && targetPath.trim().length() > 0) {
                                            base.annotateAsArrayElement("UNFOUND_TARGET_PATH", targetPath);
                                        }
                                    }
                                }
                            }

                            return;
                        } while(dataType.getCellType() != CellType.STRING);
                    } while(dataType.getStringCellValue() == null);
                } while(!"#".equals(dataType.getStringCellValue().trim()));

                start = true;
                labelIndex[0] = first;

                for(int i = first + 1; i <= last; ++i) {
                    mapping = currentRow.getCell(i);
                    if (mapping != null && mapping.getCellType() == CellType.STRING && mapping.getStringCellValue() != null && mapping.getStringCellValue().trim().length() > 0) {
                        String label = mapping.getStringCellValue().trim();
                        XlsxMappingAnnotator.Label lab = XlsxMappingAnnotator.Label.valueOf(label);
                        switch(lab) {
                            case Target:
                                labelIndex[1] = i;
                                break;
                            case DataType:
                                labelIndex[2] = i;
                                break;
                            case Cardinality:
                                labelIndex[3] = i;
                                break;
                            case Mapping:
                                labelIndex[4] = i;
                                break;
                            case Source:
                                labelIndex[5] = i;
                        }
                    }
                }
            }
        } catch (IOException | InvalidFormatException var21) {
            throw new RuntimeException(var21);
        }
    }

    private Function createFunction(String def, String extraction, Cell cell) {
        Function func = Function.TODO;
        String inputPath;
        if (def.toLowerCase().startsWith("default to ")) {
            inputPath = def.substring("default to ".length()).trim();
            char[] arr = inputPath.toCharArray();
            StringBuilder builder = new StringBuilder();
            boolean boo = false;
            char[] var9 = arr;
            int var10 = arr.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                char c = var9[var11];
                if (c == '\'') {
                    if (boo) {
                        break;
                    }

                    boo = true;
                } else {
                    builder.append(c);
                }
            }

            func = new Function("direct", builder.toString());
        } else if ((def.toLowerCase().equals("direct mapping") || def.toLowerCase().equals("directmapping")) && "jsonpath".equals(extraction)) {
            inputPath = this.toXPath(cell.getStringCellValue());
            func = new Function("jsonpath", inputPath);
        }

        return func;
    }

    private String toXPath(String path) {
        String token = path.replaceAll("\\[\\*]", "/Item[*]");
        return token;
    }

    static enum Label {
        Target,
        DataType,
        Cardinality,
        Mapping,
        Source;

        private Label() {
        }
    }
}
