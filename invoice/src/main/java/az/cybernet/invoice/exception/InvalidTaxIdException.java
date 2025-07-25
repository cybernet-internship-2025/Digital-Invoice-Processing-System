package az.cybernet.invoice.exception;

import lombok.Getter;

@Getter
public class InvalidTaxIdException extends RuntimeException {
    public InvalidTaxIdException(String taxId) {
        super("Invalid Tax ID format: " + taxId);
    }
}
