package cip.parsertest;

import cip.byteinputlexer.Input;
import cip.byteinputlexer.FileInput;
import cip.byteinputlexer.Lexer;
import cip.parser.ErrorLocator;
import cip.parser.Parser;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Input input = new FileInput(args[0]);
        try {
            cip.ast.Builder astBuilder = new DummyAstBuilder();
            cip.lexer.Builder lb = new LexerBuilder(astBuilder);
            cip.byteinputlexer.ErrorHandler leh = new LexerErrorHandler();
            Lexer lexer = new Lexer(input, lb, leh);
            ErrorLocator el = new ErrorLocatorAdapter(lexer);
            cip.parser.ErrorHandler peh = new ParserErrorHandler(el);
            Parser parser = new Parser(lexer, astBuilder, peh);
            parser.parse();
        } catch (Exception ex) {
          input.close();
          throw ex;
        }
    }
}
