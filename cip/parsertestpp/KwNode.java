package cip.parsertestpp;

import cip.Identifier;
import cip.Location;
import java.util.List;

class KwNode implements Node {
    final int kw;
    final Location location;
    final List children;

    KwNode(int kw, Location l, List children) {
        this.kw = kw;
        this.location = new Location(l);
        this.children = children;
    }
}
