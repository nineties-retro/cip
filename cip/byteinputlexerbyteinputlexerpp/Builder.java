package cip.byteinputlexerbyteinputlexerpp;

import cip.Location;
import cip.lexer.Lexeme;

/**
 * The guts of the pretty printer.  Since it is only driven off a lexical
 * scan of the input it is possible that it will pretty print files that
 * are not syntactically or semantically correct, though the output is
 * undefined for such files.
 */
class Builder implements cip.byteinputlexer.Builder {
    static private final String[] keywords = {
        "any", "attribute", "boolean", "case", "char", "const", "context",
        "default", "double", "enum", "exception", "fixed", "float", "in",
        "inout", "interface", "long", "module", "octet", "oneway", "out",
        "raises", "readonly", "sequence", "short", "string", "struct",
        "switch", "typedef", "unsigned", "union", "void", "wchar", "wstring",
        "FALSE", "Object", "TRUE"
    };

    private int indent;
    private int maxColumn;
    private int currentIndent;
    private int currentColumn;
    private boolean inStrChunk;
    private StringBuffer idChunk;
    private boolean braceOnSameLine;
    private String separator;

    Builder(int indent, boolean braceOnSameLine) {
        this.indent = indent;
        this.braceOnSameLine = braceOnSameLine;
        maxColumn = 75;         // XXX: should be an option.
        currentIndent = 0;
        currentColumn = 0;
        inStrChunk = false;
        idChunk = new StringBuffer();
        separator = "";
    }

    Builder() {
        this(4, false);
    }

    private void increaseIndentation() {
        currentIndent += indent;
    }

    private void decreaseIndentation() {
        currentIndent -= indent;
    }

    private void outputIndentation() {
        for (int i = currentColumn; i < currentIndent; i += 1) {
            System.out.print(" ");
        }
        currentColumn = currentIndent;
    }

    private void outputNewline() {
        System.out.println("");
        currentColumn = 0;
    }

    private void adjustOutput(int x) {
        if (currentColumn == 0) {
            outputIndentation();
        }
        currentColumn += x;
        if (currentColumn > maxColumn) {
            outputNewline();
            outputIndentation();
        }
    }

    private int charAdjustment(int b) {
        return (b > 077) ? 3 : (b > 07) ? 2 : 1;
    }

    public int buildChar(byte b, Location l) throws Exception {
        /*
         * XXX: Used a fixed strategy for outputting characters, could
         * make it more flexible through the use of options e.g. give
         * the choice of octal or hex characters and with a change to
         * the lexer allow the choice of preserving the format or changing
         * to a canonical form.
         */
        System.out.print("'");
        if (b < 0) {
            adjustOutput(charAdjustment((int) b));
            System.out.print(Integer.toOctalString((int) b));
        } else {
          String s;
          switch (b) {
          case '\'':
            s = "\\\'";
            break;
          case '\\':
            s = "\\";
            break;
          case '\007':
            s = "\\a";
            break;
          case '\010':
            s = "\\b";
            break;
          case '\011':
            s = "\\t";
            break;
          case '\012':
            s = "\\n";
            break;
          case '\013':
            s = "\\v";
            break;
          case '\014':
            s = "\\f";
            break;
          case '\015':
            s = "\\r";
            break;
          default:
            s = Byte.toString(b);
            break;
          }
          System.out.print(s);
        }
        System.out.print("'");
        return Lexeme.CHAR_LITERAL;
    }

    public void buildIdChunk(byte[] buffer, int start, int end,
                             Location l) throws Exception {
        idChunk.append(new String(buffer, start, end-start));
    }

    public int buildId(byte[] buffer, int start, int end,
                       Location l) throws Exception {
        idChunk.append(new String(buffer, start, end-start));
        adjustOutput(idChunk.length());
        System.out.print(separator);
        System.out.print(idChunk.toString());
        idChunk = new StringBuffer();
        separator = " ";
        return Lexeme.ID_LITERAL;
    }

    public void buildStringChunk(byte[] buffer, int start, int end,
                                 Location l) throws Exception {
        if (!inStrChunk) {
            System.out.print("\"");
            inStrChunk = true;
        }
        System.out.print(new String(buffer, start, end-start));
    }

    public void buildStringChar(byte b, Location l) throws Exception {
        if (!inStrChunk) {
            System.out.print("\"");
            inStrChunk = true;
        }
        buildChar(b, l);
    }

    public int buildString(byte[] buffer, int start, int end,
                           Location l) throws Exception {
        System.out.print(new String(buffer, start, end-start));
        if (inStrChunk) {
            System.out.print("\"");
            inStrChunk = false;
        }
        return Lexeme.STRING_LITERAL;
    }

    public int buildInt(long v, int t, Location l) throws Exception {
        String s = (t < 0)
          ? "0" + Long.toOctalString(v)
          : (t > 0)
            ? "0x" + Long.toHexString(v)
            : Long.toString(v);
        adjustOutput(s.length()+separator.length());
        System.out.print(separator);
        System.out.print(s);
        separator = " ";
        return Lexeme.INT_LITERAL;
    }

    private boolean keywordStartsLine(int keyword) {
        switch (keyword) {
        case Lexeme.KW_CONST:
        case Lexeme.KW_MODULE:
        case Lexeme.KW_INTERFACE:
        case Lexeme.KW_STRUCT:
        case Lexeme.KW_SWITCH:
        case Lexeme.KW_TYPEDEF:
        case Lexeme.KW_UNION:
          return true;
        default:
          return false;
        }
    }
    public int buildKeyword(int keyword, Location l)
            throws Exception {
        String keywordLiteral = keywords[keyword-1];
        if (keywordStartsLine(keyword) && currentColumn != 0) {
            outputNewline();
            adjustOutput(keywordLiteral.length());
            System.out.print(keywordLiteral);
        } else {
            adjustOutput(keywordLiteral.length()+separator.length());
            System.out.print(separator);
            System.out.print(keywordLiteral);
        }
        separator = " ";
        return keyword;
    }

    public int buildOperator(byte[] buffer, int start, int end,
                             int symbol, Location l) throws Exception {
        switch (symbol) {
        case Lexeme.OPEN_BRACE:
            if (!braceOnSameLine) {
                outputNewline();
            } else {
              System.out.print(separator);
            }
            System.out.print("{");
            outputNewline();
            increaseIndentation();
            separator = "";
            break;
        case Lexeme.CLOSE_BRACE:
            decreaseIndentation();
            outputIndentation();
            System.out.print("}");
            separator = "";
            break;
        case Lexeme.SEMI_COLON:
            System.out.print(";");
            outputNewline();
            separator = "";
            break;
        case Lexeme.CLOSE_BRACKET:
        case Lexeme.OPEN_ANGLE:
        case Lexeme.OPEN_BRACKET:
        case Lexeme.OPEN_PAREN:
        case Lexeme.COLON_PAIR:
            System.out.print(new String(buffer, start, end-start));
            separator = "";
            break;
        case Lexeme.CLOSE_PAREN:
        case Lexeme.COMMA:
            System.out.print(new String(buffer, start, end-start));
            separator = " ";
            break;
        default:
            System.out.print(separator);
            System.out.print(new String(buffer, start, end-start));
            separator = " ";
            break;
        }
        return symbol;
    }

    public int buildLineEnd(int v) throws Exception {
        return v;
    }

    public int buildInputEnd(Location l) throws Exception {
        return 0;
    }
}
