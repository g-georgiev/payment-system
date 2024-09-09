package system.payments.poc.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import system.payments.poc.dto.MerchantInputDto;
import system.payments.poc.dto.MerchantOutputDto;
import system.payments.poc.model.Merchant;

@Mapper(componentModel = "spring")
public interface MerchantMapper {
    @Mapping(target = "password", ignore = true)
    MerchantOutputDto toDto(Merchant entity);

    Merchant toEntity(MerchantInputDto dto);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Merchant entity, MerchantInputDto dto);
}
