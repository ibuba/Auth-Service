package Kabbe.org.AuthService.service;

import Kabbe.org.AuthService.dto.PasswordResetRequest;
import Kabbe.org.AuthService.entity.User;
import Kabbe.org.AuthService.exception.UserAlreadyExistsException;

import java.util.Optional;

public interface UserService {

    public User addUser(User user) throws UserAlreadyExistsException;
    public User updatePassword(String email);
    public String generateToken(String username);

    Optional<User> findByEmail(String email);

    void createPasswordResetTokenForUser(User user, String passwordResetToken);

    String validatePasswordResetToken(String passwordResetToken);

    User findUserByPasswordToken(String passwordResetToken);

    void resetUserPassword(User user, String newPassword);
    boolean isOldPassword(User user, String oldPassword);

    void saveUserVerificationToken(User theUser, String verificationToken);

    String isTokenValid(String token);
}
