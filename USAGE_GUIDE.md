# Java Implementation Usage Guide

## Quick Start

The Django `openpdfversion` function has been successfully converted to Java with the following key classes:

### Main Classes

#### 1. OpenPdfVersion (Main Logic)
This is the equivalent of the Django view function.

```java
OpenPdfVersion processor = new OpenPdfVersion();
processor.setMediaDir("media");          // Directory containing source files
processor.setBaseDir(".");               // Base project directory
processor.setTempDir("temp");            // Temporary working directory

// Process a document
String outputPdfPath = processor.processPdfVersion(
    "path/to/file.pdf",        // File path (relative to media dir)
    "DOC_ID_123",              // Document ID
    "false",                   // Apply watermark? "true" or "false"
    metadata                   // DocumentMetadata object
);
```

#### 2. DocumentMetadata
Represents document information from database.

```java
DocumentMetadata metadata = new DocumentMetadata(
    "DOC001",                           // id
    "QHS-2024-001",                     // documentCode
    "Quality Management Procedure",      // documentName
    "2.0",                              // versionNumber
    LocalDateTime.now().minusDays(30),  // registrationDate
    LocalDateTime.now().minusDays(30),  // dateOfIssue
    "John Smith",                       // issuerPerson
    "QA Manager",                       // issuerPosition
    null,                               // subDocumentType (null for main)
    "Updated procedures"                // changeReason
);
```

### Supporting Classes

#### FileProcessor
Handles DOCX and XLSX file processing.

```java
// Process DOCX
FileProcessor.processDOCX(
    "path/to/input.docx",
    "path/to/output.docx",
    metadata
);

// Process XLSX
FileProcessor.processXLSX(
    "path/to/input.xlsx",
    "path/to/output.xlsx",
    metadata,
    "path/to/qrcode.png"  // Optional QR code
);
```

#### PDFProcessor
Handles PDF operations.

```java
// Copy PDF
PDFProcessor.copyPDF("source.pdf", "output.pdf");

// Add footer and watermark
PDFProcessor.addFooterAndWatermark(
    "file.pdf",
    LocalDateTime.now(),
    true  // add watermark?
);

// Add image to PDF
PDFProcessor.addImageToPDF(
    "file.pdf",
    "image.png",
    1,      // page number
    50.0f,  // x coordinate
    750.0f  // y coordinate
);
```

#### QRCodeGenerator
Generate and manage QR codes.

```java
// Generate QR code
String qrPath = QRCodeGenerator.generateQRCode(
    "http://example.com/doc/123",
    "temp"  // output directory
);

// Delete QR code
QRCodeGenerator.deleteQRCode(qrPath);
```

## Workflow Comparison

### Original Django Flow
```
request → CheckUserAccess() → database query
→ process file based on type
→ create Excel summary
→ add QR code & watermark
→ serve PDF response
```

### Java Implementation Flow
```
OpenPdfVersion.processPdfVersion()
├─ Validate parameters
├─ Generate timestamp
├─ Process by file type:
│  ├─ DOCX: FileProcessor.processDOCX()
│  ├─ XLSX: FileProcessor.processXLSX() + QRCodeGenerator
│  └─ PDF: PDFProcessor.copyPDF()
├─ Generate Excel summary
├─ Add watermark/footer: PDFProcessor.addFooterAndWatermark()
├─ Cleanup temp files
└─ Return PDF path
```

## File Structure

```
project/
├── src/main/java/com/markytrics/excel/
│   ├── DocumentMetadata.java          # Data model
│   ├── OpenPdfVersion.java            # Main processor
│   ├── FileProcessor.java             # DOCX/XLSX handling
│   ├── PDFProcessor.java              # PDF operations
│   ├── QRCodeGenerator.java           # QR code utilities
│   ├── DocumentProcessorDemo.java     # Example/test
│   ├── ExcelProcessor.java            # Existing (unchanged)
│   ├── ExcelGeneratorGUI.java         # Existing (unchanged)
│   └── Main.java                      # Entry point
├── pom.xml                            # Maven config with new deps
├── CONVERSION_SUMMARY.md              # Detailed conversion doc
└── USAGE_GUIDE.md                     # This file
```

## Testing

Run the demo to see the implementation in action:

```bash
mvn package
java -cp target/excel-generator-1.0-SNAPSHOT.jar \
    com.markytrics.excel.DocumentProcessorDemo
```

## Integration with Existing Code

### Option 1: Use as Service in Spring Boot
```java
@Service
public class DocumentService {
    private final OpenPdfVersion pdfProcessor;
    
    @Autowired
    public DocumentService() {
        this.pdfProcessor = new OpenPdfVersion();
    }
    
    public String processPdf(String filePath, DocumentMetadata doc) {
        return pdfProcessor.processPdfVersion(
            filePath, 
            doc.getId(), 
            "false", 
            doc
        );
    }
}
```

### Option 2: Create REST Endpoint
```java
@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @PostMapping("/process-pdf")
    public ResponseEntity<byte[]> processPdf(
        @RequestParam String filePath,
        @RequestBody DocumentMetadata metadata) {
        // Call OpenPdfVersion.processPdfVersion()
        // Return PDF as response
    }
}
```

## Error Handling

All classes handle exceptions appropriately:

```java
try {
    String pdf = processor.processPdfVersion(...);
} catch (IOException e) {
    // File not found or read error
} catch (Exception e) {
    // Other errors (PDF format, etc.)
}
```

## Performance Notes

- **Memory**: Efficient streaming for large files
- **CPU**: Single-threaded by default; parallelizable for multiple docs
- **Disk**: Uses temp directory; cleans up automatically
- **Time**: Processing time depends on file size and conversion tools

## Future Improvements

1. Add async/parallel processing for multiple documents
2. Integrate LibreOffice for better DOCX/XLSX to PDF conversion
3. Add caching for QR codes
4. Support batch processing
5. Add progress callbacks for large files
6. Database integration layer
