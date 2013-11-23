/*
 *
 */

package cip.byteinputlexer;

import cip.Location;

/**
 * A <code>Lexer</code> does not output error reports itself.  Instead it
 * delegates the responsibility of what to do when an error occurs to an
 * instance of <code>ErrorHandler</code>.  There is a separate method for
 * each of the different types of error that a <code>Lexer</code> can
 * locate.  Each method should return a non-zero value to indicate the
 * error is fatal otherwise it should return 0.
 * <p>
 * Note that the <code>Location</code> passed to each routine is shared
 * across all calls and so if you need to preserve the <code>Location</code>
 * you should copy it before the call returns.
 */
public interface ErrorHandler {
    /**
     * <code>emptyChar</code> is called when an empty character literal
     * has been discovered in the input i.e. the input contains ''.
     */
    public int emptyChar(Location location);

    /**
     * <code>emptyHexLiteral</code> is called when an empty hexadecimal
     * literal is discovered in the input i.e. the input contains 
     * '\x' or a string contains "\x"
     */
    public int emptyHexLiteral(Location location);

    /**
     * <code>illegalCharEscape</code> is called when the character following
     * a character escape (\) is not legal.
     * @param b - The illegal character.
     */
    public int illegalCharEscape(byte b, Location location);

    /**
     * <code>illegalCharacter</code> is called when an illegal character is
     * discovered in the input.
     * @param b - The illegal character.
     */
    public int illegalCharacter(byte b, Location location);

    /**
     * <code>illegalDirective</code> is called when an illegal preprocessor
     * directive is discovered in the input, that is any directive other
     * than those listed in <URI:cip/java/README#input.directives>.
     */
    public int illegalDirective(Location location);

    /**
     * <code>malformedDirective</code> is called when one of the 
     * preprocessor directives <URI:cip/java/README#input.directives> has
     * been recognized, but any of its arguments are not correctly formed.
     * For example, if an #include is not followed by a string.
     */
    public int malformedDirective(Location location);

    /**
     * <code>charOverflow</code> is called if an octal or hexadecimal
     * character representation is larger than 255.
     */
    public int charOverflow(int value, Location location);

    /**
     * <code>integerOverflow</code> is called if an integer that is too
     * large is detected.
     */
    public int integerOverflow(Location location);

    /**
     * <code>truncatedHexInt</code> is called if the prefix of a hexadecimal
     * integer (0x) is detected but there are no following hexadecimal 
     * characters.
     */
    public int truncatedHexInt(Location location);

    /**
     * <code>unterminatedChar</code> is called if the input runs out before
     * the closing "'" of a character is detected.
     * @param location - the location of the start of the unterminated character.
     */
    public int unterminatedChar(Location location);

    /**
     * <code>unterminatedComment</code> is called if the input runs out before
     * a block comment is terminated.
     * @param location - the location of the start of the unterminated comment.
     */
    public int unterminatedComment(Location location);

    /**
     * <code>unterminatedString</code> is called if the input runs out before
     * the closing " of a string is detected.
     * @param location - the location of the start of the unterminated string.
     */
    public int unterminatedString(Location location);
}
