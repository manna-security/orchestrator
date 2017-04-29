package org.mannasecurity.kafka;

/**
 * Created by jtmelton on 3/18/17.
 */

import static org.assertj.core.api.Assertions.assertThat;

import org.mannasecurity.domain.ProjectMetadata;
import org.mannasecurity.kafka.listeners.CloneRequestListener;
import org.mannasecurity.kafka.listeners.ScanRequestListener;
import org.mannasecurity.kafka.listeners.VerifyResultsListener;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.utils.Bytes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author jtmelton
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BasicListenerTest {

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(
        1,
        true,
        Topic.CLONE_REQUEST.toString(),
        Topic.SCAN_REQUEST.toString(),
        Topic.VERIFY_RESULTS.toString()
    );

    @Autowired
    KafkaProducerConfig producerConfig;

    @Autowired
    KafkaTemplate<ProjectMetadata, Bytes> template;

    @Autowired
    CloneRequestListener cloneRequestListener;

    @Autowired
    ScanRequestListener scanRequestListener;

    @Autowired
    VerifyResultsListener verifyResultsListener;

    @BeforeClass
    public static void setUp() throws Exception {

        String kafkaBootstrapServers = embeddedKafka.getBrokersAsString();
        System.err.println("kafkaServers : " + kafkaBootstrapServers);
        // override the property in application.properties
        System.setProperty("kafka.brokerAddress", kafkaBootstrapServers);
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testTemplate() throws Exception {

        ProjectMetadata pm = new ProjectMetadata();

        template.send(Topic.CLONE_REQUEST.toString(), pm, bts("aaa"));
        template.send(Topic.CLONE_REQUEST.toString(), pm, bts("bbb"));
        template.send(Topic.CLONE_REQUEST.toString(), pm, bts("ccc"));
        template.send(Topic.CLONE_REQUEST.toString(), pm, bts("ddd"));
        template.send(Topic.CLONE_REQUEST.toString(), pm, bts("eee"));
        template.send(Topic.CLONE_REQUEST.toString(), pm, bts("fff"));
        template.send(Topic.CLONE_REQUEST.toString(), pm, bts("ggg"));

        template.send(Topic.SCAN_REQUEST.toString(), pm, bts("hhh"));
        template.send(Topic.SCAN_REQUEST.toString(), pm, bts("iii"));
        template.send(Topic.SCAN_REQUEST.toString(), pm, bts("jjj"));
        template.send(Topic.SCAN_REQUEST.toString(), pm, bts("kkk"));

        template.send(Topic.VERIFY_RESULTS.toString(), pm, bts("lll"));
        template.send(Topic.VERIFY_RESULTS.toString(), pm, bts("mmm"));
        template.send(Topic.VERIFY_RESULTS.toString(), pm, bts("nnn"));

        if(producerConfig.bytesProducerFactory() instanceof DefaultKafkaProducerFactory) {
            ((DefaultKafkaProducerFactory)producerConfig.bytesProducerFactory()).destroy();
        }

        Thread.sleep(5000);

        assertThat(cloneRequestListener.count()).isEqualTo(7);
        assertThat(scanRequestListener.count()).isEqualTo(4);
        assertThat(verifyResultsListener.count()).isEqualTo(3);
    }

    private Bytes bts(String text) {
        return new Bytes(text.getBytes(StandardCharsets.UTF_8));
    }

}
