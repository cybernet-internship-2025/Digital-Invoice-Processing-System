package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.service.abstraction.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{invoiceId}")
    public ResponseEntity<List<ItemResponse>> findAllItemsByInvoiceId(@PathVariable Long invoiceId) {
        List<ItemResponse> items = itemService.findAllItemsByInvoiceId(invoiceId);
        return ResponseEntity.ok(items);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        ItemResponse itemResponse = itemService.findById(id);
        return ResponseEntity.ok(itemResponse);
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteItems(@RequestBody List<Long> ids) {
        itemService.deleteItem(ids);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/invoice/{invoiceId}")
    public ResponseEntity<Void> deleteItemsByInvoiceId(@PathVariable Long invoiceId) {
        itemService.deleteItemsByInvoiceId(invoiceId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteItemsByItemsId(@RequestBody List<Long> ids) {
        itemService.deleteItemsByItemsId(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreItem(@PathVariable Long id) {
        itemService.restoreItem(id);
    }
}
