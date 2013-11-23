package cip.parsertestpp;

import cip.Identifier;
import cip.Location;

class IdNode implements Node {
    final Identifier id;
    final Location location;

    IdNode(Identifier id, Location l) {
        this.id = id;
        this.location = new Location(l);
    }
}
