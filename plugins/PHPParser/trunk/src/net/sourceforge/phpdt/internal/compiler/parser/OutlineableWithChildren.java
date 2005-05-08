package net.sourceforge.phpdt.internal.compiler.parser;

/**
 * The interface that will describe an object that can have children.
 *
 * @author Matthieu Casanova
 */
public interface OutlineableWithChildren extends Outlineable {
  boolean add(Outlineable o);

  Outlineable get(int index);

  int size();
}
