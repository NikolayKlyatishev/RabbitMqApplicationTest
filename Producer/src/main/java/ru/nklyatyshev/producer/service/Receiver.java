package ru.nklyatyshev.producer.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    @RabbitListener(queues = "#{loggingQueue.name}")
    public void receive1(String in) throws InterruptedException {
        receive(in, "loggingQueue");
    }

    @RabbitListener(queues = "#{onlyVmDataQueue.name}")
    public void receive2(String in) throws InterruptedException {
        receive(in, "onlyVmDataQueue");
    }

    @RabbitListener(queues = "#{createdQueue.name}")
    public void receive3(String in) throws InterruptedException {
        receive(in, "createdQueue");
    }

    @RabbitListener(queues = "#{vmCreatedQueue.name}")
    public void receive4(String in) throws InterruptedException {
        receive(in, "vmCreatedQueue");
    }

    @RabbitListener(queues = "#{auditVmQueue.name}", ackMode = "")
    public void receive5(String in) throws InterruptedException {
        receive(in, "auditVmQueue");
//        throw new RuntimeException("MESSAGE IS DEAD!");
    }

    @RabbitListener(queues = "#{auditDiskQueue.name}")
    public void receive6(String in) throws InterruptedException {
        receive(in, "auditDiskQueue");
    }

    @RabbitListener(queues = "#{deadLetterQueue.name}")
    public void receiveException(String in) throws InterruptedException {
        receive(in, "deadLetterQueue");
    }

    public void receive(String in, String receiver) throws InterruptedException {
        System.out.println("instance " + receiver + " [x] Received '" + in + "'");
        doWork(in);
    }

    private void doWork(String in) throws InterruptedException {
        for (char ch : in.toCharArray()) {
            if (ch == '.') {
                Thread.sleep(1000);
            }
        }
    }
}
