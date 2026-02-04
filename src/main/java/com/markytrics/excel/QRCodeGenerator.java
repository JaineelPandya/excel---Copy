package com.markytrics.excel;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Generates QR codes for document URLs
 */
public class QRCodeGenerator {
    private static final int QR_CODE_WIDTH = 200;
    private static final int QR_CODE_HEIGHT = 200;

    /**
     * Generate a QR code image file from the given URL
     *
     * @param url The URL to encode
     * @param tempDir The temporary directory to store the QR code
     * @return Path to the generated QR code image
     */
    public static String generateQRCode(String url, String tempDir) throws Exception {
        try {
            // Create temp directory if it doesn't exist
            Files.createDirectories(Paths.get(tempDir));

            // Generate unique filename for QR code
            String qrCodeFileName = "qrcode_" + UUID.randomUUID() + ".png";
            String qrCodePath = Paths.get(tempDir, qrCodeFileName).toString();

            // Create QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 
                                                      QR_CODE_WIDTH, QR_CODE_HEIGHT);

            // Write image file
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get(qrCodePath));

            return qrCodePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a QR code file
     *
     * @param filePath Path to the QR code file
     */
    public static void deleteQRCode(String filePath) {
        try {
            if (filePath != null && !filePath.isEmpty()) {
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to delete QR code: " + e.getMessage());
        }
    }
}
