package ata.unit.three.project.expense.service.exceptions;

import java.util.HashMap;
import java.util.Map;

public class InvalidExpenseException extends InvalidDataException {

    private String body;

    public InvalidExpenseException(String msg) {
        super(msg);
    }

    public InvalidExpenseException(String msg, String body) {
        super(msg);
        this.body = body;
    }

    @Override
    public Map<String, Object> errorPayload() {
        Map<String, Object> errorPayload = new HashMap();
        errorPayload.put("errorType", "invalid_expense");
        errorPayload.put("message", this.getMessage() + " " + this.body);
        return errorPayload;
    }
}
