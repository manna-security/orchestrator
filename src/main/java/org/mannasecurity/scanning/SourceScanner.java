package org.mannasecurity.scanning;

import java.io.File;
import java.util.Optional;
import org.mannasecurity.domain.ProjectDiff;
import org.mannasecurity.domain.ProjectMetadata;

/**
 * Created by jtmelton on 7/2/17.
 */
public interface SourceScanner {

    /**
     * This is the method each scanner/analyzer must override in order to be used in the system.
     *
     * @param metadata metadata file for project
     * @param sourceDirectory may contain a cloc.json (output of cloc on sourceDirectory)
     * @return combined projeject diff
     */
    Optional<ProjectDiff> scan(ProjectMetadata metadata, File sourceDirectory);

}
