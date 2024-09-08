package system.payments.poc.controller;

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
public class MerchantController {
    private final MerchantService merchantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantOutputDto create(@Valid @RequestBody MerchantInputDto merchantInputDto) {
        return merchantService.create(merchantInputDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public MerchantOutputPageDto getAll(@RequestParam @Min(0) Integer pageNumber, @RequestParam @Min(1) Integer pageSize,
                                        @RequestParam @Pattern(regexp = "^id|email|username|status|totalTransactionSum$") String sortColumn,
                                        @RequestParam Sort.Direction sortDirection) {
        return merchantService.getAll(pageNumber, pageSize, sortColumn, sortDirection);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MerchantOutputDto getById(@PathVariable Long id) {
        return merchantService.getById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MerchantOutputDto update(@PathVariable Long id, @RequestBody MerchantInputDto merchantInputDto) {
        return merchantService.patch(id, merchantInputDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        merchantService.deleteById(id);
    }
}
