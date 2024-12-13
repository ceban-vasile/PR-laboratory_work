package org.example.Laboratory_work_3;

import org.example.Laboratory_work_3.FTP_Server.Manager_FTPServer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws IOException {
        String host1 = "smtp.gmail.com";
        String port1 = "587";
        String username1 = "ceban.vasea20@gmail.com";
        String password1 = "jdng qmff kijm vinr";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host1);
        props.put("mail.smtp.port", port1);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username1, password1);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("ceban.vasea20@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("ceban.vasea20@gmail.com"));
            message.setSubject("Test Mail");
            message.setText("This is a test email from Java.");

            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        new Manager_FTPServer("localhost", 21, "user", "pass", "processed_data.txt", "/processed_data.txt", "downloaded_data.txt");

    }
}
