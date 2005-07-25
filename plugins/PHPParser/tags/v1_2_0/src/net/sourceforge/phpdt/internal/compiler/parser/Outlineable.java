package net.sourceforge.phpdt.internal.compiler.parser;

import gatchan.phpparser.project.itemfinder.PHPItem;
import sidekick.Asset;

import javax.swing.text.Position;

/**
 * Here is an interface that object that can be in the outline view must implement.
 *
 * @author Matthieu Casanova
 */
public interface Outlineable {
  /**
   * Returns the parent of the item.
   *
   * @return the parent
   */
  OutlineableWithChildren getParent();

  /**
   * Give the name of the item.
   *
   * @return the name of the item
   */
  String getName();

  /**
   * Returns the item type.
   * in {@link PHPItem#CLASS},{@link PHPItem#FIELD}, {@link PHPItem#INTERFACE}, {@link PHPItem#METHOD}
   *
   * @return the item type
   */
  int getItemType();
}
