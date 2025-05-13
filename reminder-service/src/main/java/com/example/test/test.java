package com.example.test;

import com.example.ReminderServiceApplication;
import com.example.model.Notification;
import com.example.service.NotificationFactory;
import com.example.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ReminderServiceApplication.class)
class NotificationSystemTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationFactory notificationFactory;

    @Autowired
    private ApplicationContext ctx;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        reset(mailSender);
    }

    @Test
    void emailNotification_sendsMailMessage() {
        String to = "user@example.com";
        String body = "Test email body";

        notificationService.notify("Email", to, body);

        ArgumentCaptor<SimpleMailMessage> cap = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(cap.capture());

        SimpleMailMessage sent = cap.getValue();
        assertThat(sent.getTo()).containsExactly(to);
        assertThat(sent.getText()).isEqualTo(body);
        assertThat(sent.getFrom()).isEqualTo("notifications@todoist-replica.com");
        assertThat(sent.getSubject()).isEqualTo("Notification from YourApp");
    }

    @Test
    void pushNotification_doesNotThrow() {
        assertThatCode(() ->
                notificationService.notify("Push", "token123", "Push payload")
        ).doesNotThrowAnyException();
    }

    @Test
    void consoleNotification_doesNotThrow() {
        assertThatCode(() ->
                notificationService.notify("Console", "admin", "Console log")
        ).doesNotThrowAnyException();
    }

    @Test
    void factory_returnsCorrectBeans_andUnknownTypeFails() {
        Notification email = notificationFactory.getNotification("Email");
        Notification push  = notificationFactory.getNotification("Push");
        Notification console = notificationFactory.getNotification("Console");

        assertThat(email).isNotNull();
        assertThat(push).isNotNull();
        assertThat(console).isNotNull();

        assertThatThrownBy(() ->
                notificationFactory.getNotification("SMS")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown notification type");
    }

    @Test
    void service_delegatesToFactoryStrategy() {
        Notification dummy = mock(Notification.class);
        NotificationFactory spyFactory = spy(notificationFactory);
        NotificationService svc = new NotificationService(spyFactory);

        when(spyFactory.getNotification("Email")).thenReturn(dummy);
        svc.notify("Email", "x@x.com", "Hello!");

        verify(spyFactory).getNotification("Email");
        verify(dummy).send("x@x.com", "Hello!");
    }

    @Test
    void contextLoads_rabbitTemplateIsAvailable() {
        assertThat(rabbitTemplate).isNotNull();
    }
}
