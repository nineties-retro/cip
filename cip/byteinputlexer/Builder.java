package cip.byteinputlexer;

import cip.Location;

/**
 * <code>Builder</code> is an instance of the Builder pattern It
 * contains a method for any lexeme that needs to pass more than its
 * own identity to the user of a <code>Lexer</code>.  <p> Note that
 * the <code>Location</code> passed to each routine is shared across
 * all calls and so if you need to preserve the <code>Location</code>
 * you should copy it before the call returns.
 */
public interface Builder {
    /**
     * <code>buildChar</code> is called when a CORBA 
     * <code>character_literal</code> is recognized.
     */
    public int buildChar(byte b, Location l) throws Exception;

    /**
     * <code>buildIdChunk</code> is called when an <code>identifier</code>
     * is recognized in the input but the input buffer was exhausted before
     * the end of the <code>identifier</code> was reached.  This gives a
     * <code>Builder</code> a chance to store the portion of the
     * <code>identifier</code> string recognized so far before
     * the input buffer is overwritten by the next block of characters.
     */
    public void buildIdChunk(byte[] buffer, int start, int end, Location l)
        throws Exception;

    /**
     * <code>buildId</code> is called when an <code>identifier</code>
     * is recognized in the input.  Note that due to the input being buffered
     * there may have been zero or more calls to <code>buildIdChunk</code>
     * that preceded the call to <code>buildId</code> and so the full identifer
     * consists of the concatenation of the strings identified in those calls
     * and the string (possibly empty) identified in this call.
     */
    public int buildId(byte[] buffer, int start, int end, Location l)
        throws Exception;

    /**
     * <code>buildStringChunk</code> is called when a <code>string_literal</code>
     * is recognized in the input but the input buffer was exhausted before
     * the end of the string was reached.  This gives a <code>Builder</code>
     * a chance to store the portion of the string recognized so far before
     * the input buffer is overwritten by the next block of characters.
     */
    public void buildStringChunk(byte[] buffer, int start, int end,
                                 Location l) throws Exception;

    /**
     * <code>buildStringChar</code> is called when a CORBA character escape
     * is recognized inside a CORBA string literal.
     */
    public void buildStringChar(byte b, Location l) throws Exception;

    /**
     * <code>buildString</code> is called when a <code>string_literal</code>
     * is recognized in the input.
     */
    public int buildString(byte[] buffer, int start, int end, Location l)
        throws Exception;

    /**
     * <code>buildInt</code> is called when a CORBA <code>integer_literal</code>
     * is recognized.  <code>type</code> indicates whether the
     * integer was scanned as decimal (0), octal (<0) or hexadecimal (>0).
     */
    public int buildInt(long value, int type, Location l)
        throws Exception;

    /**
     * @param code is the code of the keyword, it should lie between
     * <code>cip.lexer.Lexeme.KW_ANY</code> and 
     * <code>cip.lexer.Lexeme.KW_TRUE</code> inclusive.
     */
    public int buildKeyword(int code, Location l) throws Exception;

    /**
     * <code>buildOperator</code> is called when an operator is recognized.
     * An <code>operator</code> is defined as any terminal symbol in the
     * grammar other than a literal or a keyword.  For example, <code>+</code>
     * is an operator.  The start and end locations in the input buffer
     * are passed through in order to facilitate pretty printing, but this
     * feature is likely to be removed in the near future since that information
     * can be determined via the <code>symbol</code> which an integer betwee
     * <code>cip.lexer.Lexeme.AMPERSAND</code> and 
     * <code>cip.lexer.Lexeme.TILDE</code> inclusive.
     */
    public int buildOperator(byte[] buffer, int start, int end,
                             int symbol, Location l) throws Exception;
  
    /**
     * <code>buildLineEnd</code> is called when the end of each input
     * line has been reached.  <code>buildLine</code> is slightly unusual
     * in that its argument is the value it should return should it the
     * method succeed in doing whatever it needs to do.
     */
    public int buildLineEnd(int returnValue) throws Exception;

    /**
     * <code>buildInputEnd</code> is called when the end of the input
     * has been reached.
     */
    public int buildInputEnd(Location l) throws Exception;
}
