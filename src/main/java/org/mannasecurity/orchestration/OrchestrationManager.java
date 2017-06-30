package org.mannasecurity.orchestration;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.mannasecurity.processing.ScanRequestProcessor;
import org.mannasecurity.processing.TaskProcessor;
import org.mannasecurity.redis.Channel;
import org.mannasecurity.redis.TaskProcessorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jtmelton on 6/30/17.
 */
@Component
public class OrchestrationManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ScanRequestProcessor scanRequestProcessor;

    private final TaskProcessorManager taskProcessorManager;

    @Autowired
    public OrchestrationManager(final ScanRequestProcessor scanRequestProcessor,
                                final TaskProcessorManager taskProcessorManager) {
        this.scanRequestProcessor = scanRequestProcessor;
        this.taskProcessorManager = taskProcessorManager;
    }

    @PostConstruct
    public void initialize() {
        Map<String, TaskProcessor> processorMap = new HashMap<>();
        processorMap.put(Channel.SCAN_REQUEST.toString(), scanRequestProcessor);

        taskProcessorManager.setChannelProcessorMap(processorMap);

        taskProcessorManager.start();
        System.err.println("************");
        System.err.println("************");
        System.err.println("started orchestration manager");
        log.debug("Stopped orchestration manager.");
        System.err.println("************");
        System.err.println("************");
    }

    @PreDestroy
    public void shutdown() {
        taskProcessorManager.stop();
        System.err.println("************");
        System.err.println("************");
        System.err.println("stopped orchestration manager");
        log.debug("Stopped orchestration manager.");
        System.err.println("************");
        System.err.println("************");
    }
}
