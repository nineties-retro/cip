package cip.byteinputlexer;

/**
 * All input to the <code>Lexer</code> comes through an <code>Input</code>.
 * This hides the detail of whether the input comes from a file, a stream,
 * a string, or anything else that can produce an array of bytes.
 */
public interface Input {

    /**
     * Returns a name by which this input can be known.
     */
    public String getName();

    /**
     * Fills <code>buffer</code> starting at <code>offset</code>.
     * Returns the number of bytes actually read or -1 if the end
     * of the input has been reached.
     */
    public int read(byte[] buffer, int offset) throws Exception;

    /**
     * Close the input.  This should be done once no more input is
     * required.  Once done, calling <code>read</code> or <code>close</code>
     * will produce an unspecified result.
     */
    public void close() throws Exception;
}
