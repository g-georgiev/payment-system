package system.payments.poc.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import system.payments.poc.dto.TransactionOutputDto;
import system.payments.poc.model.Transaction;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface TransactionOutputMapper {
    TransactionOutputDto toDto(Transaction entity);

    @AfterMapping
    default void addTransactionType(Transaction entity, @MappingTarget TransactionOutputDto dto) {
        dto.setTransactionType(Objects.requireNonNullElse(entity.getTransactionType(), entity.getClass().getSimpleName()));
    }

}
