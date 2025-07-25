package az.cybernet.invoice.service.abstraction;

public interface ItemService {
    void updateItem();

    void findAllItemsByInvoiceId();

    void restoreItem();
}
