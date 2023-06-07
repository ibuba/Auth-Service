package Kabbe.org.AuthService.controller;

import Kabbe.org.AuthService.dto.AuthRequest;
import Kabbe.org.AuthService.entity.User;
import Kabbe.org.AuthService.repository.UserRepository;
import Kabbe.org.AuthService.service.UserService;
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
@RequestMapping("/authentication")
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public User addUser(@RequestBody User user){
//        restTemplate.postForLocation("http://localhost:9092/users/signup", User.class);
        return userService.addUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if(authentication.isAuthenticated()){
        String token = userService.generateToken(authRequest.getUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", token);
        //visit home page which accepts headers
        return new ResponseEntity<>("Welcome!! ",headers, HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException("username");
        }
    }

    public ResponseEntity<String> responseEntityBuilderAndHttpHeaders(String token) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("token", token);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Response with header using ResponseEntity");
    }

    @PutMapping("/reset")
    public ResponseEntity<User> updatePassword(@RequestParam String email){
        User user = userRepository.findUserByEmail(email).orElse(null);
        if(user == null){
            System.out.println("You are not registered, please register");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else{
            user = userService.addUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam String token){
        userService.validateToken(token);
        return "Token is valid";
    }

}
