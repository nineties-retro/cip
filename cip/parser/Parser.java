package cip.parser;

import cip.lexer.Lexeme;
import cip.lexer.Lexer;
import cip.ast.Builder;
import cip.ast.NodeTypes;

/**
 * 
 */
public class Parser {
    private Builder ast;
    private Lexer lexer;
    private ErrorHandler errorHandler;
    private int lexeme;

    public Parser(Lexer lexer, Builder ast, ErrorHandler errorHandler) {
        this.ast = ast;
        this.lexer = lexer;
        this.errorHandler = errorHandler;
    }

    private int parseScopedNameFromColonPair() throws Exception {
        ast.start(NodeTypes.ABSOLUTE_NAME);
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.ID_LITERAL) {
            ast.end();
            return lexer.lex();
        } else {
            errorHandler.start("expected_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    private int parseScopedNameFromIdentifier() throws Exception {
        int lexeme;
        ast.startLate(NodeTypes.RELATIVE_NAME);
        for (;;) {
            lexeme = lexer.lex();
            if (lexeme != Lexeme.COLON_PAIR) {
                ast.endLate();
                return lexeme;
            }
            lexeme = lexer.lex();
            if (lexeme == Lexeme.ID_LITERAL) {
                continue;
            }
            errorHandler.start("expected_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    /* idl-v2.2#primary_expr */
    /* XXX: fixed_pt_literal */
    private int parsePrimaryExpr() throws Exception {
        int lexeme = lexer.lex();
        switch (lexeme) {
        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#floating_pt_literal
        case Lexeme.FLOAT_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#integer_literal
        case Lexeme.INT_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#string_literal
        case Lexeme.STRING_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#wide_string_literal
        case Lexeme.WIDE_STRING_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#char_literal
        case Lexeme.CHAR_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#wide_char_literal
        case Lexeme.WIDE_CHAR_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#boolean_literal
        case Lexeme.KW_TRUE:
        case Lexeme.KW_FALSE:
            return lexer.lex();

        // idl-v2.2#primary_expr
        //   idl-v2.2#scoped_name
        case Lexeme.ID_LITERAL:
            return parseScopedNameFromIdentifier();

        // idl-v2.2#primary_expr
        //   idl-v2.2#scoped_name
        case Lexeme.COLON_PAIR:
            return parseScopedNameFromColonPair();

        case Lexeme.OPEN_PAREN:
            lexeme = parseConstExp(lexer.lex());
            if (lexeme == Lexeme.CLOSE_PAREN)
                return lexer.lex();
            errorHandler.start("expected_)");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;

        default:
            errorHandler.start("<primary_expr>");
            errorHandler.lexeme(lexeme);
            errorHandler.lexeme(Lexeme.INT_LITERAL);
            errorHandler.lexeme(Lexeme.STRING_LITERAL);
            errorHandler.lexeme(Lexeme.WIDE_STRING_LITERAL);
            errorHandler.lexeme(Lexeme.CHAR_LITERAL);
            errorHandler.lexeme(Lexeme.WIDE_CHAR_LITERAL);
            errorHandler.lexeme(Lexeme.KW_TRUE);
            errorHandler.lexeme(Lexeme.KW_FALSE);
            errorHandler.lexeme(Lexeme.ID_LITERAL);
            errorHandler.lexeme(Lexeme.COLON_PAIR);
            errorHandler.lexeme(Lexeme.OPEN_PAREN);
            errorHandler.end();
            return 0;
        }
    }


    /* idl-v2.2#unary_expr */
    /* XXX: fixed_pt_literal and floating_pt_literal */
    private int parseUnaryExpr(int lexeme) throws Exception {
        switch (lexeme) {
        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#integer_literal
        case Lexeme.INT_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#string_literal
        case Lexeme.STRING_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#wide_string_literal
        case Lexeme.WIDE_STRING_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#char_literal
        case Lexeme.CHAR_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#wide_char_literal
        case Lexeme.WIDE_CHAR_LITERAL:

        // idl-v2.2#primary_expr
        //   idl-v2.2#literal
        //     idl-v2.2#boolean_literal
        case Lexeme.KW_TRUE:
        case Lexeme.KW_FALSE:
            return lexer.lex();

        // idl-v2.2#primary_expr
        //   idl-v2.2#scoped_name
        case Lexeme.ID_LITERAL:
            return parseScopedNameFromIdentifier();

        // idl-v2.2#primary_expr
        //   idl-v2.2#scoped_name
        case Lexeme.COLON_PAIR:
            return parseScopedNameFromColonPair();

        // idl-v2.2#unary_operator
        case Lexeme.CROSS:
            return parsePrimaryExpr();

        // idl-v2.2#unary_operator
        case Lexeme.HYPHEN:
            return parsePrimaryExpr();

        // idl-v2.2#unary_operator
        case Lexeme.TILDE:
            return parsePrimaryExpr();

        case Lexeme.OPEN_PAREN:
            lexeme = parseConstExp(lexer.lex());
            if (lexeme == Lexeme.CLOSE_PAREN)
                return lexer.lex();
            errorHandler.start("expected_)");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        default:
            errorHandler.start("<unary_expr>");
            errorHandler.lexeme(lexeme);
            errorHandler.lexeme(Lexeme.INT_LITERAL);
            errorHandler.lexeme(Lexeme.STRING_LITERAL);
            errorHandler.lexeme(Lexeme.WIDE_STRING_LITERAL);
            errorHandler.lexeme(Lexeme.CHAR_LITERAL);
            errorHandler.lexeme(Lexeme.WIDE_CHAR_LITERAL);
            errorHandler.lexeme(Lexeme.KW_TRUE);
            errorHandler.lexeme(Lexeme.KW_FALSE);
            errorHandler.lexeme(Lexeme.ID_LITERAL);
            errorHandler.lexeme(Lexeme.COLON_PAIR);
            errorHandler.lexeme(Lexeme.CROSS);
            errorHandler.lexeme(Lexeme.HYPHEN);
            errorHandler.lexeme(Lexeme.TILDE);
            errorHandler.lexeme(Lexeme.OPEN_PAREN);
            errorHandler.end();
            return 0;
        }
    }

    private int parseMultExpr(int lexeme) throws Exception {
        for (;;) {
            lexeme = parseUnaryExpr(lexeme);
            if ((lexeme == Lexeme.STAR)
            ||  (lexeme == Lexeme.SLASH)
            ||  (lexeme == Lexeme.PERCENT)) {
                lexeme = lexer.lex();
            } else {
              return lexeme;
            }
        }
    }

    private int parseAddExpr(int lexeme) throws Exception {
        for (;;) {
            lexeme = parseMultExpr(lexeme);
            if ((lexeme == Lexeme.CROSS) ||  (lexeme == Lexeme.HYPHEN)) {
                lexeme = lexer.lex();
            } else {
                return lexeme;
            }
        }
    }

    private int parseShiftExpr(int lexeme) throws Exception {
        for (;;) {
            lexeme = parseAddExpr(lexeme);
            if ((lexeme == Lexeme.OPEN_ANGLE_PAIR)
            ||  (lexeme == Lexeme.CLOSE_ANGLE_PAIR)) {
                lexeme = lexer.lex();
            } else {
                return lexeme;
            }
        }
    }

    private int parseAndExpr(int lexeme) throws Exception {
        for (;;) {
            lexeme = parseShiftExpr(lexeme);
            if (lexeme == Lexeme.AMPERSAND) {
                lexeme = lexer.lex();
            } else {
              return lexeme;
            }
        }
    }

    private int parseXorExpr(int lexeme) throws Exception {
        for (;;) {
            lexeme = parseAndExpr(lexeme);
            if (lexeme == Lexeme.CIRCUMFLEX) {
                lexeme = lexer.lex();
            } else {
              return lexeme;
            }
        }
    }

    private int parseOrExpr(int lexeme) throws Exception {
        for (;;) {
            lexeme = parseXorExpr(lexeme);
            if (lexeme == Lexeme.BAR) {
                lexeme = lexer.lex();
            } else {
              return lexeme;
            }
        }
    }

    private int parseConstExp(int lexeme) throws Exception {
        return parseOrExpr(lexeme);
    }

    private int parsePositiveIntConst(int lexeme) throws Exception {
        return parseConstExp(lexeme);
    }


    // idl-v2.2#base_type_spec
    //   idl-v2.2#integer_type
    //     idl-v2.2#unsigned_int
    private int parseUnsigned() throws Exception {
        int lexeme = lexer.lex();
        switch (lexeme) {
        case Lexeme.KW_LONG:
            lexeme = lexer.lex();
            if (lexeme == Lexeme.KW_LONG) {
                // idl-v2.2#unsigned_longlong_int
                return lexer.lex();
            } else {
                // idl-v2.2#unsigned_long_int
                return lexeme;
            }
        case Lexeme.KW_SHORT:
            // idl-v2.2#unsigned_short_int
            return lexer.lex();
        default:
            errorHandler.start("<unsigned_int>");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }


    /* see idl-v2.2#string_type
     * and idl-v2.2#wide_string_type
     */
    private int parseStringBody() throws Exception {
        ast.start(NodeTypes.STRING);
        int lexeme = lexer.lex();
        if (lexeme != Lexeme.OPEN_ANGLE) {
            ast.endLate();
            return lexeme;
        }
        lexeme = parsePositiveIntConst(lexer.lex());
        if (lexeme == Lexeme.CLOSE_ANGLE) {
            ast.end();
            return lexer.lex();
        }
        errorHandler.start("expected_>");
        errorHandler.lexeme(lexeme);
        errorHandler.end();
        return 0;
    }


    /* see idl-v2.2#fixed_pt_type */
    private int parseFixedPointBody() throws Exception {
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.OPEN_ANGLE) {
            lexeme = parsePositiveIntConst(lexer.lex());
            if (lexeme == Lexeme.COMMA) {
                if ((lexeme = lexer.lex()) == Lexeme.INT_LITERAL) {
                    if ((lexeme = lexer.lex()) == Lexeme.CLOSE_ANGLE) {
                        return lexer.lex();
                    } else {
                        errorHandler.start("expected_>");
                        errorHandler.lexeme(lexeme);
                        errorHandler.end();
                        return 0;
                    }
                } else {
                    errorHandler.start("expected_int");
                    errorHandler.lexeme(lexeme);
                    errorHandler.end();
                    return 0;
                }
            } else {
                errorHandler.start("expected_,");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        } else {
            errorHandler.start("expected_<");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    /* see idl-v2.2#const_type.
     * XXX: missing (floating|fixed) point.
     */
    private int parseConstType(int lexeme) 
        throws Exception {
        switch (lexeme) {
        case Lexeme.KW_LONG:
            lexeme = lexer.lex();
            switch (lexeme) {
            case Lexeme.KW_LONG:
            case Lexeme.KW_DOUBLE:
                return lexer.lex();
            default:
                return lexeme;
            }
        case Lexeme.KW_SHORT:
        case Lexeme.KW_CHAR:
        case Lexeme.KW_BOOLEAN:
        case Lexeme.KW_FIXED:
        case Lexeme.KW_FLOAT:
        case Lexeme.KW_DOUBLE:
        case Lexeme.KW_WCHAR:
            return lexer.lex();
        case Lexeme.KW_UNSIGNED:
            return parseUnsigned();
        case Lexeme.KW_STRING:
        case Lexeme.KW_WSTRING:
            return parseStringBody();
        case Lexeme.ID_LITERAL:
            return parseScopedNameFromIdentifier();
        case Lexeme.COLON_PAIR:
            return parseScopedNameFromColonPair();
        default:
            errorHandler.start("<const_type>");
            errorHandler.lexeme(lexeme);
            errorHandler.lexeme(Lexeme.KW_LONG);
            errorHandler.lexeme(Lexeme.KW_SHORT);
            errorHandler.lexeme(Lexeme.KW_CHAR);
            errorHandler.lexeme(Lexeme.KW_WCHAR);
            errorHandler.lexeme(Lexeme.KW_BOOLEAN);
            errorHandler.lexeme(Lexeme.KW_FLOAT);
            errorHandler.lexeme(Lexeme.KW_DOUBLE);
            errorHandler.lexeme(Lexeme.KW_UNSIGNED);
            errorHandler.lexeme(Lexeme.KW_FIXED);
            errorHandler.lexeme(Lexeme.KW_STRING);
            errorHandler.lexeme(Lexeme.KW_WSTRING);
            errorHandler.lexeme(Lexeme.ID_LITERAL);
            errorHandler.lexeme(Lexeme.COLON_PAIR);
            errorHandler.end();
            return 0;
        }
    }

    private int parseConstDecl() throws Exception {
        int lexeme = lexer.lex();
        lexeme = parseConstType(lexeme);
        if (lexeme == Lexeme.ID_LITERAL) {
            lexeme = lexer.lex();
            if (lexeme == Lexeme.EQUAL) {
                return parseConstExp(lexer.lex());
            } else {
                errorHandler.start("expected_=");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        } else {
            errorHandler.start("expected_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    private int parseAttribute() throws Exception {
        int lexeme = parseParamTypeSpec();
        if (lexeme == Lexeme.ID_LITERAL) {
            for (;;) {
                lexeme = lexer.lex();
                if (lexeme == Lexeme.COMMA) {
                    lexeme = lexer.lex();
                    if (lexeme == Lexeme.ID_LITERAL) {
                        continue;
                    } else {
                        errorHandler.start("expected_id");
                        errorHandler.lexeme(lexeme);
                        errorHandler.end();
                        return 0;
                    }
                } else {
                    /* there will be an error if this is not a ; */
                    return lexeme;
                }
            }
        } else {
            errorHandler.start("expected_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    private int parseReadonlyAttribute() throws Exception {
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.KW_ATTRIBUTE) {
            return parseAttribute();
        } else {
            errorHandler.start("expected_attribute");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    /* See idl-v2.2#param_type_spec. */
    private int parseParamTypeSpec() throws Exception {
        int lexeme = lexer.lex();
        switch (lexeme) {
        // idl-v2.2#base_type_spec
        //   idl-v2.2#floating_pt_type
        // idl-v2.2#base_type_spec
        //   idl-v2.2#integer_type
        //     idl-v2.2#signed_int
        //       idl-v2.2#signed_long_int
        //       idl-v2.2#signed_longlong_int
        case Lexeme.KW_LONG:
            lexeme = lexer.lex();
            switch (lexeme) {
            case Lexeme.KW_LONG:      // "long" "long"
            case Lexeme.KW_DOUBLE:    // "long" "double"
                return lexer.lex();
            default:                  // "long"
                ast.addBuiltInType(lexeme);
                return lexeme;
            }
        // idl-v2.2#base_type_spec
        //   idl-v2.2#integer_type
        //     idl-v2.2#signed_int
        //       idl-v2.2#signed_short_int
        case Lexeme.KW_SHORT:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#char_type
        case Lexeme.KW_CHAR:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#wide_char_type
        case Lexeme.KW_WCHAR:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#boolean_type
        case Lexeme.KW_BOOLEAN:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#floating_pt_type
        case Lexeme.KW_FLOAT:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#floating_pt_type
        case Lexeme.KW_DOUBLE:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#object_type
        case Lexeme.KW_OBJECT:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#octet_type
        case Lexeme.KW_OCTET:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#any_type
        case Lexeme.KW_ANY:
            return lexer.lex();

        // idl-v2.2#base_type_spec
        //   idl-v2.2#integer_type
        //     idl-v2.2#unsigned_int
        case Lexeme.KW_UNSIGNED:
            return parseUnsigned();

        // idl-v2.2#string_type
        case Lexeme.KW_STRING:
            return parseStringBody();

        // idl-v2.2#wide_string_type
        case Lexeme.KW_WSTRING:
            return parseStringBody();

        // idl-v2.2#fixed_pt_type
        case Lexeme.KW_FIXED:
            return parseFixedPointBody();

        // idl-v2.2#scoped_name
        case Lexeme.ID_LITERAL:
            return parseScopedNameFromIdentifier();

        // idl-v2.2#scoped_name
        case Lexeme.COLON_PAIR:
            return parseScopedNameFromColonPair();

        default:
            errorHandler.start("expected_<param_type_spec>");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    private int parseParamDecls() throws Exception {
        for (;;) {
            int lexeme = lexer.lex();
            switch (lexeme) {
            case Lexeme.KW_IN:
            case Lexeme.KW_OUT:
            case Lexeme.KW_INOUT:
                lexeme = parseParamTypeSpec();
                if (lexeme == Lexeme.ID_LITERAL) {
                    lexeme = lexer.lex();
                    if (lexeme == Lexeme.COMMA) {
                        continue;
                    } else if (lexeme == Lexeme.CLOSE_PAREN) {
                        return lexer.lex();
                    } else {
                        errorHandler.start("<param_decl>");
                        errorHandler.lexeme(lexeme);
                        errorHandler.lexeme(Lexeme.COMMA);
                        errorHandler.lexeme(Lexeme.CLOSE_PAREN);
                        errorHandler.end();
                        return 0;
                    }
                } else {
                    errorHandler.start("expected_id");
                    errorHandler.lexeme(lexeme);
                    errorHandler.end();
                    return 0;
                }
            default:
                errorHandler.start("<parameter_decls>");
                errorHandler.lexeme(lexeme);
                errorHandler.lexeme(Lexeme.KW_IN);
                errorHandler.lexeme(Lexeme.KW_OUT);
                errorHandler.lexeme(Lexeme.KW_INOUT);
                errorHandler.end();
                return 0;
            }
        }
    }


    /* see idl-v2.2#parameter_dcls */
    private int parseOptionalParamDecls() throws Exception {
        int lexeme = lexer.lex();
        switch (lexeme) {
        case Lexeme.KW_IN:
        case Lexeme.KW_OUT:
        case Lexeme.KW_INOUT:
            lexeme = parseParamTypeSpec();
            if (lexeme == Lexeme.ID_LITERAL) {
                lexeme = lexer.lex();
                if (lexeme == Lexeme.COMMA) {
                    return parseParamDecls();
                } else if (lexeme == Lexeme.CLOSE_PAREN) {
                    return lexer.lex();
                } else {
                    errorHandler.start("<param_decl>");
                    errorHandler.lexeme(lexeme);
                    errorHandler.lexeme(Lexeme.COMMA);
                    errorHandler.lexeme(Lexeme.CLOSE_PAREN);
                    errorHandler.end();
                    return 0;
                }
            } else {
                errorHandler.start("expected_id");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        case Lexeme.CLOSE_PAREN:
            return lexer.lex();
        default:
            errorHandler.start("<parameter_decls>");
            errorHandler.lexeme(lexeme);
            errorHandler.lexeme(Lexeme.KW_IN);
            errorHandler.lexeme(Lexeme.KW_OUT);
            errorHandler.lexeme(Lexeme.KW_INOUT);
            errorHandler.lexeme(Lexeme.CLOSE_PAREN);
            errorHandler.end();
            return 0;
        }
    }

    private int parseRaises() throws Exception {
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.OPEN_PAREN) {
            for (;;) {
                lexeme = lexer.lex();
                switch (lexeme) {
                case Lexeme.ID_LITERAL:
                    lexeme = parseScopedNameFromIdentifier();
                    break;
                case Lexeme.COLON_PAIR:
                  lexeme = parseScopedNameFromColonPair();
                  break;
                default:
                    errorHandler.start("<scoped_name>");
                    errorHandler.lexeme(lexeme);
                    errorHandler.lexeme(Lexeme.ID_LITERAL);
                    errorHandler.lexeme(Lexeme.COLON_PAIR);
                    errorHandler.end();
                    return 0;
                }
                switch (lexeme) {
                case Lexeme.COMMA:
                    continue;
                case Lexeme.CLOSE_PAREN:
                    ast.end();
                    return lexer.lex();
                default:
                    errorHandler.start("yyy");
                    errorHandler.lexeme(lexeme);
                    errorHandler.lexeme(Lexeme.COMMA);
                    errorHandler.lexeme(Lexeme.CLOSE_PAREN);
                    errorHandler.end();
                    return 0;
                }
            }
        } else {
            errorHandler.start("expected_(");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    private int parseContext() throws Exception {
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.OPEN_PAREN) {
            for (;;) {
                lexeme = lexer.lex();
                if (lexeme == Lexeme.STRING_LITERAL) {
                    lexeme = lexer.lex();
                    switch (lexeme) {
                    case Lexeme.COMMA:
                        continue;
                    case Lexeme.CLOSE_PAREN:
                        ast.end(); // context
                        ast.end(); // operation
                        return lexer.lex();
                    default:
                        errorHandler.start("xxx");
                        errorHandler.lexeme(lexeme);
                        errorHandler.lexeme(Lexeme.COMMA);
                        errorHandler.lexeme(Lexeme.CLOSE_PAREN);
                        errorHandler.end();
                        return 0;
                    }
                } else {
                    errorHandler.start("expected_string");
                    errorHandler.lexeme(lexeme);
                    errorHandler.end();
                    return 0;
                }
            }
        } else {
            errorHandler.start("expected_(");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    private int parseOp(int lexeme) throws Exception {
        if (lexeme == Lexeme.ID_LITERAL) {
            lexeme = lexer.lex();
            if (lexeme == Lexeme.OPEN_PAREN) {
                lexeme = parseOptionalParamDecls();
                if (lexeme == Lexeme.KW_RAISES) {
                    ast.start(NodeTypes.RAISES);
                    lexeme = parseRaises();
                }
                if (lexeme == Lexeme.KW_CONTEXT) {
                    ast.start(NodeTypes.CONTEXT);
                    lexeme = parseContext();
                } else {
                    ast.end();
                }
                return lexeme;
            } else {
                errorHandler.start("expected_(");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        } else {
            errorHandler.start("expected_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    private int parseOnewayOp() throws Exception {
        int lexeme = lexer.lex();
        switch (lexeme) {
        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#floating_pt_type
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#integer_type
        //             idl-v2.2#signed_int
        //               idl-v2.2#signed_long_int
        //               idl-v2.2#signed_longlong_int
        case Lexeme.KW_LONG:
            lexeme = lexer.lex();
            switch (lexeme) {
            case Lexeme.KW_LONG:      // "long" "long"
            case Lexeme.KW_DOUBLE:    // "long" "double"
                return parseOp(lexer.lex());
            default:                  // "long"
                return parseOp(lexeme);
            }
        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        case Lexeme.KW_VOID:

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#integer_type
        //             idl-v2.2#signed_int
        //               idl-v2.2#signed_short_int
        case Lexeme.KW_SHORT:

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#char_type
        case Lexeme.KW_CHAR:

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#wide_char_type
        case Lexeme.KW_WCHAR:

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#boolean_type
        case Lexeme.KW_BOOLEAN:

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#floating_pt_type
        case Lexeme.KW_FLOAT:

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#floating_pt_type
        case Lexeme.KW_DOUBLE:

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#object_type
        case Lexeme.KW_OBJECT:

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#octet_type
        case Lexeme.KW_OCTET:

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#any_type
        case Lexeme.KW_ANY:
            return parseOp(lexer.lex());

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#base_type_spec
        //           idl-v2.2#integer_type
        //             idl-v2.2#unsigned_int
        case Lexeme.KW_UNSIGNED:
            return parseOp(parseUnsigned());

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#string_type
        case Lexeme.KW_STRING:
            return parseOp(parseStringBody());

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#wide_string_type
        case Lexeme.KW_WSTRING:
            return parseOp(parseStringBody());

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#fixed_pt_type
        case Lexeme.KW_FIXED:
            return parseOp(parseFixedPointBody());

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#scoped_name
        case Lexeme.ID_LITERAL:
            return parseOp(parseScopedNameFromIdentifier());

        // idl-v2.2#export
        //   idl-v2.2#op_dcl
        //     idl-v2.2#op_type_spec
        //       idl-v2.2#param_type_spec
        //         idl-v2.2#scoped_name
        case Lexeme.COLON_PAIR:
            return parseOp(parseScopedNameFromColonPair());

        default:
            errorHandler.start("expected_void_or_<param_type_spec>");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    /* see cip/idl-v2.2#export */
    private int parseInterfaceBody() throws Exception {
        for (;;) {
            int lexeme = lexer.lex();
            switch (lexeme) {
            // idl-v2.2#export
            //   idl-v2.2#type_dcl
            case Lexeme.KW_TYPEDEF:
                ast.start(NodeTypes.TYPE_DEF);
                lexeme = parseTypeDeclarator(lexer.lex());
                ast.end();
                break;

            // idl-v2.2#export
            //   idl-v2.2#type_dcl
            //     idl-v2.2#struct_type
            case Lexeme.KW_STRUCT:
                lexeme = parseExceptOrStructBody(false, NodeTypes.STRUCT);
                break;

            // idl-v2.2#export
            //   idl-v2.2#type_dcl
            //     idl-v2.2#enum_type
            case Lexeme.KW_ENUM:
                lexeme = parseEnumBody();
                break;

            // idl-v2.2#export
            //   idl-v2.2#type_dcl
            //     idl-v2.2#union_type
            case Lexeme.KW_UNION:
                lexeme = parseUnionBody();
                break;

            // idl-v2.2#export
            //   idl-v2.2#const_dcl
            case Lexeme.KW_CONST:
                lexeme = parseConstDecl();
                break;

            // idl-v2.2#export
            //   idl-v2.2#except_dcl
            case Lexeme.KW_EXCEPTION:
                lexeme = parseExceptOrStructBody(true, NodeTypes.EXCEPTION);
                break;

            // idl-v2.2#export
            //   idl-v2.2#attr_dcl
            case Lexeme.KW_READONLY:
                lexeme = parseReadonlyAttribute();
                break;

            // idl-v2.2#export
            //   idl-v2.2#attr_dcl
            case Lexeme.KW_ATTRIBUTE:
                lexeme = parseAttribute();
                break;

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_attribute
            case Lexeme.KW_ONEWAY:
                lexeme = parseOnewayOp();
                break;

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#floating_pt_type
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#integer_type
            //             idl-v2.2#signed_int
            //               idl-v2.2#signed_long_int
            //               idl-v2.2#signed_longlong_int
            case Lexeme.KW_LONG:
                ast.start(NodeTypes.OPERATION);
                ast.start(NodeTypes.TYPE_REF);
                lexeme = lexer.lex();
                switch (lexeme) {
                case Lexeme.KW_LONG:      // "long" "long"
                case Lexeme.KW_DOUBLE:    // "long" "double"
                    ast.end();
                    lexeme = parseOp(lexer.lex());
                    break;
                default:                  // "long"
                    ast.endLate();
                    lexeme = parseOp(lexeme);
                    break;
                }
                break;

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            case Lexeme.KW_VOID:

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#integer_type
            //             idl-v2.2#signed_int
            //               idl-v2.2#signed_short_int
            case Lexeme.KW_SHORT:

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#char_type
            case Lexeme.KW_CHAR:

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#wide_char_type
            case Lexeme.KW_WCHAR:

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#boolean_type
            case Lexeme.KW_BOOLEAN:

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#floating_pt_type
            case Lexeme.KW_FLOAT:

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#floating_pt_type
            case Lexeme.KW_DOUBLE:

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#object_type
            case Lexeme.KW_OBJECT:

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#octet_type
            case Lexeme.KW_OCTET:

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#any_type
            case Lexeme.KW_ANY:
                lexeme = parseOp(lexer.lex());
                break;

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#base_type_spec
            //           idl-v2.2#integer_type
            //             idl-v2.2#unsigned_int
            case Lexeme.KW_UNSIGNED:
                ast.start(NodeTypes.OPERATION);
                lexeme = parseOp(parseUnsigned());
                break;

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#string_type
            case Lexeme.KW_STRING:
                lexeme = parseOp(parseStringBody());
                break;

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#wide_string_type
            case Lexeme.KW_WSTRING:
                ast.start(NodeTypes.OPERATION);
                lexeme = parseOp(parseStringBody());
                break;

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#fixed_pt_type
            case Lexeme.KW_FIXED:
                ast.start(NodeTypes.OPERATION);
                lexeme = parseOp(parseFixedPointBody());
                break;

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#scoped_name
            case Lexeme.ID_LITERAL:
                ast.start(NodeTypes.OPERATION);
                lexeme = parseOp(parseScopedNameFromIdentifier());
                break;

            // idl-v2.2#export
            //   idl-v2.2#op_dcl
            //     idl-v2.2#op_type_spec
            //       idl-v2.2#param_type_spec
            //         idl-v2.2#scoped_name
            case Lexeme.COLON_PAIR:
                ast.start(NodeTypes.OPERATION);
                lexeme = parseOp(parseScopedNameFromColonPair());
                break;

            case Lexeme.CLOSE_BRACE:
                ast.end();
                return lexer.lex();

            default:
                errorHandler.start("expected_<export>");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }

            if (lexeme == Lexeme.SEMI_COLON) {
                continue;
            } else {
                errorHandler.start("expected_;");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        }
    }

    /* see idl-v2.2#inheritance_spec */
    private int parseInheritanceSpecBody() 
            throws Exception {
        for (;;) {
            int lexeme = lexer.lex();
            switch (lexeme) {
            // idl-v2.2#scoped_name
            case Lexeme.ID_LITERAL:
                lexeme = parseScopedNameFromIdentifier();
                break;
            // idl-v2.2#scoped_name
            case Lexeme.COLON_PAIR:
                lexeme = parseScopedNameFromColonPair();
                break;
            default:
                errorHandler.start("expected_scoped_name");
                errorHandler.lexeme(lexeme);
                errorHandler.lexeme(Lexeme.ID_LITERAL);
                errorHandler.lexeme(Lexeme.COLON_PAIR);
                errorHandler.end();
                return 0;
            }
            if (lexeme == Lexeme.COMMA) {
                continue;
            } else {
              return lexeme;
            }
        }
    }

    private int parseInterface() throws Exception {
        ast.start(NodeTypes.INTERFACE);
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.ID_LITERAL) {
            lexeme = lexer.lex();
            if (lexeme == Lexeme.COLON) {
                lexeme = parseInheritanceSpecBody();
            }
            if (lexeme == Lexeme.OPEN_BRACE) {
                return parseInterfaceBody();
            } else {
                ast.end();
                /* if it is not a Lexeme.SEMI_COLON there will be an error */
                return lexeme;
            }
        } else {
            errorHandler.start("expected_interface_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    /* see idl-v2.2#module */
    private int parseModule() throws Exception {
        ast.start(NodeTypes.MODULE);
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.ID_LITERAL) {
            lexeme = lexer.lex();
            if (lexeme == Lexeme.OPEN_BRACE) {
                lexeme = parseDefinitions(lexer.lex());
                if (lexeme == Lexeme.CLOSE_BRACE) {
                    ast.end();
                    return lexer.lex();
                } else {
                    errorHandler.start("expected_}");
                    errorHandler.lexeme(lexeme);
                    errorHandler.end();
                    return 0;
                }
            } else {
                errorHandler.start("expected_{");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        } else {
            errorHandler.start("expected_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    /* see idl-v2.2#enum_type */
    private int parseEnumBody() throws Exception {
        ast.start(NodeTypes.ENUM);
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.ID_LITERAL) {
            lexeme = lexer.lex();
            if (lexeme == Lexeme.OPEN_BRACE) {
                lexeme = lexer.lex();
                if (lexeme == Lexeme.ID_LITERAL) {
                    for (;;) {
                        lexeme = lexer.lex();
                        if (lexeme == Lexeme.COMMA) {
                            lexeme = lexer.lex();
                            if (lexeme == Lexeme.ID_LITERAL) {
                                continue;
                            } else {
                                errorHandler.start("expected_id");
                                errorHandler.lexeme(lexeme);
                                errorHandler.end();
                                return 0;
                            }
                        } else if (lexeme == Lexeme.CLOSE_BRACE) {
                            ast.end();
                            return lexer.lex();
                        } else {
                            errorHandler.start("expected_,_or_id");
                            errorHandler.lexeme(lexeme);
                            errorHandler.end();
                            return 0;
                        }
                    }
                } else {
                    errorHandler.start("expected_id");
                    errorHandler.lexeme(lexeme);
                    errorHandler.end();
                    return 0;
                }
            } else {
                errorHandler.start("expected_{");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        } else {
            errorHandler.start("expected_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    /* see idl-v2.2#switch_type_spec */
    private int parseSwitchTypeSpec() throws Exception {
        int lexeme = lexer.lex();
        switch (lexeme) {
        // idl-v2.2#integer_type
        //   idl-v2.2#signed_int
        //     idl-v2.2#signed_long_int
        //     idl-v2.2#signed_longlong_int
        case Lexeme.KW_LONG:
            lexeme = lexer.lex();
            switch (lexeme) {
            case Lexeme.KW_LONG:
                return lexer.lex();
            default:
                return lexeme;
            }
        // idl-v2.2#integer_type
        //   idl-v2.2#signed_int
        //     idl-v2.2#signed_short_int
        case Lexeme.KW_SHORT:
            return lexer.lex();

        // idl-v2.2#char_type 
        case Lexeme.KW_CHAR:
            return lexer.lex();

        // idl-v2.2#boolean_type
        case Lexeme.KW_BOOLEAN:
            return lexer.lex();

        // idl-v2.2#enum_type
        case Lexeme.KW_ENUM:
            return parseEnumBody();

        // idl-v2.2#enum_type
        case Lexeme.ID_LITERAL:
            return parseScopedNameFromIdentifier();

        // idl-v2.2#scoped_name
        case Lexeme.COLON_PAIR:
            return parseScopedNameFromColonPair();

        // idl-v2.2#integer_type
        //   idl-v2.2#unsigned_int
        case Lexeme.KW_UNSIGNED:
            return parseUnsigned();

        default:
            errorHandler.start("<simple_type_spec>");
            errorHandler.lexeme(lexeme);
            errorHandler.lexeme(Lexeme.KW_LONG);
            errorHandler.lexeme(Lexeme.KW_SHORT);
            errorHandler.lexeme(Lexeme.KW_CHAR);
            errorHandler.lexeme(Lexeme.KW_BOOLEAN);
            errorHandler.lexeme(Lexeme.KW_ENUM);
            errorHandler.lexeme(Lexeme.KW_UNSIGNED);
            errorHandler.lexeme(Lexeme.ID_LITERAL);
            errorHandler.lexeme(Lexeme.COLON_PAIR);
            errorHandler.end();
            return 0;
        }
    }

    /* see idl-v2.2#switch_body
     * As a compromise between the number of lexeme (re)checks and the size
     * of the code I've folded the detection of the terminating close brace
     * into the detection of the FIRST of the <code>case_label</code>.
     * Extra flags are used to detect if there has been at least one
     * <code>case</code> and also to ensure that there is only one
     * <code>default</code>.
     */
    private int parseSwitchBody() throws Exception {
        int lexeme;
        boolean foundCaseLabel = false;
        boolean foundDefault = false;
        for (;;) {
          caseLabel:
            for (;;) {
                lexeme = lexer.lex();
                switch (lexeme) {
                case Lexeme.KW_CASE:
                    lexeme = parseConstExp(lexer.lex());
                    foundCaseLabel = true;
                    break caseLabel;
                case Lexeme.KW_DEFAULT:
                    if (!foundDefault) {
                        lexeme = lexer.lex();
                        foundCaseLabel = true;
                        foundDefault = true;
                        break caseLabel;
                    } else {
                        errorHandler.start("default");
                        errorHandler.lexeme(lexeme);
                        errorHandler.end();
                        return 0;
                    }
                case Lexeme.CLOSE_BRACE:
                    if (foundCaseLabel) {
                        return lexer.lex();
                    } else {
                        errorHandler.start("<case_label>");
                        errorHandler.lexeme(lexeme);
                        errorHandler.lexeme(Lexeme.KW_CASE);
                        errorHandler.lexeme(Lexeme.KW_DEFAULT);
                        errorHandler.end();
                        return 0;
                    }
                default:
                    errorHandler.start("<case_label>");
                    errorHandler.lexeme(lexeme);
                    errorHandler.lexeme(Lexeme.KW_CASE);
                    errorHandler.lexeme(Lexeme.KW_DEFAULT);
                    errorHandler.end();
                    return 0;
                }
            }
            if (lexeme == Lexeme.COLON) {
                lexeme = parseDeclarator(parseTypeSpec(lexer.lex()));
                if (lexeme == Lexeme.SEMI_COLON) {
                    continue;
                } else {
                    errorHandler.start("expected_;");
                    errorHandler.lexeme(lexeme);
                    errorHandler.end();
                    return 0;
                }
            } else {
                errorHandler.start("expected_:");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        }
    }

  /* see idl-v2.2#union_type */
  private int parseUnionBody() throws Exception {
      int lexeme = lexer.lex();
      if (lexeme == Lexeme.ID_LITERAL) {
          lexeme = lexer.lex();
          if (lexeme == Lexeme.KW_SWITCH) {
              lexeme = lexer.lex();
              if (lexeme == Lexeme.OPEN_PAREN) {
                  lexeme = parseSwitchTypeSpec();
                  if (lexeme == Lexeme.CLOSE_PAREN) {
                      lexeme = lexer.lex();
                      if (lexeme == Lexeme.OPEN_BRACE) {
                          return parseSwitchBody();
                      } else {
                          errorHandler.start("expected_{");
                          errorHandler.lexeme(lexeme);
                          errorHandler.end();
                          return 0;
                      }
                  } else {
                      errorHandler.start("expected_)");
                      errorHandler.lexeme(lexeme);
                      errorHandler.end();
                      return 0;
                  }
              } else {
                  errorHandler.start("expected_(");
                  errorHandler.lexeme(lexeme);
                  errorHandler.end();
                  return 0;
              }
          } else {
              errorHandler.start("expected_switch");
              errorHandler.lexeme(lexeme);
              errorHandler.end();
              return 0;
          }
      } else {
          errorHandler.start("expected_id");
          errorHandler.lexeme(lexeme);
          errorHandler.end();
          return 0;
      }
  }

    /* see idl-v2.2#sequence_type */
    private int parseSequenceBody() throws Exception {
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.OPEN_ANGLE) {
            lexeme = parseSimpleTypeSpec(lexer.lex());
            if (lexeme == Lexeme.COMMA) {
                lexeme = parsePositiveIntConst(lexer.lex());
            }
            if (lexeme == Lexeme.CLOSE_ANGLE) {
                return lexer.lex();
            } else {
                errorHandler.start("expected_>");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        } else {
            errorHandler.start("expected_<");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }


    /* see idl-v.2.2#simple_type_spec */
    private int parseSimpleTypeSpec(int lexeme) throws Exception {
        switch (lexeme) {
        // idl-v2.2#base_type_spec
        //   idl-v2.2#floating_pt_type
        // idl-v2.2#base_type_spec
        //   idl-v2.2#integer_type
        //     idl-v2.2#signed_int
        //       idl-v2.2#signed_long_int
        //       idl-v2.2#signed_longlong_int
        case Lexeme.KW_LONG:
            lexeme = lexer.lex();
            switch (lexeme) {
            case Lexeme.KW_LONG:      // "long" "long"
            case Lexeme.KW_DOUBLE:    // "long" "double"
                return lexer.lex();
            default:                  // "long"
                return lexeme;
            }

        // idl-v2.2#base_type_spec
        //   idl-v2.2#integer_type
        //     idl-v2.2#signed_int
        //       idl-v2.2#signed_short_int
        case Lexeme.KW_SHORT:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#char_type
        case Lexeme.KW_CHAR:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#wide_char_type
        case Lexeme.KW_WCHAR:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#boolean_type
        case Lexeme.KW_BOOLEAN:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#floating_pt_type
        case Lexeme.KW_FLOAT:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#floating_pt_type
        case Lexeme.KW_DOUBLE:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#object_type
        case Lexeme.KW_OBJECT:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#octet_type
        case Lexeme.KW_OCTET:

        // idl-v2.2#base_type_spec
        //   idl-v2.2#any_type
        case Lexeme.KW_ANY:
            return lexer.lex();

        // idl-v2.2#base_type_spec
        //   idl-v2.2#integer_type
        //     idl-v2.2#unsigned_int
        case Lexeme.KW_UNSIGNED:
            return parseUnsigned();

        // idl-v2.2#template_type_spec
        //   idl-v2.2#string_type
        case Lexeme.KW_STRING:
            return parseStringBody();

        // idl-v2.2#template_type_spec
        //   idl-v2.2#wide_string_type
        case Lexeme.KW_WSTRING:
            return parseStringBody();

        // idl-v2.2#template_type_spec
        //   idl-v2.2#sequence_type
        case Lexeme.KW_SEQUENCE:
            return parseSequenceBody();

        // idl-v2.2#template_type_spec
        //   idl-v2.2#fixed_pt_type
        case Lexeme.KW_FIXED:
            return parseFixedPointBody();

        // idl-v2.2#scoped_name
        case Lexeme.ID_LITERAL:
            return parseScopedNameFromIdentifier();

        // idl-v2.2#scoped_name
        case Lexeme.COLON_PAIR:
            return parseScopedNameFromColonPair();

        default:
            errorHandler.start("<simple_type_spec>");
            errorHandler.lexeme(lexeme);
            errorHandler.lexeme(Lexeme.KW_LONG);
            errorHandler.lexeme(Lexeme.KW_SHORT);
            errorHandler.lexeme(Lexeme.KW_CHAR);
            errorHandler.lexeme(Lexeme.KW_WCHAR);
            errorHandler.lexeme(Lexeme.KW_BOOLEAN);
            errorHandler.lexeme(Lexeme.KW_FLOAT);
            errorHandler.lexeme(Lexeme.KW_DOUBLE);
            errorHandler.lexeme(Lexeme.KW_UNSIGNED);
            errorHandler.lexeme(Lexeme.KW_OCTET);
            errorHandler.lexeme(Lexeme.KW_ANY);
            errorHandler.lexeme(Lexeme.KW_OBJECT);
            errorHandler.lexeme(Lexeme.KW_STRING);
            errorHandler.lexeme(Lexeme.KW_SEQUENCE);
            errorHandler.lexeme(Lexeme.ID_LITERAL);
            errorHandler.lexeme(Lexeme.COLON_PAIR);
            errorHandler.end();
            return 0;
        }
    }

    private void reportTypeSpecError(int lexeme) throws Exception {
        errorHandler.start("<type_spec>");
        errorHandler.lexeme(lexeme);
        errorHandler.lexeme(Lexeme.KW_LONG);
        errorHandler.lexeme(Lexeme.KW_SHORT);
        errorHandler.lexeme(Lexeme.KW_CHAR);
        errorHandler.lexeme(Lexeme.KW_WCHAR);
        errorHandler.lexeme(Lexeme.KW_BOOLEAN);
        errorHandler.lexeme(Lexeme.KW_FLOAT);
        errorHandler.lexeme(Lexeme.KW_DOUBLE);
        errorHandler.lexeme(Lexeme.KW_UNSIGNED);
        errorHandler.lexeme(Lexeme.KW_OBJECT);
        errorHandler.lexeme(Lexeme.KW_OCTET);
        errorHandler.lexeme(Lexeme.KW_ANY);
        errorHandler.lexeme(Lexeme.KW_STRING);
        errorHandler.lexeme(Lexeme.KW_WSTRING);
        errorHandler.lexeme(Lexeme.KW_SEQUENCE);
        errorHandler.lexeme(Lexeme.KW_FIXED);
        errorHandler.lexeme(Lexeme.ID_LITERAL);
        errorHandler.lexeme(Lexeme.COLON_PAIR);
        errorHandler.lexeme(Lexeme.KW_STRUCT);
        errorHandler.lexeme(Lexeme.KW_ENUM);
        errorHandler.lexeme(Lexeme.KW_UNION);
        errorHandler.end();
    }

    /* see idl-v.2.2#type_spec */
    private int parseTypeSpec(int lexeme) throws Exception {
        switch (lexeme) {
        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#floating_pt_type
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#integer_type
        //       idl-v2.2#signed_int
        //         idl-v2.2#signed_long_int
        //         idl-v2.2#signed_longlong_int
        case Lexeme.KW_LONG:
            ast.start(NodeTypes.TYPE_REF);
            lexeme = lexer.lex();
            switch (lexeme) {
            case Lexeme.KW_LONG:      // "long" "long"
            case Lexeme.KW_DOUBLE:    // "long" "double"
                ast.end();
                return lexer.lex();
            default:                  // "long"
                ast.endLate();
                return lexeme;
            }
        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#integer_type
        //       idl-v2.2#signed_int
        //         idl-v2.2#signed_short_int
        case Lexeme.KW_SHORT:

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#char_type
        case Lexeme.KW_CHAR:

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#wide_char_type
        case Lexeme.KW_WCHAR:

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#boolean_type
        case Lexeme.KW_BOOLEAN:

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#floating_pt_type
        case Lexeme.KW_FLOAT:

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#floating_pt_type
        case Lexeme.KW_DOUBLE:

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#octet_type
        case Lexeme.KW_OCTET:

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#any_type
        case Lexeme.KW_ANY:

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#object_type
        case Lexeme.KW_OBJECT:
            ast.start(NodeTypes.TYPE_REF);
            ast.addBuiltInType(lexeme);
            ast.end();
            return lexer.lex();

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#base_type_spec
        //     idl-v2.2#integer_type
        //       idl-v2.2#unsigned_int
        case Lexeme.KW_UNSIGNED:
            return parseUnsigned();

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#template_type_spec
        //     idl-v2.2#string_type
        case Lexeme.KW_STRING:
            return parseStringBody();

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#template_type_spec
        //     idl-v2.2#wide_string_type
        case Lexeme.KW_WSTRING:
            return parseStringBody();

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#scoped_name
        case Lexeme.ID_LITERAL:
            return parseScopedNameFromIdentifier();

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#scoped_name
        case Lexeme.COLON_PAIR:
            return parseScopedNameFromColonPair();

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#template_type_spec
        //     idl-v2.2#sequence_type
        case Lexeme.KW_SEQUENCE:
            return parseSequenceBody();

        // idl-v2.2#simple_type_spec
        //   idl-v2.2#template_type_spec
        //     idl-v2.2#fixed_pt_type
        case Lexeme.KW_FIXED:
            return parseFixedPointBody();

        // idl-v2.2#constr_type_spec
        //   idl-v2.2#struct_type
        case Lexeme.KW_STRUCT:
            return parseExceptOrStructBody(false, NodeTypes.STRUCT);

        // idl-v2.2#constr_type_spec
        //   idl-v2.2#enum_type
        case Lexeme.KW_ENUM:
            return parseEnumBody();

        // idl-v2.2#constr_type_spec
        //   idl-v2.2#union_type
        case Lexeme.KW_UNION:
            return parseUnionBody();

        default:
          return 0;
        }
    }

    /* see idl-v2.2#member_list and any use of <member>* */
    private int parseMemberList(int lexeme, boolean empty) throws Exception {
        boolean foundOne = false;
        int l;
        while ((l = parseTypeSpec(lexeme)) != 0) {
            ast.startLate(NodeTypes.MEMBER);
            lexeme = parseDeclarators(l);
            if (lexeme == Lexeme.SEMI_COLON) {
                foundOne = true;
                ast.end();
                lexeme = lexer.lex();
            } else {
                errorHandler.start("expected_;");
                errorHandler.lexeme(l);
                errorHandler.end();
                return 0;
            }
        }
        if (!empty & !foundOne) {
            errorHandler.start("<member>+");
            errorHandler.lexeme(l);
            errorHandler.end();
            return 0;
        }
        return lexeme;
    }

    /* see idl-v2.2#struct_type or idl-v2.2#except_type */
    private int parseExceptOrStructBody(boolean empty, int nt) throws Exception {
        ast.start(nt);
        int lexeme = lexer.lex();
        if (lexeme == Lexeme.ID_LITERAL) {
            lexeme = lexer.lex();
            if (lexeme == Lexeme.OPEN_BRACE) {
                lexeme = parseMemberList(lexer.lex(), empty);
                if (lexeme == Lexeme.CLOSE_BRACE) {
                    ast.end();
                    return lexer.lex();
                } else {
                    errorHandler.start("expected_}");
                    errorHandler.lexeme(lexeme);
                    errorHandler.end();
                    return 0;
                }
            } else {
                errorHandler.start("expected_{");
                errorHandler.lexeme(lexeme);
                errorHandler.end();
                return 0;
            }
        } else {
            errorHandler.start("expected_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    /* see idl-v2.2#declarator */
    private int parseDeclarator(int lexeme) throws Exception {
        ast.startLate(NodeTypes.DECLARATOR);
        if (lexeme == Lexeme.ID_LITERAL) {
            lexeme = lexer.lex();
            for (;;) {
                if (lexeme == Lexeme.OPEN_BRACKET) {
                    lexeme = parsePositiveIntConst(lexer.lex());
                    if (lexeme == Lexeme.CLOSE_BRACKET) {
                        lexeme = lexer.lex();
                    } else {
                        errorHandler.start("expected_]");
                        errorHandler.lexeme(lexeme);
                        errorHandler.end();
                        return 0;
                    }
                } else {
                    ast.end();
                    return lexeme;
                }
            }
        } else {
            errorHandler.start("expected_id");
            errorHandler.lexeme(lexeme);
            errorHandler.end();
            return 0;
        }
    }

    /* see idl-v2.2#declarators */
    private int parseDeclarators(int lexeme) throws Exception {
        for (;;) {
          lexeme = parseDeclarator(lexeme);
          if (lexeme != Lexeme.COMMA) {
              return lexeme;
          } else {
              lexeme = lexer.lex();
          }
        }
    }

    /* see idl-v2.2#type_declarator */
    private int parseTypeDeclarator(int lexeme) throws Exception {
        int l = parseTypeSpec(lexeme);
        if (l != 0) {
            return parseDeclarators(l);
        } else {
          errorHandler.start("missing_type_spec");
          errorHandler.lexeme(lexeme);
          errorHandler.end();
        }
        return lexeme;
    }

    private int parseDefinitions(int lexeme) throws Exception {
        int l = lexeme;
        for (;;) {
            switch (l) {
            case Lexeme.KW_TYPEDEF:
                ast.start(NodeTypes.TYPE_DEF);
                l = parseTypeDeclarator(lexer.lex());
                ast.end();
                break;
            case Lexeme.KW_STRUCT:
                l = parseExceptOrStructBody(false, NodeTypes.STRUCT);
                break;
            case Lexeme.KW_ENUM:
                l = parseEnumBody();
                break;
            case Lexeme.KW_UNION:
                l = parseUnionBody();
                break;
            case Lexeme.KW_CONST:
                l = parseConstDecl();
                break;
            case Lexeme.KW_EXCEPTION:
                l = parseExceptOrStructBody(true, NodeTypes.EXCEPTION);
                break;
            case Lexeme.KW_INTERFACE:
                l = parseInterface();
                break;
            case Lexeme.KW_MODULE:
                l = parseModule();
                break;
            default:
                if (l != lexeme) {
                  /* successfully parsed at least one declaration */
                  return l;
                } else {
                  errorHandler.start("missing_declaration");
                  errorHandler.lexeme(l);
                  errorHandler.end();
                }
            }
            if (l == Lexeme.SEMI_COLON) {
                l = lexer.lex();
            } else {
              errorHandler.start("expected_;");
              errorHandler.lexeme(l);
              errorHandler.end();
              return 0;
            }
        }
    }

  //    /**
  //     * The Interface Repository does not represent hierarchical names
  //     * as some form of structure, instead such names are represented
  //     * as a single string which follows the syntax of <code>scoped_name</code>.
  //     * Therefore, to facilitate the extraction of the various components
  //     * of a scoped name from such a string, <code>parseScopedName</code>
  //     * is made available.
  //     */
  //    public int parseScopedName() throws Exception {
  //      int lexeme = lexer.lex();
  //      switch (lexeme) {
  //      case Lexeme.ID_LITERAL:
  //          return parseScopedNameFromIdentifier();
  //      case Lexeme.COLON_PAIR:
  //          return parseScopedNameFromColonPair();
  //      default:
  //          errorHandler.start("<scoped_name>");
  //          errorHandler.lexeme(lexeme);
  //          errorHandler.lexeme(Lexeme.ID_LITERAL);
  //          errorHandler.lexeme(Lexeme.COLON_PAIR);
  //          return 0;
  //      }
  //    }

    public int parse() throws Exception {
        return parseDefinitions(lexer.lex());
    }

}
