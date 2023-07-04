package Kabbe.org.AuthService.service;

import Kabbe.org.AuthService.entity.User;

import java.util.Optional;

public interface PasswordResetTokenService {
    public void createPasswordResetTokenForUser(User user, String passwordToken);
    public String validatePasswordResetToken(String theToken);
    public Optional<User> findUserByPasswordToken(String passwordToken);
}
