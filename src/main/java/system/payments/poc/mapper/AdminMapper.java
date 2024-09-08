package system.payments.poc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import system.payments.poc.dto.AdminDto;
import system.payments.poc.model.Admin;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    @Mapping(target = "password", ignore = true)
    AdminDto mapToDTO(Admin admin);
}
