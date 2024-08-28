package system.payments.poc.exceptions;

import java.io.Serial;

public class MerchantInactiveException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public MerchantInactiveException() {
        super("Merchant must be activated before processing transactions.");
    }
}
