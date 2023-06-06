package Kabbe.org.AuthService.service;

import Kabbe.org.AuthService.entity.User;

public interface UserService {

    public User addUser(User user);

    public String generateToken(String username);
    public void validateToken(String token);
}
