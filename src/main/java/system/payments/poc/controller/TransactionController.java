package system.payments.poc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.dto.TransactionOutputDto;
import system.payments.poc.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionOutputDto create(@Valid @RequestBody TransactionInputDto transactionInputDto) {
        return transactionService.createTransaction(transactionInputDto);
    }

    @GetMapping("/merchant/current")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionOutputDto> getTransactions() {
        return transactionService.getTransactions();
    }

}
