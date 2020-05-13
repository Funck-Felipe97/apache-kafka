package com.learnkafka.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class LIbraryEventCallback implements ListenableFutureCallback<SendResult<Integer, String>> {

    @Override
    public void onFailure(Throwable throwable) {
        log.error("Fail on send message: " + throwable.getMessage());
    }

    @Override
    public void onSuccess(SendResult<Integer, String> sendResult) {
        log.info("Message sent successfully for partition: ", sendResult.getProducerRecord().partition());
    }
}
