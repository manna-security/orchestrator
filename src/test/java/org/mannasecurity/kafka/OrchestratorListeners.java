package org.mannasecurity.kafka;

import org.mannasecurity.kafka.listeners.CloneRequestListener;
import org.mannasecurity.kafka.listeners.ScanRequestListener;
import org.mannasecurity.kafka.listeners.VerifyResultsListener;
import org.springframework.context.annotation.Bean;

/**
 * Created by jtmelton on 4/4/17.
 */
public class OrchestratorListeners {

    @Bean
    public CloneRequestListener cloneRequestListener() {
        return new CloneRequestListener();
    }

    @Bean
    public ScanRequestListener scanRequestListener() {
        return new ScanRequestListener();
    }

    @Bean
    public VerifyResultsListener verifyResultsListener() {
        return new VerifyResultsListener();
    }
}
