package cip.parsertestpp;

import cip.Identifier;
import cip.Location;

class StrNode implements Node {
    final String str;
    final Location location;

    StrNode(String id, Location l) {
        this.str = id;
        this.location = new Location(l);
    }
}
