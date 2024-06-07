package com.mcp.crispy.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:email.properties")
public class EmailService {

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendVerificationEmail(String to, String verificationCode) {
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);

        String processHtml = templateEngine.process("verificationEmail", context);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject("회원가입 인증코드");
            helper.setText(processHtml, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("이메일 발송 실패: {}", ex.getMessage());
        }
    }

    @Async
    public void sendTempPasswordEmail(String to, String tempPassword) {
        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);

        String processHtml = templateEngine.process("temporaryPasswordEmail", context);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject("회원가입 임시 비밀번호 안내");
            helper.setText(processHtml, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            log.error("임시 비밀번호 이메일 발송 실패: {}", ex.getMessage());
        }
    }
}
