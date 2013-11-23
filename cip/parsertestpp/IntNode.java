package cip.parsertestpp;

import cip.Location;

class IntNode implements Node {
    final long value;
    final Location location;

    IntNode(long v, Location l) {
        this.value = v;
        this.location = new Location(l);
    }
}
