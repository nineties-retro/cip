package cip.byteinputlexer;

import cip.Location;
import cip.lexer.Lexeme;

/**
 * A <code>ByteInputLexer</code> turns sequences of bytes into lexemes.
 */
public class ByteInputLexer {
    /* The implementation is based on the approach described in 
     * [lcc].
     * <p>
     * An obvious difference is that Java does not have a <code>goto</code>
     * and so when a keyword match fails the id parsing code has to be
     * called.  If the compiler can do tail call elimination then it amounts
     * to the same thing but I doubt (m)any Java compilers so (I can confirm
     * that the Sun JDK 1.2beta3 does not) though perhaps a JIT might.
     */

    /**
     * The longest sequence of characters that can make up any token other
     * than an identifier or a string.  It is actually the length of the
     * maximum long in octal (0777777777777777777777).
     */
    private static final int MAX_TOKEN = 22;

    private static final int ID = CharMap.LETTER|CharMap.DIGIT;

    final private byte[] buffer;
    private int i;
    private int end;
    final private Location location;
    final private Input input;
    final private DirectiveBuilders directiveBuilders;
    final private Builder userBuilder;
    final private ErrorHandler errorHandler;
    private int lastLine;
    private int line;

    public ByteInputLexer(byte[] buffer, Input input, Location loc,
                          Builder userBuilder,
                          DirectiveBuilders directiveBuilders,
                          ErrorHandler errorHandler) {
        this.buffer = buffer;
        this.input = input;
        this.location = loc;
        this.userBuilder = userBuilder;
        this.directiveBuilders = directiveBuilders;
        this.errorHandler = errorHandler;
        this.line = 1;
        this.i = 0;
        this.end = 0;
        this.lastLine = 0;
        buffer[0] = (byte)'\n';
    }

    // cip/changes/lex-line-basic
    private int lexLine(Builder builder) throws Exception {
        LineBuilder lb = directiveBuilders.getLineBuilder();
        int l = lex(lb);
        if (l == Lexeme.INT_LITERAL) {
            l = lex(lb);
            if (l == Lexeme.STRING_LITERAL) {
                if (lex(lb) == DirectiveBuilder.EOL) {
                    line = lb.getLine();;
                    location.inputName = lb.getFileName();
                    lb.reset();
                    return lex(builder);
                } else {
                    return errorHandler.malformedDirective(location);
                }
            } else if ((l&~DirectiveBuilder.EOL) == Lexeme.STRING_LITERAL) {
                line = lb.getLine();
                location.inputName = lb.getFileName();
                lb.reset();
                return lex(builder);
            } else {
                return errorHandler.malformedDirective(location);
            }
        } else {
            return errorHandler.malformedDirective(location);
        }
    }

    // cip/changes/lex-pragma
    private int lexPragma(Builder builder) throws Exception {
        DirectiveBuilder pb = directiveBuilders.getPragmaBuilder();
        int first = lex(pb);
        if (first == Lexeme.ID_LITERAL) {
            while ((lex(pb)&DirectiveBuilder.EOL) == 0) {
                /* do nothing */
            }
            return lex(builder);
        } else if ((first&~DirectiveBuilder.EOL) == Lexeme.ID_LITERAL) {
            return lex(builder);
        } else {
            return errorHandler.malformedDirective(location);
        }
    }

    // cip/changes/lex-include-string
    private int lexInclude(Builder builder) throws Exception {
        IncludeBuilder ib = directiveBuilders.getIncludeBuilder();
        int first = lex(ib);
        if (first == Lexeme.STRING_LITERAL) {
            while ((lex(ib)&DirectiveBuilder.EOL) == 0) {
                /* do nothing */
            }
            ib.reset();
            return lex(builder);
        } else if ((first&~DirectiveBuilder.EOL) == Lexeme.STRING_LITERAL) {
            // location.fileName = lb.getFileName();
            ib.reset();
            return lex(builder);
        } else {
            return errorHandler.malformedDirective(location);
        }
    }

    // A dummy until a proper implementation is done.
    private int lexDirective(Builder builder) throws Exception {
        IncludeBuilder ib = directiveBuilders.getIncludeBuilder();
        while ((lex(ib)&DirectiveBuilder.EOL) == 0) {
            /* do nothing */
        }
        ib.reset();
        return lex(builder);
    }

    private int lexDefine(Builder builder) throws Exception {
        return lexDirective(builder);
    }

    private int lexElse(Builder builder) throws Exception {
        return lexDirective(builder);
    }

    private int lexEndif(Builder builder) throws Exception {
        return lexDirective(builder);
    }

    private int lexIf(Builder builder) throws Exception {
        return lexDirective(builder);
    }

    private int lexIfdef(Builder builder) throws Exception {
        return lexDirective(builder);
    }

    private int lexIfndef(Builder builder) throws Exception {
        return lexDirective(builder);
    }

    // cip/changes/lex-decimal-int
    private int lexDecimalInt(Builder builder, int start, long v) 
        throws Exception {
        byte b;
        byte[] bb = buffer;
        int[] cm = CharMap.map;
        int j = start+1;

        for (;;) {
            b = bb[j];
            if (b > 0) {
                if ((cm[b]&CharMap.DIGIT) != 0) {
                    int d = b-'0';
                    if (v <= ((Long.MAX_VALUE-d)/10)) {
                        v = 10*v + d;
                        j += 1;
                    } else {
                        i = j;
                        return errorHandler.integerOverflow(location);
                    }
                } else {
                    i = j;
                    return builder.buildInt(v, 0, location);
                }
            } else {
                location.column = lastLine + (j - i);
                i = j;
                return errorHandler.illegalCharacter(b, location);
            }
        }
    }

