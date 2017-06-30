package org.mannasecurity;

import org.mannasecurity.processing.ScanRequestProcessor;
import org.mannasecurity.redis.TaskProcessorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrchestratorApplication {

    @Autowired
    ScanRequestProcessor scanRequestProcessor;

    @Autowired
    TaskProcessorManager taskProcessorManager;

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApplication.class, args);
    }

}
