package Kabbe.org.AuthService.controller;

import Kabbe.org.AuthService.dto.AuthRequest;
import Kabbe.org.AuthService.entity.User;
import Kabbe.org.AuthService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:8088")
@RequiredArgsConstructor
public class UserController {

    private  final RestTemplate restTemplate;
    private  final UserService userService;

    private final AuthenticationManager authenticationManager;

    // Signing up a new user
    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@RequestBody User user){
      User  u= userService.addUser(user);
        return  u!=null ? new ResponseEntity(u, HttpStatus.CREATED) : new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    //Signing in a user
    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if(authentication.isAuthenticated()){
        String token = userService.generateToken(authRequest.getUsername());
        return new ResponseEntity<>(token, HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException("username");
        }
    }
    //Validating a token
    @GetMapping("/validate")
    public String validateToken(@RequestParam String token){
        userService.validateToken(token);
        return "Token is valid";
    }

}
