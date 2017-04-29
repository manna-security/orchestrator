package org.mannasecurity.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.mannasecurity.domain.ProjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Created by jtmelton on 3/18/17.
 */
@Configuration
@EnableKafka
public class KafkaProducerConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${kafka.brokerAddress}")
    private String brokerAddress;

    @Bean
    public KafkaTemplate<ProjectMetadata, Bytes> bytesTemplate() {
        return new KafkaTemplate<>(bytesProducerFactory(), true);
    }

    @Bean
    public ProducerFactory<ProjectMetadata, Bytes> bytesProducerFactory() {
        return new DefaultKafkaProducerFactory<>(bytesProducerConfigs());
    }

    public Map<String, Object> bytesProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        log.debug("producer broker address was " + brokerAddress);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, BytesSerializer.class);
        return props;
    }

}
