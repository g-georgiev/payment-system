package system.payments.poc.mapper;

import org.mapstruct.Mapper;
import system.payments.poc.dto.TransactionOutputDto;
import system.payments.poc.model.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionOutputMapper {
    TransactionOutputDto toDto(Transaction entity);
}
