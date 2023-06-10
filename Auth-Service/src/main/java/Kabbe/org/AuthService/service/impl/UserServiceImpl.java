package Kabbe.org.AuthService.service.impl;

import Kabbe.org.AuthService.entity.User;
import Kabbe.org.AuthService.repository.UserRepository;
import Kabbe.org.AuthService.service.UserService;
import Kabbe.org.AuthService.token.Token;
import Kabbe.org.AuthService.token.TokenRepository;
import Kabbe.org.AuthService.token.TokenType;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final  UserRepository userRepository;

    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private  final JwtService jwtService;

    @Override
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(newUser.getUserName());
        Token  token = Token.builder()
                .user(newUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);

       return newUser;
    }

    private void revokeAllUserTokens(User user){

        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getUserId());
        if(validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t  -> { t.setExpired(true);
        t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    public String generateToken(String username){
        return jwtService.generateToken(username);
    }

    public void validateToken(String token){
        jwtService.validateToken(token);
    }
}
