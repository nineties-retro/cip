package cip.byteinputlexer;

import cip.lexer.Lexeme;

/**
 * An <code>IncludeBuilder</code> is used to build XXX.
 */
public interface IncludeBuilder extends DirectiveBuilder {
    public void reset();
    public String getFileName();
}
