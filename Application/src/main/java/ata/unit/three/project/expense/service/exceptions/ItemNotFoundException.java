package ata.unit.three.project.expense.service.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }

    public Map<String, Object> errorPayload() {
        Map<String, Object> errorPayload = new HashMap();
        errorPayload.put("errorType", "invalid_data");
        errorPayload.put("message", this.getMessage());
        return errorPayload;
    }
}
