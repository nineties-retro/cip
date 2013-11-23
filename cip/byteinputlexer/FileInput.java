package cip.byteinputlexer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * An implementation of <code>Input</code> that obtains the bytes from
 * a file.
 */
public class FileInput implements Input {
    private String fileName;
    private FileInputStream input;

    /**
     * Creates a <code>FileInput</code> on the file with the given
     * <code>fileName</code>.
     */
    public FileInput(String fileName) throws FileNotFoundException {
        this.input = new FileInputStream(fileName);
        this.fileName = fileName;
    }

    /**
     * Returns the name of the file that the <code>FileInput</code> is
     * associated with.
     */
    public String getName() {
        return fileName;
    }

    /**
     * <code>read</code> reads bytes into <code>buffer</code> starting
     * at <code>offset</code>.  It returns the number read or -1 if
     * the end of input is reached.
     */
    public int read(byte[] buffer, int offset) throws Exception {
        return input.read(buffer, offset, buffer.length-offset-1);
    }

    /**
     * Close the file 
     */
    public void close() throws Exception {
        input.close();
    }
}
