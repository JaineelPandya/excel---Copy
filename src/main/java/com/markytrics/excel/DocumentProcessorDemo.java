package com.markytrics.excel;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;


/**
 * Demo/Test class that executes the Django-equivalent function in Java
 */
public class DocumentProcessorDemo {

    public static void main(String[] args) {
        System.out.println("=== Document PDF Version Processor ===");
        System.out.println("Converting Django openpdfversion function to Java\n");

        try {
            // Create sample document metadata (simulating database query)
            DocumentMetadata metadata = createSampleMetadata();

            // Create the processor
            OpenPdfVersion processor = new OpenPdfVersion();
            processor.setMediaDir("media");
            processor.setBaseDir(".");
            processor.setTempDir("temp");

            // Example 1: Process a PDF file
            System.out.println("\n--- Example 1: Processing PDF File ---");
            processDocument(processor, "sample_document.pdf", "DOC001", "false", metadata);

            // Example 2: Process an XLSX file
            System.out.println("\n--- Example 2: Processing XLSX File ---");
            processDocument(processor, "sample_spreadsheet.xlsx", "DOC001", "false", metadata);

            // Example 3: Process a DOCX file
            System.out.println("\n--- Example 3: Processing DOCX File ---");
            processDocument(processor, "sample_document.docx", "DOC001", "false", metadata);

            // Example 4: Process without watermark (admin access)
            System.out.println("\n--- Example 4: Processing without Watermark ---");
            processDocument(processor, "sensitive_document.pdf", "DOC002", "true", metadata);

            System.out.println("\n=== Processing Complete ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Process a single document
     */
    private static void processDocument(OpenPdfVersion processor, String filePath, String docId,
                                       String noWatermark, DocumentMetadata metadata) {
        try {
            System.out.println("Input file: " + filePath);
            System.out.println("Document ID: " + docId);
            System.out.println("Apply watermark: " + !"true".equalsIgnoreCase(noWatermark));
            System.out.println("Document Code: " + metadata.getDocumentCode());
            System.out.println("Document Version: " + metadata.getVersionNumber());

            // Create sample media directory with dummy file
            Files.createDirectories(Paths.get("media"));
            if (!Files.exists(Paths.get("media", filePath))) {
                Files.createFile(Paths.get("media", filePath));
                System.out.println("Created sample file: media/" + filePath);
            }

            // Process the document
            String outputPdfPath = processor.processPdfVersion(filePath, docId, noWatermark, metadata);

            System.out.println("Output PDF: " + outputPdfPath);
            System.out.println("Status: SUCCESS");

        } catch (Exception e) {
            System.err.println("Error processing document: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create sample metadata (would normally come from database)
     */
    private static DocumentMetadata createSampleMetadata() {
        LocalDateTime now = LocalDateTime.now();

        return new DocumentMetadata(
            "DOC001",                                  // id
            "QHS-2024-001",                           // documentCode
            "Quality Management Procedure",            // documentName
            "2.0",                                     // versionNumber
            now.minusDays(30),                        // registrationDate
            now.minusDays(30),                        // dateOfIssue
            "John Smith",                             // issuerPerson
            "QA Manager",                             // issuerPosition
            null,                                      // subDocumentType (null = main document)
            "Updated procedures"                      // changeReason
        );
    }
}
