package com.daymemory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReminderEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("noreply@daymemory.com");

            mailSender.send(message);
            log.info("Reminder email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public String buildReminderEmailContent(String eventTitle, int daysRemaining) {
        return String.format(
                "안녕하세요!\n\n" +
                "'%s' 행사가 %d일 남았습니다.\n\n" +
                "준비하시는데 도움이 되시길 바랍니다.\n\n" +
                "Day Memory 팀 드림",
                eventTitle,
                daysRemaining
        );
    }
}
