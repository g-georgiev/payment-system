package system.payments.poc.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import system.payments.poc.dto.MerchantInputDto;
import system.payments.poc.dto.MerchantOutputDto;
import system.payments.poc.dto.MerchantOutputPageDto;
import system.payments.poc.model.Merchant;

import java.math.BigDecimal;

public interface MerchantService {
    MerchantOutputDto create(MerchantInputDto merchant);

    void updateTotalTransactionSum(Merchant merchant, BigDecimal newAmount);

    MerchantOutputDto patch(Long id, MerchantInputDto merchant);

    Merchant findById(Long id);

    MerchantOutputDto getById(Long id);

    @Transactional
    MerchantOutputDto getCurrent();

    MerchantOutputPageDto getAll(Integer pageNumber, Integer pageSize, String sortColumn, Sort.Direction sortDirection);

    void deleteById(Long id);
}
