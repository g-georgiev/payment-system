package system.payments.poc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import system.payments.poc.dto.UserDTO;
import system.payments.poc.model.UserCredentials;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    UserDTO mapToDTO (UserCredentials userSecurity);
}
