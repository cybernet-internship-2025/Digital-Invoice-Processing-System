package az.cybernet.invoice.util;
import az.cybernet.invoice.dto.response.invoice.FilterResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;


@Service
public class ExcelFileExporter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static byte[] exportInvoicesToExcel(List<FilterResponse> invoices, String[] headers) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Invoices");


            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // data
            int rowNum = 1;
            for (FilterResponse invoice : invoices) {
                Row row = sheet.createRow(rowNum++);

                setCell(row, 0, invoice.getFullInvoiceNumber());
                setCell(row, 1, invoice.getStatus() == null ? "" : invoice.getStatus());
                setCell(row, 2, invoice.getSenderTaxId() == null ? "" : invoice.getSenderTaxId());
                setCell(row, 3, invoice.getRecipientTaxId() == null ? null : invoice.getRecipientTaxId());
                setCell(row, 4, invoice.getTotalPrice() == null ? "" : invoice.getTotalPrice());
                setCell(row, 5, invoice.getCreatedAt());
                setCell(row, 6, invoice.getUpdatedAt());
            }


            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export invoices to Excel", e);
        }
    }

    // helper method
    private static void setCell(Row row, int col, Object value) {
        Cell cell = row.createCell(col);
        if (value == null) {
            cell.setBlank();
        } else if (value instanceof String str) {
            cell.setCellValue(str);
        } else if (value instanceof Integer i) {
            cell.setCellValue(i);
        } else if (value instanceof Long l) {
            cell.setCellValue(l);
        } else if (value instanceof BigDecimal bd) {
            cell.setCellValue(bd.doubleValue());
        } else if (value instanceof LocalDateTime dt) {
            cell.setCellValue(DATE_TIME_FORMATTER.format(dt));
        } else {
            cell.setCellValue(value.toString());
        }
    }
    public ResponseEntity<byte[]> buildExcelResponse(byte[] bytes, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName + ".xlsx").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }
}

