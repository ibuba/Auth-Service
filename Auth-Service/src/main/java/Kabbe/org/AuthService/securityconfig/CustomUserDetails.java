package Kabbe.org.AuthService.securityconfig;

import Kabbe.org.AuthService.entity.Role;
import Kabbe.org.AuthService.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


@Data
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private boolean isEnabled;
    private Set<Role> roles;

    public CustomUserDetails(User user) {
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.isEnabled = user.isEnabled();
        this.roles =  user.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String[] userRoles = getRoles().stream().map((role) -> role.getRoleName()).toArray(String[]::new);
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoles);
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
