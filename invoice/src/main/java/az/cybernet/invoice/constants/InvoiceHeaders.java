package az.cybernet.invoice.constants;

import lombok.Data;

@Data
public class InvoiceHeaders {
    public static final String[] HEADERS = {
            "Qaimə nömrəsi",
            "Göndərənin ID-si",
            "Müştərinin ID-si",
            "Status",
            "Ümumi məbləğ",
            "Yaranma tarixi",
            "Dəyişdirilmə tarixi",
            "Rəy",
            "Tip"
    };
}
