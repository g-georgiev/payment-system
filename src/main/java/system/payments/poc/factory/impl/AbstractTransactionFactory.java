package system.payments.poc.factory.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.enums.MerchantStatus;
import system.payments.poc.exceptions.MerchantInactiveException;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.Merchant;
import system.payments.poc.model.Transaction;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.service.MerchantService;

import java.util.Set;


@Service
@RequiredArgsConstructor
public abstract class AbstractTransactionFactory implements TransactionFactory {
    protected final MerchantService merchantService;

    protected void populateCommonTransaction(Transaction transaction, TransactionInputDto transactionInputDto) {
        Merchant merchant = merchantService.findById(transactionInputDto.getMerchantId());
        if(!merchant.getStatus().equals(MerchantStatus.ACTIVE)) {
            throw new MerchantInactiveException();
        }

        transaction.setMerchant(merchant);
        transaction.setCustomerEmail(transactionInputDto.getCustomerEmail());
        transaction.setCustomerPhone(transactionInputDto.getCustomerPhone());
    }

    protected boolean approveTransaction(Transaction referenceTransaction, Transaction transaction) {
        if(Set.of(TransactionStatus.APPROVED, TransactionStatus.REFUNDED).contains(referenceTransaction.getStatus())) {
            transaction.setStatus(TransactionStatus.APPROVED);
            return true;
        } else {
            transaction.setStatus(TransactionStatus.ERROR);
            return false;
        }
    }
}
