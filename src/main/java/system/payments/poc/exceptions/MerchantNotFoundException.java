package system.payments.poc.exceptions;

import java.io.Serial;

public class MerchantNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public MerchantNotFoundException() {
        super("There is no merchant with that id.");
    }
}
