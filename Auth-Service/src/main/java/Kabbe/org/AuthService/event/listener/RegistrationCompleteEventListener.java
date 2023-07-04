package Kabbe.org.AuthService.event.listener;

import Kabbe.org.AuthService.entity.User;
import Kabbe.org.AuthService.event.RegistrationCompleteEvent;
import Kabbe.org.AuthService.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;
    private final JavaMailSender mailSender;

    private User theUser;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // 1. get the newly registered user
        theUser = event.getUser();
        // 2. create a verification token for the user
        String verificationToken = UUID.randomUUID().toString();
        // 3. save the verification token for the user
        userService.saveUserVerificationToken(theUser, verificationToken);
        // 4. build the verification url to be sent to the user
        String url = event.getApplicationUrl() + "/api/v1/authentications/verifyEmail?token=" + verificationToken;
        // 5. sent email
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click the link to verify your registration : {}", url);
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, " + theUser.getEmail() + ", </p>" +
                "<p> Thank you for registering with us. Please follow the link below to complete your registration.<p/>" +
                "<a href=\"" + url + "\">Verify your email to activate your account.</a>" +
                "<p> Thank you <br> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("tnsu32@gmail.com", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }

    public void sendPasswordResetVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Password reset request Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, " + theUser.getEmail() + ", </p>" +
                "<p> <b>You recently requested to reset your password,<b> Please follow the link below to complete the action.<p/>" +
                "<a href=\"" + url + "\">Reset Password</a>" +
                "<p>Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("tnsu32@gmail.com", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}

