package Kabbe.org.AuthService.dto;

import Kabbe.org.AuthService.entity.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.ws.rs.DefaultValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String firstName;
    private String lastName;
    private Integer age;
    private String email;
    private String password;
    @OneToMany(cascade = CascadeType.ALL)
    @DefaultValue(value = "USER")
    private List<Role> roles;

}
