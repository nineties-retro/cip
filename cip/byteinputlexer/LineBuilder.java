package cip.byteinputlexer;

import cip.lexer.Lexeme;

/**
 *
 */
public interface LineBuilder extends DirectiveBuilder {
    public void reset();
    public int getLine();
    public String getFileName();
}
