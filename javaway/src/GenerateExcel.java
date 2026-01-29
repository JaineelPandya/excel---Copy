import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GenerateExcel {

    private static final String DEFAULT_TEMPLATE = "template/Remote_Access_and_PAM_Form(1).xlsx";
    private static final String OUTPUT_DIR = "output";

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java GenerateExcel <DOC_REF> <VERSION> <DATE: DD-MMM-YYYY> [templatePath]");
            System.out.println("Example: java GenerateExcel IPACK-PAM-2026-001 v1.0 29-Jan-2026 template/Remote_Access_and_PAM_Form(1).xlsx");
            return;
        }

        String docRef = args[0];
        String version = args[1];
        String dateStr = args[2];
        String templatePath = (args.length >= 4) ? args[3] : DEFAULT_TEMPLATE;

        File tmpl = new File(templatePath);
        if (!tmpl.exists()) {
            System.err.println("Template not found: " + templatePath);
            return;
        }

        Files.createDirectories(Path.of(OUTPUT_DIR));
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String outputPath = OUTPUT_DIR + File.separator + "Remote_Access_Request_" + timestamp + ".xlsx";

        try (FileInputStream fis = new FileInputStream(tmpl);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheetAt(0);

            // Write values next to labels. For date, parse and set a date cell with format.
            writeValueNextToLabel(wb, sheet, "DOC. REF. NO.", docRef, false);
            writeValueNextToLabel(wb, sheet, "VERSION NO.", version, false);
            writeValueNextToLabel(wb, sheet, "DATE:", dateStr, true);

            // Save to new file (does not modify template file)
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                wb.write(fos);
            }

            System.out.println("Generated: " + outputPath);
        }
    }

    private static void writeValueNextToLabel(Workbook wb, Sheet sheet, String label, String value, boolean isDate) {
        DataFormat df = wb.createDataFormat();
        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(df.getFormat("dd-MMM-yyyy"));

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell == null) continue;
                if (cell.getCellType() != CellType.STRING) continue;
                String cellText = cell.getStringCellValue();
                if (cellText == null) continue;
                if (cellText.trim().equals(label)) {
                    int targetRow = cell.getRowIndex();
                    int targetCol = cell.getColumnIndex() + 1;

                    // Check merged regions first: if target cell is part of a merged region,
                    // write into the top-left of that merged region so visuals (logo/checkbox) remain untouched.
                    List<CellRangeAddress> merged = sheet.getMergedRegions();
                    boolean handled = false;
                    for (CellRangeAddress region : merged) {
                        if (region.isInRange(targetRow, targetCol)) {
                            int r = region.getFirstRow();
                            int c = region.getFirstColumn();
                            Row rr = sheet.getRow(r);
                            if (rr == null) rr = sheet.createRow(r);
                            Cell tgt = rr.getCell(c);
                            if (tgt == null) tgt = rr.createCell(c);
                            setCellValue(wb, tgt, value, isDate, dateStyle);
                            handled = true;
                            break;
                        }
                    }

                    if (!handled) {
                        Row rr = sheet.getRow(targetRow);
                        if (rr == null) rr = sheet.createRow(targetRow);
                        Cell tgt = rr.getCell(targetCol);
                        if (tgt == null) tgt = rr.createCell(targetCol);
                        setCellValue(wb, tgt, value, isDate, dateStyle);
                    }

                    return; // stop after first match
                }
            }
        }
    }

    private static void setCellValue(Workbook wb, Cell cell, String value, boolean isDate, CellStyle dateStyle) {
        if (isDate) {
            // parse date string in format dd-MMM-yyyy
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            try {
                Date d = sdf.parse(value);
                cell.setCellValue(d);
                cell.setCellStyle(dateStyle);
            } catch (ParseException e) {
                // fallback to writing raw text if parse fails
                cell.setCellValue(value);
            }
        } else {
            cell.setCellValue(value);
        }
    }
}
