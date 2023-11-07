package com.example.Util;


import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

    @Resource
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;


    /**
     * @param to
     * @param subject
     * @param content
     * @throws MessagingException
     */
    public void sendMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        System.out.println(from);
        helper.setFrom(from);
        helper.setTo(to);
        //设置主题
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(helper.getMimeMessage());
    }


}
