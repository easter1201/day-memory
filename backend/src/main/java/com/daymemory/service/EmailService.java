package com.daymemory.service;

import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReminderEmail(String to, String subject, String text) {
        try {
            // HTML 이메일로 발송
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true = HTML
            helper.setFrom("noreply@daymemory.com");

            mailSender.send(mimeMessage);
            log.info("Reminder email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to create email message to: {}", to, e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public String buildReminderEmailContent(String eventTitle, int daysRemaining) {
        return buildHtmlEmailTemplate(eventTitle, daysRemaining);
    }

    private String buildHtmlEmailTemplate(String eventTitle, int daysRemaining) {
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Day Memory - 이벤트 리마인더</title>
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background-color: #f5f5f5;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 10px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                            overflow: hidden;
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            color: white;
                            padding: 30px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                            font-weight: 600;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .event-info {
                            background-color: #f8f9fa;
                            border-left: 4px solid #667eea;
                            padding: 20px;
                            margin: 20px 0;
                            border-radius: 5px;
                        }
                        .event-title {
                            font-size: 20px;
                            font-weight: 600;
                            color: #333;
                            margin-bottom: 10px;
                        }
                        .days-remaining {
                            font-size: 36px;
                            font-weight: 700;
                            color: #667eea;
                            text-align: center;
                            margin: 20px 0;
                        }
                        .days-label {
                            font-size: 16px;
                            color: #666;
                            text-align: center;
                            margin-bottom: 20px;
                        }
                        .message {
                            color: #555;
                            line-height: 1.6;
                            text-align: center;
                            font-size: 16px;
                        }
                        .footer {
                            background-color: #f8f9fa;
                            padding: 20px;
                            text-align: center;
                            color: #999;
                            font-size: 14px;
                        }
                        .footer a {
                            color: #667eea;
                            text-decoration: none;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎉 Day Memory</h1>
                        </div>
                        <div class="content">
                            <div class="event-info">
                                <div class="event-title">%s</div>
                            </div>
                            <div class="days-remaining">D-%d</div>
                            <div class="days-label">%d일 남았습니다</div>
                            <p class="message">
                                소중한 날을 위한 준비는 잘 되어가고 계신가요?<br>
                                특별한 날을 위해 미리 준비하시는 것을 추천드립니다!
                            </p>
                        </div>
                        <div class="footer">
                            <p>이 메일은 Day Memory에서 자동으로 발송되었습니다.</p>
                            <p>
                                <a href="#">알림 설정 변경</a> |
                                <a href="#">Day Memory 방문하기</a>
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(eventTitle, daysRemaining, daysRemaining);
    }
}
