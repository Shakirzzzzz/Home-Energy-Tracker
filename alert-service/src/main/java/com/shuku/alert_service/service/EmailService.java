package com.shuku.alert_service.service;

import com.shuku.alert_service.entity.Alert;
import com.shuku.alert_service.repository.AlertRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final AlertRepository alertRepository;

    public EmailService(JavaMailSender javaMailSender,AlertRepository alertRepository){
        this.javaMailSender=javaMailSender;
        this.alertRepository=alertRepository;
    }

    public void sendEmail(String to,String subject,String body,Long userId){
        log.info("Sending email to: {}, subject: {}",to,subject);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("noreply@shuku.com");
        message.setSubject(subject);
        message.setText(body);

        try{
            javaMailSender.send(message);
            final Alert alert = Alert.builder()
                    .sent(true)
                    .createdAt(LocalDateTime.now())
                    .userId(userId)
                    .build();
            //save it on db
            alertRepository.saveAndFlush(alert);
        }catch (MailException exe){
            log.error("Failed to send email to: {}",to,exe);
            //save it on db
            final Alert alert = Alert.builder()
                    .sent(false)
                    .createdAt(LocalDateTime.now())
                    .userId(userId)
                    .build();
            alertRepository.saveAndFlush(alert);

        }

        log.info("Email sent to: {}",to);

    }

}
