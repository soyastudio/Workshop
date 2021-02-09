package soya.framework.tao.edis;

import com.google.common.base.CaseFormat;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import soya.framework.tao.Barflow;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tools.xmlbeans.WorkshopRepository;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class XLSXMappingAnnotator extends EdisAnnotator {

    private String mappingFile;
    private String mappingSheet;

    private CaseFormat targetCaseFormat = CaseFormat.UPPER_CAMEL;
    private CaseFormat sourceCaseFormat = CaseFormat.LOWER_CAMEL;

    private transient int targetIndex;
    private transient int ruleIndex;
    private transient int sourceIndex;

    @Override
    public void annotate(XsdTreeBase baseline) throws Barflow.FlowExecutionException {
        File excelFile = WorkshopRepository.getFile(mappingFile);
        XSSFWorkbook workbook = null;

        try {
            workbook = new XSSFWorkbook(excelFile);
            Sheet mappingSheet = workbook.getSheet(this.mappingSheet);
            loadMappings(baseline, mappingSheet);

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();

        } finally {
            try {
                workbook.close();
            } catch (IOException e) {

            }
        }
    }

    private void loadMappings(XsdTreeBase baseline, Sheet mappingSheet) {
        KnowledgeTree<XsdTreeBase.XsNode> knowledgeTree = baseline.knowledgeBase();

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


                if (knowledgeTree.contains(targetPath) && mappingRule != null) {
                    KnowledgeTreeNode<XsdTreeBase.XsNode> node = knowledgeTree.get(targetPath);

                    EdisTask.Mapping mapping = new EdisTask.Mapping();
                    mapping.rule = mappingRule;
                    mapping.source = sourcePath;
                    if (sourcePath != null && sourcePath.trim().length() > 0) {
                        node.annotate(namespace, mapping);

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

    private boolean isEmpty(Cell cell) {
        return cell == null || cell.getStringCellValue() == null || cell.getStringCellValue().trim().length() == 0;
    }

}
