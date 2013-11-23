package cip.byteinputlexerbyteinputlexerpp;

import cip.Location;
import cip.byteinputlexer.ByteInputLexer;
import cip.byteinputlexer.DefaultDirectiveBuilders;
import cip.byteinputlexer.DirectiveBuilders;
import cip.byteinputlexer.FileInput;
import cip.byteinputlexer.Input;


/**
 * A naive pretty printer for CORBA v2.2 IDL.
 * <p>
 * Options: [ -bsd | -knr | -knr2 ] file-name.
 * <p>
 */
class Main {
    /*
     * TODO:
     *.sep: should have an option to set the brace style separately
     * from the indent level.
     *.check: should do more rigorous checking of the arguments to make
     * sure they are correct.
     */
    public static void main(String[] args) throws Exception {
        byte[] buffer = new byte[4097];
        cip.byteinputlexer.ErrorHandler eh = new ErrorHandler();
        int indent = 5;
        boolean braceOnSameLine = true;
        String fileName;

        for (int i = 0; ;) {
           if (args[i].equals("-bsd")) {
             indent = 8;
             braceOnSameLine = false;
             i += 1;
           } else if (args[i].equals("-knr")) {
             indent = 5;
             braceOnSameLine = true;
             i += 1;
           } else if (args[i].equals("-knr2")) {
             indent = 2;
             braceOnSameLine = true;
             i += 1;
           } else if (i == args.length-1) {
               fileName = args[i];
               break;
           } else {
               System.out.println("illegal option " + args[i]);
               System.exit(1);
           }
        }
        Input input = new FileInput(fileName);
        cip.byteinputlexer.Builder b = new Builder(indent, braceOnSameLine);
        Location loc = new Location(args[0]);
        DirectiveBuilders dbs = new DefaultDirectiveBuilders();
        try {
             ByteInputLexer lexer = 
                 new ByteInputLexer(buffer, input, loc, b, dbs, eh);
             while (lexer.lex() > 0) {
                 /* do nothing */
             }
             lexer.close();
        } catch (Exception ex) {
            input.close();
            throw ex;
        }
    }
}
