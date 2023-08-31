package ru.nklyatyshev.producer.service;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class Sender {

    private final RabbitTemplate template;
    private final TopicExchange vmTopic;

    AtomicInteger index = new AtomicInteger(0);

    AtomicInteger count = new AtomicInteger(0);

    private final String[] keys = {"audit.vm", "audit.disk", "audit.network",
            "compute.vm.created", "compute.disk.created", "compute.vm.powerOn"};

    public Sender(RabbitTemplate template, @Qualifier(value = "vmTopic") TopicExchange vmTopic) {
        this.template = template;
        this.vmTopic = vmTopic;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        var builder = new StringBuilder("Hello to ");
        if (this.index.incrementAndGet() == keys.length) {
            this.index.set(0);
        }
        var key = keys[this.index.get()];
        builder.append(key).append(' ');
        builder.append(this.count.incrementAndGet());
        var message = builder.toString();
        template.convertAndSend(vmTopic.getName(), key, message);
        System.out.println(" [x] Sent '" + message + "'");
    }
}
