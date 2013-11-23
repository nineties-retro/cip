package cip.byteinputlexerlexertest;

import cip.byteinputlexer.Input;
import cip.byteinputlexer.FileInput;

/**
 * A simple test-harness for a <code>cip.byteinputlexer.Lexer</code>.  It
 * accepts the name of a CORBA IDL file as the only argument and lexes
 * the file outputting the line and column of each lexeme that is recognized.
 */
class Main {

    public static void main(String[] args) throws Exception {
        Input input = new FileInput(args[0]);
        cip.lexer.Builder b = new Builder();
        cip.byteinputlexer.ErrorHandler eh = new ErrorHandler();
        cip.lexer.Lexer l = new cip.byteinputlexer.Lexer(input, b, eh);

        try {
            while (l.lex() > 0) {
              /* do nothing */
            }
        } catch (Exception ex) {
            input.close();
            throw ex;
        }
    }
}
