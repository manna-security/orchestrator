package org.mannasecurity.processing;

import org.mannasecurity.domain.TaskRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by jtmelton on 6/30/17.
 */
@Component
public class ScanRequestProcessor implements TaskProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void process(TaskRequest request) {
        log.info("Received request in scan request processor");
    }

}
