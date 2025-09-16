package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface PDFGeneratorService {
    void export(HttpServletResponse response, InvoiceResponse invoice) throws IOException;
}
