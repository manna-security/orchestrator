package org.mannasecurity.kafka.listeners;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.utils.Bytes;
import org.mannasecurity.domain.ProjectMetadata;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Created by jtmelton on 4/4/17.
 */
@EnableKafka
@Component
public class ScanRequestListener {

    private AtomicInteger count = new AtomicInteger();

    @KafkaListener(id = "ScanRequestListener", topics = "SCAN_REQUEST", group =
        "orchestrator-group")
    public void listen(ConsumerRecord<ProjectMetadata, Bytes> record) {
        System.out.println("received a ScanRequestListener record: " + record);

        count.incrementAndGet();
    }

    public Integer count() {
        return count.get();
    }

}
