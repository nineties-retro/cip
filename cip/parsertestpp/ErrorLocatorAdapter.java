/*
 *
 */

package cip.parsertestpp;

import cip.Location;
import cip.byteinputlexer.Lexer;
import cip.parser.ErrorLocator;


class ErrorLocatorAdapter implements ErrorLocator {
    private final Lexer lexer;

    ErrorLocatorAdapter(Lexer l) {
        this.lexer = l;
    }

    public Location getLocation() {
        return lexer.getLocation();
    }
}
