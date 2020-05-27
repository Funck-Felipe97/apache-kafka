package com.learnkafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class KafkaEventsConsumerManualOffset implements AcknowledgingMessageListener<Integer, String> {

    @KafkaListener(topics = {"library-events"})
    @Override
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("Consumer record: {} ", consumerRecord);
        acknowledgment.acknowledge();
    }

}
