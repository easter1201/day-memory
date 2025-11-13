package com.daymemory.service;

import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService 테스트")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = mock(MimeMessage.class);
    }

    @Test
    @DisplayName("이메일 발송 성공")
    void testSendEmail_Success() {
        // Given
        String to = "test@example.com";
        String subject = "[Day Memory] 테스트 이메일";
        String text = "<html><body>테스트 내용</body></html>";

        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        willDoNothing().given(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendReminderEmail(to, subject, text);

        // Then
        // Verify: MimeMessage 생성 및 발송 확인
        then(mailSender).should(times(1)).createMimeMessage();
        then(mailSender).should(times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 발송 실패 - MessagingException")
    void testSendEmail_Failure_MessagingException() {
        // Given
        String to = "test@example.com";
        String subject = "[Day Memory] 테스트 이메일";
        String text = "<html><body>테스트 내용</body></html>";

        // MimeMessage 생성 시 예외 발생
        given(mailSender.createMimeMessage()).willThrow(new RuntimeException("Messaging error"));

        // When & Then
        assertThatThrownBy(() -> emailService.sendReminderEmail(to, subject, text))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_SEND_FAILED);

        // Verify: send가 호출되지 않아야 함
        then(mailSender).should(never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 발송 실패 - 일반 Exception")
    void testSendEmail_Failure_GeneralException() {
        // Given
        String to = "test@example.com";
        String subject = "[Day Memory] 테스트 이메일";
        String text = "<html><body>테스트 내용</body></html>";

        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        willThrow(new RuntimeException("Mail server error"))
                .given(mailSender).send(any(MimeMessage.class));

        // When & Then
        assertThatThrownBy(() -> emailService.sendReminderEmail(to, subject, text))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_SEND_FAILED);
    }

    @Test
    @DisplayName("리마인더 이메일 템플릿 생성")
    void testSendReminderEmail_Success() {
        // Given
        String to = "user@example.com";
        String eventTitle = "친구 생일";
        int daysRemaining = 7;
        String recipientName = "홍길동";
        String subject = String.format("[Day Memory] '%s' %d일 전 알림", eventTitle, daysRemaining);
        String content = emailService.buildReminderEmailContent(eventTitle, daysRemaining, recipientName);

        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        willDoNothing().given(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendReminderEmail(to, subject, content);

        // Then
        // Verify
        then(mailSender).should(times(1)).createMimeMessage();
        then(mailSender).should(times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("리마인더 이메일 내용 빌드")
    void testBuildReminderEmailContent() {
        // Given
        String eventTitle = "친구 생일";
        int daysRemaining = 7;

        // When
        String emailContent = emailService.buildReminderEmailContent(eventTitle, daysRemaining, "테스트사용자");

        // Then
        assertThat(emailContent).isNotNull();
        assertThat(emailContent).contains(eventTitle);
        assertThat(emailContent).contains("D-" + daysRemaining);
        assertThat(emailContent).contains(daysRemaining + "일 남았습니다");
        assertThat(emailContent).contains("Day Memory");
        assertThat(emailContent).contains("<!DOCTYPE html>");
    }

    @Test
    @DisplayName("리마인더 이메일 내용 빌드 - 다양한 일수")
    void testBuildReminderEmailContent_VariousDays() {
        // Given & When & Then
        // 1일 전
        String content1 = emailService.buildReminderEmailContent("생일", 1);
        assertThat(content1).contains("D-1");
        assertThat(content1).contains("1일 남았습니다");

        // 30일 전
        String content30 = emailService.buildReminderEmailContent("기념일", 30);
        assertThat(content30).contains("D-30");
        assertThat(content30).contains("30일 남았습니다");

        // 100일 전
        String content100 = emailService.buildReminderEmailContent("결혼기념일", 100);
        assertThat(content100).contains("D-100");
        assertThat(content100).contains("100일 남았습니다");
    }

    @Test
    @DisplayName("HTML 이메일 형식 확인")
    void testBuildReminderEmailContent_HtmlFormat() {
        // Given
        String eventTitle = "테스트 이벤트";
        int daysRemaining = 14;

        // When
        String emailContent = emailService.buildReminderEmailContent(eventTitle, daysRemaining, "테스트사용자");

        // Then
        assertThat(emailContent).contains("<!DOCTYPE html>");
        assertThat(emailContent).contains("<html");
        assertThat(emailContent).contains("</html>");
        assertThat(emailContent).contains("<body>");
        assertThat(emailContent).contains("</body>");
        assertThat(emailContent).contains("<style>");
        assertThat(emailContent).contains("</style>");
    }

    @Test
    @DisplayName("인증 이메일 발송 (리마인더 이메일 메서드 재사용)")
    void testSendVerificationEmail() {
        // Given
        String to = "newuser@example.com";
        String subject = "[Day Memory] 이메일 인증";
        String verificationLink = "https://daymemory.com/verify?token=abc123";
        String text = String.format("""
                <html>
                <body>
                    <h1>Day Memory 이메일 인증</h1>
                    <p>아래 링크를 클릭하여 이메일을 인증해주세요.</p>
                    <a href="%s">이메일 인증하기</a>
                </body>
                </html>
                """, verificationLink);

        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        willDoNothing().given(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendReminderEmail(to, subject, text);

        // Then
        // Verify
        then(mailSender).should(times(1)).createMimeMessage();
        then(mailSender).should(times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("이메일 내용에 이벤트 제목 포함 확인")
    void testReminderEmailContent_ContainsEventTitle() {
        // Given
        String eventTitle = "어머니 생신";
        int daysRemaining = 3;

        // When
        String content = emailService.buildReminderEmailContent(eventTitle, daysRemaining);

        // Then
        assertThat(content).contains(eventTitle);
    }

    @Test
    @DisplayName("이메일 내용에 남은 일수 포함 확인")
    void testReminderEmailContent_ContainsDaysRemaining() {
        // Given
        String eventTitle = "프로젝트 마감";
        int daysRemaining = 5;

        // When
        String content = emailService.buildReminderEmailContent(eventTitle, daysRemaining);

        // Then
        assertThat(content).contains("D-" + daysRemaining);
        assertThat(content).contains(daysRemaining + "일 남았습니다");
    }

    @Test
    @DisplayName("이메일 스타일링 확인")
    void testReminderEmailContent_Styling() {
        // Given
        String eventTitle = "테스트 이벤트";
        int daysRemaining = 7;

        // When
        String content = emailService.buildReminderEmailContent(eventTitle, daysRemaining);

        // Then
        // 스타일 요소 확인
        assertThat(content).contains("background-color");
        assertThat(content).contains("gradient");
        assertThat(content).contains("font-family");
        assertThat(content).contains("container");
        assertThat(content).contains("header");
        assertThat(content).contains("content");
        assertThat(content).contains("footer");
    }

    @Test
    @DisplayName("다중 이메일 발송")
    void testSendMultipleEmails() {
        // Given
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        willDoNothing().given(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendReminderEmail("user1@example.com", "제목1", "내용1");
        emailService.sendReminderEmail("user2@example.com", "제목2", "내용2");
        emailService.sendReminderEmail("user3@example.com", "제목3", "내용3");

        // Then
        // Verify: 3번 호출되었는지 확인
        then(mailSender).should(times(3)).createMimeMessage();
        then(mailSender).should(times(3)).send(any(MimeMessage.class));
    }
}
