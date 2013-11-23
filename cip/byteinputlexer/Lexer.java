package cip.byteinputlexer;

import cip.Location;
import cip.lexer.Lexeme;

/**
 * <code>Lexer</code> will lex bytes to form lexemes.
 */
public class Lexer implements cip.lexer.Lexer {
    private ByteInputLexer lexer;
    private BuilderAdapter builderAdapter;

    public Lexer(Input input, cip.lexer.Builder b, Location loc, 
                 ErrorHandler errorHandler) {
        byte[] buffer = new byte[129];
        DirectiveBuilders dbs = new DefaultDirectiveBuilders();
        this.builderAdapter = new BuilderAdapter(b);
        this.lexer = new ByteInputLexer(buffer, input, loc, this.builderAdapter,
                                        dbs, errorHandler);
    }

    public Lexer(Input input, cip.lexer.Builder b, ErrorHandler errorHandler) {
        this(input, b, new Location(input.getName()), errorHandler);
    }

    public int lex(cip.lexer.Builder builder) throws Exception {
        builderAdapter.setBuilder(builder);
        return lexer.lex(builderAdapter);
    }

    public int lex() throws Exception {
        return lexer.lex(builderAdapter);
    }

    public Location getLocation() {
        return lexer.getLocation();
    }

    public void close() {
        /* does nothing */
    }

}
