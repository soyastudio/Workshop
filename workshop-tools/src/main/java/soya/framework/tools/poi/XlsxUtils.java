package soya.framework.tools.poi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class XlsxUtils {

    public static JsonObject getExcelDataAsJsonObject(File excelFile) throws Exception {
        JsonObject sheetsJsonObject = new JsonObject();
        Workbook workbook = null;

        try {
            workbook = new XSSFWorkbook(excelFile);
        } catch (InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

            JsonArray sheetArray = new JsonArray();
            ArrayList<String> columnNames = new ArrayList<String>();
            Sheet sheet = workbook.getSheetAt(i);
            Iterator<Row> sheetIterator = sheet.iterator();

            while (sheetIterator.hasNext()) {

                Row currentRow = sheetIterator.next();
                JsonObject jsonObject = new JsonObject();

                if (currentRow.getRowNum() != 0) {

                    for (int j = 0; j < columnNames.size(); j++) {

                        if (currentRow.getCell(j) != null) {
                            if (currentRow.getCell(j).getCellType() == CellType.STRING) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getStringCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.NUMERIC) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getNumericCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.BOOLEAN) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getBooleanCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.BLANK) {
                                jsonObject.addProperty(columnNames.get(j), "");
                            }
                        } else {
                            jsonObject.addProperty(columnNames.get(j), "");
                        }

                    }

                    sheetArray.add(jsonObject);

                } else {
                    // store column names
                    for (int k = 0; k < currentRow.getPhysicalNumberOfCells(); k++) {
                        if (currentRow.getCell(k) != null) {
                            columnNames.add(currentRow.getCell(k).getStringCellValue());
                        }
                    }
                }

            }

            sheetsJsonObject.add(workbook.getSheetName(i), sheetArray);

        }

        return sheetsJsonObject;

    }

    public static JsonObject getExcelDataAsJsonObject(InputStream inputStream) throws Exception {
        JsonObject sheetsJsonObject = new JsonObject();
        Workbook workbook = null;

        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

            JsonArray sheetArray = new JsonArray();
            ArrayList<String> columnNames = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(i);
            Iterator<Row> sheetIterator = sheet.iterator();

            while (sheetIterator.hasNext()) {

                Row currentRow = sheetIterator.next();
                JsonObject jsonObject = new JsonObject();

                if (currentRow.getRowNum() != 0) {

                    for (int j = 0; j < columnNames.size(); j++) {

                        if (currentRow.getCell(j) != null) {
                            if (currentRow.getCell(j).getCellType() == CellType.STRING) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getStringCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.NUMERIC) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getNumericCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.BOOLEAN) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getBooleanCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.BLANK) {
                                jsonObject.addProperty(columnNames.get(j), "");
                            }
                        } else {
                            jsonObject.addProperty(columnNames.get(j), "");
                        }

                    }

                    sheetArray.add(jsonObject);

                } else {
                    // store column names
                    for (int k = 0; k < currentRow.getPhysicalNumberOfCells(); k++) {
                        columnNames.add(currentRow.getCell(k).getStringCellValue());
                    }
                }

            }

            sheetsJsonObject.add(workbook.getSheetName(i), sheetArray);

        }

        return sheetsJsonObject;
    }

    public static JsonArray fromWorksheet(File excelFile, String worksheet) throws Exception {
        return fromWorksheet(excelFile, worksheet, false);
    }

    public static JsonArray fromWorksheet(InputStream inputStream, String worksheet) throws Exception {
        return fromWorksheet(new XSSFWorkbook(inputStream), worksheet, false);
    }

    public static JsonArray fromWorksheet(File excelFile, String worksheet, boolean includeEmptyRow) throws Exception {
        return fromWorksheet(new XSSFWorkbook(excelFile), worksheet, includeEmptyRow);
    }

    public static String toString(File excelFile, String worksheet) throws Exception {
        StringBuilder builder = new StringBuilder();
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheet(worksheet);
        Iterator<Row> sheetIterator = sheet.iterator();
        while (sheetIterator.hasNext()) {
            Row currentRow = sheetIterator.next();
            Iterator<Cell> cellIterator = currentRow.cellIterator();
            while(cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if(cell.getCellType().equals(CellType.STRING)) {
                    builder.append(cell.getStringCellValue());
                }
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    private static JsonArray fromWorksheet(Workbook workbook, String worksheet, boolean includeEmptyRow) throws Exception {

        JsonArray sheetArray = new JsonArray();
        ArrayList<String> columnNames = new ArrayList<>();
        Sheet sheet = workbook.getSheet(worksheet);
        Iterator<Row> sheetIterator = sheet.iterator();

        while (sheetIterator.hasNext()) {
            Row currentRow = sheetIterator.next();
            if (!includeEmptyRow && !isEmpty(currentRow)) {
                JsonObject jsonObject = new JsonObject();

                if (currentRow.getRowNum() != 0) {
                    for (int j = 0; j < columnNames.size(); j++) {
                        if (currentRow.getCell(j) != null) {
                            if (currentRow.getCell(j).getCellType() == CellType.STRING) {
                                String stringCellValue = currentRow.getCell(j).getStringCellValue();
                                if (stringCellValue != null) {
                                    stringCellValue = stringCellValue.trim();
                                }
                                jsonObject.addProperty(columnNames.get(j), stringCellValue);
                            } else if (currentRow.getCell(j).getCellType() == CellType.NUMERIC) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getNumericCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.BOOLEAN) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getBooleanCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.BLANK) {
                                jsonObject.addProperty(columnNames.get(j), "");
                            }
                        } else {
                            jsonObject.addProperty(columnNames.get(j), "");
                        }
                    }

                    sheetArray.add(jsonObject);

                } else {
                    for (int k = 0; k < currentRow.getPhysicalNumberOfCells(); k++) {
                        columnNames.add(currentRow.getCell(k).getStringCellValue());
                    }
                }
            }
        }

        return sheetArray;
    }

    private static JsonArray fromWorkbookSmart(Workbook workbook) throws Exception {

        JsonArray sheetArray = new JsonArray();
        ArrayList<String> columnNames = new ArrayList<>();
        Sheet sheet = findWorksheet(workbook);
        Iterator<Row> sheetIterator = sheet.iterator();

        boolean start = false;
        while (sheetIterator.hasNext()) {
            Row currentRow = sheetIterator.next();
            if (!start) {
                start = isTitleRow(currentRow);
                if (start) {
                    // store column names
                    for (int k = 0; k < currentRow.getPhysicalNumberOfCells(); k++) {
                        try {
                            String column = currentRow.getCell(k).getStringCellValue().trim();
                            if (column.length() == 0) {
                                columnNames.add("CLN_" + k);
                            } else {
                                columnNames.add(column);
                            }
                        } catch (Exception e) {
                            columnNames.add("CLN_" + k);
                        }
                    }
                }
            }

            if (start && !isEmpty(currentRow)) {
                JsonObject jsonObject = new JsonObject();

                if (currentRow.getRowNum() != 0) {
                    for (int j = 0; j < columnNames.size(); j++) {

                        if (currentRow.getCell(j) != null) {
                            if (currentRow.getCell(j).getCellType() == CellType.STRING) {
                                String stringCellValue = currentRow.getCell(j).getStringCellValue();
                                if (stringCellValue != null) {
                                    stringCellValue = stringCellValue.trim();
                                }
                                jsonObject.addProperty(columnNames.get(j), stringCellValue);
                            } else if (currentRow.getCell(j).getCellType() == CellType.NUMERIC) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getNumericCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.BOOLEAN) {
                                jsonObject.addProperty(columnNames.get(j), currentRow.getCell(j).getBooleanCellValue());
                            } else if (currentRow.getCell(j).getCellType() == CellType.BLANK) {
                                jsonObject.addProperty(columnNames.get(j), "");
                            }
                        } else {
                            if (!columnNames.get(j).startsWith("CLN_")) {
                                jsonObject.addProperty(columnNames.get(j), "");

                            }
                        }

                    }

                    sheetArray.add(jsonObject);

                }
            }
        }

        return sheetArray;
    }

    private static Sheet findWorksheet(Workbook workbook) {
        Iterator<Sheet> iterator = workbook.sheetIterator();
        while (iterator.hasNext()) {
            Sheet sheet = iterator.next();
            if (sheet.getSheetName().toLowerCase().startsWith("mapping ") && sheet.getSheetName().toLowerCase().contains(" to ")) {
                return sheet;
            }
        }
        return null;
    }

    private static boolean isTitleRow(Row row) {
        for (int i = 0; i < row.getRowNum(); i++) {
            String v = row.getCell(i).getStringCellValue();
            if (v != null) {
                if (v.equalsIgnoreCase("level")) {
                    return true;

                } else if (v.toLowerCase().contains("xpath")) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isEmpty(Row row) {
        Iterator<Cell> iterator = row.cellIterator();
        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            if (!cell.getCellType().equals(CellType._NONE) && !cell.getCellType().equals(CellType.BLANK)) {
                return false;
            }
        }

        return true;
    }
}
