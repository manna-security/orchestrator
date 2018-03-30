package org.mannasecurity.analysis.plugins.assignmentinsideif;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.mannasecurity.analysis.plugins.assignmentinsideif.generated.Java8Lexer;
import org.mannasecurity.analysis.plugins.assignmentinsideif.generated.Java8Parser;
import org.mannasecurity.domain.FileDiff;
import org.mannasecurity.domain.ProjectDiff;
import org.mannasecurity.domain.ProjectMetadata;
import org.mannasecurity.scanning.SourceScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by jtmelton on 7/6/17.
 */
@Component
public class AssignmentInsteadOfComparisonAnalyzer implements SourceScanner {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Optional<ProjectDiff> scan(ProjectMetadata metadata, File sourceDirectory) {
        ProjectDiff projectDiff = new ProjectDiff();

        String sourcePath = sourceDirectory.getAbsolutePath();

        final Collection<Path> paths = new ArrayList<>();

        try {

            Files.walk(Paths.get(sourcePath))
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().endsWith(".java"))
                .forEach(paths::add);

            for(Path path : paths) {
                Optional<FileDiff> fileDiff = scan(path);
                if(fileDiff.isPresent()) {
                    String absolutePath = fileDiff.get().getAbsolutePath();
                    String base = sourcePath;
                    String relative = new File(base).toURI().relativize(
                        new File(absolutePath).toURI()).getPath();

                    projectDiff.addFileDiff(fileDiff.get().setRelativePath(relative));
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("Failure scanning tarball.", e);
        }

        if(projectDiff.getFileDiffs().size() > 0) {
            return Optional.of(projectDiff);
        } else {
            return Optional.empty();
        }
    }

    private Optional<FileDiff> scan(Path path) throws IOException {
        String filename = path.toAbsolutePath().toString();

        Lexer lexer = new Java8Lexer(new ANTLRFileStream(filename));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill(); // load all and check time

        // Create a parser that reads from the scanner
        Java8Parser parser = new Java8Parser(tokens);

        // start parsing at the compilationUnit rule
        Java8Parser.CompilationUnitContext compilationUnit = parser.compilationUnit();

        AssignmentInsideIfVisitor visitor = new AssignmentInsideIfVisitor();

        compilationUnit.accept(visitor);

        if(visitor.getBlockDiffs().size() > 0) {
            return Optional.of(new FileDiff(filename, visitor.getBlockDiffs()));
        } else {
            return Optional.empty();
        }
    }

}
