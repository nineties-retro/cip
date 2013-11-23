package cip.byteinputlexer;

/**
 * <code>DefaultDirectiveBuilders</code> XXX
 */
public class DefaultDirectiveBuilders implements DirectiveBuilders {
    final private IncludeBuilder includeBuilder;
    final private LineBuilder lineBuilder;
    final private DirectiveBuilder pragmaBuilder;

    public DefaultDirectiveBuilders() {
        includeBuilder = new DefaultIncludeBuilder();
        lineBuilder = new DefaultLineBuilder();
        pragmaBuilder = new DefaultPragmaBuilder();
    }

    public IncludeBuilder getIncludeBuilder() {
        return includeBuilder;
    }

    public LineBuilder getLineBuilder() {
        return lineBuilder;
    }

    public DirectiveBuilder getPragmaBuilder() {
        return pragmaBuilder;
    }
}
