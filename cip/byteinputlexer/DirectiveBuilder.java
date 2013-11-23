package cip.byteinputlexer;

/**
 * A <code>DirectiveBuilder</code> extends <code>Builder</code> to support
 * the lexing of preprocessor directives.  It defines an extra lexeme which
 * is used to mark the end of a preprocessor line.
 */
public interface DirectiveBuilder extends Builder {

    /**
     * <code>EOL</code> is returned when the end of a pre-processor line is
     * detected in the input.  This is usually the trigger to switch back
     * to the default builder.
     */
    static final int EOL = 0x80;
}
