package cip.parsertest;

import cip.Location;
import cip.parser.ErrorLocator;

/**
 *
 */
class ParserErrorHandler implements cip.parser.ErrorHandler {
    private final ErrorLocator locator;

    public ParserErrorHandler(ErrorLocator locator) {
        this.locator = locator;
    }

    public void start(String key) {
        Location l = locator.getLocation();
        System.out.print("error ");
        System.out.print(key);
        System.out.print(" ");
        System.out.print(l.line);
        System.out.print(" ");
        System.out.print(l.column);
    }

    public void lexeme(int l) {
        System.out.print(" lexeme ");
        System.out.print(l);
    }

    public void end() throws Exception {
        System.out.println("");
        throw new Exception();
    }
}
