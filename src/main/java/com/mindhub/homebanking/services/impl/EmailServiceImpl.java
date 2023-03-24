package com.mindhub.homebanking.services.impl;

import com.mindhub.homebanking.models.EmailDetails;
import com.mindhub.homebanking.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendSimpleMail(EmailDetails details) {
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    @Override
    public String sendMailToken(String email, String token) {
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachments to
            // be send
            mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText("probando", this.buildEmail(token));
            mimeMessageHelper.setSubject("Confirm email");

            // Adding the attachment
       /*     FileSystemResource file
                    = new FileSystemResource(
                    new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(
                    file.getFilename(), file);*/

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }

        // Catch block to handle MessagingException
        catch (MessagingException e) {

            // Display message when exception occurred
            return "Error while sending mail!!!";
        }
    }

    public String buildEmail(String token){

        return
                "<div style='background-color: #2C3E50; color: white;'>"+
                        "<h1 style=\"padding: 10px; margin: 0; text-align: center\">Welcome to HB banking</h1>" +
                        "<div style='font-size: 20px'>"+
                        "<div style=\"margin: 0; text-align: center\">"+
                        "<p>We are happy to receive you</p>" +
                        "<p>Please confirm your email to continue registration. Click this <a style='color: white;' href='https://mindhub-huborange.up.railway.app/web/index.html?token="+token+"'>LINK</a></p>"+
                        "<p>The link is valid for the next 24 hours</p>"+
                        "</div>"+
                        "</div>"+
                        "<img   style='width: 100%; margin: 0;'" +
                        "src='https://www.travelandleisure.com/thmb/LmeI6B9xXEr3XWCB4MHtYcF1-8I=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/woman-walking-looking-phone-STAYTOOL0522-705ddb14ac4047c0a7039df98319977c.jpg'></img>"+
                        "</div>";
    }

}
