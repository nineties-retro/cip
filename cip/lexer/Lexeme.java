package cip.lexer;

/**
 * Each type of lexeme that a <code>Lexer</code> can produce is listed
 * here.
 */
public final class Lexeme {
    /*
     * These could have been included in the <code>Lexer</code>.
     * I'm not sure why they weren't.
     */
    public final static int KW_ANY =        1;
    public final static int KW_ATTRIBUTE =  2;
    public final static int KW_BOOLEAN =    3;
    public final static int KW_CASE =       4;
    public final static int KW_CHAR =       5;
    public final static int KW_CONST =      6;
    public final static int KW_CONTEXT =    7;
    public final static int KW_DEFAULT =    8;
    public final static int KW_DOUBLE =     9;
    public final static int KW_ENUM =      10;
    public final static int KW_EXCEPTION = 11;
    public final static int KW_FIXED =     12;
    public final static int KW_FLOAT =     13;
    public final static int KW_IN =        14;
    public final static int KW_INOUT =     15;
    public final static int KW_INTERFACE = 16;
    public final static int KW_LONG =      17;
    public final static int KW_MODULE =    18;
    public final static int KW_OCTET =     19;
    public final static int KW_ONEWAY =    20;
    public final static int KW_OUT =       21;
    public final static int KW_RAISES =    22;
    public final static int KW_READONLY =  23;
    public final static int KW_SEQUENCE =  24;
    public final static int KW_SHORT =     25;
    public final static int KW_STRING =    26;
    public final static int KW_STRUCT =    27;
    public final static int KW_SWITCH =    28;
    public final static int KW_TYPEDEF=    29;
    public final static int KW_UNSIGNED =  30;
    public final static int KW_UNION =     31;
    public final static int KW_VOID =      32;
    public final static int KW_WCHAR =     33;
    public final static int KW_WSTRING =   34;
    public final static int KW_FALSE =     35;
    public final static int KW_OBJECT =    36;
    public final static int KW_TRUE =      37;

    public final static int CHAR_LITERAL =        38;
    public final static int FLOAT_LITERAL =       39;
    public final static int ID_LITERAL =          40;
    public final static int INT_LITERAL =         41;
    public final static int STRING_LITERAL =      42;
    public final static int WIDE_CHAR_LITERAL =   43;
    public final static int WIDE_STRING_LITERAL = 44;

    public final static int AMPERSAND =        45;
    public final static int BAR =              46;
    public final static int CIRCUMFLEX =       47;
    public final static int CLOSE_ANGLE =      48;
    public final static int CLOSE_ANGLE_PAIR = 49;
    public final static int CLOSE_BRACE =      50;
    public final static int CLOSE_BRACKET =    51;
    public final static int CLOSE_PAREN =      52;
    public final static int COLON =            53;
    public final static int COLON_PAIR =       54;
    public final static int COMMA =            55;
    public final static int CROSS =            56;
    public final static int EQUAL =            57;
    public final static int HYPHEN =           58;
    public final static int OPEN_ANGLE =       59;
    public final static int OPEN_ANGLE_PAIR =  60;
    public final static int OPEN_BRACE =       61;
    public final static int OPEN_BRACKET =     62;
    public final static int OPEN_PAREN =       63;
    public final static int PERCENT =          64;
    public final static int SEMI_COLON =       65;
    public final static int SLASH =            66;
    public final static int STAR =             67;
    public final static int TILDE =            68;
}
