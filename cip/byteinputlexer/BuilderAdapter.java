package cip.byteinputlexer;

import cip.Identifier;
import cip.Location;
import cip.lexer.Lexeme;

/**
 * <code>BuilderAdapter</code> adapts a <code>cip.lexer.Builder</code> so
 * that it satisfies the <code>Builder</code> interface.
 */
class BuilderAdapter implements Builder {
    private cip.lexer.Builder builder;
    private StringBuffer idStore;
    private StringBuffer strStore;

    BuilderAdapter(cip.lexer.Builder builder) {
        this.builder = builder;
        idStore = new StringBuffer();
        strStore = new StringBuffer();
    }

    BuilderAdapter() {
        idStore = new StringBuffer();
        strStore = new StringBuffer();
    }

    void setBuilder(cip.lexer.Builder builder) {
        this.builder = builder;
    }

    public int buildChar(byte b, Location l) throws Exception {
        return builder.buildChar(b, l);
    }

    public void buildIdChunk(byte[] buffer, int start, int end,
                             Location l) throws Exception {
        idStore.append(new String(buffer, start, end-start));
    }

    public int buildId(byte[] buffer, int start, int end,
                       Location l) throws Exception {
        idStore.append(new String(buffer, start, end-start));
        Identifier id = new Identifier(idStore.toString());
        idStore = new StringBuffer();
        return builder.buildId(id, l);
    }

    public void buildStringChunk(byte[] buffer, int start, int end,
                                 Location l) throws Exception {
        strStore.append(new String(buffer, start, end-start));
    }

    public void buildStringChar(byte b, Location l) throws Exception {
        strStore.append((char) b);
    }

    public int buildString(byte[] buffer, int start, int end,
                           Location l) throws Exception {
        strStore.append(new String(buffer, start, end-start));
        String s = strStore.toString();
        strStore = new StringBuffer();
        return builder.buildString(s, l);
    }

    public int buildInt(long v, int t, Location l) throws Exception {
        return builder.buildInt(v, l);
    }

    public int buildKeyword(int keyword, Location l) throws Exception {
        return builder.buildKeyword(keyword, l);
    }

    public int buildOperator(byte[] buffer, int start, int end,
                             int symbol, Location l) throws Exception {
        return builder.buildOperator(symbol, l);
    }

    public int buildLineEnd(int v) throws Exception {
        return v;
    }

    public int buildInputEnd(Location l) throws Exception {
        return builder.buildInputEnd(l);
    }

}
