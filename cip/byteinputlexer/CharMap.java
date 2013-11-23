package cip.byteinputlexer;

/**
 * <code>CharMap</code> contains a table which maps from a <code>byte</code>
 * to the character classes to which it belongs.
 */
final class CharMap {
    /* This could be folded into Lexer.java but keeping it seperate makes
     * it easier to auto generate the information if necessary
     */
    static final int ILLEGAL = (1<<0);
    static final int BLANK =   (1<<1);
    static final int LETTER =  (1<<2);
    static final int DIGIT =   (1<<3);
    static final int NEWLINE = (1<<4);
    static final int HEX =     (1<<5);

    static final int map[] = {
        /* 000 */ ILLEGAL,
        /* 001 */ ILLEGAL,
        /* 002 */ ILLEGAL,
        /* 003 */ ILLEGAL,
        /* 004 */ ILLEGAL,
        /* 005 */ ILLEGAL,
        /* 006 */ ILLEGAL,
        /* 007 */ BLANK,    /* \b */
        /* 008 */ ILLEGAL,  /*    */
        /* 009 */ BLANK,    /* \t */
        /* 010 */ NEWLINE,  /* \n */
        /* 011 */ BLANK,    /* \v */
        /* 012 */ BLANK,    /* \f */
        /* 013 */ BLANK,    /* \r */
        /* 014 */ ILLEGAL,
        /* 015 */ ILLEGAL,
        /* 016 */ ILLEGAL,
        /* 017 */ ILLEGAL,
        /* 018 */ ILLEGAL,
        /* 019 */ ILLEGAL,
        /* 020 */ ILLEGAL,
        /* 021 */ ILLEGAL,
        /* 022 */ ILLEGAL,
        /* 023 */ ILLEGAL,
        /* 024 */ ILLEGAL,
        /* 025 */ ILLEGAL,
        /* 026 */ ILLEGAL,
        /* 027 */ ILLEGAL,
        /* 028 */ ILLEGAL,
        /* 029 */ ILLEGAL,
        /* 030 */ ILLEGAL,
        /* 031 */ ILLEGAL,
        /* 032 */ BLANK,    /*   */
        /* 033 */ ILLEGAL,  /* ! */
        /* 034 */ ILLEGAL,  /* " */
        /* 035 */ ILLEGAL,  /* # */
        /* 036 */ ILLEGAL,  /* $ */
        /* 037 */ ILLEGAL,  /* % */
        /* 038 */ ILLEGAL,  /* & */
        /* 039 */ ILLEGAL,  /* ' */
        /* 040 */ ILLEGAL,  /* ( */
        /* 041 */ ILLEGAL,  /* ) */
        /* 042 */ ILLEGAL,  /* * */
        /* 043 */ ILLEGAL,  /* + */
        /* 044 */ ILLEGAL,  /* , */
        /* 045 */ ILLEGAL,  /* - */
        /* 046 */ ILLEGAL,  /* . */
        /* 047 */ ILLEGAL,  /* / */
        /* 048 */ DIGIT,    /* 0 */
        /* 049 */ DIGIT,
        /* 050 */ DIGIT,
        /* 051 */ DIGIT,
        /* 052 */ DIGIT,
        /* 053 */ DIGIT,
        /* 054 */ DIGIT,
        /* 055 */ DIGIT,
        /* 056 */ DIGIT,
        /* 057 */ DIGIT,    /* 9 */
        /* 058 */ ILLEGAL,  /* : */
        /* 059 */ ILLEGAL,  /* ; */
        /* 060 */ ILLEGAL,  /* < */
        /* 061 */ ILLEGAL,  /* = */
        /* 062 */ ILLEGAL,  /* > */
        /* 063 */ ILLEGAL,  /* ? */
        /* 064 */ ILLEGAL,  /* @ */
        /* 065 */ LETTER|HEX, /* A */
        /* 066 */ LETTER|HEX, /* B */
        /* 067 */ LETTER|HEX, /* C */
        /* 068 */ LETTER|HEX, /* D */
        /* 069 */ LETTER|HEX, /* E */
        /* 070 */ LETTER|HEX, /* F */
        /* 071 */ LETTER,
        /* 072 */ LETTER,
        /* 073 */ LETTER,
        /* 074 */ LETTER,
        /* 075 */ LETTER,
        /* 076 */ LETTER,
        /* 077 */ LETTER,
        /* 078 */ LETTER,
        /* 079 */ LETTER,
        /* 080 */ LETTER,
        /* 081 */ LETTER,
        /* 082 */ LETTER,
        /* 083 */ LETTER,
        /* 084 */ LETTER,
        /* 085 */ LETTER,
        /* 086 */ LETTER,
        /* 087 */ LETTER,
        /* 088 */ LETTER,
        /* 089 */ LETTER,
        /* 090 */ LETTER,   /* Z */
        /* 091 */ ILLEGAL,  /* [ */
        /* 092 */ ILLEGAL,  /* \ */
        /* 093 */ ILLEGAL,  /* ] */
        /* 094 */ ILLEGAL,  /* ^ */
        /* 095 */ LETTER,   /* _ */
        /* 096 */ ILLEGAL,  /* ` */
        /* 097 */ LETTER|HEX, /* a */
        /* 098 */ LETTER|HEX, /* b */
        /* 099 */ LETTER|HEX, /* c */
        /* 100 */ LETTER|HEX, /* d */
        /* 101 */ LETTER|HEX, /* e */
        /* 102 */ LETTER|HEX, /* f */
        /* 103 */ LETTER,
        /* 104 */ LETTER,
        /* 105 */ LETTER,
        /* 106 */ LETTER,
        /* 107 */ LETTER,
        /* 108 */ LETTER,
        /* 109 */ LETTER,
        /* 110 */ LETTER,
        /* 111 */ LETTER,
        /* 112 */ LETTER,
        /* 113 */ LETTER,
        /* 114 */ LETTER,
        /* 115 */ LETTER,
        /* 116 */ LETTER,
        /* 117 */ LETTER,
        /* 118 */ LETTER,
        /* 119 */ LETTER,
        /* 120 */ LETTER,
        /* 121 */ LETTER,
        /* 122 */ LETTER,  /* z */
        /* 123 */ ILLEGAL, /* { */
        /* 124 */ ILLEGAL, /* | */
        /* 125 */ ILLEGAL, /* } */
        /* 126 */ ILLEGAL, /* ~ */
        /* 127 */ ILLEGAL  /* DEL */
    };
}
