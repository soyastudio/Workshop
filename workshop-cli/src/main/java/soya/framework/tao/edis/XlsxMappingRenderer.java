package soya.framework.tao.edis;

import com.google.common.base.CaseFormat;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tao.T123W;
import soya.framework.tao.TreeNode;
import soya.framework.tao.xs.XsNode;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class XlsxMappingRenderer extends EdisRenderer {
    private File mappingFile;
    private String mappingSheet;

    private CaseFormat targetCaseFormat = CaseFormat.UPPER_CAMEL;
    private CaseFormat sourceCaseFormat = CaseFormat.LOWER_CAMEL;

    private transient int targetIndex;
    private transient int ruleIndex;
    private transient int sourceIndex;

    private Set<String> aliasSet = new HashSet<>();

    public XlsxMappingRenderer mappingFile(File mappingFile) {
        this.mappingFile = mappingFile;
        return this;
    }

    public XlsxMappingRenderer mappingSheet(String mappingSheet) {
        this.mappingSheet = mappingSheet;
        return this;
    }

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledge) throws T123W.FlowExecutionException {
        XSSFWorkbook workbook = null;

        if(mappingFile == null) {
            throw new IllegalStateException("Mapping file is not set.");
        }

        if(!mappingFile.exists()) {
            throw new IllegalStateException("Can not find mapping file: " + mappingFile.toString());
        }

        try {
            workbook = new XSSFWorkbook(mappingFile);
            Sheet mappingSheet = workbook.getSheet(this.mappingSheet);
            loadMappings(knowledge, mappingSheet);

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();

        } finally {
            try {
                workbook.close();
            } catch (IOException e) {

            }
        }

        StringBuilder builder = new StringBuilder();
        knowledge.paths().forEachRemaining(e -> {
            builder.append(e).append("=");

            KnowledgeTreeNode<XsNode> node = knowledge.get(e);
            String value = getAssignment(node);
            if(value != null) {
                builder.append(value);
            }

            builder.append("\n");
        });

        return builder.toString();
    }

    private void loadMappings(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree, Sheet mappingSheet) {
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

                if (!knowledgeTree.contains(targetPath) && mappingRule != null) {
                    System.out.println("XPath " + targetPath + " does not exist in XSD.");

                } else if (mappingRule != null) {
                    KnowledgeTreeNode<XsNode> node = knowledgeTree.get(targetPath);

                    Assignment assignment = new Assignment();
                    assignment.rule = mappingRule;
                    assignment.source = sourcePath;

                    if (assignment.rule != null || sourcePath != null && sourcePath.trim().length() > 0) {
                        node.annotate(NAMESPACE_ASSIGNMENT, assignment);
                    }

                    KnowledgeTreeNode<XsNode> parent = (KnowledgeTreeNode<XsNode>) node.getParent();

                    while (parent != null && parent.getAnnotation(NAMESPACE_CONSTRUCTION) == null) {

                        Construction construction = new Construction();
                        construction.setAlias(getAlias(parent.getName()));
                        construction.setLevel(getLevel(parent));
                        if(!BigInteger.ONE.equals(parent.origin().getMaxOccurs())) {
                            construction.setArray(true);
                        }
                        parent.annotate(NAMESPACE_CONSTRUCTION, construction);

                        parent = (KnowledgeTreeNode<XsNode>) parent.getParent();
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

    private String getAlias(String baseName) {
        String token = baseName + "_";
        int count = 0;
        while (aliasSet.contains(token)) {
            count ++;
            token = baseName + count + "_";
        }
        aliasSet.add(token);
        return token;
    }

    private int getLevel(TreeNode node) {
        int level = 0;
        TreeNode parent = node;

        while (parent.getParent() != null) {
            level++;
            parent = parent.getParent();
        }

        return level;
    }

    private  String getAssignment(KnowledgeTreeNode<XsNode> node) {
        if (XsNode.XsNodeType.Folder.equals(node.origin().getNodeType()) && node.getAnnotation(NAMESPACE_CONSTRUCTION) != null) {
            Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);

            return construction.toString();

        } else {
            Assignment assignment = node.getAnnotation(NAMESPACE_ASSIGNMENT, Assignment.class);
            return assignment == null? "":assignment.toString();
        }
    }

    private String getDefaultValue(String rule) {
        try {
            return rule.substring(rule.indexOf("'"), rule.lastIndexOf("'") + 1);

        } catch (Exception e) {
            return null;
        }
    }

}
