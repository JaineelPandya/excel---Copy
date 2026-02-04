package com.markytrics.excel;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDrawing;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Handles processing of DOCX, XLSX files
 */
public class FileProcessor {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final Pattern DOC_REF_NO_PATTERN = Pattern.compile("\\s*DOC\\.\\s*REF\\.\\s*NO\\.?\\s*:?\\s*", Pattern.CASE_INSENSITIVE);
    private static final Pattern VERSION_NO_PATTERN = Pattern.compile("\\s*VERSION\\s*NO\\.?\\s*:?\\s*", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_PATTERN = Pattern.compile("\\s*DATE\\s*:?\\s*", Pattern.CASE_INSENSITIVE);

    /**
     * Process DOCX file - update headers with document metadata
     *
     * @param inputPath Path to the input DOCX file
     * @param outputPath Path to save the output DOCX file
     * @param metadata Document metadata
     */
    public static void processDOCX(String inputPath, String outputPath, DocumentMetadata metadata) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputPath);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Update headers
            document.getHeaderList().forEach(header -> {
                for (XWPFParagraph para : header.getParagraphs()) {
                    replaceTextInParagraph(para, DOC_REF_NO_PATTERN, metadata.getDocumentCode());
                    replaceTextInParagraph(para, VERSION_NO_PATTERN, metadata.getVersionNumber());
                    replaceTextInParagraph(para, DATE_PATTERN, 
                        metadata.getRegistrationDate().format(DATE_FORMATTER));
                }
            });

            // Save the modified document
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }

            System.out.println("DOCX processed: " + outputPath);
        }
    }

    /**
     * Process XLSX file - update cells with document metadata and add QR code
     *
     * @param inputPath Path to the input XLSX file
     * @param outputPath Path to save the output XLSX file
     * @param metadata Document metadata
     * @param qrCodePath Path to QR code image (optional)
     */
    public static void processXLSX(String inputPath, String outputPath, DocumentMetadata metadata, String qrCodePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                Set<String> appliedPatterns = new HashSet<>();

                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellValue = getCellValue(cell);

                        if (DOC_REF_NO_PATTERN.matcher(cellValue).find() && !appliedPatterns.contains("doc_ref_no")) {
                            setCellToRight(sheet, cell, metadata.getDocumentCode());
                            appliedPatterns.add("doc_ref_no");

                            // Add QR code if provided
                            if (qrCodePath != null && !qrCodePath.isEmpty()) {
                                addQRCodeToCell(sheet, cell, qrCodePath);
                            }
                        } else if (VERSION_NO_PATTERN.matcher(cellValue).find() && appliedPatterns.contains("doc_ref_no")) {
                            setCellToRight(sheet, cell, metadata.getVersionNumber());
                            appliedPatterns.add("version_no");
                        } else if (DATE_PATTERN.matcher(cellValue).find() && !appliedPatterns.contains("date")) {
                            setCellToRight(sheet, cell, metadata.getRegistrationDate().format(DATE_FORMATTER));
                            appliedPatterns.add("date");
                        }
                    }
                }
            }

            // Save the modified workbook
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }

            System.out.println("XLSX processed: " + outputPath);
        }
    }

    /**
     * Get the string value of a cell
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    /**
     * Set value in the cell to the right of the current cell
     */
    private static void setCellToRight(Sheet sheet, Cell cell, String value) {
        Row row = cell.getRow();
        Cell targetCell = row.getCell(cell.getColumnIndex() + 1);
        if (targetCell == null) {
            targetCell = row.createCell(cell.getColumnIndex() + 1);
        }
        targetCell.setCellValue(value);
    }

    /**
     * Add QR code image to the cell to the right
     */
    private static void addQRCodeToCell(Sheet sheet, Cell cell, String qrCodePath) {
        try {
            int colIndex = cell.getColumnIndex() + 2;
            Row row = cell.getRow();
            
            // Read QR code image
            byte[] imageData = Files.readAllBytes(Paths.get(qrCodePath));
            int pictureIndex = sheet.getWorkbook().addPicture(imageData, Workbook.PICTURE_TYPE_PNG);

            // Get the drawing
            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            
            // Add picture to sheet
            drawing.createPicture(
                drawing.createAnchor(0, 0, 0, 0, colIndex, row.getRowNum(), colIndex + 2, row.getRowNum() + 5),
                pictureIndex
            );
        } catch (Exception e) {
            System.err.println("Failed to add QR code: " + e.getMessage());
        }
    }

    /**
     * Replace text in a paragraph matching a regex pattern
     */
    private static void replaceTextInParagraph(XWPFParagraph para, Pattern pattern, String replacement) {
        StringBuilder text = new StringBuilder();
        for (XWPFRun run : para.getRuns()) {
            text.append(run.getText(0));
        }

        if (pattern.matcher(text.toString()).find()) {
            // Clear existing runs
            para.getRuns().forEach(run -> {
                XWPFRun emptyRun = para.insertNewRun(0);
                emptyRun.setText("");
            });

            // Add new text
            XWPFRun newRun = para.createRun();
            newRun.setText(replacement);
        }
    }
}
