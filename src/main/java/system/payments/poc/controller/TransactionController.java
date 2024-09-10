package system.payments.poc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a transaction. Endpoint is NOT idempotent")
    public TransactionOutputDto create(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New transaction data. Transaction type is provided here. Validations and required data depends on the type of transaction")
                                       @Valid @RequestBody TransactionInputDto transactionInputDto) {
        return transactionService.createTransaction(transactionInputDto);
    }

    @GetMapping("/merchant/current")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all transactions for the currently authenticated merchant")
    public List<TransactionOutputDto> getCurrentUserTransactions() {
        return transactionService.getCurrentUserTransactions();
    }

    @GetMapping("/merchant/{merchantId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all transactions by merchant ID")
    public List<TransactionOutputDto> getTransactions(@PathVariable Long merchantId) {
        return transactionService.getTransactions(merchantId);
    }

}
