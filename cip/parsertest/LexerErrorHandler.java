package cip.parsertest;

import cip.Location;

/**
 *
 */
class LexerErrorHandler implements cip.byteinputlexer.ErrorHandler {

    private void printLocation(Location l) {
        System.out.print(l.inputName);
        System.out.print("[");
        System.out.print(l.line);
        System.out.print(",");
        System.out.print(l.column);
        System.out.print("] ");
    }
  
    public int emptyChar(Location l) {
        printLocation(l);
        System.out.println("EMPTY_CHAR");
        return -1;
    }

    public int emptyHexLiteral(Location l) {
        printLocation(l);
        System.out.println("EMPTY_HEX_LITERAL");
        return -1;
    }

    public int illegalCharEscape(byte b, Location l) {
        printLocation(l);
        System.out.print("ILLEGAL_CHAR_ESCAPE ");
        System.out.println(b);
        return -1;
    }

    public int illegalCharacter(byte b, Location l) {
        printLocation(l);
        System.out.print("ILLEGAL_CHAR ");
        System.out.println(b);
        return -1;
    }

    public int illegalDirective(Location l) {
        printLocation(l);
        System.out.print("ILLEGAL_DIRECTIVE ");
        return -1;
    }

    public int malformedDirective(Location l) {
        printLocation(l);
        System.out.print("MALFORMED_DIRECTIVE ");
        return -1;
    }

    public int charOverflow(int value, Location l) {
        printLocation(l);
        System.out.print("CHARO ");
        System.out.println(value);
        return -1;
    }

    public int integerOverflow(Location l) {
        printLocation(l);
        System.out.println("INTO");
        return -1;
    }

    public int unterminatedChar(Location l) {
        printLocation(l);
        System.out.println("UNTERMINATED_CHAR");
        return -1;
    }

    public int truncatedHexInt(Location l) {
        printLocation(l);
        System.out.println("TRUNCATED_HEX_INT");
        return -1;
    }

    public int unterminatedComment(Location l) {
        printLocation(l);
        System.out.println("UNTERMINATED_COMMENT");
        return -1;
    }

    public int unterminatedString(Location l) {
        printLocation(l);
        System.out.println("UNTERMINATED_STRING");
        return -1;
    }
}
