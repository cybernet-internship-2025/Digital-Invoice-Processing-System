package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.service.abstraction.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ItemController {
    ItemService itemService;

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
    public ResponseEntity<Void> deleteItemsByItemsId(@RequestBody List<Long> ids) {
        itemService.deleteItemsByItemsId(ids);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/invoice/{invoiceId}")
    public ResponseEntity<Void> deleteItemsByInvoiceId(@PathVariable Long invoiceId) {
        itemService.deleteItemsByInvoiceId(invoiceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping()
    public List<ItemResponse> addItems(@RequestBody @Valid ItemsRequest requests) {
        return itemService.addItems(requests);
    }
}
