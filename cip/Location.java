package cip;


/**
 * A <code>Location</code> identifies the position of a <code>Lexeme</code>
 * in the input.  The various attributes are public because for performance
 * reasons the same <code>Location</code> is used for each lexeme and it
 * is copied when it is necessary to preserve the value.
 */
public class Location {
    /**
     * The name of the input that the associated lexeme was scanned from.
     */
    public String inputName;

    /**
     * The number of the line that the associated lexeme starts on.
     * Note that lines start at 0.
     */
    public int    line;

    /**
     * The number of the column that the associated lexeme starts on.
     * Note that columns start at 1.
     */
    public int    column;


    /** 
     * Create a <code>Location</code>
     */
    public Location(String inputName) {
        this.inputName = inputName;
        this.line = 0;
        this.column = 1;
    }

    /**
     * Create a copy of the <code>Location</code>.
     */
    public Location(Location l) {
        this.inputName = l.inputName;
        this.line = l.line;
        this.column = l.column;
    }

}
