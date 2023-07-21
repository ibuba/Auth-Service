package Kabbe.org.AuthService.securityconfig;

import Kabbe.org.AuthService.entity.User;
import Kabbe.org.AuthService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final  UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(username);
        return user.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
    }
}
