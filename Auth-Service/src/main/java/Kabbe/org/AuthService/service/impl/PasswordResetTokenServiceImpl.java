package Kabbe.org.AuthService.service.impl;

import Kabbe.org.AuthService.entity.PasswordResetToken;
import Kabbe.org.AuthService.entity.User;
import Kabbe.org.AuthService.repository.PasswordResetTokenRepository;
import Kabbe.org.AuthService.service.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public void createPasswordResetTokenForUser(User user, String passwordToken) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(passwordToken, user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String theToken) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(theToken);
        if(token == null){
            return "Invalid password reset token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if(token.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
            return "Link already expired, resend link";
        }
        return "valid";
    }

    @Override
    public Optional<User> findUserByPasswordToken(String passwordToken) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(passwordToken).getUser());
    }
}
