package cip.parsertest;

import cip.Identifier;
import cip.Location;
import cip.ast.NodeTypes;


class DummyAstBuilder implements cip.ast.Builder {

    public void start(int nt) {
    }

    public void startLate(int nt) {
    }

    public void end() {
    }

    public void endLate() {
    }

    public void addBuiltInType(int kw) {
    }

    public void addId(Identifier id, Location l) {
    }

    public void addInt(long v, Location l) {
    }

    public void addString(String s, Location l) {
    }

}
