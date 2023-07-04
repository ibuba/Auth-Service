package Kabbe.org.AuthService.controller;

import Kabbe.org.AuthService.dto.*;
import Kabbe.org.AuthService.entity.User;
import Kabbe.org.AuthService.entity.VerificationToken;
import Kabbe.org.AuthService.event.RegistrationCompleteEvent;
import Kabbe.org.AuthService.event.listener.RegistrationCompleteEventListener;
import Kabbe.org.AuthService.exception.UserAlreadyExistsException;
import Kabbe.org.AuthService.repository.UserRepository;
import Kabbe.org.AuthService.repository.VerificationTokenRepository;
import Kabbe.org.AuthService.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/authentications")
@CrossOrigin(origins = "http://localhost:8088")
@RequiredArgsConstructor
public class UserController {
    private final VerificationTokenRepository verificationTokenRepository;

    private  final RestTemplate restTemplate;
    private  final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;
    private final RegistrationCompleteEventListener eventListener;

    // Signing up a new user
    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto, HttpServletRequest request) throws UserAlreadyExistsException {
        User user = mapper.map(userDto, User.class);
        restTemplate.postForLocation("http://localhost:9092/api/v1/users/signup", userDto,  UserDto.class);
        System.out.println("signup method controller ******************************");
        userService.addUser(user);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return  user!=null ? new ResponseEntity<>(user, HttpStatus.CREATED) : new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/verifyEmail")
    public String VerifyEmail(@RequestParam("token") String token){
        VerificationToken theToken = verificationTokenRepository.findByToken(token);
        if(theToken.getUser().isEnabled()){
            return "This account has already been verified, please login";
        }
        String verificationResult = userService.isTokenValid(token);
        if(verificationResult.equalsIgnoreCase("valid")){
            return "Email verified successfully. Now you can login to your account";
        }
        return "Invalid verification token";
    }


    //Signing in a user
    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody AuthRequest authRequest){
        System.out.println("Login method controller **************");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if(authentication.isAuthenticated()){
            String token = userService.generateToken(authRequest.getEmail());
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


    @PostMapping("/requestResetPassword")
    public String resetPasswordRequest(@RequestBody PasswordResetRequest passwordResetRequest, final HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        System.out.println(" reset password request controller *********************");
        Optional<User> user = userService.findByEmail(passwordResetRequest.getEmail());
        System.out.println("reset password request controller ------------------------------");
        String passwordResetUrl = "";
        if(user.isPresent()){
            System.out.println("reset password request controller ====================");
            String passwordResetToken = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user.get(), passwordResetToken);
            passwordResetUrl = passwordResetEmailLink(user.get(), applicationUrl(request), passwordResetToken);

        }
        return passwordResetUrl;
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private String passwordResetEmailLink(User user, String applicationUrl, String passwordResetToken) throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl + "/api/v1/authentications/resetPassword?token=" + passwordResetToken;
        eventListener.sendPasswordResetVerificationEmail(url);
        log.info("Click the link to set your password : {}", url);
        return url;
    }

    @PostMapping("resetPassword")
    public String resetPassword(@RequestBody PasswordResetRequest passwordResetRequest, @RequestParam("token") String passwordResetToken){
        String tokenValidationResult = userService.validatePasswordResetToken(passwordResetToken);
        if(!tokenValidationResult.equalsIgnoreCase("valid")){
            return "Invalid password reset token";
        }
        User user = userService.findUserByPasswordToken(passwordResetToken);
        if(user!= null){
            userService.resetUserPassword(user, passwordResetRequest.getNewPassword());
            return "Password has been reset successfully";
        }
        return "Invalid reset password token";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordResetRequest passwordResetRequest){
        User user = userService.findByEmail(passwordResetRequest.getEmail()).get();
        if(!userService.isOldPassword(user, passwordResetRequest.getOldPassword())){
            return "Wrong old password";
        }
        userService.resetUserPassword(user, passwordResetRequest.getNewPassword());
        return "Password changed successfully";
    }

}

