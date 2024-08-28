package system.payments.poc.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import system.payments.poc.dto.MerchantInputDto;
import system.payments.poc.dto.MerchantOutputDto;
import system.payments.poc.dto.MerchantOutputPageDto;
import system.payments.poc.exceptions.MerchantHasTransactionsException;
import system.payments.poc.exceptions.MerchantNotFoundException;
import system.payments.poc.mapper.MerchantMapper;
import system.payments.poc.model.Merchant;
import system.payments.poc.repository.MerchantRepository;
import system.payments.poc.repository.TransactionRepository;
import system.payments.poc.service.MerchantService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

@ExtendWith(MockitoExtension.class)
class DefaultMerchantServiceTest {

    private MerchantService merchantService;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MerchantMapper merchantMapper;

    @BeforeEach
    void setUp() {
        merchantService = new DefaultMerchantService(transactionRepository, merchantRepository, merchantMapper);
    }

    @Test
    void create() {
        MerchantInputDto merchantInputDto = MerchantInputDto.builder().build();
        Merchant merchant = new Merchant();
        when(merchantMapper.toEntity(merchantInputDto)).thenReturn(merchant);
        when(merchantRepository.save(merchant)).thenReturn(merchant);

        merchantService.create(merchantInputDto);

        verify(merchantMapper).toEntity(merchantInputDto);
        verify(merchantRepository).save(merchant);
    }

    @ParameterizedTest
    @CsvSource({"10", "-10"})
    void updateTotalTransactionSum(String testAmount) {
        Merchant merchant = new Merchant();
        BigDecimal newAmount = new BigDecimal(testAmount);

        merchantService.updateTotalTransactionSum(merchant, newAmount);

        verify(merchantRepository).save(merchant);
        assertEquals(merchant.getTotalTransactionSum(), newAmount);
    }

    @Test
    void patch() {
        Long merchantId = 1L;
        Merchant merchant = new Merchant();
        MerchantInputDto merchantInputDto = MerchantInputDto.builder().build();

        MerchantService spy = spy(merchantService);
        doReturn(merchant).when(spy).findById(merchantId);
        when(merchantRepository.save(merchant)).thenReturn(merchant);

        spy.patch(merchantId, merchantInputDto);

        verify(spy).findById(merchantId);
        verify(merchantMapper).partialUpdate(merchant, merchantInputDto);
        verify(merchantRepository).save(merchant);
        verify(merchantMapper).toDto(merchant);
    }

    @Test
    void findById_success() {
        Long merchantId = 1L;
        Merchant merchant = new Merchant();
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchant));

        Merchant resultMerchant = merchantService.findById(merchantId);

        verify(merchantRepository).findById(merchantId);
        assertEquals(resultMerchant, merchant);
    }

    @Test
    void findById_fail() {
        Long merchantId = 1L;
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.empty());

        assertThrows(MerchantNotFoundException.class, () -> merchantService.findById(merchantId));
    }

    @Test
    void getById() {
        Long merchantId = 1L;
        Merchant merchant = new Merchant();

        MerchantService spy = spy(merchantService);
        doReturn(merchant).when(spy).findById(merchantId);

        spy.getById(merchantId);

        verify(spy).findById(merchantId);
        verify(merchantMapper).toDto(merchant);
    }

    @Test
    void getAll_failed_illegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> merchantService.getAll(-1, 1, "name", ASC));
        assertThrows(IllegalArgumentException.class, () -> merchantService.getAll(0, 0, "name", ASC));
        assertThrows(IllegalArgumentException.class, () -> merchantService.getAll(0, 1, "test", ASC));
    }

    @Test
    void getAll_success() {
        int page = 1;
        int size = 10;
        String sortColumn = "name";
        Sort.Direction sortDirection = ASC;
        Merchant merchant = new Merchant();
        MerchantOutputDto merchantDto = MerchantOutputDto.builder().build();

        when(merchantRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(merchant)));
        when(merchantMapper.toDto(merchant)).thenReturn(merchantDto);

        MerchantOutputPageDto resultMerchants = merchantService.getAll(page, size, sortColumn, sortDirection);

        verify(merchantRepository).findAll(any(Pageable.class));
        assertEquals(page, resultMerchants.getCurrentPage());
        assertEquals(size, resultMerchants.getPageSize());
        assertEquals(1, resultMerchants.getTotalPages());
        assertEquals(sortColumn, resultMerchants.getSortColumn());
        assertEquals(sortDirection, resultMerchants.getSortDirection());
        assertEquals(1, resultMerchants.getMerchants().size());
        assertEquals(merchant.getId(), resultMerchants.getMerchants().get(0).getId());
    }

    @Test
    void deleteById_success() {
        Long merchantId = 1L;
        when(transactionRepository.existsByMerchant_Id(merchantId)).thenReturn(false);

        merchantService.deleteById(merchantId);

        verify(merchantRepository).deleteById(merchantId);
    }

    @Test
    void deleteById_failed() {
        Long merchantId = 1L;
        when(transactionRepository.existsByMerchant_Id(merchantId)).thenReturn(true);

        assertThrows(MerchantHasTransactionsException.class, () -> merchantService.deleteById(merchantId));
    }
}