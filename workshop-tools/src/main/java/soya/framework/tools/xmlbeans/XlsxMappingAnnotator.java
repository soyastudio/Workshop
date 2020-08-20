package soya.framework.tools.xmlbeans;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class XlsxMappingAnnotator implements Buffalo.Annotator<XmlSchemaBase> {

    private String url;
    private String sheet;
    private String extraction;

    @Override
    public void annotate(XmlSchemaBase base) {
        File excelFile = new File(url);
        Workbook workbook = null;
        LinkedHashSet<Mapper> mappers = new LinkedHashSet<>();

        try {
            workbook = new XSSFWorkbook(excelFile);
            boolean start = false;
            int[] labelIndex = new int[]{-1, -1, -1, -1, -1};

            Sheet mappingSheet = workbook.getSheet(sheet);
            Iterator<Row> sheetIterator = mappingSheet.iterator();

            while (sheetIterator.hasNext()) {
                Row currentRow = sheetIterator.next();
                if (!start) {
                    int first = currentRow.getFirstCellNum();
                    int last = currentRow.getLastCellNum();
                    Cell firstCell = currentRow.getCell(first);

                    if (firstCell.getCellType() == CellType.STRING && firstCell.getStringCellValue() != null && "#".equals(firstCell.getStringCellValue().trim())) {
                        start = true;
                        labelIndex[0] = first;

                        for (int i = first + 1; i <= last; i++) {
                            Cell cell = currentRow.getCell(i);

                            if (cell != null && cell.getCellType() == CellType.STRING && cell.getStringCellValue() != null && cell.getStringCellValue().trim().length() > 0) {
                                String label = cell.getStringCellValue().trim();

                                Label lab = Label.valueOf(label);
                                switch (lab) {
                                    case Target:
                                        labelIndex[1] = i;
                                        break;
                                    case Cardinality:
                                        labelIndex[2] = i;
                                        break;
                                    case Mapping:
                                        labelIndex[3] = i;
                                        break;
                                    case Source:
                                        labelIndex[4] = i;
                                        break;
                                }
                            }
                        }
                    }
                } else {
                    Cell c0 = currentRow.getCell(labelIndex[0]);
                    if (c0 != null) {
                        Cell target = currentRow.getCell(labelIndex[1]);
                        Cell cardinality = currentRow.getCell(labelIndex[2]);
                        Cell mapping = currentRow.getCell(labelIndex[3]);
                        Cell source = currentRow.getCell(labelIndex[4]);

                        if (target != null) {
                            String targetPath = target.getStringCellValue().trim();
                            if (base.getMappings().containsKey(targetPath)) {
                                XmlSchemaBase.MappingNode mappingNode = base.getMappings().get(targetPath);

                                if (cardinality != null && !cardinality.getStringCellValue().trim().endsWith("-1")) {
                                    mappingNode.annotateAsMappedElement("mapping", "cardinality", cardinality.getStringCellValue().trim());
                                }

                                if (mapping != null && mapping.getStringCellValue().trim().length() > 0) {

                                    String mappingDef = mapping.getStringCellValue().trim();
                                    mappingNode.annotateAsMappedElement("mapping", "description", mappingDef);
                                    if(source != null && source.getStringCellValue().trim().length() > 0) {
                                        mappingNode.annotateAsMappedElement("mapping", "sourcePath", source.getStringCellValue().trim());
                                    }

                                    Function func = createFunction(mappingDef, extraction, source);
                                    mappingNode.annotateAsMappedElement("mapping", "function", func.toString());
                                    if (func.getName().equals("jsonpath")) {
                                        Mapper mapper = new Mapper(func.getArgument(), targetPath);
                                        mappers.add(mapper);
                                    }
                                }
                            } else {
                                if (targetPath != null && targetPath.trim().length() > 0) {
                                    //System.out.println("============== Not found: " + target);
                                }
                            }

                        }

                    }
                }
            }
        } catch (InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }


        mappers.forEach(e -> {
            System.out.println(e.getSourcePath() + " --> " + e.getTargetPath());
        });
    }

    private Function createFunction(String def, String extraction, Cell cell) {
        Function func = Function.TODO;
        if (def.toLowerCase().startsWith("default to ")) {
            String value = def.substring("default to ".length()).trim();
            char[] arr = value.toCharArray();
            StringBuilder builder = new StringBuilder();
            boolean boo = false;
            for (char c : arr) {
                if (c == '\'') {
                    if (boo) {
                        break;
                    } else {
                        boo = true;
                    }

                } else {
                    builder.append(c);
                }
            }

            func = new Function("direct", builder.toString());

        } else if (def.toLowerCase().equals("direct mapping") || def.toLowerCase().equals("directmapping")) {
            if ("jsonpath".equals(extraction)) {
                String inputPath = toXPath(cell.getStringCellValue());
                func = new Function("jsonpath", inputPath);
            }
        }

        return func;
    }

    private String toXPath(String path) {
        String token = path;
        token = token.replaceAll("\\[\\*]", "/Item[*]");
        return token;

    }

    static enum Label {
        Target, Cardinality, Mapping, Source
    }

    static class Mapper {
        private String sourcePath;
        private String targetPath;

        public Mapper(String sourcePath, String targetPath) {
            this.sourcePath = sourcePath;
            this.targetPath = targetPath;
        }

        public String getSourcePath() {
            return sourcePath;
        }

        public String getTargetPath() {
            return targetPath;
        }
    }
}
