package az.cybernet.invoice.dto.request.invoice;

import lombok.Data;

import java.time.LocalDate;
@Data
public class FilterInvoiceRequest {
    private Integer year;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String status;
    private String type;
    private String series;
    private String number;
    private String senderTaxId;
    private String recipientTaxId;
    private Integer offset;
    private Integer limit;
}
