package cip.parsertestpp;

import cip.Location;

class BuiltInTypeNode implements Node {
    final int kw;
    final Location location;

    BuiltInTypeNode(int kw, Location l) {
        this.kw = kw;
        this.location = new Location(l);
    }
}
