package az.cybernet.invoice.Scheduler;

import az.cybernet.invoice.service.abstraction.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class InvoiceScheduler {

    InvoiceService invoiceService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void autoApprovePendingInvoices() {
        invoiceService.approvePendingInvoicesAfterTimeout();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void autoCancelPendingInvoices(){
        invoiceService.cancelPendingInvoicesAfterTimeout();
    }
}
