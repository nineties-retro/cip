package cip.parser;

import cip.Location;

/**
 * XXX: not particularly happy with this interface but it does for now.
 */
public interface ErrorHandler {
    public void start(String key);
    public void lexeme(int lexemeCode);
    public void end() throws Exception;
}
