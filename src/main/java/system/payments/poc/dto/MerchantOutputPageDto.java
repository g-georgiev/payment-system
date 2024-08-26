package system.payments.poc.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@Builder
public class MerchantOutputPageDto {

    private List<MerchantOutputDto> merchants;

    private Integer currentPage;

    private Integer pageSize;

    private Integer totalPages;

    private String sortColumn;

    private Sort.Direction sortDirection;
}
