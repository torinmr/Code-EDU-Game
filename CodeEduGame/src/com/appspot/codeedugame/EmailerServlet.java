package com.appspot.codeedugame;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class EmailerServlet extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String msgBody = req.getParameter("comment");

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("noreply@codeedugame.appspotmail.com", "Code EDU Game"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress("javajackdev@gmail.com", "Miles Dunham"));
            msg.setSubject("New comment from " + name + " at " + email);
            msg.setText(msgBody);
            Transport.send(msg);
            resp.getWriter().print("Thank you for your comments!");

        } catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
