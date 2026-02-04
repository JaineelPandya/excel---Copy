package com.markytrics.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Handles Excel file operations including reading templates and writing values.
 */
public class ExcelProcessor {

    private static final String TEMPLATE_FILE = "template/Remote_Access_and_PAM_Form(1).xlsx";
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    /**
     * Generates an Excel file by copying the template and filling in values.
     *
     * @param docRef     Document reference number
     * @param version    Version number
     * @param dateStr    Date string in DD-MMM-YYYY format
     * @throws IOException if file operations fail
     */
    public static void generateExcel(String docRef, String version, String dateStr) throws IOException {
        // Create output file path
        String timestamp = LocalDateTime.now().format(OUTPUT_FORMATTER);
        String outputFile = "output/Remote_Access_Request_" + timestamp + ".xlsx";

        // Ensure output directory exists
        Files.createDirectories(Paths.get("output"));

        // Copy template to output file
        Files.copy(Paths.get(TEMPLATE_FILE), Paths.get(outputFile));

        // Load workbook
        try (FileInputStream fis = new FileInputStream(outputFile);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet ws = wb.getSheetAt(0);

            // Write values
            writeValueNextToLabel(ws, "DOC. REF. NO.", docRef, null);
            writeValueNextToLabel(ws, "VERSION NO.", version, null);
            writeValueNextToLabel(ws, "DATE:", dateStr, "dd-mmm-yyyy");

            // Save workbook
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                wb.write(fos);
            }
        }

        System.out.println("Excel file generated: " + outputFile);
    }

    /**
     * Writes a value next to a label cell in the worksheet.
     *
     * @param ws         The worksheet
     * @param label      The label text to search for
     * @param value      The value to write
     * @param dateFormat The date format to apply (if applicable)
     */
    private static void writeValueNextToLabel(Sheet ws, String label, String value, String dateFormat) {
        for (Row row : ws) {
            for (Cell cell : row) {
                if (cell.getStringCellValue() != null &&
                    cell.getStringCellValue().trim().equals(label)) {

                    // Get the cell to the right of the label
                    Cell targetCell = row.getCell(cell.getColumnIndex() + 1);
                    if (targetCell == null) {
                        targetCell = row.createCell(cell.getColumnIndex() + 1);
                    }

                    // Check if target cell is in a merged region
                    boolean foundMerged = false;
                    for (CellRangeAddress merged : ws.getMergedRegions()) {
                        if (merged.isInRange(targetCell)) {
                            // Write to the top-left cell of the merged region
                            Cell topLeft = ws.getRow(merged.getFirstRow())
                                    .getCell(merged.getFirstColumn());
                            topLeft.setCellValue(value);
                            if (dateFormat != null) {
                                CellStyle style = ws.getWorkbook().createCellStyle();
                                style.setDataFormat(ws.getWorkbook().createDataFormat()
                                        .getFormat(dateFormat));
                                topLeft.setCellStyle(style);
                            }
                            foundMerged = true;
                            break;
                        }
                    }

                    if (!foundMerged) {
                        targetCell.setCellValue(value);
                        if (dateFormat != null) {
                            CellStyle style = ws.getWorkbook().createCellStyle();
                            style.setDataFormat(ws.getWorkbook().createDataFormat()
                                    .getFormat(dateFormat));
                            targetCell.setCellStyle(style);
                        }
                    }
                    return;
                }
            }
        }
    }
}
