package org.mannasecurity.analysis.cloc;

import java.io.File;

/**
 * Created by jtmelton on 7/12/17.
 */
public interface ClocAnalyzer {

    public String analyze(File sourceDirectory);

}