    // cip/changes/lex-decimal-int
    private int lexOctalInt(Builder builder, int start) throws Exception {
        byte b;
        byte[] bb = buffer;
        int[] cm = CharMap.map;
        long v = 0;
        int j = start+1;
        
        for (;;) {
            b = bb[j];
            if (b > 0) {
                if (b >= '0' && b <= '7') {
                    int d = b-'0';
                    if (v <= ((Long.MAX_VALUE-d)/10)) {
                        v = (v<<3) + d;
                        j += 1;
                    } else {
                        i = j;
                        return errorHandler.integerOverflow(location);
                    }
                } else {
                    i = j;
                    return builder.buildInt(v, -1, location);
                }
            } else {
                location.column = lastLine + (j - i);
                i = j;
                return errorHandler.illegalCharacter(b, location);
            }
        }
    }

    // cip/changes/lex-decimal-int
    private int lexHexInt(Builder builder, int start) throws Exception {
        byte b;
        byte[] bb = buffer;
        int[] cm = CharMap.map;
        long v = 0;
        int j = start+2;

        for (;;) {
            b = bb[j];
            if (b > 0) {
                int d;
                if (b >= '0' && b <= '9') {
                    d = b - '0';
                } else if (b >= 'A' && b <= 'F') {
                    d = b - 'A' + 10;
                } else if (b >= 'a' && b <= 'f') {
                    d = b - 'a' + 10;
                } else if (j == start+2){
                    i = j;
                    return errorHandler.truncatedHexInt(location);
                } else {
                    i = j;
                    return builder.buildInt(v, 1, location);
                }
                if (v <= ((Long.MAX_VALUE-d)/10)) {
                    v = (v<<4) + d;
                    j += 1;
                } else {
                    i = j;
                    return errorHandler.integerOverflow(location);
                }
            } else {
                location.column = lastLine + (j - i);
                i = j;
                return errorHandler.illegalCharacter(b, location);
            }
        }
    }

    private int lexString(Builder builder, int start) throws Exception {
        byte b;
        byte[] bb = buffer;
        int j = start;
        for (;;) {
            b = bb[j];
            if (b > 0) {
                switch (b) {
                case '"':
                    i = j + 1;
                    return builder.buildString(bb, start, j, location);
                case '\007':            // \a
                case '\b':
                case '\t':
                case '\013':            // \v
                case '\f':
                case '\r':
                    location.column = j - lastLine;
                    return errorHandler.illegalCharacter(b, location);
                case '\\':
                    byte c;
                    int k = j+2;
                    switch (bb[j+1]) {
                    case '\'':
                        c = (byte)'\'';
                        break;
                    case '"':
                        c = (byte)'"';
                        break;
                    case '?':
                        c = (byte)'?';
                        break;
                    case '\\':
                        c = (byte)'\\';
                        break;
                    case 'a':
                        c = (byte)'\007';
                        break;
                    case 'b':
                        c = (byte)'\010';
                        break;
                    case 't':
                        c = (byte)'\011';
                        break;
                    case 'n':
                        c = (byte)'\012';
                        break;
                    case 'v':
                        c = (byte)'\013';
                        break;
                    case 'f':
                        c = (byte)'\014';
                        break;
                    case 'r':
                        c = (byte)'\015';
                        break;
                    case 'x':
                        b = bb[j+2];
                        if ((CharMap.map[b] & (CharMap.DIGIT|CharMap.HEX)) != 0){
                            int v = ((CharMap.map[b]&CharMap.DIGIT) != 0)
                                ? b - '0'
                                : b - ((b >= 'A' && b <= 'F') ? 'A' : 'a') + 10;
                            b = bb[j+3];
                            k += 1;
                            if ((CharMap.map[b]&(CharMap.DIGIT|CharMap.HEX))!=0){
                                v <<= 4;
                                v += ((CharMap.map[b]&CharMap.DIGIT) != 0)
                                    ? b - '0'
                                    : b - ((b>='A' && b<='F') ? 'A' : 'a')+10;
                                k += 1;
                            }
                            c = (byte) v;
                        } else {
                            location.column = j - lastLine;
                            return errorHandler.emptyHexLiteral(location);
                        }
                        break;
                    case '0': case '1': case '2': case '3': 
                    case '4': case '5': case '6': case '7':
                        int v = bb[j+1]-'0';
                        b = bb[j+2];
                        if ((b >= '0') & (b <= '7')) {
                            v = (v<<3) + (b-'0');
                            b = bb[j+3];
                            k += 1;
                            if ((b >= '0') & (b <= '7')) {
                                v = (v<<3) + (b-'0');
                                k += 1;
                            }
                        }
                        if (v > 255) {
                            i = k;
                            location.column = j-lastLine;
                            return errorHandler.charOverflow(v, location);
                        }
                        c = (byte)v;
                        break;
                    default:
                        location.column = j - lastLine;
                        return errorHandler.illegalCharEscape(bb[j+1], location);
                    }
                    builder.buildStringChunk(bb, start, j, location);
                    builder.buildStringChar(c, location);
                    start = j = k;
                    break;
                case '\n':
                    if (j == end) {
                        lastLine -= j;
                        builder.buildStringChunk(bb, start, j, location);
                        int nRead = input.read(bb, 0);
                        if (nRead < 0) {
                            i = end;
                            return errorHandler.unterminatedString(location);
                        } else {
                            j = 0;
                            start = 0;
                            end = nRead;
                            bb[nRead] = (byte)'\n';
                        }
                    }
                    break;
                default:
                    j += 1;
                }
            } else {
                i = j;
                location.column = j-lastLine;
                return errorHandler.illegalCharacter(b, location);
            }
        }
    }

