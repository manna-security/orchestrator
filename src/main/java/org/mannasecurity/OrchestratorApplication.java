package org.mannasecurity;

import org.mannasecurity.processing.ScanRequestProcessor;
import org.mannasecurity.redis.TaskProcessorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class OrchestratorApplication {

    @Autowired
    ScanRequestProcessor scanRequestProcessor;

    @Autowired
    TaskProcessorManager taskProcessorManager;

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApplication.class, args);
    }

}
