package cip.byteinputlexerbyteinputlexertest;

import cip.Location;
import cip.byteinputlexer.ByteInputLexer;
import cip.byteinputlexer.DefaultDirectiveBuilders;
import cip.byteinputlexer.DirectiveBuilders;
import cip.byteinputlexer.FileInput;
import cip.byteinputlexer.Input;

/**
 * A simple test-harness for a <code>cip.byteinputlexer.ByteInputLexer</code>.
 * It accepts the name of a CORBA IDL file as the only argument and lexes
 * the file outputting the line and column of each lexeme that is recognized.
 */
class Main {

    public static void main(String[] args) throws Exception {
        byte[] buffer = new byte[33];
        Input input = new FileInput(args[0]);
        cip.byteinputlexer.Builder b = new Builder();
        DirectiveBuilders dbs = new DefaultDirectiveBuilders();
        Location loc = new Location(args[0]);
        cip.byteinputlexer.ErrorHandler eh = new ErrorHandler();
        ByteInputLexer l = new ByteInputLexer(buffer, input, loc, b, dbs, eh);
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
