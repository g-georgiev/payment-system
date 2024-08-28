package system.payments.poc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.dto.TransactionOutputDto;
import system.payments.poc.enums.TransactionType;
import system.payments.poc.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/{transactionType}")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionOutputDto create(@PathVariable TransactionType transactionType, TransactionInputDto transactionInputDto) {
        return transactionService.createTransaction(transactionType, transactionInputDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionOutputDto> getTransactions(@RequestParam Long merchantId) {
        return transactionService.getTransactions(merchantId);
    }

}
