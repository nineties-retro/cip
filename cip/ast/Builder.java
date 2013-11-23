package cip.ast;

import cip.Identifier;
import cip.Location;

public interface Builder {
    /**
     * <code>addBuiltInType</code> will add the <code>kw</code> to the current
     * construct.
     */
    public void addBuiltInType(int kw);

    /**
     * <code>addId</code> will add the <code>Identifier</code> to the current
     * construct.
     */
    public void addId(Identifier id, Location l);

    /**
     * <code>addId</code> will add the <code>Identifier</code> to the current
     * construct.
     */
    public void addInt(long v, Location l);

    /**
     * <code>addString</code> will add the <code>String</code> to the current
     * construct.
     */
    public void addString(String id, Location l);

    /**
     * <code>end</code> should be called when the end of a construct has been
     * reached.  It terminates the current construct and adds it to its
     * parent construct.
     */
    public void end();

    /**
     * <code>end</code> should be called when the end of a construct has been
     * reached.  It terminates the current construct and adds it to its
     * parent construct. XXX
     */
    public void endLate();

    /**
     * <code>start</code> should be called to indicate that new item in the
     * abstract syntax is to start.  The new item has the given 
     * <code>NodeType</code>.  All items added after this call will become
     * children of this construct until the matching call to <code>end</code>.
     */
    public void start(int t);

    /**
     * <code>startLate</code>, like <code>start</code> indicates that a new
     * node of the given <code>NodeType</code> is starting, but it also indicates
     * that the start was not detected until a lexeme had been scanned and
     * added to the existing node and that that node should instead be made
     * the first child of the new node.
     */
    public void startLate(int t);

}
