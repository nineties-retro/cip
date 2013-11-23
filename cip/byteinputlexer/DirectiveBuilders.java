package cip.byteinputlexer;

/**
 * <code>DirectiveBuilders</code> ...
 */
public interface DirectiveBuilders {
    public IncludeBuilder   getIncludeBuilder();
    public LineBuilder      getLineBuilder();
    public DirectiveBuilder getPragmaBuilder();
}
