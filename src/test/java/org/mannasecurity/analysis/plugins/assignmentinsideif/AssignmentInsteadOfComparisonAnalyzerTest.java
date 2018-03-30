package org.mannasecurity.analysis.plugins.assignmentinsideif;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import org.mannasecurity.compression.Tarball;
import org.mannasecurity.domain.ProjectMetadata;

public class AssignmentInsteadOfComparisonAnalyzerTest {

    @Test
    public void testScan() throws Exception {
        AssignmentInsteadOfComparisonAnalyzer analyzer = new
            AssignmentInsteadOfComparisonAnalyzer();

        Path tempDir = Files.createTempDirectory("manna_tmp_dir");

        Tarball.extract(
            Files.readAllBytes(
                Paths.get("src/test/resources/analysis/assignment-instead-of-comparison.tar.gz")),
            tempDir.toFile());

        analyzer.scan(new ProjectMetadata(), tempDir.toFile());
    }

}
