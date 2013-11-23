package cip.lexer;

/**
 * To allow for different lexer implementations covering different sorts
 * of input, <code>Lexer</code> defines exactly what a lexer must be able
 * to support.  The interface is very simple.
 */
public interface Lexer {

    /**
     * <code>lex</code> returns an <code>int</code> representing a
     * <code>Lexeme</code>.  If there are no more lexemes, then 0 is
     * returned.  For each lexeme that is recognized, an appropriate
     * method in the supplied <code>Builder</code> is called.
     */
    public int lex(Builder b) throws Exception;


    /**
     * <code>lex</code> returns an <code>int</code> representing a
     * <code>Lexeme</code>.  If there are no more lexemes, then 0 is
     * returned.  For each lexeme that is recognized, an appropriate
     * method in a previously supplied <code>Builder</code> is called.
     */
    public int lex() throws Exception;


    /**
     * <code>close</code> closes the <code>Lexer</code> and frees up any
     * associated resources.
     */
    public void close() throws Exception;
}