    /* XXX: could put a lot more effort into producing better error messages
     * for the various ways that a user might get the string wrong.  Also
     * should give an error if a non-graphic character is used literally.
     * see corba-v2.2#char.escapes and 
     * corba-v2.2#char.format.codes.
     */
    private int lexChar(Builder builder, int start) throws Exception {
        int j = start+1;
        byte[] bb = buffer;
        byte b = bb[j];
        int k;
        byte c;
        switch (b) {
        case '\'':
            return errorHandler.emptyChar(location);
        case '\\':
            switch (bb[j+1]) {
            case '\'':
                c = (byte)'\'';
                break;
            case '"':
                c = (byte)'"';
                break;
            case '?':
                c = (byte)'?';
                break;
            case '\\':
                c = (byte)'\\';
                break;
            case 'a':
                c = (byte)'\007';
                break;
            case 'b':
                c = (byte)'\010';
                break;
            case 't':
                c = (byte)'\011';
                break;
            case 'n':
                c = (byte)'\012';
                break;
            case 'v':
                c = (byte)'\013';
                break;
            case 'f':
                c = (byte)'\014';
                break;
            case 'r':
                c = (byte)'\015';
                break;
            case 'x':
                b = bb[j+2];
                if ((CharMap.map[b] & (CharMap.DIGIT|CharMap.HEX)) != 0) {
                    int v = ((CharMap.map[b]&CharMap.DIGIT) != 0)
                        ? b - '0'
                        : b - (((b >= 'A') & (b <= 'F')) ? 'A' : 'a') + 10;
                    b = bb[j+3];
                    j += 1;
                    if ((CharMap.map[b] & (CharMap.DIGIT|CharMap.HEX)) != 0) {
                        v <<= 4;
                        v += ((CharMap.map[b]&CharMap.DIGIT) != 0)
                            ? b - '0'
                            : b - (((b >= 'A') & (b <= 'F')) ? 'A' : 'a') + 10;
                        j += 1;
                    }
                    c = (byte)v;
                } else {
                    return errorHandler.emptyHexLiteral(location);
                }
                break;
            case '0': case '1': case '2': case '3': 
            case '4': case '5': case '6': case '7':
                int v = bb[j+1]-'0';
                b = bb[j+2];
                if ((b >= '0') & (b <= '7')) {
                    v = (v<<3) + (b-'0');
                    b = bb[j+3];
                    j += 1;
                    if ((b >= '0') & (b <= '7')) {
                        v = (v<<3) + (b-'0');
                        j += 1;
                    }
                }
                if (v > 255) {
                    i = j;
                    return errorHandler.charOverflow(v, location);
                }
                c = (byte)v;
                break;
            default:
                location.column += 1;
                return errorHandler.illegalCharEscape(bb[j+1], location);
            }
            k = j+2;
            break;
        default:
            c = bb[j];
            k = j+1;
            break;
        }
        if (bb[k] == '\'') {
            i = k+1;
            return builder.buildChar(c, location);
        } else {
            return errorHandler.unterminatedChar(location);
        }
    }

    private int lexId(Builder builder, int j) throws Exception {
        byte b;
        byte[] bb = buffer;
        int[] cm = CharMap.map;
        int start = i;

        for (;;) {
            b = bb[j];
            if (b > 0) {
                if ((cm[b]&ID) != 0) {
                    j += 1;
                } else if (b != '\n') {
                    i = j;
                    return builder.buildId(bb, start, j, location);
                } else if (j != end) {
                    int end = j;
                    j += 1;
                    i = j;
                    lastLine = j;
                    line += 1;
                    int id = builder.buildId(bb, start, end, location);
                    return builder.buildLineEnd(id);
                } else {
                    builder.buildIdChunk(bb, start, j, location);
                    lastLine -= j;
                    int nRead = input.read(bb, 0);
                    if (nRead < 0) {
                        i = end = 0;
                        return builder.buildId(bb, start, j, location);
                    } else {
                        start = 0;
                        j = 0;
                        end = nRead;
                        bb[nRead] = (byte)'\n';
                    }
                }
            } else {
                location.column = lastLine + (j - i);
                i = j;
                return errorHandler.illegalCharacter(b, location);
            }
        }
    }

    private int refill(Builder builder) throws Exception {
        byte[] bb = buffer;
        int j = 0;
        int k = i;
        int e = end;
        lastLine -= k;

        while (k != e) {
            bb[j] = bb[k];
            k += 1;
            j += 1;
        }
        int nRead = input.read(bb, j);
        if (nRead >= 0) {
            int[] cm = CharMap.map;
            bb[j+nRead] = (byte)'\n';
            end = j+nRead;
            j = 0;
            while ((cm[bb[j]] & CharMap.BLANK) != 0) {
                j += 1;
            }
            i = j;
            return 1;
        } else {
            end = j;
            if (k == 0) {
                return builder.buildInputEnd(location);
            } else {
                i = 0;
                bb[j] = (byte)'\n';
                return 1;
            }
        }
    }

