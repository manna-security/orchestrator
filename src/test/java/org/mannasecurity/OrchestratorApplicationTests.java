package org.mannasecurity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mannasecurity.domain.TaskRequest;
import org.mannasecurity.processing.ScanRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrchestratorApplicationTests {

    static RedisServer redisServer;

    @Autowired
    RedisTemplate<String, TaskRequest> template;

    @Autowired
    ScanRequestProcessor scanRequestProcessor;

    @BeforeClass
    public static void setUp() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @AfterClass
    public static void tearDown() {
        redisServer.stop();
    }

    @Test
    public void testSingleScan() throws Exception {
//        byte[] bytes = Files.readAllBytes(
//            Paths.get(ClassLoader.getSystemResource
//                ("analysis/assignment-instead-of-comparison.tar.gz").toURI()));
//
//        TaskRequest taskRequest = new TaskRequest()
//            .setProjectMetadata(
//                new ProjectMetadata()
//                    .setGuid(UUID.randomUUID().toString())
//                    .setGitRepoUrl("some git repo")
//                    .setTimestamp(Instant.now())
//            )
//            .setContent(bytes);
//
//        template.opsForList().leftPush(Channel.SCAN_REQUEST.toString(), taskRequest);
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        assertEquals(1, scanRequestProcessor.getRequestsProcessed());
    }

}
