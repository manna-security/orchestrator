package org.mannasecurity.analysis.cloc;

import com.google.common.io.CharStreams;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jtmelton on 7/12/17.
 */
@Component
public class TokeiClocAnalyzer implements ClocAnalyzer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${value.tokei.path:EMPTY}")
    private String tokeiPath;

    @Override
    public String analyze(File sourceDirectory) {
        String response = "";

        try {

            Runtime r = Runtime.getRuntime();
            // TODO: add back  --output json
            String cmd = String.format("%s %s", tokeiPath, sourceDirectory.getAbsolutePath());

            log.info("Executing command: {}", cmd);
            Process p = r.exec(cmd);

            int exitCode = p.waitFor();
            if(exitCode != 0) {
                log.error("Execution of tokei failed with error code '{}'", exitCode);
            }

            String stdout = CharStreams.toString(
                new InputStreamReader(p.getInputStream(),StandardCharsets.UTF_8));

            response = stdout.toString();
        } catch (InterruptedException | IOException e) {
            log.warn("Failure to execute cloc.", e);
        }

        return response;
    }


}
