package com.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TASK_REMINDER_QUEUE = "reminder.queue";
    public static final String TASK_REMINDER_EXCHANGE = "reminder.exchange";
    public static final String TASK_REMINDER_ROUTING_KEY = "reminder.key";

    @Bean
    public Queue taskReminderQueue() {
        return new Queue(TASK_REMINDER_QUEUE);
    }

    @Bean
    public DirectExchange taskReminderExchange() {
        return new DirectExchange(TASK_REMINDER_EXCHANGE);
    }

    @Bean
    public Binding taskReminderBinding(Queue taskReminderQueue, DirectExchange taskReminderExchange) {
        return BindingBuilder.bind(taskReminderQueue)
                .to(taskReminderExchange)
                .with(TASK_REMINDER_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
