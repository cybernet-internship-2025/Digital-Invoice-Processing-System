package az.cybernet.filestorage.service;

import az.cybernet.filestorage.dto.client.InvoiceResponse;
import az.cybernet.filestorage.dto.client.ItemResponse;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

@Service
public class PDFGeneratorService {
    public void export(HttpServletResponse response, InvoiceResponse invoice) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=invoice.pdf");

        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Image logo = Image.getInstance(Objects.requireNonNull(getClass().getResource("/Vergi.png")));
            logo.scaleToFit(120, 60);
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            fontTitle.setColor(Color.RED);

            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font fontNormal1 = FontFactory.getFont(FontFactory.HELVETICA, 12);
            fontNormal1.setColor(Color.BLUE);

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            Paragraph title = new Paragraph("Qaimə", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(21);
            document.add(title);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10f);
            infoTable.setSpacingAfter(20f);

            infoTable.addCell(createCell("Kimdən: " + invoice.getSenderTaxId(), fontNormal));

            infoTable.addCell(createCell("Kimə: " + invoice.getRecipientTaxId(), fontNormal));

            infoTable.addCell(createCell("Qaimə Nömrəsi: INVD" + invoice.getInvoiceNumber(), fontNormal));
            infoTable.addCell(createCell("Tarix: " + invoice.getCreatedAt(), fontNormal));
            infoTable.addCell(createCell("Operator: Administrator", fontNormal));

            document.add(infoTable);


            PdfPTable itemsTable = new PdfPTable(4);
            itemsTable.setWidthPercentage(100);
            itemsTable.setSpacingBefore(10f);


            String[] headers = {"Sıra", "Malın adı", "Miqdar", "Qiymət"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, fontHeader));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(Color.LIGHT_GRAY);
                itemsTable.addCell(headerCell);
            }

            int count = 1;
            for (ItemResponse item : invoice.getItems()) {
                itemsTable.addCell(createCell(String.valueOf(count++), fontNormal));
                itemsTable.addCell(createCell(item.getProductName(), fontNormal));
                itemsTable.addCell(createCell(String.valueOf(item.getQuantity()), fontNormal));
                itemsTable.addCell(createCell(item.getTotalPrice().toString(), fontNormal));
            }

            document.add(itemsTable);

            Paragraph total = new Paragraph("Ümumi məbləği: " + invoice.getTotalPrice(), fontHeader);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(10f);
            document.add(total);

        } catch (Exception e) {
            throw new IOException("Error", e);
        }
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}
