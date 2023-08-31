package ru.nklyatyshev.producer.configuration;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
public class AmqConfiguration {

    public static class ExceptionQueueConfiguration {

        @Bean
        public TopicExchange dlqTopic() {
            return new TopicExchange("DLQ.topic");
        }

        @Bean
        public Queue deadLetterQueue() {
            return new Queue("dead-letter-queue");
        }

        @Bean
        public Binding bindDeadLetterQueue(TopicExchange dlqTopic, Queue deadLetterQueue) {
            return BindingBuilder.bind(deadLetterQueue)
                    .to(dlqTopic)
                    .with(deadLetterQueue.getName());
        }
    }

    public static class WorkQueueConfiguration {
        @Bean
        public TopicExchange vmTopic() {
            return new TopicExchange("vm.topic");
        }

        @Bean
        public Queue loggingQueue() {
            return new AnonymousQueue();
        }

        @Bean
        public Queue onlyVmDataQueue() {
            return new AnonymousQueue();
        }

        @Bean
        public Queue createdQueue() {
            return new AnonymousQueue();
        }

        @Bean
        public Queue vmCreatedQueue() {
            return new AnonymousQueue();
        }

        @Bean
        public Queue auditVmQueue(TopicExchange dlqTopic, Queue deadLetterQueue) {
            return QueueBuilder
                    .nonDurable()
                    .deadLetterExchange(dlqTopic.getName())
                    .deadLetterRoutingKey(deadLetterQueue.getName())
                    .build();
        }

        @Bean
        public Queue auditDiskQueue() {
            return new AnonymousQueue();
        }

        @Bean
        public Binding bindVm(TopicExchange vmTopic, Queue onlyVmDataQueue) {
            return BindingBuilder.bind(onlyVmDataQueue)
                    .to(vmTopic)
                    .with("*.vm.*");
        }

        @Bean
        public Binding bindCreated(TopicExchange vmTopic, Queue createdQueue) {
            return BindingBuilder.bind(createdQueue)
                    .to(vmTopic)
                    .with("*.*.created");
        }

        @Bean
        public Binding bindVmPowerOn(TopicExchange vmTopic, Queue vmCreatedQueue) {
            return BindingBuilder.bind(vmCreatedQueue)
                    .to(vmTopic)
                    .with("*.vm.created");
        }

        @Bean
        public Binding bindAuditVm(TopicExchange vmTopic, Queue auditVmQueue) {
            return BindingBuilder.bind(auditVmQueue)
                    .to(vmTopic)
                    .with("audit.vm");
        }

        @Bean
        public Binding bindAuditDisk(TopicExchange vmTopic, Queue auditDiskQueue) {
            return BindingBuilder.bind(auditDiskQueue)
                    .to(vmTopic)
                    .with("audit.disk");
        }

        @Bean
        public Binding bindLog(TopicExchange vmTopic, Queue loggingQueue) {
            return BindingBuilder.bind(loggingQueue)
                    .to(vmTopic)
                    .with("#");
        }
    }

}