package cip.byteinputlexerlexertest;

import cip.Identifier;
import cip.Location;
import cip.lexer.Lexeme;


/**
 * This <code>Builder</code> does nothing other than output the line, column
 * and type of each lexeme that is recognized.
 */
class Builder implements cip.lexer.Builder {

    private void printLocation(Location l) {
        System.out.print(l.inputName);
        System.out.print("[");
        System.out.print(l.line);
        System.out.print(",");
        System.out.print(l.column);
        System.out.print("] ");
    }

    public int buildChar(byte b, Location l) throws Exception {
        printLocation(l);
        System.out.print("CHAR ");
        System.out.println(b);
        return Lexeme.CHAR_LITERAL;
    }

    public int buildId(Identifier id, Location l)
            throws Exception {
        printLocation(l);
        System.out.print("ID ");
        System.out.println(id);
        return Lexeme.ID_LITERAL;
    }

    public int buildString(String s, Location l)
            throws Exception {
        printLocation(l);
        System.out.print("STR ");
        System.out.println(s);
        return Lexeme.STRING_LITERAL;
    }

    public int buildInt(long value, Location l) throws Exception {
        printLocation(l);
        System.out.print("INT ");
        System.out.println(value);
        return Lexeme.INT_LITERAL;
    }

    public int buildKeyword(int keyword, Location l) throws Exception {
        printLocation(l);
        System.out.print("KEYWORD ");
        System.out.println(keyword);
        return keyword;
    }

    public int buildOperator(int symbol, Location l) throws Exception {
        printLocation(l);
        System.out.print("SYM ");
        System.out.println(symbol);
        return symbol;
    }

    public int buildInputEnd(Location l) throws Exception {
        printLocation(l);
        System.out.println("EOI");
        return 0;
    }
}
