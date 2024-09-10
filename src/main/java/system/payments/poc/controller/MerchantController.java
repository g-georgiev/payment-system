package system.payments.poc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import system.payments.poc.dto.MerchantInputDto;
import system.payments.poc.dto.MerchantOutputDto;
import system.payments.poc.dto.MerchantOutputPageDto;
import system.payments.poc.service.MerchantService;

@RestController
@Validated
@RequestMapping("/merchant")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MerchantController {
    private final MerchantService merchantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new merchant. Endpoint is idempotent")
    public MerchantOutputDto create(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Information about the merchant to be created", required = true)
                                    @Valid @RequestBody MerchantInputDto merchantInputDto) {
        return merchantService.create(merchantInputDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get information about all merchants. Supports paging and sorting")
    public MerchantOutputPageDto getAll(@RequestParam @Min(0) Integer pageNumber, @RequestParam @Min(1) Integer pageSize,
                                        @RequestParam @Pattern(regexp = "^id|email|username|status|totalTransactionSum$") String sortColumn,
                                        @RequestParam Sort.Direction sortDirection) {
        return merchantService.getAll(pageNumber, pageSize, sortColumn, sortDirection);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get information about specific merchant by ID")
    public MerchantOutputDto getById(@PathVariable Long id) {
        return merchantService.getById(id);
    }

    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get information about the currently authenticated merchant")
    public MerchantOutputDto getCurrent() {
        return merchantService.getCurrent();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Update merchant data. Only data provided will be updated. Merchant will NOT be recreated (PATCH)")
    public MerchantOutputDto update(@PathVariable Long id, @RequestBody MerchantInputDto merchantInputDto) {
        return merchantService.patch(id, merchantInputDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete merchant by ID. Merchant will NOT be deleted if they have existing transactions")
    public void delete(@PathVariable Long id) {
        merchantService.deleteById(id);
    }
}
