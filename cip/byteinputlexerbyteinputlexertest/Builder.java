package cip.byteinputlexerbyteinputlexertest;

import cip.Location;
import cip.lexer.Lexeme;

/**
 * This <code>Builder</code> does nothing other than output the line, column
 * and type of each lexeme that is recognized.
 */
class Builder implements cip.byteinputlexer.Builder {

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

    public void buildIdChunk(byte[] buffer, int start, int end, Location l)
            throws Exception {
        printLocation(l);
        System.out.print("IDC ");
        System.out.println(new String(buffer, start, end-start));
    }

    public int buildId(byte[] buffer, int start, int end, Location l)
            throws Exception {
        printLocation(l);
        System.out.print("ID ");
        System.out.println(new String(buffer, start, end-start));
        return Lexeme.ID_LITERAL;
    }

    public void buildStringChunk(byte[] buffer, int start, int end, Location l)
            throws Exception {
        printLocation(l);
        System.out.print("STRC ");
        System.out.println(new String(buffer, start, end-start));
    }

    public void buildStringChar(byte b, Location l) throws Exception {
        printLocation(l);
        System.out.print("SCHAR ");
        System.out.println(b);
    }

    public int buildString(byte[] buffer, int start, int end, Location l)
            throws Exception {
        printLocation(l);
        System.out.print("STR ");
        System.out.println(new String(buffer, start, end-start));
        return Lexeme.STRING_LITERAL;
    }

    public int buildInt(long value, int type, Location l)
            throws Exception {
        printLocation(l);
        System.out.print("INT ");
        System.out.print(type);
        System.out.print(" ");
        System.out.println(value);
        return Lexeme.INT_LITERAL;
    }

    public int buildKeyword(int keyword, Location l) throws Exception {
        printLocation(l);
        System.out.print("KEYWORD ");
        System.out.println(keyword);
        return keyword;
    }

    public int buildOperator(byte[] buffer, int start, int end, 
                             int symbol, Location l) throws Exception {
        printLocation(l);
        System.out.print("SYM ");
        System.out.println(symbol);
        return symbol;
    }

    public int buildLineEnd(int rv) throws Exception {
        return rv;
    }

    public int buildInputEnd(Location l) throws Exception {
        printLocation(l);
        System.out.println("EOI");
        return 0;
    }
}
