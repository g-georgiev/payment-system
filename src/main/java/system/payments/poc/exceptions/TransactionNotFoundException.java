package system.payments.poc.exceptions;

import java.io.Serial;

public class TransactionNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public TransactionNotFoundException() {
        super("There is no transaction with that uid.");
    }
}