    private int processLineComment(Builder builder, int j) throws Exception {
        byte[] bb = buffer;
        for (;;) {
            while (bb[j] != '\n') {
                j += 1;
            }
            if (j != end) {
                line += 1;
                i = j+1;
                lastLine = j+1;
                return builder.buildLineEnd(1);
            } else {
                lastLine -= j;
                int nRead = input.read(bb, 0);
                if (nRead < 0) {
                    i = end;
                    return builder.buildInputEnd(location);
                } else {
                    j = 0;
                    end = nRead;
                    bb[nRead] = (byte)'\n';
                }
            }
        }
    }

    private int processBlockComment(Builder builder, int j) throws Exception {
        byte[] bb = buffer;
        for (;;) {
            switch (bb[j]) {
            case '*':
                switch (bb[j+1]) {
                case '/':
                    i = j+2;
                    return 1;
                case '\n':
                    if (j+1 != end) {
                        line += 1;
                        j += 1;
                        lastLine = j;
                        if (builder == userBuilder) {
                            builder.buildLineEnd(0);
                        } else {
                            i = j;
                            return errorHandler.unterminatedComment(location);
                        }
                    } else {
                        lastLine -= j;      // not j+1 due to 1 below ...
                        int nRead = input.read(bb, 1);
                        if (nRead < 0) {
                            i = end;
                            return errorHandler.unterminatedComment(location);
                        } else {
                            j = 0;
                            end = nRead+1;
                            bb[0] = (byte)'*';
                            bb[nRead+1] = (byte)'\n';
                        }
                    }
                    break;
                default:
                    break;
                }
                break;
            case '\n':
                if (j != end) {
                    line += 1;
                    j += 1;
                    lastLine = j;
                    if (builder == userBuilder) {
                        builder.buildLineEnd(0);
                    } else {
                        i = j;
                        return errorHandler.unterminatedComment(location);
                    }
                } else {
                    lastLine -= j;
                    int nRead = input.read(bb, 0);
                    if (nRead < 0) {
                        i = end;
                        return errorHandler.unterminatedComment(location);
                    } else {
                        j = 0;
                        end = nRead;
                        bb[nRead] = (byte)'\n';
                    }
                }
                break;
            default:
                j += 1;
                break;
            }
        }
    }


