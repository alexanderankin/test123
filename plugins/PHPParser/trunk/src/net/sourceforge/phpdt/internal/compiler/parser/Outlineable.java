package net.sourceforge.phpdt.internal.compiler.parser;

/**
 * Here is an interface that object that can be in the outline view must implement.
 * @author Matthieu Casanova
 */
public interface Outlineable {

  OutlineableWithChildren getParent();

  String getName();
}
