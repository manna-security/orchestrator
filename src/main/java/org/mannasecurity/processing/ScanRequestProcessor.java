package org.mannasecurity.processing;

import static org.mannasecurity.domain.ProjectMetadata.renew;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.mannasecurity.analysis.cloc.ClocAnalyzer;
import org.mannasecurity.compression.Tarball;
import org.mannasecurity.domain.ProjectDiff;
import org.mannasecurity.domain.TaskRequest;
import org.mannasecurity.redis.Channel;
import org.mannasecurity.scanning.SourceScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

/**
 * Created by jtmelton on 6/30/17.
 */
@Component
public class ScanRequestProcessor implements TaskProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Set<SourceScanner> scanners;

    private ClocAnalyzer clocAnalyzer;

    private AtomicInteger requestsProcessed = new AtomicInteger();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    RedisTemplate<String, TaskRequest> template;

    @Autowired
    public void setScanners(Set<SourceScanner> scanners) {
        this.scanners = scanners;
    }

    @Autowired
    public void setClocAnalyzer(ClocAnalyzer clocAnalyzer) {
        this.clocAnalyzer = clocAnalyzer;
    }

    @Override
    public void process(TaskRequest request) {
        String guid = request.getProjectMetadata().getGuid();

        log.info("Received request in scan request processor covering project {} for url {}",
            guid, request.getProjectMetadata().getGitRepoUrl());

        ProjectDiff allDiff = new ProjectDiff();
        Path tempDir;

        try {
            log.info("Creating temporary directory to store files for guid: {}", guid);
            tempDir = Files.createTempDirectory("manna_tmp_dir");

            log.info("Extracting tarball for guid: {} into {}", guid, tempDir);
            Tarball.extract(request.getContent(), tempDir.toFile());

            log.info("Analyzing CLOC for guid: {}", guid);
            String cloc = clocAnalyzer.analyze(tempDir.toFile());

            log.info("Writing CLOC for guid: {}", guid);
            // write cloc file out so it can be used by others
            Files.write(
                Paths.get(tempDir.toFile().getAbsolutePath(), "cloc.json"),
                cloc.getBytes(StandardCharsets.UTF_8));

            for (SourceScanner scanner : scanners) {
                log.info("Executing scanner {} for guid {}", scanner.getClass().getName(), guid);

                Optional<ProjectDiff> singleDiff = scanner.scan(
                    request.getProjectMetadata(),
                    tempDir.toFile());

                if(singleDiff.isPresent()) {
                    log.info("Got back a project diff: " + singleDiff.get());
                    allDiff = allDiff.merge(singleDiff.get());
                }
            }

            log.info("Cleaning up temporary files for guid: {} from tempDir: {}", guid, tempDir);
            FileSystemUtils.deleteRecursively(tempDir);

            if(! allDiff.requiresChanges()) {
                log.info("Scan for project guid {} does not require any changes - url {}",
                    guid, request.getProjectMetadata().getGitRepoUrl());

                // stop processing
                return;
            }

            String diffJson = objectMapper.writeValueAsString(allDiff);

            TaskRequest taskRequest = new TaskRequest()
                .setProjectMetadata(renew(request.getProjectMetadata()))
                .setContent(diffJson.getBytes(StandardCharsets.UTF_8));

            template.opsForList().leftPush(Channel.VERIFY_RESULTS.toString(), taskRequest);

            requestsProcessed.incrementAndGet();
        } catch (IOException e) {
            log.error("Error processing clone request.", e);
        }
    }

    public int getRequestsProcessed() {
        return requestsProcessed.get();
    }

}