    public int lex(Builder builder) throws Exception {
        byte b;
        int j = i;
        byte[] bb = buffer;
        int[] cm = CharMap.map;

        for (;;) {
            for (;;) {
                b = bb[j];
                if (b > 0) {
                    if ((cm[b] & CharMap.BLANK) != 0) {
                        j += 1;
                    } else {
                        i = j;
                        break;
                    }
                } else {
                    i = j;
                    return errorHandler.illegalCharacter(b, location);
                }
            }
            if ((end - i) < MAX_TOKEN) {
                if (refill(builder) == 0)
                    return 0;
                b = bb[i];
                j = i;
            }
            location.line = line;
            location.column = j-lastLine;
            switch (b) {
            case 'a':
                b = bb[j+1];
                if (b == 't') {
                    if (bb[j+2] == 't') {
                        if (bb[j+3] == 'r') {
                            if (bb[j+4] == 'i') {
                                if (bb[j+5] == 'b') {
                                    if (bb[j+6] == 'u') {
                                        if (bb[j+7] == 't') {
                                            if (bb[j+8] == 'e') {
                                                if ((cm[bb[j+9]]&ID) != 0) {
                                                    return lexId(builder, j+10);
                                                } else {
                                                    i = j+9;
                                                    return builder.buildKeyword(Lexeme.KW_ATTRIBUTE, 
                                                                                location);
                                                }
                                            } else {
                                                return lexId(builder, j+8);
                                            }
                                        } else {
                                            return lexId(builder, j+7);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else if (b == 'n') {
                    if (bb[j+2] == 'y') {
                        if ((cm[bb[j+3]]&ID) != 0) {
                            return lexId(builder, j+4);
                        } else {
                            i = j+3;
                            return builder.buildKeyword(Lexeme.KW_ANY, location);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'b':
                if (bb[j+1] == 'o') {
                    if (bb[j+2] == 'o') {
                        if (bb[j+3] == 'l') {
                            if (bb[j+4] == 'e') {
                                if (bb[j+5] == 'a') {
                                    if (bb[j+6] == 'n') {
                                        if ((cm[bb[j+7]]&ID) != 0) {
                                            return lexId(builder, j+8);
                                        } else {
                                            i = j+7;
                                            return builder.buildKeyword(Lexeme.KW_BOOLEAN, location);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'c':
                b = bb[j+1];
                if (b == 'o') {
                    if (bb[j+2] == 'n') {
                        b = bb[j+3];
                        if (b == 's') {
                            if (bb[j+4] == 't') {
                                if ((cm[bb[j+5]]&ID) != 0) {
                                    return lexId(builder, j+6);
                                } else {
                                    i = j+5;
                                    return builder.buildKeyword(Lexeme.KW_CONST, location);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else if (b == 't') {
                            if (bb[j+4] == 'e') {
                                if (bb[j+5] == 'x') {
                                    if (bb[j+6] == 't') {
                                        if ((cm[bb[j+7]]&ID) != 0) {
                                            return lexId(builder, j+8);
                                        } else {
                                            i = j+7;
                                            return builder.buildKeyword(Lexeme.KW_CONTEXT, location);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else if (b == 'h') {
                    if (bb[j+2] == 'a') {
                        if (bb[j+3] == 'r') {
                            if ((cm[bb[j+4]]&ID) != 0) {
                                return lexId(builder, j+5);
                            } else {
                                i = j+4;
                                return builder.buildKeyword(Lexeme.KW_CHAR,
                                                            location);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else if (b == 'a') {
                    if (bb[j+2] == 's') {
                        if (bb[j+3] == 'e') {
                            if ((cm[bb[j+4]]&ID) != 0) {
                                return lexId(builder, j+5);
                            } else {
                                i = j+4;
                                return builder.buildKeyword(Lexeme.KW_CASE,
                                                            location);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'd':
                b = bb[j+1];
                if (b == 'o') {
                    if (bb[j+2] == 'u') {
                        if (bb[j+3] == 'b') {
                            if (bb[j+4] == 'l') {
                                if (bb[j+5] == 'e') {
                                    if ((cm[bb[j+6]]&ID) != 0) {
                                        return lexId(builder, j+7);
                                    } else {
                                        i = j+6;
                                        return builder.buildKeyword(Lexeme.KW_DOUBLE, location);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } if (b == 'e') {
                    if (bb[j+2] == 'f') {
                        if (bb[j+3] == 'a') {
                            if (bb[j+4] == 'u') {
                                if (bb[j+5] == 'l') {
                                    if (bb[j+6] == 't') {
                                        if ((cm[bb[j+7]]&ID) != 0) {
                                            return lexId(builder, j+8);
                                        } else {
                                            i = j+7;
                                            return builder.buildKeyword(Lexeme.KW_DEFAULT, location);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'e':
                b = bb[j+1];
                if (b == 'x') {
                    if (bb[j+2] == 'c') {
                        if (bb[j+3] == 'e') {
                            if (bb[j+4] == 'p') {
                                if (bb[j+5] == 't') {
                                    if (bb[j+6] == 'i') {
                                        if (bb[j+7] == 'o') {
                                            if (bb[j+8] == 'n') {
                                                if ((cm[bb[j+9]]&ID) != 0) {
                                                    return lexId(builder, j+10);
                                                } else {
                                                    i = j+9;
                                                    return builder.buildKeyword(Lexeme.KW_EXCEPTION,
                                                                                location);
                                                }
                                            } else {
                                                return lexId(builder, j+8);
                                            }
                                        } else {
                                            return lexId(builder, j+7);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } if (b == 'n') {
                    if (bb[j+2] == 'u') {
                        if (bb[j+3] == 'm') {
                            if ((cm[bb[j+4]]&ID) != 0) {
                                return lexId(builder, j+5);
                            } else {
                                i = j+4;
                                return builder.buildKeyword(Lexeme.KW_ENUM,
                                                            location);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'f':
                b = bb[j+1];
                if (b == 'l') {
                    if (bb[j+2] == 'o') {
                        if (bb[j+3] == 'a') {
                            if (bb[j+4] == 't') {
                                if ((cm[bb[j+5]]&ID) != 0) {
                                    return lexId(builder, j+6);
                                } else {
                                    i = j+5;
                                    return builder.buildKeyword(Lexeme.KW_FLOAT,
                                                                location);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else if (b == 'i') {
                    if (bb[j+2] == 'x') {
                        if (bb[j+3] == 'e') {
                            if (bb[j+4] == 'd') {
                                if ((cm[bb[j+5]]&ID) != 0) {
                                    return lexId(builder, j+6);
                                } else {
                                    i = j+5;
                                    return builder.buildKeyword(Lexeme.KW_FIXED,
                                                                location);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'i':
                if (bb[j+1] == 'n') {
                    b = bb[j+2];
                    if (b == 't') {
                        if (bb[j+3] == 'e') {
                            if (bb[j+4] == 'r') {
                                if (bb[j+5] == 'f') {
                                    if (bb[j+6] == 'a') {
                                        if (bb[j+7] == 'c') {
                                            if (bb[j+8] == 'e') {
                                                if ((cm[bb[j+9]]&ID) != 0) {
                                                    return lexId(builder, j+10);
                                                } else {
                                                    i = j+9;
                                                    return builder.buildKeyword(Lexeme.KW_INTERFACE, 
                                                                                location);
                                                }
                                            } else {
                                                return lexId(builder, j+8);
                                            }
                                        } else {
                                            return lexId(builder, j+7);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else if (b == 'o') {
                        if (bb[j+3] == 'u') {
                            if (bb[j+4] == 't') {
                                if ((cm[bb[j+5]]&ID) != 0) {
                                    return lexId(builder, j+6);
                                } else {
                                    i = j+5;
                                    return builder.buildKeyword(Lexeme.KW_INOUT,
                                                                location);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else if ((cm[b]&ID) != 0) {
                        return lexId(builder, j+3);
                    } else {
                        i = j+2;
                        return builder.buildKeyword(Lexeme.KW_IN, location);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'l':
                if (bb[j+1] == 'o') {
                    if (bb[j+2] == 'n') {
                        if (bb[j+3] == 'g') {
                            if ((cm[bb[j+4]]&ID) != 0) {
                                return lexId(builder, j+5);
                            } else {
                                i = j+4;
                                return builder.buildKeyword(Lexeme.KW_LONG,
                                                            location);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'm':
                if (bb[j+1] == 'o') {
                    if (bb[j+2] == 'd') {
                        if (bb[j+3] == 'u') {
                            if (bb[j+4] == 'l') {
                                if (bb[j+5] == 'e') {
                                    if ((cm[bb[j+6]]&ID) != 0) {
                                        return lexId(builder, j+7);
                                    } else {
                                        i = j+6;
                                        return builder.buildKeyword(Lexeme.KW_MODULE, location);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'o':
                b = bb[j+1];
                if (b == 'u') {
                    if (bb[j+2] == 't') {
                        if ((cm[bb[j+3]]&ID) != 0) {
                            return lexId(builder, j+4);
                        } else {
                            i = j+3;
                            return builder.buildKeyword(Lexeme.KW_OUT, location);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else if (b == 'n') {
                    if (bb[j+2] == 'e') {
                        if (bb[j+3] == 'w') {
                            if (bb[j+4] == 'a') {
                                if (bb[j+5] == 'y') {
                                    if ((cm[bb[j+6]]&ID) != 0) {
                                        return lexId(builder, j+7);
                                    } else {
                                        i = j+6;
                                        return builder.buildKeyword(Lexeme.KW_ONEWAY, location);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else if (b == 'c') {
                    if (bb[j+2] == 't') {
                        if (bb[j+3] == 'e') {
                            if (bb[j+4] == 't') {
                                if ((cm[bb[j+5]]&ID) != 0) {
                                    return lexId(builder, j+6);
                                } else {
                                    i = j+5;
                                    return builder.buildKeyword(Lexeme.KW_OCTET,
                                                                location);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'r':
                b = bb[j+1];
                if (b == 'e') {
                    if (bb[j+2] == 'a') {
                        if (bb[j+3] == 'd') {
                            if (bb[j+4] == 'o') {
                                if (bb[j+5] == 'n') {
                                    if (bb[j+6] == 'l') {
                                        if (bb[j+7] == 'y') {
                                            if ((cm[bb[j+8]]&ID) != 0) {
                                                return lexId(builder, j+9);
                                            } else {
                                                i = j+8;
                                                return builder.buildKeyword(Lexeme.KW_READONLY,
                                                                            location);
                                            }
                                        } else {
                                            return lexId(builder, j+7);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } if (b == 'a') {
                    if (bb[j+2] == 'i') {
                        if (bb[j+3] == 's') {
                            if (bb[j+4] == 'e') {
                                if (bb[j+5] == 's') {
                                    if ((cm[bb[j+6]]&ID) != 0) {
                                        return lexId(builder, j+7);
                                    } else {
                                        i = j+6;
                                        return builder.buildKeyword(Lexeme.KW_RAISES, location);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 's':
                b = bb[j+1];
                if (b == 't') {
                    if (bb[j+2] == 'r') {
                        b = bb[j+3];
                        if (b == 'i') {
                            if (bb[j+4] == 'n') {
                                if (bb[j+5] == 'g') {
                                    if ((cm[bb[j+6]]&ID) != 0) {
                                        return lexId(builder, j+7);
                                    } else {
                                        i = j+6;
                                        return builder.buildKeyword(Lexeme.KW_STRING, location);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else if (b == 'u') {
                            if (bb[j+4] == 'c') {
                                if (bb[j+5] == 't') {
                                    if ((cm[bb[j+6]]&ID) != 0) {
                                        return lexId(builder, j+7);
                                    } else {
                                        i = j+6;
                                        return builder.buildKeyword(Lexeme.KW_STRUCT, location);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else if (b == 'e') {
                    if (bb[j+2] == 'q') {
                        if (bb[j+3] == 'u') {
                            if (bb[j+4] == 'e') {
                                if (bb[j+5] == 'n') {
                                    if (bb[j+6] == 'c') {
                                        if (bb[j+7] == 'e') {
                                            if ((cm[bb[j+8]]&ID) != 0) {
                                                return lexId(builder, j+9);
                                            } else {
                                                i = j+8;
                                                return builder.buildKeyword(Lexeme.KW_SEQUENCE,
                                                                            location);
                                            }
                                        } else {
                                            return lexId(builder, j+7);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else if (b == 'h') {
                    if (bb[j+2] == 'o') {
                        if (bb[j+3] == 'r') {
                            if (bb[j+4] == 't') {
                                if ((cm[bb[j+5]]&ID) != 0) {
                                    return lexId(builder, j+6);
                                } else {
                                    i = j+5;
                                    return builder.buildKeyword(Lexeme.KW_SHORT,
                                                                location);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else if (b == 'w') {
                    if (bb[j+2] == 'i') {
                        if (bb[j+3] == 't') {
                            if (bb[j+4] == 'c') {
                                if (bb[j+5] == 'h') {
                                    if ((cm[bb[j+6]]&ID) != 0) {
                                        return lexId(builder, j+7);
                                    } else {
                                        i = j+6;
                                        return builder.buildKeyword(Lexeme.KW_SWITCH, location);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 't':
                if (bb[j+1] == 'y') {
                    if (bb[j+2] == 'p') {
                        if (bb[j+3] == 'e') {
                            if (bb[j+4] == 'd') {
                                if (bb[j+5] == 'e') {
                                    if (bb[j+6] == 'f') {
                                        if ((cm[bb[j+7]]&ID) != 0) {
                                            return lexId(builder, j+8);
                                        } else {
                                            i = j+7;
                                            return builder.buildKeyword(Lexeme.KW_TYPEDEF, location);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'u':
                if (bb[j+1] == 'n') {
                    b = bb[j+2];
                    if (b == 's') {
                        if (bb[j+3] == 'i') {
                            if (bb[j+4] == 'g') {
                                if (bb[j+5] == 'n') {
                                    if (bb[j+6] == 'e') {
                                        if (bb[j+7] == 'd') {
                                            if ((cm[bb[j+8]]&ID) != 0) {
                                                return lexId(builder, j+9);
                                            } else {
                                                i = j+8;
                                                return builder.buildKeyword(Lexeme.KW_UNSIGNED, 
                                                                            location);
                                            }
                                        } else {
                                            return lexId(builder, j+7);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else if (b == 'i') {
                        if (bb[j+3] == 'o') {
                            if (bb[j+4] == 'n') {
                                if ((cm[bb[j+5]]&ID) != 0) {
                                    return lexId(builder, j+6);
                                } else {
                                    i = j+5;
                                    return builder.buildKeyword(Lexeme.KW_UNION,
                                                                location);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'v':
                if (bb[j+1] == 'o') {
                    if (bb[j+2] == 'i') {
                        if (bb[j+3] == 'd') {
                            if ((cm[bb[j+4]]&ID) != 0) {
                                return lexId(builder, j+5);
                            } else {
                                i = j+4;
                                return builder.buildKeyword(Lexeme.KW_VOID,
                                                            location);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'w':
                b = bb[j+1];
                if (b == 's') {
                    if (bb[j+2] == 't') {
                        if (bb[j+3] == 'r') {
                            if (bb[j+4] == 'i') {
                                if (bb[j+5] == 'n') {
                                    if (bb[j+6] == 'g') {
                                        if ((cm[bb[j+7]]&ID) != 0) {
                                            return lexId(builder, j+8);
                                        } else {
                                            i = j+7;
                                            return builder.buildKeyword(Lexeme.KW_WSTRING, location);
                                        }
                                    } else {
                                        return lexId(builder, j+6);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } if (b == 'c') {
                    if (bb[j+2] == 'h') {
                        if (bb[j+3] == 'a') {
                            if (bb[j+4] == 'r') {
                                if ((cm[bb[j+5]]&ID) != 0) {
                                    return lexId(builder, j+6);
                                } else {
                                    i = j+5;
                                    return builder.buildKeyword(Lexeme.KW_WCHAR,
                                                                location);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'O':
                if (bb[j+1] == 'b') {
                    if (bb[j+2] == 'j') {
                        if (bb[j+3] == 'e') {
                            if (bb[j+4] == 'c') {
                                if (bb[j+5] == 't') {
                                    if ((cm[bb[j+6]]&ID) != 0) {
                                        return lexId(builder, j+7);
                                    } else {
                                        i = j+6;
                                        return builder.buildKeyword(Lexeme.KW_OBJECT, location);
                                    }
                                } else {
                                    return lexId(builder, j+5);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'T':
                if (bb[j+1] == 'R') {
                    if (bb[j+2] == 'U') {
                        if (bb[j+3] == 'E') {
                            if ((cm[bb[j+4]]&ID) != 0) {
                                return lexId(builder, j+5);
                            } else {
                                i = j+4;
                                return builder.buildKeyword(Lexeme.KW_TRUE,
                                                            location);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'F':
                if (bb[j+1] == 'A') {
                    if (bb[j+2] == 'L') {
                        if (bb[j+3] == 'S') {
                            if (bb[j+4] == 'E') {
                                if ((cm[bb[j+5]]&ID) != 0) {
                                    return lexId(builder, j+6);
                                } else {
                                    i = j+5;
                                    return builder.buildKeyword(Lexeme.KW_FALSE,
                                                                location);
                                }
                            } else {
                                return lexId(builder, j+4);
                            }
                        } else {
                            return lexId(builder, j+3);
                        }
                    } else {
                        return lexId(builder, j+2);
                    }
                } else {
                    return lexId(builder, j+1);
                }
            case 'g': case 'h': case 'j': case 'k': case 'n':
            case 'p': case 'q': case 'x': case 'y': case 'z':
            case 'A': case 'B': case 'C': case 'D': case 'E': case 'G':
            case 'H': case 'I': case 'J': case 'L': case 'M': case 'N':
            case 'P': case 'Q': case 'R': case 'S': case 'U': case 'V':
            case 'W': case 'X': case 'Y': case 'Z':
                return lexId(builder, j+1);
            case '0':
                b = bb[j+1];
                if (b == 'x' || b == 'X') {
                    return lexHexInt(builder, j);
                } else {
                    return lexOctalInt(builder, j);
                }
            case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                return lexDecimalInt(builder, j, b-'0');
            case '#':
                b = bb[j+1];
                switch (b) {
                case 'l':
                    if (bb[j+2] == 'i' && bb[j+3] == 'n' && bb[j+4] == 'e'
                    && ((cm[bb[j+5]]&ID) == 0)) {
                        i = j+5;
                        return lexLine(builder);
                    } else {
                        return errorHandler.illegalDirective(location);
                    }
                case 'p':
                    if (bb[j+2] == 'r' && bb[j+3] == 'a' && bb[j+4] == 'g'
                    &&  bb[j+5] == 'm' && bb[j+6] == 'a'
                    && ((cm[bb[j+7]]&ID) == 0)) {
                        i = j+7;
                        return lexPragma(builder);
                    } else {
                        return errorHandler.illegalDirective(location);
                    }
                case 'i':
                    b = bb[j+2];
                    if (b == 'n') {
                        if (bb[j+3] == 'c' && bb[j+4] == 'l' && bb[j+5] == 'u'
                        &&  bb[j+6] == 'd' && bb[j+7] == 'e') {
                            i = j+8;
                            return lexInclude(builder);
                        } else {
                            return errorHandler.illegalDirective(location);
                        }
                    } else if (b == 'f') {
                        b = bb[j+3];
                        if (b == 'd') {
                            if (bb[j+4] == 'e' && bb[j+5] == 'f'
                            && ((cm[bb[j+6]]&ID) == 0)){
                                i = j+6;
                                return lexIfdef(builder);
                            } else {
                                return errorHandler.illegalDirective(location);
                            }
                        } else if (b == 'n') {
                            if (bb[j+4] == 'd' && bb[j+5] == 'e'
                            && bb[j+6]== 'f' && ((cm[bb[j+7]]&ID) == 0)) {
                                i = j+7;
                                return lexIfndef(builder);
                            } else {
                                return errorHandler.illegalDirective(location);
                            }
                        } else if (b == ' ' && ((cm[bb[j+4]]&ID) == 0)) {
                            i = j+3;
                            return lexIf(builder);
                        } else {
                            return errorHandler.illegalDirective(location);
                        }
                    }
                case 'd':
                    if (bb[j+2] == 'e' && bb[j+3] == 'f' && bb[j+4] == 'i'
                    &&  bb[j+5] == 'n' && bb[j+6] == 'e' 
                    && ((cm[bb[j+7]]&ID) == 0)) {
                        i = j+7;
                        return lexDefine(builder);
                    } else {
                        return errorHandler.illegalDirective(location);
                    }
                case 'e':
                    b = bb[j+2];
                    if (b == 'n') {
                        if (bb[j+3] == 'd' && bb[j+4] == 'i' && bb[j+5] == 'f'
                        && ((cm[bb[j+6]]&ID) == 0)){
                            i = j+6;
                            return lexEndif(builder);
                        } else {
                            return errorHandler.illegalDirective(location);
                        }
                    } else if (b == 'l') {
                        if (bb[j+3] == 's' && bb[j+4] == 'e'
                        && ((cm[bb[j+5]]&ID) == 0)){
                            i = j+5;
                            return lexElse(builder);
                        } else {
                            return errorHandler.illegalDirective(location);
                        }
                    } else {
                        return errorHandler.illegalDirective(location);
                    }
                default:
                    return errorHandler.illegalDirective(location);
                }
            case '<':
                if (bb[j+1] == '<') {
                    i = j + 2;
                    return builder.buildOperator(bb, j, j+2,
                                                 Lexeme.OPEN_ANGLE_PAIR,
                                                 location);
                } else {
                    i = j + 1;
                    return builder.buildOperator(bb, j, j+1, Lexeme.OPEN_ANGLE,
                                                 location);
                }
            case '>':
                if (bb[j+1] == '>') {
                    i = j + 2;
                    return builder.buildOperator(bb, j, j+2,
                                                 Lexeme.CLOSE_ANGLE_PAIR,
                                                 location);
                } else {
                    i = j + 1;
                    return builder.buildOperator(bb, j, j+1, Lexeme.CLOSE_ANGLE,
                                                 location);
                }
            case '(':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.OPEN_PAREN,
                                             location);
            case ')':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.CLOSE_PAREN,
                                             location);
            case '{':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.OPEN_BRACE,
                                             location);
            case '}':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.CLOSE_BRACE,
                                             location);
            case '[':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.OPEN_BRACKET,
                                             location);
            case ']':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.CLOSE_BRACKET,
                                             location);
            case ',':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.COMMA, location);
            case ';':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.SEMI_COLON,
                                             location);
            case ':':
                if (bb[j+1] == ':') {
                    i = j + 2;
                    return builder.buildOperator(bb, j, j+2, Lexeme.COLON_PAIR,
                                                 location);
                } else {
                    i = j + 1;
                    return builder.buildOperator(bb, j, j+1, Lexeme.COLON,
                                                 location);
                }
            case '=':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.EQUAL, location);
            case '|':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.BAR, location);
            case '^':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.CIRCUMFLEX,
                                             location);
            case '&':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.AMPERSAND,
                                             location);
            case '*':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.STAR, location);
            case '-':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.HYPHEN,location);
            case '+':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.CROSS, location);
            case '/':
                if (bb[j+1] == '/') {
                    if (processLineComment(builder, j+2) == 0)
                        return 0;
                    j = i;
                    break;
                } else if (bb[j+1] == '*') {
                    int r = processBlockComment(builder, j+2);
                    if (r <= 0)
                        return r;
                    j = i;
                    break;
                } else {
                    i = j + 1;
                    return builder.buildOperator(bb, j, j+1, Lexeme.SLASH,
                                                 location);
                }
            case '%':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.PERCENT,
                                             location);
            case '~':
                i = j + 1;
                return builder.buildOperator(bb, j, j+1, Lexeme.TILDE, location);
            case '"':
                return lexString(builder, j+1);
            case '\'':
                return lexChar(builder, j);
            case '\n':
                if (j != end) {
                    line += 1;
                    j += 1;
                    lastLine = i = j;
                    // cip/changes/lex-pragma
                    if (builder == userBuilder) {
                        builder.buildLineEnd(0);
                    } else {
                        return builder.buildLineEnd(0);
                    }
                } else {
                    int nRead = input.read(bb, 0);
                    if (nRead < 0) {
                        return builder.buildInputEnd(location);
                    }
                    lastLine = i = j = 0;
                    end = nRead;
                    bb[nRead] = (byte)'\n';
                }
                break;
            default:
                return errorHandler.illegalCharacter(b, location);
            }
        }
    }

    public int lex() throws Exception {
        return lex(userBuilder);
    }

    public Location getLocation() {
        return location;
    }

    public void close() {
        /* there is nothing to do in this implementation */
    }
}
