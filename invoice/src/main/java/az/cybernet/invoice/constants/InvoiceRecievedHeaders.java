package az.cybernet.invoice.constants;

import lombok.Data;

@Data
public class InvoiceRecievedHeaders {
    public static final String[] HEADERS = {
            "Göndərənin ID-si",
            "Müştərinin ID-si",
            "Ümumi məbləğ",
            "Yaranma tarixi",
            "Status",
            "Qaimə nömrəsi",
            "Seriya nömrəsi",
            "Məhsul sayı"
    };
}
