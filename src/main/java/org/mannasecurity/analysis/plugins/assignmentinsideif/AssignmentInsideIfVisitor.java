package org.mannasecurity.analysis.plugins.assignmentinsideif;

import java.util.HashSet;
import java.util.Set;
import org.mannasecurity.analysis.plugins.assignmentinsideif.generated.Java8BaseVisitor;
import org.mannasecurity.analysis.plugins.assignmentinsideif.generated.Java8Parser;
import org.mannasecurity.domain.BlockDiff;
import org.mannasecurity.domain.Vulnerability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jtmelton on 7/6/17.
 */
public class AssignmentInsideIfVisitor extends Java8BaseVisitor<Void> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Set<BlockDiff> blockDiffs = new HashSet<>();

    private StringBuffer sb = new StringBuffer();
    private int depth = 0;
    private int blockDepth = 0;

    public Set<BlockDiff> getBlockDiffs() {
        return blockDiffs;
    }

    @Override
    public Void visitMethodBody(Java8Parser.MethodBodyContext ctx) {
        Void result = visitChildren(ctx);
        log.debug(sb.toString());
        return result;
    }

    @Override
    public Void visitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        depth++;
        blockDepth = 0;
        Void result = visitChildren(ctx);
        depth--;
        return result;
    }

    @Override
    public Void visitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        depth++;
        blockDepth = 0;
        Void result = visitChildren(ctx);
        depth--;
        return result;
    }

    @Override
    public Void visitBlock(Java8Parser.BlockContext ctx) {
        blockDepth++;
        Void result = visitChildren(ctx);
        blockDepth--;
        return result;
    }

    @Override
    public Void visitAssignmentOperator(Java8Parser.AssignmentOperatorContext ctx) {
        int startLine = ctx.getStart().getLine();
        int startColumn = ctx.getStart().getCharPositionInLine();
        int endLine = ctx.getStop().getLine();
        int charDiff = ctx.getStop().getStopIndex() - ctx.getStart().getStartIndex() + 1;
        int endColumn = startColumn + charDiff;

        String pos = "'" + ctx.getText() + "' [" + startLine + "(" + startColumn + ")" + endLine +
                     "(" +
                     endColumn +
                     ")]";

        if(depth > 0 && blockDepth == 0) {
            log("found assignment inside if (" + depth + ")(" + blockDepth + "): " + pos);

            blockDiffs.add(
                new BlockDiff()
                .setVulnerability(new Vulnerability("Assignment Instead of Comparison"))
                .setStartLine(startLine)
                .setEndLine(endLine)
                .setStartColumn(startColumn)
                .setEndColumn(endColumn)
                .setOldContent("=")
                .setNewContent("==")
            );
        }

        return visitChildren(ctx);
    }

    @Override
    public Void visitLiteral(Java8Parser.LiteralContext ctx) {
        return visitChildren(ctx);
    }

    void log(String value) {
        sb.append(value + "\n");
    }

}
