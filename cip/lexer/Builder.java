package cip.lexer;

import cip.Identifier;
import cip.Location;


/**
 * <code>Builder</code> is an instance of the Builder pattern
 * It contains a method for any lexeme that
 * needs to pass more than its own identity to the user of a <code>Lexer</code>.
 * <p>
 * Note that the <code>Location</code> passed to each routine is shared
 * across all calls and so if you need to preserve the <code>Location</code>
 * you should copy it before the call returns.
 */
public interface Builder {
    /**
     * <code>buildChar</code> is called when a CORBA 
     * <code>character_literal</code> is recognized.
     */
    public int buildChar(byte b, Location l) throws Exception;

    /**
     * <code>buildId</code> is called when an <code>identifier</code>
     * is recognized in the input.
     */
    public int buildId(Identifier id, Location l) throws Exception;

    /**
     * <code>buildString</code> is called when a <code>string_literal</code>
     * is recognized in the input.
     */
    public int buildString(String s, Location l) throws Exception;

    /**
     * <code>buildInt</code> is called when a CORBA <code>integer_literal</code>
     * is recognized.  <code>type</code> indicates whether the
     * integer was scanned as decimal (0), octal (<0) or hexadecimal (>0).
     */
    public int buildInt(long value, Location l) throws Exception;

    /**
     * <code>buildKeyword</code> is called when one of the CORBA 
     * keywords is recognized.
     *
     * @param code is the code of the keyword, it should lie between
     * <code>cip.lexer.Lexeme.KW_ANY</code> and 
     * <code>cip.lexer.Lexeme.KW_TRUE</code> inclusive.
     */
    public int buildKeyword(int code, Location l) throws Exception;

    /**
     * <code>buildOperator</code> is called when an operator is recognized.
     * An <code>operator</code> is defined as any terminal symbol in the
     * grammar other than a literal or a keyword.  For example, <code>+</code>
     * is an operator.  The <code>symbol</code> identifies which operator
     * has been recognized, it should lie between
     * <code>cip.lexer.Lexeme.AMPERSAND</code> and 
     * <code>cip.lexer.Lexeme.TILDE</code> inclusive.
     */
    public int buildOperator(int symbol, Location l) throws Exception;
  
    /**
     * <code>buildInputEnd</code> is called when the end of the input
     * has been reached.
     */
    public int buildInputEnd(Location l) throws Exception;
}
