package soya.framework.tools.xmlbeans;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import soya.framework.tools.xmlbeans.Buffalo.Annotator;
import soya.framework.tools.xmlbeans.XmlSchemaBase.MappingNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class XlsxMappingAnnotator implements Annotator<XmlSchemaBase>, MappingFeature {

    private String url;
    private String sheet;
    private List<String> excludes;
    private List<?> targetFix;
    private List<?> sourceFix;

    private transient int targetIndex;
    private transient int ruleIndex;
    private transient int sourceIndex;

    private transient Set<String> ignores = new HashSet<>();

    public XlsxMappingAnnotator() {
    }

    public void annotate(XmlSchemaBase base) {

        Map<String, String> targetFixMap = new HashMap();
        Map<String, String> sourceFixMap = new HashMap();
        if(excludes != null) {
            ignores.addAll(excludes);
        }

        if (this.targetFix != null) {
            this.targetFix.forEach((e) -> {
                Map<String, String> t = (Map) e;
                targetFixMap.putAll(t);
            });
        }

        if (this.sourceFix != null) {
            this.sourceFix.forEach((e) -> {
                Map<String, String> s = (Map) e;
                sourceFixMap.putAll(s);
            });
        }

        File excelFile = new File(this.url);
        XSSFWorkbook workbook = null;

        try {
            workbook = new XSSFWorkbook(excelFile);
            boolean start = false;

            Sheet mappingSheet = workbook.getSheet(this.sheet);
            Iterator<Row> sheetIterator = mappingSheet.iterator();

            while (sheetIterator.hasNext()) {
                Row currentRow = sheetIterator.next();

                if (start) {
                    Cell targetCell = currentRow.getCell(targetIndex);
                    Cell ruleCell = currentRow.getCell(ruleIndex);
                    Cell sourceCell = currentRow.getCell(sourceIndex);


                    String targetPath = isEmpty(targetCell) ? null : targetCell.getStringCellValue().trim();
                    if (targetFixMap.containsKey(targetPath)) {
                        targetPath = targetFixMap.get(targetPath);
                    }

                    String mappingRule = isEmpty(ruleCell) ? null : ruleCell.getStringCellValue().trim();

                    String sourcePath = isEmpty(sourceCell) ? null : sourceCell.getStringCellValue().trim();
                    if (sourceFixMap.containsKey(sourcePath)) {
                        sourcePath = sourceFixMap.get(sourcePath);
                    }

                    if (targetPath != null && mappingRule != null) {
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
                            base.annotateAsArrayElement("UNKNOWN_TARGET_PATH", targetPath);
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
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    private boolean ignored(MappingNode node) {
        String path = node.getPath();
        for(String prefix: ignores) {
            if(path.startsWith(prefix)) {
                System.out.println("===================== ignored: " + node.getPath());
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
