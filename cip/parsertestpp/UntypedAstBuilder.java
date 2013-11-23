package cip.parsertestpp;

import cip.Identifier;
import cip.Location;
import cip.ast.NodeTypes;
import java.util.LinkedList;


class UntypedAstBuilder implements cip.ast.Builder {
    private LinkedList openNodes;
    private LinkedList currentNodes;
    private Location l;

    UntypedAstBuilder(Location l) {
        this.openNodes = new LinkedList();
        this.currentNodes = new LinkedList();
        KwNode root = new KwNode(0, new Location(""), this.currentNodes);
        openNodes.add(root);
        this.l = l;
    }

    public void start(int nt) {
        LinkedList children = new LinkedList();
        KwNode n = new KwNode(nt, new Location(l), children);
        currentNodes.add(n);
        openNodes.add(n);
        currentNodes = children;
    }

    public void startLate(int nt) {
        Object o = currentNodes.removeLast();
        start(nt);
        currentNodes.add(o);
    }

    public void end() {
        openNodes.removeLast();
        KwNode newTop = (KwNode) openNodes.getLast();
        currentNodes = (LinkedList) newTop.children;
    }

    public void endLate() {
        Object o = currentNodes.removeLast();
        end();
        currentNodes.add(o);
    }

    public void addId(Identifier id, Location l) {
        currentNodes.add(new IdNode(id, l));
    }

    public void addInt(long v, Location l) {
        currentNodes.add(new IntNode(v, l));
    }

    public void addString(String s, Location l) {
        currentNodes.add(new StrNode(s, l));
    }

    public void addBuiltInType(int kw) {
        currentNodes.add(new BuiltInTypeNode(kw, l));
    }

    public Node getRoot() {
        return (Node) openNodes.removeFirst();
    }

}
