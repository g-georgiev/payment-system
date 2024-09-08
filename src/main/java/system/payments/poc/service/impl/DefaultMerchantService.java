package system.payments.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import system.payments.poc.dto.MerchantInputDto;
import system.payments.poc.dto.MerchantOutputDto;
import system.payments.poc.dto.MerchantOutputPageDto;
import system.payments.poc.exceptions.MerchantHasTransactionsException;
import system.payments.poc.mapper.MerchantMapper;
import system.payments.poc.model.Merchant;
import system.payments.poc.repository.MerchantRepository;
import system.payments.poc.repository.TransactionRepository;
import system.payments.poc.service.MerchantService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DefaultMerchantService implements MerchantService {
    private final TransactionRepository transactionRepository;

    private final MerchantRepository merchantRepository;

    private final MerchantMapper merchantMapper;

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public MerchantOutputDto create(MerchantInputDto merchant) {
        return merchantMapper.toDto(merchantRepository.findByEmail(merchant.getEmail())
                .orElseGet(() -> merchantRepository.save(merchantMapper.toEntity(merchant))));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @Transactional
    public void updateTotalTransactionSum(Merchant merchant, BigDecimal newAmount) {
        BigDecimal newTotalMerchant = merchant.getTotalTransactionSum().add(newAmount);
        merchant.setTotalTransactionSum(newTotalMerchant);
        merchantRepository.save(merchant);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public MerchantOutputDto patch(Long id, MerchantInputDto merchantDto) {
        Merchant merchant = findById(id);
        merchantMapper.partialUpdate(merchant, merchantDto);
        return merchantMapper.toDto(merchantRepository.save(merchant));
    }

    @Override
    @Transactional
    public Merchant findById(Long id) {
        return merchantRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public MerchantOutputDto getById(Long id) {
        return merchantMapper.toDto(findById(id));
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public MerchantOutputPageDto getAll(Integer pageNumber, Integer pageSize, String sortColumn, Sort.Direction sortDirection) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sortColumn));
        Page<Merchant> merchantPage = merchantRepository.findAll(pageable);

        return MerchantOutputPageDto.builder()
                .merchants(merchantPage.map(merchantMapper::toDto).getContent())
                .totalPages(merchantPage.getTotalPages())
                .currentPage(pageNumber)
                .pageSize(pageSize)
                .sortColumn(sortColumn)
                .sortDirection(sortDirection)
                .build();
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void deleteById(Long id) {
        if (transactionRepository.existsByMerchant_Id(id)) {
            throw new MerchantHasTransactionsException();
        }

        merchantRepository.deleteById(id);
    }
}
