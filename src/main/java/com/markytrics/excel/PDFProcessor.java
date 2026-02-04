package com.markytrics.excel;


import com.itextpdf.text.Image;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles PDF processing operations including merging, adding watermarks, and footers
 */
public class PDFProcessor {
    private static final DateTimeFormatter FOOTER_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    /**
     * Merge/copy PDF pages from source to writer
     *
     * @param sourcePath Source PDF file path
     * @param outputPath Output PDF file path
     */
    public static void copyPDF(String sourcePath, String outputPath) throws Exception {
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(sourcePath);
            stamper = new PdfStamper(reader, new FileOutputStream(outputPath));
        } finally {
            if (stamper != null) {
                stamper.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Add footer and watermark to PDF
     *
     * @param pdfPath Path to the PDF file
     * @param timestamp Timestamp to add to footer
     * @param addWatermark Whether to add watermark
     */
    public static void addFooterAndWatermark(String pdfPath, LocalDateTime timestamp, boolean addWatermark) throws Exception {
        PdfReader reader = null;
        FileOutputStream fos = null;
        PdfStamper stamper = null;
        
        try {
            reader = new PdfReader(pdfPath);
            fos = new FileOutputStream(pdfPath);
            stamper = new PdfStamper(reader, fos);

            String footerText = "Downloaded on " + timestamp.format(FOOTER_FORMATTER);

            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                PdfContentByte under = stamper.getUnderContent(i);
                
                // Add watermark if needed
                if (addWatermark) {
                    addWatermark(under);
                }

                // Add footer
                addFooter(under, footerText, reader.getPageSizeWithRotation(i));
            }
        } finally {
            if (stamper != null) {
                stamper.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Add image to PDF
     *
     * @param pdfPath Path to the PDF file
     * @param imagePath Path to the image file
     * @param pageNumber Page number to add image to (1-based)
     * @param x X coordinate
     * @param y Y coordinate
     */
    public static void addImageToPDF(String pdfPath, String imagePath, int pageNumber, float x, float y) throws Exception {
        PdfReader reader = null;
        FileOutputStream fos = null;
        PdfStamper stamper = null;
        
        try {
            reader = new PdfReader(pdfPath);
            fos = new FileOutputStream(pdfPath);
            stamper = new PdfStamper(reader, fos);

            Image image = Image.getInstance(imagePath);
            image.setAbsolutePosition(x, y);
            image.scaleToFit(150, 150);

            stamper.getOverContent(pageNumber).addImage(image);
        } finally {
            if (stamper != null) {
                stamper.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Add watermark to PDF page
     */
    private static void addWatermark(PdfContentByte cb) throws Exception {
        cb.beginText();
        cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false), 60);
        cb.setTextMatrix(200, 400);
        cb.setColorFill(new GrayColor(0.5f));
        cb.showText("WATERMARK");
        cb.endText();
    }

    /**
     * Add footer text to PDF page
     */
    private static void addFooter(PdfContentByte cb, String footerText, Rectangle pageSize) throws Exception {
        cb.beginText();
        cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false), 8);
        cb.setTextMatrix(10, 10);
        cb.showText(footerText);
        cb.endText();
    }

    /**
     * Convert DOCX to PDF (using external tool simulation)
     * Note: Actual implementation would require LibreOffice or similar
     */
    public static void convertDocToPDF(String docPath) {
        try {
            String pdfPath = docPath.replace(".docx", ".pdf");
            // In a real implementation, this would call LibreOffice or another PDF converter
            System.out.println("Document conversion would be performed: " + docPath + " -> " + pdfPath);
            Files.createFile(Paths.get(pdfPath));
        } catch (Exception e) {
            System.err.println("Failed to convert document to PDF: " + e.getMessage());
        }
    }

    /**
     * Convert Excel to PDF (using external tool simulation)
     */
    public static void convertExcelToPDF(String excelPath) {
        try {
            String pdfPath = excelPath.replace(".xlsx", ".pdf");
            // In a real implementation, this would call LibreOffice or another PDF converter
            System.out.println("Excel conversion would be performed: " + excelPath + " -> " + pdfPath);
            Files.createFile(Paths.get(pdfPath));
        } catch (Exception e) {
            System.err.println("Failed to convert Excel to PDF: " + e.getMessage());
        }
    }
}
