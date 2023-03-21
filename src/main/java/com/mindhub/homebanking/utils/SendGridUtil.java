package com.mindhub.homebanking.utils;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridUtil {

    @Value("${sendgrid.apikey}")
    private String sendGridApiKey;

    @Value("${sendgrid.email}")
    private String email;

    Request request = new Request();

    public void sendEmailConfirmation(String to, String token) throws IOException {

        SendGrid sg = new SendGrid(sendGridApiKey);

        Content content = new Content("text/html", this.buildEmail(token));

        Mail mail = new Mail(new Email(email), "Confirm email", new Email(to), content);

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (
                IOException ex) {
            throw ex;
        }

    }

    public String buildEmail(String token){

        return
                "<div style='background-color: #2C3E50; color: white;'>"+
                        "<h1 style=\"padding: 10px; margin: 0; text-align: center\">Welcome to HB banking</h1>" +
                "<div style='font-size: 20px'>"+
                "<div style=\"margin: 0; text-align: center\">"+
                "<p>We are happy to receive you</p>" +
                "<p>Please confirm your email to continue registration. Click this <a style='color: white;' href='http://localhost:8080/web/index.html?token="+token+"'>LINK</a></p>"+
                        "<p>The link is valid for the next 24 hours</p>"+
                        "</div>"+
                "</div>"+
                "<img   style='width: 100%; margin: 0;'" +
                        "src='https://www.travelandleisure.com/thmb/LmeI6B9xXEr3XWCB4MHtYcF1-8I=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/woman-walking-looking-phone-STAYTOOL0522-705ddb14ac4047c0a7039df98319977c.jpg'></img>"+
                "</div>";
    }

}
