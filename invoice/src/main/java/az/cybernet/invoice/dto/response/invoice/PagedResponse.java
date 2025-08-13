package az.cybernet.invoice.dto.response.invoice;

import lombok.Data;

import java.util.List;

@Data
public class PagedResponse<T> {
    private List<T> content;
    private boolean hasNext;
    private Integer offset;
    private Integer limit;
}