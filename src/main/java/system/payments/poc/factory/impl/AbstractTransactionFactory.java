package system.payments.poc.factory.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.factory.TransactionFactory;
import system.payments.poc.model.Transaction;
import system.payments.poc.enums.TransactionStatus;
import system.payments.poc.service.MerchantService;


@Service
@RequiredArgsConstructor
public abstract class AbstractTransactionFactory implements TransactionFactory {
    protected final MerchantService merchantService;

    protected void populateCommonTransaction(Transaction transaction, TransactionInputDto transactionInputDto) {
        transaction.setMerchant(merchantService.findById(transactionInputDto.getMerchantId()));
        transaction.setCustomerEmail(transactionInputDto.getCustomerEmail());
        transaction.setCustomerPhone(transactionInputDto.getCustomerPhone());
    }

    protected boolean approveTransaction(Transaction referenceTransaction, Transaction transaction) {
        if(!referenceTransaction.getStatus().equals(TransactionStatus.APPROVED)) {
            transaction.setStatus(TransactionStatus.ERROR);
            return false;
        } else {
            transaction.setStatus(TransactionStatus.APPROVED);
            return true;
        }
    }
}
