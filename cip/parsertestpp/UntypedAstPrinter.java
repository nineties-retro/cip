package cip.parsertestpp;

import java.util.ListIterator;


class UntypedAstPrinter {

    private static void print(Node n, int indentation) {
        if (n instanceof KwNode) {
            KwNode kwNode = (KwNode) n;
            ListIterator i = kwNode.children.listIterator(0);
            System.out.println("");
            outputIndentation(indentation);
            System.out.println("(" + kwNode.kw);
            while (i.hasNext()) {
                Node c = (Node) i.next();
                print(c, indentation+2);
            }
            System.out.print(")");
        } else if (n instanceof IdNode) {
            outputIndentation(indentation);
            System.out.print(((IdNode) n).id);
        } else if (n instanceof IntNode) {
            outputIndentation(indentation);
            System.out.print(((IntNode) n).value);
        } else if (n instanceof StrNode) {
            outputIndentation(indentation);
            System.out.print(((StrNode) n).str);
        } else if (n instanceof BuiltInTypeNode) {
            outputIndentation(indentation);
            System.out.print(((BuiltInTypeNode) n).kw);
        } else {
            System.out.println("Unknown node");
        }
    }

    private static void outputIndentation(int indentation) {
        for (int i = 0; i != indentation; i += 1) {
            System.out.print(" ");
        }
    }

    static void print(Node n) {
        print(n, 0);
    }

}
