package cip.parsertest;

import cip.Identifier;
import cip.Location;
import cip.lexer.Lexeme;

/**
 *
 */
class LexerBuilder implements cip.lexer.Builder {
    private cip.ast.Builder astBuilder;

    LexerBuilder(cip.ast.Builder astBuilder) {
        this.astBuilder = astBuilder;
    }

    public int buildChar(byte b, Location l) throws Exception {
        return Lexeme.CHAR_LITERAL;
    }

    public int buildId(Identifier id, Location l) throws Exception {
        astBuilder.addId(id, l);
        return Lexeme.ID_LITERAL;
    }

    public int buildString(String s, Location l) throws Exception {
        return Lexeme.STRING_LITERAL;
    }

    public int buildInt(long v, Location l) throws Exception {
        astBuilder.addInt(v, l);
        return Lexeme.INT_LITERAL;
    }

    public int buildKeyword(int keyword, Location l) throws Exception {
        return keyword;
    }

    public int buildOperator(int symbol, Location l) throws Exception {
        return symbol;
    }

    public int buildInputEnd(Location l) throws Exception {
        return 0;
    }
}
