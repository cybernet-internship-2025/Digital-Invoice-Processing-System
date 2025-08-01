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

    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreItem(@PathVariable Long id) {
        itemService.restoreItem(id);
    }
}
