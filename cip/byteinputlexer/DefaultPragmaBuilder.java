package cip.byteinputlexer;

import cip.Location;
import cip.lexer.Lexeme;

/**
 * <code>DefaultPragmaBuilder</code> XXX
 */
class DefaultPragmaBuilder implements DirectiveBuilder {
    // cip/changes/lex-pragma

    DefaultPragmaBuilder() {
    }

    public int buildChar(byte b, Location l) throws Exception {
        return Lexeme.CHAR_LITERAL;
    }

    public void buildIdChunk(byte[] buffer, int start, int end, Location l)
            throws Exception {
    }

    public int buildId(byte[] buffer, int start, int end, Location l)
            throws Exception {
        return Lexeme.ID_LITERAL;
    }

    public void buildStringChunk(byte[] buffer, int start, int end, Location l)
            throws Exception {
    }

    public void buildStringChar(byte b, Location l) throws Exception {
    }

    public int buildString(byte[] buffer, int start, int end, Location l)
            throws Exception {
        return Lexeme.STRING_LITERAL;
    }

    public int buildInt(long value, int type, Location l) throws Exception {
        return Lexeme.INT_LITERAL;
    }

    public int buildKeyword(int keyword, Location l) throws Exception {
        return keyword;
    }

    public int buildOperator(byte[] buffer, int start, int end, 
                             int symbol, Location l) throws Exception {
        return symbol;                               
    }

    public int buildLineEnd(int rv) throws Exception {
        return rv|EOL;
    }

    public int buildInputEnd(Location l) throws Exception {
        return EOL;
    }
}
