package system.payments.poc.exceptions;

import java.io.Serial;

public class MerchantHasTransactionsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public MerchantHasTransactionsException() {
        super("Merchant can only be deleted if no transactions are related to them.");
    }
}
