package com.markytrics.excel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;


/**
 * Simulates the Django openpdfversion view function in Java
 * Handles document processing, PDF generation, and file delivery
 */
public class OpenPdfVersion {
    private String mediaDir = "media";
    private String baseDir = ".";
    private String tempDir = "temp";

    /**
     * Main method that processes the document and returns the PDF
     *
     * @param filePath The file path (from request)
     * @param docId The document ID (from request)
     * @param noWatermark Whether to disable watermark (from request)
     * @param metadata Document metadata from database
     * @return Path to the generated PDF file
     */
    public String processPdfVersion(String filePath, String docId, String noWatermark, DocumentMetadata metadata) throws Exception {
        System.out.println("Processing document: " + filePath);

        // Validate access and set watermark flag
        boolean shouldAddWatermark = !"true".equalsIgnoreCase(noWatermark);

        // Create temp directory if it doesn't exist
        Files.createDirectories(Paths.get(tempDir));

        // Generate unique timestamp for files
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String pdfFileName;

        // Determine file type and create output filename
        if (filePath.endsWith(".docx")) {
            pdfFileName = filePath.replace(".docx", "converted" + timeStamp + ".pdf");
        } else if (filePath.endsWith(".xlsx")) {
            pdfFileName = filePath.replace(".xlsx", "converted" + timeStamp + ".pdf");
        } else if (filePath.endsWith(".pdf")) {
            pdfFileName = filePath.replace(".pdf", "converted" + timeStamp + ".pdf");
        } else {
            // For unsupported files, return the original file
            return new File(mediaDir, filePath).getAbsolutePath();
        }

        String pdfFilePath = new File(tempDir, pdfFileName).getAbsolutePath();

        // Remove existing file if present
        if (Files.exists(Paths.get(pdfFilePath))) {
            Files.delete(Paths.get(pdfFilePath));
        }

        String qrCodePath = null;
        String convertedFilePath = null;

        try {
            // Process based on file type
            if (filePath.endsWith(".docx")) {
                processDocxFile(filePath, metadata, timeStamp);
                convertedFilePath = filePath.replace(".docx", "converted" + timeStamp + ".docx");
                PDFProcessor.convertDocToPDF(new File(tempDir, convertedFilePath).getAbsolutePath());
                pdfFilePath = new File(tempDir, convertedFilePath.replace(".docx", ".pdf")).getAbsolutePath();

            } else if (filePath.endsWith(".xlsx")) {
                processXlsxFile(filePath, metadata, timeStamp);
                convertedFilePath = filePath.replace(".xlsx", "converted" + timeStamp + ".xlsx");
                qrCodePath = QRCodeGenerator.generateQRCode("http://example.com/doc/" + docId, tempDir);
                PDFProcessor.convertExcelToPDF(new File(tempDir, convertedFilePath).getAbsolutePath());
                pdfFilePath = new File(tempDir, convertedFilePath.replace(".xlsx", ".pdf")).getAbsolutePath();

            } else if (filePath.endsWith(".pdf")) {
                String sourceFile = new File(mediaDir, filePath).getAbsolutePath();
                PDFProcessor.copyPDF(sourceFile, pdfFilePath);
            }

            // Generate document summary Excel if needed
            String excelSummaryPath = null;
            if (metadata.getSubDocumentType() == null) {
                excelSummaryPath = generateExcelSummary(metadata, timeStamp);
                if (excelSummaryPath != null) {
                    PDFProcessor.convertExcelToPDF(excelSummaryPath);
                }
            }

            // Add footer and watermark
            PDFProcessor.addFooterAndWatermark(pdfFilePath, LocalDateTime.now(), shouldAddWatermark);

            // Add QR code if available
            if (qrCodePath != null) {
                PDFProcessor.addImageToPDF(pdfFilePath, qrCodePath, 1, 50, 750);
            }

            return pdfFilePath;

        } finally {
            // Cleanup temporary files
            cleanupFiles(qrCodePath, convertedFilePath, timeStamp);
        }
    }

    /**
     * Process DOCX file
     */
    private void processDocxFile(String filePath, DocumentMetadata metadata, String timeStamp) throws Exception {
        String sourcePath = new File(mediaDir, filePath).getAbsolutePath();
        String outputPath = new File(tempDir, filePath.replace(".docx", "converted" + timeStamp + ".docx")).getAbsolutePath();

        FileProcessor.processDOCX(sourcePath, outputPath, metadata);
    }

    /**
     * Process XLSX file
     */
    private void processXlsxFile(String filePath, DocumentMetadata metadata, String timeStamp) throws Exception {
        String sourcePath = new File(mediaDir, filePath).getAbsolutePath();
        String outputPath = new File(tempDir, filePath.replace(".xlsx", "converted" + timeStamp + ".xlsx")).getAbsolutePath();
        String qrCodePath = QRCodeGenerator.generateQRCode("http://example.com/doc/" + metadata.getId(), tempDir);

        FileProcessor.processXLSX(sourcePath, outputPath, metadata, qrCodePath);
    }

    /**
     * Generate Excel summary/change history document
     */
    private String generateExcelSummary(DocumentMetadata metadata, String timeStamp) throws Exception {
        String excelPath = new File(tempDir, metadata.getDocumentCode() + "_" + timeStamp + ".xlsx").getAbsolutePath();

        // Create simple Excel file with metadata
        // This would use ExcelProcessor or similar to create the summary
        System.out.println("Excel summary would be generated at: " + excelPath);

        return excelPath;
    }

    /**
     * Cleanup temporary files
     */
    private void cleanupFiles(String qrCodePath, String convertedFilePath, String timeStamp) {
        try {
            // Delete QR code if present
            if (qrCodePath != null && !qrCodePath.isEmpty()) {
                QRCodeGenerator.deleteQRCode(qrCodePath);
            }

            // Delete temporary converted files
            if (convertedFilePath != null) {
                File tempFile = new File(tempDir, convertedFilePath);
                if (tempFile.exists()) {
                    tempFile.delete();
                }

                // Delete converted PDF
                File pdfFile = new File(tempDir, convertedFilePath.replace(".xlsx", ".pdf").replace(".docx", ".pdf"));
                if (pdfFile.exists()) {
                    pdfFile.delete();
                }
            }

        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    // Getters and Setters
    public String getMediaDir() { return mediaDir; }
    public void setMediaDir(String mediaDir) { this.mediaDir = mediaDir; }

    public String getBaseDir() { return baseDir; }
    public void setBaseDir(String baseDir) { this.baseDir = baseDir; }

    public String getTempDir() { return tempDir; }
    public void setTempDir(String tempDir) { this.tempDir = tempDir; }
}
